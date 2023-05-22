package com.example.imole;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity   {

    NavigationView nav;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    Button instantButton;
    Button cumButton;
    TextView energymonitor;
    TextView availkwh;
    TextView kwhvalue;
    TextView cpc;
    TextView put;
    TextView tc;
    TextView tpc;
    View graphView;


    private double powerPurchased;
    private double threshold;
    private long latestPowerPurchaseTimestamp;
    private ArrayList<Double>powerValues;
    private ArrayList<Long> timestamps;
    private ArrayList<Double> energyValues = new ArrayList<>();
    private Handler handler;
    private HashMap<String, ArrayList<Double>> dailyPowerReadings = new HashMap<>();
    private double totalPowerToday = 0;
    private long todayInMillis = 0;
    private final int delay = 60000; // 1 minute delay for updating chart
    private Handler mHandler = new Handler();
    private static MainActivity instance;


    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            // Call startFetchingData() to begin fetching data from the web API
            startFetchingData();
            calculateDailyPowerConsumption();
            updateAvailableBalance();
            mHandler.postDelayed(this, 5000); // run again in 5 seconds
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        powerValues= new ArrayList<>();
        timestamps= new ArrayList<>();

        mHandler.post(mRunnable); // start updating UI every 5 seconds

        powerPurchased = loadPowerPurchaseValue();
        latestPowerPurchaseTimestamp = loadLatestPowerPurchaseTimestamp();

        // Retrieve the power purchased value from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        powerPurchased = preferences.getFloat("powerPurchased", 0.0f);

        // Retrieve the threshold value from SharedPreferences
        SharedPreferences preference = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        threshold = preference.getFloat("threshold", 0.0f);




        Window window = getWindow();
        // Show status bar
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Hide status bar
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);



        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        nav=(NavigationView)findViewById(R.id.navmenu);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer);
        instantButton = findViewById(R.id.instantaneous_button);
        cumButton =findViewById(R.id.cumulative_button);
        energymonitor =findViewById(R.id.energy_monitor_title);
        availkwh =findViewById(R.id.available_kilowatt_textview);
        kwhvalue =findViewById(R.id.kwh_value_textview);
        cpc =findViewById(R.id.current_power_consumption_textview);
        put =findViewById(R.id.power_used_today_textview);
        tc =findViewById(R.id.total_consumption_textview);
        tpc =findViewById(R.id.total_power_consumption_textview);
        graphView =findViewById(R.id.graph_view);

        toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        instance = this; // Assign the instance

        // Schedule the daily notification at 10 PM
        scheduleDailyNotification(this);

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Intent intent;
                switch (menuItem.getItemId())
                {
                    case R.id.menu_home:
                        intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(),"Energy monitor is Open",Toast.LENGTH_LONG).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.menu_contol :
                        intent = new Intent(MainActivity.this, power_control.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(),"power control is Open",Toast.LENGTH_LONG).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.menu_setting :
                        intent = new Intent(MainActivity.this, settings.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(),"Setting Panel is Open",Toast.LENGTH_LONG).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }
                return true;
            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic("energy_notifications")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "Subscribed to topic: energy_notifications");
                    } else {
                        Log.w("TAG", "Failed to subscribe to topic: energy_notifications", task.getException());
                    }
                });

       /* nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                Intent intent;
                switch (menuItem.getItemId())
                {
                    case R.id.menu_home:
                        intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(),"Energy monitor is Open",Toast.LENGTH_LONG).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.menu_contol :
                        intent = new Intent(MainActivity.this, power_control.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(),"power control is Open",Toast.LENGTH_LONG).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.menu_setting :
                        intent = new Intent(MainActivity.this, settings.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(),"Setting Panel is Open",Toast.LENGTH_LONG).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }

                return true;
            }
        }); */


    }

    public static MainActivity getInstance() {
        return instance;
    }

    public double getTextViewValue() {
        String text = put.getText().toString();
        double value = 0.0;
        try {
            value = Double.parseDouble(text);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            // Handle the case where the text cannot be parsed as a double
        }
        return value;
    }

    private void fetchData() {
        String url = "https://dayofinalproject.herokuapp.com/api/get";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Double powerValue = jsonObject.getDouble("power");
                            String timestamp = jsonObject.getString("timestamp");

                            // Convert the timestamp string to a Date object
                            Date timestampDate = dateFormat.parse(timestamp);

                            // Convert the Date object to milliseconds
                            long timestampMillis = timestampDate.getTime();
                            // Convert milliseconds to hours
                            long hours = TimeUnit.MILLISECONDS.toHours(timestampMillis);

                            // Add the values to the ArrayLists
                            powerValues.add(powerValue);
                            timestamps.add(hours);
                        }

                        // Update the UI with the new data
                        updateUI(powerValues, timestamps);

                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            error.printStackTrace();
        });

        // Add the request to the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    public void updateUI(ArrayList<Double> powerValues, ArrayList<Long> timestamps) {
        // Get the last power value and timestamp
        int lastIndex = powerValues.size() - 1;
        double currentPower = powerValues.get(lastIndex);
        Long currentTime = timestamps.get(lastIndex);

        // Calculate the power consumption
        double powerConsumption = calculatePowerConsumption(powerValues, timestamps);

        // Update the UI elements
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Update the current power TextView
               // TextView currentPowerTextView = findViewById(R.id.current_power_text_view);
                //  currentPowerTextView.setText(String.valueOf(currentPower));

                // Update the power consumption TextView
                TextView tpc = findViewById(R.id.total_power_consumption_textview);
                tpc.setText(String.valueOf(powerConsumption));

                // Update the last update time TextView
                //TextView lastUpdateTextView = findViewById(R.id.last_update_text_view);
               // lastUpdateTextView.setText(currentTime);
            }
        });
    }

    private void startFetchingData() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(() -> fetchData());
            }
        };
        timer.schedule(task, 0, 5000); // Fetch data every 5 seconds
    }

    private double calculatePowerConsumption(ArrayList<Double> powerValues, ArrayList<Long> timestamps) {
        double totalPower = 0.0;

        // Check if there are at least two data points
        if (powerValues.size() >= 2 && timestamps.size() >= 2) {
            // Calculate the time interval in seconds between each data point
            ArrayList<Long> intervals = new ArrayList<>();
            for (int i = 0; i < timestamps.size() - 1; i++) {
                long interval = timestamps.get(i + 1) - timestamps.get(i);
                intervals.add(interval);
            }

            // Calculate the total power consumption using the trapezoidal rule
            for (int i = 0; i < powerValues.size() - 1; i++) {
                double power1 = powerValues.get(i);
                double power2 = powerValues.get(i + 1);
                long interval = intervals.get(i);
                double energy = (power1 + power2) / 2 * interval / 3600 / 1000; // Convert to kWh
                totalPower += energy;

                energyValues.add(totalPower);
            }
        }



        return totalPower;
    }
   /* private void dailyPowerConsumption() {
        double totalPower = 0;
        long nowInMillis = System.currentTimeMillis();
        for (int i = 0; i < powerValues.size() - 1; i++) {
            double power1 = powerValues.get(i);
            double power2 = powerValues.get(i + 1);
            long timestamp1 = timestamps.get(i);
            long timestamp2 = timestamps.get(i + 1);
            if (timestamp1 > todayInMillis) {
                totalPowerToday = 0; // reset total power for the day
                todayInMillis = timestamp1; // update today's start time
            }
            if (timestamp2 > todayInMillis) {
                double elapsedSeconds = (timestamp2 - Math.max(todayInMillis, timestamp1)) / 1000.0;
                double averagePower = (power1 + power2) / 2;
                totalPowerToday += elapsedSeconds * averagePower / 3600; // kWh
            }
            double elapsedSeconds = (timestamp2 - timestamp1) / 1000.0;
            double averagePower = (power1 + power2) / 2;
            totalPower += elapsedSeconds * averagePower / 3600; // kWh
        }
        final double dailyPower = totalPowerToday;
        final double currentPower = totalPower;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //currentPowerConsumptionTextView.setText(String.format("%.2f kWh", currentPower));
                put.setText(String.format("%.2f kWh", dailyPower));
            }
        });
    }
    private void calculateDailyPowerConsumption() {
        Calendar calendar = Calendar.getInstance();
        double dailyPowerConsumption = 0.0;
        for (int i = 0; i < powerValues.size() - 1; i++) {
            Date currentTime = new Date(timestamps.get(i));
            calendar.setTime(currentTime);
            int currentDay = calendar.get(Calendar.DAY_OF_YEAR);

            Date nextTime = new Date(timestamps.get(i + 1));
            calendar.setTime(nextTime);
            int nextDay = calendar.get(Calendar.DAY_OF_YEAR);

            if (currentDay != nextDay) {
                dailyPowerConsumption = 0.0;
                currentDay = nextDay;
            }

            double currentPowerConsumption = calculateDailyConsumption(powerValues.get(i), powerValues.get(i + 1), timestamps.get(i), timestamps.get(i + 1));
            dailyPowerConsumption += currentPowerConsumption;
        }

        // Update the daily power consumption text view
        put.setText(String.format(Locale.getDefault(), "%.2f", dailyPowerConsumption));
    } */
     double calculateDailyConsumption(double startPower, double endPower, long startTime, long endTime) {
        double timeDiff = (endTime - startTime) / 3600000.0; // convert milliseconds to hours
        return ((startPower + endPower) / 2.0) * timeDiff / 1000.0; // trapezoidal rule formula
    }
   private double dailyPower = 0.0;
    private long currentDayMillis = 0L;

     void calculateDailyPowerConsumption() {
        long currentTimeMillis = System.currentTimeMillis();
        double currentPower = 0.0;
        for (int i = 0; i < powerValues.size() - 1; i++) {
            long hours = timestamps.get(i);
            long milliseconds = TimeUnit.MILLISECONDS.convert(hours, TimeUnit.HOURS);
            timestamps.set(i, milliseconds);
            if (timestamps.get(i) >= currentDayMillis && timestamps.get(i + 1) <= currentTimeMillis) {
                currentPower += calculateDailyConsumption(powerValues.get(i), powerValues.get(i + 1), timestamps.get(i), timestamps.get(i + 1));
            }
        }
        if (currentTimeMillis >= currentDayMillis + 86400000) { // Check if it's a new day
            dailyPower = 0.0;
            currentDayMillis = currentTimeMillis - (currentTimeMillis % 86400000); // Reset currentDayMillis to the start of the day
        }
        dailyPower += currentPower;
        runOnUiThread(() -> put.setText(String.format(Locale.getDefault(), "%.2f kWh", dailyPower)));
    }
    public void updateAvailableBalance() {
        // Calculate the total power consumption
        double PowerConsumption = calculatePowerConsumption(powerValues, timestamps);

        powerPurchased =loadPowerPurchaseValue();
        threshold=loadThresholdValue();

        double availableBalance = powerPurchased - PowerConsumption;
        availkwh.setText(String.format(Locale.getDefault(), "%.2f kWh", availableBalance));

        if (availableBalance <= threshold) {
            // Create the notification message
            RemoteMessage notification = new RemoteMessage.Builder("energy_notifications")
                    .setMessageId(String.valueOf(System.currentTimeMillis()))
                    .addData("title", "LOW ENERGY BALANCE")
                    .addData("body", "Your energy balance is low. Please recharge and conserve energy until the next recharge by switching off appliances")
                    .build();

            // Send the notification
            FirebaseMessaging.getInstance().send(notification);



        }

    }

    private double loadPowerPurchaseValue() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        powerPurchased = preferences.getFloat("powerPurchased", 0.0f);
        return preferences.getFloat("PowerPurchased", 0.0f);
    }

    private double loadThresholdValue() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        powerPurchased = preferences.getFloat("threshold", 0.0f);
        return preferences.getFloat("threshold", 0.0f);
    }

    private long loadLatestPowerPurchaseTimestamp() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getLong("LatestPowerPurchaseTimestamp", 0L);
    }



    private void saveLatestPowerPurchaseTimestamp(long timestamp) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("LatestPowerPurchaseTimestamp", timestamp);
        editor.apply();
    }



    void filterTimestampsAndPowerValues() {

        // Get the current timestamp


        // Save the current timestamp as the latest power purchase timestamp
        long currentTimestamp =loadLatestPowerPurchaseTimestamp();



        // Create new ArrayLists to store the filtered values
        ArrayList<Long> filteredTimestamps = new ArrayList<>();
        ArrayList<Double> filteredPowerValues = new ArrayList<>();

        // Iterate through the existing arrays and filter the values
        for (int i = 0; i < timestamps.size(); i++) {
            long timestamp = timestamps.get(i);
            if (timestamp >= currentTimestamp) {
                // Add the timestamp and power value to the filtered arrays
                filteredTimestamps.add(timestamp);
                filteredPowerValues.add(powerValues.get(i));
            }
        }

        // Replace the existing arrays with the filtered arrays
        timestamps = filteredTimestamps;
        powerValues = filteredPowerValues;

        calculatePowerConsumption(powerValues, timestamps);
    }

    private void scheduleDailyNotification(Context context) {
        // Create a Calendar instance for 10 PM
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        // Set up the Intent and PendingIntent for the notification
        Intent notificationIntent = new Intent(context, MyNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        // Set up the AlarmManager to schedule the notification
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }





  /*  private void filterTimestampsAndPowerValues(long powerPurchasedTimestamp) {
        // Iterate through the existing arrays and remove values that are less than the power purchased timestamp
        for (int i = timestamps.size() - 1; i >= 0; i--) {
            long timestamp = timestamps.get(i);
            if (timestamp < powerPurchasedTimestamp) {
                // Remove the timestamp and corresponding power value
                timestamps.remove(i);
                powerValues.remove(i);
            }
        }
    }*/


}
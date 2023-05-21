package com.example.imole;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class settings extends AppCompatActivity {

    NavigationView nav;
    ActionBarDrawerToggle toggle;
    TextView energyMonitor;
    TextView settings;
    TextView ppinkwh;
    TextView tpinkwh;
    TextView autoRefreshview;
    TextView autoRefresh;
    TextView dailynot;
    TextView spt;
    TextView timetext;
    TextView charttint;
    TextView sctc;
    private EditText enterkwhp;
    private EditText entertp;
    Switch autorefreshbut;
    DrawerLayout drawerLayout;


    private void savePowerPurchasedValue(double powerPurchased) {
        // Save the power purchased value to SharedPreferences or any other storage mechanism
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat("powerPurchased", (float) powerPurchased);
        editor.apply();
    }
    private void onPowerPurchasedValueChanged() {
        String powerPurchasedStr = enterkwhp.getText().toString();
        double powerPurchased = Double.parseDouble(powerPurchasedStr);

        // Save the power purchased value
        savePowerPurchasedValue(powerPurchased);

    }

    private void savethresholdValue(double threshold) {
        // Save the power purchased value to SharedPreferences or any other storage mechanism
        SharedPreferences preference = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preference.edit();
        editor.putFloat("threshold", (float) threshold);
        editor.apply();
    }

    private void onthresholdValueChanged() {
        String thresholdStr = entertp.getText().toString();
        double threshold = Double.parseDouble(thresholdStr);

        // Save the power purchased value
        savethresholdValue(threshold);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Window window = getWindow();
        // Show status bar
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Hide status bar
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();




        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        nav=(NavigationView)findViewById(R.id.navmenu);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer);
        settings = findViewById(R.id.settings);
        ppinkwh = findViewById(R.id.power_purchased);
        tpinkwh = findViewById(R.id.threshold_power);
        autoRefreshview = findViewById(R.id.automatic_refresh);
        autoRefresh = findViewById(R.id.textViewAutomaticRefresh);
        dailynot = findViewById(R.id.textViewDailyNotification);
        spt = findViewById(R.id.textViewSelectPreferredTime);
        timetext = findViewById(R.id.textViewTime);
        enterkwhp =findViewById(R.id.edit_text_kwh);
        entertp = findViewById(R.id.edit_text_threshold);
        autorefreshbut = findViewById(R.id.refresh_switch);

        toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        enterkwhp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    // The user has left the EditText
                    showConfirmationDialog();
                }
            }
        });

        entertp.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    // The user has left the EditText
                    showThresholdDialog();
                }
            }
        });

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem)
            {
                Intent intent;
                switch (menuItem.getItemId())
                {
                    case R.id.menu_home:
                        intent = new Intent(settings.this, MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(),"Energy monitor is Open",Toast.LENGTH_LONG).show();
                        break;

                    case R.id.menu_contol :
                        intent = new Intent(settings.this, power_control.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(),"power control is Open",Toast.LENGTH_LONG).show();
                        break;

                    case R.id.menu_setting :
                        intent = new Intent(settings.this, settings.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(),"Setting Panel is Open",Toast.LENGTH_LONG).show();
                        break;
                }


                drawerLayout.closeDrawer(GravityCompat.START);
                return true;

            }
        });

    }
    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Power Purchased");
        builder.setMessage("Inputting a new value will refresh the app. Are you sure you want to proceed?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle Yes button click
                onPowerPurchasedValueChanged();
                updatePowerPurchased();

                // Get the new power purchase value from the EditText
                double newPowerPurchase = Double.parseDouble(enterkwhp.getText().toString());

                // Call the method in MainActivity to handle the new power purchase value
                MainActivity mainActivity = (MainActivity) getParent();
                mainActivity.handleNewPowerPurchaseValue(newPowerPurchase);
                mainActivity.filterTimestampsAndPowerValues();

                // Finish the SettingsActivity
                finish();
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }
    private void showThresholdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Threshold Value");
        builder.setMessage("Are you sure you want to change the threshold value");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle Yes button click
                onthresholdValueChanged();



            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }
    private void updatePowerPurchased() {

        // Refresh the MainActivity by restarting it
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        // Finish the SettingsActivity
        finish();
    }

}
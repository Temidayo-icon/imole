package com.example.imole;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class power_control extends AppCompatActivity  {


    private final String phoneNumber = "+2349159774476"; // Replace with your GSM module phone number

    NavigationView nav;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    TextView aotl;
    TextView loadoneT;
    TextView loadtwoT;
    TextView loadthreeT;
    TextView loadfourT;
    Switch loadoneS;
    Switch loadtwoS;
    Switch loadthreeS;
    Switch loadfourS;
    ImageView loadoneV;
    ImageView loadtwoV;
    ImageView loadthreeV;
    ImageView loadfourV;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_control);

        nav=(NavigationView)findViewById(R.id.navmenu);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer);
        loadoneT=findViewById(R.id.load1_textview);
        loadtwoT = findViewById(R.id.load2_textview);
        loadthreeT = findViewById(R.id.load3_textview);
         loadfourT= findViewById(R.id.load4_textview);
        loadoneS = findViewById(R.id.load1_switch);
        loadtwoS = findViewById(R.id.load2_switch);
        loadthreeS = findViewById(R.id.load3_switch);
        loadfourS = findViewById(R.id.load4_switch);
        loadoneV = findViewById(R.id.load1_imageview);
        loadtwoV = findViewById(R.id.load2_imageview);
        loadthreeV = findViewById(R.id.load3_imageview);
        loadfourV = findViewById(R.id.load4_imageview);
        aotl =findViewById(R.id.allofthelights);

        Window window = getWindow();
        // Show status bar
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Hide status bar
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                Intent intent;
                switch (menuItem.getItemId())
                {
                    case R.id.menu_home:
                        intent = new Intent(power_control.this, MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(),"Energy monitor is Open",Toast.LENGTH_LONG).show();
                        break;

                    case R.id.menu_contol :
                        intent = new Intent(power_control.this, power_control.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(),"Call Panel is Open",Toast.LENGTH_LONG).show();
                        break;

                    case R.id.menu_setting :
                        intent = new Intent(power_control.this, settings.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(),"Setting Panel is Open",Toast.LENGTH_LONG).show();
                        break;
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        loadoneS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Send SMS to turn on or off load 1
                sendSMS(isChecked ? "Load1on" : "Load1off");
            }
        });

        loadtwoS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Send SMS to turn on or off load 2
                sendSMS(isChecked ? "Load2on" : "Load2off");
            }
        });

        loadthreeS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Send SMS to turn on or off load 3
                sendSMS(isChecked ? "Load3on" : "Load3off");
            }
        });

        loadfourS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Send SMS to turn on or off load 4
                sendSMS(isChecked ? "Load4on" : "Load4off");
            }
        });
    }
    // Send SMS to GSM module
    private void sendSMS(String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
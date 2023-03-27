package com.example.imole;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
    EditText enterkwhp;
    EditText entertp;
    Switch autorefreshbut;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
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
}
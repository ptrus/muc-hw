package fri.muc.peterus.muc_hw.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import fri.muc.peterus.muc_hw.adapters.SettingsPagerAdapter;
import fri.muc.peterus.muc_hw.R;
import fri.muc.peterus.muc_hw.helpers.ApplicationContext;
import fri.muc.peterus.muc_hw.receivers.LocationSensingAlarmReceiver;
import fri.muc.peterus.muc_hw.services.LocationIntentService;

/**
 * Created by peterus on 22.10.2015.
 */

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SettingsPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocationSensingAlarmReceiver.startAlarm(ApplicationContext.getContext());

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.map)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.summary)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.settings)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new SettingsPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                return;
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                return;
            }
        });
    }

    @Override
    protected void onStop() {
        stopService(new Intent(this, LocationIntentService.class));
        super.onStop();
    }
}

package fri.muc.peterus.muc_hw.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

import fri.muc.peterus.muc_hw.services.LocationIntentService;

/**
 * Created by peterus on 25.10.2015.
 */
public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Class activityClass = isRegistered() ? MainActivity.class : RegistrationActivity.class;
        Intent newActivity = new Intent(this, activityClass);
        newActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(newActivity);

        // End the activity, to prevent going back to this empty activity.
        //finish();
    }

    private boolean isRegistered() {
        SharedPreferences settings = getSharedPreferences(RegistrationActivity.ACC_PREFS, MODE_PRIVATE);
        return settings.getBoolean("registered", false);
    }
}
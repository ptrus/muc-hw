package fri.muc.peterus.muc_hw.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import fri.muc.peterus.muc_hw.services.WiFiConnectivityIntentService;

/**
 * Created by peterus on 25.10.2015.
 */
public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = new Intent(this, WiFiConnectivityIntentService.class);
        this.startService(i);
        Class activityClass = isRegistered() ? MainActivity.class : RegistrationActivity.class;
        Intent newActivity = new Intent(this, activityClass);
        newActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(newActivity);
    }

    private boolean isRegistered() {
        SharedPreferences settings = getSharedPreferences(RegistrationActivity.ACC_PREFS, MODE_PRIVATE);
        return settings.getBoolean("registered", false);
    }

}
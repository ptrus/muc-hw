package fri.muc.peterus.muc_hw.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by peterus on 25.10.2015.
 */
public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Class activityClass = isRegistered() ? MainActivity.class : RegistrationActivity.class;
        Intent newActivity = new Intent(this, activityClass);
        startActivity(newActivity);

        // End the activity, to prevent going back to this empty activity.
        finish();
    }

    private boolean isRegistered() {
        SharedPreferences settings = getSharedPreferences(RegistrationActivity.ACC_PREFS, MODE_PRIVATE);
        Log.d("StartActivity", "BOOL:"+settings.getBoolean("registered", false));
        return settings.getBoolean("registered", false);
    }
}

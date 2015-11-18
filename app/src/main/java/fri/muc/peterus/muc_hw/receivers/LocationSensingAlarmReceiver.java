package fri.muc.peterus.muc_hw.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.SystemClock;

import java.util.Calendar;

import fri.muc.peterus.muc_hw.activities.RegistrationActivity;
import fri.muc.peterus.muc_hw.helpers.ApplicationContext;
import fri.muc.peterus.muc_hw.helpers.Constants;
import fri.muc.peterus.muc_hw.helpers.SensingTriggerHelper;
import fri.muc.peterus.muc_hw.services.LocationIntentService;
import fri.muc.peterus.muc_hw.services.WiFiConnectivityIntentService;

/**
 * Created by peterus on 5.11.2015.
 */
public class LocationSensingAlarmReceiver extends BroadcastReceiver{
    public static final String ALARM_ACTION = "si.uni_lj.fri.muc.periodicsensing.ALARM_ACTION";
    public static long alarmPeriod = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = getActionAndUpdateAlarmPeriod();
        if (action != null) {
            SensingTriggerHelper.incrementSensingTriggerId();

            Intent i = new Intent(context, LocationIntentService.class);
            context.startService(i);

            Intent j = new Intent(context, WiFiConnectivityIntentService.class);
            context.startService(j);
        }

        startAlarm(ApplicationContext.getContext());
    }

    private String getActionAndUpdateAlarmPeriod() {
        Calendar now = Calendar.getInstance();

        Calendar workStart = Calendar.getInstance();
        workStart.set(Calendar.HOUR_OF_DAY, Constants.WORK_START);
        workStart.set(Calendar.MINUTE, 0);

        Calendar workStop = Calendar.getInstance();
        workStop.set(Calendar.HOUR_OF_DAY, Constants.WORK_STOP);
        workStop.set(Calendar.MINUTE, 0);

        Calendar sleepStart = Calendar.getInstance();
        sleepStart.set(Calendar.HOUR_OF_DAY, Constants.SLEEP_START);
        sleepStart.set(Calendar.MINUTE, 0);

        Calendar sleepStop = Calendar.getInstance();
        sleepStop.set(Calendar.HOUR_OF_DAY, Constants.SLEEP_STOP);
        sleepStop.set(Calendar.MINUTE, 0);
        if (now.before(workStop) && workStart.before(now)){
            // At work.
            alarmPeriod = getSamplingUpdateInterval();
            return "work";
        }

        else if (now.before(sleepStop) && sleepStart.before(now)){
            // Sleeping.
            alarmPeriod = getSamplingUpdateInterval();
            return "sleep";
        }

        else{
            if (now.before(sleepStart)){
                // Before sleep start.
                alarmPeriod = sleepStart.getTimeInMillis() - now.getTimeInMillis();
            }
            else if (now.before(workStart)){
                // Before work start, and after sleep stop.
                alarmPeriod = workStart.getTimeInMillis() - now.getTimeInMillis();
            }
            else{
                // After work stop.
                sleepStart.add(Calendar.DATE, 1);
                alarmPeriod = sleepStart.getTimeInMillis() - now.getTimeInMillis();
            }
            return null;
        }
    }

    public static void startAlarm(Context context){
        Intent intent = new Intent(ALARM_ACTION);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);

        // Cancel any previous alarms set.
        pi.cancel();

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        int type = AlarmManager.ELAPSED_REALTIME_WAKEUP;
        long fireTime = SystemClock.elapsedRealtime() + alarmPeriod;
        if(Build.VERSION.SDK_INT >= 19)
            am.setExact(type, fireTime, pi);
        else
            am.set(type, fireTime, pi);
    }

    private static long getSamplingUpdateInterval(){
        SharedPreferences settings = ApplicationContext.getContext().getSharedPreferences(RegistrationActivity.ACC_PREFS, Context.MODE_PRIVATE);
        int minutes = settings.getInt("samplingInterval", Constants.LOCATION_UPDATE_INTERVAL_MINUTES_ACTIVE);
        long millis = minutes * 60 * 1000;
        return millis;
    }
}

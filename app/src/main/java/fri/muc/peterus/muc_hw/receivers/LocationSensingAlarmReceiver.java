package fri.muc.peterus.muc_hw.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

import fri.muc.peterus.muc_hw.helpers.ApplicationContext;
import fri.muc.peterus.muc_hw.helpers.Constants;
import fri.muc.peterus.muc_hw.services.LocationIntentService;

/**
 * Created by peterus on 5.11.2015.
 */
public class LocationSensingAlarmReceiver extends BroadcastReceiver{
    public static final String ALARM_ACTION = "si.uni_lj.fri.muc.periodicsensing.ALARM_ACTION";
    public static long alarmPeriod = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Alarm received", Toast.LENGTH_LONG).show();

        String action = getActionAndUpdateAlarmPeriod();
        Log.d("LocationSensingAlarm", "Action: " + action + " alarmPeriod: " + alarmPeriod);
        if (action != null) {
            Intent i = new Intent(context, LocationIntentService.class);
            intent.putExtra("action", action);
            context.startService(i);
        }

        startAlarm(ApplicationContext.getContext());
    }

    private String getActionAndUpdateAlarmPeriod() {
        Calendar now = Calendar.getInstance();

        Calendar workStart = Calendar.getInstance();
        workStart.set(Calendar.HOUR_OF_DAY, Constants.WORK_START);

        Calendar workStop = Calendar.getInstance();
        workStop.set(Calendar.HOUR_OF_DAY, Constants.WORK_STOP);

        Calendar sleepStart = Calendar.getInstance();
        sleepStart.set(Calendar.HOUR_OF_DAY, Constants.SLEEP_START);

        Calendar sleepStop = Calendar.getInstance();
        sleepStop.set(Calendar.HOUR_OF_DAY, Constants.SLEEP_STOP);

        if (now.before(workStop) && workStart.before(now)){
            // At work.
            alarmPeriod = Constants.LOCATION_UPDATE_INTERVAL_MILLIS_ACTIVE;
            return "work";
        }

        else if (now.before(sleepStop) && sleepStart.before(now)){
            // Sleeping.
            alarmPeriod = Constants.LOCATION_UPDATE_INTERVAL_MILLIS_ACTIVE;
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
        Log.d("LocationSensingAlarm", "Started alarm in:" + alarmPeriod);
        Intent intent = new Intent(ALARM_ACTION);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        int type = AlarmManager.ELAPSED_REALTIME_WAKEUP;
        long fireTime = SystemClock.elapsedRealtime() + alarmPeriod;
        if(Build.VERSION.SDK_INT >= 19)
            am.setExact(type, fireTime, pi);
        else
            am.set(type, fireTime, pi);
    }
}
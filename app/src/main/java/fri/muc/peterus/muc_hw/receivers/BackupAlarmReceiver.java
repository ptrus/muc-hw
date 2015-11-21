package fri.muc.peterus.muc_hw.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import java.util.ArrayList;

import fri.muc.peterus.muc_hw.helpers.ApplicationContext;
import fri.muc.peterus.muc_hw.helpers.Constants;
import fri.muc.peterus.muc_hw.helpers.DBOpenHelper;
import fri.muc.peterus.muc_hw.helpers.WiFiHelper;
import fri.muc.peterus.muc_hw.tasks.AccessPointsCursorTask;
import fri.muc.peterus.muc_hw.tasks.ConnectionEndpointBackupTask;
import fri.muc.peterus.muc_hw.tasks.LocationEndpointBackupTask;

/**
 * Created by peterus on 21.11.2015.
 */
public class BackupAlarmReceiver extends BroadcastReceiver {
    private static String TAG = "BackupAlarmReciever";

    public static final String ALARM_ACTION = "si.uni_lj.fri.muc.backup.ALARM_ACTION";
    private static long alarmPeriod = Constants.BACKUP_ACTION_ALARM_INTERVAL_MILLIS;
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mContext == null)
            mContext = ApplicationContext.getContext();

        if (onGoodWiFi()){
            Log.d(TAG, "On good wifi");
            new LocationEndpointBackupTask().execute();
            new ConnectionEndpointBackupTask().execute();
            alarmPeriod = Constants.BACKUP_ACTION_ALARM_INTERVAL_MILLIS;
        }
        else{
            Log.d(TAG, "On bad wifi");
            alarmPeriod = Constants.BACKUP_ACTION_ALARM_INTERVAL_REPEAT_MILLIS;
        }
        startAlarm(mContext);
    }

    public static void startAlarm(Context context){
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

    private boolean onGoodWiFi() {
        if (WiFiHelper.isConnected(mContext)){
            WifiInfo wifiInfo = WiFiHelper.getWifiInfo(mContext);
            return isTopAccessPoint(wifiInfo.getBSSID());
        }
        return false;
    }

    private boolean isTopAccessPoint(String bssid) {
        DBOpenHelper mDatabaseHelper = new DBOpenHelper(ApplicationContext.getContext());
        SQLiteDatabase readableDatabase = mDatabaseHelper.getReadableDatabase();
        Cursor cursor = readableDatabase.rawQuery(AccessPointsCursorTask.TOP_AP_QUERY, null);

        while(cursor.moveToNext()){
            String _bssid = cursor.getString(cursor.getColumnIndex(DBOpenHelper.BSSID));
            if (bssid.equals(_bssid)) {
                cursor.close();
                readableDatabase.close();
                mDatabaseHelper.close();
                return true;
            }
        }
        cursor.close();
        readableDatabase.close();
        mDatabaseHelper.close();
        return false;
    }
}


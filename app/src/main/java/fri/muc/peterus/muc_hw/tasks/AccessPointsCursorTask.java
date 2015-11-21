package fri.muc.peterus.muc_hw.tasks;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import fri.muc.peterus.muc_hw.helpers.DBOpenHelper;

/**
 * Created by peterus on 20.11.2015.
 */
public class AccessPointsCursorTask extends AsyncTask<SQLiteDatabase, Void, Cursor> {
    private static final String TAG = "AccessPointsCursorTask";

    private TaskListener mTaskListener;
    public interface TaskListener{
        void beforeTaskStarts();
        void onTaskCompleted(Cursor c);
    }

    public AccessPointsCursorTask(TaskListener tl){
        mTaskListener = tl;
    }

    public static String TOP_AP_QUERY = "SELECT loc." + DBOpenHelper._ID +", AVG(conn." + DBOpenHelper.RSSI + "), conn." + DBOpenHelper.SSID +
            ", loc." + DBOpenHelper.LABEL + ", AVG(loc." + DBOpenHelper.LAT + "), AVG(loc." + DBOpenHelper.LNG +
            "), COUNT(*)" + ", conn." + DBOpenHelper.BSSID +
            " FROM " + DBOpenHelper.TABLE_NAME + " AS loc, " +
            DBOpenHelper.TABLE_NAME2 + " AS conn " +
            "WHERE loc." + DBOpenHelper.TRIGGER_ID + " = conn." + DBOpenHelper.TRIGGER_ID +
            " GROUP BY conn." + DBOpenHelper.SSID + ", loc." + DBOpenHelper.LABEL +
            " ORDER BY AVG(conn." + DBOpenHelper.RSSI + ") ASC" +
            " LIMIT 10";

    @Override
    protected Cursor doInBackground(SQLiteDatabase... params) {
        SQLiteDatabase readableDatabase = params[0];
        if (readableDatabase.isOpen()) {
            Cursor cursor = readableDatabase.rawQuery(TOP_AP_QUERY, null);
            return cursor;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Cursor c) {
        mTaskListener.onTaskCompleted(c);
    }
}

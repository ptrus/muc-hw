package fri.muc.peterus.muc_hw.tasks;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.util.Log;

import fri.muc.peterus.muc_hw.helpers.ApplicationContext;
import fri.muc.peterus.muc_hw.helpers.DBOpenHelper;

/**
 * Created by peterus on 18.11.2015.
 */
public class RSSIQueryTask extends AsyncTask<String, Void, Pair<String, Double>> {
    private static final String TAG = "RSSIQueryTask";

    private TaskListener mTaskListner;
    public interface TaskListener{
        void beforeTaskStarts(double s);
        void onTaskCompleted(Pair<String, Double> s);
    }

    public RSSIQueryTask(TaskListener tl){
        mTaskListner = tl;
    }

    private String query = "SELECT conn." + DBOpenHelper.RSSI +
            " FROM " + DBOpenHelper.TABLE_NAME + " AS loc, " +
            DBOpenHelper.TABLE_NAME2 + " AS conn " +
            "WHERE loc." + DBOpenHelper.TRIGGER_ID + " = conn." + DBOpenHelper.TRIGGER_ID +
            " AND loc." + DBOpenHelper.LABEL + " =" + " ?;";

    @Override
    protected Pair<String, Double> doInBackground(String... params) {
        DBOpenHelper mDatabaseHelper = new DBOpenHelper(ApplicationContext.getContext());
        SQLiteDatabase readableDatabase = mDatabaseHelper.getReadableDatabase();

        String label = params[0];
        Cursor cursor = readableDatabase.rawQuery(query, new String[]{label});

        double all = 0.0;
        int c = cursor.getCount();
        while (cursor.moveToNext()){
            all += cursor.getDouble(cursor.getColumnIndex(DBOpenHelper.RSSI));
        }
        cursor.close();
        mDatabaseHelper.close();
        return new Pair<>(label, c == 0 ? 0 : all / c);
    }

    @Override
    protected void onPostExecute(Pair<String, Double> result) {
        mTaskListner.onTaskCompleted(result);
    }
}

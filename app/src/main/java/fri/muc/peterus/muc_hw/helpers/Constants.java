package fri.muc.peterus.muc_hw.helpers;

/**
 * Created by peterus on 4.11.2015.
 */
public class Constants {
    public final static int LOCATION_UPDATE_INTERVAL_MINUTES_ACTIVE = 10;
    public static final int SLEEP_START = 1;
    public static final int SLEEP_STOP = 7;
    public static final int WORK_START = 9;
    public static final int WORK_STOP = 16;

    // TODO change.
    public static final int CLUST_THRESHOLD = 200;


    public static final long BACKUP_ACTION_ALARM_INTERVAL_MILLIS = 6 * 60 * 60 * 1000; // 6 Hours.
    public static final long BACKUP_ACTION_ALARM_INTERVAL_REPEAT_MILLIS = 1 * 60 * 60 * 1000; //1 Hour.
}

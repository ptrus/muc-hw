package fri.muc.peterus.muc_hw.helpers;

import android.app.Application;
import android.content.Context;

/**
 * Created by peterus on 5.11.2015.
 */
public class ApplicationContext extends Application {
    private static Context instance;

    @Override
    public void onCreate(){
        super.onCreate();
        instance = getApplicationContext();
    }

    public static Context getContext(){
        return instance;
    }
}

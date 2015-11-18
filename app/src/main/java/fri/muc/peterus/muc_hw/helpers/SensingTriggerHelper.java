package fri.muc.peterus.muc_hw.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import fri.muc.peterus.muc_hw.activities.RegistrationActivity;

/**
 * Created by peterus on 18.11.2015.
 */
public class SensingTriggerHelper {

    public static long getSensingTriggerId(){
        SharedPreferences settings = ApplicationContext.getContext().getSharedPreferences(RegistrationActivity.ACC_PREFS, Context.MODE_PRIVATE);
        long triggerId = settings.getLong("triggerId", -1);
        return triggerId;
    }

    public static long incrementSensingTriggerId(){
        SharedPreferences settings = ApplicationContext.getContext().getSharedPreferences(RegistrationActivity.ACC_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor settingsEditor = settings.edit();
        long triggerId = settings.getLong("triggerId", -1);
        triggerId += 1;
        settingsEditor.putLong("triggerId", triggerId);
        settingsEditor.commit();
        return triggerId;
    }
}

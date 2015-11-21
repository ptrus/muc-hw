package fri.muc.peterus.muc_hw.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import fri.muc.peterus.muc_hw.fragments.ConnectionMapFragment;
import fri.muc.peterus.muc_hw.fragments.SettingsFragment;
import fri.muc.peterus.muc_hw.fragments.SummaryFragment;

/**
 * Created by peterus on 22.10.2015.
 */
public class SettingsPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "SettingsPagerAdapter";
    private int nTabs;

    public SettingsPagerAdapter(FragmentManager fm, int nTabs){
        super(fm);
        this.nTabs = nTabs;
    }

    @Override
    public int getCount() {
        return nTabs;
    }

    @Override
    public Fragment getItem(int position){
        switch (position){
            case 0:
                return new ConnectionMapFragment();
            case 1:
                return new SummaryFragment();
            case 2:
                return new SettingsFragment();
            default:
                return null;
        }
    }
}

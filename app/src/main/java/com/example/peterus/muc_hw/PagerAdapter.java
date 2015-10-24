package com.example.peterus.muc_hw;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by peterus on 22.10.2015.
 */
public class PagerAdapter extends FragmentStatePagerAdapter {
    private int nTabs;

    public PagerAdapter(FragmentManager fm, int nTabs){
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

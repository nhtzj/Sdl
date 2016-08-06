package com.nht.sdl.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Haitao on 2016/8/4.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {
    List<Fragment> data;

    public MainPagerAdapter(FragmentManager fm, List<Fragment> data) {
        super(fm);
        this.data = data;
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        return data.get(position);
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return data.size();
    }
}

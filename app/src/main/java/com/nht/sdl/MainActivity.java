package com.nht.sdl;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.nht.sdl.adapter.MainPagerAdapter;
import com.nht.sdl.base.BaseActivity;
import com.nht.sdl.fragment.CircleFragment;
import com.nht.sdl.fragment.MsgFragment;

import java.util.ArrayList;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener, TabHost.OnTabChangeListener {

    @BindView(R.id.viewPager)
    android.support.v4.view.ViewPager viewPager;
    @BindView(R.id.tabHost)
    android.support.v4.app.FragmentTabHost tabHost;

    private String[] name = {"信息", "发现"};
    private int[] icons = {R.drawable.tab_icon_msg, R.drawable.tab_icon_circle};
    private Class[] fragments = {MsgFragment.class, CircleFragment.class};

    @Override
    protected boolean hasBackButton() {
        return false;
    }

    @Override
    protected boolean hasOptionsMenu() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        super.initView();
//        ButterKnife.bind(MainActivity.this);
        tabHost.setup(this, getSupportFragmentManager(), R.id.viewPager);
        initTab();
        initViewPager();
    }

    private void initViewPager() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(MsgFragment.newInstance());
        fragments.add(CircleFragment.newInstance());
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
    }

    private void initTab() {
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {//10
            tabHost.getTabWidget().setShowDividers(0);
        }
        TabHost.TabSpec tabSpec;
        View indicator;
        TextView title;
        Drawable drawable;
        for (int i = 0; i < name.length; i++) {
            tabSpec = tabHost.newTabSpec(Integer.toString(i));
            indicator = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.tab_indicator, null);
            title = (TextView) indicator.findViewById(R.id.tab_title);
            drawable = getResources().getDrawable(
                    icons[i]);
            title.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null,
                    null);
            title.setText(name[i]);
            tabSpec.setIndicator(indicator);
            tabSpec.setContent(new TabHost.TabContentFactory() {

                @Override
                public View createTabContent(String tag) {
                    return new View(MainActivity.this);
                }
            });
            tabHost.addTab(tabSpec, fragments[i], null);
        }
    }

    @Override
    public void bindEvent() {
        super.bindEvent();
        viewPager.addOnPageChangeListener(this);
        tabHost.setOnTabChangedListener(this);
    }


    /**
     * This method will be invoked when the current page is scrolled, either as part
     * of a programmatically initiated smooth scroll or a user initiated touch scroll.
     *
     * @param position             Position index of the first page currently being displayed.
     *                             Page position+1 will be visible if positionOffset is nonzero.
     * @param positionOffset       Value from [0, 1) indicating the offset from the page at position.
     * @param positionOffsetPixels Value in pixels indicating the offset from position.
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /**
     * This method will be invoked when a new page becomes selected. Animation is not
     * necessarily complete.
     *
     * @param position Position index of the new selected page.
     */
    @Override
    public void onPageSelected(int position) {
        tabHost.setCurrentTab(position);

    }

    /**
     * Called when the scroll state changes. Useful for discovering when the user
     * begins dragging, when the pager is automatically settling to the current page,
     * or when it is fully stopped/idle.
     *
     * @param state The new scroll state.
     * @see ViewPager#SCROLL_STATE_IDLE
     * @see ViewPager#SCROLL_STATE_DRAGGING
     * @see ViewPager#SCROLL_STATE_SETTLING
     */
    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTabChanged(String tabId) {
        int currentTab = tabHost.getCurrentTab();
        viewPager.setCurrentItem(currentTab);
    }
}

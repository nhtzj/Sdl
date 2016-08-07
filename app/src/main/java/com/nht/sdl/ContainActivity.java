package com.nht.sdl;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.nht.sdl.base.BaseActivity;
import com.nht.sdl.fragment.MsgFragment;

import java.io.Serializable;

public class ContainActivity extends BaseActivity {

    public static final String ARG_DATA="arg_data";
    public static final String ARG_INT="arg_int";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_contain;
    }

    @Override
    public void initView() {
        super.initView();
        setActionBarTitle(R.string.change);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        int position = getIntent().getIntExtra(ARG_INT, 0);
        Serializable extra = getIntent().getSerializableExtra(ARG_DATA);
        MsgFragment msgFragment = MsgFragment.newInstance(position, extra);
        FragmentTransaction trans = getSupportFragmentManager()
                .beginTransaction();
        trans.replace(R.id.container, msgFragment);
        trans.commitAllowingStateLoss();
    }

    @Override
    protected boolean hasOptionsMenu() {
        return false;
    }
}

package com.nht.sdl.base;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nht.sdl.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Haitao.
 */
public abstract class BaseSwipListViewFragment<T> extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener,
        AbsListView.OnScrollListener {
    public static final int STATE_NONE = 0;
    public static final int STATE_REFRESH = 1;
    public static final int STATE_LOADMORE = 2;
    public static int mState = STATE_NONE;

    @BindView(R.id.swiperefreshlayout)
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.listview)
    protected ListView mListView;
    protected BaseListAdapter<T> mAdapter;
    private AsyncTask<String, Void, ArrayList<T>> mCacheTask;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(getLayoutId(), container, false);
            initView(view);
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }

    protected int getLayoutId() {
        return R.layout.fragment_pull_refresh_listview;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void initView(View view) {
        ButterKnife.bind(this, view);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);
        mSwipeRefreshLayout.setEnabled(swipeRefreshLayoutEnadble());

        mListView.setOnItemClickListener(this);
        mListView.setOnScrollListener(this);

        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter = getListAdapter();
            mListView.setAdapter(mAdapter);

            if (requestDataIfViewCreated()) {
                mState = STATE_NONE;
                readDbData(mAdapter.getLastTimestamp());
            } else if (mAdapter.getData().size() == 0) {
                mAdapter.setState(BaseListAdapter.STATE_EMPTY_ITEM);
            }

        }

    }

    @Override
    public void onRefresh() {

        if (mState == STATE_REFRESH) {
            return;
        }
        // 设置顶部正在刷新
        mListView.setSelection(0);
        setSwipeRefreshLoadingState();
        readDbData(mAdapter.getLastTimestamp());
    }

    protected void readDbData(String lastTimestamp) {
        cancelReadDbTask();
        mCacheTask = new DbTask(getActivity()).execute(lastTimestamp);
    }

    private void cancelReadDbTask() {
        if (mCacheTask != null) {
            mCacheTask.cancel(true);
            mCacheTask = null;
        }
    }

    private class DbTask extends AsyncTask<String, Void, ArrayList<T>> {
        private final WeakReference<Context> mContext;

        private DbTask(Context context) {
            mContext = new WeakReference<Context>(context);
        }

        @Override
        protected ArrayList<T> doInBackground(String... params) {

            List<T> list = readFromDb();

            if (list == null) {
                return null;
            } else {
                return (ArrayList<T>) list;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<T> info) {
            super.onPostExecute(info);
            readSuccess(info);
            stopLoadingAnimation();
        }
    }

    protected List<T> readFromDb() {
        return null;
    }


    protected void readSuccess(List<T> list) {

        if (list == null) {
            list = new ArrayList<T>();
        }

        if (STATE_REFRESH == mState) {
            mAdapter.clearItems();
        }

        int adapterState;
        if (mAdapter.getCount() + list.size() == 0) {
            adapterState = BaseListAdapter.STATE_EMPTY_ITEM;
        } else if (list.size() < getPageSize()) {
            adapterState = BaseListAdapter.STATE_NO_MORE;
        } else {
            adapterState = BaseListAdapter.STATE_LOAD_MORE;
        }
        mAdapter.setState(adapterState);
        mAdapter.addItems(list);

        if (mAdapter.getCount() == 1 && mAdapter.hasFooterView() || (mAdapter.getCount() == 0 && !mAdapter.hasFooterView())) {
            mAdapter.setState(BaseListAdapter.STATE_EMPTY_ITEM);
            mAdapter.notifyDataSetChanged();
        }
    }

    protected int getPageSize() {
        return 5;
    }

    // 完成刷新
    protected void stopLoadingAnimation() {
        setSwipeRefreshLoadedState();
        mState = STATE_NONE;
    }

    /**
     * 设置顶部加载完毕的状态
     */
    protected void setSwipeRefreshLoadedState() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setEnabled(true);
        }
    }

    /**
     * 设置顶部正在加载的状态
     */
    protected void setSwipeRefreshLoadingState() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
            // 防止多次重复刷新
            mSwipeRefreshLayout.setEnabled(false);
        }
    }

    protected boolean requestDataIfViewCreated() {
        return true;
    }

    protected abstract BaseListAdapter<T> getListAdapter();

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (mAdapter == null || mAdapter.getCount() == 0) {
            return;
        }
        // 数据已经全部加载，或数据为空时，或正在加载，不处理滚动事件
        if (mState == STATE_LOADMORE || mState == STATE_REFRESH) {
            return;
        }
        // 判断是否滚动到底部
        boolean scrollEnd = false;
        try {
            if (view.getPositionForView(mAdapter.getFooterView()) == view
                    .getLastVisiblePosition())
                scrollEnd = true;
        } catch (Exception e) {
            scrollEnd = false;
        }

        if (mState == STATE_NONE && scrollEnd) {
            if (mAdapter.getState() == BaseListAdapter.STATE_LOAD_MORE) {
                mState = STATE_LOADMORE;
                readDbData(mAdapter.getLastTimestamp());
                mAdapter.setFooterViewLoading();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    public boolean swipeRefreshLayoutEnadble() {
        return false;
    }
}

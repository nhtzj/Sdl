package com.nht.sdl.base;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.nht.sdl.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Haitao.
 */
public class BaseListAdapter<T> extends BaseAdapter {
    public static final int STATE_EMPTY_ITEM = 0;
    public static final int STATE_LOAD_MORE = 1;
    public static final int STATE_NO_MORE = 2;
    public static final int STATE_NO_DATA = 3;
    public static final int STATE_LESS_ONE_PAGE = 4;

    protected int state = STATE_LESS_ONE_PAGE;

    protected int _loadmoreText;
    protected int _loadFinishText;
    protected int _noDataText;

    LayoutInflater mInflater;
    protected List<T> mData = new ArrayList<T>();
    View mFooterView;

    public BaseListAdapter() {
        this._loadmoreText = R.string.loading;
        this._loadFinishText = R.string.loading_no_more;
        this._noDataText = R.string.error_view_no_data;
    }

    public LayoutInflater getLayoutInflater(Context context) {
        if (mInflater == null) {
            mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        return mInflater;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int get_loadmoreText() {
        return _loadmoreText;
    }

    public void set_loadmoreText(int _loadmoreText) {
        this._loadmoreText = _loadmoreText;
    }

    public int get_loadFinishText() {
        return _loadFinishText;
    }

    public void set_loadFinishText(int _loadFinishText) {
        this._loadFinishText = _loadFinishText;
    }

    public int get_noDataText() {
        return _noDataText;
    }

    public void set_noDataText(int _noDataText) {
        this._noDataText = _noDataText;
    }

    @Override
    public int getCount() {
        switch (getState()) {
            case STATE_NO_DATA:
                return 1;
            case STATE_EMPTY_ITEM:
            case STATE_LOAD_MORE:
            case STATE_NO_MORE:
                return getDataSizePlus1();
            case STATE_LESS_ONE_PAGE:
                return getDataSize();
            default:
                break;
        }
        return getDataSize();
    }

    public int getDataSizePlus1() {
        if (hasFooterView()) {
            return getDataSize() + 1;
        }
        return getDataSize();
    }

    public int getDataSize() {
        return mData.size();
    }

    @Override
    public T getItem(int position) {
        if (mData.size() > position) {
            return mData.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == getCount() - 1 && hasFooterView()) {
            if (state != STATE_LESS_ONE_PAGE) {
                mFooterView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_footer, null);
                ProgressBar progressBar = (ProgressBar) mFooterView.findViewById(R.id.progressbar);
                TextView textView = (TextView) mFooterView.findViewById(R.id.text);
                switch (state) {
                    case STATE_LOAD_MORE:
                        setFooterViewLoading();
                        break;
                    case STATE_EMPTY_ITEM:
                        progressBar.setVisibility(View.GONE);
                        textView.setVisibility(View.VISIBLE);
                        textView.setText(_noDataText);
                        break;
                    case STATE_NO_MORE:
                        progressBar.setVisibility(View.GONE);
                        textView.setVisibility(View.VISIBLE);
                        textView.setText(_loadFinishText);
                        break;

                    default:
                        mFooterView.setVisibility(View.GONE);
                        break;

                }
                return mFooterView;
            }
        }
        if (position < 0) {
            position = 0;
        }
        return getRealView(position, convertView, parent);
    }

    protected View getRealView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    public String getLastTimestamp(){
        return String.valueOf(System.currentTimeMillis());
    }

    public void setFooterViewLoading(String loadMsg) {
        ProgressBar progressBar = (ProgressBar) mFooterView
                .findViewById(R.id.progressbar);
        TextView textView = (TextView) mFooterView.findViewById(R.id.text);
        mFooterView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(loadMsg)) {
            textView.setText(_loadmoreText);
        } else {
            textView.setText(loadMsg);
        }
    }

    public void setFooterViewLoading() {
        setFooterViewLoading("");
    }

    public void setData(List<T> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public List<T> getData() {
        return mData == null ? new ArrayList<T>() : mData;
    }

    public void addItem(T t) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        mData.add(t);
        notifyDataSetChanged();
    }

    public void addItems(List<T> list) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public void clearItems() {
        if (mData != null) {
            mData.clear();
        }
        notifyDataSetChanged();
    }

    public void clearItem(T t) {
        if (mData != null) {
            mData.remove(t);
        }
    }

    public void clearItem(int position) {
        if (mData != null && mData.size() > position) {
            mData.remove(position);
        }
        notifyDataSetChanged();
    }

    protected boolean hasFooterView() {
        return true;
    }

    public View getFooterView() {
        return mFooterView;
    }
}

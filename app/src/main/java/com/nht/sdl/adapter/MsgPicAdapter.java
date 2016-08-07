package com.nht.sdl.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nht.sdl.R;
import com.nht.sdl.SdlApp;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Haitao.
 */
public class MsgPicAdapter extends BaseAdapter {
    ArrayList<String> mData;
    LayoutInflater mInflater;
    boolean showDel;

    public MsgPicAdapter(Context context, ArrayList<String> mData) {
        mInflater = LayoutInflater.from(context);
        this.mData = mData;
    }

    public void setData(ArrayList<String> mData) {
        this.mData = mData;
        notifyDataSetChanged();
    }

    public ArrayList<String> getData() {
        return mData;
    }

    public void clearItems(){
        mData.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public String getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null || convertView.getTag() == null) {
            convertView = mInflater.inflate(R.layout.list_msg_pic_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String path = mData.get(position);
        bindData(holder, path, position);
        return convertView;
    }

    private void bindData(ViewHolder holder, String path, final int position) {
        SdlApp.myImageLoader.bindBitmap(path, holder.ivPic);
        if (showDel) {
            holder.deleteChatmember.setVisibility(View.VISIBLE);
            holder.deleteChatmember.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mData.remove(position);
                    notifyDataSetChanged();
                }
            });
        }else {
            holder.deleteChatmember.setVisibility(View.GONE);
        }
    }

    static class ViewHolder {
        @BindView(R.id.ivPic)
        ImageView ivPic;
        @BindView(R.id.deleteChatmember)
        ImageView deleteChatmember;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public void showDel() {
        showDel = !showDel;
        notifyDataSetChanged();
    }

    public String getDataString() {
        StringBuffer buffer = new StringBuffer();
        for (String pic : mData) {
            buffer.append(pic).append(",");
        }
        if (buffer.length() > 0) {
            buffer.deleteCharAt(buffer.length() - 1);
        }
        return buffer.toString();
    }
}

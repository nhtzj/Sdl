package com.nht.sdl.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nht.sdl.R;
import com.nht.sdl.base.BaseListAdapter;
import com.nht.sdl.bean.Message;
import com.nht.sdl.interf.OnCirclesListener;
import com.nht.sdl.utils.DeviceUtils;
import com.nht.sdl.widget.NinePhotoView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Haitao on 2016/8/6.
 */
public class CirclesAdapter extends BaseListAdapter<Message> {
    private OnCirclesListener mListener;
    private DisplayImageOptions options;
    private ImageLoader mLoader;
    private Activity context;

    public CirclesAdapter(Activity context) {
        this.context=context;
        mLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .resetViewBeforeLoading(true)
                .cacheOnDisc(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    public void addOnDeleteListener(OnCirclesListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public String getLastTimestamp() {
        if (mData.size() == 0) {
            return "-1";
        } else {
            return mData.get(mData.size() - 1).getTime();
        }
    }

    @Override
    protected View getRealView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null || convertView.getTag() == null) {
            convertView = getLayoutInflater(parent.getContext()).inflate(R.layout.list_circles_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Message message = mData.get(position);
        initData(viewHolder, message, position);
        return convertView;
    }

    private void initData(final ViewHolder viewHolder, final Message message, final int position) {
        viewHolder.tvName.setText(message.getName());
        viewHolder.tvContent.setText(message.getContent());
        viewHolder.tvTime.setText(DeviceUtils.parseTime(message.getTime()));
        viewHolder.tvName.setText(message.getName());
        viewHolder.npvPhoto.setUrls(message.getPictures());
        viewHolder.npvPhoto.setmLoader(mLoader, options);
        viewHolder.npvPhoto.display();
        if ("1234".equals(message.getOwnerId())) {
            viewHolder.tvDelete.setVisibility(View.VISIBLE);
            viewHolder.tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(viewHolder.tvName.getContext()).setMessage("确认删除此条？").setTitle("警告")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (mListener != null) {
                                        mListener.delete(position, message.getId());
                                    }
                                }
                            })
                            .setNegativeButton("取消", null).show();
                }
            });
            viewHolder.tvChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.change(position, message);
                    }
                }
            });

        } else {
            viewHolder.tvDelete.setVisibility(View.GONE);
            viewHolder.tvChange.setVisibility(View.GONE);
        }


    }


    static class ViewHolder {
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvContent)
        TextView tvContent;
        @BindView(R.id.npvPhoto)
        NinePhotoView npvPhoto;
        @BindView(R.id.tvTime)
        TextView tvTime;
        @BindView(R.id.tvDelete)
        TextView tvDelete;
        @BindView(R.id.tvChange)
        TextView tvChange;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

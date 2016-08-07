package com.nht.sdl.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.nht.sdl.ContainActivity;
import com.nht.sdl.adapter.CirclesAdapter;
import com.nht.sdl.base.BaseListAdapter;
import com.nht.sdl.base.BaseSwipListViewFragment;
import com.nht.sdl.bean.Message;
import com.nht.sdl.db.MessagesDB;
import com.nht.sdl.interf.OnCirclesListener;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CircleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CircleFragment extends BaseSwipListViewFragment<Message> {

    public static final int REQUEST_CODE_CHANGE = 202;
    CirclesAdapter adapter;
    MessagesDB db;

    public static CircleFragment newInstance() {
        return new CircleFragment();
    }

    public CircleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new MessagesDB(getActivity());
    }

    @Override
    protected BaseListAdapter<Message> getListAdapter() {
        adapter = new CirclesAdapter(getActivity());
        adapter.addOnDeleteListener(new OnCirclesListener() {
            @Override
            public void delete(int position, int id) {
                db.deleteWithId(id);
                adapter.getData().remove(position);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void change(int position, Message msg) {
                Intent intent = new Intent(getActivity(), ContainActivity.class);
                intent.putExtra(ContainActivity.ARG_DATA, msg);
                intent.putExtra(ContainActivity.ARG_INT, position);
                startActivityForResult(intent, REQUEST_CODE_CHANGE);
            }
        });
        return adapter;
    }

    @Override
    protected List<Message> readFromDb() {

        return db.getListWithTimestamp(adapter.getLastTimestamp());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_CHANGE:
                if (data != null) {
                    int position = data.getIntExtra("position", -1);
                    if (position==-1){
                        return;
                    }
                    Serializable serializableExtra = data.getSerializableExtra("data");
                    if (serializableExtra instanceof Message){
                        List<Message> list = mAdapter.getData();
                        Collections.replaceAll(list,list.get(position), (Message) serializableExtra);
                        mAdapter.notifyDataSetChanged();
                    }
                }

                break;
        }
    }
}

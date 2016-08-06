package com.nht.sdl.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.nht.sdl.adapter.CirclesAdapter;
import com.nht.sdl.base.BaseListAdapter;
import com.nht.sdl.base.BaseSwipListViewFragment;
import com.nht.sdl.bean.Message;
import com.nht.sdl.db.MessagesDB;
import com.nht.sdl.interf.OnDeleteListener;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CircleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CircleFragment extends BaseSwipListViewFragment<Message> {

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
        adapter = new CirclesAdapter();
        adapter.addOnDeleteListener(new OnDeleteListener() {
            @Override
            public void delete(int position, int id) {
                db.deleteWithId(id);
                adapter.getData().remove(position);
                adapter.notifyDataSetChanged();
            }
        });
        return adapter;
    }

    @Override
    protected List<Message> readFromDb() {

        return db.getListWithTimestamp(adapter.getLastTimestamp());
    }
}

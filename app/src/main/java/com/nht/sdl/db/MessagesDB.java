package com.nht.sdl.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.nht.sdl.bean.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Haitao on 2016/8/5.
 */
public class MessagesDB {
    public DBHelper dbHelper;

    public MessagesDB(Context context) {
        dbHelper = new DBHelper(context);
    }

    public boolean save(ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long insert = db.insert("tblMessages", null, values);
        if (-1 == insert) {
            return false;
        } else {
            return true;
        }

    }

    public List<Message> getListWithTimestamp(String timestamp) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<Message> datas = new ArrayList<>();
        synchronized (MessagesDB.class) {
            Cursor cursor = db.query("tblMessages", new String[]{"*"}, "timestamp > ?", new String[]{timestamp}, null, null, "timestamp asc", "5");
            Message msg;
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("_id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                String pictures = cursor.getString(cursor.getColumnIndex("pictures"));
                String time = cursor.getString(cursor.getColumnIndex("timestamp"));
                String ownerId = cursor.getString(cursor.getColumnIndex("ownerId"));
                msg = new Message();
                msg.setId(id);
                msg.setName(name);
                msg.setContent(content);
                msg.setTime(time);
                msg.setOwnerId(ownerId);
                msg.setPictures(strToList(pictures));
                datas.add(msg);
            }
            cursor.close();
            db.close();
        }

        return datas;
    }

    private List<String> strToList(String str) {
        if (TextUtils.isEmpty(str)) {
            return new ArrayList<>();
        }
        String[] split = str.split(",");
        return Arrays.asList(split);
    }

    public synchronized boolean deleteWithId(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = db.delete("tblMessages", "_id=?", new String[]{String.valueOf(id)});
        db.close();
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

}

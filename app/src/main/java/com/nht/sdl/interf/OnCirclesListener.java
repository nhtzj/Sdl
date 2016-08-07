package com.nht.sdl.interf;

import com.nht.sdl.bean.Message;

/**
 * Created by Haitao on 2016/8/6.
 */
public interface OnCirclesListener {
    void delete(int position, int id);
    void change(int position, Message msg);
}

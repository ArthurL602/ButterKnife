package com.butterknife;

import android.support.annotation.UiThread;

/**
 * Author      :ljb
 * Date        :2018/7/19
 * Description :
 */
public interface Unbinder {
    @UiThread
    void unbind();
    Unbinder EMPTY=new Unbinder() {
        @Override
        public void unbind() {

        }
    };
}
package com.aslan.personalguardianapp.util;

/**
 * Created by gobinath on 11/2/15.
 */
public interface OnResponseListener<T> {
    void onResponseReceived(T result);

    Class getType();
}

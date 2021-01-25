package com.github.gallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhongXiaolong on 2019/12/30 10:27.
 */
class ChoiceGalleryReceiver extends BroadcastReceiver {

    private static final String ACTION = "com.github.application.base.choice.gallery.receiver";
    private Context mContext;
    private OnChoiceGalleryCallback mOnReceiveCall;
    private String mTag;

    public ChoiceGalleryReceiver(Context context, String tag, OnChoiceGalleryCallback onReceiveCall) {
        mContext = context;
        mTag = tag;
        mOnReceiveCall = onReceiveCall;
    }

    public void register() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION);
        mContext.registerReceiver(this, filter);
    }

    public static void post(Context context, String tag, List<String> photos) {
        //设置Action
        Intent intent = new Intent(ACTION);

        //传递数据
        intent.putExtra("tag", tag);
        intent.putStringArrayListExtra("photos", new ArrayList<>(photos));

        //发送广播
        context.sendBroadcast(intent);
    }

    @Override
    public void onReceive(Context context, Intent data) {
        String startTag = data.getStringExtra("tag");
        List<String> photos = data.getStringArrayListExtra("photos");
        if (mTag.equalsIgnoreCase(startTag)) {
            if (mOnReceiveCall != null && photos != null && photos.size() > 0) {
                mOnReceiveCall.onChoiceGalleryComplete(photos);
            }
            mContext.unregisterReceiver(this);
        }
    }
}

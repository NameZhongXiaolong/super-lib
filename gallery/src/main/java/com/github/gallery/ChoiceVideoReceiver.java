package com.github.gallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhongXiaolong on 2019/12/30 10:27.
 */
class ChoiceVideoReceiver extends BroadcastReceiver {

    private static final String ACTION = ".choice.video.receiver";
    private final Context mContext;
    private final OnChoiceVideoCallback mOnReceiveCall;
    private final String mTag;

    public ChoiceVideoReceiver(Context context, String tag, OnChoiceVideoCallback onReceiveCall) {
        mContext = context;
        mTag = tag;
        mOnReceiveCall = onReceiveCall;
    }

    public void register() {
        String callingApp = mContext.getPackageManager().getNameForUid(Binder.getCallingUid());
        String action = TextUtils.isEmpty(callingApp) ? ACTION : callingApp + ACTION;

        //设置action
        IntentFilter filter = new IntentFilter();
        filter.addAction(action);
        mContext.registerReceiver(this, filter);
    }

    public static void post(Context context, String tag, List<String> photos) {
        String callingApp = context.getPackageManager().getNameForUid(Binder.getCallingUid());

        //设置Action
        String action = TextUtils.isEmpty(callingApp) ? ACTION : callingApp + ACTION;
        Intent intent = new Intent(action);

        //传递数据
        intent.putExtra("tag", tag);
        intent.putStringArrayListExtra("videos", new ArrayList<>(photos));

        //发送广播
        context.sendBroadcast(intent);
    }

    @Override
    public void onReceive(Context context, Intent data) {
        String startTag = data.getStringExtra("tag");
        List<String> photos = data.getStringArrayListExtra("videos");
        if (mTag.equalsIgnoreCase(startTag)) {
            if (mOnReceiveCall != null && photos != null && photos.size() > 0) {
                mOnReceiveCall.onChoiceVideoComplete(photos);
            }
            mContext.unregisterReceiver(this);
        }
    }
}

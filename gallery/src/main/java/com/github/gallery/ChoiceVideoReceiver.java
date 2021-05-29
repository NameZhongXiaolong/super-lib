package com.github.gallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Binder;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZhongXiaolong on 2019/12/30 10:27.
 */
class ChoiceVideoReceiver extends BroadcastReceiver {

    private static final String ACTION = ".choice.video.receiver";
    private final Context mContext;
    private OnMediaVideoCallback onMediaVideoCallback;
    private OnVideoUriCallback onVideoUriCallback;
    private OnVideoPathCallback onVideoPathCallback;
    private final String mTag;

    public ChoiceVideoReceiver(Context context, String tag) {
        mContext = context;
        mTag = tag;
    }

    public ChoiceVideoReceiver setOnMediaVideoCallback(OnMediaVideoCallback onMediaVideoCallback) {
        this.onMediaVideoCallback = onMediaVideoCallback;
        return this;
    }

    public ChoiceVideoReceiver setOnVideoUriCallback(OnVideoUriCallback onVideoUriCallback) {
        this.onVideoUriCallback = onVideoUriCallback;
        return this;
    }

    public ChoiceVideoReceiver setOnVideoPathCallback(OnVideoPathCallback onVideoPathCallback) {
        this.onVideoPathCallback = onVideoPathCallback;
        return this;
    }

    public void register() {
        String callingApp = mContext.getPackageManager().getNameForUid(Binder.getCallingUid());
        String action = TextUtils.isEmpty(callingApp) ? ACTION : callingApp + ACTION;

        //设置action
        IntentFilter filter = new IntentFilter();
        filter.addAction(action);
        mContext.registerReceiver(this, filter);
    }

    public static void post(Context context, String tag, List<MediaVideo> mediaVideos) {
        String callingApp = context.getPackageManager().getNameForUid(Binder.getCallingUid());

        //设置Action
        String action = TextUtils.isEmpty(callingApp) ? ACTION : callingApp + ACTION;
        Intent intent = new Intent(action);

        //传递数据
        intent.putExtra("tag", tag);
        intent.putParcelableArrayListExtra("media_videos", new ArrayList<>(mediaVideos));

        //发送广播
        context.sendBroadcast(intent);
    }

    @Override
    public void onReceive(Context context, Intent data) {
        String startTag = data.getStringExtra("tag");
        List<MediaVideo> mediaVideos = data.getParcelableArrayListExtra("media_videos");
        if (mTag.equalsIgnoreCase(startTag)) {
            if ( mediaVideos != null && mediaVideos.size() > 0) {
                if (onMediaVideoCallback != null) {
                    onMediaVideoCallback.onChoiceVideoComplete(mediaVideos);
                }
                if (onVideoUriCallback != null) {
                    List<Uri> uris = new ArrayList<>();
                    for (MediaVideo mediaVideo : mediaVideos) {
                        uris.add(mediaVideo.getUri());
                    }
                    onVideoUriCallback.onChoiceVideoComplete(uris);
                }
                if (onVideoPathCallback != null) {
                    List<String> paths = new ArrayList<>();
                    for (MediaVideo mediaVideo : mediaVideos) {
                        paths.add(mediaVideo.getAbsolutePath());
                    }
                    onVideoPathCallback.onChoiceVideoComplete(paths);
                }
            }
            mContext.unregisterReceiver(this);
        }
    }
}

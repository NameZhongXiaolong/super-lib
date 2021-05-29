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
class ChoiceGalleryReceiver extends BroadcastReceiver {

    private static final String ACTION = ".choice.gallery.receiver";
    private final Context mContext;
    private OnGalleryPathCallback OnGalleryPathCallback;
    private OnGalleryUriCallback OnGalleryUriCallback;
    private OnGalleryMediaImageCallback OnGalleryMediaImageCallback;
    private final String mTag;

    public ChoiceGalleryReceiver(Context context, String tag) {
        mContext = context;
        mTag = tag;
    }

    public ChoiceGalleryReceiver setOnGalleryPathCallback(com.github.gallery.OnGalleryPathCallback onGalleryPathCallback) {
        OnGalleryPathCallback = onGalleryPathCallback;
        return this;
    }

    public ChoiceGalleryReceiver setOnGalleryUriCallback(com.github.gallery.OnGalleryUriCallback onGalleryUriCallback) {
        OnGalleryUriCallback = onGalleryUriCallback;
        return this;
    }

    public ChoiceGalleryReceiver setOnGalleryMediaImageCallback(com.github.gallery.OnGalleryMediaImageCallback onGalleryMediaImageCallback) {
        OnGalleryMediaImageCallback = onGalleryMediaImageCallback;
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

    public static void post(Context context, String tag, List<MediaImage> photos) {
        String callingApp = context.getPackageManager().getNameForUid(Binder.getCallingUid());

        //设置Action
        String action = TextUtils.isEmpty(callingApp) ? ACTION : callingApp + ACTION;
        Intent intent = new Intent(action);

        //传递数据
        intent.putExtra("tag", tag);
        intent.putParcelableArrayListExtra("media_images", new ArrayList<>(photos));

        //发送广播
        context.sendBroadcast(intent);
    }

    @Override
    public void onReceive(Context context, Intent data) {
        String startTag = data.getStringExtra("tag");
        List<MediaImage> mediaImages = data.getParcelableArrayListExtra("media_images");
        if (mTag.equalsIgnoreCase(startTag)) {
            if ( mediaImages != null && mediaImages.size() > 0) {
                //地址回调
                if (OnGalleryPathCallback != null) {
                    List<String> paths = new ArrayList<>();
                    for (MediaImage mediaImage : mediaImages) {
                        paths.add(mediaImage.getAbsolutePath());
                    }
                    OnGalleryPathCallback.onChoiceGalleryComplete(paths);
                }
                //Uri回调
                if (OnGalleryUriCallback != null) {
                    List<Uri> uris = new ArrayList<>();
                    for (MediaImage mediaImage : mediaImages) {
                        uris.add(mediaImage.getUri());
                    }
                    OnGalleryUriCallback.onChoiceGalleryComplete(uris);
                }
                //对象回调
                if (OnGalleryMediaImageCallback != null) {
                    OnGalleryMediaImageCallback.onChoiceGalleryComplete(mediaImages);
                }
            }
            mContext.unregisterReceiver(this);
        }
    }
}

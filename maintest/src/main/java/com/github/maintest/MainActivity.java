package com.github.maintest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.github.dialog.Dialog;
import com.github.gallery.ChoiceGallerySetting;
import com.github.gallery.ChoiceVideo;
import com.github.gallery.OnChoiceVideoCallback;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //图片选择器
    public void onSelectImage(View view) {
        boolean crop = true;
        new ChoiceGallerySetting(this).setLanguage(ChoiceGallerySetting.LANGUAGE_EN).setMilaChatTheme().build();
//        new ChoiceGallerySetting(this).setMilaChatTheme().setLanguage(ChoiceGallerySetting.LANGUAGE_ZH_CN).build();

//        UCrop.Options options = ChoiceGallery.getDefCropOptions(this);
//        options.setToolbarWidgetColor(Color.WHITE);
//        options.setToolbarColor(Color.GREEN);
//        options.withAspectRatio(1, 2);
//        new ChoiceGallery(this)
//                .setCallback(photos -> {
//                    for (String photo : photos) {
//                        Log.d("MaainActivity", photo);
//                    }
//                    ImageView image = findViewById(R.id.image_view);
//                    image.setImageBitmap(BitmapFactory.decodeFile(photos.get(0)));
//                })
//                .setShowCamera(true)
//                .setMaxChoice(6)
//                .start();

        new ChoiceVideo(this).setMaxChoice(3).setVideoSize(0,1024*1024).setCallback(new OnChoiceVideoCallback() {
            @Override
            public void onChoiceVideoComplete(List<String> videos) {
                Log.d("MainActaaivity", "videos:" + videos);
            }
        }).start();
    }

    //弹出框
    public void onDialog(View view) {
        if (mDialog == null) {
            mDialog = new Dialog.InputBuilder(this)
                    .setMessage("请稍候")
                    .create();
        }
        mDialog.show();
    }
}
package com.github.maintest;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.dialog.Dialog;
import com.github.gallery.ChoiceGallery;
import com.github.gallery.ChoiceGallerySetting;
import com.yalantis.ucrop.UCrop;

import java.io.File;

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
        new ChoiceGallerySetting(this).setLanguage(ChoiceGallerySetting.LANGUAGE_ZH_CN).setMilaChatTheme().build();
//        new ChoiceGallerySetting(this).setMilaChatTheme().setLanguage(ChoiceGallerySetting.LANGUAGE_ZH_CN).build();

        UCrop.Options options = ChoiceGallery.getDefCropOptions(this);
        options.setToolbarWidgetColor(Color.WHITE);
        options.setToolbarColor(Color.GREEN);
        options.withAspectRatio(1, 2);
        new ChoiceGallery(this)
                .setCallback(mediaImages -> {
                    ImageView image = findViewById(R.id.image_view);
                    File file = new File(mediaImages.get(0).getAbsolutePath());
                    Glide.with(this).load(file).into(image);
                })
                .setShowCamera(true)
                .setCropWhiteSingle()
                .setMaxChoice(10)
                .start();

//        new ChoiceVideo(this).setMaxChoice(3).setVideoSize(0,1024*1024).setCallback(new OnChoiceVideoCallback() {
//            @Override
//            public void onChoiceVideoComplete(List<MediaVideo> videos) {
//                Log.d("MainActaaivity", "videos:" + videos);
//            }
//        }).start();
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
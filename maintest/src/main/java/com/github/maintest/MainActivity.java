package com.github.maintest;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.github.dialog.Dialog;
import com.github.gallery.ChoiceGallery;
import com.github.gallery.ChoiceGallerySetting;
import com.yalantis.ucrop.UCrop;

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
        new ChoiceGallerySetting(this).setLanguage(ChoiceGallerySetting.LANGUAGE_EN).setMeStoreTheme().build();
//        new ChoiceGallerySetting(this).setMilaChatTheme().setLanguage(ChoiceGallerySetting.LANGUAGE_ZH_CN).build();

        UCrop.Options options = ChoiceGallery.getDefCropOptions(this);
        options.setToolbarWidgetColor(Color.WHITE);
        options.setToolbarColor(Color.GREEN);
        options.withAspectRatio(1, 2);
        new ChoiceGallery(this)
                .setCallback(photos -> {
                    Log.d("MainActivity", photos.toString());
                    ImageView image = findViewById(R.id.image_view);
                    image.setImageBitmap(BitmapFactory.decodeFile(photos.get(0)));
                })
                .setShowCamera(true)
                .setMaxChoice(6)
                .setCropWhiteSingle(options)
                .start();
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
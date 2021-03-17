package com.github.gallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

/**
 * Created by ZhongXiaolong on 2020/4/20 18:00.
 */
public class GalleryPreviewActivity extends BaseActivity {

    interface Code{
        int REQUEST_CODE = 100;
        int RESULT_OK = 167;
        int RESULT_CANCEL = 168;
    }

    private Button mBtnChoiceComplete;
    private ViewGroup mGroupBottomParent;
    private ViewGroup mGroupToolBarParent;
    private TextView mTvTitle;

    private List<String> mChoicePhotos;
    private List<String> mPhotos;
    private ViewPager mViewPager;
    private GalleryPreviewAdapter mAdapter;
    private CheckBox mCheckBox;
    private RecyclerView mRecyclerView;
    private int mMaxChoice;

    static void start(Activity activity, List<String> photos, List<String> choicePhotos, int checkedPosition, int max) {
        Intent starter = new Intent(activity, GalleryPreviewActivity.class);
        //防止数据太大造成异常,使用Eventbus粘性事件传递数据
        EventBus.getDefault().postSticky(new PreviewDataSticky(photos));
        starter.putStringArrayListExtra("choicePhotos", new ArrayList<>(choicePhotos));
        starter.putExtra("checked", checkedPosition);
        starter.putExtra("max", max);
        activity.startActivityForResult(starter, Code.REQUEST_CODE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_activity_preview);

        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onEventBus(PreviewDataSticky dataSticky) {
        mPhotos = dataSticky.getPhotos();

        EventBus.getDefault().removeStickyEvent(dataSticky);
        EventBus.getDefault().unregister(this);

        final int position = getIntent().getIntExtra("checked", 0);
        mMaxChoice = getIntent().getIntExtra("max", 0);
        mChoicePhotos = getIntent().getStringArrayListExtra("choicePhotos");

        Toolbar toolbar = findViewById(R.id.gallery_tool_bar);
        mViewPager = findViewById(R.id.gallery_view_pager);
        mCheckBox = findViewById(R.id.gallery_check_box);
        mRecyclerView = findViewById(R.id.gallery_list);
        mBtnChoiceComplete = findViewById(R.id.gallery_button_complete);
        mGroupToolBarParent = findViewById(R.id.gallery_tool_bar_parent);
        mGroupBottomParent = findViewById(R.id.gallery_bottom_parent);
        mTvTitle = findViewById(R.id.gallery_text_title);

        //完成按钮
        mBtnChoiceComplete.setOnClickListener(this::onCompleteClick);
        if (mMaxChoice == 1) {
            mBtnChoiceComplete.setText(R.string.gallery_complete);
            mBtnChoiceComplete.setEnabled(true);
            //单选不显示底部预览
            mGroupBottomParent.setAlpha(0);
            mCheckBox.setVisibility(View.GONE);
        } else {
            mBtnChoiceComplete.setText(getString(R.string.gallery_catalog_complete, mChoicePhotos.size(), mMaxChoice));
            mBtnChoiceComplete.setEnabled(mChoicePhotos.size() > 0);
        }

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new GalleryPreviewAdapter(mChoicePhotos, this::onItemClick);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setChecked(mChoicePhotos.indexOf(mPhotos.get(position)));
        mTvTitle.setText(((position + 1) + "/" + mPhotos.size()));
        mRecyclerView.scrollToPosition(mAdapter.getCheckedPosition());

        mCheckBox.setChecked(mAdapter.getCheckedPosition() >= 0);
        mCheckBox.setOnCheckedChangeListener(this::onCheckedChanged);

        mViewPager.setAdapter(new PhotoAdapter(getSupportFragmentManager(), mPhotos));
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setCurrentItem(position, false);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                mAdapter.setChecked(mChoicePhotos.indexOf(mPhotos.get(position)));
                mCheckBox.setChecked(mAdapter.getCheckedPosition() >= 0);
                mTvTitle.setText(((position + 1) + "/" + mPhotos.size()));
                if (mAdapter.getCheckedPosition() >= 0) {
                    mRecyclerView.scrollToPosition(mAdapter.getCheckedPosition());
                }
            }
        });
    }

    private void onItemClick(String photo, int position) {
        int index = mPhotos.indexOf(photo);
        if (index >= 0) {
            mViewPager.setCurrentItem(index, true);
            mAdapter.setChecked(position);
            mCheckBox.setChecked(mAdapter.getCheckedPosition() >= 0);
        }
    }

    private void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String photo = mPhotos.get(mViewPager.getCurrentItem());
        if (isChecked) {
            if (!mChoicePhotos.contains(photo)) {
                if (mChoicePhotos.size() < mMaxChoice) {
                    mChoicePhotos.add(photo);
                    mAdapter.notifyItemRangeInserted(mAdapter.getItemCount() - 1, mChoicePhotos.size());
                    mAdapter.setChecked(mChoicePhotos.size() - 1);
                    mRecyclerView.scrollToPosition(mChoicePhotos.size() - 1);
                    mBtnChoiceComplete.setText(getString(R.string.gallery_catalog_complete, mChoicePhotos.size(), mMaxChoice));
                    mBtnChoiceComplete.setEnabled(mChoicePhotos.size() > 0);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.gallery_max_photo, mMaxChoice), Toast.LENGTH_SHORT).show();
                    mCheckBox.setChecked(false);
                }
            }
        } else {
            if (mChoicePhotos.contains(photo)) {
                int index = mChoicePhotos.indexOf(photo);
                mChoicePhotos.remove(photo);
                mAdapter.notifyItemRemoved(index);
                mAdapter.notifyItemRangeChanged(index, mChoicePhotos.size() - index);
                mBtnChoiceComplete.setText(getString(R.string.gallery_catalog_complete, mChoicePhotos.size(), mMaxChoice));
                mBtnChoiceComplete.setEnabled(mChoicePhotos.size() > 0);
                mAdapter.setChecked(-1);
            }
        }
    }

    /**
     * 完成事件
     */
    private void onCompleteClick(View view) {
        if (mMaxChoice == 1 && mChoicePhotos.size() == 0) {
            //如果单选同时没有选中的照片,添加当前的照片(单选完成按钮可以点击)
            mChoicePhotos.add(mPhotos.get(mViewPager.getCurrentItem()));
        }
        Intent data = new Intent();
        data.putStringArrayListExtra("choicePhotos", new ArrayList<>(mChoicePhotos));
        setResult(Code.RESULT_OK, data);
        finish();
    }

    private boolean mIsFullScreen;
    private long mSwitchFullScreenTime;

    /**
     * 切换/关闭全屏
     */
    public void switchFullScreen() {
        final int duration = 500;
        if (System.currentTimeMillis() - mSwitchFullScreenTime < duration) {
            return;
        }

        mSwitchFullScreenTime = System.currentTimeMillis();

        if (mIsFullScreen) {
            //关闭全屏
            mGroupToolBarParent.setVisibility(View.VISIBLE);
            TranslateAnimation translate1 = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, -1.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f);
            translate1.setDuration(duration);
            mGroupToolBarParent.startAnimation(translate1);

            mGroupBottomParent.setVisibility(View.VISIBLE);
            TranslateAnimation translate2 = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 1.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f);
            translate2.setDuration(duration);
            mGroupBottomParent.startAnimation(translate2);

        }else{
            //开启全屏
            TranslateAnimation translate1 = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, -1.0f);
            translate1.setDuration(duration);
            mGroupToolBarParent.startAnimation(translate1);
            mGroupToolBarParent.postDelayed(() -> mGroupToolBarParent.setVisibility(View.GONE), duration);

            TranslateAnimation translate2 = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 1.0f);
            translate2.setDuration(duration);
            mGroupBottomParent.startAnimation(translate2);
            mGroupBottomParent.postDelayed(() -> mGroupBottomParent.setVisibility(View.GONE), duration);
        }
        mIsFullScreen = !mIsFullScreen;
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.putStringArrayListExtra("choicePhotos", new ArrayList<>(mChoicePhotos));
        setResult(Code.RESULT_CANCEL, data);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class PhotoAdapter extends FragmentPagerAdapter {

        private final List<String> mPhotos;

        public PhotoAdapter(@NonNull FragmentManager fm, List<String> photos) {
            super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            mPhotos = photos;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return GalleryPreviewFragment.newInstance(mPhotos.get(position));
        }

        @Override
        public int getCount() {
            return mPhotos.size();
        }
    }
}

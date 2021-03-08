package com.github.gallery;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static android.widget.AbsListView.CHOICE_MODE_SINGLE;
import static com.github.gallery.PhotoAdapter.OnItemClickType.CAMERA;
import static com.github.gallery.PhotoAdapter.OnItemClickType.CHECKED;

/**
 * Created by ZhongXiaolong on 2019/12/30 17:16.
 * <p>
 * 使用方法{@link ChoiceGallery},不要直接调用本Activity
 * <p>
 * Event事件
 * 扫描本地图片结果 {@link ChoiceGalleryActivity#onScanningLocalPhotoResultEvent(LoadResultData)}
 * 扫描本地图片成功返回的集合 {@link ChoiceGalleryActivity#onScanningLocalPhotoCompleteEvent(List)}
 * 拍照成功 {@link ChoiceGalleryActivity#onCameraResultEvent(CameraResult)}
 * 裁剪成功 {@link ChoiceGalleryActivity#onCropResultEvent(CropResult)}
 */
public class ChoiceGalleryActivity extends BaseActivity {

    private AlertDialog mPermissionDialog;//权限弹出框
    private boolean mLoadSucceed = true;//加载是否成功
    private PhotoAdapter mPhotoAdapter;
    private Button mBtnChoiceComplete;
    private Button mBtnPreview;
    private TextView mTvTitle;
    private ImageButton mIbTitleIcon;
    private String mTag;
    private int mMaxChoice;
    private long mCatalogSelectClickTimeMillis;
    private ViewStub mVsContent;
    private ArrayAdapter<PhotoData> mCatalogAdapter;
    private ListView mLvCatalog;
    private SwipeRefreshLayout mRefreshLayout;
    private View mVBgButton;
    private int mCatalogSelectedPosition = -1;
    private boolean mCanCrop;
    private boolean mShowCamera;

    static void start(ChoiceGallery choiceGallery) {
        Intent starter = new Intent(choiceGallery.getContext(), ChoiceGalleryActivity.class);
        String tag = "tag" + System.currentTimeMillis();
        starter.putExtra("tag", tag);
        starter.putExtra("maxChoice", choiceGallery.getMaxChoice());
        starter.putExtra("crop", choiceGallery.getMaxChoice() == 1 && choiceGallery.isCropWhiteSingle());
        starter.putExtra("show_camera", choiceGallery.isShowCamera());
        GalleryCropFragment.mUCropOptions = choiceGallery.getCropOptions();
        starter.putStringArrayListExtra("choiceList", new ArrayList<>(choiceGallery.getChoiceList()));
        choiceGallery.getContext().startActivity(starter);
        new ChoiceGalleryReceiver(choiceGallery.getContext(), tag, choiceGallery.getCallback()).register();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_activity_choice);

        //find view
        Toolbar toolbar = findViewById(R.id.gallery_tool_bar);
        mBtnChoiceComplete = findViewById(R.id.gallery_button_complete);
        mTvTitle = findViewById(R.id.gallery_text_title);
        mIbTitleIcon = findViewById(R.id.gallery_image_button_ic);


        //get intent data
        mTag = getIntent().getStringExtra("tag");
        mMaxChoice = getIntent().getIntExtra("maxChoice", 0);
        mCanCrop = getIntent().getBooleanExtra("crop", false);
        mShowCamera = getIntent().getBooleanExtra("show_camera", false);

        //标题
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        mVsContent = findViewById(R.id.gallery_view_stub);
        mVsContent.setOnInflateListener(this::onScanningLocalPhotoSuccessInflateContentView);

        //单选裁剪隐藏"完成按钮"
        if (mMaxChoice == 1 && mCanCrop) {
            mBtnChoiceComplete.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    /**
     * 扫描图片成功填充主布局监听
     */
    private void onScanningLocalPhotoSuccessInflateContentView(ViewStub stub, View contentView) {
        mBtnPreview = contentView.findViewById(R.id.gallery_button_preview);
        mLvCatalog = contentView.findViewById(R.id.gallery_list_catalog);
        mRefreshLayout = contentView.findViewById(R.id.gallery_refresh_layout);
        RecyclerView recyclerView = contentView.findViewById(R.id.gallery_list);
        mVBgButton = contentView.findViewById(R.id.gallery_button_bg);

        //初始化目录
        mLvCatalog.setChoiceMode(CHOICE_MODE_SINGLE);
        mCatalogAdapter = new ArrayAdapter<>(this, R.layout.gallery_item_catalog, R.id.gallery_text_catalog);
        mLvCatalog.setAdapter(mCatalogAdapter);
        mLvCatalog.setOnItemClickListener((parent, view, position, id) -> onCatalogItemClick(mCatalogAdapter, position));
        GradientDrawable listViewBackground = new GradientDrawable();
        float dp10 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        listViewBackground.setCornerRadii(new float[]{0, 0, 0, 0, dp10, dp10, dp10, dp10});
        listViewBackground.setColor(Color.WHITE);
        mLvCatalog.setBackground(listViewBackground);

        //初始化照片列表
        List<String> choicePhoto = getIntent().getStringArrayListExtra("choiceList");
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        mPhotoAdapter = new PhotoAdapter(this::onPhotoItemClick);
        mPhotoAdapter.showCamera(mShowCamera);
        mPhotoAdapter.setChoicePhotos(choicePhoto);
        recyclerView.setAdapter(mPhotoAdapter);
        recyclerView.setHasFixedSize(true);

        //刷新
        mRefreshLayout.setColorSchemeColors(getColorAccent());
        mRefreshLayout.setOnRefreshListener(this::onRefresh);

        //完成按钮
        mBtnChoiceComplete.setText(getString(R.string.gallery_catalog_complete, mPhotoAdapter.getChoicePhotoCount(), mMaxChoice));
        mBtnChoiceComplete.setEnabled(mPhotoAdapter.getChoicePhotoCount() > 0);
        mBtnPreview.setEnabled(mBtnChoiceComplete.isEnabled());

        //预览按钮
        int[] colors = new int[]{Color.parseColor("#333333"), Color.parseColor("#CCCCCC")};
        int[][] states = {{android.R.attr.state_enabled}, {}};
        mBtnPreview.setTextColor(new ColorStateList(states, colors));
        mBtnPreview.setOnClickListener(this::onStartPreview);


        //onClick
        mVBgButton.setOnClickListener(v -> onCatalogViewVisibility());
        mTvTitle.setOnClickListener(v -> onCatalogViewVisibility());
        mIbTitleIcon.setOnClickListener(v -> onCatalogViewVisibility());
        mBtnChoiceComplete.setOnClickListener(this::onCompleteButtonClick);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //注册接收事件
        EventBus.getDefault().register(this);
        //扫描
        ScanningLocalPhotoService.enqueueWork(this, new Intent(this, ScanningLocalPhotoService.class));

        //等待进度
        View progressLayout = findViewById(R.id.gallery_progress_layout);
        progressLayout.postDelayed(() -> progressLayout.setVisibility(View.VISIBLE), 800);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        //扫描图片
                        ScanningLocalPhotoService.enqueueWork(this, new Intent(this, ScanningLocalPhotoService.class));
                    } else {
                        showPermissionDialog();
                    }
                    break;
                }
            }
        }
    }

    /**
     * 刷新
     */
    private void onRefresh() {
        ScanningLocalPhotoService.enqueueWork(this, new Intent(this, ScanningLocalPhotoService.class));
    }

    /**
     * 检查权限
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (!mLoadSucceed) {
            ScanningLocalPhotoService.enqueueWork(this, new Intent(this, ScanningLocalPhotoService.class));
        }
    }

    /**
     * 扫面本地图片返回(成功的时候才会返回)
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScanningLocalPhotoCompleteEvent(List<PhotoData> data) {
        if (mVsContent != null) {
            mVsContent.inflate();
            mVsContent = null;
        }

        //目录
        PhotoData selectedItem = mCatalogSelectedPosition >= 0 ? mCatalogAdapter.getItem(mCatalogSelectedPosition) : null;
        mCatalogAdapter.clear();
        mCatalogAdapter.addAll(data);

        //显示完成按钮
        mBtnChoiceComplete.setVisibility(View.VISIBLE);

        //显示标题和图标
        mTvTitle.setVisibility(View.VISIBLE);
        mIbTitleIcon.setVisibility(View.VISIBLE);

        //显示预览按钮
        mBtnPreview.setVisibility(View.VISIBLE);

        //设置默认点击项
        int selected = 0;
        if (selectedItem != null) selected = Math.max(data.indexOf(selectedItem), 0);
        if (mCatalogAdapter.getCount() > selected) mLvCatalog.performItemClick(mLvCatalog.getChildAt(selected), selected, selected);

        mRefreshLayout.setRefreshing(false);
    }

    /**
     * 扫描结果事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onScanningLocalPhotoResultEvent(LoadResultData data) {
        //没有权限
        if (data.getCode() == LoadResultData.CODE_NO_PERMISSION) {
            mLoadSucceed = false;
            //请求权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            }
        }
        if (data.getCode() == LoadResultData.CODE_SUCCESS) {
            mLoadSucceed = true;
            dismissPermissionDialog();
        }
    }

    /**
     * 拍照结果
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCameraResultEvent(CameraResult data) {
        final String path = data.getPath();
        if (mCanCrop) {
            //裁剪
            GalleryCropFragment.start(this, path);
        }

        mPhotoAdapter.add(1, path);
        if (mCanCrop) {
            //裁剪-默认选中
            mPhotoAdapter.setChoicePhotos(new ArrayList<>());
            mPhotoAdapter.setChecked(1, true);
        }
        if (mCatalogAdapter.getCount() > 0) {
            mCatalogAdapter.getItem(0).getPhotoList().add(0, path);
            for (int i = 0; i < mCatalogAdapter.getCount(); i++) {
                if (mCatalogAdapter.getItem(i).getPath().equals("Camera")) {
                    mCatalogAdapter.getItem(i).getPhotoList().add(0, path);
                    break;
                }
            }
        }
    }

    /**
     * 裁剪结果
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCropResultEvent(CropResult data) {
        ChoiceGalleryReceiver.post(this, mTag, Collections.singletonList(data.getPath()));
        finish();
    }

    /**
     * 目录Item点击事件
     */
    private void onCatalogItemClick(ArrayAdapter<PhotoData> catalogAdapter, int position) {
        mPhotoAdapter.setData(Objects.requireNonNull(catalogAdapter.getItem(position)).getPhotoList());
        mTvTitle.setText(Objects.requireNonNull(catalogAdapter.getItem(position)).getCatalog());
        mLvCatalog.postDelayed(this::closeCatalogView, 200);
        mCatalogSelectedPosition = position;
    }

    /**
     * 照片点击事件
     */
    private void onPhotoItemClick(PhotoAdapter.OnItemClickType type, int position) {
        if (type == CAMERA) {
            //相机
            GalleryCameraFragment.start(this);
        } else if (mCanCrop) {
            //裁剪
            mPhotoAdapter.setChoicePhotos(new ArrayList<>());
            mPhotoAdapter.setChecked(position, true);
            GalleryCropFragment.start(this, mPhotoAdapter.getItem(position));
        } else if (type == CHECKED) {
            //勾选
            boolean checked = mPhotoAdapter.getChecked(position);
            if (!checked && mPhotoAdapter.getChoicePhotoCount() >= mMaxChoice) {
                Toast.makeText(getApplicationContext(), getString(R.string.gallery_max_photo, mMaxChoice), Toast.LENGTH_SHORT).show();
            } else {
                mPhotoAdapter.setChecked(position, !checked);
                mBtnChoiceComplete.setText(getString(R.string.gallery_catalog_complete, mPhotoAdapter.getChoicePhotoCount(), mMaxChoice));
                mBtnChoiceComplete.setEnabled(mPhotoAdapter.getChoicePhotoCount() > 0);
                mBtnPreview.setEnabled(mBtnChoiceComplete.isEnabled());
            }
        } else {
            //查看(预览)
            List<String> data = new ArrayList<>(mPhotoAdapter.getData());
            int checkedPosition = position;
            if (mShowCamera) {
                //如果显示了相机去掉相机的数据
                if (data.remove(PhotoAdapter.SHOW_CAMERA)) {
                    checkedPosition -= 1;
                }
            }
            GalleryPreviewActivity.start(this, data, mPhotoAdapter.getChoicePhotos(), checkedPosition, mMaxChoice);
        }
    }

    /**
     * 预览
     */
    private void onStartPreview(View view) {
        List<String> choicePhotos = mPhotoAdapter.getChoicePhotos();
        GalleryPreviewActivity.start(this, choicePhotos, choicePhotos, 0, mMaxChoice);
    }

    /**
     * 完成点击事件
     */
    private void onCompleteButtonClick(View view) {
        if (!closeCatalogView()) {
            ChoiceGalleryReceiver.post(this, mTag, mPhotoAdapter.getChoicePhotos());
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GalleryPreviewActivity.Code.REQUEST_CODE && data != null) {
            if (resultCode == GalleryPreviewActivity.Code.RESULT_OK) {
                //完成,回调
                List<String> photos = data.getStringArrayListExtra("choicePhotos");
                ChoiceGalleryReceiver.post(this, mTag, photos);
                finish();
            }
            if (resultCode == GalleryPreviewActivity.Code.RESULT_CANCEL) {
                //取消,更新 mPhotoAdapter
                List<String> photos = data.getStringArrayListExtra("choicePhotos");
                mPhotoAdapter.setChoicePhotos(photos);
                mBtnChoiceComplete.setText(getString(R.string.gallery_catalog_complete, mPhotoAdapter.getChoicePhotoCount(), mMaxChoice));
                mBtnChoiceComplete.setEnabled(mPhotoAdapter.getChoicePhotoCount() > 0);
                mBtnPreview.setEnabled(mBtnChoiceComplete.isEnabled());
            }
        }
    }

    /**
     * 显示/隐藏目录
     */
    private void onCatalogViewVisibility() {
        //防止连续点击
        long catalogSelectClickTimeMillis = System.currentTimeMillis();
        if (catalogSelectClickTimeMillis - mCatalogSelectClickTimeMillis < 350) {
            return;
        }

        mCatalogSelectClickTimeMillis = catalogSelectClickTimeMillis;

        Animation anim = new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(300); // 设置动画时间
        anim.setInterpolator(new AccelerateInterpolator()); // 设置插入器
        mIbTitleIcon.startAnimation(anim);
        mIbTitleIcon.postDelayed(() -> mIbTitleIcon.setRotation((mIbTitleIcon.getRotation() + 180) % 360 == 0 ? 0 : 180), 300);

        if (mLvCatalog.getVisibility() == View.VISIBLE) {
            //隐藏
            TranslateAnimation translate = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, -1.0f);
            translate.setDuration(300);
            mLvCatalog.startAnimation(translate);
            mLvCatalog.postDelayed(() -> mLvCatalog.setVisibility(View.GONE), 300);

            mVBgButton.setBackgroundColor(Color.TRANSPARENT);
            mVBgButton.setVisibility(View.GONE);
            mBtnChoiceComplete.postDelayed(() -> mBtnChoiceComplete.setVisibility(View.VISIBLE), 300);
        } else {
            //显示
            mLvCatalog.setVisibility(View.VISIBLE);

            TranslateAnimation translate = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, -1.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f);
            translate.setDuration(300);
            mLvCatalog.startAnimation(translate);

            mVBgButton.setVisibility(View.VISIBLE);
            mVBgButton.setBackgroundColor(Color.parseColor("#80000000"));
            mBtnChoiceComplete.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 关闭目录
     *
     * @return true关闭成功, false已经处于关闭状态
     */
    private boolean closeCatalogView() {
        if (mLvCatalog == null || mLvCatalog.getVisibility() != View.VISIBLE) {
            return false;
        } else {
            onCatalogViewVisibility();
            return true;
        }
    }

    /**
     * 显示提示权限弹出框
     */
    private void showPermissionDialog() {
        if (mPermissionDialog != null) {
            mPermissionDialog.show();
            return;
        }
        mPermissionDialog = new AlertDialog
                .Builder(this, R.style.Gallery_AlertDialogTheme)
                .setTitle(R.string.gallery_hint_tip)
                .setMessage(R.string.gallery_hint_need_sd_permission)
                .setCancelable(false)
                .setPositiveButton(R.string.gallery_go_setting, (dialog, which) -> {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                        }
                    } else {
                        Intent starter = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                        String pkg = "com.android.settings";
                        String cls = "com.android.settings.applications.InstalledAppDetails";
                        starter.setComponent(new ComponentName(pkg, cls));
                        starter.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(starter);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.gallery_exit, (dialog, which) -> {
                    dialog.dismiss();
                    new Handler(getMainLooper()).postDelayed(this::finish, 300);
                })
                .show();
        mPermissionDialog.setCanceledOnTouchOutside(false);
        mPermissionDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#E84393"));
        mPermissionDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#999999"));
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //关闭弹窗
    private void dismissPermissionDialog() {
        if (mPermissionDialog != null && mPermissionDialog.isShowing()) {
            mPermissionDialog.dismiss();
        }
    }

    private boolean mIsFinished;

    private void onFinish() {
        mIsFinished = true;
        GalleryCropFragment.mUCropOptions = null;
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing() && !mIsFinished) {
            onFinish();
        }
    }

    @Override
    public void onBackPressed() {
        if (!closeCatalogView()) {
            super.onBackPressed();
            //返回也发送广播,触发注销广播方法
            ChoiceGalleryReceiver.post(this, mTag, new ArrayList<>());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!mIsFinished) {
            onFinish();
        }
    }
}

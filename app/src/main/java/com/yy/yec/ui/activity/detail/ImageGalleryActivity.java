package com.yy.yec.ui.activity.detail;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.request.target.Target;
import com.yy.yec.R;
import com.yy.yec.adapter.DetailPhotoViewAdapter;
import com.yy.yec.global.AppThreadPool;
import com.yy.yec.ui.base.BaseActivity;
import com.yy.yec.ui.widget.PictureViewPager;
import com.yy.yec.ui.widget.PicturesCompressor;
import com.yy.yec.utils.ImgUtils;
import com.yy.yec.utils.StreamUtils;
import com.yy.yec.utils.ToastUtil;

import java.io.File;
import java.util.concurrent.Future;

import butterknife.Bind;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.RuntimePermissions;

/**
 * 图片预览Activity
 */
@RuntimePermissions
public class ImageGalleryActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    public static String KEY_IMAGE = "images";
    public static String KEY_COOKIE = "cookie";
    public static String KEY_POSITION = "position";
    public static String KEY_NEED_SAVE = "save";
    @Bind(R.id.vp_photoView)
    protected PictureViewPager vPager;
    @Bind(R.id.tv_index)
    protected TextView mIndexText;
    private String[] mImageSources;
    private int mCurPosition;
    private boolean mNeedSaveLocal;
    private boolean mNeedCookie;

    @Override
    protected void onDestroy() {
        vPager = null;
        mIndexText = null;
        mImageSources = null;
        super.onDestroy();
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initWidget();
        initData();
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        mImageSources = bundle.getStringArray(KEY_IMAGE);
        mCurPosition = bundle.getInt(KEY_POSITION, 0);
        mNeedSaveLocal = bundle.getBoolean(KEY_NEED_SAVE, true);
        mNeedCookie = bundle.getBoolean(KEY_COOKIE, false);
        return mImageSources != null;
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_image_gallery;
    }

    protected void initWidget() {
        if (mNeedSaveLocal)
            findViewById(R.id.iv_save).setOnClickListener(this);
        else
            findViewById(R.id.iv_save).setVisibility(View.GONE);
    }

    private void setPageNum(int curPosition) {
        StringBuilder _sb = new StringBuilder();
        _sb.append(curPosition);
        _sb.append("/");
        _sb.append(mImageSources.length);
        mIndexText.setText(_sb.toString());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (mIndexText.getVisibility() == View.GONE) return;
        mCurPosition = position;
        setPageNum(mCurPosition + 1);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    protected void initData() {
        int len = mImageSources.length;
        if (mCurPosition < 0 || mCurPosition >= len)
            mCurPosition = 0;
        vPager.setAdapter(new DetailPhotoViewAdapter(this, mImageSources));
        vPager.addOnPageChangeListener(this);
        vPager.setCurrentItem(mCurPosition);

        if (len < 2) mIndexText.setVisibility(View.GONE);
        else setPageNum(mCurPosition + 1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_save:
                ImageGalleryActivityPermissionsDispatcher.saveToFileWithCheck(this);
                break;
        }
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)//获取“写外部存储权限”执行的代码
    protected void saveToFile() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            ToastUtil.showToast("没有外部存储!");
            return;
        }
        String path = mImageSources[mCurPosition];
        Object urlOrPath;
        if (mNeedCookie)
            urlOrPath = ImgUtils.getUserGlideUrl(path);
        else
            urlOrPath = path;

        final Future<File> future = ImgUtils.load(this, urlOrPath).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);

        AppThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    File sourceFile = future.get();//直接拿到Glide下载的图片
                    if (sourceFile == null || !sourceFile.exists())
                        return;
                    String extension = PicturesCompressor.getExtension(sourceFile.getAbsolutePath());
                    String extDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "YEC";
                    File extDirFile = new File(extDir);
                    if (!extDirFile.exists()) {
                        if (!extDirFile.mkdirs()) {
                            // If mk dir error
                            callSaveStatus(false, null);
                            return;
                        }
                    }
                    final File saveFile = new File(extDirFile, String.format("IMG_%s.%s", System.currentTimeMillis(), extension));
                    final boolean isSuccess = StreamUtils.copyFile(sourceFile, saveFile);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callSaveStatus(isSuccess, saveFile);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

//    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)//提示用户为什么需要此权限
//    protected void saveToFileTip(final PermissionRequest request) {
//        new AlertDialog.Builder(ImageGalleryActivity.this)
//                .setTitle(R.string.help_dialog)
//                .setMessage(R.string.help_dialog_content)
//                .setPositiveButton(R.string.help_dialog_apply, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        request.proceed();//再发起申请
//                    }
//                }).show();
//    }

//    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)//用户拒绝“写外部存储权限”执行的代码
//    protected void saveToFileDenied() {
//        ToastUtil.showToast("拒绝");
//    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)//用户选定不再提醒后调用
    protected void saveToFileNever() {
        new AlertDialog.Builder(ImageGalleryActivity.this)
                .setTitle(R.string.help_dialog)
                .setMessage(R.string.help_dialog_content)
                .setPositiveButton(R.string.help_dialog_setting, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startAppSettings();//发起设置
                    }
                }).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ImageGalleryActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    //启动应用的设置
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    private void callSaveStatus(boolean success, File savePath) {
        if (success) {
            Uri uri = Uri.fromFile(savePath);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            ToastUtil.showToast(R.string.save_state_succeed);
        } else
            ToastUtil.showToast(R.string.save_state_fail);
    }
}
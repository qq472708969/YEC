package com.yy.yec.ui.widget;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;

import com.yy.yec.R;
import com.common.widget.base.BaseDialog;
import com.yy.yec.utils.ToastUtil;

import butterknife.OnClick;

/**
 * Created by zzq on 2016/9/29.
 */
public class TipDialog extends BaseDialog {
    @Override
    protected int setContentView() {
        return R.layout.dialog_tip;
    }

    public TipDialog(Context context) {
        super(context, R.style.dialog_common);
    }

    @Override
    protected void init(Bundle savedInstanceState, Window currWin) {
        currWin.setGravity(Gravity.RIGHT);
//        currWin.setWindowAnimations(R.style.dialog_tip_animation);
    }

    @OnClick(R.id.ll_item_word)
    public void ll_item_wordClick(View v) {
        ToastUtil.showToast("ll_item_wordClick");
    }

    @OnClick(R.id.ll_item_album)
    public void ll_item_albumClick(View v) {
        ToastUtil.showToast("ll_item_albumClick");
    }

    @OnClick(R.id.ll_item_camera)
    public void ll_item_camera(View v) {
        ToastUtil.showToast("ll_item_wordClick");
    }

    @OnClick(R.id.ll_item_voice)
    public void ll_item_voice(View v) {
        ToastUtil.showToast("ll_item_albumClick");
    }

    @OnClick(R.id.ll_item_code2)
    public void ll_item_code2(View v) {
        ToastUtil.showToast("ll_item_wordClick");
    }

    @OnClick(R.id.ll_item_note)
    public void ll_item_note(View v) {
        ToastUtil.showToast("ll_item_albumClick");
    }
}

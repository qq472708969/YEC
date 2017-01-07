package com.common.widget.comment;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.widget.R;
import com.common.widget.Utils;
import com.common.widget.comment.brow.BrowEntity;
import com.common.widget.comment.brow.BrowPagerAdapter;
import com.common.widget.comment.brow.BrowType;
import com.common.widget.comment.brow.BrowViewPager;

import java.util.List;

/**
 * Created by zzq on 2016/9/28.
 */
public class CommentArea<Input> extends RelativeLayout implements View.OnClickListener {
    private EditText et_input;
    private ImageView iv_fav;
    private ImageView iv_share;
    private ImageView iv_brow;
    private CommentAreaCtrlListener<Input> cl;
    private boolean showShare;
    private boolean showFav;
    private boolean showBrow;
    private Input paramObj;
    private FrameLayout commonPanel;//评论区下面的操作面板，可容纳表情等
    private View browLayout;//表情操作区
    private BrowViewPager vpBrow;//表情区切换
    private boolean softKeyboardHide = false;
    private int softKeyboardTagBottom = -1;
    private Rect r;
    private ImageView iv_send;

    public CommentArea(Context context) {
        this(context, null);
    }

    public CommentArea(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            TypedArray attrsTypedArray = context.obtainStyledAttributes(attrs, R.styleable.CommentArea);
            if (attrsTypedArray != null) {
                showBrow = attrsTypedArray.getBoolean(R.styleable.CommentArea_showBrow, false);
                showFav = attrsTypedArray.getBoolean(R.styleable.CommentArea_showFav, false);
                showShare = attrsTypedArray.getBoolean(R.styleable.CommentArea_showShare, false);
                attrsTypedArray.recycle();
            }
        }
        setBackgroundResource(R.color.white);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {//设置merge后引入的内部间距
        int bottom = 0;
        if (!(showBrow || showFav || showShare))
            bottom = (int) getResources().getDimension(R.dimen.space_size_6);
        setPadding((int) getResources().getDimension(R.dimen.space_size_8), (int) getResources().getDimension(R.dimen.space_size_6), (int) getResources().getDimension(R.dimen.space_size_8), bottom);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setCommentAreaCtrlListener(CommentAreaCtrlListener<Input> cl) {
        this.cl = cl;
    }

    /**
     * 保存参数到回调函数中
     */
    public void saveParam(String hint, Input param) {
        if (param != null) paramObj = param;
        et_input.setHint(hint);
        Utils.showSoftKeyboard(et_input);
    }

    public void saveParam(String hint) {
        saveParam(hint, null);
    }

    public void clearParam() {
        paramObj = null;
    }

    public void setShowShare(boolean show) {
        if (show)
            iv_share.setVisibility(VISIBLE);
        else
            iv_share.setVisibility(GONE);
    }

    public void setShowFav(boolean show) {
        if (show)
            iv_fav.setVisibility(VISIBLE);
        else
            iv_fav.setVisibility(GONE);
    }

    public void setShowBrow(boolean show) {
        if (show) {
            iv_brow.setVisibility(VISIBLE);
        } else
            iv_brow.setVisibility(GONE);
    }

    public void initBrow(List<BrowEntity> list) {
        if (iv_brow.getVisibility() == GONE)
            throw new ExceptionInInitializerError("need invoke setShowBrow(true) Function！");
        FragmentManager fm = null;
        if (getContext() instanceof FragmentActivity) {
            FragmentActivity activity = (FragmentActivity) getContext();
            fm = activity.getSupportFragmentManager();
        }
        if (fm == null)
            throw new ExceptionInInitializerError("need invoke setFragmentManager Function！");
        if (browLayout == null) {
            browLayout = View.inflate(getContext(), R.layout.fragment_brow, null);
            vpBrow = (BrowViewPager) browLayout.findViewById(R.id.vp_brow);
            vpBrow.setAdapter(new BrowPagerAdapter(fm, list, et_input));
            vpBrow.setCurrentItem(BrowType.QQ.getVal());//默认选中qq的表情面板
            browLayout.findViewById(R.id.qq_brow_panel).setOnClickListener(this);
            browLayout.findViewById(R.id.e_brow_panel).setOnClickListener(this);
            browLayout.findViewById(R.id.other_brow_panel).setOnClickListener(this);
            browLayout.findViewById(R.id.btn_del).setOnClickListener(this);
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {//软键盘全部消失后，在显示表情区
                @Override
                public void onGlobalLayout() {
                    if (softKeyboardTagBottom <= 0)
                        softKeyboardTagBottom = getDecorViewBottom();//记录屏幕高度初值
                    if (softKeyboardTagBottom != getDecorViewBottom() && commonPanelVISIBLE())//防止选中文本后的选区操作会同时弹出软键盘和表情面板
                        commonPanelClose();
                    else if (softKeyboardHide && softKeyboardTagBottom == getDecorViewBottom()) {//防止表情面板弹出时被瞬间挤到软键盘以上，一闪
                        commonPanelOpen();
                        softKeyboardHide = false;
                    }
                }
            });
        }
        commonPanel.removeAllViews();
        commonPanel.addView(browLayout);
    }

    private void init() {
        View root = View.inflate(getContext(), R.layout.comment_ctrl_view, this);
        et_input = (EditText) root.findViewById(R.id.et_input);
        if (getVisibility() == VISIBLE)//当前控件如果已经显示，显示之后EditText强制获得光标
            et_input.requestFocus();
        iv_fav = (ImageView) root.findViewById(R.id.iv_fav);
        iv_fav.setOnClickListener(this);
        iv_share = (ImageView) root.findViewById(R.id.iv_share);
        iv_share.setOnClickListener(this);
        iv_brow = (ImageView) root.findViewById(R.id.iv_brow);
        iv_brow.setOnClickListener(this);
        iv_send = (ImageView) root.findViewById(R.id.iv_send);
        iv_send.setOnClickListener(this);
        commonPanel = (FrameLayout) root.findViewById(R.id.bottom);
        setShowShare(showShare);
        setShowFav(showFav);
        setShowBrow(showBrow);
        et_input.setOnClickListener(this);
        et_input.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v.getId() == R.id.et_input && hasFocus)//输入框获取焦点时隐藏
                    commonPanelClose();
            }
        });
        et_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND && cl != null) {//点击发送按钮
                    cl.sendCommentMsg(et_input, getParamObj());
                    Utils.hideSoftKeyboard(et_input);
                    return true;
                }
                return false;
            }
        });
        et_input.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && cl != null)//删除按钮监听
                    cl.keyCodeDelete(et_input, getParamObj());
                return false;
            }
        });
    }

    private int getDecorViewBottom() {
        if (r == null) r = new Rect();
        getWindowVisibleDisplayFrame(r);//可视区域测量
        return r.bottom;//返回可见区域高度，DecorView的高度
    }

    @Override
    public void onClick(View v) {//无法使用switch，lib目录下的R资源没有被final修饰
        int id = v.getId();
        if (id == R.id.et_input)
            commonPanelClose();
        else if (id == R.id.iv_brow)
            commonPanelToggle();
        else if (id == R.id.iv_share) {
            if (cl != null) cl.share();
        } else if (id == R.id.iv_fav) {
            if (cl != null) cl.collection();
        } else if (id == R.id.qq_brow_panel)
            vpBrow.setCurrentItem(BrowType.QQ.getVal());
        else if (id == R.id.e_brow_panel)
            vpBrow.setCurrentItem(BrowType.E.getVal());
        else if (id == R.id.other_brow_panel)
            vpBrow.setCurrentItem(BrowType.Other.getVal());
        else if (id == R.id.btn_del)
            inputDelKey();
        else if (id == R.id.iv_send) {
            if (cl == null) return;
            cl.sendCommentMsg(et_input, getParamObj());
            Utils.hideSoftKeyboard(et_input);
            commonPanelClose();
        }
    }

    //相当于点击软键盘的退格键
    private void inputDelKey() {
        KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
        et_input.dispatchKeyEvent(event);
    }

    private void commonPanelToggle() {
        if (!commonPanelVISIBLE()) {
            if (getDecorViewBottom() == softKeyboardTagBottom)
                commonPanelOpen();
            else {
                Utils.hideSoftKeyboard(et_input);
                softKeyboardHide = true;
            }
        } else if (commonPanelVISIBLE())
            commonPanelClose();
    }

    private boolean commonPanelVISIBLE() {
        return commonPanel.getVisibility() == VISIBLE;
    }


    private void commonPanelOpen() {
        commonPanel.setVisibility(VISIBLE);
    }

    private void commonPanelClose() {
        commonPanel.setVisibility(GONE);
    }

    private Input getParamObj() {
        return paramObj != null ? paramObj : null;
    }

    public interface CommentAreaCtrlListener<Input> {
        void sendCommentMsg(EditText editText, Input param);

        void keyCodeDelete(EditText editText, Input param);

        void share();

        void collection();
    }

    @Override
    protected void onDetachedFromWindow() {
        et_input = null;
        iv_fav = null;
        iv_share = null;
        iv_brow = null;
        iv_send = null;
        cl = null;
        paramObj = null;
        commonPanel = null;
        browLayout = null;
        vpBrow = null;
        r = null;
        super.onDetachedFromWindow();
    }
}
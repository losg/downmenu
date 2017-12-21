package com.losg.downmenu;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by losg on 2016/9/19.
 */
public class DownMenuView extends RelativeLayout implements View.OnClickListener, BaSingleMenuContentAdapter.SingleItemClick, BaMulitMenuContentAdapter.MulitItemClick {

    //标题
    private LinearLayout                               mTitleLinear;
    //标题ID
    private int                                        mTitleID;
    //标题Adapter
    private DownMenuTitleAdapter                       mDownMenuTitleAdapter;
    //之前点击的view
    private View                                       mBeforePressView;
    //下拉菜单容器
    private FrameLayout                                mMenuListContanter;
    //下划线
    private TextView                                   mLineView;
    //下划线ID
    private int                                        mLineID;
    //单行点击事件
    private BaSingleMenuContentAdapter.SingleItemClick mSingleItemClick;
    //多行点击事件
    private BaMulitMenuContentAdapter.MulitItemClick   mMulitItemClick;

    private List<String> mOlderTitle;

    //默认下划线颜色
    private int mLineColor    = 0xffefefef;
    private int mLineResource = 0;
    private int mLineHeight   = 0;

    private boolean beforeVisiable       = false;
    private int     mCurrentShowPosition = 0;
    private boolean mIsClosing           = false;
    private boolean mContentChange       = false;

    private AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    public DownMenuView(Context context) {
        this(context, null);
    }

    public DownMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DownMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        context.obtainStyledAttributes(attrs, R.s)
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DownMenuView);
        mLineColor = typedArray.getColor(R.styleable.DownMenuView_DownMenuView_line_color, mLineColor);
        mLineResource = typedArray.getResourceId(R.styleable.DownMenuView_DownMenuView_line_resource, 0);
        mLineHeight = (int) typedArray.getDimension(R.styleable.DownMenuView_DownMenuView_line_height, 0);
        typedArray.recycle();
        initTitle();
    }

    //添加标题
    private void initTitle() {
        mOlderTitle = new ArrayList<>();
        mTitleLinear = new LinearLayout(getContext());
        mTitleLinear.setOrientation(LinearLayout.HORIZONTAL);
        mTitleID = getID();
        mTitleLinear.setId(mTitleID);
        addView(mTitleLinear, new LayoutParams(-1, -2));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() == 2) {
            initChild();
        } else {
            Log.e("losg_log", "downMenu xml must only one child");
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    //初始化子View
    private void initChild() {
        View contentView = getChildAt(1);
        LayoutParams layoutParams = (LayoutParams) contentView.getLayoutParams();
        layoutParams.addRule(RelativeLayout.BELOW, mTitleID);

        //下拉菜单容器
        mMenuListContanter = new FrameLayout(getContext());
        mMenuListContanter.setBackgroundColor(0x55000000);
        mMenuListContanter.setVisibility(GONE);
        LayoutParams menuParams = new LayoutParams(-1, -1);
        menuParams.addRule(RelativeLayout.BELOW, mTitleID);
        addView(mMenuListContanter, menuParams);
        mMenuListContanter.setOnClickListener(this);

        mLineView = new TextView(getContext());
        LayoutParams params = null;
        if (mLineResource != 0) {
            mLineView.setBackgroundResource(mLineResource);
            params = new LayoutParams(-1, mLineHeight);
        } else {
            mLineView.setBackgroundColor(mLineColor);
            params = new LayoutParams(-1, mLineHeight);
        }
        mLineID = getID();
        mLineView.setId(mLineID);
        params.addRule(RelativeLayout.BELOW, mTitleID);
        addView(mLineView, params);

    }

    public void setDownMenuTitleAdapter(DownMenuTitleAdapter downMenuTitleAdapter) {
        mDownMenuTitleAdapter = downMenuTitleAdapter;
        if (mOlderTitle.size() == 0) {
            mOlderTitle.addAll(downMenuTitleAdapter.getTitles());
        }
        if (mDownMenuTitleAdapter != null) {
            mTitleLinear.removeAllViews();
            for (int i = 0; i < mDownMenuTitleAdapter.getItemCount(); i++) {
                View view = mDownMenuTitleAdapter.getView(i);
                view.setTag(i);
                view.setOnClickListener(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -2);
                params.weight = 1;
                mTitleLinear.addView(view, params);
            }
        }
    }

    public void notifyTitleDataChange() {
        setDownMenuTitleAdapter(mDownMenuTitleAdapter);
    }

    //生成view的id
    private int getID() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1;
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    //处理标题的点击
    @Override
    public void onClick(View v) {
        if (mIsClosing) return;
        if (v == mMenuListContanter) {
            closeDownMenu();
            return;
        }
        int position = (int) v.getTag();
        showDownMenu(position);
    }

    public void showDownMenu(int position) {
        View pressView = mTitleLinear.getChildAt(position);
        if (mBeforePressView == null) {
            mDownMenuTitleAdapter.viewSelected(pressView);
            mBeforePressView = pressView;
        } else if (mBeforePressView == pressView) {
            mDownMenuTitleAdapter.viewDisSelected(mBeforePressView);
            mBeforePressView = null;
            closeDownMenu();
            return;
        } else {
            mDownMenuTitleAdapter.viewDisSelected(mBeforePressView);
            mDownMenuTitleAdapter.viewSelected(pressView);
            mBeforePressView = pressView;
        }

        mCurrentShowPosition = position;
        mMenuListContanter.removeAllViews();
        mMenuListContanter.setVisibility(VISIBLE);
        BaDownMenuContentAdapter downMenuContentAdapter = mDownMenuTitleAdapter.getDownMenuContentAdapter(position);
        View contentView = null;
        int height = 0;
        if (downMenuContentAdapter != null) {
            downMenuContentAdapter.setPosition(position);
            if (downMenuContentAdapter instanceof BaSingleMenuContentAdapter) {
                ((BaSingleMenuContentAdapter) downMenuContentAdapter).setSingleItemClick(this);
                contentView = downMenuContentAdapter.getRootView();
                contentView.measure(0, 0);
                height = contentView.getMeasuredHeight() > getMeasuredHeight() / 2 ? getMeasuredHeight() / 2 : contentView.getMeasuredHeight();
            } else if (downMenuContentAdapter instanceof BaMulitMenuContentAdapter) {
                ((BaMulitMenuContentAdapter) downMenuContentAdapter).setMulitItemClick(this);
                contentView = downMenuContentAdapter.getRootView();
                View leftContent = ((LinearLayout) contentView).getChildAt(0);
                leftContent.measure(0, 0);
                height = leftContent.getMeasuredHeight() > getMeasuredHeight() / 2 ? getMeasuredHeight() / 2 : leftContent.getMeasuredHeight();
            } else {
                contentView = downMenuContentAdapter.getRootView();
                contentView.measure(0, 0);
                height = contentView.getMeasuredHeight() > getMeasuredHeight() / 2 ? getMeasuredHeight() / 2 : contentView.getMeasuredHeight();
            }
        }
        contentView.setClickable(true);
        mMenuListContanter.addView(contentView, new FrameLayout.LayoutParams(-1, height));
        if (!beforeVisiable) {
            showAnimation(contentView, height);
        }
        beforeVisiable = true;
    }

    private void showAnimation(View contentView, int height) {
        ObjectAnimator.ofFloat(contentView, "translationY", -height, 0).setDuration(300).start();

    }

    //关闭下拉菜单
    public void closeDownMenu() {
        if (mIsClosing) return;
        if (mMenuListContanter.getVisibility() == GONE) return;

        if (mDownMenuTitleAdapter != null && mBeforePressView != null) {
            mDownMenuTitleAdapter.viewDisSelected(mBeforePressView);
            mBeforePressView = null;
        }
        beforeVisiable = false;
        mIsClosing = true;
        final BaDownMenuContentAdapter downMenuContentAdapter = mDownMenuTitleAdapter.getDownMenuContentAdapter(mCurrentShowPosition);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(downMenuContentAdapter.getRootView(), "translationY", 0, -downMenuContentAdapter.getRootView().getMeasuredHeight());
        objectAnimator.setDuration(300);
        objectAnimator.start();
        objectAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mMenuListContanter.setVisibility(GONE);
                downMenuContentAdapter.getRootView().setTranslationY(0);
                mIsClosing = false;
                if (mContentChange) {
                    if (mDownMenuTitleAdapter != null) mDownMenuTitleAdapter.notifyContentChange();
                    mContentChange = false;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void notifyMenuContentChange() {
        mContentChange = true;
        if (mMenuListContanter.getVisibility() == GONE) {
            if (mDownMenuTitleAdapter != null) {
                mDownMenuTitleAdapter.getTitles().clear();
                mDownMenuTitleAdapter.getTitles().addAll(mOlderTitle);
                mDownMenuTitleAdapter.notifyContentChange();
                notifyTitleDataChange();
                mContentChange = false;
            }
        } else {
            closeDownMenu();
        }
    }

    @Override
    public void singleItemClick(int titleIndex, int itemIndex, String name) {
        if (mSingleItemClick != null) {
            mSingleItemClick.singleItemClick(titleIndex, itemIndex, name);
        }
        mDownMenuTitleAdapter.getTitles().remove(titleIndex);
        mDownMenuTitleAdapter.getTitles().add(titleIndex, name);
        notifyTitleDataChange();
        closeDownMenu();
    }

    public void setSingleItemClick(BaSingleMenuContentAdapter.SingleItemClick singleItemClick) {
        mSingleItemClick = singleItemClick;
    }

    public void setMulitItemClick(BaMulitMenuContentAdapter.MulitItemClick mulitItemClick) {
        mMulitItemClick = mulitItemClick;
    }

    @Override
    public void mulitItemClick(int titleIndex, int leftIndex, int rightIndex, String name) {
        if (mIsClosing) return;
        if (mMulitItemClick != null) {
            mMulitItemClick.mulitItemClick(titleIndex, leftIndex, rightIndex, name);
        }
        mDownMenuTitleAdapter.getTitles().remove(titleIndex);
        mDownMenuTitleAdapter.getTitles().add(titleIndex, name);
        notifyTitleDataChange();
        closeDownMenu();
    }


}

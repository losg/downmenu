package com.losg.downmenu;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by losg on 2016/9/21.
 */
//双列
public abstract class BaMulitMenuContentAdapter<T, K> extends BaDownMenuContentAdapter implements View.OnClickListener {

    private MulitItemClick mMulitItemClick;
    private int mLeftWeight  = 1;
    private int mRightWeight = 2;
    private DownRightAdapter mDownRightAdapter;
    private LinearLayout     mLeftLayer;

    protected List<T> mTList;

    public BaMulitMenuContentAdapter(Context context, List<T> TList) {
        super(context);
        mTList = TList;
    }

    @Override
    protected View createContentView() {
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setBackgroundColor(getBackGroundColor());

        ScrollView leftView = new ScrollView(mContext);
        leftView.setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
        mLeftLayer = new LinearLayout(mContext);
        mLeftLayer.setOrientation(LinearLayout.VERTICAL);
        mLeftLayer.setLayoutParams(new ScrollView.LayoutParams(-1, -2));
        leftView.addView(mLeftLayer);
        for (int i = 0; i < getLeftItemCount(); i++) {
            View leftChild = createLeftView(getItemName(mTList.get(i)));
            leftChild.setTag(i);
            leftChild.setOnClickListener(this);
            mLeftLayer.addView(leftChild, new LinearLayout.LayoutParams(-1, -2));
            if (mIsFirstSelected && i == 0) {
                leftViewSelected(leftChild);
            }
        }

        LinearLayout.LayoutParams leftParams = new LinearLayout.LayoutParams(0, -2);
        leftParams.weight = mLeftWeight;
        linearLayout.addView(leftView, leftParams);

        RecyclerView recyclerView = new RecyclerView(mContext);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
        mDownRightAdapter = new DownRightAdapter(this);
        if (mIsFirstSelected) {
            mDownRightAdapter.setRightSelecedIndex(0);
        }
        mDownRightAdapter.setMulitItemClick(mMulitItemClick);
        recyclerView.setAdapter(mDownRightAdapter);

        LinearLayout.LayoutParams rightParams = new LinearLayout.LayoutParams(0, -1);
        rightParams.weight = mRightWeight;
        linearLayout.addView(recyclerView, rightParams);

        return linearLayout;
    }

    protected void setWight(int leftWight, int rightWight) {
        this.mLeftWeight = leftWight;
        this.mRightWeight = rightWight;
    }

    protected int getBackGroundColor() {
        return 0xffefefef;
    }

    protected int getLeftItemCount(){
        return mTList.size();
    }

    protected abstract View createLeftView(String name);

    protected int getRightItemCount(int leftIndex){
        T t = mTList.get(leftIndex);
        List<K> childItems = getChildItems(t);
        return childItems.size();
    }

    protected abstract void leftViewSelected(View view);

    protected abstract void leftViewUnSelected(View view);

    protected abstract View createRightItemView();

    protected abstract void rightViewSelected(View view);

    protected abstract void rightViewUnSelected(View view);

    protected void initRightView(View itemView, final int leftIndex, final int position){
        T t = mTList.get(leftIndex);
        List<K> childItems = getChildItems(t);
        final String itemName = getItemName(childItems.get(position));
        initRightView(itemView, itemName);
        itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mMulitItemClick != null) {
                    mMulitItemClick.mulitItemClick(mPosition, leftIndex, position, itemName);
                    mDownRightAdapter.setLeftSelectedIndex(leftIndex);
                    mDownRightAdapter.setRightSelecedIndex(position);
                }
            }
        });
    }

    protected abstract void initRightView(View itemView, String name);

    @Override
    public void onClick(View v) {
        mDownRightAdapter.setCurrentLeftIndex((int) v.getTag());
        mDownRightAdapter.setMulitItemClick(mMulitItemClick);
        for (int i = 0; i < mLeftLayer.getChildCount(); i++) {
            leftViewUnSelected(mLeftLayer.getChildAt(i));
        }
        leftViewSelected(v);
        if (getRightItemCount((int) v.getTag()) == 0) {
            mDownRightAdapter.setRightSelecedIndex(-1);
            if (mMulitItemClick != null) {
                mMulitItemClick.mulitItemClick(mPosition, (int) v.getTag(), -1, getItemName(mTList.get((int) v.getTag())));
            }
            return;
        }
        mDownRightAdapter.notifyDataSetChanged();
    }

    @Override
    protected View getRootView() {
        if (mDownRightAdapter != null) {
            mDownRightAdapter.notifyDataSetChanged();
        }
        return super.getRootView();
    }

    //多行菜单点击事件
    public interface MulitItemClick {

        //点击标题的位置，左边列的位置，右边列的位置
        void mulitItemClick(int titleIndex, int leftIndex, int rightIndex, String name);
    }

    public void setMulitItemClick(MulitItemClick mulitItemClick) {
        mMulitItemClick = mulitItemClick;
    }

    private List<K> getChildItems(T t) {
        Class<?> aClass = t.getClass();
        List<K> mList = new ArrayList<>();
        Field[] declaredFields = aClass.getDeclaredFields();
        if (declaredFields != null) {
            for (Field field : declaredFields) {
                Annotation[] declaredAnnotations = field.getDeclaredAnnotations();
                if (declaredAnnotations != null && declaredAnnotations.length != 0) {
                    for (Annotation annotation : declaredAnnotations) {
                        if (annotation instanceof MenuChildItem) {
                            field.setAccessible(true);
                            try {
                                mList = (ArrayList<K>) field.get(t);
                            } catch (Exception e) {
                                e.printStackTrace();

                            }
                        }
                    }
                }
            }
        }
        return mList;
    }

}

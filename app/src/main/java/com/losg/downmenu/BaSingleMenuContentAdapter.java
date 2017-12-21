package com.losg.downmenu;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.List;

/**
 * Created by losg on 2016/9/21.
 */
//单行
public abstract class BaSingleMenuContentAdapter <T> extends BaDownMenuContentAdapter implements View.OnClickListener {

    private SingleItemClick mSingleItemClick;
    protected List<T>         mList;

    public BaSingleMenuContentAdapter(Context context, List<T> list) {
        super(context);
        mList = list;
    }

    @Override
    protected View createContentView() {
        ScrollView scrollView = new ScrollView(mContext);
        scrollView.setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(Color.WHITE);
        for (int i = 0; i < getItemCount(); i++) {
            View view = createView(getItemName(mList.get(i)));
            view.setTag(i);
            view.setOnClickListener(this);
            linearLayout.addView(view, new LinearLayout.LayoutParams(-1, -2));
            if (mIsFirstSelected && i == 0) {
                itemSelected(view);
            }
        }
        scrollView.addView(linearLayout, new ScrollView.LayoutParams(-1, -1));
        return scrollView;
    }

    protected int getItemCount() {
        return mList.size();
    }

    protected abstract View createView(String name);

    @Override
    public void onClick(View v) {
        LinearLayout parent = (LinearLayout) v.getParent();

        for (int i = 0; i < parent.getChildCount(); i++) {
            itemUnSelected(parent.getChildAt(i));
        }

        if (mSingleItemClick != null) {
            mSingleItemClick.singleItemClick(mPosition, (int) v.getTag(), getItemName(mList.get((int) v.getTag())));
        }

        itemSelected(v);
    }

    protected abstract void itemSelected(View view);

    protected abstract void itemUnSelected(View view);

    protected void setSingleItemClick(SingleItemClick singleItemClick) {
        mSingleItemClick = singleItemClick;
    }


    //单行菜单点击事件
    public interface SingleItemClick {
        //点击标题的位置, 点击的位置
        void singleItemClick(int titleIndex, int itemIndex, String name);
    }

}
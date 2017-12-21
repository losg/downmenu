package com.losg.downmenu;

import android.content.Context;
import android.view.View;

import java.util.HashMap;
import java.util.List;

/**
 * Created by losg on 2016/9/21.
 */
public abstract class DownMenuTitleAdapter {

    protected Context                                    mContext;
    private   HashMap<Integer, BaDownMenuContentAdapter> mMenuContents;
    private   List<String>                               mTitles;

    public DownMenuTitleAdapter(Context context, List<String> titles) {
        mContext = context;
        mTitles = titles;
        mMenuContents = new HashMap<>();
    }

    //标题的数量
    public int getItemCount(){
        return mTitles.size();
    }

    //标题的View
    public View getView(int position){
        return  getView(mTitles.get(position), position);
    }

    protected abstract View getView(String name, int position);

    //view被选中
    protected abstract void viewSelected(View view);

    //view取消选中
    protected abstract void viewDisSelected(View view);

    //创建内容的adpter(可自定义，实现 BaDownMenuContentAdapter 方法即可)
    protected abstract BaDownMenuContentAdapter createDownMenuContentAdapter(int position);

    protected void notifyContentChange(){
        mMenuContents.clear();
    }

    protected BaDownMenuContentAdapter getDownMenuContentAdapter(int position) {
        if (mMenuContents.get(position) == null) {
            mMenuContents.put(position, createDownMenuContentAdapter(position));
        }
        return mMenuContents.get(position);
    }

    protected List<String> getTitles(){
        return mTitles;
    }

}


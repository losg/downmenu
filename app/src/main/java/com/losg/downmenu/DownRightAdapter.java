package com.losg.downmenu;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by losg on 2016/9/21.
 */
public class DownRightAdapter extends RecyclerView.Adapter<DownRightAdapter.ViewHolder> {

    private BaMulitMenuContentAdapter                mMulitMenuContentAdapter;
    private BaMulitMenuContentAdapter.MulitItemClick mMulitItemClick;
    private int mLeftSelectedIndex = 0;
    private int mRightSelecedIndex = -1;

    private int mCurrentLeftIndex = 0;

    public DownRightAdapter(BaMulitMenuContentAdapter mulitMenuContentAdapter) {
        mMulitMenuContentAdapter = mulitMenuContentAdapter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View contentView = mMulitMenuContentAdapter.createRightItemView();
        contentView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        contentView.setClickable(true);
        return new ViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mCurrentLeftIndex == mLeftSelectedIndex && position == mRightSelecedIndex) {
            mMulitMenuContentAdapter.rightViewSelected(holder.itemView);
        } else {
            mMulitMenuContentAdapter.rightViewUnSelected(holder.itemView);
        }
        mMulitMenuContentAdapter.initRightView(holder.itemView, mCurrentLeftIndex, position);
    }

    @Override
    public int getItemCount() {
        int count = 0;
        try {
            count = mMulitMenuContentAdapter.getRightItemCount(mCurrentLeftIndex);
        } catch (Exception e) {

        }
        return count;
    }

    public void setCurrentLeftIndex(int currentLeftIndex) {
        mCurrentLeftIndex = currentLeftIndex;
    }

    public void setLeftSelectedIndex(int leftSelectedIndex) {
        mLeftSelectedIndex = leftSelectedIndex;
    }

    public void setRightSelecedIndex(int rightSelecedIndex) {
        mRightSelecedIndex = rightSelecedIndex;
    }

    public void setMulitItemClick(BaMulitMenuContentAdapter.MulitItemClick mulitItemClick) {
        mMulitItemClick = mulitItemClick;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

}

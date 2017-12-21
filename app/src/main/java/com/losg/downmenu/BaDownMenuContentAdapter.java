package com.losg.downmenu;

import android.content.Context;
import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Created by losg on 2016/9/21.
 */
public abstract class BaDownMenuContentAdapter {

    protected View    mRootView;
    protected int     mPosition;
    protected Context mContext;
    protected boolean mIsFirstSelected = false;

    public BaDownMenuContentAdapter(Context context) {
        mContext = context;
    }

    protected abstract View createContentView();

    protected View getRootView() {
        if (mRootView == null) {
            mRootView = createContentView();
        }
        return mRootView;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public void setFirstSelected(boolean firstSelected) {
        mIsFirstSelected = firstSelected;
    }

    protected  <T> String getItemName(T t) {
        Class<?> aClass = t.getClass();
        if (aClass.getName().equals(String.class.getName())) {
            return (String) t;
        }
        Field[] declaredFields = aClass.getDeclaredFields();
        if (declaredFields != null) {
            for (Field field : declaredFields) {
                Annotation[] declaredAnnotations = field.getDeclaredAnnotations();
                if (declaredAnnotations != null && declaredAnnotations.length != 0) {
                    for (Annotation annotation : declaredAnnotations) {
                        if (annotation instanceof MenuItemName) {
                            field.setAccessible(true);
                            try {
                                return (String) field.get(t);
                            } catch (Exception e) {
                                e.printStackTrace();
                                return "";
                            }
                        }
                    }
                }
            }
        }
        return "";
    }
}
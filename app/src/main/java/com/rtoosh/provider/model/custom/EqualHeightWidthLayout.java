package com.rtoosh.provider.model.custom;


import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class EqualHeightWidthLayout extends LinearLayout {
    public EqualHeightWidthLayout(Context context) {
        super(context);
    }

    public EqualHeightWidthLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EqualHeightWidthLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int h = this.getMeasuredHeight();
        int w = this.getMeasuredWidth();
        int r = Math.max(w,h);

        setMeasuredDimension(r, r);

    }
}

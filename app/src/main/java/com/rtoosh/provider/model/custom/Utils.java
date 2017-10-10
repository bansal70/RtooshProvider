package com.rtoosh.provider.model.custom;

/*
 * Created by win 10 on 10/6/2017.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.LinearLayout;

import com.rtoosh.provider.R;

public class Utils {

    @SuppressWarnings("ConstantConditions")
    public static Dialog createDialog(Context context, int layout) {
        ColorDrawable dialogColor = new ColorDrawable(Color.BLACK);
        dialogColor.setAlpha(0); //(0-255) 0 means fully transparent, and 255 means fully opaque
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(dialogColor);

        return dialog;
    }

    public static void gotoPreviousActivityAnimation(Context mContext) {
        ((Activity)mContext).overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    public static void gotoNextActivityAnimation(Context mContext) {
        ((Activity)mContext).overridePendingTransition(R.anim.slide_in_from_right,R.anim.slide_out_to_left);
    }
}

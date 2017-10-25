package com.rtoosh.provider.model.custom;

/*
 * Created by win 10 on 10/6/2017.
 */

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.rtoosh.provider.R;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static boolean emailValidator(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void maxDatePicker(Context mContext, final TextView textView) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        // date picker dialog
        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {
                // set day of month , month and year value in the edit text
                String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                textView.setText(date);

            }};

        DatePickerDialog dpDialog = new DatePickerDialog(mContext, onDateSetListener, mYear, mMonth, mDay);
        DatePicker datePicker = dpDialog.getDatePicker();
        datePicker.setMaxDate(c.getTimeInMillis());
        dpDialog.show();
    }

    public static void changeRatingBarColor(Context mContext, RatingBar ratingBar, int filledColor, int halfFilledColor, int emptyStarColor) {
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(ContextCompat.getColor(mContext,
                filledColor), PorterDuff.Mode.SRC_ATOP); // for filled stars
        stars.getDrawable(1).setColorFilter(ContextCompat.getColor(mContext,
                halfFilledColor), PorterDuff.Mode.SRC_ATOP); // for half filled stars
        stars.getDrawable(0).setColorFilter(ContextCompat.getColor(mContext,
                emptyStarColor), PorterDuff.Mode.SRC_ATOP); // for empty stars
    }

    public static void setTextWatcherMoveFocus(EditText editText, final EditText editNext) {
        editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String str = s.toString();
                    if (str.length() == 1) {
                        editNext.requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
    }

    public static void hideKeyboard(Context mContext, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

package com.rtoosh.provider.views;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.rtoosh.provider.R;

public class LanguageDialog extends DialogFragment implements DialogInterface.OnClickListener{
    static final String TAG = "SelectorDialog";

    static int mLanguageArray;
    static int mSelectedIndex;
    static OnDialogSelectorListener mDialogSelectorCallback;

    public interface OnDialogSelectorListener {
        public void onSelectedOption(int dialogId);
    }

    public static LanguageDialog newInstance(int language, int selected) {

        LanguageDialog dialog = new LanguageDialog();
        mLanguageArray = language;
        mSelectedIndex = selected;

        return dialog;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());

        builder.setTitle(R.string.language);
        builder.setSingleChoiceItems(mLanguageArray, mSelectedIndex, this);
        builder.setPositiveButton(android.R.string.ok, this);
        builder.setNegativeButton(android.R.string.cancel, this);
        return builder.create();
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case Dialog.BUTTON_NEGATIVE:
                dialog.cancel();
                break;

            case Dialog.BUTTON_POSITIVE:
                dialog.dismiss();
                // message selected value to registered callbacks
                mDialogSelectorCallback.onSelectedOption(mSelectedIndex);
                break;

            default: // choice selected click
                mSelectedIndex = which;
                break;
        }

    }

    public void setDialogSelectorListener (OnDialogSelectorListener listener) {
        mDialogSelectorCallback = listener;
    }
}

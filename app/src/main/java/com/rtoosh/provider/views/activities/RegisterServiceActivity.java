package com.rtoosh.provider.views.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.custom.Utils;

public class RegisterServiceActivity extends AppCompatActivity {

    private Dialog dialogSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_service);

        initSelectionDialog();
    }

    private void initSelectionDialog() {
        dialogSelection = Utils.createDialog(this, R.layout.dialog_add_services);
        dialogSelection.findViewById(R.id.tvDoneSelection)
                .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSelection.dismiss();
                findViewById(R.id.cardServices).setVisibility(View.VISIBLE);
            }
        });

    }

    public void nailsClick(View view) {
        dialogSelection.show();
    }

    public void makeupClick(View view) {
        dialogSelection.show();
    }

    public void hairClick(View view) {
        dialogSelection.show();
    }

    public void nextService(View v) {
        startActivity(new Intent(this, RegisterInfoActivity.class));
        Utils.gotoNextActivityAnimation(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

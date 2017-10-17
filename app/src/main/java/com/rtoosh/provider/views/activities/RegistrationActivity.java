package com.rtoosh.provider.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.custom.Utils;

public class RegistrationActivity extends AppBaseActivity {

    EditText editName, editSelect, editEmail, editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initViews();
    }

    private void initViews() {
        editName = (EditText) findViewById(R.id.editName);
        editSelect = (EditText) findViewById(R.id.editSelect);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
    }

    public void registerDone(View v) {
        String name = editName.getText().toString().trim();
        String select = editSelect.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString();

        if (name.isEmpty() || select.isEmpty() || email.isEmpty() || password.isEmpty())
            Toast.makeText(mContext, R.string.toast_fill_data, Toast.LENGTH_SHORT).show();
        else if (!Utils.emailValidator(email))
            Toast.makeText(mContext, R.string.toast_invalid_email, Toast.LENGTH_SHORT).show();
        else if (password.length() < 6)
            Toast.makeText(mContext, R.string.toast_invalid_password_length, Toast.LENGTH_SHORT).show();
        else {
            startActivity(new Intent(this, RegisterIDActivity.class));
            Utils.gotoNextActivityAnimation(this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

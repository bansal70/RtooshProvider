package com.rtoosh.provider.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.custom.Utils;

import java.util.ArrayList;
import java.util.List;

public class RegisterIDActivity extends AppBaseActivity {

    Spinner spinnerIDType;
    List<String> listIdType;
    TextView tvIssueDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_id);

        initViews();
        initID();
    }

    private void initViews() {
        spinnerIDType = (Spinner) findViewById(R.id.spinnerIDType);
        tvIssueDate = (TextView) findViewById(R.id.tvIssueDate);
    }

    private void initID() {
        listIdType = new ArrayList<>();
        listIdType.add("ID Type");
        listIdType.add("Driving Licence");
        listIdType.add("Utility Bill");
        listIdType.add("Voter Card");
        listIdType.add("Electricity Bill");
        listIdType.add("Government ID");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, listIdType);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIDType.setAdapter(arrayAdapter);
    }

    public void issueDate(View view) {
        Utils.maxDatePicker(this, tvIssueDate);
    }

    public void nextID(View v) {
        startActivity(new Intent(this, RegisterOrderActivity.class));
        Utils.gotoNextActivityAnimation(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

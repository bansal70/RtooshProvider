package com.rtoosh.provider.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.Operations;
import com.rtoosh.provider.model.custom.Utils;

import java.util.ArrayList;
import java.util.List;

public class RegisterIDActivity extends AppBaseActivity implements CompoundButton.OnCheckedChangeListener,
        AdapterView.OnItemSelectedListener{

    Spinner spinnerIDType;
    List<String> listIdType;
    TextView tvIssueDate;
    CheckBox cbOnline, cbSchedule;
    EditText editID;
    List<String> listWork;
    String idType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_id);

        initViews();
        initID();
    }

    private void initViews() {
        spinnerIDType = findViewById(R.id.spinnerIDType);
        tvIssueDate = findViewById(R.id.tvIssueDate);
        cbOnline = findViewById(R.id.cbOnline);
        cbSchedule = findViewById(R.id.cbSchedule);
        editID = findViewById(R.id.editID);

        cbOnline.setOnCheckedChangeListener(this);
        cbSchedule.setOnCheckedChangeListener(this);
    }

    private void initID() {
        listWork = new ArrayList<>();
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
        spinnerIDType.setOnItemSelectedListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cbOnline:
                if (isChecked)
                    listWork.add("online");
                else
                    listWork.remove("online");
                break;

            case R.id.cbSchedule:
                if (isChecked)
                    listWork.add("schedule");
                else
                    listWork.remove("schedule");
                break;
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position != 0)
            idType = listIdType.get(position);
        else
            idType = "";
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void issueDate(View view) {
        Utils.maxDatePicker(this, tvIssueDate);
    }

    public void nextID(View v) {
        String id = editID.getText().toString().trim();
        String date = tvIssueDate.getText().toString();
        String registerID = Operations.makeJsonRegisterID(id, idType, date, listWork);

        startActivity(new Intent(this, RegisterOrderActivity.class)
                .putExtra("ID", registerID));
        Utils.gotoNextActivityAnimation(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}

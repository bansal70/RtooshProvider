package com.rtoosh.provider.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rtoosh.provider.R;
import com.rtoosh.provider.controller.ModelManager;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.POJO.register.RegisterID;
import com.rtoosh.provider.model.RPPreferences;
import com.rtoosh.provider.model.custom.ImagePicker;
import com.rtoosh.provider.model.custom.Utils;
import com.rtoosh.provider.model.event.ApiErrorEvent;
import com.rtoosh.provider.model.event.ApiErrorWithMessageEvent;
import com.rtoosh.provider.model.network.AbstractApiResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static android.os.Build.VERSION_CODES.M;

public class RegisterIDActivity extends AppBaseActivity implements CompoundButton.OnCheckedChangeListener,
        AdapterView.OnItemSelectedListener {

    private final String ID_TAG = "RegisterIDActivity";

    @BindView(R.id.tvIssueDate) TextView tvIssueDate;
    @BindView(R.id.cbOnline) CheckBox cbOnline;
    @BindView(R.id.cbSchedule) CheckBox cbSchedule;
    @BindView(R.id.editID) EditText editID;
    @BindView(R.id.spinnerIDType) Spinner spinnerIDType;
    @BindView(R.id.textUpload) TextView textUpload;
    @BindView(R.id.tvPickSchedule) TextView tvPickSchedule;

    private List<String> listIdType;
    private String online = "0", schedule = "0";
    String id, date, idType = "", filePath = "", user_id, lang;
    private static final int PERMISSION_REQUEST_CODE = 1010;
    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private boolean isUploaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_id);
        ButterKnife.bind(this);

        initViews();
        initID();
    }

    private void initViews() {
        lang = RPPreferences.readString(mContext, "lang");
        user_id = RPPreferences.readString(mContext, "user_id");

        cbOnline.setOnCheckedChangeListener(this);
        cbSchedule.setOnCheckedChangeListener(this);
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
        spinnerIDType.setOnItemSelectedListener(this);
    }

    @OnClick(R.id.tvUploadID)
    public void uploadID() {
        dispatchTakePictureIntent();
    }

    @OnClick(R.id.tvPickSchedule)
    public void pickSchedule() {
        startActivity(new Intent(mContext, ScheduleWorkActivity.class));
        Utils.gotoNextActivityAnimation(mContext);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cbOnline:
                if (isChecked)
                    online = "1";
                else
                    online = "0";
                break;

            case R.id.cbSchedule:
                if (isChecked)
                    schedule = "1";
                else
                    schedule = "0";
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

        id = editID.getText().toString().trim();
        date = tvIssueDate.getText().toString();
        if (id.isEmpty() || date.isEmpty() || idType.isEmpty()) {
            showToast(getString(R.string.toast_fill_data));
        } else if (online.equals("0") && schedule.equals("0")) {
            showToast(getString(R.string.error_work_type));
        } else if (!isUploaded) {
            showToast(getString(R.string.error_upload_id));
        } else {
            RegisterID registerID = createIdObject();
            Gson gson = new Gson();
            String json = gson.toJson(registerID);
            Timber.e("register_id fields-- " + json);

            startActivity(new Intent(this, RegisterOrderActivity.class)
                    .putExtra("ID", json));
            Utils.gotoNextActivityAnimation(this);
        }
    }

    private RegisterID createIdObject() {
        RegisterID registerID1 = new RegisterID();
        registerID1.setIdNumber(id);
        registerID1.setIdType(idType);
        registerID1.setIssueDate(date);
        registerID1.setWorkOnline(online);
        registerID1.setWorkSchedule(schedule);

        return registerID1;
    }

    private void dispatchTakePictureIntent() {
        if (Build.VERSION.SDK_INT >= M) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        } else {
            chooseImage();
        }
    }

    public void chooseImage() {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(this);
        startActivityForResult(chooseImageIntent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
            chooseImage();
        } else {
            Toast.makeText(this, R.string.grant_permissions, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap photo = ImagePicker.getImageFromResult(this, resultCode, data);
            Uri tempUri = ImagePicker.getImageUri(this, photo);
            File finalFile = new File(ImagePicker.getRealPathFromURI(this, tempUri));
            filePath = finalFile.getAbsolutePath();

            ModelManager.getInstance().getUploadIDManager()
                    .uploadIDTask(mContext, ID_TAG, user_id, lang, filePath);
            textUpload.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe(sticky = true)
    public void onEvent(AbstractApiResponse apiResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        switch (apiResponse.getRequestTag()) {
            case ID_TAG:
                showToast(apiResponse.getMessage());
                textUpload.setText(R.string.uploaded);
                isUploaded = true;
                break;

            default:
                break;
        }
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(ApiErrorWithMessageEvent event) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        showToast(event.getResultMsgUser());
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(ApiErrorEvent event) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        showToast(Constants.SERVER_ERROR);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}

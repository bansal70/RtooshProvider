package com.rtoosh.provider.views;

import android.Manifest;
import android.content.Intent;
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
import com.myhexaville.smartimagepicker.ImagePicker;
import com.rtoosh.provider.R;
import com.rtoosh.provider.controller.ModelManager;
import com.rtoosh.provider.controller.ProgressRequestBody;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.POJO.register.RegisterID;
import com.rtoosh.provider.model.RPPreferences;
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
import okhttp3.MultipartBody;
import timber.log.Timber;

import static android.os.Build.VERSION_CODES.M;

public class RegisterIDActivity extends AppBaseActivity implements CompoundButton.OnCheckedChangeListener,
        AdapterView.OnItemSelectedListener, ProgressRequestBody.UploadCallbacks {

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
    private boolean isUploaded = false, isUploading = false;
    ImagePicker imagePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_id);
        ButterKnife.bind(this);

        initViews();
        initID();
    }

    private void initViews() {
        lang = RPPreferences.readString(mContext, Constants.LANGUAGE_KEY);
        user_id = RPPreferences.readString(mContext, Constants.USER_ID_KEY);

        cbOnline.setOnCheckedChangeListener(this);
        cbSchedule.setOnCheckedChangeListener(this);
    }

    private void initID() {
        listIdType = new ArrayList<>();
        listIdType.add(getString(R.string.spinner_id_type));
        listIdType.add(getString(R.string.spinner_licence));
        listIdType.add(getString(R.string.spinner_bill));
        listIdType.add(getString(R.string.spinner_voter_card));
        listIdType.add(getString(R.string.spinner_electricity_bill));
        listIdType.add(getString(R.string.spinner_gov_id));

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_item, listIdType);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIDType.setAdapter(arrayAdapter);
        spinnerIDType.setOnItemSelectedListener(this);
    }

    @OnClick(R.id.tvUploadID)
    public void uploadID() {
        if (Build.VERSION.SDK_INT >= M) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        } else {
            pickImage();
        }
        //dispatchTakePictureIntent();
    }

    private void pickImage() {
        imagePicker = new ImagePicker(this, null,
                (Uri imageUri) -> {
                    String path = Utils.getPathFromUri(mContext, imageUri);
                    if (path != null) {
                        File finalFile = new File(path);
                        filePath = finalFile.getAbsolutePath();

                        ProgressRequestBody fileBody = new ProgressRequestBody(finalFile, this);
                        MultipartBody.Part filePart = MultipartBody.Part.createFormData("id_image",
                                finalFile.getName(), fileBody);

                        ModelManager.getInstance().getUploadIDManager()
                                .uploadIDTask(mContext, ID_TAG, user_id, lang, filePart);
                        textUpload.setVisibility(View.VISIBLE);
                        isUploaded = true;
                    }
                });
        imagePicker.choosePicture(true);
    }

    @OnClick(R.id.tvPickSchedule)
    public void pickSchedule() {
        if (schedule.equals("0")) {
            showToast(getString(R.string.text_set_schedule_type));
            return;
        }
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

    @Override
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
        } else if (!isUploading) {
            showToast(getString(R.string.id_upload_in_progress));
        } else if (id.equals("0")) {
            showToast(getString(R.string.error_id_number));
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imagePicker.handleActivityResult(resultCode,requestCode, data);
        /* if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap photo = ImagePicker.getImageFromResult(this, resultCode, data);
            Uri tempUri = ImagePicker.getImageUri(this, photo);
            File finalFile = new File(ImagePicker.getRealPathFromURI(this, tempUri));
            filePath = finalFile.getAbsolutePath();

            ProgressRequestBody fileBody = new ProgressRequestBody(finalFile, this);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("id_image",
                    finalFile.getName(), fileBody);

            ModelManager.getInstance().getUploadIDManager()
                    .uploadIDTask(mContext, ID_TAG, user_id, lang, filePart);
            textUpload.setVisibility(View.VISIBLE);
            isUploaded = true;
        } */
    }

    @Override
    public void onProgressUpdate(int percentage) {
        String percent = getString(R.string.uploading) + " " + percentage + "%";
        textUpload.setText(percent);
    }

    @Override
    public void onError() {
        // do something on error
        Timber.e("error while uploading the image");
        isUploading = false;
    }

    @Override
    public void onFinish() {
        // do something on upload finished
        // for example start next uploading at queue
        isUploading = true;
        textUpload.setText(R.string.uploaded);
    }

    @Subscribe(sticky = true)
    public void onEvent(AbstractApiResponse apiResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        switch (apiResponse.getRequestTag()) {
            case ID_TAG:
                showToast(apiResponse.getMessage());
                textUpload.setText(R.string.uploaded);
                isUploading = true;
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
        showToast(getString(R.string.something_went_wrong));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
            pickImage();
        } else {
            Toast.makeText(this, R.string.grant_permissions, Toast.LENGTH_SHORT).show();
        }
    }

}

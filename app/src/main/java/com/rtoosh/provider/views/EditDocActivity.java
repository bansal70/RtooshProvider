package com.rtoosh.provider.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rtoosh.provider.R;
import com.rtoosh.provider.controller.ModelManager;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.Operations;
import com.rtoosh.provider.model.POJO.ProfileResponse;
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

public class EditDocActivity extends AppBaseActivity implements AdapterView.OnItemSelectedListener{

    private final String PROFILE_TAG = "DOC_INFO";
    private final String UPDATE_ID_PIC = "UPDATE_ID_PIC";
    private final String UPDATE_DOC_INFO = "UPDATE_DOC";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbarTitle) TextView toolbarTitle;
    @BindView(R.id.imgID) ImageView imgID;
    @BindView(R.id.editID) EditText editID;
    @BindView(R.id.spinnerIDType) Spinner spinnerIDType;
    @BindView(R.id.textUpload) TextView textUpload;
    @BindView(R.id.tvIssueDate) TextView tvIssueDate;

    private List<String> listIdType;
    String id, idType = "", filePath = "";

    String user_id, lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_doc);
        ButterKnife.bind(this);

        initViews();
        initID();
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarTitle.setText(getString(R.string.documents));

        lang = RPPreferences.readString(mContext, Constants.LANGUAGE_KEY);
        user_id = RPPreferences.readString(mContext, Constants.USER_ID_KEY);

        showDialog();
        ModelManager.getInstance().getProfileManager().profileTask(mContext, PROFILE_TAG, user_id, lang);
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

    @OnClick(R.id.imgID)
    public void selectImage() {
        dispatchTakePictureIntent();
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

    private void setProfile(ProfileResponse profileResponse) {
        ProfileResponse.Data data = profileResponse.data;
        ProfileResponse.User user = data.user;

        Glide.with(mContext)
                .load(user.idImage)
                .apply(RequestOptions.centerInsideTransform())
                .into(imgID);

        editID.setText(user.idNumber);
        tvIssueDate.setText(user.issueDate);

        for (int i=0; i<listIdType.size(); i++) {
            if (listIdType.get(i).equals(user.idType)) {
                spinnerIDType.setSelection(i);
            }
        }

       // setEnabled(false);
    }

    @OnClick(R.id.tvIssueDate)
    public void issueDate() {
        Utils.maxDatePicker(mContext, tvIssueDate);
    }

   /* private void setEnabled(boolean enabled) {
        editID.setEnabled(enabled);
        tvIssueDate.setEnabled(enabled);
        spinnerIDType.setEnabled(enabled);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_work, menu);
        //menu.getItem(0).setIcon(ContextCompat.getDrawable(mContext, R.drawable.ic_edit_service));
        return super.onCreateOptionsMenu(menu);
    }

    private void updateDoc() {
        String idNumber = editID.getText().toString().trim();
        String issueDate = tvIssueDate.getText().toString();
        if (idNumber.isEmpty() || issueDate.isEmpty() || idType.isEmpty()) {
            showToast(getString(R.string.toast_fill_data));
        } else {
            ModelManager.getInstance().getUploadIDManager().updateDocTask(mContext, UPDATE_DOC_INFO,
                    Operations.updateDocParams(user_id, issueDate, idType, idNumber, lang), filePath);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mItem) {
        switch (mItem.getItemId()) {
            case R.id.menuDone:
                showDialog();
                updateDoc();
                break;
        }
        return super.onOptionsItemSelected(mItem);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap photo = ImagePicker.getImageFromResult(this, resultCode, data);
            Uri tempUri = ImagePicker.getImageUri(this, photo);
            File finalFile = new File(ImagePicker.getRealPathFromURI(this, tempUri));
            filePath = finalFile.getAbsolutePath();

            imgID.setImageBitmap(photo);

            /*Glide.with(mContext)
                    .load(filePath)
                    .apply(RequestOptions.centerInsideTransform())
                    .into(imgID);*/
        }
    }

    @Subscribe(sticky = true)
    public void onEvent(ProfileResponse profileResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        switch (profileResponse.getRequestTag()) {
            case PROFILE_TAG:
                //showToast(profileResponse.getMessage());
                setProfile(profileResponse);
                break;

            default:
                break;
        }
    }

    @Subscribe(sticky = true)
    public void onEvent(AbstractApiResponse apiResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        switch (apiResponse.getRequestTag()) {
            case UPDATE_DOC_INFO:
                showToast(apiResponse.getMessage());
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
}

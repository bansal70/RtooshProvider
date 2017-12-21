package com.rtoosh.provider.views;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.myhexaville.smartimagepicker.ImagePicker;
import com.rtoosh.provider.R;
import com.rtoosh.provider.controller.ModelManager;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.Operations;
import com.rtoosh.provider.model.POJO.ProfileResponse;
import com.rtoosh.provider.model.RPPreferences;
import com.rtoosh.provider.model.custom.Utils;
import com.rtoosh.provider.model.event.ApiErrorEvent;
import com.rtoosh.provider.model.event.ApiErrorWithMessageEvent;
import com.rtoosh.provider.model.event.RequestFinishedEvent;
import com.rtoosh.provider.model.network.AbstractApiResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.os.Build.VERSION_CODES.M;

public class ProfileActivity extends AppBaseActivity implements LanguageDialog.OnDialogSelectorListener{

    private final String PROFILE_TAG = "ProfileActivity";
    private final String UPDATE_PROFILE_TAG = "UpdateProfile";
    private final String UPDATE_PASSWORD_TAG = "UpdatePassword";
    private final String UPDATE_LANGUAGE_TAG = "UPDATE_LANGUAGE";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbarTitle) TextView toolbarTitle;
    @BindView(R.id.tvAccountStatus) TextView tvAccountStatus;
    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.tvPhone) TextView tvPhone;
    @BindView(R.id.tvEmail) TextView tvEmail;
    @BindView(R.id.tvNoCover) TextView tvNoCover;

    @BindView(R.id.layoutChangePic) LinearLayout layoutChangePic;
    @BindView(R.id.editSurname) EditText editSurname;
    @BindView(R.id.editBio) EditText editBio;
    @BindView(R.id.imgWork1) ImageView imgWork1;
    @BindView(R.id.imgWork2) ImageView imgWork2;
    @BindView(R.id.imgViewAll) ImageView imgViewAll;
    @BindView(R.id.imgCover) ImageView imgCover;
    @BindView(R.id.imgProfilePic) ImageView imgProfilePic;
    @BindView(R.id.imgEdit) ImageView imgEdit;
    @BindView(R.id.tvViewAll) TextView tvViewAll;
    @BindView(R.id.layoutProfile) LinearLayout layoutProfile;
    @BindView(R.id.layoutStatus) LinearLayout layoutStatus;

    boolean isEdit = false;

    String lang, user_id, profilePic = "", coverImage = "", imageType = "";
    Dialog dialog;
    ArrayList<ProfileResponse.ProviderImage> imagesList;
    final int REQUEST_CODE = 101;
    ImagePicker imagePicker;

    private int selectedLangId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        initViews();
        //initDialog();
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarTitle.setText(getString(R.string.profile));

        lang = RPPreferences.readString(mContext, Constants.LANGUAGE_KEY);
        user_id = RPPreferences.readString(mContext, Constants.USER_ID_KEY);
        setEnabled(false );

        if (lang.equals("en")) {
            selectedLangId = 0;
        } else {
            selectedLangId = 1;
        }

        //showDialog();
        showProgressBar();
        ModelManager.getInstance().getProfileManager().profileTask(mContext, PROFILE_TAG, user_id, lang);
    }

    public void setEnabled(boolean status) {
        editBio.setEnabled(status);
        editSurname.setEnabled(status);
        layoutChangePic.setClickable(status);
        imgCover.setClickable(status);
    }

    public void setProfile(ProfileResponse profileResponse) {
        imagesList = new ArrayList<>();
        ProfileResponse.Data data = profileResponse.data;
        ProfileResponse.User user = data.user;
        tvName.setText(user.fullName);
        tvPhone.setText(user.phone);
        tvEmail.setText(user.email);

        switch (user.status) {
            case Constants.ACCOUNT_ACTIVE:
                tvAccountStatus.setText(R.string.account_active);
                break;
            case Constants.ACCOUNT_INACTIVE:
                tvAccountStatus.setText(R.string.account_under_review);
                break;
            case Constants.ACCOUNT_REVIEWING:
                tvAccountStatus.setText(R.string.account_under_review);
                break;
            case Constants.ACCOUNT_SUSPENDED:
                tvAccountStatus.setText(R.string.account_suspended);
                break;
        }

        if (user.online.equals(Constants.STATUS_ONLINE))
            RPPreferences.putString(mContext, Constants.USER_STATUS_KEY, Constants.STATUS_ONLINE);
        else
            RPPreferences.putString(mContext, Constants.USER_STATUS_KEY, Constants.STATUS_OFFLINE);

        editSurname.setText(user.surname);
        editBio.setText(user.bio);

        imagesList = data.providerImages;
        for (int i=0; i<imagesList.size(); i++) {
            String images = imagesList.get(i).image;
            if (i == 0)
                Glide.with(mContext).load(images)
                        .apply(RequestOptions.centerInsideTransform().placeholder(R.mipmap.ic_upload_image))
                        .into(imgWork1);
            if (i == 1)
                Glide.with(mContext).load(images)
                        .apply(RequestOptions.centerInsideTransform().placeholder(R.mipmap.ic_upload_image))
                        .into(imgWork2);
        }

        Glide.with(mContext)
                .load(user.coverImage)
                .into(imgCover);

        Glide.with(mContext).load(user.profilePic)
                .apply(RequestOptions.circleCropTransform().placeholder(R.mipmap.ic_user_placeholder))
                .into(imgProfilePic);

        if (imagesList.size() == 0) {
            imgWork1.setVisibility(View.GONE);
            imgWork2.setVisibility(View.GONE);
            tvViewAll.setText(getString(R.string.add_photo));

        } else if (imagesList.size() == 1) {
            imgWork1.setVisibility(View.VISIBLE);
            imgWork2.setVisibility(View.GONE);
            tvViewAll.setText(getString(R.string.view_all));
        } else {
            imgWork1.setVisibility(View.VISIBLE);
            imgWork2.setVisibility(View.VISIBLE);
            tvViewAll.setText(getString(R.string.view_all));
        }

        if (user.coverImage.isEmpty())
            tvNoCover.setVisibility(View.VISIBLE);


        RPPreferences.putString(mContext, Constants.PROFILE_PIC_KEY, user.profilePic);
        RPPreferences.putString(getApplicationContext(), Constants.ACCOUNT_STATUS_KEY, user.status);
    }

    @OnClick(R.id.layoutChangePic)
    public void updateProfilePic() {
        imageType = "profile";
        if (Build.VERSION.SDK_INT >= M) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        } else {
            selectProfilePic();
        }
        // dispatchTakePictureIntent();

    }

    @OnClick(R.id.imgCover)
    public void updateCoverPic() {
        imageType = "work";
        if (Build.VERSION.SDK_INT >= M) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        } else {
            selectCoverPic();
        }
        // dispatchTakePictureIntent();
    }

    private void selectProfilePic() {
        imagePicker = new ImagePicker(this, null,
                (Uri imageUri) -> {
                    String path = Utils.getPathFromUri(mContext, imageUri);
                    if (path != null) {
                        path = Utils.decodeFile(path, 400, 800);
                       // File finalFile = new File(path);
                        Glide.with(mContext).load(imageUri)
                                .apply(RequestOptions.circleCropTransform().placeholder(R.mipmap.ic_user_placeholder))
                                .into(imgProfilePic);
                        profilePic = path;
                    }
                });
        imagePicker.choosePicture(true);
    }

     private void selectCoverPic() {
         imagePicker = new ImagePicker(this, null,
                 (Uri imageUri) -> {
                     tvNoCover.setVisibility(View.GONE);
                     String path = Utils.getPathFromUri(mContext, imageUri);
                     if (path != null) {
                         path = Utils.decodeFile(path, 800, 400);
                         //File finalFile = new File(path);
                         imgCover.setImageURI(imageUri);
                         coverImage = path;
                     }
                 });
         imagePicker.choosePicture(true);
     }

    public void updateProfile() {
        String surname = editSurname.getText().toString().trim();
        String bio = editBio.getText().toString();
        if (surname.isEmpty() || editBio.getText().toString().trim().isEmpty()) {
            showToast(getString(R.string.toast_fill_data));
        }

        ModelManager.getInstance().getUpdateProfileManager().updateProfileTask(mContext, UPDATE_PROFILE_TAG, user_id,
                surname, bio, coverImage, profilePic, lang);
    }

    @OnClick(R.id.imgEdit)
    public void editProfile() {
        if (isEdit) {
            showDialog();
            updateProfile();
        } else {
            imgEdit.setImageResource(R.drawable.ic_profile_done);
            setEnabled(true);
            isEdit = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            showDialog();
            ModelManager.getInstance().getProfileManager().profileTask(mContext, PROFILE_TAG, user_id, lang);
            return;
        }

        if (imagePicker != null)
            imagePicker.handleActivityResult(resultCode,requestCode, data);
        /*if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //isEdit = true;
            //imgEdit.setImageResource(R.drawable.ic_profile_done);

            Bitmap photo = ImagePicker.getImageFromResult(this, resultCode, data);
            Uri tempUri = ImagePicker.getImageUri(this, photo);
            File finalFile = new File(ImagePicker.getRealPathFromURI(this, tempUri));

            if (imageType.equals("work")) {
                tvNoCover.setVisibility(View.GONE);

                coverImage = finalFile.getAbsolutePath();
                Glide.with(mContext)
                        .load(coverImage)
                        .into(imgCover);
            } else {
                profilePic = finalFile.getAbsolutePath();
                Glide.with(mContext).load(profilePic)
                        .apply(RequestOptions.circleCropTransform().placeholder(R.mipmap.ic_user_placeholder))
                        .into(imgProfilePic);
            }
        }
        */
    }

    public void initDialog() {
        dialog = Utils.createDialog(mContext, R.layout.dialog_change_password);
        EditText editOldPass = dialog.findViewById(R.id.editOldPass);
        EditText editNewPass = dialog.findViewById(R.id.editNewPass);
        EditText editConfirmPass = dialog.findViewById(R.id.editConfirmPass);
        Button btUpdate = dialog.findViewById(R.id.btUpdate);

        btUpdate.setOnClickListener(v -> {
            String oldPass = editOldPass.getText().toString().trim();
            String newPass = editNewPass.getText().toString().trim();
            String confirmPass = editConfirmPass.getText().toString().trim();

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                showToast(getString(R.string.toast_fill_data));
            } else if (!newPass.equals(confirmPass)) {
                showToast(getString(R.string.error_password_missmatch));
            } else {
                showDialog();
                ModelManager.getInstance().getUpdatePasswordManager().updatePasswordTask(mContext, UPDATE_PASSWORD_TAG,
                        user_id, oldPass, newPass, lang);
            }
        });
        dialog.show();
    }

    @OnClick(R.id.tvChangePassword)
    public void changePassword() {
        initDialog();
    }

    @OnClick(R.id.imgViewAll)
    public void openPortfolio() {
        startActivityForResult(new Intent(mContext, PortfolioActivity.class)
                .putParcelableArrayListExtra("imagesList", imagesList), REQUEST_CODE);
    }

    @OnClick(R.id.tvEditDocument)
    public void editDocument() {
        startActivity(new Intent(mContext, EditDocActivity.class));
        Utils.gotoNextActivityAnimation(mContext);
    }


    @OnClick(R.id.tvEditLanguage)
    public void updateLanguage() {
        LanguageDialog dialog = LanguageDialog.newInstance(R.array.language_titles, selectedLangId);
        dialog.setDialogSelectorListener(this);
        dialog.show(getFragmentManager(), getString(R.string.language));
    }

    @Override
    public void onSelectedOption(int dialogId) {
        selectedLangId = dialogId;
        showDialog();

        if (dialogId == 0) {
            ModelManager.getInstance().getLanguageManager().languageTask(mContext, UPDATE_LANGUAGE_TAG, Operations.languageParams(user_id, Constants.LANGUAGE_EN));
        } else if (dialogId == 1) {
            ModelManager.getInstance().getLanguageManager().languageTask(mContext, UPDATE_LANGUAGE_TAG, Operations.languageParams(user_id, Constants.LANGUAGE_AR));
        }
    }

    @Subscribe(sticky = true)
    public void onEvent(ProfileResponse profileResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        switch (profileResponse.getRequestTag()) {
            case PROFILE_TAG:
                layoutProfile.setVisibility(View.VISIBLE);
                layoutStatus.setVisibility(View.VISIBLE);
                hideProgressBar();
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
        switch (apiResponse.getRequestTag()) {
            case UPDATE_PROFILE_TAG:
                showToast(apiResponse.getMessage());
                setEnabled(false);
                isEdit = false;
                imgEdit.setImageResource(R.drawable.ic_edit_service);
                ModelManager.getInstance().getProfileManager().profileTask(mContext, PROFILE_TAG, user_id, lang);
                break;

            case UPDATE_PASSWORD_TAG:
                dialog.dismiss();
                dismissDialog();
                showToast(apiResponse.getMessage());
                break;

            case UPDATE_LANGUAGE_TAG:
                dismissDialog();
                showToast(apiResponse.getMessage());
                switch (selectedLangId) {
                    case 0:
                        RPPreferences.putString(mContext, Constants.LANGUAGE_KEY, Constants.LANGUAGE_EN);
                        updateLanguage(Constants.LANGUAGE_EN);
                        break;
                    case 1:
                        RPPreferences.putString(mContext, Constants.LANGUAGE_KEY, Constants.LANGUAGE_AR);
                        updateLanguage(Constants.LANGUAGE_AR);
                        break;
                }
                break;

            default:
                break;
        }
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(ApiErrorWithMessageEvent event) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        hideProgressBar();
        showToast(event.getResultMsgUser());
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(RequestFinishedEvent event) {
        EventBus.getDefault().removeAllStickyEvents();
        hideProgressBar();
//        dismissDialog();
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(ApiErrorEvent event) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        hideProgressBar();
        showToast(getString(R.string.something_went_wrong));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
            if (imageType.equals("work")) {
                selectCoverPic();
            } else {
                selectProfilePic();
            }
        } else {
            Toast.makeText(this, R.string.grant_permissions, Toast.LENGTH_SHORT).show();
        }
    }

}

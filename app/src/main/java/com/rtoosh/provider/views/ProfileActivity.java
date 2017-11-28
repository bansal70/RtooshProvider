package com.rtoosh.provider.views;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rtoosh.provider.R;
import com.rtoosh.provider.controller.ModelManager;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.POJO.ProfileResponse;
import com.rtoosh.provider.model.RPPreferences;
import com.rtoosh.provider.model.custom.ImagePicker;
import com.rtoosh.provider.model.custom.Utils;
import com.rtoosh.provider.model.event.ApiErrorEvent;
import com.rtoosh.provider.model.event.ApiErrorWithMessageEvent;
import com.rtoosh.provider.model.event.RequestFinishedEvent;
import com.rtoosh.provider.model.network.AbstractApiResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends AppBaseActivity {

    private final String PROFILE_TAG = "ProfileActivity";
    private final String UPDATE_PROFILE_TAG = "UpdateProfile";
    private final String UPDATE_PASSWORD_TAG = "UpdatePassword";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbarTitle) TextView toolbarTitle;
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

    boolean isEdit = false;

    String lang, user_id, profilePic = "",coverImage = "", imageType = "";
    Dialog dialog;
    ArrayList<ProfileResponse.ProviderImage> imagesList;
    private final int REQUEST_CODE = 101;

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

        showDialog();
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
    }

    @OnClick(R.id.layoutChangePic)
    public void updateProfilePic() {
        imageType = "cover";
        dispatchTakePictureIntent();
    }

    @OnClick(R.id.imgCover)
    public void updateCoverPic() {
        imageType = "work";
        dispatchTakePictureIntent();
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
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
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
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            showDialog();
            ModelManager.getInstance().getProfileManager().profileTask(mContext, PROFILE_TAG, user_id, lang);
        }
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
    public void onEventMainThread(RequestFinishedEvent event) {
        EventBus.getDefault().removeAllStickyEvents();
//        dismissDialog();
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(ApiErrorEvent event) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        showToast(getString(R.string.something_went_wrong));
    }
}

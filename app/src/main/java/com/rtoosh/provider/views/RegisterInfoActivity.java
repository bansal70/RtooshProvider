package com.rtoosh.provider.views;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.myhexaville.smartimagepicker.ImagePicker;
import com.rtoosh.provider.R;
import com.rtoosh.provider.controller.ModelManager;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.POJO.register.RegisterInfo;
import com.rtoosh.provider.model.RPPreferences;
import com.rtoosh.provider.model.custom.Utils;
import com.rtoosh.provider.model.event.ApiErrorEvent;
import com.rtoosh.provider.model.event.ApiErrorWithMessageEvent;
import com.rtoosh.provider.model.network.AbstractApiResponse;
import com.rtoosh.provider.views.adapters.MyWorkAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static android.os.Build.VERSION_CODES.M;

public class RegisterInfoActivity extends AppBaseActivity {

    private final String REGISTRATION_TAG = "Registration";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.editSurname) EditText editSurname;
    @BindView(R.id.editBio) EditText editBio;
    @BindView(R.id.imgBg) ImageView imgBg;
    @BindView(R.id.recyclerWork) RecyclerView recyclerWork;
    @BindView(R.id.layoutWork) LinearLayout layoutWork;
    @BindView(R.id.scrollPortfolio) HorizontalScrollView scrollPortfolio;
   // @BindView(R.id.cardWork) CardView cardWork;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    String pathBg = "";
    String surname, bio;
    String lang, user_id, id, order, services, info, deviceToken;
    MyWorkAdapter myWorkAdapter;
    private List<String> listImages;
    private String imageType = "";
    ImagePicker imagePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_info);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        deviceToken = FirebaseInstanceId.getInstance().getToken();
        lang = RPPreferences.readString(mContext, Constants.LANGUAGE_KEY);
        user_id = RPPreferences.readString(mContext, Constants.USER_ID_KEY);
        id = getIntent().getStringExtra("ID");
        order = getIntent().getStringExtra("order");
        services = getIntent().getStringExtra("services");
        listImages = new ArrayList<>();

        recyclerWork.setHasFixedSize(true);
        recyclerWork.setNestedScrollingEnabled(false);
        recyclerWork.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        myWorkAdapter = new MyWorkAdapter(mContext, listImages);
        recyclerWork.setAdapter(myWorkAdapter);
    }

    @OnClick(R.id.imgSelect)
    public void selectImage() {
        imageType = "work";
        if (Build.VERSION.SDK_INT >= M) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        } else {
            selectWorkImage();
        }
        //   dispatchTakePictureIntent();
    }

    @OnClick(R.id.imgBg)
    public void bgImage() {
        imageType = "cover";
        if (Build.VERSION.SDK_INT >= M) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
        } else {
            selectCoverImage();
        }
        //   dispatchTakePictureIntent();
    }

    private void selectWorkImage() {
        imagePicker = new ImagePicker(this, null,
                (Uri imageUri) -> {
                    String path = Utils.getPathFromUri(mContext, imageUri);
                    if (path != null) {
                        path = Utils.decodeFile(path, 400, 800);
                        //File finalFile = new File(path);
                        listImages.add(path);
                        myWorkAdapter.notifyDataSetChanged();
                    }
                });
        imagePicker.choosePicture(true);
    }

    private void selectCoverImage() {
        imagePicker = new ImagePicker(this, null,
                (Uri imageUri) -> {
                    String path = Utils.getPathFromUri(mContext, imageUri);
                    if (path != null) {
                        path = Utils.decodeFile(path, 800, 400);
                        //File finalFile = new File(path);
                        Glide.with(mContext).asBitmap().load(imageUri)
                                .apply(RequestOptions.centerCropTransform()).into(imgBg);

                        pathBg = path;
                    }
                });
        imagePicker.choosePicture(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imagePicker.handleActivityResult(resultCode,requestCode, data);
        /*if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bitmap photo = ImagePicker.getImageFromResult(this, resultCode, data);

            Uri tempUri = ImagePicker.getImageUri(this, photo);
            File finalFile = new File(ImagePicker.getRealPathFromURI(this, tempUri));
            String path = finalFile.getAbsolutePath();
            if (imageType.equals("work")) {
                listImages.add(path);
                myWorkAdapter.notifyDataSetChanged();
            } else {
                pathBg = finalFile.getAbsolutePath();
                Glide.with(mContext).asBitmap().load(pathBg)
                        .apply(new RequestOptions().centerCrop()).into(imgBg);
            }
        }*/
    }

    public void saveAccount(View v) {
        deviceToken = FirebaseInstanceId.getInstance().getToken();
        surname = editSurname.getText().toString().trim();
        bio = editBio.getText().toString().trim();
        if (surname.isEmpty() || bio.isEmpty()) {
            showToast(getString(R.string.toast_fill_data));
            return;
        }
        Gson gson = new Gson();
        info = gson.toJson(getInfo());
        Timber.e("json info-- %s", info);

        showDialog();
        ModelManager.getInstance().getProviderInfoManager().providerInfoTask(mContext, REGISTRATION_TAG,
                user_id, id, order, services, info, deviceToken, lang, pathBg, myWorkAdapter.getAllImages());
    }

    private RegisterInfo getInfo() {
        RegisterInfo registerInfo = new RegisterInfo();
        registerInfo.setBio(bio);
        registerInfo.setSurname(surname);

        return registerInfo;
    }

    @Subscribe(sticky = true)
    public void onEvent(AbstractApiResponse apiResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        switch (apiResponse.getRequestTag()) {
            case REGISTRATION_TAG:
                showToast(apiResponse.getMessage());
              //  RPPreferences.putBoolean(mContext, Constants.REGISTERED_KEY, true);
                startActivity(new Intent(mContext, PasswordActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK  | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                //Utils.gotoNextActivityAnimation(this);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
            if (imageType.equals("work"))
                selectWorkImage();
            else
                selectCoverImage();
        } else {
            Toast.makeText(this, R.string.grant_permissions, Toast.LENGTH_SHORT).show();
        }
    }

}

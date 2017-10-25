package com.rtoosh.provider.views.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rtoosh.provider.R;
import com.rtoosh.provider.model.Operations;
import com.rtoosh.provider.model.custom.ImagePicker;
import com.rtoosh.provider.model.custom.Utils;

import java.io.File;

import static android.os.Build.VERSION_CODES.M;

public class RegisterInfoActivity extends AppBaseActivity implements View.OnClickListener{

    ImageView imgWork1, imgWork2, imgWork3, imgBg;
    private static final int PERMISSION_REQUEST_CODE = 1001;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String pathWork1 = "", pathWork2 = "", pathWork3 = "", pathBg = "";
    String imagePick = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_info);

        initViews();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgWork1 = findViewById(R.id.imgWork1);
        imgWork2 = findViewById(R.id.imgWork2);
        imgWork3 = findViewById(R.id.imgWork3);
        imgBg = findViewById(R.id.imgBg);

        imgWork1.setOnClickListener(this);
        imgWork2.setOnClickListener(this);
        imgWork3.setOnClickListener(this);
        imgBg.setOnClickListener(this);

        Operations.makeJSONRegisterInfo("Johnson", "bio of person");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgWork1:
                imagePick = "1";
                dispatchTakePictureIntent();
                break;

            case R.id.imgWork2:
                imagePick = "2";
                dispatchTakePictureIntent();
                break;

            case R.id.imgWork3:
                imagePick = "3";
                dispatchTakePictureIntent();
                break;

            case R.id.imgBg:
                imagePick = "4";
                dispatchTakePictureIntent();
                break;
        }
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
            switch (imagePick) {
                case "1":
                    pathWork1 = finalFile.getAbsolutePath();
                    //imgWork1.setImageBitmap(photo);
                    Glide.with(mContext)
                            .load(pathWork1)
                            .asBitmap()
                            .fitCenter()
                            .into(imgWork1);
                    break;
                case "2":
                    pathWork2 = finalFile.getAbsolutePath();
                    //imgWork2.setImageBitmap(photo);
                    Glide.with(mContext)
                            .load(pathWork2)
                            .asBitmap()
                            .centerCrop()
                            .into(imgWork2);
                    break;
                case "3":
                    pathWork3 = finalFile.getAbsolutePath();
                    //imgWork3.setImageBitmap(photo);
                    Glide.with(mContext)
                            .load(pathWork3)
                            .asBitmap()
                            .centerCrop()
                            .fitCenter()
                            .into(imgWork3);
                    break;
                case "4":
                    pathBg = finalFile.getAbsolutePath();
                    //imgBg.setImageBitmap(photo);
                    Glide.with(mContext)
                            .load(pathBg)
                            .into(imgBg);
                    break;
            }
        }
    }

    public void saveAccount(View v) {
        startActivity(new Intent(this, MainActivity.class));
        Utils.gotoNextActivityAnimation(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

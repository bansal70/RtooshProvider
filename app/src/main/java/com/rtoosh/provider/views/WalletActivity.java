package com.rtoosh.provider.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.rtoosh.provider.R;
import com.rtoosh.provider.controller.ModelManager;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.Operations;
import com.rtoosh.provider.model.POJO.WalletResponse;
import com.rtoosh.provider.model.RPPreferences;
import com.rtoosh.provider.model.custom.Utils;
import com.rtoosh.provider.model.event.ApiErrorEvent;
import com.rtoosh.provider.model.event.ApiErrorWithMessageEvent;
import com.rtoosh.provider.model.network.AbstractApiResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WalletActivity extends AppBaseActivity {

    private final String WALLET_TAG = "AccountWallet";
    private final String UPDATE_WALLET_TAG = "UpdateWallet";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbarTitle) TextView toolbarTitle;
    @BindView(R.id.editAccountName) EditText editAccountName;
    @BindView(R.id.editIBAN) EditText editIBAN;
    @BindView(R.id.imgEdit) ImageView imgEdit;

    @BindView(R.id.tvYear) TextView tvYear;
    @BindView(R.id.tvMonth) TextView tvMonth;
    @BindView(R.id.tvWeek) TextView tvWeek;
    @BindView(R.id.tvTotalEarnings) TextView tvTotalEarnings;
    @BindView(R.id.tvBalance) TextView tvBalance;

    String lang, user_id;
    boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarTitle.setText(getString(R.string.wallet));

        lang = RPPreferences.readString(mContext, "lang");
        user_id = RPPreferences.readString(mContext, "user_id");

        showDialog();
        ModelManager.getInstance().getWalletManager().walletTask(mContext, WALLET_TAG,
                Operations.walletParams(user_id, lang));
    }

    @OnClick(R.id.imgEdit)
    public void editProfile() {
        if (isEdit) {
            showDialog();
            updateWallet();
        } else {
            imgEdit.setImageResource(R.drawable.ic_profile_done);
            setEnabled(true);
            isEdit = true;
        }
    }

    private void updateWallet() {
        String accountName = editAccountName.getText().toString().trim();
        String iBan = editIBAN.getText().toString().trim();

        if (accountName.isEmpty() || iBan.isEmpty()) {
            showToast(getString(R.string.toast_fill_data));
            return;
        }

        showDialog();
        ModelManager.getInstance().getWalletManager().updateWalletTask(mContext, UPDATE_WALLET_TAG,
                Operations.updateWalletParams(user_id, accountName, iBan, lang));

    }

    private void setEnabled(boolean status) {
        editAccountName.setEnabled(status);
        editIBAN.setEnabled(status);
    }

    private void setWalletData(WalletResponse walletResponse) {
        WalletResponse.Data data = walletResponse.data;

        tvBalance.setText(String.format("%s %s", String.valueOf(data.today), Constants.CURRENCY));
        tvMonth.setText(String.format("%s %s", String.valueOf(data.monthly), Constants.CURRENCY));
        tvWeek.setText(String.format("%s %s", String.valueOf(data.weekly), Constants.CURRENCY));
        tvYear.setText(String.format("%s %s", String.valueOf(data.total), Constants.CURRENCY));
        tvTotalEarnings.setText(String.format("%s %s", String.valueOf(data.total), Constants.CURRENCY));

        WalletResponse.Account account = walletResponse.account;
        WalletResponse.AccountDetails details = account.account;

        editAccountName.setText(details.name);
        editIBAN.setText(details.ibanNo);
    }

    @Subscribe(sticky = true)
    public void onEvent(WalletResponse walletResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        switch (walletResponse.getRequestTag()) {
            case WALLET_TAG:
                setWalletData(walletResponse);
                break;
        }
    }

    @Subscribe(sticky = true)
    public void onEvent(AbstractApiResponse apiResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        switch (apiResponse.getRequestTag()) {
            case UPDATE_WALLET_TAG:
                showToast(apiResponse.getMessage());
                setEnabled(false);
                isEdit = false;
                imgEdit.setImageResource(R.drawable.ic_edit_service);
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

    @OnClick(R.id.tvContactUs)
    public void contactScreen() {
        startActivity(new Intent(mContext, ContactActivity.class));
        Utils.gotoNextActivityAnimation(mContext);
    }

}

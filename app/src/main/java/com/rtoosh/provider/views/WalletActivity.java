package com.rtoosh.provider.views;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.RPPreferences;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WalletActivity extends AppBaseActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbarTitle) TextView toolbarTitle;
    String lang, user_id;

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
    }
}

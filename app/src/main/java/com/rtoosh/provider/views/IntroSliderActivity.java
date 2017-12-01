package com.rtoosh.provider.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.rtoosh.provider.R;
import com.rtoosh.provider.views.adapters.SliderAdapter;
import com.rtoosh.provider.model.custom.Utils;
import com.viewpagerindicator.LinePageIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IntroSliderActivity extends AppBaseActivity {

    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.pageIndicator)
    LinePageIndicator pageIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_slider);
        ButterKnife.bind(this);

        initUI();
    }

    private void initUI(){
        pager.setAdapter(new SliderAdapter(mContext));
        pageIndicator.setViewPager(pager);
    }

    public void skipClick(View v){
        startActivity(new Intent(mContext, PhoneVerificationActivity.class));
        Utils.gotoNextActivityAnimation(mContext);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

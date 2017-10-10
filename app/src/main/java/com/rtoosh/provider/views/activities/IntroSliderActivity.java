package com.rtoosh.provider.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.rtoosh.provider.R;
import com.rtoosh.provider.views.adapters.SliderAdapter;
import com.rtoosh.provider.model.custom.Utils;
import com.viewpagerindicator.LinePageIndicator;

public class IntroSliderActivity extends AppBaseActivity {

    ViewPager pager;
    LinePageIndicator pageIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_slider);

        initUI();
    }

    private void initUI(){
        pager=(ViewPager)findViewById(R.id.pager);
        pageIndicator=(LinePageIndicator)findViewById(R.id.pageIndicator);

        pager.setAdapter(new SliderAdapter(mContext));
        pageIndicator.setViewPager(pager);
    }

    public void skipClick(View v){
        startActivity(new Intent(mContext, MainActivity.class));
        Utils.gotoNextActivityAnimation(mContext);
    }
}

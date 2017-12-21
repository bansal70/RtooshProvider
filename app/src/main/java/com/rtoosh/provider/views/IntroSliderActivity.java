package com.rtoosh.provider.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.rtoosh.provider.R;
import com.rtoosh.provider.views.adapters.SliderAdapter;
import com.rtoosh.provider.model.custom.Utils;
import com.viewpagerindicator.LinePageIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IntroSliderActivity extends AppBaseActivity {

    @BindView(R.id.pager) ViewPager pager;
    @BindView(R.id.pageIndicator) LinePageIndicator pageIndicator;
    @BindView(R.id.btNext)
    Button btNext;

    private int pagerPosition = 0;

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

        pager.addOnPageChangeListener(viewPagerPageChangeListener);
    }

    public void skipClick(View v){
        startActivity(new Intent(mContext, PhoneVerificationActivity.class));
        Utils.gotoNextActivityAnimation(mContext);
    }

    @OnClick(R.id.btNext)
    public void actionNext(){
        int current = pager.getCurrentItem();
        if (current < 2) {
            pager.setCurrentItem(current + 1);
        } else {
            startActivity(new Intent(mContext, PhoneVerificationActivity.class));
            Utils.gotoNextActivityAnimation(mContext);
        }
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            pagerPosition = position;
            if (position == 2) {
                btNext.setText(R.string.button_register_now);
            } else {
                btNext.setText(getString(R.string.next));
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };


    private int getItem(int i) {
        return pager.getCurrentItem() + i;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

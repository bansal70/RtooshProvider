package com.rtoosh.provider.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.rtoosh.provider.R;
import com.rtoosh.provider.controller.ModelManager;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.POJO.Portfolio;
import com.rtoosh.provider.model.POJO.ProfileResponse;
import com.rtoosh.provider.model.RPPreferences;
import com.rtoosh.provider.model.custom.DividerItemDecorator;
import com.rtoosh.provider.model.custom.ImagePicker;
import com.rtoosh.provider.model.event.ApiErrorEvent;
import com.rtoosh.provider.model.event.ApiErrorWithMessageEvent;
import com.rtoosh.provider.model.event.RequestFinishedEvent;
import com.rtoosh.provider.model.network.AbstractApiResponse;
import com.rtoosh.provider.views.adapters.PortfolioAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class PortfolioActivity extends AppBaseActivity {

    private final String PORTFOLIO_TAG = "PortfolioActivity";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbarTitle) TextView toolbarTitle;
    @BindView(R.id.recyclerPortfolio) RecyclerView recyclerPortfolio;
    @BindView(R.id.btRemovePhoto) Button btRemovePhoto;
    @BindView(R.id.btAddPhoto) Button btAddPhoto;

    String lang, user_id;
    ArrayList<Portfolio> listPortfolio;
    private PortfolioAdapter portfolioAdapter;
    ArrayList<ProfileResponse.ProviderImage> portfolioList;
    private int lastId = 0;
    private List<String> listRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);
        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarTitle.setText(getString(R.string.profile));

        lang = RPPreferences.readString(mContext, Constants.LANGUAGE_KEY);
        user_id = RPPreferences.readString(mContext, Constants.USER_ID_KEY);

        initRecycler();
    }

    private void initRecycler() {
        portfolioList = getIntent().getParcelableArrayListExtra("imagesList");
        listPortfolio = new ArrayList<>();
        listRemove = new ArrayList<>();

        for (int i = 0; i < portfolioList.size(); i++) {
            listPortfolio.add(new Portfolio(portfolioList.get(i).id, portfolioList.get(i).image,
                    false, false));
            if (i == portfolioList.size() - 1)
                lastId = Integer.parseInt(portfolioList.get(portfolioList.size() - 1).id);
        }

        recyclerPortfolio.setLayoutManager(new GridLayoutManager(mContext, 3));

        recyclerPortfolio.addItemDecoration(new DividerItemDecorator(20, 3));

        portfolioAdapter = new PortfolioAdapter(mContext, listPortfolio);
        recyclerPortfolio.setAdapter(portfolioAdapter);
    }

    @OnClick(R.id.btRemovePhoto)
    public void removePhoto() {
        listPortfolio = portfolioAdapter.getPortfolio();
        for (int i = listPortfolio.size() - 1; i >= 0; i--) {
            if (listPortfolio.get(i).isSelected()) {
                Timber.e("selected-- " + listPortfolio.get(i).getId());
                listRemove.add(listPortfolio.get(i).getId());
                listPortfolio.remove(i);
                portfolioAdapter.notifyItemRemoved(i);
                portfolioAdapter.notifyItemRangeChanged(i, listPortfolio.size());
            }
        }
    }

    @OnClick(R.id.btAddPhoto)
    public void addPhoto() {
        dispatchTakePictureIntent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap photo = ImagePicker.getImageFromResult(this, resultCode, data);
            Uri tempUri = ImagePicker.getImageUri(this, photo);
            File finalFile = new File(ImagePicker.getRealPathFromURI(this, tempUri));
            lastId++;
            listPortfolio.add(new Portfolio(String.valueOf(lastId), finalFile.getAbsolutePath(), false, true));
            portfolioAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_work, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mItem) {
        switch (mItem.getItemId()) {
            case R.id.menuDone:
                showDialog();
                ModelManager.getInstance().getUpdateProfileManager().updatePortfolio(mContext,
                        PORTFOLIO_TAG, user_id, lang, listPortfolio, listRemove);
                break;
        }
        return super.onOptionsItemSelected(mItem);
    }

    @Subscribe(sticky = true)
    public void onEvent(AbstractApiResponse apiResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dismissDialog();
        switch (apiResponse.getRequestTag()) {
            case PORTFOLIO_TAG:
                showToast(apiResponse.getMessage());
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                break;

            default:
                break;
        }
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(RequestFinishedEvent event) {
        switch (event.getRequestTag()) {
            case PORTFOLIO_TAG:
                EventBus.getDefault().removeAllStickyEvents();
                dismissDialog();
                finish();
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

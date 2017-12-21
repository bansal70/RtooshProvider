package com.rtoosh.provider.views.adapters;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.rtoosh.provider.model.Operations;
import com.rtoosh.provider.model.POJO.ProfileResponse;
import com.rtoosh.provider.model.RPPreferences;
import com.rtoosh.provider.model.custom.Utils;
import com.rtoosh.provider.model.event.ApiErrorEvent;
import com.rtoosh.provider.model.event.ApiErrorWithMessageEvent;
import com.rtoosh.provider.model.network.AbstractApiResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SpecialOffersAdapter extends RecyclerView.Adapter<SpecialOffersAdapter.ViewHolder>{

    private final String ADD_SPECIAL_TAG = "ADD_SPECIAL";
    private Context mContext;
    private List<ProfileResponse.Service> serviceList;
    private boolean isSpecial = false;
    private String user_id, lang;
    private Dialog dialog, specialDialog;
    private int pos;
    private EditText editPrice;

    public SpecialOffersAdapter(Context mContext, List<ProfileResponse.Service> serviceList) {
        this.mContext = mContext;
        this.serviceList = serviceList;
        lang = RPPreferences.readString(mContext, Constants.LANGUAGE_KEY);
        user_id = RPPreferences.readString(mContext, Constants.USER_ID_KEY);
        dialog = Utils.showDialog(mContext);

        EventBus.getDefault().register(this);
        specialDialog = Utils.createDialog(mContext, R.layout.dialog_special_price);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_special_offers, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ProfileResponse.Service service = serviceList.get(position);
        ProfileResponse.Category category = service.category;
        holder.tvServiceName.setText(service.serviceName);
        holder.tvServiceContent.setText(service.description);
        holder.tvServicePrice.setText(String.format("%s %s", service.price, mContext.getString(R.string.currency)));
        holder.tvServiceDuration.setText(service.duration);

        Glide.with(mContext).load(category.image).apply(RequestOptions.centerCropTransform()).into(holder.imgService);

        if (service.special.equals("1")) {
            holder.cardOffers.setBackgroundResource(R.drawable.custom_stroke_pink);
            holder.cardOffers.setPadding(5,5,5,5);
            holder.imgSpecial.setVisibility(View.VISIBLE);
        } else {
            holder.imgSpecial.setVisibility(View.GONE);
        }

        holder.tvCategory.setText(service.serviceName);

    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvServiceName) TextView tvServiceName;
        @BindView(R.id.tvServiceContent) TextView tvServiceContent;
        @BindView(R.id.tvServicePrice) TextView tvServicePrice;
        @BindView(R.id.tvServiceDuration) TextView tvServiceDuration;
        @BindView(R.id.cardOffers) LinearLayout cardOffers;
        @BindView(R.id.imgSpecial) ImageView imgSpecial;
        @BindView(R.id.imgService) ImageView imgService;
        @BindView(R.id.tvCategory) TextView tvCategory;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(v -> {
                for (int i=0; i<serviceList.size(); i++) {
                    if (getAdapterPosition() != i && serviceList.get(i).special.equals("1")) {
                        Utils.showToast(mContext, "You can not add more than one special offer");
                        return;
                    }
                }

                if (serviceList.get(getAdapterPosition()).special.equals("1")) {
                    dialogSpecial(getAdapterPosition());
                    isSpecial = false;
                } else {
                    dialogSpecial(getAdapterPosition());
                    isSpecial = true;
                }

                pos = getAdapterPosition();

            });
        }
    }

    private void dialogSpecial(int position) {
        editPrice = specialDialog.findViewById(R.id.editPrice);
        Button btSubmit = specialDialog.findViewById(R.id.btSubmit);

        ProfileResponse.Service service = serviceList.get(position);
        editPrice.setText(service.price);
        if (service.special.equals("0"))
            btSubmit.setText(R.string.text_add_special);
        else
            btSubmit.setText(R.string.text_remove_special);

        btSubmit.setOnClickListener(v -> {
            if (editPrice.getText().toString().trim().isEmpty()) {
                Utils.showToast(mContext, "Price can not be empty");
                return;
            }

            dialog.show();
            ModelManager.getInstance().getSpecialOffersManager().specialOffersTask(mContext,
                    ADD_SPECIAL_TAG, Operations.specialOfferParams(user_id, service.id,
                            editPrice.getText().toString(), isSpecial, lang));

        });

        specialDialog.show();
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(AbstractApiResponse apiResponse) {
        EventBus.getDefault().removeAllStickyEvents();
        dialog.dismiss();
        specialDialog.dismiss();
        switch (apiResponse.getRequestTag()) {
            case ADD_SPECIAL_TAG:
                Utils.showToast(mContext, apiResponse.getMessage());
                if (isSpecial) {
                    serviceList.get(pos).setSpecial("1");
                    serviceList.get(pos).setPrice(editPrice.getText().toString().trim());
                }
                else {
                    serviceList.get(pos).setSpecial("0");
                    serviceList.get(pos).setPrice(editPrice.getText().toString().trim());
                }

                //notifyDataSetChanged();
                notifyItemChanged(pos);
                break;
        }
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(ApiErrorWithMessageEvent event) {
        EventBus.getDefault().removeAllStickyEvents();
        dialog.dismiss();
    }

    @Subscribe(sticky = true)
    public void onEventMainThread(ApiErrorEvent event) {
        EventBus.getDefault().removeAllStickyEvents();
        dialog.dismiss();
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        EventBus.getDefault().unregister(this);
    }
}

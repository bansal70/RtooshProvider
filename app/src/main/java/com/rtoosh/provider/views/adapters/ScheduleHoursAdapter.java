package com.rtoosh.provider.views.adapters;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rtoosh.provider.R;
import com.rtoosh.provider.model.POJO.register.OpeningHours;
import com.rtoosh.provider.model.POJO.register.OpeningTime;
import com.rtoosh.provider.model.custom.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScheduleHoursAdapter extends RecyclerView.Adapter<ScheduleHoursAdapter.ViewHolder>{
    private Context context;
    private List<OpeningHours> openingHoursList;
    private Dialog timeDialog;
    private int position;
    @BindView(R.id.tvOpenTime) TextView tvOpenTime;
    @BindView(R.id.tvCloseTime) TextView tvCloseTime;
    @BindView(R.id.tvAddTime) TextView tvAddTime;
    @BindView(R.id.tvCancelTime) TextView tvCancelTime;

    public ScheduleHoursAdapter(Context context, List<OpeningHours> openingHoursList) {
        this.context = context;
        this.openingHoursList = openingHoursList;

        timeDialog = Utils.createDialog(context, R.layout.dialog_opening_hours);
        ButterKnife.bind(this, timeDialog);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_schedule_hours, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        OpeningHours openingHours = openingHoursList.get(position);
        OpeningTime openingTime = openingHours.getOpeningTime();
        holder.tvDays.setText(openingHours.getDay());
        if (!openingTime.getFrom().isEmpty()) {
            holder.tvOpen.setText(openingTime.getFrom());
            holder.tvClose.setText(openingTime.getTo());
        }
    }

    @Override
    public int getItemCount() {
        return openingHoursList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvDays) TextView tvDays;
        @BindView(R.id.tvOpen) TextView tvOpen;
        @BindView(R.id.tvClose) TextView tvClose;

        private ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.tvDays)
        public void selectHours() {
            position = getAdapterPosition();
            timeDialog.show();
        }
    }

    @OnClick(R.id.tvOpenTime)
    public void setOpenTime() {
        Utils.setTimePicker(context, tvOpenTime);
    }

    @OnClick(R.id.tvCloseTime)
    public void setCloseTime() {
        Utils.setTimePicker(context, tvCloseTime);
    }

    @OnClick(R.id.tvAddTime)
    public void addTime() {
        OpeningHours openingHours = openingHoursList.get(position);
        OpeningTime openingTime = openingHours.getOpeningTime();
        openingTime.setFrom(tvOpenTime.getText().toString());
        openingTime.setTo(tvCloseTime.getText().toString());

        openingHours.setDay(openingHours.getDay());
        openingHours.setOpeningTime(openingTime);
        openingHoursList.set(position, openingHours);
        notifyItemChanged(position);
        timeDialog.dismiss();
    }

    @OnClick(R.id.tvCancelTime)
    public void cancelTime() {
        timeDialog.dismiss();
    }

    public List<OpeningHours> hoursList() {
        return openingHoursList;
    }
}

package com.rtoosh.provider.views.adapters;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
    private FragmentManager manager;
    @BindView(R.id.tvOpenTime) TextView tvOpenTime;
    @BindView(R.id.tvCloseTime) TextView tvCloseTime;
    @BindView(R.id.tvAddTime) TextView tvAddTime;
    @BindView(R.id.tvCancelTime) TextView tvCancelTime;

    public ScheduleHoursAdapter(Context context, List<OpeningHours> openingHoursList, FragmentManager manager) {
        this.context = context;
        this.openingHoursList = openingHoursList;
        this.manager = manager;

        timeDialog = Utils.createDialog(context, R.layout.dialog_opening_hours);
        ButterKnife.bind(this, timeDialog);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_job_hours, parent, false);
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

        @OnClick(R.id.layoutDay)
        public void selectHours() {
            OpeningHours openingHours = openingHoursList.get(getAdapterPosition());
            OpeningTime openingTime = openingHours.getOpeningTime();
            tvOpenTime.setText(openingTime.getFrom());
            tvCloseTime.setText(openingTime.getTo());

            position = getAdapterPosition();
            timeDialog.show();
        }

        @OnClick(R.id.imgClear)
        public void clearDay() {
            position = getAdapterPosition();
            OpeningHours openingHours = openingHoursList.get(position);
            OpeningTime openingTime = new OpeningTime("", "");
            openingHours = new OpeningHours(openingHours.getDay(), openingTime);
            //if (!openingTime.getFrom().isEmpty() || !openingTime.getTo().isEmpty()) {
           /* openingTime.setFrom("");
            openingTime.setTo("");

            openingHours.setDay(openingHours.getDay());
            openingHours.setOpeningTime(openingTime);*/
            openingHoursList.set(position, openingHours);
            //notifyItemChanged(position);
            notifyDataSetChanged();
            // }
        }
    }

    @OnClick(R.id.tvOpenTime)
    public void setOpenTime() {
      //  Utils.setTimePicker(context, tvOpenTime);
        initTimeDialog("open");
    }

    @OnClick(R.id.tvCloseTime)
    public void setCloseTime() {
      //  Utils.setTimePicker(context, tvCloseTime);
        initTimeDialog("close");
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

    private void initTimeDialog(String openClose) {
        Dialog timeDialog = Utils.createDialog(context, R.layout.dialog_time_picker);
        EditText editHours = timeDialog.findViewById(R.id.editHours);
        EditText editMinutes = timeDialog.findViewById(R.id.editMinutes);
        Button btSet = timeDialog.findViewById(R.id.btSet);
        Button btCancel = timeDialog.findViewById(R.id.btCancel);
        TextView tvInvalidTime = timeDialog.findViewById(R.id.tvInvalidTime);

        if (openClose.equals("open")) {
            if (tvOpenTime.getText().toString().contains(":")) {
                String[] hhMM = tvOpenTime.getText().toString().split(":");
                editHours.setText(hhMM[0]);
                editMinutes.setText(hhMM[1]);
            }
        } else {
            if (tvCloseTime.getText().toString().contains(":")) {
                String[] hhMM = tvCloseTime.getText().toString().split(":");
                editHours.setText(hhMM[0]);
                editMinutes.setText(hhMM[1]);
            }
        }

        btSet.setOnClickListener(view -> {
            if (editHours.getText().toString().isEmpty() || editMinutes.getText().toString().isEmpty()) {
                tvInvalidTime.setVisibility(View.VISIBLE);
                return;
            }

            int hours = Integer.parseInt(editHours.getText().toString());
            int minutes = Integer.parseInt(editMinutes.getText().toString());

            if (hours > 23 || minutes > 59) {
                tvInvalidTime.setVisibility(View.VISIBLE);
                return;
            }

            timeDialog.dismiss();
            if (openClose.equals("open")) {
                tvOpenTime.setText(String.format("%s:%s", editHours.getText().toString(), editMinutes.getText().toString()));
            } else {
                tvCloseTime.setText(String.format("%s:%s", editHours.getText().toString(), editMinutes.getText().toString()));
            }
           // tvServiceDuration.setText(String.format("%s:%s", editHours.getText().toString(), editMinutes.getText().toString()));
        });

        btCancel.setOnClickListener(view -> timeDialog.cancel());

        timeDialog.show();
    }
}

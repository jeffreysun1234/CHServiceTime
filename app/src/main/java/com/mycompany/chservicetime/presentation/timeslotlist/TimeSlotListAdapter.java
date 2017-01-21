package com.mycompany.chservicetime.presentation.timeslotlist;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mycompany.chservicetime.CHApplication;
import com.mycompany.chservicetime.R;
import com.mycompany.chservicetime.model.TimeSlot;
import com.mycompany.chservicetime.service.SchedulingIntentService;
import com.mycompany.chservicetime.util.DisplayUtils;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class TimeSlotListAdapter extends SwipeMenuAdapter<TimeSlotListAdapter.ViewHolder> {
    private static final String TAG = "TimeSlotListAdapter";

    private static ItemActionListenerInterface mItemListener = null;

    static List<TimeSlot> mTimeSlots = null;

    public TimeSlotListAdapter(List<TimeSlot> timeSlots, ItemActionListenerInterface itemListener) {
        setList(timeSlots);
        mItemListener = itemListener;
    }

    public void replaceData(List<TimeSlot> timeSlots) {
        setList(timeSlots);
        notifyDataSetChanged();

        // Send the open and close sound alarms based on the current data.
        SchedulingIntentService.startActionSetAlarm(CHApplication.getContext());
    }

    private void setList(List<TimeSlot> timeSlots) {
        mTimeSlots = checkNotNull(timeSlots);
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext())
                .inflate(R.layout.time_slot_list_item, parent, false);
    }

    @Override
    public ViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        return new ViewHolder(realContentView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(mTimeSlots.get(position));
    }

    @Override
    public int getItemCount() {
        return mTimeSlots == null ? 0 : mTimeSlots.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox activeSwitch;
        TextView nameTextView;
        TextView timeTextView;
        TextView daysTextView;
        TextView repeatWeeklyTextView;
        TextView serviceOptionView;

        public ViewHolder(View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            activeSwitch = (CheckBox) itemView.findViewById(R.id.activeSwitch);
            timeTextView = (TextView) itemView.findViewById(R.id.timeTextView);
            daysTextView = (TextView) itemView.findViewById(R.id.daysTextView);
            repeatWeeklyTextView = (TextView) itemView.findViewById(R.id.repeatWeeklyTextView);
            serviceOptionView = (TextView) itemView.findViewById(R.id.serviceOptionIcon);

            setListeners();
        }

        public void bindData(@NonNull TimeSlot timeSlot) {
            this.nameTextView.setText(timeSlot.name());
            this.activeSwitch.setChecked(timeSlot.activation_flag());
            this.timeTextView.setText(DisplayUtils.buildTimePeriodString(timeSlot.begin_time_hour(),
                    timeSlot.begin_time_minute(), timeSlot.end_time_hour(), timeSlot.end_time_minute()));
            this.daysTextView.setText(DisplayUtils.daysToText(timeSlot.days()));
            this.repeatWeeklyTextView.setText(DisplayUtils.repeatFlagToText(timeSlot.repeat_flag()));
            this.serviceOptionView.setText(timeSlot.service_option().toString());
        }

        public void setListeners() {
            checkNotNull(mItemListener);

            itemView.setOnLongClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mItemListener.onItemLongClicked(mTimeSlots.get(position)._id());
                }
                return true;
            });

            itemView.setOnClickListener(v -> {
                Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mItemListener.onTimeSlotClick(mTimeSlots.get(position)._id());
                }
            });

            activeSwitch.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mItemListener.onActiveFlagSwitchClicked(mTimeSlots.get(position)._id(),
                            ((CheckBox) view).isChecked());
                }
            });
        }
    }

}

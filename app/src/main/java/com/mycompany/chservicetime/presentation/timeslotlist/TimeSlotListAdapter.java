package com.mycompany.chservicetime.presentation.timeslotlist;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mycompany.chservicetime.R;
import com.mycompany.chservicetime.model.TimeSlot;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by szhx on 1/19/2017.
 */

public class TimeSlotListAdapter extends RecyclerView.Adapter<TimeSlotListAdapter.ViewHolder> {

    private static final String TAG = "TimeSlotListAdapter";

    private static TimeSlotItemListener mItemListener = null;

    private List<TimeSlot> mTimeSlots;

    public TimeSlotListAdapter(List<TimeSlot> timeSlots, TimeSlotItemListener itemListener) {
        setList(timeSlots);
        mItemListener = itemListener;
    }

    public void replaceData(List<TimeSlot> timeSlots) {
        setList(timeSlots);
        notifyDataSetChanged();
    }

    private void setList(List<TimeSlot> timeSlots) {
        mTimeSlots = checkNotNull(timeSlots);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.time_slot_list_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(mTimeSlots.get(position));
    }

    @Override
    public int getItemCount() {
        return mTimeSlots.size();
    }

//    @Override
//    public View getView(int i, View view, ViewGroup viewGroup) {
//        View rowView = view;
//        if (rowView == null) {
//            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
//            rowView = inflater.inflate(R.layout.timeslot_item, viewGroup, false);
//        }
//
//        final TimeSlot timeSlot = getItem(i);
//
//        TextView titleTV = (TextView) rowView.findViewById(R.id.title);
//        titleTV.setText(timeSlot.name());
//
//        CheckBox completeCB = (CheckBox) rowView.findViewById(R.id.complete);
//
////            // Active/completed timeSlot UI
////            completeCB.setChecked(timeSlot.isCompleted());
////            if (timeSlot.isCompleted()) {
////                rowView.setBackgroundDrawable(viewGroup.getContext()
////                        .getResources().getDrawable(R.drawable.list_completed_touch_feedback));
////            } else {
////                rowView.setBackgroundDrawable(viewGroup.getContext()
////                        .getResources().getDrawable(R.drawable.touch_feedback));
////            }
//
//        completeCB.setOnClickListener(v -> {
//            if (!timeSlot.activation_flag()) {
//                mItemListener.onCompleteTimeSlotClick(timeSlot);
//            } else {
//                mItemListener.onActivateTimeSlotClick(timeSlot);
//            }
//        });
//
//        rowView.setOnClickListener(__ -> mItemListener.onTimeSlotClick(timeSlot));
//
//        return rowView;
//    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTV;
        CheckBox completeCB;

        String currentTimeSlotId;
        TimeSlot currentTimeSlot;

        public ViewHolder(View itemView) {
            super(itemView);

            nameTV = (TextView) itemView.findViewById(R.id.title);
            completeCB = (CheckBox) itemView.findViewById(R.id.complete);

            setListeners();
        }

        public void bindData(@NonNull TimeSlot timeSlot) {
            currentTimeSlot = timeSlot;

            this.nameTV.setText(timeSlot.name());
        }

        public void setListeners() {
            // Define click listener for the ViewHolder's View.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                    mItemListener.onTimeSlotClick(currentTimeSlot);
                }
            });

            completeCB.setOnClickListener(v -> {
                if (!currentTimeSlot.activation_flag()) {
                    mItemListener.onCompleteTimeSlotClick(currentTimeSlot);
                } else {
                    mItemListener.onActivateTimeSlotClick(currentTimeSlot);
                }
            });
        }

        public TextView getNameTV() {
            return nameTV;
        }
    }

}

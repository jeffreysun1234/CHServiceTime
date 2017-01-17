package com.mycompany.chservicetime.presentation.timeslots;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mycompany.chservicetime.R;
import com.mycompany.chservicetime.model.ColumnIndexCache;
import com.mycompany.chservicetime.model.ModelConverter;
import com.mycompany.chservicetime.model.TimeSlot;
import com.mycompany.chservicetime.util.DisplayUtils;


/**
 * Created by szhx on 3/8/2016.
 */
public class TimeSlotCursorRecyclerAdapter extends RecyclerView.Adapter<TimeSlotCursorRecyclerAdapter.TimeSlotViewHolder> {

    private final ItemActionListener mItemActionListener;

    public interface ItemActionListener {
        void onItemLongClicked(String timeSlotId);

        void deleteItem(String timeSlotId);

        void onActiveFlagSwitchClicked(String timeSlotId, boolean activeFlag);
    }

    Cursor mDataCursor;
    boolean mDataValid;

    /**
     * current id in cursor
     **/
    int mTimeSlotIdColumn;

    /**
     * observer the change of curror
     **/
    DataSetObserver mDataSetObserver;

    public class TimeSlotViewHolder extends RecyclerView.ViewHolder {
        CustomCardView swipeLayout;
        RelativeLayout upperChildView;
        CheckBox activeSwitch;
        TextView nameTextView;
        TextView timeTextView;
        TextView daysTextView;
        TextView repeatWeeklyTextView;
        ImageButton editItemButton;
        ImageButton deleteItemButton;

        String currentTimeSlotId;

        public TimeSlotViewHolder(View itemView) {
            super(itemView);

            swipeLayout = (CustomCardView) itemView.findViewById(R.id.card_view);
            upperChildView = (RelativeLayout) itemView.findViewById(R.id.upper_child_view);
            nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
            activeSwitch = (CheckBox) itemView.findViewById(R.id.activeSwitch);
            timeTextView = (TextView) itemView.findViewById(R.id.timeTextView);
            daysTextView = (TextView) itemView.findViewById(R.id.daysTextView);
            repeatWeeklyTextView = (TextView) itemView.findViewById(R.id.repeatWeeklyTextView);
            editItemButton = (ImageButton) itemView.findViewById(R.id.edit_item_button);
            deleteItemButton = (ImageButton) itemView.findViewById(R.id.delete_item_button);

            setListeners();
        }

        public void bindData(@NonNull TimeSlot timeSlot) {
            this.nameTextView.setText(timeSlot.name());
            this.activeSwitch.setChecked(timeSlot.activation_flag());
            this.timeTextView.setText(DisplayUtils.buildTimePeriodString(timeSlot.begin_time_hour(),
                    timeSlot.begin_time_minute(), timeSlot.end_time_hour(), timeSlot.end_time_minute()));
            this.daysTextView.setText(DisplayUtils.daysToText(timeSlot.days()));
            this.repeatWeeklyTextView.setText(DisplayUtils.repeatFlagToText(timeSlot.repeat_flag()));

            this.currentTimeSlotId = timeSlot._id();
        }

        public void setListeners() {
            if (mItemActionListener != null) {
                upperChildView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        mItemActionListener.onItemLongClicked(currentTimeSlotId);
                        return true;
                    }
                });

                activeSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mItemActionListener.onActiveFlagSwitchClicked(currentTimeSlotId, activeSwitch.isChecked());
                    }
                });

                editItemButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemActionListener.onItemLongClicked(currentTimeSlotId);
                    }
                });

                deleteItemButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemActionListener.deleteItem(currentTimeSlotId);
                    }
                });
            }
        }
    }

    /**
     * to show if the item can be deleted,it basically depends on
     * whether nowOpen is null.BUT the change of the value may be delayed
     * to avoid user slide a item to right and delete it without ACTION_UP.
     */
    private boolean canDelete = true;

    /**
     * in this situation,we assume that there's only one item
     * can be at the state OPEN at one time. so when a holder is
     * opened, we get its instance. and before you do other operations,
     * the opened item should be closed.
     */
    private TimeSlotViewHolder nowOpen = null;

    static CustomCardView.SwipeConfig swipeConfig = new CustomCardView.SwipeConfig(240, 0);

    public TimeSlotCursorRecyclerAdapter(ItemActionListener itemActionListener, Cursor cursor) {
        this.mItemActionListener = itemActionListener;
        this.mDataCursor = cursor;
        this.mDataValid = cursor != null;
        mTimeSlotIdColumn = mDataValid ? mDataCursor.getColumnIndex("_id") : -1;
        mDataSetObserver = new NotifyingDataSetObserver();
        if (mDataCursor != null) {
            mDataCursor.registerDataSetObserver(mDataSetObserver);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TimeSlotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        ((CustomCardView) itemView).configSwipe(swipeConfig);

        return new TimeSlotViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final TimeSlotViewHolder holder, int position) {
        holder.bindData(getItem(position));

        /*
        to manage get more buttons issue and delete issue,we suppose there's only
        one item open at time.
         */
        holder.swipeLayout.setOnOnSwipeListener(new CustomCardView.OnSwipeListener() {
            @Override
            public void onStartOpen() {
                if (nowOpen != null && (nowOpen != holder)) {
                    nowOpen.swipeLayout.close();
                    nowOpen = null;
                }
            }

            @Override
            public void onOpen() {
                nowOpen = holder;
                canDelete = false;

            }

            @Override
            public void onStartClose() {

            }


            @Override
            public void onClose() {
                if (nowOpen == holder) {
                    nowOpen = null;
                }

            }
        });

        /*
        we use this listener to close any open item before the next action
        you might notice that the field canDelete IS NOT changed once close the
        item,it's changed only after the user make an ACTION_DOWN event again,which
        assure we won't delete the item carelessly
         */
        holder.upperChildView.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (nowOpen != null) {
                    nowOpen.swipeLayout.close();
                    return true;
                } else {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        canDelete = true;
                    }

                }
                return false;
            }
        });
    }

    ////// swipe item //////

    /**
     * Since the view is cached when is not seen,we should restore the
     * state of the view once in cache,that is,we should reset the childlayout
     *
     * @param holder
     */
    @Override
    public void onViewDetachedFromWindow(TimeSlotViewHolder holder) {

        //if it was once deleted,we should reset the position of its child layout
        holder.swipeLayout.initialState();
        super.onViewDetachedFromWindow(holder);
    }

    /**
     * to tell the ItemTouchHelper if the item can be deleted
     *
     * @return
     */
    public boolean canDelete() {
        return canDelete;
    }

    /////// cursor data //////
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return (mDataValid && mDataCursor != null) ? mDataCursor.getCount() : 0;
    }

    @Override
    public long getItemId(int position) {
        if (mDataValid && mDataCursor != null && mDataCursor.moveToPosition(position)) {
            return mDataCursor.getLong(mTimeSlotIdColumn);
        }
        return 0;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be closed.
     */
    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em> closed.
     */
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mDataCursor) {
            return null;
        }
        final Cursor oldCursor = mDataCursor;
        if (oldCursor != null && mDataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }
        mDataCursor = newCursor;
        if (mDataCursor != null) {
            if (mDataSetObserver != null) {
                mDataCursor.registerDataSetObserver(mDataSetObserver);
            }
            mTimeSlotIdColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            notifyDataSetChanged();
        } else {
            mTimeSlotIdColumn = -1;
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
        return oldCursor;
    }

    private TimeSlot getItem(int position) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mDataCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        TimeSlot timeSlot = ModelConverter.cursorToTimeSlotModel(mDataCursor, new ColumnIndexCache());
        return timeSlot;
    }

    private class NotifyingDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            mDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
    }
}

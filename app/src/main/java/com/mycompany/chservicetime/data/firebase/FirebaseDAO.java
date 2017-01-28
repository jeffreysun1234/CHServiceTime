package com.mycompany.chservicetime.data.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mycompany.chservicetime.data.firebase.model.TimeSlotItem;
import com.mycompany.chservicetime.data.firebase.model.TimeSlotList;
import com.mycompany.chservicetime.model.ModelConverter;
import com.mycompany.chservicetime.model.TimeSlot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mycompany.chservicetime.util.LogUtils.LOGD;
import static com.mycompany.chservicetime.util.LogUtils.makeLogTag;

/**
 * Created by szhx on 3/24/2016.
 */
public class FirebaseDAO {
    public static final String TAG = makeLogTag("FirebaseDAO");

    DatabaseReference mDatabase;

    private FirebaseDAO() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public static FirebaseDAO create() {
        return new FirebaseDAO();
    }

    /**
     * Add new TimeSlot list
     */
    public void addTimeSlotList(String userId) throws IOException {
        /* build a TimeSlot list */
//        TimeSlotList newTimeSlotList = new TimeSlotList("My List", FirebaseAuthAdapter.getEmail(),
//                FirebaseUtils.getTimestampNowObject());
        TimeSlotList newTimeSlotList = new TimeSlotList("My List", userId,
                FirebaseUtils.getTimestampNowObject());

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(FirebaseConstants.timeSlotListURL(userId, false), newTimeSlotList);
        mDatabase.updateChildren(childUpdates);
    }

    public void deleteTimeSlotItems(String userId) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(FirebaseConstants.timeSlotItemListURL(userId, false), null);
        mDatabase.updateChildren(childUpdates);
    }

    public void addTimeSlotItemList(String userId, TimeSlotItem timeSlotItem) {
        String url = FirebaseConstants.timeSlotItemListURL(userId, false);
        String key = mDatabase.child(url).push().getKey();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(url + "/" + key, timeSlotItem);

        mDatabase.updateChildren(childUpdates);
    }

    /**
     * restore TimeSlot list
     */
    public Collection<TimeSlotItem> getTimeSlotItemList(String userId) throws IOException {
        Collection<TimeSlotItem> timeSlotItems = new ArrayList<>();

        mDatabase.child(FirebaseConstants.timeSlotItemListURL(userId, false))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            timeSlotItems.add(dataSnapshot.getValue(TimeSlotItem.class));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        return timeSlotItems;
    }

    /**
     * Backup TimeSlot list
     *
     * @return the count saved successfully.
     */
    public int addTimeSlotItemList(String userId, List<TimeSlot> timeSlotItems) throws IOException {
        int ii = 0; // count successful save.

        // add a TimeSlotList to Firebase
        addTimeSlotList(userId);

        if (timeSlotItems != null && timeSlotItems.size() > 0) {
            // clear TimeSlotItems on Firebase
            deleteTimeSlotItems(userId);

            LOGD(TAG, "successful clear TimeSlotItems on Firebase.");

            for (TimeSlot tsItem : timeSlotItems) {
                addTimeSlotItemList(userId, ModelConverter.TimeSlotToFirebaseTimeSlotItem(tsItem));
                ii++;
            }

            if (ii == timeSlotItems.size()) {
                LOGD(TAG, "successful backup all TimeSlotItem on Firebase.");
            } else {
                LOGD(TAG, "fail backup all TimeSlotItem on Firebase.");
            }
        }

        return ii;
    }
}

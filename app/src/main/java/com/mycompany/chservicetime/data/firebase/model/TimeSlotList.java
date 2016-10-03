package com.mycompany.chservicetime.data.firebase.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;
import com.mycompany.chservicetime.data.firebase.FirebaseConstants;
import com.mycompany.chservicetime.data.firebase.FirebaseUtils;

import java.util.HashMap;

/**
 * Created by szhx on 3/24/2016.
 */
public class TimeSlotList {
    private String listName;
    private String owner;
    private HashMap<String, Object> timestampLastChanged;
    private HashMap<String, Object> timestampCreated;

    /**
     * Required public constructor
     */
    public TimeSlotList() {
    }

    /**
     * Use this constructor to create new TimeSlotLists.
     * Takes TimeSlot list listName and owner. Set's the last changed time to what is stored in
     * ServerValue.TIMESTAMP
     *
     * @param listName
     * @param owner
     */
    public TimeSlotList(String listName, String owner, HashMap<String, Object> timestampCreated) {
        this.listName = listName;
        this.owner = owner;
        this.timestampCreated = timestampCreated;
        this.timestampLastChanged = FirebaseUtils.getTimestampNowObject();
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    public void setTimestampCreated(
            HashMap<String, Object> timestampCreated) {
        this.timestampCreated = timestampCreated;
    }

    public HashMap<String, Object> getTimestampLastChanged() {
        return timestampLastChanged;
    }

    public void setTimestampLastChanged(
            HashMap<String, Object> timestampLastChanged) {
        this.timestampLastChanged = timestampLastChanged;
    }

    @Exclude
    public long getTimestampLastChangedLong() {
        return (long) timestampLastChanged.get(FirebaseConstants.FIREBASE_PROPERTY_TIMESTAMP);
    }

    @Exclude
    public long getTimestampCreatedLong() {
        return (long) timestampLastChanged.get(FirebaseConstants.FIREBASE_PROPERTY_TIMESTAMP);
    }

    public void setTimestampLastChangedToNow() {
        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put(FirebaseConstants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.timestampLastChanged = timestampNowObject;
    }
}

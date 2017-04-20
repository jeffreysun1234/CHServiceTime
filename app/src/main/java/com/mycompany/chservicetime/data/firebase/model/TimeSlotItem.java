package com.mycompany.chservicetime.data.firebase.model;

/**
 * Created by szhx on 3/24/2016.
 */
public class TimeSlotItem {
    private String timeSlotId;
    private String name;
    private String description;
    private int beginTimeHour;
    private int beginTimeMinute;
    private int endTimeHour;
    private int endTimeMinute;
    private String days;
    private boolean repeatFlag;
    private boolean activationFlag;
    private String serviceOption;

    public TimeSlotItem() {
    }

    public TimeSlotItem(String timeSlotId, String name, String description,
                        int beginTimeHour, int beginTimeMinute, int endTimeHour, int endTimeMinute,
                        String days, boolean repeatFlag, boolean activationFlag, String serviceOption
    ) {
        this.timeSlotId = timeSlotId;
        this.name = name;
        this.description = description;
        this.beginTimeHour = beginTimeHour;
        this.beginTimeMinute = beginTimeMinute;
        this.endTimeHour = endTimeHour;
        this.endTimeMinute = endTimeMinute;
        this.days = days;
        this.repeatFlag = repeatFlag;
        this.activationFlag = activationFlag;
        this.serviceOption = serviceOption;
    }

    public int getBeginTimeHour() {
        return beginTimeHour;
    }

    public void setBeginTimeHour(int beginTimeHour) {
        this.beginTimeHour = beginTimeHour;
    }

    public int getBeginTimeMinute() {
        return beginTimeMinute;
    }

    public void setBeginTimeMinute(int beginTimeMinute) {
        this.beginTimeMinute = beginTimeMinute;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public int getEndTimeHour() {
        return endTimeHour;
    }

    public void setEndTimeHour(int endTimeHour) {
        this.endTimeHour = endTimeHour;
    }

    public int getEndTimeMinute() {
        return endTimeMinute;
    }

    public void setEndTimeMinute(int endTimeMinute) {
        this.endTimeMinute = endTimeMinute;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRepeatFlag() {
        return repeatFlag;
    }

    public void setRepeatFlag(boolean repeatFlag) {
        this.repeatFlag = repeatFlag;
    }

    public boolean isActivationFlag() {
        return activationFlag;
    }

    public void setActivationFlag(boolean activationFlag) {
        this.activationFlag = activationFlag;
    }

    public String getTimeSlotId() {
        return timeSlotId;
    }

    public void setTimeSlotId(String timeSlotId) {
        this.timeSlotId = timeSlotId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServiceOption() {
        return serviceOption;
    }

    public void setServiceOption(String serviceOption) {
        this.serviceOption = serviceOption;
    }
}

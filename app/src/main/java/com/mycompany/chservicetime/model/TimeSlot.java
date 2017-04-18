package com.mycompany.chservicetime.model;

import com.google.auto.value.AutoValue;
import com.mycompany.chservicetime.data.source.local.DateAdapter;
import com.squareup.sqldelight.EnumColumnAdapter;

import java.util.Calendar;
import java.util.UUID;

/**
 * Created by szhx on 12/13/2015.
 */
@AutoValue
public abstract class TimeSlot implements TimeSlotModel {

    public enum ServiceOption {
        NORMAL, VIBRATION, MUTE
    }

    private static final DateAdapter DATE_ADAPTER = new DateAdapter();
    private static final EnumColumnAdapter<ServiceOption> SERVICE_OPTION_ADAPTER = EnumColumnAdapter.create(ServiceOption.class);

    public static int INVALID_ID = -1;

    public static final Factory<TimeSlot> FACTORY = new Factory<>(
            (id, name, description,
             beginTimeHour, beginTimeMinute, endTimeHour, endTimeMinute, days,
             repeatFlag, activationFlag, serviceOption, updateStamp)
                    -> TimeSlot.builder()
                    ._id(id)
                    .name(name)
                    .description(description)
                    .begin_time_hour(beginTimeHour)
                    .begin_time_minute(beginTimeMinute)
                    .end_time_hour(endTimeHour)
                    .end_time_minute(endTimeMinute)
                    .days(days)
                    .repeat_flag(repeatFlag)
                    .activation_flag(activationFlag)
                    .service_option(serviceOption)
                    .build(), SERVICE_OPTION_ADAPTER, DATE_ADAPTER);

    public static final Mapper<TimeSlot> MAPPER = new Mapper<>(FACTORY);

    public static Marshal getMarshal(TimeSlot timeSlot) {
        return FACTORY.marshal(timeSlot);
    }

    public static Builder builder() {
        return new AutoValue_TimeSlot.Builder()
                ._id(UUID.randomUUID().toString())
                .activation_flag(false)
                .update_timestamp(Calendar.getInstance());
    }

    /**
     * creating slightly altered instances9
     */
    public TimeSlot withName(String name) {
        return toBuilder().name(name).build();
    }

    public TimeSlot with_Id(String id) {
        return toBuilder()._id(id).build();
    }

    /**
     * Initialize a builder to the same property values as an existing value instance
     */
    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder _id(String value);

        public abstract Builder name(String value);

        public abstract Builder description(String value);

        public abstract Builder begin_time_hour(int value);

        public abstract Builder begin_time_minute(int value);

        public abstract Builder end_time_hour(int value);

        public abstract Builder end_time_minute(int value);

        public abstract Builder days(String value);

        public abstract Builder repeat_flag(Boolean value);

        public abstract Builder activation_flag(Boolean value);

        public abstract Builder service_option(ServiceOption value);

        public abstract Builder update_timestamp(Calendar value);

        public abstract TimeSlot build();
    }

    /*** Custom methods ***/

    /**
     * Use this constructor to create a new TimeSlot with default values.
     */
    public static TimeSlot createTimeSlot(String name, String description,
                                          Integer beginTimeHour, Integer beginTimeMinute,
                                          Integer endTimeHour, Integer endTimeMinute,
                                          String days, Boolean repeatFlag,
                                          ServiceOption serviceOption) {
        return createTimeSlot(UUID.randomUUID().toString(), name, description,
                beginTimeHour, beginTimeMinute, endTimeHour, endTimeMinute, days,
                repeatFlag, false, serviceOption);
    }

    /**
     * Use this constructor to specify a TimeSlot with all properties.
     * update_timestamp is not included in the parameter list because it is assigned by App.
     */
    public static TimeSlot createTimeSlot(String id, String name, String description,
                                          Integer beginTimeHour, Integer beginTimeMinute,
                                          Integer endTimeHour, Integer endTimeMinute,
                                          String days, Boolean repeatFlag, Boolean activationFlag,
                                          ServiceOption serviceOption) {
        return TimeSlot.FACTORY.creator.create(id, name, description,
                beginTimeHour, beginTimeMinute, endTimeHour, endTimeMinute, days,
                repeatFlag, activationFlag, serviceOption, null);
    }

    /***
     * Custom methods
     ***/

//    public boolean isEmpty() {
//        return Strings.isNullOrEmpty(name());
//    }

    /**
     * exclude the update_timestamp field
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof TimeSlot) {
            TimeSlot that = (TimeSlot) o;
            return (this._id().equals(that._id()))
                    && (this.name().equals(that.name()))
                    && ((this.description() == null) ? (that.description() == null) : this.description().equals(that.description()))
                    && (this.begin_time_hour() == that.begin_time_hour())
                    && (this.begin_time_minute() == that.begin_time_minute())
                    && (this.end_time_hour() == that.end_time_hour())
                    && (this.end_time_minute() == that.end_time_minute())
                    && (this.days().equals(that.days()))
                    && ((this.repeat_flag() == null) ? (that.repeat_flag() == null) : this.repeat_flag().equals(that.repeat_flag()))
                    && ((this.activation_flag() == null) ? (that.activation_flag() == null) : this.activation_flag().equals(that.activation_flag()))
                    && ((this.service_option() == null) ? (that.service_option() == null) : this.service_option().equals(that.service_option()));
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this._id().hashCode();
        h *= 1000003;
        h ^= this.name().hashCode();
        h *= 1000003;
        h ^= (description() == null) ? 0 : this.description().hashCode();
        h *= 1000003;
        h ^= this.begin_time_hour();
        h *= 1000003;
        h ^= this.begin_time_minute();
        h *= 1000003;
        h ^= this.end_time_hour();
        h *= 1000003;
        h ^= this.end_time_minute();
        h *= 1000003;
        h ^= this.days().hashCode();
        h *= 1000003;
        h ^= (repeat_flag() == null) ? 0 : this.repeat_flag().hashCode();
        h *= 1000003;
        h ^= (activation_flag() == null) ? 0 : this.activation_flag().hashCode();
        h *= 1000003;
        h ^= (service_option() == null) ? 0 : this.service_option().hashCode();
        return h;
    }

}

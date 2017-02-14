package com.mycompany.chservicetime.data.source.local;

import android.net.Uri;
import android.provider.BaseColumns;

import com.mycompany.chservicetime.util.ParserUtils;

import java.util.UUID;

/**
 * Contract class for interacting with {@link CHServiceTimeProvider}. Unless otherwise noted, all
 * time-based fields are milliseconds since epoch and can be compared against
 * {@link System#currentTimeMillis()}.
 * <p/>
 * The backing {@link android.content.ContentProvider} assumes that {@link Uri}
 * are generated using stronger {@link String} identifiers, instead of
 * {@code int} {@link BaseColumns#_ID} values, which are prone to shuffle during
 * sync.
 */
public final class CHServiceTimeContract {

    public static final String CONTENT_TYPE_APP_BASE = "com.mycompany.chservicetime.";

    public static final String CONTENT_TYPE_BASE = "vnd.android.cursor.dir/vnd."
            + CONTENT_TYPE_APP_BASE;

    public static final String CONTENT_ITEM_TYPE_BASE = "vnd.android.cursor.item/vnd."
            + CONTENT_TYPE_APP_BASE;

    public interface SyncColumns {

        // Last time this entry was updated or synchronized.
        String UPDATED_TIMESTAMP = "updated_timestamp";
    }

    interface TimeSlotsColumns {

        // Unique string identifying this slot of time.
        String TIME_SLOT_ID = "time_slot_id";
        String NAME = "name";
        String DESCRIPTION = "description";
        String BEGIN_TIME_HOUR = "begin_time_hour";
        String BEGIN_TIME_MINUTE = "begin_time_minute";
        String END_TIME_HOUR = "end_time_hour";
        String END_TIME_MINUTE = "end_time_minute";
        // Days in a week, on which the time slot is used.
        String DAYS = "days";
        // Flag to indicate if the time slot needs to be repeated based on a week.
        String REPEAT_FLAG = "repeat_flag";
        // Flag to indicate if the time slot is activated.
        String ACTIVATION_FLAG = "activation_flag";
        // Key for app service.
        String APP_SERVICE_ID = "app_service_id";
    }

    interface AppServices {

        // Unique string identifying this app service.
        String APP_SERVICE_ID = "app_service_id";
        String NAME = "name";
        String DESCRIPTION = "description";
        // Flag to indicate if the app service is activated.
        String ACTIVATION_FLAG = "activation_flag";
    }

    public static final String CONTENT_AUTHORITY = "com.mycompany.chservicetime";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String PATH_TIME_SLOTS = "time_slots";

    private static final String PATH_APP_SERVICES = "app_services";

    public static final String[] TOP_LEVEL_PATHS = {
            PATH_TIME_SLOTS,
            PATH_APP_SERVICES
    };

    public static final String[] USER_DATA_RELATED_PATHS = {
            // TODO: for what is the code?
            //PATH_SESSIONS,
            //PATH_MY_SCHEDULE
    };

    public static String makeContentType(String id) {
        if (id != null) {
            return CONTENT_TYPE_BASE + id;
        } else {
            return null;
        }
    }

    /**
     * @param id is usually the name of a table.
     * @return
     */
    public static String makeContentItemType(String id) {
        if (id != null) {
            return CONTENT_ITEM_TYPE_BASE + id;
        } else {
            return null;
        }
    }

    public static class TimeSlots implements TimeSlotsColumns, BaseColumns, SyncColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TIME_SLOTS).build();

        public static final String CONTENT_TYPE_ID = "time_slot";

        /**
         * Build {@link Uri} that references all time slots.
         */
        public static Uri buildTimeSlotsUri() {
            return CONTENT_URI;
        }

        /**
         * Build {@link Uri} for requested {@link #TIME_SLOT_ID}.
         */
        public static Uri buildTimeSlotUri(String timeSlotId) {
            return CONTENT_URI.buildUpon().appendPath(timeSlotId).build();
        }

        /**
         * Read {@link #TIME_SLOT_ID} from {@link TimeSlots} {@link Uri}.
         */
        public static String getTimeSlotlId(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        /**
         * Generate a {@link #TIME_SLOT_ID} that will always match the requested
         * {@link TimeSlots} details.
         */
        public static String generateTimeSlotId() {
            return ParserUtils.sanitizeId(UUID.randomUUID().toString());
        }

        /**
         * Default "ORDER BY" clause.
         */
        public static final String DEFAULT_ORDER = BaseColumns._ID + " ASC";

        // Used for calculating time slots.
        public static final String ORDER_BY_TIME = BEGIN_TIME_HOUR + " ASC, "
                + BEGIN_TIME_MINUTE + " ASC, " + END_TIME_HOUR + " ASC, "
                + END_TIME_MINUTE + " ASC";

        public static final String[] DEFAULT_PROJECTION = new String[]{
                BaseColumns._ID,
                NAME,
                DESCRIPTION,
                TIME_SLOT_ID,
                BEGIN_TIME_HOUR,
                BEGIN_TIME_MINUTE,
                END_TIME_HOUR,
                END_TIME_MINUTE,
                REPEAT_FLAG,
                ACTIVATION_FLAG,
                DAYS,
                UPDATED_TIMESTAMP
        };
    }

    private CHServiceTimeContract() {
    }
}

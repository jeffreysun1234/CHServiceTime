package com.mycompany.chservicetime.data.firebase;

/**
 * Created by szhx on 3/23/2016.
 */
public class FirebaseConstants {
    /**
     * Constants related to locations in Firebase, such as the name of the node
     * where user lists are stored (ie "userLists")
     */
    public static final String FIREBASE_LOCATION_TIMESLOT_ITEMS = "timeSlotItems";
    public static final String FIREBASE_LOCATION_USERS = "users";
    public static final String FIREBASE_LOCATION_TIMESLOT_LISTS = "timeSlotLists";
    public static final String FIREBASE_LOCATION_UID_MAPPINGS = "uidMappings";
    public static final String FIREBASE_LOCATION_OWNER_MAPPINGS = "ownerMappings";
    //TODO: a temporary name
    public static final String FIREBASE_LOCATION_DEVICE_ID = "phoneIMEI";

    /**
     * Constants for Firebase object properties
     */
    public static final String FIREBASE_PROPERTY_SERVICE_FLAG = "serviceFlag";
    public static final String FIREBASE_PROPERTY_TIMESLOT_ID = "timeSlotId";
    public static final String FIREBASE_PROPERTY_LIST_NAME = "listName";
    public static final String FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED = "timestampLastChanged";
    public static final String FIREBASE_PROPERTY_TIMESTAMP = "timestamp";
    public static final String FIREBASE_PROPERTY_ITEM_NAME = "name";
    public static final String FIREBASE_PROPERTY_EMAIL = "email";
    public static final String FIREBASE_PROPERTY_USER_HAS_LOGGED_IN_WITH_PASSWORD =
            "hasLoggedInWithPassword";

    /**
     * Constants for Firebase login
     */
    public static final String PASSWORD_PROVIDER = "password";
    public static final String GOOGLE_PROVIDER = "google";
    public static final String PROVIDER_DATA_DISPLAY_NAME = "displayName";

    /**
     * Constants for Firebase URL
     */
    public static final String FIREBASE_URL = "https://chservicetime-6ac05.firebaseio.com/";
    public static final String FIREBASE_URL_USERS = FIREBASE_URL + "/" + FIREBASE_LOCATION_USERS;
    public static final String FIREBASE_URL_TIMESLOT_LISTS =
            FIREBASE_URL + FIREBASE_LOCATION_TIMESLOT_LISTS;

    /**
     * Constants for the replacement variable in URL
     */
    public static final String PATH_UNIIQUE_ID = "uniqueId";
    public static final String PATH_USER_EMAIL = "userEmail";

    /**
     * Methods for Firebase REST URL
     */
    // example: timeSlotLists/<uid>/phoneIMEI/timeSlotItems.json
    public static String timeSlotItemListRestURL(String userId) {
        return FIREBASE_LOCATION_TIMESLOT_LISTS +
                "/" + userId + "/" +
                FIREBASE_LOCATION_DEVICE_ID + "/" +
                FIREBASE_LOCATION_TIMESLOT_ITEMS + ".json";
    }

    // example: timeSlotLists/<uid>/phoneIMEI.json
    public static String timeSlotListRestURL(String userId) {
        return FIREBASE_LOCATION_TIMESLOT_LISTS +
                "/" + userId + "/" +
                FIREBASE_LOCATION_DEVICE_ID + ".json";
    }
}

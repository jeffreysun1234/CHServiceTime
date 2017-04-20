package com.mycompany.chservicetime.data.firebase;

import com.mycompany.chservicetime.data.firebase.model.TimeSlotItem;
import com.mycompany.chservicetime.data.firebase.model.TimeSlotList;
import com.mycompany.chservicetime.model.ModelConverter;
import com.mycompany.chservicetime.model.TimeSlot;
import com.mycompany.chservicetime.util.CHLog;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.mycompany.chservicetime.util.CHLog.makeLogTag;

/**
 * Created by szhx on 3/24/2016.
 */
public class FirebaseRestDAO {
    public static final String TAG = makeLogTag("FirebaseRestDAO");

    public FirebaseEndpointInterface mService;

    private FirebaseRestDAO(String baseUrl) {
        if (mService == null) {
            /* build a retrofit instance */
            Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create());

            // Log Http request and response information
            if (CHLog.getLogger() != null) {
                // set logging
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                // set your desired log level
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);

                OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
                httpClient.addInterceptor(logging);

                retrofitBuilder.client(httpClient.build());
            }

            Retrofit retrofit = retrofitBuilder.build();

            /* get the interface of restful service */
            mService = retrofit.create(FirebaseEndpointInterface.class);
        }
    }

    /**
     * create a default FirebaseRestDAO with the firebase url of the app.
     *
     * @return
     */
    public static FirebaseRestDAO create() {
        return create(FirebaseConstants.FIREBASE_URL);
    }

    public static FirebaseRestDAO create(String baseUrl) {
        return new FirebaseRestDAO(baseUrl);
    }

    /**
     * Add new TimeSlot list
     */
    public TimeSlotList addTimeSlotList(String userId, String authToken) throws
            IOException {
        /* build a TimeSlot list */
        TimeSlotList newTimeSlotList = new TimeSlotList("My List", userId,
                FirebaseUtils.getTimestampNowObject());

        /* access firebase database */
        Response<TimeSlotList> response = mService.addTimeSlotList(
                FirebaseConstants.timeSlotListRestURL(userId), newTimeSlotList,
                authToken).execute();
        if (response.isSuccessful()) {
            return response.body();
        } else {
            return null;
        }
    }

    /**
     * restore TimeSlot list
     */
    public Collection<TimeSlotItem> restoreTimeSlotItemList(String userId,
                                                            String authToken)
            throws IOException {
        String encodeUserId = FirebaseUtils.encodeEmail(userId);
        Response<HashMap<String, TimeSlotItem>> response = mService
                .getTimeSlotItemList(FirebaseConstants.timeSlotItemListRestURL(encodeUserId),
                        authToken).execute();

        if (response.isSuccessful()) {
            HashMap<String, TimeSlotItem> body = response.body();
            if (body != null && body.values().size() > 0) {
                return body.values();
            }
        }

        return null;
    }

    /**
     * Backup TimeSlot list
     *
     * @return the count saved successfully.
     */
    public int backupTimeSlotItemList(String userId, String authToken,
                                      List<TimeSlot> timeSlotItems) throws IOException {

        int ii = 0; // count successful save.

        String encodeUserId = FirebaseUtils.encodeEmail(userId);

        // add a TimeSlotList to Firebase
        addTimeSlotList(encodeUserId, authToken);

        if (timeSlotItems != null && timeSlotItems.size() > 0) {
            // clear TimeSlotItems on Firebase
            Response<Object> response = mService.deleteTimeSlotItems(
                    FirebaseConstants.timeSlotItemListRestURL(encodeUserId), authToken)
                    .execute();
            if (response.isSuccessful()) {
                CHLog.d(TAG, "successful clear TimeSlotItems on Firebase.");

                for (TimeSlot tsItem : timeSlotItems) {
                    // save to Firebase
                    Response<HashMap<String, String>> message = mService.addTimeSlotItemList(
                            FirebaseConstants.timeSlotItemListRestURL(encodeUserId),
                            ModelConverter.TimeSlotToFirebaseTimeSlotItem(tsItem),
                            authToken)
                            .execute();
                    if (message.isSuccessful())
                        ii++;
                }

                if (ii == timeSlotItems.size()) {
                    CHLog.d(TAG, "successful backup all TimeSlotItem on Firebase.");
                } else {
                    CHLog.d(TAG, "fail backup all TimeSlotItem on Firebase.");
                }

            } else {
                CHLog.d(TAG, "fail to clear TimeSlotItems on Firebase.");
            }
        }

        return ii;
    }

    public boolean createNode(String userId, String authToken) throws IOException {
        // Create timeSlotList node
        HashMap<String, Object> userIdMap = new HashMap<>();
        userIdMap.put(userId, "");
        HashMap<String, Object> timeSlotListMap = new HashMap<>();
        timeSlotListMap.put(FirebaseConstants.FIREBASE_LOCATION_TIMESLOT_LISTS, userIdMap);
        Response<String> resp = mService.createNode(
                timeSlotListMap,
                authToken).execute();
        if (resp.isSuccessful()) {
            CHLog.d(TAG, "successful create node path.");
            return true;
        } else {
            CHLog.d(TAG, "fail to create node path.");
            return false;
        }
    }
}

package com.mycompany.chservicetime.data.firebase;

import android.content.Context;
import android.util.Config;

import com.mycompany.chservicetime.data.firebase.model.TimeSlotItem;
import com.mycompany.chservicetime.data.firebase.model.TimeSlotList;
import com.mycompany.chservicetime.util.LogUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.mycompany.chservicetime.util.LogUtils.LOGD;
import static com.mycompany.chservicetime.util.LogUtils.makeLogTag;

/**
 * Created by szhx on 3/24/2016.
 */
public class FirebaseRestDAO {
    public static final String TAG = makeLogTag("FirebaseRestDAO");

    public FirebaseEndpointInterface mService;

    private static Context mContext;

    private FirebaseRestDAO(String baseUrl) {
        if (mService == null) {
            /* build a retrofit instance */
            Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create());

            // Log Http request and response information
            if (LogUtils.LOGGING_ENABLED) {
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
    public TimeSlotList addTimeSlotList(String encodedUserEmail, String authToken) throws
            IOException {
        /* build a TimeSlot list */
        TimeSlotList newTimeSlotList = new TimeSlotList("My List", encodedUserEmail,
                FirebaseUtils.getTimestampNowObject());

        /* access firebase database */
        Response<TimeSlotList> response = mService.addTimeSlotList(
                FirebaseConstants.timeSlotListRestURL(encodedUserEmail), newTimeSlotList,
                authToken).execute();
        if (response.isSuccessful()) {
            return (TimeSlotList) response.body();
        } else {
            return null;
        }
    }

    /**
     * restore TimeSlot list
     */
    public Collection<TimeSlotItem> restoreTimeSlotItemList(String encodedUserEmail,
                                                            String authToken) throws
            IOException {
        Response<HashMap<String, TimeSlotItem>> response = mService
                .getTimeSlotItemList(FirebaseConstants.timeSlotItemListRestURL(encodedUserEmail),
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
    public int backupTimeSlotItemList(String encodedUserEmail, String authToken,
                                      ArrayList<TimeSlotItem> timeSlotItems) throws IOException {

        int ii = 0; // count successful save.

        // add a TimeSlotList to Firebase
        addTimeSlotList(encodedUserEmail, authToken);

        if (timeSlotItems != null && timeSlotItems.size() > 0) {
            // clear TimeSlotItems on Firebase
            Response<Object> response = mService.deleteTimeSlotItems(
                    FirebaseConstants.timeSlotItemListRestURL(encodedUserEmail), authToken)
                    .execute();
            if (response.isSuccessful()) {
                LOGD(TAG, "successful clear TimeSlotItems on Firebase.");

                for (TimeSlotItem tsItem : timeSlotItems) {
                    // save to Firebase
                    Response<HashMap<String, String>> message = mService.addTimeSlotItemList(
                            FirebaseConstants.timeSlotItemListRestURL(encodedUserEmail), tsItem, authToken)
                            .execute();
                    if (message.isSuccessful())
                        ii++;
                }

                if (ii == timeSlotItems.size()) {
                    LOGD(TAG, "successful backup all TimeSlotItem on Firebase.");
                } else {
                    LOGD(TAG, "fail backup all TimeSlotItem on Firebase.");
                }

            } else {
                LOGD(TAG, "fail to clear TimeSlotItems on Firebase.");
            }

        }

        return ii;
    }
}

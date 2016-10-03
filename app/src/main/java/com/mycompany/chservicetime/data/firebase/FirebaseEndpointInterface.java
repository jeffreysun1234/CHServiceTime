package com.mycompany.chservicetime.data.firebase;

import com.mycompany.chservicetime.data.firebase.model.TimeSlotItem;
import com.mycompany.chservicetime.data.firebase.model.TimeSlotList;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by szhx on 3/23/2016.
 */
public interface FirebaseEndpointInterface {

    /**
     * Only for test
     *
     * @return
     */
    @GET("example.json")
    Call<String> getTestString();

    /**
     * FirebaseConstants.timeSlotListRestURL();
     */
    @PUT
    Call<TimeSlotList> addTimeSlotList(@Url String url,
                                       @Body TimeSlotList timeSlotList,
                                       @Query("auth") String auth);

    /**
     * FirebaseConstants.timeSlotItemListRestURL();
     */
    @DELETE
    Call<Object> deleteTimeSlotItems(@Url String url,
                                     @Query("auth") String auth);

    /**
     * FirebaseConstants.timeSlotItemListRestURL();
     */
    @POST
    Call<HashMap<String, String>> addTimeSlotItemList(@Url String url,
                                                      @Body TimeSlotItem timeSlotItem,
                                                      @Query("auth") String auth);

    /**
     * FirebaseConstants.timeSlotItemListRestURL();
     */
    @GET
    Call<HashMap<String, TimeSlotItem>> getTimeSlotItemList(@Url String url,
                                                            @Query("auth") String auth);
}

package com.mycompany.chservicetime.data.firebase;

import com.mycompany.chservicetime.data.firebase.model.HttpResult;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by szhx on 1/22/2017.
 */

public interface FirebaseService {

    /**
     * Only for test
     *
     * @return
     */
    @GET("example.json")
    Observable<HttpResult<String>> getTestString();
}

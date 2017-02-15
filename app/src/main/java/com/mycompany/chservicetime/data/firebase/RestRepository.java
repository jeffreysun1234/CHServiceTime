package com.mycompany.chservicetime.data.firebase;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mycompany.chservicetime.data.firebase.model.HttpResult;
import com.mycompany.chservicetime.util.CHLog;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.mycompany.chservicetime.util.CHLog.makeLogTag;

/**
 * Created by szhx on 1/22/2017.
 */
@Singleton
public class RestRepository {
    public static final String TAG = makeLogTag("RestRepository");

    public FirebaseService mService;

    @Nullable
    private static RestRepository INSTANCE = null;

    /**
     * Returns the single instance of this class, creating it if necessary.
     */
    public static RestRepository getInstance(@NonNull String baseUrl) {
        if (INSTANCE == null) {
            INSTANCE = new RestRepository(baseUrl);
        }
        return INSTANCE;
    }

    /**
     * Used to force to create a new instance at next time.
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Inject
    RestRepository(@NonNull String baseUrl) {
        if (mService == null) {
            /* build a retrofit instance */
            Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create());

            // Log Http request and response information
            if (CHLog.getLogger() == null) {
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
            mService = retrofit.create(FirebaseService.class);
        }
    }

    /**
     * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
     *
     * @param <T> Subscriber真正需要的数据类型，也就是Data部分的数据类型
     */
    private class HttpResultFunc<T> implements Func1<HttpResult<T>, T> {

        @Override
        public T call(HttpResult<T> httpResult) {
//            if (httpResult.resultCode() != 0) {
//                throw new Exception("Return code: " + httpResult.resultCode() + ", "
//                        + httpResult.resultMessage());
//            }
            return httpResult.data();
        }
    }

    public void getTestString(Subscriber<String> subscriber) {
        mService.getTestString()
                .map(new HttpResultFunc<String>())
                .subscribeOn(Schedulers.io())
                //.unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

}

package com.mycompany.chservicetime.data.firebase;

import com.mycompany.chservicetime.data.firebase.testUtil.RecordingSubscriber;
import com.mycompany.chservicetime.data.firebase.testUtil.RxJavaPluginsResetRule;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static com.mycompany.chservicetime.data.firebase.RestCallTest.MOCK_SERVER_BASE_URL_PATH;

/**
 * Created by szhx on 1/23/2017.
 */
public class RestRepositoryTest {

    @Rule
    public final MockWebServer server = new MockWebServer();

    @Rule
    public final TestRule pluginsReset = new RxJavaPluginsResetRule();

    @Rule
    public final RecordingSubscriber.Rule subscriberRule = new RecordingSubscriber.Rule();


    private RestRepository mRestRepository;

    @Before
    public void setUp() {
        // Ask the server for its URL. You'll need this to make HTTP requests.
        HttpUrl baseUrl = server.url(MOCK_SERVER_BASE_URL_PATH);

        mRestRepository = RestRepository.getInstance(baseUrl.toString());
    }

    @Test
    @Ignore
    public void bodySuccess200() {
        server.enqueue(new MockResponse().setBody("{\"name\": \"Hi\"}"));

        RecordingSubscriber<String> subscriber = subscriberRule.create();
        mRestRepository.getTestString(subscriber);
        subscriber.assertValue("Hi").assertCompleted();
    }
}
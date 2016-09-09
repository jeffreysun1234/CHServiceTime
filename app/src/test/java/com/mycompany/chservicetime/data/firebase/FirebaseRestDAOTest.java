package com.mycompany.chservicetime.data.firebase;

import com.mycompany.chservicetime.data.firebase.model.TimeSlotItem;
import com.mycompany.chservicetime.data.firebase.model.TimeSlotList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.HttpUrl;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by szhx on 3/31/2016.
 * <p/>
 * This test class is for confirming the test data is correct, and keeping all restful call methods
 * are stable.
 */
public class FirebaseRestDAOTest {

    final static String MOCK_SERVER_BASE_URL_PATH = "/";

    public static MockWebServer mockServer;
    public static FirebaseEndpointInterface mService;

    static FirebaseRestDAO mFirebaseRestDAO;

    static String encodedUserEmail = FirebaseUtils.encodeEmail("a@a.com");
    static String authToken = null;

    @BeforeClass
    public static void setup() throws IOException {
        mockServer = new MockWebServer();
        mockServer.setDispatcher(new MockServerDispatcher(encodedUserEmail));
        mockServer.start();

        // Ask the server for its URL. You'll need this to make HTTP requests.
        HttpUrl baseUrl = mockServer.url(MOCK_SERVER_BASE_URL_PATH);

        mFirebaseRestDAO = FirebaseRestDAO.create(baseUrl.toString());

        mService = mFirebaseRestDAO.mService;
    }

    @AfterClass
    public static void tearDown() throws IOException {
        mockServer.shutdown();
    }

    /**
     * Mock URL+Method and response
     */
    static class MockServerDispatcher extends Dispatcher {
        String userEmailPath;

        public MockServerDispatcher(String userEmailPath) {
            this.userEmailPath = userEmailPath;
        }

        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {

            String timeSlotItemListRestURL = FirebaseConstants.timeSlotItemListRestURL(userEmailPath);
            String timeSlotListRestURL = FirebaseConstants.timeSlotListRestURL(userEmailPath);

            String getTimeSlotItemListURLResponse = "{\"-KE8oUN5U5BGgBMey02U\":{\"beginTimeHour" +
                    "\":17,\"beginTimeMinute\":5,\"days\":\"0100000\",\"endTimeHour\":17," +
                    "\"endTimeMinute\":6,\"name\":\"test\",\"repeatFlag\":true," +
                    "\"serviceFlag\":true,\"timeSlotId\":\"1459378867092\"}," +
                    "\"-KE8oUU0SQYR9RomqBdH\":{\"beginTimeHour\":15,\"beginTimeMinute\":57," +
                    "\"days\":\"1000001\",\"endTimeHour\":16,\"endTimeMinute\":57," +
                    "\"name\":\"work\",\"repeatFlag\":true,\"serviceFlag\":false," +
                    "\"timeSlotId\":\"1459378867117\"}," +
                    "\"-KE8oUttnj3qfm-uO9ZK\":{\"beginTimeHour\":19,\"beginTimeMinute\":17," +
                    "\"days\":\"0001100\",\"endTimeHour\":22,\"endTimeMinute\":17," +
                    "\"name\":\"school\",\"repeatFlag\":true,\"serviceFlag\":false," +
                    "\"timeSlotId\":\"1459379887970\"}}";

            String addTimeSlotItemListURLResponse = "{\"name\":\"-JSOpn9ZC54A4P4RoqVa\"}";

            String addTimeSlotListURLResponse = "{\"listName\":\"My List\",\"owner\":\"a@a," +
                    "com\",\"timestampCreated\":{\"timestamp\":1459512599670}," +
                    "\"timestampLastChanged\":{\"timestamp\":1459512599670}}";

            if (request.getPath().equals(MOCK_SERVER_BASE_URL_PATH + "example.json")) {
                return new MockResponse().setResponseCode(200).setBody("\"Hi\"");
            } else if (request.getPath().equals(MOCK_SERVER_BASE_URL_PATH + timeSlotItemListRestURL)
                    && request.getMethod().equals("GET")) {
                return new MockResponse().setResponseCode(200)
                        .setBody(getTimeSlotItemListURLResponse);
            } else if (request.getPath().equals(MOCK_SERVER_BASE_URL_PATH + timeSlotItemListRestURL)
                    && request.getMethod().equals("POST")) {
                return new MockResponse().setResponseCode(200)
                        .setBody(addTimeSlotItemListURLResponse);
            } else if (request.getPath().equals(MOCK_SERVER_BASE_URL_PATH + timeSlotItemListRestURL)
                    && request.getMethod().equals("DELETE")) {
                return new MockResponse().setResponseCode(200)
                        .setBody("\"{}\"");
            } else if (request.getPath().equals(MOCK_SERVER_BASE_URL_PATH + timeSlotListRestURL)
                    && request.getMethod().equals("PUT")) {
                return new MockResponse().setResponseCode(200)
                        .setBody(addTimeSlotListURLResponse);
            }
            return new MockResponse().setResponseCode(404);
        }
    }

    @Test
    public void http200Sync() throws IOException {
        Response<String> response = mService.getTestString().execute();
        assertThat(response.isSuccessful(), is(true));
        assertThat(response.body(), is(equalTo("Hi")));
    }

    @Test
    public void testAddTimeSlotList() throws Exception {
        TimeSlotList response = mFirebaseRestDAO.addTimeSlotList(encodedUserEmail, authToken);

        assertThat(response, is(notNullValue()));
    }

    @Test
    public void testRestoreTimeSlotItemList() throws Exception {
        Collection<TimeSlotItem> response = mFirebaseRestDAO.restoreTimeSlotItemList(encodedUserEmail, authToken);

        assertThat(response, is(notNullValue()));
        assertThat(response.size(), is(3));
    }

    @Test
    public void testBackupTimeSlotItemList() throws Exception {
        ArrayList<TimeSlotItem> tsItems = new ArrayList<TimeSlotItem>();
        tsItems.add(
                new TimeSlotItem("time_slot_id_1234", "Test", "Test Item", 9, 10, 10, 10, "0011001", false, false));

        int count = mFirebaseRestDAO.backupTimeSlotItemList(encodedUserEmail, authToken, tsItems);

        assertThat(count, is(1));
    }

    @Test
    public void testDeleteTimeSlotItems() throws IOException, InterruptedException {
        Call<Object> message = mService
                .deleteTimeSlotItems(FirebaseConstants.timeSlotItemListRestURL(encodedUserEmail), authToken);

        final AtomicReference<Response<Object>> responseRef = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);
        message.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call,
                                   Response<Object> response) {
                responseRef.set(response);
                latch.countDown();
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

            }
        });
        //assertThat(latch.await(2, SECONDS), is(true));
        latch.await(2, TimeUnit.SECONDS);

        Response<Object> response = responseRef.get();

        assertThat(response.isSuccessful(), is(true));
        //assertThat(response.body(), is(nullValue()));
    }

}

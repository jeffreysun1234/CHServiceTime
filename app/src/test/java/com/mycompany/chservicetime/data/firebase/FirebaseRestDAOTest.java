package com.mycompany.chservicetime.data.firebase;

import com.mycompany.chservicetime.data.firebase.model.TimeSlotItem;
import com.mycompany.chservicetime.data.firebase.model.TimeSlotList;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by szhx on 3/24/2016.
 * <p/>
 * This test class accesses the real Firebase. For running successful, we need to prohibit the
 * security rules on Firebase, and set authToken to null.
 */
public class FirebaseRestDAOTest {

    final static String FIREBASE_SERVER_BASE_URL = "https://chservicetime-6ac05.firebaseio.com/";

    static FirebaseEndpointInterface mService;
    static FirebaseRestDAO mFirebaseRestDAO;

    static String encodedUserEmail = FirebaseUtils.encodeEmail("a@a.com");
    static String authToken = null;

    @BeforeClass
    public static void setupBeforeClass() {
        mFirebaseRestDAO = FirebaseRestDAO.create(FIREBASE_SERVER_BASE_URL);
        mService = mFirebaseRestDAO.mService;
    }

    //@Test
    public void testAddTimeSlotList() throws Exception {
        TimeSlotList response = mFirebaseRestDAO.addTimeSlotList(encodedUserEmail, authToken);

        assertThat(response, is(notNullValue()));
    }

    @Test
    public void testRestoreTimeSlotItemList() throws Exception {
        ArrayList<TimeSlotItem> tsItems = new ArrayList<TimeSlotItem>();
        tsItems.add(
                new TimeSlotItem("time_slot_id_1234", "Test", "Test Item", 9, 10, 10, 10, "0011001", false, false));
        tsItems.add(
                new TimeSlotItem("time_slot_id_4321", "Work", "Work Item", 9, 10, 10, 10, "0011001", false, false));

        int count = mFirebaseRestDAO.backupTimeSlotItemList(encodedUserEmail, authToken, tsItems);
        assertThat(count, is(2));

        Collection<TimeSlotItem> response = mFirebaseRestDAO.restoreTimeSlotItemList(encodedUserEmail, authToken);

        assertThat(response, is(notNullValue()));
        assertThat(response.size(), is(2));
    }

    @Test
    public void testBackupTimeSlotItemList() throws Exception {
        ArrayList<TimeSlotItem> tsItems = new ArrayList<TimeSlotItem>();
        tsItems.add(
                new TimeSlotItem("time_slot_id_1234", "Test", "Test Item", 9, 10, 10, 10, "0011001", false, false));

        int count = mFirebaseRestDAO.backupTimeSlotItemList(encodedUserEmail, authToken, tsItems);

        assertThat(count, is(1));
    }
}

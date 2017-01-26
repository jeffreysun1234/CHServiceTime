package com.mycompany.chservicetime.data.firebase;

import com.google.firebase.database.FirebaseDatabase;
import com.mycompany.chservicetime.model.TimeSlot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by szhx on 1/26/2017.
 */
// TODO: FirebaseDAOTest
public class FirebaseDAOTest {
    FirebaseDatabase database;

    String uid = "test@test.com";

    @Before
    public void setUp() throws Exception {

        //FirebaseApp.initializeApp();

        ArrayList<TimeSlot> tsItems = new ArrayList<TimeSlot>();
        tsItems.add(
                TimeSlot.createTimeSlot("111", "Work", "work time", 9, 0, 17, 0, "0111110", true, false, TimeSlot.ServiceOption.NORMAL));
        tsItems.add(
                TimeSlot.createTimeSlot("222", "Free", "Free time", 19, 0, 6, 0, "0111110", true, false, TimeSlot.ServiceOption.NORMAL));

        database = FirebaseDatabase.getInstance();
    }

    @After
    public void tearDown() throws Exception {


    }

    @Test
    public void addTimeSlotList() throws Exception {
        FirebaseDAO.create().addTimeSlotList(uid);
    }

    @Test
    public void deleteTimeSlotItems() throws Exception {

    }

    @Test
    public void addTimeSlotItemList() throws Exception {

    }

    @Test
    public void getTimeSlotItemList() throws Exception {

    }

    @Test
    public void addTimeSlotItemList1() throws Exception {

    }

}
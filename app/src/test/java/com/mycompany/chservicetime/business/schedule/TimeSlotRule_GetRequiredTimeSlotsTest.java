package com.mycompany.chservicetime.business.schedule;

import com.mycompany.chservicetime.model.TimeSlot;
import com.mycompany.chservicetime.util.CHLog;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TimeSlotRule_GetRequiredTimeSlotsTest {

    private List<TimeSlot> originalTimeSlots;

    @BeforeClass
    public static void runOnceBeforeClass() {
        CHLog.setLogger(CHLog.TESTOUT);
    }

    @AfterClass
    public static void runOnceAfterClass() {
        CHLog.setLogger(null);
    }

    @Before
    public void setUp() throws Exception {
        originalTimeSlots = new ArrayList<TimeSlot>();
        originalTimeSlots.add(TimeSlot.createTimeSlot("111", "Work", "work time",
                23, 0, 1, 30, "0111110", true, true, TimeSlot.ServiceOption.MUTE));
        originalTimeSlots.add(TimeSlot.createTimeSlot("111", "Work", "work time",
                3, 10, 7, 30, "0111110", true, true, TimeSlot.ServiceOption.MUTE));
        originalTimeSlots.add(TimeSlot.createTimeSlot("111", "Work", "work time",
                3, 10, 7, 30, "0111110", true, true, TimeSlot.ServiceOption.VIBRATION));
        originalTimeSlots.add(TimeSlot.createTimeSlot("111", "Work", "work time",
                16, 0, 18, 30, "0111110", true, true, TimeSlot.ServiceOption.VIBRATION));
        originalTimeSlots.add(TimeSlot.createTimeSlot("111", "Work", "work time",
                8, 0, 13, 20, "0111110", true, true, TimeSlot.ServiceOption.MUTE));
        originalTimeSlots.add(TimeSlot.createTimeSlot("111", "Work", "work time",
                11, 11, 13, 13, "0111110", true, false, TimeSlot.ServiceOption.VIBRATION));
        originalTimeSlots.add(TimeSlot.createTimeSlot("111", "Work", "work time",
                22, 22, 3, 3, "0101110", true, true, TimeSlot.ServiceOption.VIBRATION));
        originalTimeSlots.add(TimeSlot.createTimeSlot("111", "Work", "work time",
                11, 10, 12, 30, "0111110", true, true, TimeSlot.ServiceOption.VIBRATION));
        originalTimeSlots.add(TimeSlot.createTimeSlot("111", "Work", "work time",
                21, 0, 1, 0, "0111110", true, true, TimeSlot.ServiceOption.MUTE));
    }

    /**
     * This test includes overnight time sector, valid sort, and filter by day in week.
     */
    @Test
    public void testGetRequiredTimeSlots() throws Exception {
        List<int[]> timeSectors =
                TimeSlotRule.getRequiredTimeSlots(originalTimeSlots, Calendar.TUESDAY, true);

        // expect is
        // [[0, 100], [0, 130], [0, 303], [310, 730], [800, 1320], [1110, 1230], [1600, 1830], [2100, 2500], [2300, 2530]]
        CHLog.d("testGetRequiredTimeSlots", Arrays.deepToString(timeSectors.toArray()));

        assertEquals(Arrays.deepToString(timeSectors.toArray()),
                "[[0, 100, 2], [0, 130, 2], [0, 303, 1], [310, 730, 1], [310, 730, 2], [800, 1320, 2], [1110, 1230, 1], [1600, 1830, 1], [2100, 2500, 2], [2300, 2530, 2]]");
    }
}

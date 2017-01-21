package com.mycompany.chservicetime.business.schedule;

import com.mycompany.chservicetime.model.TimeSlot;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TimeSlotRule_GetRequiredTimeSlotsTest {

    private List<TimeSlot> originalTimeSlots;

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

    @Test
    public void testGetRequiredTimeSlots() throws Exception {
        List<int[]> timeSectors =
                TimeSlotRule.getRequiredTimeSlots(originalTimeSlots, Calendar.TUESDAY, true);

        // expect is
        // [[0, 100], [0, 130], [0, 303], [310, 730], [800, 1320], [1110, 1230], [1600, 1830], [2100, 2500], [2300, 2530]]
        System.out.println(Arrays.deepToString(timeSectors.toArray()));
    }

    // TODO: detail test usecases.
    @Test
    public void filterByActivationFlag_IsTrue() {
        originalTimeSlots = new ArrayList<TimeSlot>();
        originalTimeSlots.add(TimeSlot.createTimeSlot("111", "Work", "work time",
                16, 0, 18, 30, "0111110", true, true, TimeSlot.ServiceOption.NORMAL));
        originalTimeSlots.add(TimeSlot.createTimeSlot("111", "Work", "work time",
                8, 0, 13, 20, "0111110", true, false, TimeSlot.ServiceOption.NORMAL));

        List<int[]> expect = new ArrayList<int[]>();
        expect.add(new int[]{1600, 1830});

        List<int[]> timeSectors =
                TimeSlotRule.getRequiredTimeSlots(originalTimeSlots, Calendar.TUESDAY, true);

        assertEquals(1, timeSectors.size());
        //assertThat(expect, containsInAnyOrder(timeSectors));
        // filterByActivationFlag. true

        // Include overnight time sector.


        // if begin_time < end_time, then end_time + 2400

        // valid sort

        // filter by activationFlag = false

        // filter by day in week
    }
}

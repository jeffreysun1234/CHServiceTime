package com.mycompany.chservicetime.business.schedule;

import android.content.Context;

import com.mycompany.chservicetime.data.source.AppRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by szhx on 8/26/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class TimeSlotRepositoryTest {

    private AppRepository mTimeSlotRepository;

    @Mock
    private Context mContext;

    @Before
    public void setup() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);
    }


    // TODO: business method
    @Test
    public void testGetRequiredTimeSlots() {
//        // Create time slot data.
//        mTimeSlotRepository.createOrUpdateTimeSlot(
//                new TimeSlot(1, ParserUtils.sanitizeId(UUID.randomUUID().toString()),
//                        "Work-Morning", "Work time before noon",
//                        9, 0, 11, 30, "0111110", true, true, System.currentTimeMillis()));
//        mTimeSlotRepository.createOrUpdateTimeSlot(
//                new TimeSlot(3, ParserUtils.sanitizeId(UUID.randomUUID().toString()),
//                        "Test", "time for test",
//                        19, 30, 22, 0, "0110110", false, true, System.currentTimeMillis()));
//        mTimeSlotRepository.createOrUpdateTimeSlot(
//                new TimeSlot(2, ParserUtils.sanitizeId(UUID.randomUUID().toString()),
//                        "Work-Afternoon", "Work time after noon",
//                        13, 0, 17, 0, "0111110", true, true, System.currentTimeMillis()));
//        mTimeSlotRepository.createOrUpdateTimeSlot(
//                new TimeSlot(3, ParserUtils.sanitizeId(UUID.randomUUID().toString()),
//                        "Test", "time for test",
//                        20, 30, 23, 0, "0110110", false, false, System.currentTimeMillis()));
//        mTimeSlotRepository.createOrUpdateTimeSlot(
//                new TimeSlot(3, ParserUtils.sanitizeId(UUID.randomUUID().toString()),
//                        "Test", "time for test",
//                        23, 30, 3, 0, "0110110", false, true, System.currentTimeMillis()));
//
//        ArrayList<int[]> timeSlotList;
//
//        // filter by activatonFlag
//        timeSlotList = mTimeSlotRepository.getRequiredTimeSlots(3, true);
//        System.out.println(Arrays.deepToString(timeSlotList.toArray()));
//
//        // include overnigh TimeSlots
//        assertEquals(5, timeSlotList.size());
//
//        // if begin_time < end_time, then end_time + 2400
//        assertEquals(2700, timeSlotList.get(4)[1]);
//
//        // valid sort
//        assertEquals(1300, timeSlotList.get(2)[0]);
//
//        // filter by activationFlag = false
//        timeSlotList = mTimeSlotRepository.getRequiredTimeSlots(2, false);
//        System.out.println(Arrays.deepToString(timeSlotList.toArray()));
//        assertEquals(1, timeSlotList.size());
//
//        // filter by day in week
//        timeSlotList = mTimeSlotRepository.getRequiredTimeSlots(2, true);
//        System.out.println(Arrays.deepToString(timeSlotList.toArray()));
//        assertEquals(4, timeSlotList.size());
//
//        // overnigh in last day.
//        timeSlotList = mTimeSlotRepository.getRequiredTimeSlots(4, true);
//        System.out.println(Arrays.deepToString(timeSlotList.toArray()));
//        assertEquals(3, timeSlotList.size());
    }

}

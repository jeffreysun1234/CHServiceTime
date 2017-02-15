package com.mycompany.chservicetime.business.schedule;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by szhx on 4/6/2016.
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
public class TimeSlotRule_GetServiceTimeTest {

    @Parameterized.Parameter(0)
    public int currentTime;

    @Parameterized.Parameter(1)
    public long expected;

    @Parameterized.Parameter(2)
    public Integer currentOperation;

    //Declares parameters here
    @Parameterized.Parameters(name = "{index}: ({0} --> {1} --> {2}")
    public static Collection<Object[]> data1() {
        // TimeSectors is {[0, 730],[800, 1320],[1600, 1830],[2100, 2500]}
        return Arrays.asList(new Object[][]{
                {200, 730, ServiceTime.Mute},
                {730, 800, ServiceTime.Normal},
                {1600, 1830, ServiceTime.Mute},
                {2050, 2100, ServiceTime.Normal},
                {2350, 2500, ServiceTime.Mute}
        });
    }

    private List<int[]> originalTimeSectors;

    @Before
    public void setUp() throws Exception {
        originalTimeSectors = new ArrayList<int[]>();
        originalTimeSectors.add(new int[]{0, 330, 2});
        originalTimeSectors.add(new int[]{310, 730, 2});
        originalTimeSectors.add(new int[]{800, 1320, 2});
        originalTimeSectors.add(new int[]{1110, 1230, 2});
        originalTimeSectors.add(new int[]{1600, 1830, 2});
        originalTimeSectors.add(new int[]{2100, 2500, 2});
        // {[0, 730],[800, 1320],[1600, 1830],[2100, 2500]}

    }

    @Test
    public void testGetNextAlarmTime() throws Exception {
        ServiceTime serviceTime = TimeSlotRule.getServiceTime(originalTimeSectors, currentTime);
        assertEquals(expected, serviceTime.nextAlarmTimeInt);
        assertEquals(currentOperation, serviceTime.currentOperation);
    }

}

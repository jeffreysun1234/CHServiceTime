package com.mycompany.chservicetime.business.schedule;

import com.mycompany.chservicetime.util.LogUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * Created by szhx on 4/6/2016.
 */
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest(LogUtils.class)
public class TimeSlotRuleTest {

    @Parameterized.Parameter(0)
    public int currentTime;

    @Parameterized.Parameter(1)
    public long expected;

    @Parameterized.Parameter(2)
    public Integer currentOperation;

    //Declares parameters here
    @Parameterized.Parameters(name = "{index}: ({0} --> {1} --> {2}")
    public static Collection<Object[]> data1() {
        // {[0, 730],[800, 1320],[1600, 1830],[2100, 2500]}
        return Arrays.asList(new Object[][]{
                {200, 730, ServiceTime.Vibrate},
                {730, 800, ServiceTime.Normal},
                {1600, 1830, ServiceTime.Vibrate},
                {2050, 2100, ServiceTime.Normal},
                {2350, 2500, ServiceTime.Vibrate}
        });
    }

    private ArrayList<int[]> originalTimeSectors;

    @Before
    public void setUp() throws Exception {
        originalTimeSectors = new ArrayList<int[]>();
        originalTimeSectors.add(new int[]{0, 330});
        originalTimeSectors.add(new int[]{310, 730});
        originalTimeSectors.add(new int[]{800, 1320});
        originalTimeSectors.add(new int[]{1110, 1230});
        originalTimeSectors.add(new int[]{1600, 1830});
        originalTimeSectors.add(new int[]{2100, 2500});
        // {[0, 730],[800, 1320],[1600, 1830],[2100, 2500]}

        // mock all the static methods in LogUtils
        mockStatic(LogUtils.class);

        //expect( NetworkUtil.getLocalHostname() ).andReturn( "localhost" );
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetNextAlarmTime() throws Exception {
        assertEquals(expected, TimeSlotRule.getServiceTime(originalTimeSectors, currentTime).nextAlarmTimeInt);
        assertEquals(currentOperation, TimeSlotRule.getServiceTime(originalTimeSectors, currentTime).currentOperation);
    }
}

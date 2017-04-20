/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mycompany.chservicetime.data.source;

import com.mycompany.chservicetime.model.TimeSlot;

import java.util.HashMap;
import java.util.Map;

public class MockCursorProvider {

    public static Map<Integer, Object> createNewTimeSlotCursorEntry(TimeSlot timeSlot) {
        Map<Integer, Object> entry = new HashMap<>();
//        entry.put(0, timeSlot.id);
//        entry.put(1, timeSlot.timeSlotId);
//        entry.put(2, timeSlot.name);
//        entry.put(3, timeSlot.description);
//        entry.put(4, timeSlot.beginTimeHour);
//        entry.put(5, timeSlot.beginTimeMinute);
//        entry.put(6, timeSlot.endTimeHour);
//        entry.put(7, timeSlot.endTimeMinute);
//        entry.put(8, timeSlot.days);
//        entry.put(9, timeSlot.repeatFlag ? 1 : 0);
//        entry.put(10, timeSlot.activationFlag ? 1 : 0);
//        entry.put(11, timeSlot.updatedTimestamp);
        return entry;
    }

//    public static TimeSlotMockCursor createActiveTimeSlotCursor() {
//        List<Map<Integer, Object>> entryList = new ArrayList<>();
//        entryList.add(createNewTimeSlotCursorEntry(
//                new TimeSlot(1, ParserUtils.sanitizeId(UUID.randomUUID().toString()),
//                        "Work", "Work time",
//                        9, 0, 17, 0, "0111110", true, true, System.currentTimeMillis())));
//        return new TimeSlotMockCursor(entryList);
//    }
//
//    public static TimeSlotMockCursor createActiveTimeSlotsCursor() {
//        List<Map<Integer, Object>> entryList = new ArrayList<>();
//        entryList.add(createNewTimeSlotCursorEntry(
//                new TimeSlot(1, ParserUtils.sanitizeId(UUID.randomUUID().toString()),
//                        "Work-Morning", "Work time before noon",
//                        9, 0, 11, 30, "0111110", true, true, System.currentTimeMillis())));
//        entryList.add(createNewTimeSlotCursorEntry(
//                new TimeSlot(2, ParserUtils.sanitizeId(UUID.randomUUID().toString()),
//                        "Work-Afternoon", "Work time after noon",
//                        13, 0, 17, 0, "0111110", true, true, System.currentTimeMillis())));
//        return new TimeSlotMockCursor(entryList);
//    }
//
//    public static TimeSlotMockCursor createAllTimeSlotsCursor() {
//        List<Map<Integer, Object>> entryList = new ArrayList<>();
//        entryList.add(createNewTimeSlotCursorEntry(
//                new TimeSlot(1, ParserUtils.sanitizeId(UUID.randomUUID().toString()),
//                        "Work-Morning", "Work time before noon",
//                        9, 0, 11, 30, "0111110", true, true, System.currentTimeMillis())));
//        entryList.add(createNewTimeSlotCursorEntry(
//                new TimeSlot(2, ParserUtils.sanitizeId(UUID.randomUUID().toString()),
//                        "Work-Afternoon", "Work time after noon",
//                        13, 0, 17, 0, "0111110", true, true, System.currentTimeMillis())));
//        entryList.add(createNewTimeSlotCursorEntry(
//                new TimeSlot(3, ParserUtils.sanitizeId(UUID.randomUUID().toString()),
//                        "Test", "time for test",
//                        9, 30, 17, 0, "0110110", false, false, System.currentTimeMillis())));
//        return new TimeSlotMockCursor(entryList);
//    }
//
//    public static TimeSlotMockCursor createAllTimeSlotsCursorFromList(List<TimeSlot> timeSlots) {
//        List<Map<Integer, Object>> entryList = new ArrayList<>();
//
//        if (timeSlots != null) {
//            for (TimeSlot vTimeSlot : timeSlots) {
//                entryList.add(createNewTimeSlotCursorEntry(vTimeSlot));
//            }
//        }
//
//        return new TimeSlotMockCursor(entryList);
//    }
//
//    public static TimeSlotMockCursor createEmptyTimeSlotsCursor() {
//        List<Map<Integer, Object>> entryList = new ArrayList<>();
//        return new TimeSlotMockCursor(entryList);
//    }
//
//    public static class TimeSlotMockCursor extends MockCursor {
//        public Map<Integer, Object> entry;
//        public int cursorIndex = -1;
//        public List<Map<Integer, Object>> entryList;
//        public Map<String, Integer> columnIndexes;
//
//        {
//            columnIndexes = new HashMap<>();
//            columnIndexes.put(TimeSlots._ID, 0);
//            columnIndexes.put(TimeSlots.TIME_SLOT_ID, 1);
//            columnIndexes.put(TimeSlots.NAME, 2);
//            columnIndexes.put(TimeSlots.DESCRIPTION, 3);
//            columnIndexes.put(TimeSlots.BEGIN_TIME_HOUR, 4);
//            columnIndexes.put(TimeSlots.BEGIN_TIME_MINUTE, 5);
//            columnIndexes.put(TimeSlots.END_TIME_HOUR, 6);
//            columnIndexes.put(TimeSlots.END_TIME_MINUTE, 7);
//            columnIndexes.put(TimeSlots.DAYS, 8);
//            columnIndexes.put(TimeSlots.REPEAT_FLAG, 9);
//            columnIndexes.put(TimeSlots.ACTIVATION_FLAG, 10);
//            columnIndexes.put(TimeSlots.UPDATED_TIMESTAMP, 11);
//        }
//
//        public TimeSlotMockCursor(List<Map<Integer, Object>> entryList) {
//            this.entryList = entryList;
//        }
//
//        @Override
//        public int getCount() {
//            return entryList.size();
//        }
//
//        @Override
//        public String getString(int columnIndex) {
//            return getValueString(columnIndex);
//        }
//
//        @Override
//        public float getFloat(int columnIndex) {
//            return Float.parseFloat(getValueString(columnIndex));
//        }
//
//        @Override
//        public int getInt(int columnIndex) {
//            return getValueInt(columnIndex);
//        }
//
//        private String getValueString(int columnIndex) {
//            entry = entryList.get(cursorIndex);
//            String value = (String) entry.get(columnIndex);
//            return value;
//        }
//
//        private int getValueInt(int columnIndex) {
//            entry = entryList.get(cursorIndex);
//            int value = (int) entry.get(columnIndex);
//            return value;
//        }
//
//        @Override
//        public int getColumnIndex(String columnName) {
//            return Integer.valueOf(columnIndexes.get(columnName));
//        }
//
//        @Override
//        public int getColumnIndexOrThrow(String columnName) {
//            return Integer.valueOf(columnIndexes.get(columnName));
//        }
//
//        @Override
//        public boolean moveToFirst() {
//            return entryList.size() > 0;
//        }
//
//        @Override
//        public boolean moveToLast() {
//            return cursorIndex < entryList.size();
//        }
//
//        @Override
//        public boolean moveToNext() {
//            cursorIndex++;
//            return cursorIndex < entryList.size();
//        }
//
//        @Override
//        public boolean isAfterLast() {
//            return super.isAfterLast();
//        }
//    }
}

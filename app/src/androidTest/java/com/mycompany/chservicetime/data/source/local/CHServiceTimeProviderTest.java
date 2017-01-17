package com.mycompany.chservicetime.data.source.local;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import com.mycompany.chservicetime.data.source.local.CHServiceTimeContract.TimeSlots;
import com.mycompany.chservicetime.data.source.local.CHServiceTimeDatabase.Tables;
import com.mycompany.chservicetime.data.source.local.helper.TimeSlotContentValues;
import com.mycompany.chservicetime.model.ColumnIndexCache;
import com.mycompany.chservicetime.model.ModelConverter;
import com.mycompany.chservicetime.model.TimeSlot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by szhx on 8/18/2016.
 */
@RunWith(AndroidJUnit4.class)
public class CHServiceTimeProviderTest extends ProviderTestCase2<CHServiceTimeProvider> {

    // A URI that the provider does not offer, for testing error handling.
    private static final Uri INVALID_URI = Uri.withAppendedPath(TimeSlots.CONTENT_URI, "invalid");

    // Contains a reference to the mocked content resolver for the provider under test.
    private MockContentResolver mMockResolver;

    // Contains an SQLite database, used as test data
    private SQLiteDatabase mDb;

    // Contains the test data, as an array of NoteInfo instances.
    private final TimeSlot[] TEST_TIME_SLOTS = {
            TimeSlot.createTimeSlot("111", "Work", "work time", 9, 0, 17, 0, "0111110", true, false, TimeSlot.ServiceOption.NORMAL),
            TimeSlot.createTimeSlot("222", "School", "school time", 8, 30, 15, 0, "0111110", true, false, TimeSlot.ServiceOption.MUTE),
            TimeSlot.createTimeSlot("333", "Test", "test", 6, 20, 12, 22, "1100011", true, false, TimeSlot.ServiceOption.MUTE)
    };

    /**
     * Constructor for the test case class.
     * Calls the super constructor with the class name of the provider under test and the
     * authority name of the provider.
     */
    public CHServiceTimeProviderTest() {
        super(CHServiceTimeProvider.class, CHServiceTimeContract.CONTENT_AUTHORITY);
    }

    /*
     * Sets up the test environment before each test method. Creates a mock content resolver,
     * gets the provider under test, and creates a new database for the provider.
     */
    @Before
    public void setUp() throws Exception {
        setContext(InstrumentationRegistry.getTargetContext());
        super.setUp();

        // Gets the resolver for this test.
        mMockResolver = getMockContentResolver();

        /*
         * Gets a handle to the database underlying the provider. Gets the provider instance
         * created in super.setUp(), gets the DatabaseOpenHelper for the provider, and gets
         * a database object from the helper.
         */
        mDb = getProvider().getSQLiteOpenHelperForTest().getWritableDatabase();
    }

    @After
    public void cleanUp() {
        // delete all data for next test
        emptyData();
    }

    /**
     * This method is called after each test method, to clean up the current fixture. Since
     * this sample test case runs in an isolated context, no cleanup is necessary.
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Sets up test data.
     * The test data is in an SQL database, which is created in setUp() without any data,
     * and populated in insertData if necessary.
     */
    private void insertData() {
        for (int index = 0; index < TEST_TIME_SLOTS.length; index++) {
            mDb.insertOrThrow(
                    Tables.TIME_SLOTS,
                    null,
                    ModelConverter.timeSlotToContentValues(TEST_TIME_SLOTS[index]));
        }
    }

    /**
     * Empty test data.
     */
    private void emptyData() {
        mDb.delete(Tables.TIME_SLOTS, null, null);
    }

    /**
     * Tests the provider's publicly available URIs. If the URI is not one that the provider
     * understands, the provider should throw an exception. It also tests the provider's getType()
     * method for each URI, which should return the MIME type associated with the URI.
     */
    @Test
    public void testUriAndGetType() {
        // Tests the MIME type for the TimeSlot table URI.
        String mimeType = mMockResolver.getType(TimeSlots.buildTimeSlotsUri());
        assertEquals(CHServiceTimeUriEnum.TIMESLOTS.contentType, mimeType);

        // Creates a URI with a pattern for TimeSlot ids. The id doesn't have to exist.
        Uri timeSlotIdUri = TimeSlots.buildTimeSlotUri("1");

        // Gets the TimeSlot ID URI MIME type.
        mimeType = mMockResolver.getType(timeSlotIdUri);
        assertEquals(CHServiceTimeUriEnum.TIMESLOTS_ID.contentType, mimeType);

        // Tests an invalid URI. This should throw an IllegalArgumentException.
        mimeType = mMockResolver.getType(INVALID_URI);
    }

    /**
     * test the insert operation at the time_slot table of the data model.
     */
    @Test
    public void testInsertTimeSlot() {
        // Creates a new time slot instance.
        TimeSlot timeSlot1 = TEST_TIME_SLOTS[0];

        // Inserts a row using the new TimeSlot instance.
        // No assertion will be done. The insert() method either works or throws an Exception
        Uri rowUri = mMockResolver.insert(
                TimeSlots.buildTimeSlotsUri(),
                ModelConverter.timeSlotToContentValues(timeSlot1));

        // Parses the returned URI to get the TimeSlot ID of the new TimeSlot. The ID is used in subtest 2.
        String timeSlotId = rowUri.getLastPathSegment();

        // Does a full query on the table. Since insertData() hasn't yet been called, the
        // table should only contain the record just inserted.
        Cursor cursor = mMockResolver.query(
                TimeSlots.buildTimeSlotsUri(), // the main table URI
                null, null, null, null);

        // Asserts that there should be only 1 record.
        assertEquals(1, cursor.getCount());

        // Moves to the first (and only) record in the cursor and asserts that this worked.
        assertTrue(cursor.moveToFirst());

        TimeSlot timeSlotFromDB = ModelConverter.cursorToTimeSlotModel(cursor, new ColumnIndexCache());

        // Tests each column in the returned cursor against the data that was inserted,
        // comparing the field in the TimeSlot object to the data in the cursor.
        //assertTrue(timeSlot1.compare(timeSlotFromDB));
    }

    /**
     * Tests deletions from the data model.
     */
    @Test
    public void testDeletes() {
        /**
         * Subtest 1.
         */
        // Tries to delete a record from a data model that is empty.
        int rowsDeleted = mMockResolver.delete(TimeSlots.buildTimeSlotsUri(), null, null);

        // Assert that the deletion did not work. The number of deleted rows should be zero.
        assertEquals(0, rowsDeleted);

        /**
         * Subtest 2.
         */
        // Tries to delete an existing record.

        // Inserts data into the model.
        insertData();

        // Builds a URI based on the provider's content ID URI base and the saved TimeSlot ID.
        Uri timeSlotIdUri = null;
        //TimeSlots.buildTimeSlotUri(TEST_TIME_SLOTS[1].timeSlotId);

        // Uses the same parameters to try to delete the row with title "Note0"
        rowsDeleted = mMockResolver.delete(timeSlotIdUri, null, null);

        // The number of deleted rows should be 1.
        assertEquals(1, rowsDeleted);

        // Tests that the record no longer exists. Tries to get it from the table, and
        // asserts that nothing was returned.

        // Queries the table with the same selection column and argument used to delete the row.
        Cursor cursor = mMockResolver.query(timeSlotIdUri, null, null, null, null);

        // Asserts that the cursor is empty since the record had already been deleted.
        assertEquals(0, cursor.getCount());
    }

    /**
     * Tests updates to the data model.
     */
    @Test
    public void testUpdates() {
        /**
         * Subtest 1.
         */
        // Tries to update a record in an empty table.

        // Sets up the update by putting the "name" column and a value into the values map.
        TimeSlotContentValues timeSlotContentValues = new TimeSlotContentValues().putName("No Nmae");

        // Tries to update the table
        int rowsUpdated = mMockResolver.update(TimeSlots.buildTimeSlotUri("1"),
                timeSlotContentValues.values(), null, null);

        // Asserts that no rows were updated.
        assertEquals(0, rowsUpdated);

        /**
         * Subtest 2.
         */
        // Builds the table, and then tries the update again using the same arguments.

        // Inserts data into the model.
        insertData();

        // Builds a URI based on the provider's content ID URI base and the saved TimeSlot ID.
        Uri timeSlotIdUri = null;
        //TimeSlots.buildTimeSlotUri(TEST_TIME_SLOTS[1].timeSlotId);

        //  Does the update again, using the same arguments as in subtest 1.
        rowsUpdated = mMockResolver.update(timeSlotIdUri, timeSlotContentValues.values(), null, null);

        // Asserts that only one row was updated.
        assertEquals(1, rowsUpdated);
    }

    /*
     * Tests the provider's public API for querying data in the table, using the URI for
     * a dataset of records.
     */
    @Test
    public void testQueriesOnTimeSlotsUri() {
        /**
         * Query subtest 1.
         */
        // If there are no records in the table, the returned cursor from a query should be empty.
        Cursor cursor = mMockResolver.query(TimeSlots.buildTimeSlotsUri(), null, null, null, null);

        // Asserts that the returned cursor contains no records
        assertEquals(0, cursor.getCount());

        /**
         * Query subtest 2.
         */
        // If the table contains records, the returned cursor from a query should contain records.

        // Inserts the test data into the provider's underlying data source
        insertData();

        // Gets all the columns for all the rows in the table
        cursor = mMockResolver.query(TimeSlots.buildTimeSlotsUri(), null, null, null, null);

        // Asserts that the returned cursor contains the same number of rows as the size of the
        // test data array.
        assertEquals(TEST_TIME_SLOTS.length, cursor.getCount());
    }

    /**
     * Tests queries against the provider, using the TimeSlot id URI. This URI encodes a single
     * record ID. The provider should only return 0 or 1 record.
     */
    @Test
    public void testQueriesOnTimeSlotIdUri() {
        /**
         * Query subtest 1.
         */
        // Tests that a query against an empty table returns null.

        // Constructs a URI that matches the provider's TimeSlot id URI pattern, using an arbitrary
        // value of 1 as the note ID.
        Uri timeSlotIdUri = TimeSlots.buildTimeSlotUri("1");

        // Queries the table with the notes ID URI. This should return an empty cursor.
        Cursor cursor = mMockResolver.query(timeSlotIdUri, null, null, null, null);

        // Asserts that the cursor is null.
        assertEquals(0, cursor.getCount());

        /**
         * Query subtest 2.
         */
        // Tests that a query against a table containing records returns a single record whose ID
        // is the one requested in the URI provided.

        // Inserts the test data into the provider's underlying data source.
        insertData();

        // Builds a URI based on the provider's content ID URI base and the saved TimeSlot ID.
        timeSlotIdUri = null;
        //TimeSlots.buildTimeSlotUri(TEST_TIME_SLOTS[1].timeSlotId);

        // Queries the table using the URI for the full table.
        cursor = mMockResolver.query(timeSlotIdUri, null, null, null, null);

        // Asserts that the cursor contains only one row.
        assertEquals(1, cursor.getCount());

        // Moves to the cursor's first row, and asserts that this did not fail.
        assertTrue(cursor.moveToFirst());

        // Asserts that the TimeSlot ID passed to the provider is the same as the TimeSlot ID returned.
//        assertEquals(TEST_TIME_SLOTS[1].timeSlotId,
//                cursor.getString(cursor.getColumnIndex(TimeSlots.TIME_SLOT_ID)));
    }


}
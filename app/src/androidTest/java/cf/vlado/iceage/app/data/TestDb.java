/*
 * Copyright (C) 2014 The Android Open Source Project
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
package cf.vlado.iceage.app.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void deleteTheDatabase() {
        mContext.deleteDatabase(IceWeatherDbHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {

        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(IceWeatherContract.LocationEntry.TABLE_NAME);
        tableNameHashSet.add(IceWeatherContract.WeatherEntry.TABLE_NAME);

        mContext.deleteDatabase(IceWeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new IceWeatherDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: This means that the database has not been created correctly", c.moveToFirst());

        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        assertTrue("Error: Your database was created without both the location entry and weather entry tables", tableNameHashSet.isEmpty());
        c = db.rawQuery("PRAGMA table_info(" + IceWeatherContract.LocationEntry.TABLE_NAME + ")", null);
        assertTrue("Error: This means that we were unable to query the database for table information.",c.moveToFirst());

        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(IceWeatherContract.LocationEntry._ID);
        locationColumnHashSet.add(IceWeatherContract.LocationEntry.COLUMN_CITY_NAME);
        locationColumnHashSet.add(IceWeatherContract.LocationEntry.COLUMN_COORD_LAT);
        locationColumnHashSet.add(IceWeatherContract.LocationEntry.COLUMN_COORD_LONG);
        locationColumnHashSet.add(IceWeatherContract.LocationEntry.COLUMN_LOCATION_SETTING);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required location entry columns", locationColumnHashSet.isEmpty());
        db.close();
    }

    public void testLocationTable() {
        insertLocation();
    }

    public void testWeatherTable() {
        long locationRowId = insertLocation();
        assertFalse("Error: Location Not Inserted Correctly", locationRowId == -1L);
        IceWeatherDbHelper dbHelper = new IceWeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues weatherValues = TestUtilities.createWeatherValues(locationRowId);
        long weatherRowId = db.insert(IceWeatherContract.WeatherEntry.TABLE_NAME, null, weatherValues);
        assertTrue(weatherRowId != -1);
        Cursor weatherCursor = db.query(
                IceWeatherContract.WeatherEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );
        assertTrue( "Error: No Records returned from location query", weatherCursor.moveToFirst() );
        TestUtilities.validateCurrentRecord("testInsertReadDb weatherEntry failed to validate", weatherCursor, weatherValues);
        assertFalse( "Error: More than one record returned from weather query", weatherCursor.moveToNext() );
        weatherCursor.close();
        dbHelper.close();
    }

    public long insertLocation() {
        IceWeatherDbHelper dbHelper = new IceWeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();
        long locationRowId;
        locationRowId = db.insert(IceWeatherContract.LocationEntry.TABLE_NAME, null, testValues);
        assertTrue(locationRowId != -1);
        Cursor cursor = db.query(
                IceWeatherContract.LocationEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );
        assertTrue( "Error: No Records returned from location query", cursor.moveToFirst() );
        TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed", cursor, testValues);
        assertFalse( "Error: More than one record returned from location query", cursor.moveToNext() );
        cursor.close();
        db.close();
        return locationRowId;
    }
}

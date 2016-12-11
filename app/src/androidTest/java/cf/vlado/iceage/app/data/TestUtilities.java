package cf.vlado.iceage.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

import cf.vlado.iceage.app.utils.PollingCheck;

public class TestUtilities extends AndroidTestCase {
    public static final String TEST_LOCATION = "99705";
    public static final long TEST_DATE = 1419033600L;  // December 20th, 2014

    public static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    public static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() + "' did not match the expected value '" + expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    public static ContentValues createWeatherValues(long locationRowId) {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(IceWeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(IceWeatherContract.WeatherEntry.COLUMN_DATE, TEST_DATE);
        weatherValues.put(IceWeatherContract.WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(IceWeatherContract.WeatherEntry.COLUMN_HUMIDITY, 1.2);
        weatherValues.put(IceWeatherContract.WeatherEntry.COLUMN_PRESSURE, 1.3);
        weatherValues.put(IceWeatherContract.WeatherEntry.COLUMN_MAX_TEMP, 75);
        weatherValues.put(IceWeatherContract.WeatherEntry.COLUMN_MIN_TEMP, 65);
        weatherValues.put(IceWeatherContract.WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        weatherValues.put(IceWeatherContract.WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        weatherValues.put(IceWeatherContract.WeatherEntry.COLUMN_WEATHER_ID, 321);
        return weatherValues;
    }

    public static ContentValues createNorthPoleLocationValues() {
        ContentValues testValues = new ContentValues();
        testValues.put(IceWeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, TEST_LOCATION);
        testValues.put(IceWeatherContract.LocationEntry.COLUMN_CITY_NAME, "North Pole");
        testValues.put(IceWeatherContract.LocationEntry.COLUMN_COORD_LAT, 64.7488);
        testValues.put(IceWeatherContract.LocationEntry.COLUMN_COORD_LONG, -147.353);

        return testValues;
    }

    public static long insertNorthPoleLocationValues(Context context) {
        IceWeatherDbHelper dbHelper = new IceWeatherDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createNorthPoleLocationValues();
        long locationRowId;
        locationRowId = db.insert(IceWeatherContract.LocationEntry.TABLE_NAME, null, testValues);
        assertTrue("Error: Failure to insert North Pole Location Values", locationRowId != -1);
        return locationRowId;
    }

    public static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
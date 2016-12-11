package cf.vlado.iceage.app;

/**
 * Created by blado on 12/11/16.
 */

import android.annotation.TargetApi;
import android.database.Cursor;
import android.test.AndroidTestCase;

import cf.vlado.iceage.app.data.IceWeatherContract;

public class TestIceWeatherTask extends AndroidTestCase{
    static final String ADD_LOCATION_SETTING = "Sunnydale, CA";
    static final String ADD_LOCATION_CITY = "Sunnydale";
    static final double ADD_LOCATION_LAT = 34.425833;
    static final double ADD_LOCATION_LON = -119.714167;

    @TargetApi(11)
    public void testAddLocation() {
        // start from a clean state
        getContext().getContentResolver().delete(IceWeatherContract.LocationEntry.CONTENT_URI, IceWeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ?", new String[]{ADD_LOCATION_SETTING});
        IceWeatherTask fwt = new IceWeatherTask(getContext());
        long locationId = fwt.addLocation(ADD_LOCATION_SETTING, ADD_LOCATION_CITY, ADD_LOCATION_LAT, ADD_LOCATION_LON);
        assertFalse("Error: addLocation returned an invalid ID on insert", locationId == -1);
        for ( int i = 0; i < 2; i++ ) {
            Cursor locationCursor = getContext().getContentResolver().query(
                    IceWeatherContract.LocationEntry.CONTENT_URI,
                    new String[]{
                            IceWeatherContract.LocationEntry._ID,
                            IceWeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
                            IceWeatherContract.LocationEntry.COLUMN_CITY_NAME,
                            IceWeatherContract.LocationEntry.COLUMN_COORD_LAT,
                            IceWeatherContract.LocationEntry.COLUMN_COORD_LONG
                    },
                    IceWeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
                    new String[]{ADD_LOCATION_SETTING},
                    null);
            if (locationCursor.moveToFirst()) {
                assertEquals("Error: the queried value of locationId does not match the returned value" + "from addLocation", locationCursor.getLong(0), locationId);
                assertEquals("Error: the queried value of location setting is incorrect", locationCursor.getString(1), ADD_LOCATION_SETTING);
                assertEquals("Error: the queried value of location city is incorrect", locationCursor.getString(2), ADD_LOCATION_CITY);
                assertEquals("Error: the queried value of latitude is incorrect", locationCursor.getDouble(3), ADD_LOCATION_LAT);
                assertEquals("Error: the queried value of longitude is incorrect", locationCursor.getDouble(4), ADD_LOCATION_LON);
            } else {
                fail("Error: the id you used to query returned an empty cursor");
            }
            assertFalse("Error: there should be only one record returned from a location query", locationCursor.moveToNext());
            long newLocationId = fwt.addLocation(ADD_LOCATION_SETTING, ADD_LOCATION_CITY, ADD_LOCATION_LAT, ADD_LOCATION_LON);
            assertEquals("Error: inserting a location again should return the same ID", locationId, newLocationId);
        }
        // reset our state back to normal
        getContext().getContentResolver().delete(IceWeatherContract.LocationEntry.CONTENT_URI, IceWeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ?", new String[]{ADD_LOCATION_SETTING});
        // clean up the test so that other tests can use the content provider
        getContext().getContentResolver().acquireContentProviderClient(IceWeatherContract.LocationEntry.CONTENT_URI).getLocalContentProvider().shutdown();
    }
}

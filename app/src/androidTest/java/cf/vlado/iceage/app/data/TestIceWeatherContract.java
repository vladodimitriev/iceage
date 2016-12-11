package cf.vlado.iceage.app.data;

/**
 * Created by blado on 12/11/16.
 */
import android.net.Uri;
import android.test.AndroidTestCase;

public class TestIceWeatherContract extends AndroidTestCase {

    private static final String TEST_WEATHER_LOCATION = "/North Pole";
    private static final long TEST_WEATHER_DATE = 1419033600L;  // December 20th, 2014

    public void testBuildWeatherLocation() {
        Uri locationUri = IceWeatherContract.WeatherEntry.buildWeatherLocation(TEST_WEATHER_LOCATION);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildWeatherLocation in " + "WeatherContract.", locationUri);
        assertEquals("Error: Weather location not properly appended to the end of the Uri", TEST_WEATHER_LOCATION, locationUri.getLastPathSegment());
        assertEquals("Error: Weather location Uri doesn't match our expected result", locationUri.toString(), "content://com.example.android.sunshine.app/weather/%2FNorth%20Pole");
    }
}
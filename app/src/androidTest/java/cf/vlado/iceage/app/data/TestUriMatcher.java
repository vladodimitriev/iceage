package cf.vlado.iceage.app.data;

/**
 * Created by blado on 12/10/16.
 */

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

public class TestUriMatcher extends AndroidTestCase {
    private static final String LOCATION_QUERY = "London, UK";
    private static final long TEST_DATE = 1419033600L;  // December 20th, 2014
    private static final long TEST_LOCATION_ID = 10L;

    // content://cf.vlado.iceage.app/weather"
    private static final Uri TEST_WEATHER_DIR = IceWeatherContract.WeatherEntry.CONTENT_URI;
    private static final Uri TEST_WEATHER_WITH_LOCATION_DIR = IceWeatherContract.WeatherEntry.buildWeatherLocation(LOCATION_QUERY);
    private static final Uri TEST_WEATHER_WITH_LOCATION_AND_DATE_DIR = IceWeatherContract.WeatherEntry.buildWeatherLocationWithDate(LOCATION_QUERY, TEST_DATE);
    // content://cf.vlado.iceage.app/location"
    private static final Uri TEST_LOCATION_DIR = IceWeatherContract.LocationEntry.CONTENT_URI;

    public void testUriMatcher() {
        UriMatcher testMatcher = IceWeatherProvider.buildUriMatcher();
        assertEquals("Error: The WEATHER URI was matched incorrectly.", testMatcher.match(TEST_WEATHER_DIR), IceWeatherProvider.WEATHER);
        assertEquals("Error: The WEATHER WITH LOCATION URI was matched incorrectly.", testMatcher.match(TEST_WEATHER_WITH_LOCATION_DIR), IceWeatherProvider.WEATHER_WITH_LOCATION);
        assertEquals("Error: The WEATHER WITH LOCATION AND DATE URI was matched incorrectly.", testMatcher.match(TEST_WEATHER_WITH_LOCATION_AND_DATE_DIR), IceWeatherProvider.WEATHER_WITH_LOCATION_AND_DATE);
        assertEquals("Error: The LOCATION URI was matched incorrectly.", testMatcher.match(TEST_LOCATION_DIR), IceWeatherProvider.LOCATION);
    }
}
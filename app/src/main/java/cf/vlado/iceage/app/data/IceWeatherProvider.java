package cf.vlado.iceage.app.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by blado on 12/11/16.
 */

public class IceWeatherProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private IceWeatherDbHelper mOpenHelper;

    static final int WEATHER = 100;
    static final int WEATHER_WITH_LOCATION = 101;
    static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
    static final int LOCATION = 300;

    private static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;

    static{
        sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();
        sWeatherByLocationSettingQueryBuilder.setTables(
                IceWeatherContract.WeatherEntry.TABLE_NAME + " INNER JOIN " +
                        IceWeatherContract.LocationEntry.TABLE_NAME +
                        " ON " + IceWeatherContract.WeatherEntry.TABLE_NAME +
                        "." + IceWeatherContract.WeatherEntry.COLUMN_LOC_KEY +
                        " = " + IceWeatherContract.LocationEntry.TABLE_NAME +
                        "." + IceWeatherContract.LocationEntry._ID);
    }

    private static final String sLocationSettingSelection = IceWeatherContract.LocationEntry.TABLE_NAME+ "." + IceWeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? ";
    private static final String sLocationSettingWithStartDateSelection = IceWeatherContract.LocationEntry.TABLE_NAME+ "." + IceWeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " + IceWeatherContract.WeatherEntry.COLUMN_DATE + " >= ? ";
    private static final String sLocationSettingAndDaySelection = IceWeatherContract.LocationEntry.TABLE_NAME + "." + IceWeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " + IceWeatherContract.WeatherEntry.COLUMN_DATE + " = ? ";

    private Cursor getWeatherByLocationSetting(Uri uri, String[] projection, String sortOrder) {
        String locationSetting = IceWeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
        long startDate = IceWeatherContract.WeatherEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        if (startDate == 0) {
            selection = sLocationSettingSelection;
            selectionArgs = new String[]{locationSetting};
        } else {
            selectionArgs = new String[]{locationSetting, Long.toString(startDate)};
            selection = sLocationSettingWithStartDateSelection;
        }

        return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getWeatherByLocationSettingAndDate(Uri uri, String[] projection, String sortOrder) {
        String locationSetting = IceWeatherContract.WeatherEntry.getLocationSettingFromUri(uri);
        long date = IceWeatherContract.WeatherEntry.getDateFromUri(uri);

        return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sLocationSettingAndDaySelection,
                new String[]{locationSetting, Long.toString(date)},
                null,
                null,
                sortOrder
        );
    }

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = IceWeatherContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, IceWeatherContract.PATH_WEATHER, WEATHER);
        matcher.addURI(authority, IceWeatherContract.PATH_WEATHER + "/*", WEATHER_WITH_LOCATION);
        matcher.addURI(authority, IceWeatherContract.PATH_WEATHER + "/*/#", WEATHER_WITH_LOCATION_AND_DATE);
        matcher.addURI(authority, IceWeatherContract.PATH_LOCATION, LOCATION);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new IceWeatherDbHelper(getContext());
        return true;
    }

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.
     */
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
//            case WEATHER_WITH_LOCATION_AND_DATE:
//            case WEATHER_WITH_LOCATION:
            case WEATHER:
                return IceWeatherContract.WeatherEntry.CONTENT_TYPE;
            case LOCATION:
                return IceWeatherContract.LocationEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "weather/*/*"
            case WEATHER_WITH_LOCATION_AND_DATE:
            {
                retCursor = getWeatherByLocationSettingAndDate(uri, projection, sortOrder);
                break;
            }
            // "weather/*"
            case WEATHER_WITH_LOCATION: {
                retCursor = getWeatherByLocationSetting(uri, projection, sortOrder);
                break;
            }
            // "weather"
            case WEATHER: {
                retCursor = null;
                break;
            }
            // "location"
            case LOCATION: {
                retCursor = null;
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case WEATHER: {
                normalizeDate(values);
                long _id = db.insert(IceWeatherContract.WeatherEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = IceWeatherContract.WeatherEntry.buildWeatherUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Student: Start by getting a writable database

        // Student: Use the uriMatcher to match the WEATHER and LOCATION URI's we are going to
        // handle.  If it doesn't match these, throw an UnsupportedOperationException.

        // Student: A null value deletes all rows.  In my implementation of this, I only notified
        // the uri listeners (using the content resolver) if the rowsDeleted != 0 or the selection
        // is null.
        // Oh, and you should notify the listeners here.

        // Student: return the actual rows deleted
        return 0;
    }

    private void normalizeDate(ContentValues values) {
        // normalize the date value
        if (values.containsKey(IceWeatherContract.WeatherEntry.COLUMN_DATE)) {
            long dateValue = values.getAsLong(IceWeatherContract.WeatherEntry.COLUMN_DATE);
            values.put(IceWeatherContract.WeatherEntry.COLUMN_DATE, IceWeatherContract.normalizeDate(dateValue));
        }
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Student: This is a lot like the delete function.  We return the number of rows impacted
        // by the update.
        return 0;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WEATHER:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);
                        long _id = db.insert(IceWeatherContract.WeatherEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    //Used for testing
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}

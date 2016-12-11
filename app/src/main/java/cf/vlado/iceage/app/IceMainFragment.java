package cf.vlado.iceage.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import cf.vlado.iceage.app.data.IceWeatherContract;


/**
 * A simple {@link Fragment} subclass.
 */
public class IceMainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int FORECAST_LOADER = 0;

    private static final String[] FORECAST_COLUMNS = {
            IceWeatherContract.WeatherEntry.TABLE_NAME + "." + IceWeatherContract.WeatherEntry._ID,
            IceWeatherContract.WeatherEntry.COLUMN_DATE,
            IceWeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            IceWeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            IceWeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            IceWeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            IceWeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            IceWeatherContract.LocationEntry.COLUMN_COORD_LAT,
            IceWeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;

    private IceCursorAdapter mForecastAdapter;

    public IceMainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.ice_main_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.ice_main_fragment_action_refresh) {
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mForecastAdapter = new IceCursorAdapter(getActivity(), null, 0);
        View rootView = inflater.inflate(R.layout.fragment_ice_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_ice_main_fragment);
        listView.setAdapter(mForecastAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    String locationSetting = IceUtility.getPreferredLocation(getActivity());
                    Intent intent = new Intent(getActivity(), IceDetailActivity.class).setData(IceWeatherContract.WeatherEntry.buildWeatherLocationWithDate(locationSetting, cursor.getLong(COL_WEATHER_DATE)));
                    startActivity(intent);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String locationSetting = IceUtility.getPreferredLocation(getActivity());
        String sortOrder = IceWeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = IceWeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(locationSetting, System.currentTimeMillis());
        return new CursorLoader(getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder);
    }

    public void onLocationChanged( ) {
        updateWeather();
        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mForecastAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mForecastAdapter.swapCursor(null);
    }

    private void updateWeather() {
        IceWeatherTask weatherTask = new IceWeatherTask(getActivity());
        String location = IceUtility.getPreferredLocation(getActivity());
        weatherTask.execute(location);
    }

}

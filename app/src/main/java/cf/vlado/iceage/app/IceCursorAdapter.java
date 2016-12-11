package cf.vlado.iceage.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by blado on 12/11/16.
 */

public class IceCursorAdapter extends CursorAdapter {

    public IceCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    private String formatHighLows(double high, double low) {
        boolean isMetric = IceUtility.isMetric(mContext);
        String highLowStr = IceUtility.formatTemperature(high, isMetric) + "/" + IceUtility.formatTemperature(low, isMetric);
        return highLowStr;
    }

    private String convertCursorRowToUXFormat(Cursor cursor) {
        String highAndLow = formatHighLows(cursor.getDouble(IceMainFragment.COL_WEATHER_MAX_TEMP), cursor.getDouble(IceMainFragment.COL_WEATHER_MIN_TEMP));
        return IceUtility.formatDate(cursor.getLong(IceMainFragment.COL_WEATHER_DATE)) + " - " + cursor.getString(IceMainFragment.COL_WEATHER_DESC) + " - " + highAndLow;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_forecast, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv = (TextView)view;
        tv.setText(convertCursorRowToUXFormat(cursor));
    }
}
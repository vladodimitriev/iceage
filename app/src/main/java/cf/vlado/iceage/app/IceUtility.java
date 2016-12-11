package cf.vlado.iceage.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.DateFormat;
import java.util.Date;

import cf.vlado.iceage.app.data.IceWeatherContract;

/**
 * Created by blado on 12/11/16.
 */

public class IceUtility {

    private static final String DATE_FORMAT = IceWeatherContract.DATE_FORMAT;

    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_location_key), context.getString(R.string.pref_location_default));
    }

    public static boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_units_key),context.getString(R.string.pref_units_metric)).equals(context.getString(R.string.pref_units_metric));
    }

    public static String formatTemperature(double temperature, boolean isMetric) {
        double temp;
        if ( !isMetric ) {
            temp = 9*temperature/5+32;
        } else {
            temp = temperature;
        }
        return String.format("%.0f", temp);
    }

    public static String formatDate(long dateInMillis) {
        Date date = new Date(dateInMillis);
        return DateFormat.getDateInstance().format(date);
    }

    public static String getFriendlyDayString(Context context, String dateStr) {
        return "Friday";
    }

    public static String getDayName(Context context, String dateStr) {
        return "Friday";
    }

    public static String getFormattedMonthDay(Context context, String dateStr) {
        return "Friday";
    }

    public static String getFormattedWind(Context context, float windSpeed, float degrees) {
        return "100km/h";
    }

    public static int getIconResourceForWeatherCondition(int weatherId) {
        return -1;
    }

    public static int getArtResourceForWeatherCondition(int weatherId) {
        return -1;
    }
}

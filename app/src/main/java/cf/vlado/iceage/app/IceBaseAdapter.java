package cf.vlado.iceage.app;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by blado on 12/11/16.
 */

public class IceBaseAdapter extends BaseAdapter {

    private final String LOG_TAG = IceBaseAdapter.class.getSimpleName();

    private String[] title;
    private String[] detail;
    private int[] imge;
    private Context context;
    private String[] data;
    private List<ContentValues> contentValuesList;
    private HashMap<Integer, String[]> contentValuesMap;

    public IceBaseAdapter(Context context) {
        Log.d(LOG_TAG, "IceBaseAdapter()");
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Log.d(LOG_TAG, "getView(), position: [" + position + "]");
        LayoutInflater inflater = LayoutInflater.from(context);
        View listItemView = inflater.inflate(R.layout.list_item_forecast, viewGroup, false);

        try {
            if (data != null) {
                String title = IceUtility.getFriendlyDayString(context, data[position]);

                TextView titleTxt = (TextView) listItemView.findViewById(R.id.ice_detail_text);
                titleTxt.setText(title);

                TextView detailTxt = (TextView) listItemView.findViewById(R.id.ice_detail_text);
                detailTxt.setText(data[position]);
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, "Exception", e);
        }
        return listItemView;
    }
}

package cs378.exmaple;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CountryActivity extends ListActivity {

    private static final String TAG = "CountryList";

    private ListView view;
    private ArrayList<String> countries;
    private Map<String, Boolean> countriesSafeStatus;
    private ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createModel();
        view = getListView();
        setAdapter();
        createOnItemClickListener();
    }

    private void createModel() {
        String[] rawData
                = getResources().getStringArray(R.array.countries);

        countries
                = new ArrayList<String>(Arrays.asList(rawData));

        // every country is safe to start
        countriesSafeStatus = new HashMap<String, Boolean>(countries.size());
        final int NUM = countries.size();
        for(String country : countries) {
            countriesSafeStatus.put(country, true);
        }

    }

    private void setAdapter() {
        // for layout that is simply a TextView
//                adapter
//                    = new ArrayAdapter<String>(this, R.layout.list_item, countries);


//        // for layout with TextView in more complex layout
        adapter
                = new ArrayAdapter<String>(
                this, // context
                R.layout.complex_list_item, // layout of list items / rows
                R.id.countryTextView, // sub layout to place text
                countries); // model of text

        setListAdapter(adapter);
    }

    private void createOnItemClickListener() {

        view.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent,
                    View v, int position, long id) {

                Log.d(TAG, "Selected view: " + v);

                String country = countries.get(position);

                String toastString = "position: " + position +
                        ", id: " + id + "\ndata: "
                        + country;

                // example if creating and showing a Toast. Cheers!
                Toast.makeText(CountryActivity.this,
                        toastString,
                        Toast.LENGTH_LONG).show();

//                // remove item selected from arraylist
//                countries.remove(position);
//                //
//                adapter.notifyDataSetChanged();

                //needed?
                // view.invalidateViews();

                // if we want to perform web search for country
//                searchWeb(country);
            }
        });
    }

    // from https://developer.android.com/guide/components/intents-common.html#Browser
    public void searchWeb(String query) {
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, query);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


//    // implement our own adapter to handle toggle switch
    class CountryAdapter extends ArrayAdapter<String> {

        CountryAdapter() {
            super(CountryActivity.this, R.layout.complex_list_item, R.id.countryTextView, countries);
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            if(convertView != null)
                Log.d(TAG, "convert view: " + ((TextView) convertView.findViewById(R.id.countryTextView)).getText());

            View row = super.getView(position, convertView, parent);
            ViewHolder holder = (ViewHolder)row.getTag();
            Log.d(TAG, "in getView. position: " + position + ", country: " +
                    countries.get(position) + ", holder: " + holder);
            if (holder == null) {
                holder = new ViewHolder(row);
                row.setTag(holder);
            }

            // likely a better way, but if recycling a view, get old status
            boolean statusOfOldView = false;
            String oldCountry = null;
            if(convertView != null) {
                oldCountry = (String) ((TextView) convertView.findViewById(R.id.countryTextView)).getText();
                statusOfOldView = countriesSafeStatus.get(oldCountry);
            }

            Log.d(TAG, "holder: " + holder + ", safe status: " + countriesSafeStatus.get(position));

            // PROBLEM!! If we are setting the switch on a recycled view
            // this leads to a call to the listener which mucks up the
            // old value of safe in the ArrayList!!!
            holder.safe.setChecked(countriesSafeStatus.get(countries.get(position)));

            // if recycled fix our model. (MUST BE A BETTER WAY!)
            if(convertView != null) {
                countriesSafeStatus.put(oldCountry, statusOfOldView);
            }

            holder.safe.setOnCheckedChangeListener(
                    new CompoundButton.OnCheckedChangeListener() {
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            Log.d(TAG, "switch checked. position: " + position + ", country: "
                                    + countries.get(position) + ", isChecked param: " + isChecked);
                            countriesSafeStatus.put(countries.get(position), isChecked);
                        }
                    }
            );

            return row;
        }
    }

    // ViewHolder for complex row: country name and switch for "safe":
    // separate class???
    private static class ViewHolder {

        // array adapter will handle country text view for us
        private Switch safe;

        ViewHolder(View row) {
            this.safe = (Switch)row.findViewById(R.id.countrySafeSwitch);
        }

        public String toString() {
            return "" + safe.isChecked();
        }
    }
}

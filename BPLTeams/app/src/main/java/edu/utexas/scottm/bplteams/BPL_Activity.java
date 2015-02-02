package edu.utexas.scottm.bplteams;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import java.util.Random;


public class BPL_Activity extends ActionBarActivity {

    private static final String TAG = "BPL Activity";
    private static Random randNumGen = new Random();

    // parallel array of images matched to spinner array
    // VERY FRAGILE.
    private int[] imageIds = {R.drawable.bpl, R.drawable.arsenal,
            R.drawable.chelsea, R.drawable.everton, R.drawable.tottenham};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bpl_);
        setSpinnerListener();
        setRandomButtonListener();
    }

    private void setRandomButtonListener() {
        ((Button) findViewById(R.id.random_button)).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int index = randNumGen.nextInt(imageIds.length);
                        Log.d(TAG, "random index selected = " + index);
                        ImageView iv = (ImageView) findViewById(R.id.imageView);
                        iv.setImageResource(imageIds[index]);
                        Spinner spinner = (Spinner) findViewById(R.id.football_club_spinner);
                        spinner.setSelection(index);
                    }
                });

    }


    private void setSpinnerListener() {
        Spinner spinner = (Spinner) findViewById(R.id.football_club_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Log.d(TAG, "id of selected item: " + position);
                ImageView iv = (ImageView) findViewById(R.id.imageView);
                iv.setImageResource(imageIds[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // nothing to do
            }

        });
    }

    // from http://developer.android.com/guide/topics/ui/controls/spinner.html
    // NOT NEEDED IF SPINNER ENTRIES SET IN XML
    private void populateSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.football_club_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.football_clubs, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }


}

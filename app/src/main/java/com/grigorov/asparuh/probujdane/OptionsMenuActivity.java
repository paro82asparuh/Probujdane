package com.grigorov.asparuh.probujdane;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

//import static androidx.constraintlayout.R.id.parent;

public class OptionsMenuActivity extends AppCompatActivity {

    Spinner spinnerBesedaTextSize;
    static private String[] besedaTextSizes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options_menu);

        besedaTextSizes = new String[]{"8", "10", "12", "14", "16", "18", "20", "22", "24", "26", "28", "30"};

        spinnerBesedaTextSize = (Spinner)findViewById(R.id.spinnerBesedaTextSize);

        SpinnerTextSizesAdapter adapterBesedaTextSize = new SpinnerTextSizesAdapter(this, R.layout.spinner_options_text_size_item, besedaTextSizes);
        //ArrayAdapter<String> adapterBesedaTextSize = ArrayAdapter.createFromResource(this,
        //        besedaTextSizes, R.layout.spinner_besede_test_sizes_item);
        //adapterBesedaTextSize.setDropDownViewResource(android.R.layout.spinner_besede_test_sizes_dropdown_item);

        spinnerBesedaTextSize.setAdapter(adapterBesedaTextSize);

        spinnerBesedaTextSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("com.grigorov.asparuh.probujdane.textsize", besedaTextSizes[pos]);
                editor.commit();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String besedaTextSizeString = sharedPref.getString("com.grigorov.asparuh.probujdane.textsize", "14");
        int currentSelection = Integer.parseInt(besedaTextSizeString);
        currentSelection = (currentSelection-8)/2;

        spinnerBesedaTextSize.setSelection(currentSelection,true);

    }

    static public String getBesedaTextSizeString(int position) {
        return besedaTextSizes[position];
    }


    /***** Adapter class extends with ArrayAdapter ******/
    public class SpinnerTextSizesAdapter extends ArrayAdapter<String>{

        private final Activity activity;
        private final String[] data;
        LayoutInflater inflater;

        /*************  CustomAdapter Constructor *****************/
        public SpinnerTextSizesAdapter(
                OptionsMenuActivity activitySpinner,
                int textViewResourceId,
                String[] objects) {
            super(activitySpinner, textViewResourceId, objects);
            /********** Take passed values **********/
            activity = activitySpinner;
            data     = objects;
            /***********  Layout inflator to call external xml layout () **********************/
            inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
                return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //return getCustomView(position, convertView, parent);
            View row = inflater.inflate(R.layout.spinner_options_text_size_top, parent, false);
            TextView textView1        = row.findViewById(R.id.textViewSpinnerTextSizesTop);
            textView1.setText(OptionsMenuActivity.getBesedaTextSizeString(position));
            return row;
        }

        // This funtion called for each row ( Called data.size() times )
        public View getCustomView(int position, View convertView, ViewGroup parent) {

            /********** Inflate spinner_rows.xml file for each row ( Defined below ) ************/
            View row = inflater.inflate(R.layout.spinner_options_text_size_item, parent, false);
            TextView textView1        = row.findViewById(R.id.textViewSpinnerTextSizes);
            textView1.setText(OptionsMenuActivity.getBesedaTextSizeString(position));
            return row;
        }
    }

}

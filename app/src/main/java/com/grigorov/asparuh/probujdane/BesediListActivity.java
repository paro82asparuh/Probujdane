package com.grigorov.asparuh.probujdane;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.widget.Toast.LENGTH_LONG;

public class BesediListActivity extends AppCompatActivity {

    private int SelectedBesediType;
    public final static int Nedelni_Besedi =1;
    public final static int OOK_Besedi =2;
    public final static int MOK_Besedi =3;
    public final static int Utrinni_Slova_Besedi =4;
    public final static int Syborni_Besedi =5;
    public final static int MladejkiSyborni_Besedi =6;
    public final static int Rilski_Besedi =7;
    public final static int PoslednotoSlovo_Besedi =8;
    public final static int PredSestrite_Besedi =9;
    public final static int PredRykovoditelite_Besedi =10;
    public final static int Izvynredni_Besedi =11;
    public final static int KlasNaDobrodetelite_Besedi =12;
    String BesediTypeCyrillic;

    private ArrayList<besedaInfo> listBesediInfo= new ArrayList<besedaInfo>();

    private besediDBHelper mydb;

    private Integer[] besediUniqueYears;
    private String[] besediFromYears;
    private String[] besediToYears;

    Spinner spinerFromYears;
    Spinner spinerToYears;

    BesediInfoAdapter besediInfoAdapter;

    private String screenWidthInPixels;


    public class BesediInfoAdapter extends ArrayAdapter<besedaInfo> {

        // View lookup cache
        private class ViewHolder {
            TextView besedaName;
            TextView besadaDate;
        }

        public BesediInfoAdapter (Context context, ArrayList<besedaInfo> besediInfo) {
            super(context, 0, besediInfo);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // Get the data item for this position
            final besedaInfo currentBesedaInfo = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                // If there's no view to re-use, inflate a brand new view for row
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.besedi_list_item, parent, false);
                viewHolder.besedaName = convertView.findViewById(R.id.textBesediName);
                viewHolder.besadaDate = convertView.findViewById(R.id.textBesediDate);
                // Cache the viewHolder object inside the fresh view
                convertView.setTag(viewHolder);
            } else {
                // View is being recycled, retrieve the viewHolder object from tag
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // Lookup view for data population
            //TextView tvBesedaName = (TextView) convertView.findViewById(R.id.textBesediName);
            //TextView tvBesedaDate = (TextView) convertView.findViewById(R.id.textBesediDate);
            // Populate the data into the template view using the data object
            viewHolder.besedaName.setText(currentBesedaInfo.getBesedaName());
            viewHolder.besadaDate.setText(currentBesedaInfo.getBesedaDateString());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    screenWidthInPixels = ((Integer) (findViewById(R.id.listViewBesedi).getWidth())).toString();
                    Intent intent = new Intent(BesediListActivity.this, BesedaActivity.class);
                    intent.putExtra("com.grigorov.asparuh.probujdane.BesedaNameVar", currentBesedaInfo.getBesedaName());
                    intent.putExtra("com.grigorov.asparuh.probujdane.BesedaDateYearVar", currentBesedaInfo.getBesedaDateYear());
                    intent.putExtra("com.grigorov.asparuh.probujdane.BesedaDateMonthVar", currentBesedaInfo.getBesedaDateMonth());
                    intent.putExtra("com.grigorov.asparuh.probujdane.BesedaDateDayVar", currentBesedaInfo.getBesedaDateDay());
                    intent.putExtra("com.grigorov.asparuh.probujdane.BesedaLinkVar", currentBesedaInfo.getBesedaLink());
                    intent.putExtra("com.grigorov.asparuh.probujdane.BesedaVarinatVar", "1");
                    intent.putExtra("com.grigorov.asparuh.probujdane.BesedaMarkersVar", "");
                    intent.putExtra("com.grigorov.asparuh.probujdane.screenWidthInPixels", screenWidthInPixels);
                    intent.putExtra("com.grigorov.asparuh.probujdane.BesedaScrollIndecesVar", "1 0");
                    startActivity(intent);
                }
            });
            // Return the completed view to render on screen
            return convertView;
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_besedi_list);

        Intent intent = getIntent();
        int SelectedBesediTypeTemp = intent.getIntExtra("com.grigorov.asparuh.probujdane.SelectedBesediTypeVar",0);
        SelectedBesediType = SelectedBesediTypeTemp;

        setBesediTypeCyrillic();
        setTextBesediListTittle();

        // Read from the database
        mydb = new besediDBHelper(this);
        setListBesedi();

        // Spinners
        spinerFromYears = (Spinner)findViewById(R.id.spinnerFromYear);
        spinerToYears = (Spinner)findViewById(R.id.spinnerToYear);
        SpinnerBesediListAdapter adapterFromYears = new SpinnerBesediListAdapter(this, R.layout.spinner_besedi_list_item, besediFromYears);
        SpinnerBesediListAdapter adapterToYears = new SpinnerBesediListAdapter(this, R.layout.spinner_besedi_list_item, besediToYears);
        spinerFromYears.setAdapter(adapterFromYears);
        adapterFromYears.notifyDataSetChanged();
        spinerToYears.setAdapter(adapterToYears);
        adapterToYears.notifyDataSetChanged();
        spinerFromYears.setSelection(0,true);
        spinerToYears.setSelection(besediToYears.length-1,true);

        besediInfoAdapter = new BesediInfoAdapter(this, listBesediInfo);
        ListView listView1 = (ListView) findViewById(R.id.listViewBesedi);
        listView1.setAdapter(besediInfoAdapter);
    }

    public void onResume () {
        super.onResume();
        mydb = new besediDBHelper(this);
    }

    public void onPause () {
        super.onPause();
        mydb.close();
    }

    public void startSearchMenuTask (View view) {
        Intent intent = new Intent(this, SearchMenuActivity.class);
        intent.putExtra("com.grigorov.asparuh.probujdane.searchSource", "SEARCH_ALL_BESEDI");
        startActivity(intent);
    }

    public void startOptionsMenuTask (View view) {
        Intent intent = new Intent(this, OptionsMenuActivity.class);
        startActivity(intent);
    }

    private void setBesediTypeCyrillic() {
        switch (SelectedBesediType) {
            case Nedelni_Besedi: BesediTypeCyrillic="Неделни беседи"; break;
            case OOK_Besedi: BesediTypeCyrillic="Общ Окултен клас"; break;
            case MOK_Besedi: BesediTypeCyrillic="Младежки окултен клас"; break;
            case Utrinni_Slova_Besedi: BesediTypeCyrillic="Утрини Слова"; break;
            case Syborni_Besedi: BesediTypeCyrillic="Съборни беседи"; break;
            case MladejkiSyborni_Besedi: BesediTypeCyrillic="Младежки събори"; break;
            case Rilski_Besedi: BesediTypeCyrillic="Рилски беседи"; break;
            case PoslednotoSlovo_Besedi: BesediTypeCyrillic="Последното Слово"; break;
            case PredSestrite_Besedi: BesediTypeCyrillic="Беседи пред сестрите"; break;
            case PredRykovoditelite_Besedi: BesediTypeCyrillic="Беседи пред ръководителите"; break;
            case Izvynredni_Besedi: BesediTypeCyrillic="Извънредни беседи"; break;
            case KlasNaDobrodetelite_Besedi: BesediTypeCyrillic="Клас на Добродетелите"; break;
            default: BesediTypeCyrillic="Грешка"; break;
        }
    }


    private void setTextBesediListTittle() {
        String setTextBesediListTitle;
        setTextBesediListTitle = BesediTypeCyrillic;
        TextView textview1 = (TextView) findViewById(R.id.textBesediListTitle);
        textview1.setText(setTextBesediListTitle);
    }

    public void setListBesedi () {

        Cursor rs = mydb.getbesediInfo(BesediTypeCyrillic);
        rs.moveToFirst();

        listBesediInfo.ensureCapacity(rs.getCount());

        besediUniqueYears = new Integer[0];
        String fromString = getResources().getString(R.string.list_besedi_from_years_string);
        String toString = getResources().getString(R.string.list_besedi_to_years_string);

        for (int i=1; i <=rs.getCount(); i++) {

            Integer besedaID = i+1;
            String besedaName = rs.getString(rs.getColumnIndex("Name"));
            String besedaDateYear = rs.getString(rs.getColumnIndex("Year_"));
            String besedaDateMonth = rs.getString(rs.getColumnIndex("Month_"));
            String besedaDateDay = rs.getString(rs.getColumnIndex("Day_of_Month"));
            String besedaLink = rs.getString(rs.getColumnIndex("Link"));
            listBesediInfo.add(new besedaInfo(besedaID, besedaName, besedaDateYear, besedaDateMonth, besedaDateDay, besedaLink));

            if (    (checkIntegerArrayContains(besediUniqueYears, rs.getInt(rs.getColumnIndex("Year_")) ) == false ) &&
                    (besedaDateYear!="")
                    ) {
                besediUniqueYears = addIntegerToArray (besediUniqueYears, rs.getInt(rs.getColumnIndex("Year_")));
            }

            rs.moveToNext();
        }

        if (!rs.isClosed())  {
            rs.close();
        }

        besediFromYears = new String[besediUniqueYears.length];
        besediFromYears = createYearsStringArray(besediUniqueYears, fromString);
        besediToYears = new String[besediUniqueYears.length];
        besediToYears = createYearsStringArray(besediUniqueYears, toString);

    }

    private static boolean checkIntegerArrayContains(Integer[] integerArray, Integer integerToSearch)
    {
        boolean result = false;
        for (Integer element:integerArray) {
            if ( element.equals(integerToSearch) ) {
                result = true;
                break;
            }
        }
        return result;
    }

    private static Integer[] addIntegerToArray(Integer[] integerArray, Integer newValue)
    {
        Integer[] tempArray = new Integer[ integerArray.length + 1 ];
        for (int i=0; i<integerArray.length; i++)
        {
            tempArray[i] = integerArray[i];
        }
        tempArray[integerArray.length] = newValue;
        return tempArray;
    }

    private static String[] createYearsStringArray(Integer[] integerArray, String beginningString)
    {
        String[] tempArray = new String[integerArray.length];
        for (int i=0; i<integerArray.length; i++)
        {
            tempArray[i] = beginningString + " " + integerArray[i].toString();
        }
        return tempArray;
    }

    public void updateListBesedi (View view) {

        Integer selectedFromYears = spinerFromYears.getSelectedItemPosition();
        Integer selectedToYears = spinerToYears.getSelectedItemPosition();

        if (selectedFromYears > selectedToYears) {
            Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.not_valid_filter_years_string), LENGTH_LONG);
            toast.show();
            return;
        }

        Cursor rs = mydb.getbesediInfoFiltered(BesediTypeCyrillic,
                        besediUniqueYears[selectedFromYears], besediUniqueYears[selectedToYears]);
        rs.moveToFirst();

        listBesediInfo.clear();
        listBesediInfo.ensureCapacity(rs.getCount());

        for (int i=1; i <=rs.getCount(); i++) {

            Integer besedaID = i+1;
            String besedaName = rs.getString(rs.getColumnIndex("Name"));
            String besedaDateYear = rs.getString(rs.getColumnIndex("Year_"));
            String besedaDateMonth = rs.getString(rs.getColumnIndex("Month_"));
            String besedaDateDay = rs.getString(rs.getColumnIndex("Day_of_Month"));
            String besedaLink = rs.getString(rs.getColumnIndex("Link"));
            listBesediInfo.add(new besedaInfo(besedaID, besedaName, besedaDateYear, besedaDateMonth, besedaDateDay, besedaLink));

            rs.moveToNext();
        }

        if (!rs.isClosed())  {
            rs.close();
        }

        besediInfoAdapter.notifyDataSetChanged();

    }


    /***** Adapter class extends with ArrayAdapter ******/
    public class SpinnerBesediListAdapter extends ArrayAdapter<String>{

        private Activity activity;
        private String[] data;
        LayoutInflater inflater;

        /*************  CustomAdapter Constructor *****************/
        public SpinnerBesediListAdapter(
                BesediListActivity activitySpinner,
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
            View row = inflater.inflate(R.layout.spinner_besedi_list_top, parent, false);
            TextView textView1        = row.findViewById(R.id.textViewSpinnerBesediListTop);
            textView1.setText(data[position]);
            return row;
        }

        // This funtion called for each row ( Called data.size() times )
        public View getCustomView(int position, View convertView, ViewGroup parent) {

            /********** Inflate spinner_rows.xml file for each row ( Defined below ) ************/
            View row = inflater.inflate(R.layout.spinner_besedi_list_item, parent, false);
            TextView textView1        = row.findViewById(R.id.textViewSpinnerBesediList);
            textView1.setText(data[position]);
            return row;
        }
    }



}

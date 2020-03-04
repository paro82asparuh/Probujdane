package com.grigorov.asparuh.probujdane;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class FormuliMenuActivity extends AppCompatActivity {

    private ArrayList<Formula> listFormuli= new ArrayList<Formula>();
    private FormulaAdapter formulaAdapter;
    private formuliDBHelper mydb;

    public class FormulaAdapter extends ArrayAdapter<Formula> {

        // View lookup cache
        private class ViewHolder {
            TextView formulaShownText;
        }

        public FormulaAdapter (Context context, ArrayList<Formula> formuli) {
            super(context, 0, formuli);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // Get the data item for this position
            final Formula currentFormula = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            FormulaAdapter.ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                // If there's no view to re-use, inflate a brand new view for row
                viewHolder = new FormulaAdapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.formuli_list_item, parent, false);
                viewHolder.formulaShownText = convertView.findViewById(R.id.formulaShownText);
                // Cache the viewHolder object inside the fresh view
                convertView.setTag(viewHolder);
            } else {
                // View is being recycled, retrieve the viewHolder object from tag
                viewHolder = (FormulaAdapter.ViewHolder) convertView.getTag();
            }
            // Lookup view for data population
            //TextView tvBesedaName = (TextView) convertView.findViewById(R.id.textBesediName);
            //TextView tvBesedaDate = (TextView) convertView.findViewById(R.id.textBesediDate);
            // Populate the data into the template view using the data object
            String formulihownText = "";
            formulihownText = currentFormula.getTitle();
            if (formulihownText.equals("")==false) {
                formulihownText += "\n";
            }
            formulihownText += currentFormula.getText();
            viewHolder.formulaShownText.setText(formulihownText);
            // Return the completed view to render on screen
            return convertView;
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formuli_menu);

        // Read from the database
        mydb = new formuliDBHelper(this);
        setListFormuli();

        formulaAdapter = new FormulaAdapter(this, listFormuli);
        ListView listView1 = (ListView) findViewById(R.id.listViewFormuli);
        listView1.setAdapter(formulaAdapter);
    
    }

    public void onResume () {
        super.onResume();
        mydb = new formuliDBHelper(this);
    }

    public void onPause () {
        super.onPause();
        mydb.close();
    }

    public void startSearchMenuTask (View view) {
        Intent intent = new Intent(this, SearchMenuActivity.class);
        startActivity(intent);
    }

    public void startOptionsMenuTask (View view) {
        Intent intent = new Intent(this, OptionsMenuActivity.class);
        startActivity(intent);
    }

    public void setListFormuli () {

        Cursor rs = mydb.getFormuli();
        rs.moveToFirst();

        listFormuli.ensureCapacity(rs.getCount());

        for (int i=1; i <=rs.getCount(); i++) {

            //int formulaID = Integer.parseInt(rs.getString(rs.getColumnIndex("ID")));
            int formulaID = 5;
            String formulaText = rs.getString(rs.getColumnIndex("Text"));
            String formulaTitle = rs.getString(rs.getColumnIndex("Title"));
            listFormuli.add(new Formula(formulaID, formulaTitle, formulaText));

            rs.moveToNext();
        }

        if (!rs.isClosed())  {
            rs.close();
        }

    }






}

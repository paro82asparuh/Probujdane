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

public class MolitviMenuActivity extends AppCompatActivity {


    MolitvaAdapter molitvaAdapter;

    private String screenWidthInPixels;

    private MolitviDBHelper mydb;

    private ArrayList<Molitva> listMolitvi= new ArrayList<Molitva>();

    public class MolitvaAdapter extends ArrayAdapter<Molitva> {

        // View lookup cache
        private class ViewHolder {
            TextView molitvaTitle;
        }

        public MolitvaAdapter (Context context, ArrayList<Molitva> molitva) {
            super(context, 0, molitva);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // Get the data item for this position
            final Molitva currentMolitva = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            MolitviMenuActivity.MolitvaAdapter.ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                // If there's no view to re-use, inflate a brand new view for row
                viewHolder = new MolitviMenuActivity.MolitvaAdapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.molitvi_list_item, parent, false);
                viewHolder.molitvaTitle = convertView.findViewById(R.id.textMolitvaTitle);
                // Cache the viewHolder object inside the fresh view
                convertView.setTag(viewHolder);
            } else {
                // View is being recycled, retrieve the viewHolder object from tag
                viewHolder = (MolitviMenuActivity.MolitvaAdapter.ViewHolder) convertView.getTag();
            }
            // Populate the data into the template view using the data object
            viewHolder.molitvaTitle.setText(currentMolitva.getMolitvaTitle());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    screenWidthInPixels = ((Integer) (findViewById(R.id.listViewMolitvi).getWidth())).toString();
                    Intent intent = new Intent(MolitviMenuActivity.this, MolitvaActivity.class);
                    intent.putExtra("com.grigorov.asparuh.probujdane.MolitvaTitleVar", currentMolitva.getMolitvaTitle());
                    intent.putExtra("com.grigorov.asparuh.probujdane.MolitvaTextVar", currentMolitva.getMolitvaText());
                    intent.putExtra("com.grigorov.asparuh.probujdane.MolitvaMarkersVar", "");
                    intent.putExtra("com.grigorov.asparuh.probujdane.screenWidthInPixels", screenWidthInPixels);
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
        setContentView(R.layout.activity_molitvi_menu);

        // Read from the database
        mydb = new MolitviDBHelper(this);
        setListMolitvi();

        molitvaAdapter = new MolitviMenuActivity.MolitvaAdapter(this, listMolitvi);
        ListView listView1 = (ListView) findViewById(R.id.listViewMolitvi);
        listView1.setAdapter(molitvaAdapter);

    }

    public void onResume () {
        super.onResume();
        mydb = new MolitviDBHelper(this);
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

    public void setListMolitvi () {

        Cursor rs = mydb.getMolitvi();
        rs.moveToFirst();

        listMolitvi.ensureCapacity(rs.getCount());

        for (int i=1; i <=rs.getCount(); i++) {
            String molitvaTitle = rs.getString(rs.getColumnIndex("Title"));
            String molitvaText = rs.getString(rs.getColumnIndex("Text"));
            listMolitvi.add(new Molitva(molitvaTitle, molitvaText));
            rs.moveToNext();
        }

        if (!rs.isClosed())  {
            rs.close();
        }

    }

}

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class NaukaVyzpitanieMenuActivity extends AppCompatActivity {

    private NaukaVyzDBHelper mydb;
    private Cursor rs;

    private ArrayList<ChapterNaukaVyz> listChapters = new ArrayList<ChapterNaukaVyz>();

    private String screenWidthInPixels;

    TextView textNaukaVyzMenuTitle;

    public class ChaptersAdapter extends ArrayAdapter<ChapterNaukaVyz> {

        // View lookup cache
        private class ViewHolder {
            TextView chapterTitle;
        }

        public ChaptersAdapter (Context context, ArrayList<ChapterNaukaVyz> chapters) {
            super(context, 0, chapters);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // Get the data item for this position
            final ChapterNaukaVyz currentChapterNaukaVyz = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ChaptersAdapter.ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                // If there's no view to re-use, inflate a brand new view for row
                viewHolder = new ChaptersAdapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.book_menu_chapter_item, parent, false);
                viewHolder.chapterTitle = convertView.findViewById(R.id.textChapterTitle);
                // Cache the viewHolder object inside the fresh view
                convertView.setTag(viewHolder);
            } else {
                // View is being recycled, retrieve the viewHolder object from tag
                viewHolder = (ChaptersAdapter.ViewHolder) convertView.getTag();
            }
            // Lookup view for data population
            //TextView tvBesedaName = (TextView) convertView.findViewById(R.id.textBesediName);
            //TextView tvBesedaDate = (TextView) convertView.findViewById(R.id.textBesediDate);
            // Populate the data into the template view using the data object
            String stringChapterTitle = currentChapterNaukaVyz.getTitle();
            viewHolder.chapterTitle.setText(stringChapterTitle);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewHolder.chapterTitle.getLayoutParams();
            params.weight = 25 - (Integer.parseInt(currentChapterNaukaVyz.getLevel()) - 1) * 4;
            viewHolder.chapterTitle.setLayoutParams(params);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    screenWidthInPixels = ((Integer) (findViewById(R.id.listViewNaukaVyzChapters).getWidth())).toString();
                    Intent intent = new Intent(NaukaVyzpitanieMenuActivity.this, NaukaVyzpitanieActivity.class);
                    intent.putExtra("com.grigorov.asparuh.probujdane.BookMarkersVar", "");
                    intent.putExtra("com.grigorov.asparuh.probujdane.screenWidthInPixels", screenWidthInPixels);
                    intent.putExtra("com.grigorov.asparuh.probujdane.BookScrollIndecesVar", currentChapterNaukaVyz.getID() + " 0");
                    startActivity(intent);
                }
            });
            // Return the completed view to render on screen
            return convertView;
        }

    }

    ChaptersAdapter chaptersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nauka_vyzpitanie_menu);
        mydb = new NaukaVyzDBHelper(this);
        listChapters.clear();
        rs = mydb.getChapters();
        rs.moveToFirst();
        rs.moveToNext();
        for (int i_loop=0; i_loop<rs.getCount()-1; i_loop++) {
            listChapters.add( new ChapterNaukaVyz(
                    rs.getString(rs.getColumnIndex("ID")),
                    rs.getString(rs.getColumnIndex("Chapter_Level")),
                    rs.getString(rs.getColumnIndex("Chapter_Title")),
                    rs.getString(rs.getColumnIndex("Chapter_Content")),
                    rs.getString(rs.getColumnIndex("Chapter_Indentation"))
            ));
            rs.moveToNext();
        }

        chaptersAdapter = new ChaptersAdapter(this, listChapters);
        ListView listView1 = findViewById(R.id.listViewNaukaVyzChapters);
        listView1.setAdapter(chaptersAdapter);

        chaptersAdapter.notifyDataSetChanged();

        textNaukaVyzMenuTitle = findViewById(R.id.textNaukaVyzMenuTitle);
        textNaukaVyzMenuTitle.setText(getResources().getString(R.string.nauka_vyzpitanie_string));

    }

    public void startSearchMenuTask (View view) {
        Intent intent = new Intent(this, SearchMenuActivity.class);
        intent.putExtra("com.grigorov.asparuh.probujdane.searchSource", "SEARCH_BOOKS");
        startActivity(intent);
    }

    public void startOptionsMenuTask (View view) {
        Intent intent = new Intent(this, OptionsMenuActivity.class);
        startActivity(intent);
    }

    public void startEntireNaukaVyzBook (View view) {
        screenWidthInPixels = ((Integer) (findViewById(R.id.listViewNaukaVyzChapters).getWidth())).toString();
        Intent intent = new Intent(NaukaVyzpitanieMenuActivity.this, NaukaVyzpitanieActivity.class);
        intent.putExtra("com.grigorov.asparuh.probujdane.ChapterID", "0");
        intent.putExtra("com.grigorov.asparuh.probujdane.BookMarkersVar", "");
        intent.putExtra("com.grigorov.asparuh.probujdane.screenWidthInPixels", screenWidthInPixels);
        intent.putExtra("com.grigorov.asparuh.probujdane.BookScrollIndecesVar", "0 0");
        startActivity(intent);
    }

    public void onResume () {
        super.onResume();
        mydb = new NaukaVyzDBHelper(this);
        // To DO !!! updateTextSize ();
    }

    public void onPause () {
        super.onPause();
        mydb.close();
    }

}
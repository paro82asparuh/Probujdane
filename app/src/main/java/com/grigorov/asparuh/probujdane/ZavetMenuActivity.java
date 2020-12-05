package com.grigorov.asparuh.probujdane;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.res.ResourcesCompat;
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

public class ZavetMenuActivity extends AppCompatActivity {

    private ZavetDBHelper mydb;
    private Cursor rs;

    private ArrayList<ChapterZavet> listChapters = new ArrayList<ChapterZavet>();

    private String screenWidthInPixels;

    TextView textZavetMenuTitle;

    public class ChaptersAdapter extends ArrayAdapter<ChapterZavet> {

        // View lookup cache
        private class ViewHolder {
            TextView chapterTitle;
            LinearLayout linearChapterTop;
        }

        public ChaptersAdapter (Context context, ArrayList<ChapterZavet> chapters) {
            super(context, 0, chapters);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // Get the data item for this position
            final ChapterZavet currentChapterZavet = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ChaptersAdapter.ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                // If there's no view to re-use, inflate a brand new view for row
                viewHolder = new ChaptersAdapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.book_menu_chapter_item, parent, false);
                viewHolder.chapterTitle = convertView.findViewById(R.id.textChapterTitle);
                viewHolder.linearChapterTop = convertView.findViewById(R.id.linearChapterTop);
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
            String stringChapterTitle = currentChapterZavet.getTextCenter();
            viewHolder.chapterTitle.setText(stringChapterTitle);
            String chapterColor = currentChapterZavet.getColor();
            if (chapterColor.equals("Red")) {
                viewHolder.linearChapterTop.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetRedBackgorund, null));
                viewHolder.chapterTitle.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetRedText, null));
            } else if (chapterColor.equals("Pink")) {
                viewHolder.linearChapterTop.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetPinkBackgorund, null));
                viewHolder.chapterTitle.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetPinkText, null));
            } else if (chapterColor.equals("Orange")) {
                viewHolder.linearChapterTop.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetOrangeBackgorund, null));
                viewHolder.chapterTitle.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetOrangeText, null));
            } else if (chapterColor.equals("Yellow")) {
                viewHolder.linearChapterTop.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetYellowBackgorund, null));
                viewHolder.chapterTitle.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetYellowText, null));
            } else if (chapterColor.equals("Green")) {
                viewHolder.linearChapterTop.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetGreenBackgorund, null));
                viewHolder.chapterTitle.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetGreenText, null));
            } else if (chapterColor.equals("Blue")) {
                viewHolder.linearChapterTop.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetBlueBackgorund, null));
                viewHolder.chapterTitle.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetBlueText, null));
            } else if (chapterColor.equals("Purple")) {
                viewHolder.linearChapterTop.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetPurpleBackgorund, null));
                viewHolder.chapterTitle.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetPurpleText, null));
            } else if (chapterColor.equals("Amethyst")) {
                viewHolder.linearChapterTop.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetAmethystBackgorund, null));
                viewHolder.chapterTitle.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetAmethystText, null));
            } else if (chapterColor.equals("Diamant")) {
                viewHolder.linearChapterTop.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetDiamantBackgorund, null));
                viewHolder.chapterTitle.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetDiamantText, null));
            } else if (chapterColor.equals("Bright")) {
                viewHolder.linearChapterTop.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetBrightBackgorund, null));
                viewHolder.chapterTitle.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetBrightText, null));
            } else if (chapterColor.equals("White")) {
                viewHolder.linearChapterTop.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetWhiteBackgorund, null));
                viewHolder.chapterTitle.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorZavetWhiteText, null));
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    screenWidthInPixels = ((Integer) (findViewById(R.id.listViewZavetChapters).getWidth())).toString();
                    Intent intent = new Intent(ZavetMenuActivity.this, ZavetActivity.class);
                    intent.putExtra("com.grigorov.asparuh.probujdane.BookMarkersVar", "");
                    intent.putExtra("com.grigorov.asparuh.probujdane.screenWidthInPixels", screenWidthInPixels);
                    intent.putExtra("com.grigorov.asparuh.probujdane.BookScrollIndecesVar", currentChapterZavet.getID() );
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
        setContentView(R.layout.activity_zavet_menu);
        mydb = new ZavetDBHelper(this);
        listChapters.clear();
        rs = mydb.getChapters();
        rs.moveToFirst();
        for (int i_loop=0; i_loop<rs.getCount()-1; i_loop++) {
            int currentID = Integer.parseInt(rs.getString(rs.getColumnIndex("ID")));
            int currentLevel = Integer.parseInt(rs.getString(rs.getColumnIndex("Level")));
            if ( (currentID>=7) && (currentLevel<=1)) {
                listChapters.add(new ChapterZavet(
                        rs.getString(rs.getColumnIndex("ID")),
                        rs.getString(rs.getColumnIndex("Level")),
                        rs.getString(rs.getColumnIndex("Color")),
                        rs.getString(rs.getColumnIndex("Left_Text")),
                        rs.getString(rs.getColumnIndex("Center_Text")),
                        rs.getString(rs.getColumnIndex("Right_Text")),
                        rs.getString(rs.getColumnIndex("Center_Bold"))
                ));
            }
            rs.moveToNext();
        }

        chaptersAdapter = new ChaptersAdapter(this, listChapters);
        ListView listView1 = findViewById(R.id.listViewZavetChapters);
        listView1.setAdapter(chaptersAdapter);

        chaptersAdapter.notifyDataSetChanged();

        textZavetMenuTitle = findViewById(R.id.textZavetMenuTitle);
        textZavetMenuTitle.setText(getResources().getString(R.string.zaveta_cvetnite_lychi_string));
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

    public void startEntireZavetBook (View view) {
        screenWidthInPixels = ((Integer) (findViewById(R.id.listViewZavetChapters).getWidth())).toString();
        Intent intent = new Intent(ZavetMenuActivity.this, ZavetActivity.class);
        intent.putExtra("com.grigorov.asparuh.probujdane.BookMarkersVar", "");
        intent.putExtra("com.grigorov.asparuh.probujdane.screenWidthInPixels", screenWidthInPixels);
        intent.putExtra("com.grigorov.asparuh.probujdane.BookScrollIndecesVar", "0");
        startActivity(intent);
    }

    public void onResume () {
        super.onResume();
        mydb = new ZavetDBHelper(this);
        // To DO !!! updateTextSize ();
    }

    public void onPause () {
        super.onPause();
        mydb.close();
    }


}
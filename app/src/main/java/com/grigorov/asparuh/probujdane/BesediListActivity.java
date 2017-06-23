package com.grigorov.asparuh.probujdane;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

    private ArrayList<beseda> listBesedi= new ArrayList<beseda>();

    private boolean listedByDate; // true-yes, false-by Name

    public class BesediAdapter extends ArrayAdapter<beseda> {

        // View lookup cache
        private class ViewHolder {
            TextView besedaName;
            TextView besadaDate;
        }

        public BesediAdapter (Context context, ArrayList<beseda> besedi) {
            super(context, 0, besedi);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            final beseda currentBeseda = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                // If there's no view to re-use, inflate a brand new view for row
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.besedi_list_item, parent, false);
                viewHolder.besedaName = (TextView) convertView.findViewById(R.id.textBesediName);
                viewHolder.besadaDate = (TextView) convertView.findViewById(R.id.textBesediDate);
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
            viewHolder.besedaName.setText(currentBeseda.getbesedaName());
            Date date1 = new Date();
            date1 = currentBeseda.getBesedaDate();
            SimpleDateFormat simpledateformat = new SimpleDateFormat("dd.MM.yyyy");
            final String date1_string = simpledateformat.format(date1);
            viewHolder.besadaDate.setText(date1_string);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    Intent intent = new Intent(BesediListActivity.this, BesedaActivity.class);
                    intent.putExtra("com.grigorov.asparuh.probujdane.BesedaNameVar", currentBeseda.getbesedaName());
                    intent.putExtra("com.grigorov.asparuh.probujdane.BesedaDateVar", date1_string);
                    intent.putExtra("com.grigorov.asparuh.probujdane.BesedaTextVar", currentBeseda.getBesedaText());
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
        setTextBesediNotReady();

        listedByDate=true;
        setListOrder();

        setListBesedi(setNumberBesedi());
        BesediAdapter adapter = new BesediAdapter(this, listBesedi);
        ListView listView1 = (ListView) findViewById(R.id.listViewBesedi);
        listView1.setAdapter(adapter);
    }

    private void setBesediTypeCyrillic() {
        switch (SelectedBesediType) {
            case Nedelni_Besedi: BesediTypeCyrillic="Неделни Беседи"; break;
            case OOK_Besedi: BesediTypeCyrillic="Беседи общ окултен клас"; break;
            case MOK_Besedi: BesediTypeCyrillic="Беседи младежки окултен клас"; break;
            case Utrinni_Slova_Besedi: BesediTypeCyrillic="Утринни Слова"; break;
            case Syborni_Besedi: BesediTypeCyrillic="Съборни Беседи"; break;
            case MladejkiSyborni_Besedi: BesediTypeCyrillic="Младежки съборни беседи"; break;
            case Rilski_Besedi: BesediTypeCyrillic="Рилски Беседи"; break;
            case PoslednotoSlovo_Besedi: BesediTypeCyrillic="Последното Слово"; break;
            case PredSestrite_Besedi: BesediTypeCyrillic="Беседи пред сестрите"; break;
            case PredRykovoditelite_Besedi: BesediTypeCyrillic="Беседи пред ръководителите"; break;
            case Izvynredni_Besedi: BesediTypeCyrillic="Изжънредни беседи"; break;
            case KlasNaDobrodetelite_Besedi: BesediTypeCyrillic="Клас на добродетелите"; break;
            default: BesediTypeCyrillic="Грешка"; break;
        }
    }

    private int setNumberBesedi() {
        switch (SelectedBesediType) {
            case Nedelni_Besedi: return 20;
            case OOK_Besedi: return 21;
            case MOK_Besedi: return 22;
            case Utrinni_Slova_Besedi: return 23;
            case Syborni_Besedi: return 24;
            case MladejkiSyborni_Besedi: return 25;
            case Rilski_Besedi: return 26;
            case PoslednotoSlovo_Besedi: return 27;
            case PredSestrite_Besedi: return 28;
            case PredRykovoditelite_Besedi: return 29;
            case Izvynredni_Besedi: return 30;
            case KlasNaDobrodetelite_Besedi: return 31;
            default: return 40;
        }
    }


    private void setTextBesediNotReady() {
        String setTextBesediNotReady;
        setTextBesediNotReady = " Страницата " + BesediTypeCyrillic + " е в процес на разработка ";
        TextView textview1 = (TextView) findViewById(R.id.textBesediNotReady);
        textview1.setText(setTextBesediNotReady);
    }

    private void setListOrder() {
        String setTextListOrder;
        if (listedByDate==true) { setTextListOrder = "Подреди по азбучен ред"; }
        else { setTextListOrder = "Подреди по дата"; }
        Button button1 = (Button) findViewById(R.id.buttonListOrder);
        button1.setText(setTextListOrder);
    }

    public void invertListOrder(View view) {
        listedByDate = listedByDate != true;
        setListOrder();
    }

    public void setListBesedi (int NumberBesedi) {

        listBesedi.ensureCapacity(NumberBesedi);

        for (int i=1; i <=NumberBesedi; i++) {
            String besedaText = new String(i+" Текст ");
            for (int j = 0; j<1000; j++) {
                besedaText = besedaText + i + " Текст ";
            }
            String besedaName = new String("Беседа номер: "+i);
            String aDate = new String(String.valueOf(1900+i)+"."+String.valueOf(i%12)+"."+String.valueOf(i%28));
            //String aDate = new String(String.valueOf(1900+i)+".01.01");
            ParsePosition pos = new ParsePosition(0);
            SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy.MM.dd");
            Date besedaDate = simpledateformat.parse(aDate, pos);
            listBesedi.add(new beseda(besedaText, besedaName, besedaDate));
        }

    }


}

package com.grigorov.asparuh.probujdane;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class BesediMenuActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_besedi_menu);
    }

    public void startDrugiBesediMenuTask (View view) {
        Intent intent = new Intent(this, DrugiBesediMenuActivity.class);
        startActivity(intent);
    }

    public void startSearchMenuTask (View view) {
        Intent intent = new Intent(this, SearchMenuActivity.class);
        intent.putExtra("com.grigorov.asparuh.probujdane.searchSource", "SEARCH_ALL_BESEDI");
        startActivity(intent);
    }

    public void startNedelniBesediTask(View view) {
        SelectedBesediType = Nedelni_Besedi;
        startBesediListTask(view);
    }

    public void startOOKBesediTask(View view) {
        SelectedBesediType = OOK_Besedi;
        startBesediListTask(view);
    }

    public void startMOKBesediTask(View view) {
        SelectedBesediType = MOK_Besedi;
        startBesediListTask(view);
    }

    public void startUtrinniSlovaBesediTask(View view) {
        SelectedBesediType = Utrinni_Slova_Besedi;
        startBesediListTask(view);
    }

    public void startSyborniBesediTask(View view) {
        SelectedBesediType = Syborni_Besedi;
        startBesediListTask(view);
    }

    private void startBesediListTask(View view) {
        Intent intent = new Intent(this, BesediListActivity.class);
        intent.putExtra("com.grigorov.asparuh.probujdane.SelectedBesediTypeVar", SelectedBesediType);
        startActivity(intent);
    }

}

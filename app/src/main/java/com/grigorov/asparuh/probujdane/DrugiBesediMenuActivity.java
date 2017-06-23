package com.grigorov.asparuh.probujdane;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class DrugiBesediMenuActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_drugi_besedi_menu);
    }
    public void startNotificationsTask (View view) {
        Intent intent = new Intent(this, NotificationsMenuActivity.class);
        startActivity(intent);
    }

    public void startSearchMenuTask (View view) {
        Intent intent = new Intent(this, SearchMenuActivity.class);
        startActivity(intent);
    }

    public void startSyborniBesediTask(View view) {
        SelectedBesediType = Syborni_Besedi;
        startBesediListTask(view);
    }

    public void startMladejkiSyboriBesediTask(View view) {
        SelectedBesediType = MladejkiSyborni_Besedi;
        startBesediListTask(view);
    }

    public void startRilskiBesediTask(View view) {
        SelectedBesediType = Rilski_Besedi;
        startBesediListTask(view);
    }

    public void startPoslednotoSlovoBesediTask(View view) {
        SelectedBesediType = PoslednotoSlovo_Besedi;
        startBesediListTask(view);
    }

    public void startPredSestriteBesediTask(View view) {
        SelectedBesediType = PredSestrite_Besedi;
        startBesediListTask(view);
    }

    public void startPredRykovoditeliteBesediTask(View view) {
        SelectedBesediType = PredRykovoditelite_Besedi;
        startBesediListTask(view);
    }

    public void startIzvynredniBesediTask(View view) {
        SelectedBesediType = Izvynredni_Besedi;
        startBesediListTask(view);
    }

    public void startKlasNaDobroteteliteBesediTask(View view) {
        SelectedBesediType = KlasNaDobrodetelite_Besedi;
        startBesediListTask(view);
    }


    private void startBesediListTask(View view) {
        Intent intent = new Intent(this, BesediListActivity.class);
        intent.putExtra("com.grigorov.asparuh.probujdane.SelectedBesediTypeVar", SelectedBesediType);
        startActivity(intent);
    }

}

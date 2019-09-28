package com.grigorov.asparuh.probujdane;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;

import java.util.ArrayList;

public class MusicEntireActivity extends AppCompatActivity {

    private musicDBHelper mydb;

    private int musicState;
    private int musicStateOld;
    public final static int STATE_MENU =1;
    public final static int STATE_SONGS_LIST =2;
    public final static int STATE_PANEVRITMIA_LIST = 3;
    public final static int STATE_INSTRUMENTAl_LIST = 4;
    public final static int STATE_SONG_SINGLE = 5;

    TextView buttonMusicPlayPause;
    TextView musicInfoText;

    private LinearLayout topMusicLinearLayout;

    private ArrayList<songInfo> listSongsInfo= new ArrayList<songInfo>();

    private String SongsType;

    SongsInfoAdapter songsInfoAdapter;

    private int scrollViewFirstVisiblePosition;

    private Song songOnScreen;
    private Song songSinlgePlayed;

    private ArrayList<Song> songList= new ArrayList<Song>();

    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;

    private int positionPlayedSong;
    private boolean songIsPaused;

    private BroadcastReceiver broadcastReceiver;

    public class SongsInfoAdapter extends ArrayAdapter<songInfo> {

        // View lookup cache
        private class ViewHolder {
            TextView songName;
            TextView songListen;
            View songPlay;
        }

        public SongsInfoAdapter (Context context, ArrayList<songInfo> songInfo) {
            super(context, 0, songInfo);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // Get the data item for this position
            final songInfo currentSongInfo = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            MusicEntireActivity.SongsInfoAdapter.ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                // If there's no view to re-use, inflate a brand new view for row
                viewHolder = new MusicEntireActivity.SongsInfoAdapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.music_songs_list_item, parent, false);
                viewHolder.songName = (TextView) convertView.findViewById(R.id.textSongName);
                viewHolder.songListen = (TextView) convertView.findViewById(R.id.textSongListen);
                viewHolder.songPlay = (View) convertView.findViewById(R.id.textSongPlay);
                // Cache the viewHolder object inside the fresh view
                convertView.setTag(viewHolder);
            } else {
                // View is being recycled, retrieve the viewHolder object from tag
                viewHolder = (MusicEntireActivity.SongsInfoAdapter.ViewHolder) convertView.getTag();
            }
            // Lookup view for data population
            // Populate the data into the template view using the data object
            viewHolder.songName.setText(currentSongInfo.getSongName());
            if (currentSongInfo.isSongPlayble()==false) {
                viewHolder.songListen.setText("");
                viewHolder.songPlay.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorMain));
            } else {
                viewHolder.songListen.setText(getResources().getString(R.string.music_listen_string));
                viewHolder.songPlay.setBackgroundResource(R.drawable.music_speaker);
                viewHolder.songListen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        startPlayingSingleSong(currentSongInfo.getSongID());
                    }
                });
                viewHolder.songPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        startPlayingSingleSong(currentSongInfo.getSongID());
                    }
                });
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    // Put the logic on press here
                    // Store the scroll view location
                    ListView listView1 = (ListView) findViewById(R.id.listViewMusicSongs);
                    scrollViewFirstVisiblePosition = listView1.getFirstVisiblePosition();
                    // Invoke the song layout
                    showSongLayout(currentSongInfo.getSongID());
                }
            });
            // Return the completed view to render on screen
            return convertView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_entire);

        topMusicLinearLayout = (LinearLayout) findViewById(R.id.topMusicLinearLayout);

        // Initialize the database
        mydb = new musicDBHelper(this);

        scrollViewFirstVisiblePosition = 0;

        songIsPaused = false;

        startMenu();

        registerBroadcastReceiver();

        buttonMusicPlayPause = (TextView) findViewById(R.id.buttonMusicPlayPause);
        musicInfoText  = (TextView) findViewById(R.id.musicInfoText);
        musicInfoText.setText(getResources().getString(R.string.no_music_selected_string));

    }

    private void registerBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (musicBound==true) {
                    unbindService(musicConnection);
                    musicInfoText.setText(getResources().getString(R.string.no_music_selected_string));
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("com.grigorov.asparuh.probujdane.IntentToUnbindMusicService"));
    }

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setList(songList);
            musicBound = true;
            musicSrv.setSong(0);
            musicSrv.playSong();

        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
            musicSrv=null;
        }
    };

    public void startMenu () {
        musicState = STATE_MENU;
        // Load music menu on creation
        topMusicLinearLayout.removeAllViews();
        View inflatedLayout= getLayoutInflater().inflate(R.layout.music_menu, null, false);
        topMusicLinearLayout.addView(inflatedLayout);
        scrollViewFirstVisiblePosition = 0;

    }

    public void startSongsListTask (View view) {
        musicState = STATE_SONGS_LIST;
        musicStateOld = musicState;

        topMusicLinearLayout.removeAllViews();
        View inflatedLayout= getLayoutInflater().inflate(R.layout.music_songs_list, null, false);
        topMusicLinearLayout.addView(inflatedLayout);

        SongsType = "Songs";
        setListSongs();
        showListSongs();

    }

    public void startPanevritmiaTask (View view) {
        musicState = STATE_PANEVRITMIA_LIST;
        musicStateOld = musicState;

        topMusicLinearLayout.removeAllViews();
        View inflatedLayout= getLayoutInflater().inflate(R.layout.music_songs_list, null, false);
        topMusicLinearLayout.addView(inflatedLayout);

        SongsType = "Panevrtimia";
        setListSongs();
        showListSongs();

    }

    public void startPanevritmiaInstrumentalTask (View view) {
        musicState = STATE_INSTRUMENTAl_LIST;
        musicStateOld = musicState;

        topMusicLinearLayout.removeAllViews();
        View inflatedLayout= getLayoutInflater().inflate(R.layout.music_songs_list, null, false);
        topMusicLinearLayout.addView(inflatedLayout);

        SongsType = "Panevrtimia_Instumental";
        setListSongs();
        showListSongs();

    }

    public void setListSongs() {

        Cursor rs = mydb.getSongsInfo(SongsType);
        rs.moveToFirst();

        listSongsInfo.clear();
        listSongsInfo.ensureCapacity(rs.getCount());

        for (int i=1; i <=rs.getCount(); i++) {
            String songID = rs.getString(rs.getColumnIndex("ID"));
            String songTitle = rs.getString(rs.getColumnIndex("Title"));
            boolean songPlayable = true;
            if (rs.getString(rs.getColumnIndex("File_Name")).equals("")) songPlayable=false;
            listSongsInfo.add(new songInfo(songID, songTitle, songPlayable));
            rs.moveToNext();
        }

        if (!rs.isClosed())  {
            rs.close();
        }
    }

    public void showListSongs() {
        songsInfoAdapter = new MusicEntireActivity.SongsInfoAdapter(this, listSongsInfo);
        ListView listView1 = (ListView) findViewById(R.id.listViewMusicSongs);
        listView1.setAdapter(songsInfoAdapter);
        listView1.setSelection(scrollViewFirstVisiblePosition);
    }

    public void showSongLayout (String songID) {
        musicState = STATE_SONG_SINGLE;

        topMusicLinearLayout.removeAllViews();
        View inflatedLayout= getLayoutInflater().inflate(R.layout.music_song, null, false);
        topMusicLinearLayout.addView(inflatedLayout);

        Cursor rs = mydb.getSongSingle(songID);
        rs.moveToFirst();

        songOnScreen = new Song (songID,
                rs.getString(rs.getColumnIndex("Title")),
                rs.getString(rs.getColumnIndex("Text")),
                rs.getString(rs.getColumnIndex("Type_")),
                rs.getString(rs.getColumnIndex("File_Name"))
            );

        if (!rs.isClosed())  {
            rs.close();
        }

        // Populate Song details into the layout
        TextView textViewSongTitle = (TextView) findViewById(R.id.textSongTitle);
        textViewSongTitle.setText(songOnScreen.getSongName());
        TextView textViewSongText = (TextView) findViewById(R.id.textSongText);
        textViewSongText.setText(songOnScreen.getSongText());
        View viewSongPlay = (View) findViewById(R.id.viewSongPlay);
        if (songOnScreen.isSongPlayble()==false) {
            viewSongPlay.setBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite));
        } else {
            viewSongPlay.setBackgroundResource(R.drawable.music_speaker_wb);
        }

    }

    @Override
    public void onBackPressed() {
        switch (musicState) {
            case STATE_MENU:
                finish();
                break;
            case STATE_SONGS_LIST:
                startMenu();
                break;
            case STATE_PANEVRITMIA_LIST:
                startMenu();
                break;
            case STATE_INSTRUMENTAl_LIST:
                startMenu();
                break;
            case STATE_SONG_SINGLE: {
                    switch (musicStateOld) {
                        case STATE_SONGS_LIST:
                            startSongsListTask(new View(getApplicationContext()));
                            break;
                        case STATE_PANEVRITMIA_LIST:
                            startPanevritmiaTask(new View(getApplicationContext()));
                            break;
                        case STATE_INSTRUMENTAl_LIST:
                            startPanevritmiaInstrumentalTask(new View(getApplicationContext()));
                            break;
                        default:
                            finish();
                    }
                }
                break;
            default:
                finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // was initService content in the example, move to the actual music starts !!!
    }

    public void initService () {
        //if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            //startService(playIntent); // may not be needed!
        //}
    }

    public void onButtonSongPlayPressed (View view) {
        startPlayingSingleSong(songOnScreen.getSongID());
    }

    public void startPlayingSingleSong (String songID) {

        Cursor rs = mydb.getSongSingle(songID);
        rs.moveToFirst();

        songSinlgePlayed = new Song (songID,
                rs.getString(rs.getColumnIndex("Title")),
                rs.getString(rs.getColumnIndex("Text")),
                rs.getString(rs.getColumnIndex("Type_")),
                rs.getString(rs.getColumnIndex("File_Name"))
        );

        if (!rs.isClosed())  {
            rs.close();
        }

        musicInfoText.setText(songSinlgePlayed.getSongName());

        songList.clear();
        songList.add(songSinlgePlayed);

        initService();
    }

    public void onButtonMusicStopPressed (View view) {
        if (musicBound==true) {
            unbindService(musicConnection);
        }
        buttonMusicPlayPause.setBackgroundResource(R.drawable.music_play);
        musicInfoText.setText(getResources().getString(R.string.no_music_selected_string));
        songIsPaused = true;
    }

    @Override
    protected void onDestroy() {
        if (musicBound==true) {
            unbindService(musicConnection);
        }
        super.onDestroy();
    }

    public void onButtonMusicPrevPressed (View view) {
        musicSrv.playPrev();
    }

    public void onButtonMusicNextPressed (View view) {
        musicSrv.playNext();
    }

    public void onButtonMusicPlayPausePressed (View view) {
        if ( (musicBound==true) && (musicSrv.isPlaying()==true) ) {
            buttonMusicPlayPause.setBackgroundResource(R.drawable.music_play);
            musicSrv.pausePlayer();
            positionPlayedSong = musicSrv.getCurrentPosition();
            songIsPaused = true;
        } else {
            if ( (musicBound==true) && (songIsPaused==true) ) {
                buttonMusicPlayPause.setBackgroundResource(R.drawable.music_pause);
                musicSrv.seekTo(positionPlayedSong);
                musicSrv.go();
                songIsPaused = false;
            } else {
                if ( musicState == STATE_SONG_SINGLE) {
                    buttonMusicPlayPause.setBackgroundResource(R.drawable.music_pause);
                    if (musicBound==true) {
                        unbindService(musicConnection);
                    }
                    startPlayingSingleSong(songOnScreen.getSongID());
                }
            }
        }
    }



}

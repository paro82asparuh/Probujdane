package com.grigorov.asparuh.probujdane;

import android.app.ActivityManager;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MusicEntireActivity extends AppCompatActivity implements PlaylistDeleteDialogFragment.PlaylistDeleteDialogListener,
                SongsDownloadDialogFragment.SongsDownloadDialogListener, SongsDeleteDiaglogFragment.SongsDeleteDialogListener {

    public final static long SONGS_FOLDER_SIZE_IN_BYTES = 743751680;

    private musicDBHelper songsDB;
    private playlistsDBhelper playlistsDB;

    private Integer musicState;
    private Integer musicStateOld;
    public final static int STATE_MENU =1;
    public final static int STATE_SONGS_LIST =2;
    public final static int STATE_PANEVRITMIA_LIST = 3;
    public final static int STATE_INSTRUMENTAl_LIST = 4;
    public final static int STATE_SONG_SINGLE = 5;
    public final static int STATE_PLAYLISTS_LIST = 6;
    public final static int STATE_PLAYLIST = 7;
    public final static int STATE_SONG_FROM_PLAYLIST = 8;
    public final static int STATE_PLAYLIST_ADD_SONG = 9;
    public final static int STATE_PLAYLIST_REMOVE_SONG = 10;
    public final static int STATE_PLAYLIST_CREATE_NEW_FROM_SONG = 11;
    public final static int STATE_SONG_FROM_SEARCH=12;
    public final static int STATE_DELETE_SONGS=13;

    private TextView buttonMusicPlayPause;
    private TextView musicInfoText;
    private SeekBar seekbarMusic;
    private int seekBarMaxUnits = 1000;

    private LinearLayout topMusicLinearLayout;

    private ArrayList<Playlist> listPlaylists= new ArrayList<Playlist>();

    private ArrayList<songInfo> listSongsInfo= new ArrayList<songInfo>();
    private ArrayList<PlaylistSongInfo> listPlaylistEditSongsInfo= new ArrayList<PlaylistSongInfo>();

    private ArrayList<Song> listPlaylistsSongs= new ArrayList<Song>();

    private String songsType;

    SongsInfoAdapter songsInfoAdapter;
    PlaylistsListAdapter playlistsListAdapter;
    PlaylistAdapter playlistAdapter;
    PlaylistEditSongsInfoAdapter playlistEditSongsInfoAdapter;
    PlaylistsSelectAdapter playlistsSelectAdapter;

    private int scrollViewSongsListFirstVisiblePosition;
    private int scrollViewPlaylistsListFirstVisiblePosition;
    private int scrollViewPlaylistFirstVisiblePosition;

    private Song songOnScreen;
    private Song songSinlgePlayed;

    private Playlist playlistOnScreen;
    private Playlist playlistSelected;
    private Playlist playlistPlayed;

    private ArrayList<Song> songList= new ArrayList<Song>();
    private int startSongNumber;

    private ArrayList<SongMarker> listSongMarkers= new ArrayList<SongMarker>();

    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;

    private int positionPlayedSong;
    private boolean songIsPaused;

    private boolean serviceInitOnlyBinding;

    private String activitySource;

    private ArrayList<Song> listDownloadSongs = new ArrayList<Song>();
    private boolean downloadingVocalFile;
    private long downloadReference;
    private DownloadManager downloadManager;

    private BroadcastReceiver broadcastReceiverEndMusicPlay;
    private BroadcastReceiver broadcastReceiverPlayAnotherSong;

    private Handler handlerUpdateSeekbar = new Handler();
    private Runnable runnableUpdateSeekbar = new Runnable() {
        @Override
        public void run() {
            if (musicBound==true) {
                if (musicSrv.isPlaying() == true) {
                    seekbarMusic.setProgress(seekBarMaxUnits * musicSrv.getCurrentPosition() / musicSrv.getDuration());
                }
            }
            handlerUpdateSeekbar.postDelayed(this, 500);
        }
    };

    public class SongsInfoAdapter extends ArrayAdapter<songInfo> {

        // View lookup cache
        private class ViewHolder {
            TextView songName;
            TextView textVocalPlay;
            TextView textInstrumentalPlay;
            View viewVocalPlay;
            View viewInstrumentalPlay;
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
                viewHolder.songName = convertView.findViewById(R.id.textSongName);
                viewHolder.textVocalPlay = convertView.findViewById(R.id.textVocalPlay);
                viewHolder.textInstrumentalPlay = convertView.findViewById(R.id.textInstrumentalPlay);
                viewHolder.viewVocalPlay = convertView.findViewById(R.id.viewVocalPlay);
                viewHolder.viewInstrumentalPlay = convertView.findViewById(R.id.viewInstrumentalPlay);
                // Cache the viewHolder object inside the fresh view
                convertView.setTag(viewHolder);
            } else {
                // View is being recycled, retrieve the viewHolder object from tag
                viewHolder = (MusicEntireActivity.SongsInfoAdapter.ViewHolder) convertView.getTag();
            }
            // Lookup view for data population
            // Populate the data into the template view using the data object
            viewHolder.songName.setText(currentSongInfo.getSongName());
            if (currentSongInfo.isSongVocalPlayble()==false) {
                viewHolder.textVocalPlay.setText("");
                viewHolder.viewVocalPlay.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorMain));
            } else {
                viewHolder.textVocalPlay.setText(getResources().getString(R.string.vocal));
                viewHolder.viewVocalPlay.setBackgroundResource(R.drawable.music_speaker);
                viewHolder.textVocalPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        startPlayingSingleSong(currentSongInfo.getSongID(),Song.PLAY_VOCAL);
                    }
                });
                viewHolder.viewVocalPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        startPlayingSingleSong(currentSongInfo.getSongID(),Song.PLAY_VOCAL);
                    }
                });
            }
            if (currentSongInfo.isSongInstrumentalPlayble()==false) {
                viewHolder.textInstrumentalPlay.setText("");
                viewHolder.viewInstrumentalPlay.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorMain));
            } else {
                viewHolder.textInstrumentalPlay.setText(getResources().getString(R.string.instrumental));
                viewHolder.viewInstrumentalPlay.setBackgroundResource(R.drawable.music_speaker);
                viewHolder.textInstrumentalPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        startPlayingSingleSong(currentSongInfo.getSongID(),Song.PLAY_INSTRUMENTAL);
                    }
                });
                viewHolder.viewInstrumentalPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        startPlayingSingleSong(currentSongInfo.getSongID(),Song.PLAY_INSTRUMENTAL);
                    }
                });
            }
            if ( (currentSongInfo.isSongVocalPlayble()==false) && (currentSongInfo.isSongInstrumentalPlayble()==false) ) {
                String currentSongID = currentSongInfo.getSongID();
                Cursor rs = songsDB.getSongSingle(currentSongID);
                rs.moveToFirst();
                String currentVocalFileName = rs.getString(rs.getColumnIndex("Vocal_File_Name"));
                String currentInstrumentalFileName = rs.getString(rs.getColumnIndex("Instrumental_File_Name"));
                if ( ( currentVocalFileName.equals("")==false) || (currentInstrumentalFileName.equals("")==false) ) {
                    viewHolder.textVocalPlay.setText("");
                    viewHolder.viewVocalPlay.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorMain));
                    viewHolder.textInstrumentalPlay.setText(getResources().getString(R.string.music_download_string));
                    viewHolder.viewInstrumentalPlay.setBackgroundResource(R.drawable.download);
                    viewHolder.textInstrumentalPlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            downloadSingleSong(currentSongInfo.getSongID());
                        }
                    });
                    viewHolder.viewInstrumentalPlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            downloadSingleSong(currentSongInfo.getSongID());
                        }
                    });
                }
                if (!rs.isClosed())  {
                    rs.close();
                }
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    // Put the logic on press here
                    // Store the scroll view location
                    ListView listView1 = findViewById(R.id.listViewMusicSongs);
                    scrollViewSongsListFirstVisiblePosition = listView1.getFirstVisiblePosition();
                    // Invoke the song layout
                    showSongLayout(currentSongInfo.getSongID(),false, -1, musicState, Song.PLAY_UNDEFINED);
                }
            });
            // Return the completed view to render on screen
            return convertView;
        }
    }


    public class PlaylistsListAdapter extends ArrayAdapter<Playlist> {

        // View lookup cache
        private class ViewHolder {
            TextView playlistName;
            TextView playlistListen;
            View playlistPlay;
        }

        public PlaylistsListAdapter (Context context, ArrayList<Playlist> playlistsList) {
            super(context, 0, playlistsList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // Get the data item for this position
            final Playlist currentPlaylist = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            MusicEntireActivity.PlaylistsListAdapter.ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                // If there's no view to re-use, inflate a brand new view for row
                viewHolder = new MusicEntireActivity.PlaylistsListAdapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.music_playlists_list_item, parent, false);
                viewHolder.playlistName = convertView.findViewById(R.id.textPlaylistName);
                viewHolder.playlistListen = convertView.findViewById(R.id.textPlaylistListen);
                viewHolder.playlistPlay = convertView.findViewById(R.id.textPlaylistPlay);
                // Cache the viewHolder object inside the fresh view
                convertView.setTag(viewHolder);
            } else {
                // View is being recycled, retrieve the viewHolder object from tag
                viewHolder = (MusicEntireActivity.PlaylistsListAdapter.ViewHolder) convertView.getTag();
            }
            // Lookup view for data population
            // Populate the data into the template view using the data object
            viewHolder.playlistName.setText(currentPlaylist.getPlaylistName());
            viewHolder.playlistListen.setText(getResources().getString(R.string.music_listen_string));
            viewHolder.playlistPlay.setBackgroundResource(R.drawable.music_speaker);
            viewHolder.playlistListen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    startPlayingPlaylist(currentPlaylist.getPlaylistID(),0);
                }
            });
            viewHolder.playlistPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    startPlayingPlaylist(currentPlaylist.getPlaylistID(),0);
                }
            });
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    // Put the logic on press here
                    // Store the scroll view location
                    ListView listView1 = findViewById(R.id.listViewMusicPlaylists);
                    scrollViewPlaylistsListFirstVisiblePosition = listView1.getFirstVisiblePosition();
                    // Invoke the song layout
                    showPlaylistLayout(currentPlaylist.getPlaylistID());
                }
            });
            // Return the completed view to render on screen
            return convertView;
        }
    }

    public class PlaylistsSelectAdapter extends ArrayAdapter<Playlist> {

        // View lookup cache
        private class ViewHolder {
            TextView playlistName;
        }

        public PlaylistsSelectAdapter (Context context, ArrayList<Playlist> playlistsList) {
            super(context, 0, playlistsList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // Get the data item for this position
            final Playlist currentPlaylist = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            MusicEntireActivity.PlaylistsSelectAdapter.ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                // If there's no view to re-use, inflate a brand new view for row
                viewHolder = new MusicEntireActivity.PlaylistsSelectAdapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.music_playlists_select_item, parent, false);
                viewHolder.playlistName = convertView.findViewById(R.id.textPlaylistName);
                // Cache the viewHolder object inside the fresh view
                convertView.setTag(viewHolder);
            } else {
                // View is being recycled, retrieve the viewHolder object from tag
                viewHolder = (MusicEntireActivity.PlaylistsSelectAdapter.ViewHolder) convertView.getTag();
            }
            // Lookup view for data population
            // Populate the data into the template view using the data object
            viewHolder.playlistName.setText(currentPlaylist.getPlaylistName());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    // Put the logic on press here
                    setPlaylistSelection(currentPlaylist.getPlaylistID());
                }
            });
            // Return the completed view to render on screen
            return convertView;
        }
    }

    public class PlaylistAdapter extends ArrayAdapter<Song> {

        // View lookup cache
        private class ViewHolder {
            TextView songName;
            View songArrowDown;
            View songArrowUp;
        }

        public PlaylistAdapter (Context context, ArrayList<Song> playlistSongs) {
            super(context, 0, playlistSongs);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // Get the data item for this position
            final Song currentSong = getItem(position);
            final int fPosition = position;
            // Check if an existing view is being reused, otherwise inflate the view
            MusicEntireActivity.PlaylistAdapter.ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                // If there's no view to re-use, inflate a brand new view for row
                viewHolder = new MusicEntireActivity.PlaylistAdapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.music_playlist_item, parent, false);
                viewHolder.songName = convertView.findViewById(R.id.textSongName);
                viewHolder.songArrowDown = convertView.findViewById(R.id.viewSongArrowDown);
                viewHolder.songArrowUp = convertView.findViewById(R.id.viewSongArrowUp);
                // Cache the viewHolder object inside the fresh view
                convertView.setTag(viewHolder);
            } else {
                // View is being recycled, retrieve the viewHolder object from tag
                viewHolder = (MusicEntireActivity.PlaylistAdapter.ViewHolder) convertView.getTag();
            }
            // Lookup view for data population
            // Populate the data into the template view using the data object

            String songTitle = currentSong.getSongName();
            if (currentSong.getSongPlayType()==Song.PLAY_VOCAL) {
                songTitle = songTitle + " - " + getResources().getString(R.string.vocal);
            } else {
                songTitle = songTitle + " - " + getResources().getString(R.string.instrumental);
            }
            viewHolder.songName.setText(songTitle);
            if (playlistOnScreen.isEditable()==false) {
                viewHolder.songArrowDown.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorMain));
                viewHolder.songArrowUp.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorMain));
            } else {
                viewHolder.songArrowDown.setBackgroundResource(R.drawable.arrow_down);
                viewHolder.songArrowUp.setBackgroundResource(R.drawable.arrow_up);
                viewHolder.songArrowDown.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        playlistOnScreen.moveSongDown( listPlaylistsSongs.get(fPosition).getSongPositionInPlaylist(),getApplicationContext());
                        listPlaylistsSongs = playlistOnScreen.getSongsArrayList();
                        playlistAdapter.notifyDataSetChanged();
                        updateMusicServicePlaylistAfterPositionChange();
                    }
                });
                viewHolder.songArrowUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        playlistOnScreen.moveSongUp(listPlaylistsSongs.get(fPosition).getSongPositionInPlaylist(),getApplicationContext());
                        listPlaylistsSongs = playlistOnScreen.getSongsArrayList();
                        playlistAdapter.notifyDataSetChanged();
                        updateMusicServicePlaylistAfterPositionChange();
                    }
                });
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    // Put the logic on press here
                    // Store the scroll view location
                    ListView listView1 = findViewById(R.id.listViewMusicPlaylist);
                    scrollViewPlaylistFirstVisiblePosition = listView1.getFirstVisiblePosition();
                    // Invoke the song layout
                    showSongLayout(currentSong.getSongID(),true, currentSong.getSongPositionInPlaylist(),
                            musicState, currentSong.getSongPlayType());
                }
            });
            // Return the completed view to render on screen
            return convertView;
        }
    }

    public class PlaylistEditSongsInfoAdapter extends ArrayAdapter<PlaylistSongInfo> {

        // View lookup cache
        private class ViewHolder {
            TextView songName;
            View viewSongTick;
            private Integer position;
            public Integer getPosition() { return position; }
        }

        public PlaylistEditSongsInfoAdapter (Context context, ArrayList<PlaylistSongInfo> playlistSongInfo) {
            super(context, 0, playlistSongInfo);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // Get the data item for this position
            final PlaylistSongInfo currentSongInfo = getItem(position);
            final int fPosition = position;
            // Check if an existing view is being reused, otherwise inflate the view
            final MusicEntireActivity.PlaylistEditSongsInfoAdapter.ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                // If there's no view to re-use, inflate a brand new view for row
                viewHolder = new MusicEntireActivity.PlaylistEditSongsInfoAdapter.ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.music_playlist_edit_item, parent, false);
                viewHolder.songName = convertView.findViewById(R.id.textSongName);
                viewHolder.viewSongTick = convertView.findViewById(R.id.viewSongTick);
                viewHolder.position = position;
                // Cache the viewHolder object inside the fresh view
                convertView.setTag(viewHolder);
            } else {
                // View is being recycled, retrieve the viewHolder object from tag
                viewHolder = (MusicEntireActivity.PlaylistEditSongsInfoAdapter.ViewHolder) convertView.getTag();
            }
            // Lookup view for data population
            // Populate the data into the template view using the data object
            viewHolder.songName.setText(currentSongInfo.getSongName());
            if (listPlaylistEditSongsInfo.get(position).getSongTicked()==true) {
                viewHolder.viewSongTick.setBackgroundResource(R.drawable.box_checked);
            } else {
                viewHolder.viewSongTick.setBackgroundResource(R.drawable.box_not_checked);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    PlaylistEditSongsInfoAdapter.ViewHolder vh = (PlaylistEditSongsInfoAdapter.ViewHolder) v.getTag();
                    //int position = vh.getPosition();
                    //int position = viewHolder.getPosition();
                    if (listPlaylistEditSongsInfo.get(fPosition).getSongTicked()==false) {
                        listPlaylistEditSongsInfo.get(fPosition).setSongTicked(true);
                        //viewHolder.viewSongTick.setBackgroundResource(R.drawable.box_checked);
                    } else {
                        listPlaylistEditSongsInfo.get(fPosition).setSongTicked(false);
                        //viewHolder.viewSongTick.setBackgroundResource(R.drawable.box_not_checked);
                    }
                    playlistEditSongsInfoAdapter.notifyDataSetChanged();
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

        // Get the source
        Intent intent = getIntent();
        activitySource = getIntent().getExtras().getString("com.grigorov.asparuh.probujdane.musicActivitySourceVar", "MainActivity");

        topMusicLinearLayout = findViewById(R.id.topMusicLinearLayout);

        // Initialize the databases
        songsDB = new musicDBHelper(this);
        playlistsDB = new playlistsDBhelper(this);

        listDownloadSongs.clear();
        downloadingVocalFile = false;
        downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, filter);

        updateSongsDownloadedStatus();

        musicState = STATE_MENU;
    }

    @Override
    protected void onStart() {
        super.onStart();

        setListPlaylistsList();

        scrollViewSongsListFirstVisiblePosition = 0;
        scrollViewPlaylistsListFirstVisiblePosition = 0;
        scrollViewPlaylistFirstVisiblePosition = 0;

        songIsPaused = false;

        //startMenu();

        registerBroadcastReceivers();

        buttonMusicPlayPause = findViewById(R.id.buttonMusicPlayPause);
        musicInfoText  = findViewById(R.id.musicInfoText);
        musicInfoText.setText(getResources().getString(R.string.no_music_selected_string));
        if (musicBound==true) {
            if (musicSrv.isPlaying()==true) {
                String musicInfoTextString = musicSrv.getPlayedSong().getSongName();
                if (musicSrv.getPlayedSong().getSongPlayType()==Song.PLAY_INSTRUMENTAL) {
                    musicInfoTextString = musicInfoTextString + " - "  + getResources().getString(R.string.instrumental);
                }
                musicInfoText.setText(musicInfoTextString);
            }
        }

        //Seekbar
        seekbarMusic = findViewById(R.id.seekbarMusic);
        seekbarMusic.setMax(seekBarMaxUnits);
        seekbarMusic.setProgress(0);
        seekbarMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if( (musicBound==true) && (musicSrv.isPlaying()==true) && (fromUser==true) ){
                    musicSrv.seekTo(musicSrv.getDuration() * progress / seekBarMaxUnits);
                }
            }
        });

        // Check if service is running
        serviceInitOnlyBinding = true;
        playIntent = new Intent(this, MusicService.class);
        bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);

        listSongMarkers.clear();
        if (activitySource.equals("SearchMenuActivity")) {
            String songMarkers =  getIntent().getExtras().getString("com.grigorov.asparuh.probujdane.FormulaMarkersVar");
            if (songMarkers.equals("")==false) {
                String[] inputSongMarkers = songMarkers.split(" "); // Split to " " to read integers
                for (int marker_loop=0; marker_loop<inputSongMarkers.length;marker_loop=marker_loop+3) {
                    listSongMarkers.add(
                            new SongMarker(
                                    Integer.parseInt(inputSongMarkers[marker_loop]),
                                    Integer.parseInt(inputSongMarkers[marker_loop+1]),
                                    Integer.parseInt(inputSongMarkers[marker_loop+2])
                            )
                    );
                }
            }
            String searchedSongID = getIntent().getExtras().getString("com.grigorov.asparuh.probujdane.SongIDVar");
            showSongLayout(searchedSongID,false, 0, musicState, Song.PLAY_UNDEFINED);
        } else {
            /* Removed as it is very complex to keep and restors all state vars
                Estimated to not be really needed for the user!
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            String stringInputMusicState = sharedPref.getString("com.grigorov.asparuh.probujdane.musicState", "1");
            Integer inputMusicState = Integer.parseInt(stringInputMusicState);
            String stringInputMusicStateOld = sharedPref.getString("com.grigorov.asparuh.probujdane.musicStateOld", "1");
            Integer inputMusicStateOld = Integer.parseInt(stringInputMusicStateOld);
            String inputMusicSongID = sharedPref.getString("com.grigorov.asparuh.probujdane.musicSongID", "1");
            String stringInputMusicSongPositionInPlaylist = sharedPref.getString("com.grigorov.asparuh.probujdane.musicSongPositionInPlaylist", "");
            String inputMusicPlaylistID = sharedPref.getString("com.grigorov.asparuh.probujdane.musicPlaylistID", "");
            Integer inputMusicSongPositionInPlaylist;
            boolean inputSongFromPlaylist;
            switch (inputMusicState) {
                case STATE_SONGS_LIST:
                    startSongsListTask(topMusicLinearLayout);
                    break;
                case STATE_PANEVRITMIA_LIST:
                    startPanevritmiaTask(topMusicLinearLayout);
                    break;
                case STATE_INSTRUMENTAl_LIST:
                    startPanevritmiaInstrumentalTask(topMusicLinearLayout);
                    break;
                case STATE_SONG_SINGLE:
                    musicStateOld = inputMusicStateOld;
                    showSongLayout(inputMusicSongID, false, -1);
                    break;
                case STATE_SONG_FROM_PLAYLIST:
                    inputMusicSongPositionInPlaylist = Integer.parseInt(stringInputMusicSongPositionInPlaylist);
                    inputSongFromPlaylist = ((inputMusicSongPositionInPlaylist.equals("-1"))==false);
                    setPlaylistOnScreen(inputMusicPlaylistID);
                    showSongLayout(inputMusicSongID, inputSongFromPlaylist, inputMusicSongPositionInPlaylist);
                    break;
                case STATE_PLAYLISTS_LIST:
                    startPlaylistsTask(topMusicLinearLayout);
                    break;
                case STATE_PLAYLIST:
                    showPlaylistLayout(inputMusicPlaylistID);
                    break;
                case STATE_PLAYLIST_ADD_SONG:
                    setPlaylistOnScreen(inputMusicPlaylistID);
                    showPlaylistAddSongLayout();
                    break;
                case STATE_PLAYLIST_REMOVE_SONG:
                    setPlaylistOnScreen(inputMusicPlaylistID);
                    showPlaylistRemoveSongLayout();
                    break;
                case STATE_PLAYLIST_CREATE_NEW_FROM_SONG:
                    musicStateOld = inputMusicStateOld;
                    inputMusicSongPositionInPlaylist = Integer.parseInt(stringInputMusicSongPositionInPlaylist);
                    inputSongFromPlaylist = ((inputMusicSongPositionInPlaylist.equals("-1"))==false);
                    setSongOnScreen(inputMusicSongID, inputMusicSongPositionInPlaylist);
                    onButtonSongAdd2PlaylistPressed(topMusicLinearLayout);
                    break;
                default:
            }
             */
            switch (musicState) {
                case STATE_MENU:
                    startMenu();
                    break;
                case STATE_SONGS_LIST:
                    startSongsListTask(topMusicLinearLayout);
                    break;
                case STATE_PANEVRITMIA_LIST:
                    startPanevritmiaTask(topMusicLinearLayout);
                    break;
                case STATE_SONG_SINGLE:
                    showSongLayout(songOnScreen.getSongID(), false, -1, musicStateOld, songOnScreen.getSongPlayType());
                    break;
                case STATE_SONG_FROM_PLAYLIST:
                    showSongLayout(songOnScreen.getSongID(), true, songOnScreen.getSongPositionInPlaylist(),
                            musicStateOld, songOnScreen.getSongPlayType());
                    break;
                case STATE_PLAYLISTS_LIST:
                    startPlaylistsTask(topMusicLinearLayout);
                    break;
                case STATE_PLAYLIST:
                    showPlaylistLayout(playlistOnScreen.getPlaylistID());
                    break;
                case STATE_PLAYLIST_ADD_SONG:
                    showPlaylistAddSongLayout();
                    break;
                case STATE_PLAYLIST_REMOVE_SONG:
                    showPlaylistRemoveSongLayout();
                    break;
                case STATE_PLAYLIST_CREATE_NEW_FROM_SONG:
                    onButtonSongAdd2PlaylistPressed(topMusicLinearLayout);
                    break;
                case STATE_SONG_FROM_SEARCH:
                    showSongLayout(songOnScreen.getSongID(), false, -1, musicStateOld, Song.PLAY_UNDEFINED);
                    break;
                case STATE_DELETE_SONGS:
                    showDeleteSongs();
                    break;
                default:
            }
        }

    }

    private void registerBroadcastReceivers() {
        broadcastReceiverEndMusicPlay = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (musicBound==true) {
                    musicBound = false;
                    unbindService(musicConnection);
                    musicInfoText.setText(getResources().getString(R.string.no_music_selected_string));
                    seekbarMusic.setProgress(0);
                    handlerUpdateSeekbar.removeCallbacks(runnableUpdateSeekbar);
                }
            }
        };
        registerReceiver(broadcastReceiverEndMusicPlay, new IntentFilter("com.grigorov.asparuh.probujdane.IntentToUnbindMusicService"));
        broadcastReceiverPlayAnotherSong = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String musicInfoTextString = playlistPlayed.getPlaylistName()
                        + " - "
                        + songList.get(musicSrv.getSongPosn()).getSongName();
                if (songList.get(musicSrv.getSongPosn()).getSongPlayType()==Song.PLAY_INSTRUMENTAL) {
                    musicInfoTextString = musicInfoTextString + " - "  + getResources().getString(R.string.instrumental);
                }
                musicInfoText.setText( musicInfoTextString );
                seekbarMusic.setProgress(0);
                if (musicState == STATE_SONG_FROM_PLAYLIST) {
                    showSongLayout(songList.get(musicSrv.getSongPosn()).getSongID(),
                            true,
                            musicSrv.getSongPosn(),
                            musicStateOld,
                            songList.get(musicSrv.getSongPosn()).getSongPlayType()
                    );
                }
            }
        };
        registerReceiver(broadcastReceiverPlayAnotherSong, new IntentFilter("com.grigorov.asparuh.probujdane.intentToPlayAnotherSong"));
    }

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            musicBound = true;
            if (serviceInitOnlyBinding==true) {
                if (musicSrv.isPlaying() == true) {
                    if (musicSrv.getSongsSize()>1) {
                        playlistPlayed = musicSrv.getPlayedPlaylist();
                        songList = new ArrayList<Song>(playlistPlayed.getSongsArrayList());
                        String musicInfoTextString = playlistPlayed.getPlaylistName()
                                + " - "
                                + songList.get(musicSrv.getSongPosn()).getSongName();
                        if (songList.get(musicSrv.getSongPosn()).getSongPlayType()==Song.PLAY_INSTRUMENTAL) {
                            musicInfoTextString = musicInfoTextString + " - "  + getResources().getString(R.string.instrumental);
                        }
                        musicInfoText.setText( musicInfoTextString );
                    } else {
                        songSinlgePlayed = musicSrv.getPlayedSong();
                        String musicInfoTextString = songSinlgePlayed.getSongName();
                        if (songSinlgePlayed.getSongPlayType()==Song.PLAY_INSTRUMENTAL) {
                            musicInfoTextString = musicInfoTextString + " - "  + getResources().getString(R.string.instrumental);
                        }
                        musicInfoText.setText(musicInfoTextString);
                    }
                    buttonMusicPlayPause.setBackgroundResource(R.drawable.music_pause);
                    handlerUpdateSeekbar.postDelayed(runnableUpdateSeekbar, 0);
                } else {
                    musicBound = false;
                    unbindService(musicConnection);
                    stopService(playIntent);
                }
            } else {
                //pass list
                musicSrv.setList(songList);
                musicSrv.setPlaylist(playlistPlayed);
                musicSrv.setSong(startSongNumber);
                musicSrv.playSong();
            }
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
        scrollViewSongsListFirstVisiblePosition = 0;
        scrollViewPlaylistsListFirstVisiblePosition = 0;
        scrollViewPlaylistFirstVisiblePosition = 0;

    }

    public void startSongsListTask (View view) {
        musicStateOld = musicState;
        musicState = STATE_SONGS_LIST;

        topMusicLinearLayout.removeAllViews();
        View inflatedLayout= getLayoutInflater().inflate(R.layout.music_songs_list, null, false);
        topMusicLinearLayout.addView(inflatedLayout);

        Cursor rs = songsDB.getSongsNotDownloaded();
        if (rs.getCount()==0) {
            View viewAboveDownload = topMusicLinearLayout.findViewById(R.id.viewAboveDownload);
            ((ViewManager)viewAboveDownload.getParent()).removeView(viewAboveDownload);
            LinearLayout linearDownload = topMusicLinearLayout.findViewById(R.id.linearDownload);
            ((ViewManager)linearDownload.getParent()).removeView(linearDownload);
            View viewBelowDownload = topMusicLinearLayout.findViewById(R.id.viewBelowDownload);
            ((ViewManager)viewBelowDownload.getParent()).removeView(viewBelowDownload);
        }
        if (!rs.isClosed())  {
            rs.close();
        }
        rs = songsDB.getSongsDownloaded("Songs");
        if (rs.getCount()==0) {
            LinearLayout linearDeleteSongs = topMusicLinearLayout.findViewById(R.id.linearDeleteSongs);
            ((ViewManager)linearDeleteSongs.getParent()).removeView(linearDeleteSongs);
            View viewBelowDeleteSongs = topMusicLinearLayout.findViewById(R.id.viewBelowDeleteSongs);
            ((ViewManager)viewBelowDeleteSongs.getParent()).removeView(viewBelowDeleteSongs);
        }
        if (!rs.isClosed())  {
            rs.close();
        }


        songsType = "Songs";
        setListSongs();
        showListSongs();

    }

    public void startPanevritmiaTask (View view) {
        musicStateOld = musicState;
        musicState = STATE_PANEVRITMIA_LIST;

        topMusicLinearLayout.removeAllViews();
        View inflatedLayout= getLayoutInflater().inflate(R.layout.music_songs_list, null, false);
        topMusicLinearLayout.addView(inflatedLayout);

        View viewAboveDownload = topMusicLinearLayout.findViewById(R.id.viewAboveDownload);
        ((ViewManager)viewAboveDownload.getParent()).removeView(viewAboveDownload);
        LinearLayout linearDownload = topMusicLinearLayout.findViewById(R.id.linearDownload);
        ((ViewManager)linearDownload.getParent()).removeView(linearDownload);
        View viewBelowDownload = topMusicLinearLayout.findViewById(R.id.viewBelowDownload);
        ((ViewManager)viewBelowDownload.getParent()).removeView(viewBelowDownload);
        LinearLayout linearDeleteSongs = topMusicLinearLayout.findViewById(R.id.linearDeleteSongs);
        ((ViewManager)linearDeleteSongs.getParent()).removeView(linearDeleteSongs);
        View viewBelowDeleteSongs = topMusicLinearLayout.findViewById(R.id.viewBelowDeleteSongs);
        ((ViewManager)viewBelowDeleteSongs.getParent()).removeView(viewBelowDeleteSongs);

        songsType = "Panevrtimia";
        setListSongs();
        showListSongs();

    }

    public void setListSongs() {

        Cursor rs = songsDB.getSongsInfo(songsType);
        rs.moveToFirst();

        listSongsInfo.clear();
        listSongsInfo.ensureCapacity(rs.getCount());

        for (int i=1; i <=rs.getCount(); i++) {
            String songID = rs.getString(rs.getColumnIndex("ID"));
            String songTitle = rs.getString(rs.getColumnIndex("Title"));
            boolean songVocalPlayable = true;
            if (rs.getString(rs.getColumnIndex("Vocal_File_Name")).equals("")) songVocalPlayable=false;
            boolean songInstrumentalPlayable = true;
            if (rs.getString(rs.getColumnIndex("Instrumental_File_Name")).equals("")) songInstrumentalPlayable=false;
            if ( Integer.parseInt(rs.getString(rs.getColumnIndex("Files_Downloaded"))) == Song.FILES_NOT_DOWNLOADED ) {
                songVocalPlayable = false;
                songInstrumentalPlayable = false;
            }
            listSongsInfo.add(new songInfo(songID, songTitle, songVocalPlayable, songInstrumentalPlayable));
            rs.moveToNext();
        }

        if (!rs.isClosed())  {
            rs.close();
        }
    }

    public void showListSongs() {
        songsInfoAdapter = new MusicEntireActivity.SongsInfoAdapter(this, listSongsInfo);
        ListView listView1 = findViewById(R.id.listViewMusicSongs);
        listView1.setAdapter(songsInfoAdapter);
        listView1.setSelection(scrollViewSongsListFirstVisiblePosition);
    }

    public void setListPlaylistsList() {

        Cursor rs = playlistsDB.getAll();
        rs.moveToFirst();

        listPlaylists.clear();
        listPlaylists.ensureCapacity(rs.getCount());

        for (int i=1; i <=rs.getCount(); i++) {
            String playlistID = rs.getString(rs.getColumnIndex("ID"));
            String playlistName = rs.getString(rs.getColumnIndex("Name"));
            String playlistStringSongs = rs.getString(rs.getColumnIndex("Songs"));
            String playlistStringPlayTypes = rs.getString(rs.getColumnIndex("PlayType"));
            listPlaylists.add(new Playlist(playlistID, playlistName, playlistStringSongs, playlistStringPlayTypes, getApplicationContext()));
            rs.moveToNext();
        }

        if (!rs.isClosed())  {
            rs.close();
        }
    }

    public void startPlaylistsTask (View view) {

        setListPlaylistsList();

        musicStateOld = musicState;
        musicState = STATE_PLAYLISTS_LIST;

        topMusicLinearLayout.removeAllViews();
        View inflatedLayout= getLayoutInflater().inflate(R.layout.music_playlists_list, null, false);
        topMusicLinearLayout.addView(inflatedLayout);

        playlistsListAdapter = new MusicEntireActivity.PlaylistsListAdapter(this, listPlaylists);
        ListView listView1 = findViewById(R.id.listViewMusicPlaylists);
        listView1.setAdapter(playlistsListAdapter);
        listView1.setSelection(scrollViewPlaylistsListFirstVisiblePosition);
    }

    private void setSongOnScreen (String songID, int inputSongPlayType, Integer songPositionInPlaylist) {
        Cursor rs = songsDB.getSongSingle(songID);
        rs.moveToFirst();

        songOnScreen = new Song (songID,
                rs.getString(rs.getColumnIndex("Title")),
                rs.getString(rs.getColumnIndex("Text")),
                rs.getString(rs.getColumnIndex("Type_")),
                rs.getString(rs.getColumnIndex("Vocal_File_Name")),
                rs.getString(rs.getColumnIndex("Instrumental_File_Name")),
                rs.getString(rs.getColumnIndex("Files_Downloaded")),
                inputSongPlayType,
                songPositionInPlaylist
        );

        if (!rs.isClosed())  {
            rs.close();
        }
    }

    public void showSongLayout (String songID, boolean fromPlaylist, Integer songPositionInPlaylist, Integer inputMusicStateOld, int inputSongPlayType) {
        musicStateOld = inputMusicStateOld;
        if (activitySource.equals("SearchMenuActivity")) {
            musicState = STATE_SONG_FROM_SEARCH;
        } else if (fromPlaylist==false) {
            musicState = STATE_SONG_SINGLE;
        } else {
            musicState = STATE_SONG_FROM_PLAYLIST;
        }

        topMusicLinearLayout.removeAllViews();
        View inflatedLayout= getLayoutInflater().inflate(R.layout.music_song, null, false);
        topMusicLinearLayout.addView(inflatedLayout);

        setSongOnScreen(songID, inputSongPlayType, songPositionInPlaylist);

        // Get text sizes
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String songTextSizeString = sharedPref.getString("com.grigorov.asparuh.probujdane.textsize", "14");
        int songTextSize = Integer.parseInt(songTextSizeString);
        int songTitleSize = songTextSize + 4;

        // Populate Song details into the layout
        // Add search markers if needed!
        int flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

        SpannableStringBuilder songNameBuilder = new SpannableStringBuilder();
        for (int textIndex=0; textIndex < songOnScreen.getSongName().length(); textIndex++) {
            String c = String.valueOf(songOnScreen.getSongName().charAt(textIndex));
            SpannableString spannableString = new SpannableString(c);
            boolean marked = false;
            for (int markerIndex=0;markerIndex<listSongMarkers.size();markerIndex=markerIndex+1) {
                if (
                        (listSongMarkers.get(markerIndex).getColumnIndex()==1) &&
                                (listSongMarkers.get(markerIndex).getStartIndex()<=textIndex) &&
                                (listSongMarkers.get(markerIndex).getEndIndex()>=textIndex)
                ) {
                    marked = true;
                }
            }
            if (marked==false) {
                spannableString.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.colorFormulaTitleText, null)),
                        0, spannableString.length(), flag);
            } else {
                spannableString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spannableString.length(), flag);
                spannableString.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.colorSearchResultMarker, null)),
                        0, spannableString.length(), flag);
            }
            songNameBuilder.append(spannableString);
        }

        TextView textViewSongTitle = findViewById(R.id.textSongTitle);
        textViewSongTitle.setText(songNameBuilder);
        textViewSongTitle.setTextSize(songTitleSize);

        SpannableStringBuilder songTextBuilder = new SpannableStringBuilder();
        for (int textIndex=0; textIndex < songOnScreen.getSongText().length(); textIndex++) {
            String c = String.valueOf(songOnScreen.getSongText().charAt(textIndex));
            SpannableString spannableString = new SpannableString(c);
            boolean marked = false;
            for (int markerIndex=0;markerIndex<listSongMarkers.size();markerIndex=markerIndex+1) {
                if (
                        (listSongMarkers.get(markerIndex).getColumnIndex()==2) &&
                                (listSongMarkers.get(markerIndex).getStartIndex()<=textIndex) &&
                                (listSongMarkers.get(markerIndex).getEndIndex()>=textIndex)
                ) {
                    marked = true;
                }
            }
            if (marked==false) {
                spannableString.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.colorFormulaText, null)),
                        0, spannableString.length(), flag);
            } else {
                spannableString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spannableString.length(), flag);
                spannableString.setSpan(new ForegroundColorSpan(ResourcesCompat.getColor(getResources(), R.color.colorSearchResultMarker, null)),
                        0, spannableString.length(), flag);
            }
            songTextBuilder.append(spannableString);
        }

        TextView textViewSongText = findViewById(R.id.textSongText);
        textViewSongText.setText(songTextBuilder);
        textViewSongText.setTextSize(songTextSize);

        if (songOnScreen.isSongVocalPlayable()==false) {
            LinearLayout linearSongPlayOptions = topMusicLinearLayout.findViewById(R.id.linearSongPlayOptions);
            if (linearSongPlayOptions.getChildCount() > 0) {
                linearSongPlayOptions.removeAllViews();
            }
            ((ViewManager)linearSongPlayOptions.getParent()).removeView(linearSongPlayOptions);
            View viewBelowSongPlayOptions = topMusicLinearLayout.findViewById(R.id.viewBelowSongPlayOptions);
            ((ViewManager)viewBelowSongPlayOptions.getParent()).removeView(viewBelowSongPlayOptions);
        }
        if (songOnScreen.isSongInstrumentalPlayable()==false) {
            LinearLayout linearSongInstrumentalOptions = topMusicLinearLayout.findViewById(R.id.linearInstrumentalPlayOptions);
            if (linearSongInstrumentalOptions.getChildCount() > 0) {
                linearSongInstrumentalOptions.removeAllViews();
            }
            ((ViewManager)linearSongInstrumentalOptions.getParent()).removeView(linearSongInstrumentalOptions);
            View viewBelowSongInstrumentalOptions = topMusicLinearLayout.findViewById(R.id.viewBelowInstrumentalPlayOptions);
            ((ViewManager)viewBelowSongInstrumentalOptions.getParent()).removeView(viewBelowSongInstrumentalOptions);
        }

    }

    private void setPlaylistOnScreen (String playlistID) {
        Cursor rs = playlistsDB.getPlaylist(playlistID);
        rs.moveToFirst();

        playlistOnScreen = new Playlist (playlistID,
                rs.getString(rs.getColumnIndex("Name")),
                rs.getString(rs.getColumnIndex("Songs")),
                rs.getString(rs.getColumnIndex("PlayType")),
                getApplicationContext()
        );

        if (!rs.isClosed())  {
            rs.close();
        }
    }

    public void showPlaylistLayout (String playlistID) {
        musicState = STATE_PLAYLIST;

        topMusicLinearLayout.removeAllViews();
        View inflatedLayout= getLayoutInflater().inflate(R.layout.music_playlist, null, false);
        topMusicLinearLayout.addView(inflatedLayout);

        setPlaylistOnScreen(playlistID);

        listPlaylistsSongs = playlistOnScreen.getSongsArrayList();

        // Populate Playlist details into the layout
        playlistAdapter = new MusicEntireActivity.PlaylistAdapter(this, listPlaylistsSongs);
        ListView listView1 = findViewById(R.id.listViewMusicPlaylist);
        listView1.setAdapter(playlistAdapter);
        listView1.setSelection(scrollViewPlaylistFirstVisiblePosition);

        TextView textPlaylistName;
        textPlaylistName = findViewById(R.id.textPlaylistName);
        textPlaylistName.setText(playlistOnScreen.getPlaylistName());

        View viewPlaylistPlay;
        viewPlaylistPlay = findViewById(R.id.viewPlaylistPlay);
        viewPlaylistPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                startPlayingPlaylist(playlistOnScreen.getPlaylistID(),0);
            }
        });

        TextView textPlaylistAddSong;
        textPlaylistAddSong = findViewById(R.id.textPlaylistAddSong);
        textPlaylistAddSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                showPlaylistAddSongLayout();
            }
        });

        View viewPlusPlaylistAddSong;
        viewPlusPlaylistAddSong = findViewById(R.id.viewPlusPlaylistAddSong);
        viewPlusPlaylistAddSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                showPlaylistAddSongLayout();
            }
        });

        TextView textPlaylistRemoveSong;
        textPlaylistRemoveSong = findViewById(R.id.textPlaylistRemoveSong);
        textPlaylistRemoveSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                showPlaylistRemoveSongLayout();
            }
        });

        View viewMinusPlaylistRemoveSong;
        viewMinusPlaylistRemoveSong = findViewById(R.id.viewMinusPlaylistRemoveSong);
        viewMinusPlaylistRemoveSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                showPlaylistRemoveSongLayout();
            }
        });

        TextView textDeletePlaylist;
        textDeletePlaylist = findViewById(R.id.textDeletePlaylist);
        textDeletePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                deletePlaylistOnScreen();
            }
        });

        View viewDeletePlaylist;
        viewDeletePlaylist = findViewById(R.id.viewDeletePlaylist);
        viewDeletePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                deletePlaylistOnScreen();
            }
        });

        if (playlistOnScreen.isEditable()==false) {
            LinearLayout linearPlaylistOptions = topMusicLinearLayout.findViewById(R.id.linearPlaylistOptions);
            if (linearPlaylistOptions.getChildCount() > 0) {
                linearPlaylistOptions.removeAllViews();
            }
            View viewBelowPlaylistOptions = topMusicLinearLayout.findViewById(R.id.viewBelowPlaylistOptions);
            ((ViewManager)viewBelowPlaylistOptions.getParent()).removeView(viewBelowPlaylistOptions);
        }

    }

    public void showPlaylistAddSongLayout () {
        musicState = STATE_PLAYLIST_ADD_SONG;

        topMusicLinearLayout.removeAllViews();
        View inflatedLayout = getLayoutInflater().inflate(R.layout.music_playlist_add, null, false);
        topMusicLinearLayout.addView(inflatedLayout);

        Cursor rs = songsDB.getSongsInfo();
        rs.moveToFirst();

        listPlaylistEditSongsInfo.clear();
        listPlaylistEditSongsInfo.ensureCapacity(rs.getCount());

        for (int i=1; i <=rs.getCount(); i++) {
            String songID = rs.getString(rs.getColumnIndex("ID"));
            String songTitle;
            String songTitleBase = rs.getString(rs.getColumnIndex("Title"));
            String songType = rs.getString(rs.getColumnIndex("Type_"));
            boolean songVocalPlayable = true;
            if (rs.getString(rs.getColumnIndex("Vocal_File_Name")).equals("")) songVocalPlayable=false;
            boolean songInstrumentalPlayable = true;
            if (rs.getString(rs.getColumnIndex("Instrumental_File_Name")).equals("")) songInstrumentalPlayable=false;
            if ( Integer.parseInt(rs.getString(rs.getColumnIndex("Files_Downloaded"))) == Song.FILES_NOT_DOWNLOADED ) {
                songVocalPlayable = false;
                songInstrumentalPlayable = false;
            }
            if ( (songVocalPlayable==true) && (playlistOnScreen.isSongInPlaylist(songID,Song.PLAY_VOCAL) == false) ) {
                songTitle = songTitleBase + " - " + getResources().getString(R.string.vocal);
                listPlaylistEditSongsInfo.add(new PlaylistSongInfo(songID, songTitle,
                        true, false, Song.PLAY_VOCAL, false));
            }
            if ( (songInstrumentalPlayable==true) && (playlistOnScreen.isSongInPlaylist(songID,Song.PLAY_INSTRUMENTAL) == false) ) {
                songTitle = songTitleBase + " - " + getResources().getString(R.string.instrumental);
                listPlaylistEditSongsInfo.add(new PlaylistSongInfo(songID, songTitle,
                        false, true, Song.PLAY_INSTRUMENTAL, false));
            }
            rs.moveToNext();
        }

        if (!rs.isClosed())  {
            rs.close();
        }

        playlistEditSongsInfoAdapter = new MusicEntireActivity.PlaylistEditSongsInfoAdapter(this, listPlaylistEditSongsInfo);
        ListView listView1 = findViewById(R.id.listViewMusicPlaylistAdd);
        listView1.setAdapter(playlistEditSongsInfoAdapter);

        View viewMusicPlaylistAddTick;
        viewMusicPlaylistAddTick = findViewById(R.id.viewMusicPlaylistAddTick);
        viewMusicPlaylistAddTick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onPlaylistAddSongCompletetion();
            }
        });

    }

    private void onPlaylistAddSongCompletetion () {
        for (int i=0; i <listPlaylistEditSongsInfo.size(); i++) {
            PlaylistSongInfo currentSongInfo = listPlaylistEditSongsInfo.get(i);
            if (currentSongInfo.getSongTicked()==true) {
                playlistOnScreen.addSong(Integer.parseInt(currentSongInfo.getSongID()),currentSongInfo.getSongPlayType(), getApplicationContext());
                listPlaylistsSongs = playlistOnScreen.getSongsArrayList();
                playlistAdapter.notifyDataSetChanged();
                updateMusicServicePlaylistAfterPositionChange();
            }
        }
        showPlaylistLayout(playlistOnScreen.getPlaylistID());
    }

    public void showPlaylistRemoveSongLayout () {
        musicState = STATE_PLAYLIST_REMOVE_SONG;

        topMusicLinearLayout.removeAllViews();
        View inflatedLayout = getLayoutInflater().inflate(R.layout.music_playlist_rem, null, false);
        topMusicLinearLayout.addView(inflatedLayout);

        listPlaylistEditSongsInfo = playlistOnScreen.getSongsInfoArrayList(getApplicationContext());

        playlistEditSongsInfoAdapter = new MusicEntireActivity.PlaylistEditSongsInfoAdapter(this, listPlaylistEditSongsInfo);
        ListView listView1 = findViewById(R.id.listViewMusicPlaylistRem);
        listView1.setAdapter(playlistEditSongsInfoAdapter);

        View viewMusicPlaylistRemoveTick;
        viewMusicPlaylistRemoveTick = findViewById(R.id.viewMusicPlaylistRemoveTick);
        viewMusicPlaylistRemoveTick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onPlaylistRemoveSongCompletition();
            }
        });

    }

    private void onPlaylistRemoveSongCompletition () {
        for (int i=listPlaylistEditSongsInfo.size()-1; i>=0; i--) {
            PlaylistSongInfo currentSongInfo = listPlaylistEditSongsInfo.get(i);
            if (currentSongInfo.getSongTicked()==true) {
                playlistOnScreen.removeSong(currentSongInfo.getSongPositionInPlaylist(),getApplicationContext());
                listPlaylistsSongs = playlistOnScreen.getSongsArrayList();
                playlistAdapter.notifyDataSetChanged();
                updateMusicServicePlaylistAfterPositionChange();
            }
        }
        showPlaylistLayout(playlistOnScreen.getPlaylistID());
    }

    private void deletePlaylistOnScreen () {
        PlaylistDeleteDialogFragment newFragment = new PlaylistDeleteDialogFragment();
        newFragment.setPlaylistName(playlistOnScreen.getPlaylistName());
        newFragment.show(getSupportFragmentManager(), "playlistDeleteDialog");
    }

    @Override
    public void onPlaylistDeleteDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onPlaylistDeleteDialogPositiveClick(DialogFragment dialog) {
        playlistsDB.deletePlaylist(playlistOnScreen.getPlaylistID());
        startPlaylistsTask(topMusicLinearLayout);
    }

    public void onButtonSongAdd2PlaylistPressed(View view) {
        musicStateOld = musicState;
        musicState = STATE_PLAYLIST_CREATE_NEW_FROM_SONG;

        topMusicLinearLayout.removeAllViews();
        View inflatedLayout= getLayoutInflater().inflate(R.layout.music_playlists_list, null, false);
        topMusicLinearLayout.addView(inflatedLayout);

        playlistsSelectAdapter = new MusicEntireActivity.PlaylistsSelectAdapter(this, listPlaylists);
        ListView listView1 = findViewById(R.id.listViewMusicPlaylists);
        listView1.setAdapter(playlistsSelectAdapter);
    }

    public void setPlaylistSelection(String selectedPlaylistID) {
        Cursor rs = playlistsDB.getPlaylist(selectedPlaylistID);
        rs.moveToFirst();
        playlistSelected = new Playlist (selectedPlaylistID,
                rs.getString(rs.getColumnIndex("Name")),
                rs.getString(rs.getColumnIndex("Songs")),
                rs.getString(rs.getColumnIndex("PlayType")),
                getApplicationContext()
        );
        if (!rs.isClosed())  {
            rs.close();
        }

        if (musicState==STATE_PLAYLIST_CREATE_NEW_FROM_SONG) {
            playlistSelected.addSong(Integer.parseInt(songOnScreen.getSongID()),songOnScreen.getSongPlayType(),getApplicationContext());
            showPlaylistLayout(selectedPlaylistID);
        }
    }

    public void onCreateNewPlaylistPressed(View view) {
        if (musicState == STATE_PLAYLIST_CREATE_NEW_FROM_SONG) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.create_new_playlist_string));
            final EditText input = new EditText(this);
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newPlaylistName = input.getText().toString();
                    Cursor rs = playlistsDB.getAll();
                    Integer newPlaylistID = rs.getCount()+1;
                    if (!rs.isClosed())  {
                        rs.close();
                    }
                    playlistsDB.insertPlaylist(
                            newPlaylistID.toString(),
                            newPlaylistName,
                            songOnScreen.getSongID(),
                            songOnScreen.getSongType()
                    );
                    showPlaylistLayout(newPlaylistID.toString());
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        } else if (musicState == STATE_PLAYLISTS_LIST) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.create_new_playlist_string));
            final EditText input = new EditText(this);
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newPlaylistName = input.getText().toString();
                    Cursor rs = playlistsDB.getAll();
                    Integer newPlaylistID = rs.getCount() + 1;
                    if (!rs.isClosed())  {
                        rs.close();
                    }
                    playlistsDB.insertPlaylist(
                            newPlaylistID.toString(),
                            newPlaylistName,
                            "",
                            ""
                    );
                    playlistOnScreen = new Playlist (newPlaylistID.toString(),
                            newPlaylistName,
                            "",
                            "",
                            getApplicationContext()
                    );
                    showPlaylistAddSongLayout ();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
    }

    @Override
    public void onBackPressed() {
        switch (musicState) {
            case STATE_MENU:
                if (activitySource.equals("Notification")) {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
                finish();
                break;
            case STATE_SONGS_LIST:
                startMenu();
                break;
            case STATE_PANEVRITMIA_LIST:
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
                        default:
                            finish();
                    }
                }
                break;
            case STATE_PLAYLISTS_LIST:
                startMenu();
                break;
            case STATE_PLAYLIST:
                startPlaylistsTask(topMusicLinearLayout);
                break;
            case STATE_SONG_FROM_PLAYLIST:
                showPlaylistLayout(playlistOnScreen.getPlaylistID());
                break;
            case STATE_PLAYLIST_ADD_SONG:
                showPlaylistLayout(playlistOnScreen.getPlaylistID());
                break;
            case STATE_PLAYLIST_REMOVE_SONG:
                showPlaylistLayout(playlistOnScreen.getPlaylistID());
                break;
            case STATE_PLAYLIST_CREATE_NEW_FROM_SONG:
                showSongLayout(songOnScreen.getSongID(),false, -1, musicStateOld, songOnScreen.getSongPlayType());
                break;
            case STATE_SONG_FROM_SEARCH:
                finish();
                break;
            case STATE_DELETE_SONGS:
                switch (musicStateOld) {
                    case STATE_SONGS_LIST:
                        startSongsListTask(new View(getApplicationContext()));
                        break;
                    case STATE_PANEVRITMIA_LIST:
                        startPanevritmiaTask(new View(getApplicationContext()));
                        break;
                    default:
                        finish();
                }
                break;
            default:
                finish();
        }
    }

    public void initService () {
        bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
        startService(playIntent);
    }

    public void onButtonSongVocalPlayPressed (View view) {
        if (musicState == STATE_SONG_FROM_PLAYLIST) {
            startPlayingPlaylist(playlistOnScreen.getPlaylistID(),songOnScreen.getSongPositionInPlaylist());
        } else {
            songOnScreen.setSongPlayType(Song.PLAY_VOCAL);
            startPlayingSingleSong(songOnScreen.getSongID(),songOnScreen.getSongPlayType());
        }
    }

    public void onButtonSongInstrumentalPlayPressed (View view) {
        if (musicState == STATE_SONG_FROM_PLAYLIST) {
            startPlayingPlaylist(playlistOnScreen.getPlaylistID(),songOnScreen.getSongPositionInPlaylist());
        } else {
            songOnScreen.setSongPlayType(Song.PLAY_INSTRUMENTAL);
            startPlayingSingleSong(songOnScreen.getSongID(),songOnScreen.getSongPlayType());
        }
    }

    public void startPlayingSingleSong (String songID, int songPlayType) {

        startSongNumber = 0;
        Cursor rs = songsDB.getSongSingle(songID);
        rs.moveToFirst();

        songSinlgePlayed = new Song (songID,
                rs.getString(rs.getColumnIndex("Title")),
                rs.getString(rs.getColumnIndex("Text")),
                rs.getString(rs.getColumnIndex("Type_")),
                rs.getString(rs.getColumnIndex("Vocal_File_Name")),
                rs.getString(rs.getColumnIndex("Instrumental_File_Name")),
                rs.getString(rs.getColumnIndex("Files_Downloaded")),
                songPlayType
        );

        // dummy
        playlistPlayed = new Playlist ("0",
                "dummy",
                songID,
                (new Integer(songPlayType)).toString(),
                getApplicationContext()
        );

        if (!rs.isClosed())  {
            rs.close();
        }

        String musicInfoTextString = songSinlgePlayed.getSongName();
        if (songSinlgePlayed.getSongPlayType()==Song.PLAY_INSTRUMENTAL) {
            musicInfoTextString = musicInfoTextString + " - "  + getResources().getString(R.string.instrumental);
        }
        musicInfoText.setText(musicInfoTextString);

        songList.clear();
        songList.add(songSinlgePlayed);

        buttonMusicPlayPause.setBackgroundResource(R.drawable.music_pause);

        handlerUpdateSeekbar.postDelayed(runnableUpdateSeekbar, 0);

        serviceInitOnlyBinding = false;

        if (musicBound == false) {
            initService();
        } else {
            if (musicSrv.isPlaying() == true) {
                musicSrv.stopPlayer();
            }
            musicSrv.setList(songList);
            musicSrv.setSong(startSongNumber);
            musicSrv.playSong();
        }
    }

    public void startPlayingPlaylist (String playlistID, int inputStartSongNumber) {
        startSongNumber = inputStartSongNumber;
        Cursor rs = playlistsDB.getPlaylist(playlistID);
        rs.moveToFirst();

        playlistPlayed = new Playlist (playlistID,
                rs.getString(rs.getColumnIndex("Name")),
                rs.getString(rs.getColumnIndex("Songs")),
                rs.getString(rs.getColumnIndex("PlayType")),
                getApplicationContext()
        );

        if (!rs.isClosed())  {
            rs.close();
        }

        songList = new ArrayList<Song>(playlistPlayed.getSongsArrayList());

        String musicInfoTextString = playlistPlayed.getPlaylistName()+" - "+songList.get(startSongNumber).getSongName();
        if (songList.get(startSongNumber).getSongPlayType()==Song.PLAY_INSTRUMENTAL) {
            musicInfoTextString = musicInfoTextString + " - "  + getResources().getString(R.string.instrumental);
        }
        musicInfoText.setText(musicInfoTextString);

        buttonMusicPlayPause.setBackgroundResource(R.drawable.music_pause);

        handlerUpdateSeekbar.postDelayed(runnableUpdateSeekbar, 0);

        serviceInitOnlyBinding = false;

        if (musicBound == false) {
            initService();
        } else {
            if (musicSrv.isPlaying() == true) {
                musicSrv.stopPlayer();
            }
            musicSrv.setList(songList);
            musicSrv.setSong(startSongNumber);
            musicSrv.setPlaylist(playlistPlayed);
            musicSrv.playSong();
        }
    }

    public void onButtonMusicStopPressed (View view) {
        if (musicBound==true) {
            musicBound=false;
            unbindService(musicConnection);
            stopService(playIntent);
        }
        buttonMusicPlayPause.setBackgroundResource(R.drawable.music_play);
        musicInfoText.setText(getResources().getString(R.string.no_music_selected_string));
        songIsPaused = true;
        seekbarMusic.setProgress(0);
        handlerUpdateSeekbar.removeCallbacks(runnableUpdateSeekbar);
    }

    @Override
    protected void onDestroy() {
        if (musicBound==true) {
            musicBound=false;
            unbindService(musicConnection);
        }
        handlerUpdateSeekbar.removeCallbacks(runnableUpdateSeekbar);
        unregisterReceiver(broadcastReceiverEndMusicPlay);
        unregisterReceiver(broadcastReceiverPlayAnotherSong);
        unregisterReceiver(downloadReceiver);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("com.grigorov.asparuh.probujdane.musicState", musicState.toString());
        switch (musicState) {
            case STATE_SONG_SINGLE:
                editor.putString("com.grigorov.asparuh.probujdane.musicStateOld", musicStateOld.toString());
                editor.putString("com.grigorov.asparuh.probujdane.musicSongID", songOnScreen.getSongID());
                editor.putString("com.grigorov.asparuh.probujdane.musicSongPositionInPlaylist", songOnScreen.getSongPositionInPlaylist().toString());
                break;
            case STATE_SONG_FROM_SEARCH:
                editor.putString("com.grigorov.asparuh.probujdane.musicSongID", songOnScreen.getSongID());
                editor.putString("com.grigorov.asparuh.probujdane.musicSongPositionInPlaylist", songOnScreen.getSongPositionInPlaylist().toString());
                break;
            case STATE_SONG_FROM_PLAYLIST:
                editor.putString("com.grigorov.asparuh.probujdane.musicSongID", songOnScreen.getSongID());
                editor.putString("com.grigorov.asparuh.probujdane.musicSongPositionInPlaylist", songOnScreen.getSongPositionInPlaylist().toString());
                editor.putString("com.grigorov.asparuh.probujdane.musicPlaylistID", playlistOnScreen.getPlaylistID());
                break;
            case STATE_PLAYLIST:
                editor.putString("com.grigorov.asparuh.probujdane.musicPlaylistID", playlistOnScreen.getPlaylistID());
                break;
            case STATE_PLAYLIST_ADD_SONG:
                editor.putString("com.grigorov.asparuh.probujdane.musicPlaylistID", playlistOnScreen.getPlaylistID());
                break;
            case STATE_PLAYLIST_REMOVE_SONG:
                editor.putString("com.grigorov.asparuh.probujdane.musicPlaylistID", playlistOnScreen.getPlaylistID());
                break;
            case STATE_PLAYLIST_CREATE_NEW_FROM_SONG:
                editor.putString("com.grigorov.asparuh.probujdane.musicStateOld", musicStateOld.toString());
                editor.putString("com.grigorov.asparuh.probujdane.musicSongID", songOnScreen.getSongID());
                break;
            default:
        }
        editor.commit();

        if (musicBound==true) {
            musicBound=false;
            unbindService(musicConnection);
        }

        super.onStop();
    }

    public void onButtonMusicPrevPressed (View view) {
        musicSrv.playPrev();
    }

    public void onButtonMusicNextPressed (View view) {
        musicSrv.playNext();
    }

    public void onButtonMusicPlayPausePressed (View view) {
        if (musicBound==true) {
            if (musicSrv.isPlaying()==true) {
                buttonMusicPlayPause.setBackgroundResource(R.drawable.music_play);
                musicSrv.pausePlayer();
                positionPlayedSong = musicSrv.getCurrentPosition();
                songIsPaused = true;
            } else if (songIsPaused==true) {
                buttonMusicPlayPause.setBackgroundResource(R.drawable.music_pause);
                musicSrv.seekTo(positionPlayedSong);
                musicSrv.go();
                songIsPaused = false;
            } else if (musicState == STATE_SONG_FROM_PLAYLIST) {
                buttonMusicPlayPause.setBackgroundResource(R.drawable.music_pause);
                unbindService(musicConnection);
                stopService(playIntent);
                startPlayingPlaylist(playlistOnScreen.getPlaylistID(), songOnScreen.getSongPositionInPlaylist() );
            } else if ( ( musicState == STATE_SONG_SINGLE) || (musicState == STATE_SONG_FROM_SEARCH) ) {
                if ( (songOnScreen.isSongVocalPlayable()==true) || (songOnScreen.isSongInstrumentalPlayable()==true) ) {
                    buttonMusicPlayPause.setBackgroundResource(R.drawable.music_pause);
                    unbindService(musicConnection);
                    stopService(playIntent);
                    if (songOnScreen.isSongVocalPlayable()==true) {
                        startPlayingSingleSong(songOnScreen.getSongID(),Song.PLAY_VOCAL);
                    } else {
                        startPlayingSingleSong(songOnScreen.getSongID(),Song.PLAY_INSTRUMENTAL);
                    }
                }
            } else if (musicState == STATE_PLAYLIST) {
                buttonMusicPlayPause.setBackgroundResource(R.drawable.music_pause);
                unbindService(musicConnection);
                stopService(playIntent);
                startPlayingPlaylist(playlistOnScreen.getPlaylistID(), 0);
            }
        } else if (musicState == STATE_SONG_FROM_PLAYLIST) {
            buttonMusicPlayPause.setBackgroundResource(R.drawable.music_pause);
            startPlayingPlaylist(playlistOnScreen.getPlaylistID(), songOnScreen.getSongPositionInPlaylist() );
        } else if ( ( musicState == STATE_SONG_SINGLE) || (musicState == STATE_SONG_FROM_SEARCH) ) {
            if ( (songOnScreen.isSongVocalPlayable()==true) || (songOnScreen.isSongInstrumentalPlayable()==true) ) {
                buttonMusicPlayPause.setBackgroundResource(R.drawable.music_pause);
                if (songOnScreen.isSongVocalPlayable()==true) {
                    startPlayingSingleSong(songOnScreen.getSongID(),Song.PLAY_VOCAL);
                } else {
                    startPlayingSingleSong(songOnScreen.getSongID(),Song.PLAY_INSTRUMENTAL);
                }
            }
        } else if (musicState == STATE_PLAYLIST) {
            buttonMusicPlayPause.setBackgroundResource(R.drawable.music_pause);
            startPlayingPlaylist(playlistOnScreen.getPlaylistID(), 0);
        }

    }

    public void startMusicSearchMenuTask (View view) {
        Intent intent = new Intent(this, SearchMenuActivity.class);
        startActivity(intent);
    }

    private class SongMarker {
        private int columnIndex;
        private int startIndex;
        private int endIndex;

        public SongMarker (int inputColumnIndex, int inputStartIndex, int inputEndIndex) {
            columnIndex = inputColumnIndex;
            startIndex = inputStartIndex;
            endIndex = inputEndIndex;
        }

        public int getColumnIndex() {
            return columnIndex;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }
    }

    public void startSearchMenuTask (View view) {
        Intent intent = new Intent(this, SearchMenuActivity.class);
        intent.putExtra("com.grigorov.asparuh.probujdane.searchSource", "SEARCH_SOURCE_MUSIC");
        startActivity(intent);
    }


    public void startOptionsMenuTask (View view) {
        Intent intent = new Intent(this, OptionsMenuActivity.class);
        startActivity(intent);
    }

    private void downloadSingleSong (String downloadSongID) {
        Cursor rs = songsDB.getSongSingle(downloadSongID);
        rs.moveToFirst();
        listDownloadSongs.add (new Song (downloadSongID,
                rs.getString(rs.getColumnIndex("Title")),
                rs.getString(rs.getColumnIndex("Text")),
                rs.getString(rs.getColumnIndex("Type_")),
                rs.getString(rs.getColumnIndex("Vocal_File_Name")),
                rs.getString(rs.getColumnIndex("Instrumental_File_Name")),
                rs.getString(rs.getColumnIndex("Files_Downloaded")),
                rs.getString(rs.getColumnIndex("Vocal_File_Link")),
                rs.getString(rs.getColumnIndex("Instrumental_File_Link"))
            )
        );
        if (!rs.isClosed())  {
            rs.close();
        }
        tryNewDownloadStrart();
    }

    private void tryNewDownloadStrart() {
        if (checkDownloadOngoing() == false) {
            startNextSongDownload();
        }
    }

    private boolean checkDownloadOngoing() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterByStatus(DownloadManager.STATUS_PAUSED|
                DownloadManager.STATUS_RUNNING|DownloadManager.STATUS_PENDING);
        Cursor cursor = downloadManager.query( query );
        for (int i = 0; i < cursor.getCount() ; i++)
        {
            cursor.moveToPosition(i);
            String iTitle = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
            String iDescription = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION));
            String iID = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
            if ( iTitle.equals( getString(R.string.music_download_title_string))) {
                if (!cursor.isClosed())  {
                    cursor.close();
                }
                return true;
            }
        }
        if (!cursor.isClosed())  {
            cursor.close();
        }
        return false;
    }

    private void startNextSongDownload() {
        if (listDownloadSongs.get(0).getSongVocalFileLink().equals("")==false){
            downloadingVocalFile = true;
            startSongFileDownload(listDownloadSongs.get(0).getSongVocalFileLink(), listDownloadSongs.get(0).getSongVocalFileName());
        } else if (listDownloadSongs.get(0).getSongInstrumentalFileLink().equals("")==false){
            downloadingVocalFile = false;
            startSongFileDownload(listDownloadSongs.get(0).getSongInstrumentalFileLink(), listDownloadSongs.get(0).getSongInstrumentalFileName());
        }
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //check if the broadcast message is for our Enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadReference == referenceId) {
                if ( (downloadingVocalFile==true) &&
                        (listDownloadSongs.get(0).getSongInstrumentalFileLink().equals("")==false) ) {
                    downloadingVocalFile = false;
                    startSongFileDownload(listDownloadSongs.get(0).getSongInstrumentalFileLink(), listDownloadSongs.get(0).getSongInstrumentalFileName());
                } else {
                    songsDB.updateSongDownloaded(listDownloadSongs.get(0).getSongID(),"1");
                    listDownloadSongs.remove(0);
                    if (listDownloadSongs.size()>0) {
                        startNextSongDownload();
                    }
                }
                if ( (musicState==STATE_SONGS_LIST) || (musicState==STATE_PANEVRITMIA_LIST) ) {
                    ListView listView1 = findViewById(R.id.listViewMusicSongs);
                    scrollViewSongsListFirstVisiblePosition = listView1.getFirstVisiblePosition();
                    setListSongs();
                    showListSongs();
                }
            }
        }
    };

    private void startSongFileDownload(String downloadLink, String downloadFileName) {
        Uri Download_Uri = Uri.parse(downloadLink);
        DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
        //Set the title of this download, to be displayed in notifications (if enabled).
        request.setTitle(getString(R.string.music_download_title_string));
        //Set a description of this download, to be displayed in notifications (if enabled)
        String downloadDescriptionTextString = listDownloadSongs.get(0).getSongName();
        if (listDownloadSongs.get(0).getSongPlayType()==Song.PLAY_INSTRUMENTAL) {
            downloadDescriptionTextString = downloadDescriptionTextString + " - "  + getResources().getString(R.string.instrumental);
        }
        request.setDescription(downloadDescriptionTextString);
        //Set the local destination for the downloaded file to a path within the application's external files directory
        request.setDestinationInExternalFilesDir(this,null,"/Pesni/"+downloadFileName);
        //Enqueue a new download and same the referenceId
        downloadReference = downloadManager.enqueue(request);
    }

    public void onDownloadAllPressed(View view) {
        Cursor rs = songsDB.getSongsNotDownloaded();
        rs.moveToFirst();
        if (rs.getCount()>0) {
            long songsSizeToDownloadInMB = (SONGS_FOLDER_SIZE_IN_BYTES -
                    getFolderSize(new File(getApplicationContext().getExternalFilesDir(null)+ "/Pesni")) ) / (1024*1024) ;
            SongsDownloadDialogFragment newFragment = new SongsDownloadDialogFragment();
            newFragment.setDownloadSizeInMB(songsSizeToDownloadInMB);
            newFragment.show(getSupportFragmentManager(), "songsDownloadDialog");
        }
        if (!rs.isClosed())  {
            rs.close();
        }
    }

    @Override
    public void onSongsDownloadDialogPositiveClick(DialogFragment dialog) {
        Cursor rs = songsDB.getSongsNotDownloaded();
        if (rs.getCount()>0) {
            rs.moveToFirst();
            for (int i = 1; i <= rs.getCount(); i++) {
                Song currentSongToBeDownloaded = new Song(rs.getString(rs.getColumnIndex("ID")),
                        rs.getString(rs.getColumnIndex("Title")),
                        rs.getString(rs.getColumnIndex("Text")),
                        rs.getString(rs.getColumnIndex("Type_")),
                        rs.getString(rs.getColumnIndex("Vocal_File_Name")),
                        rs.getString(rs.getColumnIndex("Instrumental_File_Name")),
                        "1",
                        rs.getString(rs.getColumnIndex("Vocal_File_Link")),
                        rs.getString(rs.getColumnIndex("Instrumental_File_Link"))
                );
                if ( (currentSongToBeDownloaded.getSongVocalFileName().equals("")==false) ||
                        (currentSongToBeDownloaded.getSongInstrumentalFileName().equals("")==false) ) {
                    listDownloadSongs.add(currentSongToBeDownloaded);
                }
                rs.moveToNext();
            }
            if (listDownloadSongs.size()>0) {
                tryNewDownloadStrart();
            }
        }
        if (!rs.isClosed())  {
            rs.close();
        }
    }

    @Override
    public void onSongsDownloadDialogNegativeClick(DialogFragment dialog) {

    }

    public static long getFolderSize(File f) {
        long size = 0;
        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                size += getFolderSize(file);
            }
        } else {
            size=f.length();
        }
        return size;
    }

    private void updateSongsDownloadedStatus () {
        Cursor rs = songsDB.getSongsInfo();
        rs.moveToFirst();
        for (int i=1; i <=rs.getCount(); i++) {
            String currentID = rs.getString(rs.getColumnIndex("ID"));
            String currentVocalFileName = rs.getString(rs.getColumnIndex("Vocal_File_Name"));
            String currentInstrumentalFileName = rs.getString(rs.getColumnIndex("Instrumental_File_Name"));
            String currentType = rs.getString(rs.getColumnIndex("Type_"));
            String subDirectory;
            if (currentType.equals("Songs")) {
                subDirectory = "/Pesni/";
            } else {
                subDirectory = "/Panevritmia/";
            }
            File currentVocalFile = new File(getApplicationContext().getExternalFilesDir(null)+ subDirectory +currentVocalFileName);
            File currentInstrumentalFile =  new File(getApplicationContext().getExternalFilesDir(null)+ subDirectory +currentInstrumentalFileName);
            String filesDownloaded = "1";
            if (currentVocalFileName.equals("") && currentInstrumentalFileName.equals("")) {
                filesDownloaded = "0";
            } else {
                if ( (currentVocalFileName.equals("")==false) && (currentVocalFile.exists()==false) ) {
                    filesDownloaded = "0";
                }
                if ( (currentInstrumentalFileName.equals("")==false) && (currentInstrumentalFile.exists()==false) ) {
                    filesDownloaded = "0";
                }
            }
            songsDB.updateSongDownloaded(currentID,filesDownloaded);
            rs.moveToNext();
        }
        if (!rs.isClosed())  {
            rs.close();
        }
    }

    public void onDeleteSongsPressed (View view) {
        showDeleteSongs();
    }

    private void showDeleteSongs() {
        musicStateOld = musicState;
        musicState = STATE_DELETE_SONGS;

        topMusicLinearLayout.removeAllViews();
        View inflatedLayout = getLayoutInflater().inflate(R.layout.music_songs_rem, null, false);
        topMusicLinearLayout.addView(inflatedLayout);

        Cursor rs = songsDB.getSongsDownloaded("Songs");
        rs.moveToFirst();

        listPlaylistEditSongsInfo.clear();
        listPlaylistEditSongsInfo.ensureCapacity(rs.getCount());

        for (int i=1; i <=rs.getCount(); i++) {
            String songID = rs.getString(rs.getColumnIndex("ID"));
            String songTitle = rs.getString(rs.getColumnIndex("Title"));
            String songType = rs.getString(rs.getColumnIndex("Type_"));
            listPlaylistEditSongsInfo.add(new PlaylistSongInfo(songID, songTitle,
                        true, false, Song.PLAY_UNDEFINED, false));
            rs.moveToNext();
        }

        if (!rs.isClosed())  {
            rs.close();
        }

        playlistEditSongsInfoAdapter = new MusicEntireActivity.PlaylistEditSongsInfoAdapter(this, listPlaylistEditSongsInfo);
        ListView listView1 = findViewById(R.id.listViewMusicSongsRem);
        listView1.setAdapter(playlistEditSongsInfoAdapter);

        View viewMusicSongsRemoveTick;
        viewMusicSongsRemoveTick = findViewById(R.id.viewMusicSongsRemoveTick);
        viewMusicSongsRemoveTick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onSongsRemoveSelectionCompletetion();
            }
        });

    }

    void onSongsRemoveSelectionCompletetion () {
        Integer numberSongsToDelete = 0;
        for (int i=listPlaylistEditSongsInfo.size()-1; i>=0; i--) {
            PlaylistSongInfo currentSongInfo = listPlaylistEditSongsInfo.get(i);
            if (currentSongInfo.getSongTicked()==true) {
                numberSongsToDelete++;
            }
        }
        if (numberSongsToDelete>0) {
            SongsDeleteDiaglogFragment newFragment = new SongsDeleteDiaglogFragment();
            newFragment.setNumberSongsToDelete(numberSongsToDelete);
            newFragment.show(getSupportFragmentManager(), "songsDeleteDialog");
        } else {
            onBackPressed();
        }
    }

    @Override
    public void onSongsDeleteDialogNegativeClick(DialogFragment dialog) {
    }

    @Override
    public void onSongsDeleteDialogPositiveClick(DialogFragment dialog) {
        for (int i=listPlaylistEditSongsInfo.size()-1; i>=0; i--) {
            PlaylistSongInfo currentSongInfo = listPlaylistEditSongsInfo.get(i);
            if (currentSongInfo.getSongTicked()==true) {
                Cursor rs = songsDB.getSongSingle(currentSongInfo.getSongID());
                rs.moveToFirst();
                String currentVocalFileName = rs.getString(rs.getColumnIndex("Vocal_File_Name"));
                String currentInstrumentalFileName = rs.getString(rs.getColumnIndex("Instrumental_File_Name"));
                if (currentVocalFileName.equals("")==false) {
                    File currentVocalFile = new File(getApplicationContext().getExternalFilesDir(null)+ "/Pesni/"+currentVocalFileName);
                    currentVocalFile.delete();
                }
                if (currentInstrumentalFileName.equals("")==false) {
                    File currentInstrumentalFile = new File(getApplicationContext().getExternalFilesDir(null)+ "/Pesni/"+currentInstrumentalFileName);
                    currentInstrumentalFile.delete();
                }
                if (!rs.isClosed())  {
                    rs.close();
                }

            }
        }
        updateSongsDownloadedStatus();
        onBackPressed();
    }

    private void updateMusicServicePlaylistAfterPositionChange () {
        if (musicBound == true) {
            if ( (musicSrv.isPlaying() == true)
                    && (musicSrv.getPlayedPlaylist().getPlaylistID()==playlistOnScreen.getPlaylistID() ) ) {
                playlistPlayed = new Playlist(playlistOnScreen);
                songList = new ArrayList<Song>(playlistPlayed.getSongsArrayList());
                musicSrv.setList(songList);
                musicSrv.setPlaylist(playlistPlayed);
                for (int i=0; i<songList.size(); i++) {
                    if ( (songList.get(i).getSongID().equals(musicSrv.getPlayedSong().getSongID() ) ) &&
                            (songList.get(i).getSongPlayType() == musicSrv.getPlayedSong().getSongPlayType() ) ) {
                        musicSrv.setSong(i);
                    }
                }
            }
        }
    }

}

package com.grigorov.asparuh.probujdane;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by agr on 10/09/2019.
 */

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    public final static String NOTIFICATION_CHANNEL_ID = "PROBUJDANE_NOTIFICATION_CHANNEL_ID";

    //media player
    private MediaPlayer player;
    //song list
    private ArrayList<Song> songs;
    //current position
    private int songPosn;

    private final IBinder musicBind = new MusicBinder();

    private String songTitle="";
    private static final int NOTIFY_ID=1;

    private Song playedSong;

    private Playlist playedPlaylist;

    private boolean playerIsReleased;

    @Override
    public IBinder onBind(Intent arg0) {
        return musicBind;
    }

    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent){
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        songPosn++;
        if(songPosn<songs.size()) {
            Intent intentToPlayNextSong = new Intent("com.grigorov.asparuh.probujdane.onButtonMusicPlayPausePressed");
            getApplicationContext().sendBroadcast(intentToPlayNextSong);
            playSong();
        } else {
            //stopSelf();
            Intent intentToUnbindMusicService = new Intent("com.grigorov.asparuh.probujdane.IntentToUnbindMusicService");
            getApplicationContext().sendBroadcast(intentToUnbindMusicService);
            stopSelf();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();
        // notification
        Intent notIntent = new Intent(this, MusicEntireActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notIntent.putExtra("com.grigorov.asparuh.probujdane.musicActivitySourceVar", "Notification");
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.music_notification_channel_name);
            String description = getString(R.string.music_notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.music_play)
                //.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.music_play))
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing");
        String notificationContentText = "";
        if (getSongsSize()>1) {
            notificationContentText = playedPlaylist.getPlaylistName() + " - " +songTitle;
        } else {
            notificationContentText = songTitle;
        }
        builder.setContentText(notificationContentText);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        }
        Notification notification = builder.build();
                    // NO_CLEAR makes the notification stay when the user performs a "delete all" command
        notification.flags = notification.flags | Notification.FLAG_NO_CLEAR;
        startForeground(NOTIFY_ID, notification);
    }

    public void onCreate(){
        //create the service
        super.onCreate();
        // initialize songs list
        songs= new ArrayList<Song>();
        //initialize position
        songPosn=0;
        //create player
        player = new MediaPlayer();
        playerIsReleased = true;

        initMusicPlayer();
    }

    public void initMusicPlayer(){
        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setList(ArrayList<Song> theSongs){
        songs=theSongs;
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    public void playSong(){

        playerIsReleased = false;

        player.reset();

        playedSong = songs.get(songPosn);
        songTitle = playedSong.getSongName();
        // get the file
        File songFile = new File(getApplicationContext().getExternalFilesDir(null)+playedSong.getSongSubPath(),
                playedSong.getSongFileName());
        //set uri
        Uri trackUri = Uri.fromFile(songFile);
        //Uri trackUri = Uri.parse(songFile.toString());

        try{
            player.setDataSource(getApplicationContext(), trackUri);
            //player.setDataSource(songFile.getPath());
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();

    }

    public Song getPlayedSong () {
        return playedSong;
    }

    public Playlist getPlayedPlaylist() {
        return playedPlaylist;
    }

    public void setPlaylist (Playlist inputPlaylist) {
        playedPlaylist=inputPlaylist;
    }

    public void setSong(int songIndex){
        songPosn=songIndex;
    }

    public int getCurrentPosition(){
        return player.getCurrentPosition();
    }

    public int getDuration(){
        return player.getDuration();
    }

    public boolean isPlaying(){
        if (playerIsReleased == true) {
            return false;
        } else {
            return player.isPlaying();
        }
    }

    public void pausePlayer(){
        player.pause();
    }

    public void seekTo(int posn){
        player.seekTo(posn);
    }

    public void go(){
        player.start();
    }

    public void playPrev(){
        if(songs.size()>1) {
            songPosn--;
            if (songPosn < 0) songPosn = songs.size() - 1;
            Intent intentToPlayNextSong = new Intent("com.grigorov.asparuh.probujdane.intentToPlayAnotherSong");
            getApplicationContext().sendBroadcast(intentToPlayNextSong);
            playSong();
        }
    }

    public void playNext(){
        if(songs.size()>1) {
            songPosn++;
            if (songPosn >= songs.size()) songPosn = 0;
            Intent intentToPlayNextSong = new Intent("com.grigorov.asparuh.probujdane.intentToPlayAnotherSong");
            getApplicationContext().sendBroadcast(intentToPlayNextSong);
            playSong();
        }
    }

    public void stopPlayer() {
        stopForeground(true);
        player.stop();
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        player.stop();
        player.reset();
        player.release();
        playerIsReleased = true;
    }

    public int getSongPosn () { return songPosn; }

    public int getSongsSize () { return songs.size(); }
}

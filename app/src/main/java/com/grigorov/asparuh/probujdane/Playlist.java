package com.grigorov.asparuh.probujdane;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by agr on 28/11/2019.
 */

public class Playlist {

    private String playlistID;
    private String playlistName;
    private String stringSongs;

    private ArrayList<Song> songs = new ArrayList<Song>();

    public String getPlaylistID () { return playlistID; }
    public String getPlaylistName () { return playlistName; }
    public String getSongsString () { return stringSongs; }
    public boolean isEditable () {
        boolean result = true;
        if (Integer.parseInt(playlistID)<3) {
            result = false;
        }
        return result;
    }

    private musicDBHelper songsDB;
    private playlistsDBhelper playlistsDB;

    public Playlist (String inputID, String inputName, String inputStringSongs, Context context) {
        playlistID = inputID;
        playlistName = inputName;
        stringSongs = inputStringSongs;
        // Make the ArrayList
        songsDB = new musicDBHelper(context);
        String[] songsStringSplit = inputStringSongs.split(" ");
        songs.clear();
        songs.ensureCapacity(songsStringSplit.length);
        //List<String> songsStringSplit = inputStringSongs.split(" ");
        for (int i=0; i<songsStringSplit.length; i++) {
            Cursor rs = songsDB.getSongSingle(songsStringSplit[i]);
            if (rs.getCount()>0) {
                rs.moveToFirst();
                songs.add(new Song(
                        songsStringSplit[i],
                        rs.getString(rs.getColumnIndex("Title")),
                        rs.getString(rs.getColumnIndex("Text")),
                        rs.getString(rs.getColumnIndex("Type_")),
                        rs.getString(rs.getColumnIndex("File_Name")),
                        i
                ));
            }
        }
        songsDB.close();
    }

    public ArrayList<Song> getSongsArrayList () {
        return songs;
    }

    public ArrayList<PlaylistSongInfo> getSongsInfoArrayList (Context context) {
        ArrayList<PlaylistSongInfo> songsInfo = new ArrayList<PlaylistSongInfo>();
        songsDB = new musicDBHelper(context);
        String[] songsStringSplit = stringSongs.split(" ");
        songsInfo.clear();
        songsInfo.ensureCapacity(songsStringSplit.length);
        //List<String> songsStringSplit = inputStringSongs.split(" ");
        for (int i=0; i<songsStringSplit.length; i++) {
            Cursor rs = songsDB.getSongSingle(songsStringSplit[i]);
            rs.moveToFirst();
            songsInfo.add( new PlaylistSongInfo (
                    songsStringSplit[i],
                    rs.getString(rs.getColumnIndex("Title")),
                    false,
                    false,
                    i
            ));
        }
        songsDB.close();
        return songsInfo;
    }

    private String getUpdatedSongsString() {
        String result = "";
        result = "";
        for(int i=0; i<songs.size(); i++) {
            result = result + songs.get(i).getSongID();
            if (i!=(songs.size()-1)) {
                result = result + " ";
            }
        }
        stringSongs = result;
        return result;
    }

    private void updateDBsongsString (Context context) {
        playlistsDB = new playlistsDBhelper(context);
        playlistsDB.updatePlaylistSongs(playlistID, getUpdatedSongsString());
    }

    public void moveSongUp (Integer songCurrentPosition, Context context) {
        if (songCurrentPosition>0) {
            Song songTemp = songs.get(songCurrentPosition);
            songs.set(songCurrentPosition, songs.get(songCurrentPosition-1));
            songs.set(songCurrentPosition-1, songTemp);
            songs.get(songCurrentPosition).setSongPositionInPlaylist(songCurrentPosition);
            songs.get(songCurrentPosition-1).setSongPositionInPlaylist(songCurrentPosition-1);
            updateDBsongsString(context);
        }
    }

    public void moveSongDown (Integer songCurrentPosition, Context context) {
        if (songCurrentPosition<(songs.size()-1)) {
            Song songTemp = songs.get(songCurrentPosition);
            songs.set(songCurrentPosition, songs.get(songCurrentPosition+1));
            songs.set(songCurrentPosition+1, songTemp);
            songs.get(songCurrentPosition).setSongPositionInPlaylist(songCurrentPosition);
            songs.get(songCurrentPosition+1).setSongPositionInPlaylist(songCurrentPosition+1);
            updateDBsongsString(context);
        }
    }

    public void removeSong (int songCurrentPosition, Context context) {
        songs.remove(songCurrentPosition);
        for (int loop_i=songCurrentPosition;loop_i<songs.size();loop_i++) {
            songs.get(loop_i).setSongPositionInPlaylist(loop_i);
        }
        updateDBsongsString(context);
    }

    public void addSong (Integer songID, Context context) {
        songsDB = new musicDBHelper(context);
        Cursor rs = songsDB.getSongSingle(songID.toString());
        rs.moveToFirst();
        songs.add( new Song (
                songID.toString(),
                rs.getString(rs.getColumnIndex("Title")),
                rs.getString(rs.getColumnIndex("Text")),
                rs.getString(rs.getColumnIndex("Type_")),
                rs.getString(rs.getColumnIndex("File_Name")),
                //rs.getPosition()
                songs.size()
        ));
        updateDBsongsString(context);
    }

    public boolean isSongInPlaylist (String songID) {
        boolean result = false;
        for(int i=0; i<songs.size(); i++) {
            if (songID.equals(songs.get(i).getSongID())) {
                result = true;
            }
        }
        return result;
    }

}

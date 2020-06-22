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
    private String stringPlayTypes;

    private ArrayList<Song> songs = new ArrayList<Song>();

    public String getPlaylistID () { return playlistID; }
    public String getPlaylistName () { return playlistName; }
    public String getSongsString () { return stringSongs; }
    public String getStringPlayTypes () { return stringPlayTypes; }
    public boolean isEditable () {
        boolean result = true;
        if (Integer.parseInt(playlistID)<3) {
            result = false;
        }
        return result;
    }

    private musicDBHelper songsDB;
    private playlistsDBhelper playlistsDB;

    public Playlist (String inputID, String inputName, String inputStringSongs, String inputPlayTypeString, Context context) {
        playlistID = inputID;
        playlistName = inputName;
        stringSongs = inputStringSongs;
        stringPlayTypes = inputPlayTypeString;
        // Make the ArrayList
        songsDB = new musicDBHelper(context);
        String[] songsStringSplit = inputStringSongs.split(" ");
        String[] playTypeStringSplit = inputPlayTypeString.split(" ");
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
                        rs.getString(rs.getColumnIndex("Vocal_File_Name")),
                        rs.getString(rs.getColumnIndex("Instrumental_File_Name")),
                        rs.getString(rs.getColumnIndex("Files_Downloaded")),
                        Integer.parseInt(playTypeStringSplit[i]),
                        i
                ));
            }
        }
        songsDB.close();
    }

    public Playlist (Playlist inputPlaylist) {
        playlistID = inputPlaylist.getPlaylistID();
        playlistName = inputPlaylist.getPlaylistName();
        stringSongs = inputPlaylist.getSongsString();
        stringPlayTypes = inputPlaylist.getStringPlayTypes();
        songs = inputPlaylist.getSongsArrayList();
    }

    public ArrayList<Song> getSongsArrayList () {
        return songs;
    }

    public ArrayList<PlaylistSongInfo> getSongsInfoArrayList (Context context) {
        ArrayList<PlaylistSongInfo> songsInfo = new ArrayList<PlaylistSongInfo>();
        songsDB = new musicDBHelper(context);
        String[] songsStringSplit = stringSongs.split(" ");
        String[] songsStringPlayTypesSplit = stringPlayTypes.split(" ");
        songsInfo.clear();
        songsInfo.ensureCapacity(songsStringSplit.length);
        //List<String> songsStringSplit = inputStringSongs.split(" ");
        for (int i=0; i<songsStringSplit.length; i++) {
            Cursor rs = songsDB.getSongSingle(songsStringSplit[i]);
            rs.moveToFirst();
            boolean songVocalPlayable = false;
            if (Integer.parseInt(songsStringPlayTypesSplit[i])==Song.PLAY_VOCAL) {
                songVocalPlayable = true;
            }
            boolean songInstrumentalPlayable = false;
            if (Integer.parseInt(songsStringPlayTypesSplit[i])==Song.PLAY_INSTRUMENTAL) {
                songInstrumentalPlayable = true;
            }
            songsInfo.add( new PlaylistSongInfo (
                    songsStringSplit[i],
                    rs.getString(rs.getColumnIndex("Title")),
                    songVocalPlayable,
                    songInstrumentalPlayable,
                    Integer.parseInt(songsStringPlayTypesSplit[i]),
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

    private String getUpdatedPlayTypesString() {
        String result = "";
        result = "";
        for(int i=0; i<songs.size(); i++) {
            result = result + songs.get(i).getSongPlayType();
            if (i!=(songs.size()-1)) {
                result = result + " ";
            }
        }
        stringPlayTypes = result;
        return result;
    }

    private void updateDBsongsString (Context context) {
        playlistsDB = new playlistsDBhelper(context);
        playlistsDB.updatePlaylistSongs(playlistID, getUpdatedSongsString(), getUpdatedPlayTypesString());
    }

    public void moveSongUp (Integer songCurrentPosition, Context context) {
        if ( (songCurrentPosition>0)&&(songs.size()>1)  ) {
            Song songTemp = songs.get(songCurrentPosition);
            songs.set(songCurrentPosition, songs.get(songCurrentPosition-1));
            songs.set(songCurrentPosition-1, songTemp);
            songs.get(songCurrentPosition).setSongPositionInPlaylist(songCurrentPosition);
            songs.get(songCurrentPosition-1).setSongPositionInPlaylist(songCurrentPosition-1);
            updateDBsongsString(context);
        }
    }

    public void moveSongDown (Integer songCurrentPosition, Context context) {
        if ( (songCurrentPosition<(songs.size()-1))&&(songs.size()>1) ) {
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

    public void addSong (Integer songID, int playType, Context context) {
        songsDB = new musicDBHelper(context);
        Cursor rs = songsDB.getSongSingle(songID.toString());
        rs.moveToFirst();
        songs.add( new Song (
                songID.toString(),
                rs.getString(rs.getColumnIndex("Title")),
                rs.getString(rs.getColumnIndex("Text")),
                rs.getString(rs.getColumnIndex("Type_")),
                rs.getString(rs.getColumnIndex("Vocal_File_Name")),
                rs.getString(rs.getColumnIndex("Instrumental_File_Name")),
                rs.getString(rs.getColumnIndex("Files_Downloaded")),
                playType,
                songs.size()
        ));
        updateDBsongsString(context);
    }

    public boolean isSongInPlaylist (String songID, int sonPlayType) {
        boolean result = false;
        for(int i=0; i<songs.size(); i++) {
            if (songID.equals(songs.get(i).getSongID()) && (sonPlayType==songs.get(i).getSongPlayType()) ) {
                result = true;
            }
        }
        return result;
    }

}

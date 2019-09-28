package com.grigorov.asparuh.probujdane;

/**
 * Created by agr on 05/09/2019.
 */

public class Song {

    private String songID;
    private String songName;
    private String songText;
    private String songType;
    private String songFileName;
    private String songSubPath;
    private boolean songPlayable;

    public Song (String inputSongID, String inputSongName, String inputSongText,
                 String inputSongType, String inputSongFileName) {
        songID = inputSongID;
        songName = inputSongName;
        songText = inputSongText;
        songType = inputSongType;
        songFileName = inputSongFileName;
        songPlayable = true;
        if (inputSongFileName.equals("")==true) songPlayable=false;
        if (songType.equals("Songs")) {
            songSubPath = "/Pesni" ;
        } else if (songType.equals("Panevrtimia")) {
            songSubPath = "/Panevrtimia";
        } else {
            songSubPath = "/Panevritmia_Instrumental";
        }
    }

    public String getSongID () { return this.songID; }

    public String getSongName () { return this.songName; }

    public String getSongText () { return this.songText; }

    public String getSongType () { return this.songType; }

    public String getSongFileName () { return this.songFileName; }

    public String getSongSubPath () { return this.songSubPath; }

    public boolean isSongPlayble () { return this.songPlayable; }
}

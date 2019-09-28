package com.grigorov.asparuh.probujdane;

/**
 * Created by agr on 08/08/2019.
 */

public class songInfo {

    private String songID;
    private String songName;
    private boolean songPlayable;

    public songInfo (String inputSongID, String inputSongName, boolean inputSongPlayable) {
        songID = inputSongID;
        songName = inputSongName;
        songPlayable = inputSongPlayable;
    }

    public String getSongID () { return this.songID; }

    public String getSongName () { return this.songName; }

    public boolean isSongPlayble () { return this.songPlayable; }

}

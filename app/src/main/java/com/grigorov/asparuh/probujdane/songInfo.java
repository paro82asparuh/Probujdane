package com.grigorov.asparuh.probujdane;

/**
 * Created by agr on 08/08/2019.
 */

public class songInfo {

    private String songID;
    private String songName;
    private boolean songVocalPlayable;
    private boolean songInstrumentalPlayable;

    public songInfo (String inputSongID, String inputSongName, boolean inputSongVocalPlayable, boolean inputSongInstrumentalPlayable) {
        songID = inputSongID;
        songName = inputSongName;
        songVocalPlayable = inputSongVocalPlayable;
        songInstrumentalPlayable = inputSongInstrumentalPlayable;
    }

    public String getSongID () { return this.songID; }

    public String getSongName () { return this.songName; }

    public boolean isSongVocalPlayble () { return this.songVocalPlayable; }

    public boolean isSongInstrumentalPlayble () { return this.songInstrumentalPlayable; }

}

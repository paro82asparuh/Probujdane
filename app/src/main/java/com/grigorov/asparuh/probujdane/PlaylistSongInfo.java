package com.grigorov.asparuh.probujdane;

/**
 * Created by agr on 12/12/2019.
 */

public class PlaylistSongInfo extends com.grigorov.asparuh.probujdane.songInfo {

    private boolean songTicked;
    private int songPositionInPlaylist;
    private int songPlayType;

    public PlaylistSongInfo (String inputSongID, String inputSongName, boolean inputSongVocalPlayable, boolean inputSongInstrumentalPlayable,
                             int inputSongPlayType, boolean inputTicked) {
        super(inputSongID, inputSongName, inputSongVocalPlayable, inputSongInstrumentalPlayable );
        songTicked = inputTicked;
        songPositionInPlaylist = -1;
        songPlayType = inputSongPlayType;
    }

    public PlaylistSongInfo (String inputSongID, String inputSongName, boolean inputSongVocalPlayable, boolean inputSongInstrumentalPlayable,
                             int inputSongPlayType, boolean inputTicked, int inputSongPositionInPlaylist) {
        super(inputSongID, inputSongName, inputSongVocalPlayable, inputSongInstrumentalPlayable );
        songTicked = inputTicked;
        songPositionInPlaylist = inputSongPositionInPlaylist;
        songPlayType = inputSongPlayType;
    }

    public void setSongTicked (boolean inputTicked) { songTicked = inputTicked; }

    public boolean getSongTicked () { return songTicked; }

    public int getSongPositionInPlaylist() { return songPositionInPlaylist; }

    public int getSongPlayType() { return songPlayType; }

}

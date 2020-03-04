package com.grigorov.asparuh.probujdane;

/**
 * Created by agr on 12/12/2019.
 */

public class PlaylistSongInfo extends com.grigorov.asparuh.probujdane.songInfo {

    private boolean songTicked;
    private int songPositionInPlaylist;

    public PlaylistSongInfo (String inputSongID, String inputSongName, boolean inputSongPlayable,
                             boolean inputTicked) {
        super(inputSongID, inputSongName, inputSongPlayable );
        songTicked = inputTicked;
        songPositionInPlaylist = -1;
    }

    public PlaylistSongInfo (String inputSongID, String inputSongName, boolean inputSongPlayable,
                             boolean inputTicked, int inputSongPositionInPlaylist) {
        super(inputSongID, inputSongName, inputSongPlayable );
        songTicked = inputTicked;
        songPositionInPlaylist = inputSongPositionInPlaylist;
    }

    public void setSongTicked (boolean inputTicked) { songTicked = inputTicked; }

    public boolean getSongTicked () { return songTicked; }

    public int getSongPositionInPlaylist() { return songPositionInPlaylist; }

}

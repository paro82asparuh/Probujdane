package com.grigorov.asparuh.probujdane;

/**
 * Created by agr on 05/09/2019.
 */

public class Song {

    public final static int PLAY_UNDEFINED = 0;
    public final static int PLAY_VOCAL = 1;
    public final static int PLAY_INSTRUMENTAL = 2;
    public final static int FILES_NOT_DOWNLOADED = 0;
    public final static int FILES_DOWNLOADED = 1;

    private String songID;
    private String songName;
    private String songText;
    private String songType;
    private String songVocalFileName;
    private String songInstrumentalFileName;
    private String songVocalFileLink;
    private String songInstrumentalFileLink;
    private String songSubPath;
    private boolean songVocalPlayable;
    private boolean songInstrumentalPlayable;
    private Integer songPositionInPlaylist;
    private int songPlayType;

    public Song (String inputSongID, String inputSongName, String inputSongText,
                 String inputSongType, String inputSongVocalFileName, String inputSongInstrumentalFileName,
                 String inputFilesDownloaded) {
        songID = inputSongID;
        songName = inputSongName;
        songText = inputSongText;
        songType = inputSongType;
        songVocalFileName = inputSongVocalFileName;
        songInstrumentalFileName = inputSongInstrumentalFileName;
        songVocalPlayable = (inputSongVocalFileName.equals("") == false) && (Integer.parseInt(inputFilesDownloaded) == Song.FILES_DOWNLOADED);
        songInstrumentalPlayable = (inputSongInstrumentalFileName.equals("") == false) && (Integer.parseInt(inputFilesDownloaded) == Song.FILES_DOWNLOADED);
        if (songType.equals("Songs")) {
            songSubPath = "/Pesni" ;
        } else {
            songSubPath = "/Panevritmia";
        }
        songPositionInPlaylist = -1;
        songPlayType = Song.PLAY_UNDEFINED;
    }

    public Song (String inputSongID, String inputSongName, String inputSongText,
                 String inputSongType, String inputSongVocalFileName, String inputSongInstrumentalFileName,
                 String inputFilesDownloaded, int inputSongPlayType) {
        this(inputSongID, inputSongName, inputSongText,
                inputSongType, inputSongVocalFileName, inputSongInstrumentalFileName, inputFilesDownloaded);
        songPlayType = inputSongPlayType;
    }

    public Song (String inputSongID, String inputSongName, String inputSongText,
                 String inputSongType, String inputSongVocalFileName, String inputSongInstrumentalFileName,
                 String inputFilesDownloaded, int inputSongPlayType, Integer inputSongPositionInPlaylist ) {
        this(inputSongID, inputSongName, inputSongText,
                inputSongType, inputSongVocalFileName, inputSongInstrumentalFileName, inputFilesDownloaded);
        songPositionInPlaylist = inputSongPositionInPlaylist;
        songPlayType = inputSongPlayType;
    }

    public Song (String inputSongID, String inputSongName, String inputSongText,
                 String inputSongType, String inputSongVocalFileName, String inputSongInstrumentalFileName,
                 String inputFilesDownloaded, String inputSongVocalFileLink, String inputSongInstrumentalFileLink) {
        this(inputSongID, inputSongName, inputSongText,
                inputSongType, inputSongVocalFileName, inputSongInstrumentalFileName, inputFilesDownloaded);
        songVocalFileLink = inputSongVocalFileLink;
        songInstrumentalFileLink = inputSongInstrumentalFileLink;
    }


    public String getSongID () { return this.songID; }

    public String getSongName () { return this.songName; }

    public String getSongText () { return this.songText; }

    public String getSongType () { return this.songType; }

    public String getSongVocalFileName () { return this.songVocalFileName; }

    public String getSongInstrumentalFileName () { return this.songInstrumentalFileName; }

    public String getSongSubPath () { return this.songSubPath; }

    public boolean isSongVocalPlayable () { return this.songVocalPlayable; }

    public boolean isSongInstrumentalPlayable () { return this.songInstrumentalPlayable; }

    public Integer getSongPositionInPlaylist() { return songPositionInPlaylist; }

    public void setSongPositionInPlaylist (Integer inputSongPositionInPlaylist) {
        songPositionInPlaylist = inputSongPositionInPlaylist;
    }

    public void setSongPlayType(int inputSongPlayType) {
        songPlayType = inputSongPlayType;
    }

    public int getSongPlayType() { return songPlayType; }

    public String getSongVocalFileLink() { return songVocalFileLink; }

    public String getSongInstrumentalFileLink() { return songInstrumentalFileLink; }

}

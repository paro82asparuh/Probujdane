package com.grigorov.asparuh.probujdane;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class MusicMenuActivity extends AppCompatActivity {

    private musicDBHelper mydb;

    private final ArrayList<String> pathsPanevritmiaTracks = new ArrayList<String>() ;
    private static final String namePlaylistPanevritmia = "Playlist_Panevritmia";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_menu);

        mydb = new musicDBHelper(this);

    }

    public void onResume () {
        super.onResume();
        mydb = new musicDBHelper(this);
    }

    public void onPause () {
        super.onPause();
        mydb.close();
    }

    public void startPanevritmiaTask (View view) {
        Cursor rs = mydb.getPathsPanevritmiaTRaks();
        rs.moveToFirst();
        pathsPanevritmiaTracks.ensureCapacity(rs.getCount());

        for (int i=1; i <=rs.getCount(); i++) {
            pathsPanevritmiaTracks.add( this.getApplicationContext().getExternalFilesDir(null)
                    + rs.getString(rs.getColumnIndex("File_Name")) );
            rs.moveToNext();
        }
        if (!rs.isClosed())  {
            rs.close();
        }

        writePlaylist(this.getApplicationContext(), namePlaylistPanevritmia, pathsPanevritmiaTracks);
        playPlaylist(idForplaylist(this.getApplicationContext(), namePlaylistPanevritmia));

    }

    private void playPlaylist (int playlistID) {
        Intent intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
        intent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS,
                MediaStore.Audio.Playlists.ENTRY_CONTENT_TYPE);
        intent.putExtra("android.intent.extra.playlist", playlistID);
        intent.putExtra(SearchManager.QUERY, playlistID);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

//    public void addnewPlaylist(Context context, String newplaylist) {
//        ContentResolver resolver = context.getContentResolver();
//        ContentValues values = new ContentValues(1);
//        values.put(MediaStore.Audio.Playlists.NAME, newplaylist);
//        resolver.insert(uri, values);
//    }

    public void addTrackToPlaylist(Context context, String audio_id,
                                   long playlist_id, int pos) {
        Uri newuri = MediaStore.Audio.Playlists.Members.getContentUri(
                "external", playlist_id);
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, pos);
        values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audio_id);
        values.put(MediaStore.Audio.Playlists.Members.PLAYLIST_ID,
                playlist_id);
        resolver.insert(newuri, values);
    }



    public static Cursor query(Context context, Uri uri, String[] projection,
                               String selection, String[] selectionArgs, String sortOrder, int limit) {
        try {
            ContentResolver resolver = context.getContentResolver();
            if (resolver == null) {
                return null;
            }
            if (limit > 0) {
                uri = uri.buildUpon().appendQueryParameter("limit", String.valueOf(limit)).build();
            }
            return resolver.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (UnsupportedOperationException ex) {
            return null;
        }
    }

    public static Cursor query(Context context, Uri uri, String[] projection,
                               String selection, String[] selectionArgs, String sortOrder) {
        return query(context, uri, projection, selection, selectionArgs, sortOrder, 0);
    }

    private static int intFromCursor(Cursor c) {
        int id = -1;
        if (c != null) {
            c.moveToFirst();
            if (!c.isAfterLast()) {
                id = c.getInt(0);
            }
        }
        c.close();
        return id;
    }

    public static int idForplaylist(Context context, String name) {
        Cursor c = query(context, MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Playlists._ID },
                MediaStore.Audio.Playlists.NAME + "=?",
                new String[] { name },
                MediaStore.Audio.Playlists.NAME);
        return intFromCursor(c);
    }

    public static int idFortrack(Context context, String path) {
        Cursor c = query(context, MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Media._ID },
                MediaStore.Audio.Media.DATA + "=?",
                new String[] { path },
                MediaStore.Audio.Media.DATA);
        return intFromCursor(c);
    }

    public static void writePlaylist(Context context, String playlistName, ArrayList<String> paths) {
        ContentResolver resolver = context.getContentResolver();
        int playlistId = idForplaylist(context, playlistName);
        Uri uri;
        if (playlistId == -1) {
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Audio.Playlists.NAME, playlistName);
            uri = resolver.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, values);
            playlistId = idForplaylist(context, playlistName);
        } else {
            uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId);
        }

        // Delete everything from the old playlist
        context.getContentResolver().delete(uri, null, null);

        // Add all the new tracks to the playlist.
        int size = paths.size();
        ContentValues[] values = new ContentValues[size];
        for (int k = 0; k < size; ++k) {
            values[k] = new ContentValues();
            values[k].put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, k);
            values[k].put(MediaStore.Audio.Playlists.Members.AUDIO_ID, idFortrack(context, paths.get(k)));
        }

        resolver.bulkInsert(uri, values);
    }
}





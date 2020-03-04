package com.grigorov.asparuh.probujdane;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Created by agr on 14/12/2019.
 */

public class PlaylistDeleteDialogFragment extends DialogFragment {

    public interface PlaylistDeleteDialogListener {
        void onPlaylistDeleteDialogPositiveClick(DialogFragment dialog);
        void onPlaylistDeleteDialogNegativeClick(DialogFragment dialog);
    }

    private String playlistName;
    public void setPlaylistName(String inputPlaylistname) {
        playlistName = inputPlaylistname;
    }

    PlaylistDeleteDialogFragment.PlaylistDeleteDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = context instanceof Activity ? (Activity) context : null;
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (PlaylistDeleteDialogFragment.PlaylistDeleteDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement PlaylistDeleteDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String dialogMessage = getResources().getString(R.string.dialog_playlist_delete)+" "+playlistName+"?";
        builder.setMessage(dialogMessage)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onPlaylistDeleteDialogPositiveClick(PlaylistDeleteDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onPlaylistDeleteDialogNegativeClick(PlaylistDeleteDialogFragment.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}

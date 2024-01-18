package com.grigorov.asparuh.probujdane;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;

public class SongsDownloadDialogFragment extends DialogFragment {

    public interface SongsDownloadDialogListener {
        void onSongsDownloadDialogPositiveClick(DialogFragment dialog);
        void onSongsDownloadDialogNegativeClick(DialogFragment dialog);
    }

    SongsDownloadDialogFragment.SongsDownloadDialogListener mListener;

    private Long downloadSizeInMB;
    public void setDownloadSizeInMB (long inputDownloadSizeInMB) { downloadSizeInMB = inputDownloadSizeInMB; }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = context instanceof Activity ? (Activity) context : null;
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (SongsDownloadDialogFragment.SongsDownloadDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity
                    + " must implement SongsDownloadDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String messageQuestion = getResources().getString(R.string.dialog_download_songs_start)
                + downloadSizeInMB.toString()
                + getResources().getString(R.string.dialog_download_songs_end);
        builder.setMessage(messageQuestion)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onSongsDownloadDialogPositiveClick(SongsDownloadDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onSongsDownloadDialogNegativeClick(SongsDownloadDialogFragment.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}

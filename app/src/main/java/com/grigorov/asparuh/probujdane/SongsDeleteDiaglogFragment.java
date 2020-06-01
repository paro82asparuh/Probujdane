package com.grigorov.asparuh.probujdane;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class SongsDeleteDiaglogFragment extends DialogFragment {

    public interface SongsDeleteDialogListener {
        void onSongsDeleteDialogPositiveClick(DialogFragment dialog);
        void onSongsDeleteDialogNegativeClick(DialogFragment dialog);
    }

    private Integer numberSongsToDelete;
    public void setNumberSongsToDelete(Integer inputNumberSongsToDelete) {
        numberSongsToDelete = inputNumberSongsToDelete;
    }

    SongsDeleteDiaglogFragment.SongsDeleteDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = context instanceof Activity ? (Activity) context : null;
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (SongsDeleteDiaglogFragment.SongsDeleteDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement SongsDeleteDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String dialogMessage;
        if (numberSongsToDelete==1) {
            dialogMessage = getResources().getString(R.string.dialog_songs_delete_one);
        } else {
            dialogMessage = getResources().getString(R.string.dialog_songs_delete_start) + " "
                    + numberSongsToDelete.toString()+" "
                    + getResources().getString(R.string.dialog_songs_delete_end);
        }
        builder.setMessage(dialogMessage)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onSongsDeleteDialogPositiveClick(SongsDeleteDiaglogFragment.this);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onSongsDeleteDialogNegativeClick(SongsDeleteDiaglogFragment.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}


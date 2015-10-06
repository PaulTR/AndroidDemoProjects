package com.tutsplus.mapsdemo;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

/**
 * Created by Paul on 8/11/15.
 */
public class PlayServicesUnavailableDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
        builder.setMessage( getResources().getString( R.string.dialog_play_services_unavailable ) );
        builder.setNegativeButton(R.string.dialog_close_program, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
                getActivity().finish();
            }
        });

        builder.setPositiveButton( R.string.dialog_download_play_services, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent( Intent.ACTION_VIEW );
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.gms&hl=en") );
                startActivity( intent );
            }
        });

        return builder.create();
    }
}

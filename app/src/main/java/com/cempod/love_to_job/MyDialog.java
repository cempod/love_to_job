package com.cempod.love_to_job;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class MyDialog extends DialogFragment {
    @NonNull

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        return builder
                .setTitle("Сегодня по графику:")
                .setIcon(android.R.drawable.ic_dialog_alert)

                .setSingleChoiceItems(new String[]{"первый рабочий день", "второй рабочий день", "первый выходной", "второй выходной"},-1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                ((Settings) getActivity()).okClicked(id);
                dialog.dismiss();
            }
                })
                .create();
    }


}
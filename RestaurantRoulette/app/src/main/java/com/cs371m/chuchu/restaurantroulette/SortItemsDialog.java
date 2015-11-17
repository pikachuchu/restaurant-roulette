package com.cs371m.chuchu.restaurantroulette;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by chuchu on 11/17/15.
 */
public class SortItemsDialog extends DialogFragment {
    public interface SortItemsListener {
        void onSelectSortItem(String sortOption);
    }
    public final String[] sortOptions = { "Name", "Distance", "Time" };
    private SortItemsListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (SortItemsListener) activity;
        } catch(ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement SortItemsListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Sort By")
            .setItems(sortOptions, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listener.onSelectSortItem(sortOptions[which]);
                }
            });
        return builder.create();
    }
}

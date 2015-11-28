package com.cs371m.chuchu.restaurantroulette;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Toast;

/**
 * Created by chuchu on 11/17/15.
 */
public class RateDialog extends DialogFragment {
    public interface RateAttendeeListener {
        void onRate(String attendee, float rating, String reliability);
    }
    private RateAttendeeListener listener;
    private String attendee;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (RateAttendeeListener) activity;
        } catch(ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement RateAttendeeListener");
        }
        attendee = getArguments().getString("attendee");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.rate_dialog, null);
        builder.setTitle("Rate Attendee: " + attendee)
            .setView(view)
            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    float rating = ((RatingBar) view.findViewById(R.id.ratingBar)).getRating();
                    if (rating == 0) {
                        Toast.makeText(getActivity().getApplicationContext(), "Please select reliability", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int selected = ((RadioGroup) view.findViewById(R.id.radioGroup)).getCheckedRadioButtonId();
                    if (selected == -1) {
                        Toast.makeText(getActivity().getApplicationContext(), "Please select reliability", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String reliability = "";
                    if (selected == R.id.onTime) {
                        reliability = "onTime";
                    } else if (selected == R.id.late) {
                        reliability = "late";
                    } else if (selected == R.id.noShow) {
                        reliability = "noShow";
                    }
                    Log.d("RateDialog", "calling onRate");
                    listener.onRate(attendee, rating, reliability);
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        return builder.create();
    }
}

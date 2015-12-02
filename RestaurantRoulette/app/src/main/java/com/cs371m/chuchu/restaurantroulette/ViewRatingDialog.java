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
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by chuchu on 11/17/15.
 */
public class ViewRatingDialog extends DialogFragment {
    public interface ViewRatingListener {
        void onRate();
    }
    private ViewRatingListener listener;
    private String attendee;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (ViewRatingListener) activity;
        } catch(ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ViewRatingListener");
        }
        attendee = getArguments().getString("attendee");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.view_rating_dialog, null);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Rating");
        query.whereEqualTo("username", attendee);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e != null) {
                    Log.d("ViewRatingDialog", e.getMessage());
                    return;
                }
                if (objects.size() == 0) {
                    return;
                }
                ParseObject obj = objects.get(0);

                TextView numRatings = (TextView) view.findViewById(R.id.numRatings);
                TextView avgRating = (TextView) view.findViewById(R.id.avgRating);
                TextView numOnTime = (TextView) view.findViewById(R.id.numOnTime);
                TextView numLate = (TextView) view.findViewById(R.id.numLate);
                TextView numNoShow = (TextView) view.findViewById(R.id.numNoShow);

                numRatings.append("" + obj.get("numRatings"));
                avgRating.append("" + obj.get("rating"));
                numOnTime.append("" + obj.get("onTime"));
                numLate.append("" + obj.get("late"));
                numNoShow.append("" + obj.get("noShow"));

            }
        });
        builder.setTitle("Rating for " + attendee)
            .setView(view)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
        return builder.create();
    }
}

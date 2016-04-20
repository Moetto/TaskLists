package t3waii.tasklists;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by matti on 4/1/16.
 */
public class ManageGroupLocations extends Activity {
    private int PLACE_PICKER_REQUEST = 1;
    public static ArrayAdapter<Location> locationListAdapter;
    private static final String TAG = "ManageGroupLocations";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_group_locations);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                    Intent intent = intentBuilder.build(ManageGroupLocations.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);

                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        if(locationListAdapter == null) {
            locationListAdapter = new ArrayAdapter<Location>(this, R.layout.location_layout, new ArrayList<Location>()) {
                View.OnClickListener handleClick = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Location l = null;

                        try {
                            l = (Location) v.getTag();
                        } catch (NullPointerException e) {
                            Log.d(TAG, "Unable to get Location from tag!");
                        }

                        switch (v.getId()) {
                            case R.id.remove_location:
                                // TODO: send delete and remove from list on success
                                if(l != null) {
                                    locationListAdapter.remove(l);
                                }
                                break;
                            default:
                                Log.d(TAG, "Halp, I've been clicked but I don't know where!");
                        }
                    }
                };

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    LayoutInflater inflater = getLayoutInflater();
                    View view = inflater.inflate(R.layout.location_layout, null);
                    Location location = getItem(position);

                    TextView name = (TextView) view.findViewById(R.id.location_name);
                    name.setText(location.toString());

                    View removeButton = view.findViewById(R.id.remove_location);
                    removeButton.setOnClickListener(handleClick);
                    removeButton.setTag(location);

                    return view;
                }
            };
        }

        ListView locationList = (ListView)findViewById(R.id.location_list);
        locationList.setAdapter(locationListAdapter);

        if(locationListAdapter.getCount() == 0) {
            fab.performClick();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                takeLocationName(place.getLatLng());
            }
        }
    }

    private void takeLocationName(final LatLng location) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.group_new, null);
        TextView textLabel = (TextView)promptView.findViewById(R.id.textLabel);
        textLabel.setText(getResources().getString(R.string.location_name));
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.NameDialogCustom);
        alertDialogBuilder.setView(promptView);
        final EditText input = (EditText) promptView.findViewById(R.id.newGroupName);
        // setup a dialog window
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // get user input and set it to result
                        // TODO: post new group, get name by input.getText().toString()
                        locationListAdapter.add(new Location(input.getText().toString(), location));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // create an alert dialog
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }
}
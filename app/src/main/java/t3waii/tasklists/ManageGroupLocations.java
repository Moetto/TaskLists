package t3waii.tasklists;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by matti on 4/1/16.
 */
public class ManageGroupLocations extends Activity {
    private int PLACE_PICKER_REQUEST = 1;
    private List<Location> locations = new ArrayList<>();
    public static ArrayAdapter<Location> locationListAdapter;
    private static final String TAG = "TasksManageGroupLocati";
    private BroadcastReceiver locationReceiver;
    String[] actions = new String[]{Location.ACTION_LOCATION_REMOVED, Location.ACTION_NEW_LOCATION, Location.ACTION_NEW_LOCATION};

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
        locations = MainActivity.getLocations();
        if (locationListAdapter == null) {
            locationListAdapter = new ArrayAdapter<Location>(this, R.layout.location_layout, locations) {
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
                                if (l != null) {
                                    NetworkLocations.deleteLocation(ManageGroupLocations.this, l);
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

        final ListView locationList = (ListView) findViewById(R.id.location_list);
        locationList.setAdapter(locationListAdapter);

        if (locationListAdapter.getCount() == 0) {
            fab.performClick();
        }

        locationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case Location.ACTION_GET_LOCATIONS:
                        try {
                            locations.clear();
                            locations.addAll(Location.parseLocations(intent.getStringExtra(Location.EXTRA_LOCATIONS_JSON)));
                        } catch (JSONException ex) {
                            Log.e(TAG, "Error in locations JSON");
                            Log.e(TAG, Log.getStackTraceString(ex));
                        }
                        locationListAdapter.notifyDataSetChanged();
                        break;
                    case Location.ACTION_NEW_LOCATION:
                        try {
                            Location newLocation = new Location(new JSONObject(intent.getStringExtra(Location.EXTRA_LOCATION)));
                            if (!locations.contains(newLocation)) {
                                locations.add(newLocation);
                                locationListAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException ex) {
                            Log.e(TAG, "Error in location data");
                            Log.e(TAG, Log.getStackTraceString(ex));
                        }
                        break;
                    case Location.ACTION_LOCATION_REMOVED:
                        Log.d(TAG, "Removing location");
                        int removedId = intent.getIntExtra(Location.EXTRA_REMOVED_ID, 0);
                        for(Location location : locations) {
                            if (location.getId() == removedId) {
                                locations.remove(location);
                                break;
                            }
                        }
                        locationListAdapter.notifyDataSetChanged();
                        break;
                }
                Log.d(TAG, "Received new locations");
            }
        };
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
        TextView textLabel = (TextView) promptView.findViewById(R.id.textLabel);
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
                        Map<String, String> values = new HashMap<>();
                        values.put("name", input.getText().toString());
                        values.put("latitude", Double.toString(location.latitude));
                        values.put("longitude", Double.toString(location.longitude));
                        NetworkLocations.postNewLocation(values, ManageGroupLocations.this);
                        //locationListAdapter.add(new Location(0, input.getText().toString(), location));
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

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(locationReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (String action : actions) {
            IntentFilter intentFilter = new IntentFilter(action);
            registerReceiver(locationReceiver, intentFilter);
        }
    }
}
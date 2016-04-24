package t3waii.tasklists;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by moetto on 3/11/16.
 */
public class TODOTasksFragment extends TasksFragment implements PopupMenu.OnMenuItemClickListener {
    private static final int LOCATION_PERMISSION_REQUEST = 11;
    private GoogleApiClient googleApiClient;
    private PendingIntent geofencePendingIntent;
    private Set<Task> tasksPendingGeofence = new HashSet<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.todo_tasks_list, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        TAG = "TODOTasksFragment";
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();

        ArrayAdapter<Task> taskListAdapter = new ArrayAdapter<Task>(getContext(), R.layout.complex_task, new ArrayList<Task>()) {
            View.OnClickListener handleClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Task t = null;

                    try {
                        t = (Task) v.getTag();
                    } catch (NullPointerException e) {
                        Log.d(TAG, "Unable to get Task from tag!");
                    }

                    switch (v.getId()) {
                        case R.id.complete_button:
                            Log.d(TAG, "complete clicked");
                            break;
                        case R.id.edit_button:
                            Log.d(TAG, "edit clicked");
                            PopupMenu popupMenu = new PopupMenu(getActivity(), v);
                            popupMenu.getMenuInflater().inflate(R.menu.edit_task, popupMenu.getMenu());
                            Intent i = new Intent();
                            i.putExtra("taskId", t.getId());
                            for (int j = 0; j < popupMenu.getMenu().size(); j++) {
                                MenuItem menuItem = popupMenu.getMenu().getItem(j);
                                menuItem.setIntent(i);
                            }
                            popupMenu.setOnMenuItemClickListener(TODOTasksFragment.this);
                            popupMenu.show();
                            break;
                        default:
                            Log.d(TAG, "Halp, I've been clicked but I don't know where!");
                    }
                }
            };

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                Task task = tasks.get(position);
                if (task.getChildren().isEmpty()) {
                    convertView = createComplexTask(inflater, task);
                    TextView textView = (TextView) convertView.findViewById(R.id.complex_text);
                    textView.setText(task.getName());
                    return convertView;
                }

                convertView = getActivity().getLayoutInflater().inflate(R.layout.group_task_parent, null);
                View claimButton = convertView.findViewById(R.id.claim_button);
                claimButton.setVisibility(View.GONE);
                addListenerAndTag(convertView, task);
                TextView parentText = (TextView) convertView.findViewById(R.id.complex_text);
                parentText.setText(task.getName());

                LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.group_task_parent_layout);
                List<Task> children = task.getChildren();
                for (int i = 0; i < children.size(); i++) {
                    View childView = createChildView(getActivity().getLayoutInflater(), (i == (children.size() - 1) ? true : false), children.get(i));
                    TextView textView = (TextView) childView.findViewById(R.id.complex_text);
                    textView.setText(children.get(i).getName());
                    linearLayout.addView(childView, (i + 1));
                }
                return convertView;
            }

            private View createComplexTask(LayoutInflater inflater, Task task) {
                addGeoFenceIfAvailable(task);
                View view = inflater.inflate(R.layout.complex_task, null);
                View claimButton = view.findViewById(R.id.claim_button);
                claimButton.setVisibility(View.GONE);
                addListenerAndTag(view, task);
                return view;
            }

            private View createChildView(LayoutInflater inflater, boolean lastChild, Task childTask) {
                View view = inflater.inflate(R.layout.child_complex_task, null);
                View claimButton = view.findViewById(R.id.claim_button);
                claimButton.setVisibility(View.GONE);
                if (lastChild) {
                    ImageView imageView = (ImageView) view.findViewById(R.id.arrow);
                    imageView.setImageResource(R.drawable.tree_end);
                }
                addListenerAndTag(view, childTask);
                return view;
            }

            private void addListenerAndTag(View v, Task t) {
                ImageButton imageButton = (ImageButton) v.findViewById(R.id.edit_button);
                imageButton.setOnClickListener(handleClick);
                imageButton.setTag(t);
                imageButton = (ImageButton) v.findViewById(R.id.complete_button);
                imageButton.setOnClickListener(handleClick);
                imageButton.setTag(t);
            }
        };

        super.onActivityCreated(savedInstanceState);
        setListAdapter(taskListAdapter);
    }

    private PendingIntent getGeofencePendingIntent() {
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(getContext(), GeofenceIntentService.class);
        geofencePendingIntent = PendingIntent.getService(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    private void addPendingGeofences() {
        for (Task task : tasksPendingGeofence) {
            addGeoFence(task);
            tasksPendingGeofence.remove(task);
        }
    }

    private void addGeoFenceIfAvailable(Task task) {
        if (task.getLatitude() == 0 || task.getLongitude() == 0) {
            return;
        }

        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            tasksPendingGeofence.add(task);
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
        } else {
            addGeoFence(task);
        }
    }

    @Override
    protected boolean affectThisFragment(Task task) {
        return task.getResponsibleMemberId() == MainActivity.getSelfGroupMemberId() && !task.getCompleted();
    }

    private void addGeoFence(Task task) {
        new AsyncTask<Task, Void, Void>() {
            @Override
            protected Void doInBackground(Task... params) {
                Task task = params[0];

                Geofence geofence = new Geofence.Builder()
                        .setRequestId("" + task.getId())
                        .setCircularRegion(
                                task.getLatitude(),
                                task.getLongitude(),
                                1000)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                        .setLoiteringDelay(1000)
                        .setExpirationDuration(Geofence.NEVER_EXPIRE)
                        .setRequestId("" + task.getId() + ";" + task.getName())
                        .build();

                GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                        .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL | GeofencingRequest.INITIAL_TRIGGER_ENTER)
                        .addGeofence(geofence)
                        .build();

                if (googleApiClient.blockingConnect().isSuccess() &&
                        ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    LocationServices.GeofencingApi.addGeofences(googleApiClient, geofencingRequest, getGeofencePendingIntent());
                    Log.d(TAG, "Added geofence");

                } else {
                    Log.d(TAG, "Adding geofence failed");
                }
                return null;
            }
        }.execute(task);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                addPendingGeofences();
            }
        }
    }

    public void removeGeoFence(Task task) {
        if (task.getLatitude() != 0 && task.getLongitude() != 0) {
            ArrayList<String> removed = new ArrayList<>();
            removed.add("" + task.getId() + ";" + task.getName());
            LocationServices.GeofencingApi.removeGeofences(googleApiClient, removed);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int taskId = item.getIntent().getIntExtra("taskId", 0);
        Log.d(TAG, "taskId:" + Integer.toString(taskId)); //getActionView().toString()

        switch (item.getItemId()) {
            case R.id.cancel_task:
                Log.d(TAG, "cancel clicked");
                return true;
            case R.id.edit_task:
                Log.d(TAG, "edit clicked");
                return true;
            default:
                Log.d(TAG, "dunno what was clicked");
                return false;
        }
    }

    @Override
    public void addTask(Task task) {
        addGeoFence(task);
        super.addTask(task);
    }

    @Override
    public void removeTask(Task task) {
        removeGeoFence(task);
        super.removeTask(task);
    }
}

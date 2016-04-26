package t3waii.tasklists;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by moetto on 3/15/16.
 */
public class NewTask extends Activity implements View.OnFocusChangeListener {
    private static final String TAG = "NewTask";
    private EditText dueDate, dueTime, estimatedDate, estimatedTime;
    private DatePickerDialog dueDateDialog, estimatedDateDialog;
    private TimePickerDialog dueTimeDialog, estimatedTimeDialog;

    private SimpleDateFormat dateFormatter, timeFormatter;

    private Spinner newTaskAssignedTo, newTaskLocation;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_new_task);

        setDueDateDialog();
        setDueTimeDialog();

        setLocationElement();
        setAssignedToElement();

        Intent i = getIntent();
        if (i.getBooleanExtra("edit", false)) {
            int taskId = i.getIntExtra("taskId", 0);
            if (taskId == 0) {
                Log.d(TAG, "unable to find editable task id, aborting!");
                finish();
            }
            setEditMode(i.getBooleanExtra("creator", false), i.getBooleanExtra("assigned", false), MainActivity.getTaskById(taskId));
        }
    }

    public void setEditMode(boolean creator, boolean assigned, final Task task) {
        EditText name = (EditText) findViewById(R.id.newTaskName);
        name.setText(task.getName());

        Button save = (Button) findViewById(R.id.newTaskSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> values = parseValues();

                String name = values.get("title");
                String description = values.get("description");
                String deadline = values.get("deadline");
                String estimatedCompletionTime = values.get("estimated_completion_time");
                String responsibleMember = values.get("responsible_member");
                String location = values.get("location");

                Map<String, String> changedValues = new HashMap<>();

                if (name != task.getName()) {
                    changedValues.put("title", name);
                    changedValues.put("description", description);
                }

                String taskDeadline = task.getDeadline() != null ? Long.toString(task.getDeadline().getTime()) : "";
                if (deadline != taskDeadline) {
                    changedValues.put("deadline", deadline);
                }

                if (estimatedCompletionTime != null || task.getEstimatedCompletion() != null) {
                    if (estimatedCompletionTime != Long.toString(task.getEstimatedCompletion().getTime())) {
                        changedValues.put("estimated_completion_time", estimatedCompletionTime);
                    }
                }

                if (responsibleMember != Integer.toString(task.getResponsibleMemberId())) {
                    changedValues.put("responsible_member", responsibleMember);
                }

                if (location != Integer.toString(task.getLocation())) {
                    changedValues.put("location", location);
                }

                Log.d(TAG, "changed:" + changedValues);

                //TODO: find differences and send to server
                finish();
            }
        });

        if (assigned) {
            TextView estimatedLabel = (TextView) findViewById(R.id.editTaskEstimatedLabel);
            estimatedLabel.setVisibility(View.VISIBLE);
            LinearLayout estimatedFields = (LinearLayout) findViewById(R.id.editTaskEstimatedFields);
            estimatedFields.setVisibility(View.VISIBLE);

            estimatedDate = (EditText) findViewById(R.id.editTaskDueDate);
            estimatedTime = (EditText) findViewById(R.id.editTaskDueTime);

            estimatedDate.setInputType(InputType.TYPE_NULL);
            estimatedTime.setInputType(InputType.TYPE_NULL);

            initializeEstimatedDialogs();

            estimatedDate.setOnFocusChangeListener(this);
            estimatedTime.setOnFocusChangeListener(this);
        }

        if (!creator) {
            name.setEnabled(false);

            dueDate.setOnFocusChangeListener(null);
            dueDate.setFocusable(false);
            dueDate.setTextColor(Color.LTGRAY);
            if (dueDate.getText().length() == 0) {
                dueDate.setHint("");
            }
            dueTime.setOnFocusChangeListener(null);
            dueTime.setFocusable(false);
            dueTime.setTextColor(Color.LTGRAY);
            if (dueTime.getText().length() == 0) {
                dueTime.setHint("");
            }

            ImageButton clearDue = (ImageButton) findViewById(R.id.newTaskClearDue);
            clearDue.setVisibility(View.GONE);

            Spinner assignedTo = (Spinner) findViewById(R.id.newTaskAssignedTo);
            assignedTo.setEnabled(false);

            Spinner location = (Spinner) findViewById(R.id.newTaskLocation);
            location.setEnabled(false);
        }
    }

    private void initializeEstimatedDialogs() {
        Calendar newCalendar = Calendar.getInstance();

        estimatedDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                estimatedDate.setText(dateFormatter.format(newDate.getTime()));
                estimatedDate.clearFocus();
                if (estimatedTime.getText().length() == 0) {
                    estimatedTimeDialog.show();
                }
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        estimatedDateDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                estimatedDate.clearFocus();
            }
        });

        estimatedTimeDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar newTime = Calendar.getInstance();
                newTime.set(1, 1, 1970, hourOfDay, minute);
                estimatedTime.setText(timeFormatter.format(newTime.getTime()));
                estimatedTime.clearFocus();
            }
        }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(getApplicationContext()));

        estimatedTimeDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                estimatedTime.clearFocus();
            }
        });
    }

    public void clickSaveButton(View v) {
        Map<String, String> values = parseValues();

        Log.d(TAG, values.toString());

        NetworkTasks.postNewTask(this, values);
        finish();
    }

    private Map<String, String> parseValues() {
        Map<String, String> values = new HashMap<>();

        EditText name = (EditText) findViewById(R.id.newTaskName);
        String title = name.getText().toString();
        values.put("title", title);
        values.put("description", title);

        if (dueDate.getText().toString().length() > 0 || dueTime.getText().toString().length() > 0) {
            Calendar c = Calendar.getInstance(Locale.getDefault());

            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DATE);
            int hour = 23;
            int minute = 59;

            if (dueDate.getText().toString().length() > 0) {
                Calendar date = Calendar.getInstance(Locale.getDefault());
                date.setTime(dateFormatter.parse(dueDate.getText().toString(), new ParsePosition(0)));
                year = date.get(Calendar.YEAR);
                month = date.get(Calendar.MONTH);
                day = date.get(Calendar.DATE);
            }

            if (dueTime.getText().toString().length() > 0) {
                Calendar time = Calendar.getInstance();
                time.setTime(timeFormatter.parse(dueTime.getText().toString(), new ParsePosition(0)));
                hour = time.get(Calendar.HOUR_OF_DAY);
                minute = time.get(Calendar.MINUTE);
            }

            c.set(year, month, day, hour, minute, 0);
            values.put("deadline", Long.toString(c.getTimeInMillis()));
        }

        if (estimatedDate != null && estimatedTime != null && estimatedTimeDialog != null && estimatedDateDialog != null) {
            if (estimatedDate.getText().toString().length() > 0 || estimatedTime.getText().toString().length() > 0) {
                Calendar c = Calendar.getInstance(Locale.getDefault());

                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DATE);
                int hour = 23;
                int minute = 59;

                if (estimatedDate.getText().toString().length() > 0) {
                    Calendar date = Calendar.getInstance(Locale.getDefault());
                    date.setTime(dateFormatter.parse(estimatedDate.getText().toString(), new ParsePosition(0)));
                    year = date.get(Calendar.YEAR);
                    month = date.get(Calendar.MONTH);
                    day = date.get(Calendar.DATE);
                }

                if (estimatedTime.getText().toString().length() > 0) {
                    Calendar time = Calendar.getInstance();
                    time.setTime(timeFormatter.parse(estimatedTime.getText().toString(), new ParsePosition(0)));
                    hour = time.get(Calendar.HOUR_OF_DAY);
                    minute = time.get(Calendar.MINUTE);
                }

                c.set(year, month, day, hour, minute, 0);
                values.put("estimated_completion_time", Long.toString(c.getTimeInMillis()));
            }
        }

        User u = (User) newTaskAssignedTo.getSelectedItem();
        if (u.getId() > 0) {
            values.put("responsible_member", Integer.toString(u.getId()));
        }

        Location l = (Location) newTaskLocation.getSelectedItem();
        if (l.getId() > 0) {
            values.put("location", Integer.toString(l.getId()));
        }

        return values;
    }

    private void setAssignedToElement() {
        newTaskAssignedTo = (Spinner) findViewById(R.id.newTaskAssignedTo);
        List<User> users = MainActivity.getGroupMembers();
        users.add(0, new User("Not assigned", 0));

        ArrayAdapter<User> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, users);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newTaskAssignedTo.setAdapter(dataAdapter);
    }

    private void setLocationElement() {
        newTaskLocation = (Spinner) findViewById(R.id.newTaskLocation);
        List<Location> locations = new ArrayList<>(MainActivity.getLocations());
        locations.add(0, new Location(0, "No location", new LatLng(0, 0)));
        ArrayAdapter<Location> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locations);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newTaskLocation.setAdapter(dataAdapter);
    }

    //Create DatePickerDialog functionality
    private void setDueDateDialog() {
        dueDate = (EditText) findViewById(R.id.newTaskDueDate);
        dueDate.setInputType(InputType.TYPE_NULL);
        dueDate.setOnFocusChangeListener(this);

        dateFormatter = new SimpleDateFormat("E d.M.yyyy", Locale.getDefault());
        Calendar newCalendar = Calendar.getInstance();

        dueDateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dueDate.setText(dateFormatter.format(newDate.getTime()));
                dueDate.clearFocus();
                if (dueTime.getText().length() == 0) {
                    dueTimeDialog.show();
                }
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        dueDateDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                dueDate.clearFocus();
            }
        });
    }

    //Create TimePickerDialog functionality
    private void setDueTimeDialog() {
        dueTime = (EditText) findViewById(R.id.newTaskDueTime);
        dueTime.setInputType(InputType.TYPE_NULL);
        dueTime.setOnFocusChangeListener(this);

        timeFormatter = new SimpleDateFormat("h:mm a", Locale.getDefault()); //(DateFormat.is24HourFormat(getApplicationContext()) ? "h:mm" : "h:mm a")
        Calendar newCalendar = Calendar.getInstance(Locale.getDefault());

        dueTimeDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar newTime = Calendar.getInstance();
                newTime.set(1, 1, 1970, hourOfDay, minute);
                dueTime.setText(timeFormatter.format(newTime.getTime()));
                dueTime.clearFocus();
            }
        }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(getApplicationContext()));


        dueTimeDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                dueTime.clearFocus();
            }
        });
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            if (v == dueDate) {
                dueDateDialog.show();
            } else if (v == dueTime) {
                dueTimeDialog.show();
            } else if (v == estimatedDate) {
                estimatedDateDialog.show();
            } else if (v == estimatedTime) {
                estimatedTimeDialog.show();
            }
        }
    }

    public void clearEstimatedDateTime(View view) {
        estimatedDate.setText("");
        estimatedTime.setText("");
        initializeEstimatedDialogs();
    }

    public void clearDateTime(View view) {
        dueDate.setText("");
        dueTime.setText("");

        setDueDateDialog();
        setDueTimeDialog();
    }
}
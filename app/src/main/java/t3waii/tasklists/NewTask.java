package t3waii.tasklists;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
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
    private EditText dueDate, dueTime;
    private DatePickerDialog dueDateDialog;
    private TimePickerDialog dueTimeDialog;

    private SimpleDateFormat dateFormatter, timeFormatter;

    private Spinner newTaskParentTask, newTaskAssignedTo, newTaskLocation;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_new_task);

        setDueDateDialog();
        setDueTimeDialog();

        setParentTaskElement();
        setLocationElement();
        setAssignedToElement();
    }

    public void clickSaveButton(View v) {
        Map<String, String> values = new HashMap<>();

        EditText name = (EditText) findViewById(R.id.newTaskName);
        String title = name.getText().toString();
        values.put("title", title);
        values.put("description", title);

        if(dueDate.getText().toString().length() > 0 || dueTime.getText().toString().length() > 0) {
            Calendar c = Calendar.getInstance(Locale.getDefault());

            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DATE);
            int hour = 23;
            int minute = 59;

            if(dueDate.getText().toString().length() > 0) {
                Calendar date = Calendar.getInstance(Locale.getDefault());
                date.setTime(dateFormatter.parse(dueDate.getText().toString(), new ParsePosition(0)));
                year = date.get(Calendar.YEAR);
                month = date.get(Calendar.MONTH);
                day = date.get(Calendar.DATE);
            }

            if(dueTime.getText().toString().length() > 0) {
                Calendar time = Calendar.getInstance();
                time.setTime(timeFormatter.parse(dueTime.getText().toString(), new ParsePosition(0)));
                hour = time.get(Calendar.HOUR_OF_DAY);
                minute = time.get(Calendar.MINUTE);
            }

            c.set(year, month, day, hour, minute, 0);
            values.put("deadline", Long.toString(c.getTimeInMillis()));
        }

        User u = (User) newTaskAssignedTo.getSelectedItem();
        if(u.getId() > 0) { values.put("responsible_member", Long.toString(u.getId())); }

        Location l = (Location) newTaskLocation.getSelectedItem();
        if(l.getId() > 0) { values.put("location", Long.toString(l.getId())); }

        Task parent = (Task) newTaskParentTask.getSelectedItem();
        Long id = parent.getId();
        if(id != -1) { values.put("parent", Long.toString(id)); }

        Log.d(TAG, values.toString());

        NetworkTasks.postNewTask(values);
        finish();
    }

    private void setAssignedToElement() {
        newTaskAssignedTo = (Spinner) findViewById(R.id.newTaskAssignedTo);
        List<User> users = new ArrayList<>(MainActivity.users);
        users.add(0, new User("Not assigned", Long.valueOf(0)));
        ArrayAdapter<User> dataAdapter = new ArrayAdapter<User>(this, android.R.layout.simple_spinner_item, users);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newTaskAssignedTo.setAdapter(dataAdapter);
    }

    private void setLocationElement() {
        newTaskLocation = (Spinner) findViewById(R.id.newTaskLocation);
        List<Location> locations = new ArrayList<>(MainActivity.locations);
        locations.add(0, new Location(Long.valueOf(0), "No location", new LatLng(0, 0)));
        ArrayAdapter<Location> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locations);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newTaskLocation.setAdapter(dataAdapter);
    }

    private void setParentTaskElement() {
        List<Task> createdTasks = new ArrayList<>();

        Task noParent = new Task(-1, 0);
        noParent.setName("No parent");
        createdTasks.add(noParent);

        for(int i = 0; i < CreatedTasksFragment.tasks.size(); i++) {
            createdTasks.add(CreatedTasksFragment.tasks.get(i));
        }

        newTaskParentTask = (Spinner) findViewById(R.id.newTaskParentTask);
        ArrayAdapter<Task> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, createdTasks);
        newTaskParentTask.setAdapter(dataAdapter);
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
                if(dueTime.getText().length() == 0) {
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
            }
        }
    }

    public void clearDateTime(View view) {
        dueDate.setText("");
        dueTime.setText("");

        setDueDateDialog();
        setDueTimeDialog();
    }
}
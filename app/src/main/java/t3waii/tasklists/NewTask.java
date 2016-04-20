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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

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

    private Button newTaskSave;

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
        //TODO: POST to API and create using that data
        Dictionary<String, String> values = new Hashtable<>();
        // TODO: get values and insert to values

        Task t = new Task(2424, 2);
        t.setName("New task");
        OpenTasksFragment.openTasks.add(t);
        //TODO: implement save new task
        Task parent = (Task) newTaskParentTask.getSelectedItem();
        Long id = parent.getId();
        Log.d(TAG, "parentTaskId:" + (id == -1 ? "no parent" : Long.toString(parent.getId())));
        NetworkTasks.postNewTask(values);
        finish();
    }

    private void setAssignedToElement() {
        newTaskAssignedTo = (Spinner) findViewById(R.id.newTaskAssignedTo);

        ArrayAdapter<User> dataAdapter = new ArrayAdapter<User>(this, android.R.layout.simple_spinner_item, MainActivity.users);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newTaskAssignedTo.setAdapter(dataAdapter);
    }

    private void setLocationElement() {
        newTaskLocation = (Spinner) findViewById(R.id.newTaskLocation);

        List<String> list = new ArrayList<>();
        //TODO: get values from actual source
        list.add("Home");
        list.add("School");
        list.add("Garage");
        list.add("Work");
        list.add("Summer home");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newTaskLocation.setAdapter(dataAdapter);
    }

    private void setParentTaskElement() {
        newTaskParentTask = (Spinner) findViewById(R.id.newTaskParentTask);
        ArrayAdapter<Task> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<Task>());

        Task t = new Task(-1, 0);
        t.setName("No parent");
        dataAdapter.add(t);

        for(int i = 0; i < CreatedTasksFragment.taskListAdapter.getCount(); i++) {
            dataAdapter.add(CreatedTasksFragment.taskListAdapter.getItem(i));
        }
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

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
        Calendar newCalendar = Calendar.getInstance();

        dueTimeDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar newTime = Calendar.getInstance();
                newTime.set(1, 1, 1970, hourOfDay, minute);
                dueTime.setText(timeFormatter.format(newTime.getTime()));
                dueTime.clearFocus();
            }
        }, newCalendar.get(Calendar.HOUR), newCalendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(getApplicationContext()));

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
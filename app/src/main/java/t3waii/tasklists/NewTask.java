package t3waii.tasklists;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import java.util.List;
import java.util.Locale;

/**
 * Created by moetto on 3/15/16.
 */
public class NewTask extends Activity implements View.OnFocusChangeListener {
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
        //TODO: implement save new task
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

        List<String> list = new ArrayList<String>();
        //TODO: get values from actual source
        list.add("Home");
        list.add("School");
        list.add("Garage");
        list.add("Work");
        list.add("Summer home");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        newTaskLocation.setAdapter(dataAdapter);
    }

    private void setParentTaskElement() {
        newTaskParentTask = (Spinner) findViewById(R.id.newTaskParentTask);

        List<String> list = new ArrayList<String>();
        //TODO: get values from actual source
        list.add("Task 1");
        list.add("Task 2");
        list.add("Task 3");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
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
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    //Create TimePickerDialog functionality
    private void setDueTimeDialog() {
        dueTime = (EditText) findViewById(R.id.newTaskDueTime);
        dueTime.setInputType(InputType.TYPE_NULL);
        dueTime.setOnFocusChangeListener(this);

        timeFormatter = new SimpleDateFormat((DateFormat.is24HourFormat(getApplicationContext()) ? "h:mm" : "h:mm a") , Locale.getDefault());
        Calendar newCalendar = Calendar.getInstance();

        dueTimeDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar newTime = Calendar.getInstance();
                newTime.set(1, 1, 1970, hourOfDay, minute);
                dueTime.setText(timeFormatter.format(newTime.getTime()));
            }
        }, newCalendar.get(Calendar.HOUR), newCalendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(getApplicationContext()));
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus) {
            if(v == dueDate) {
                dueDateDialog.show();
            } else if(v == dueTime) {
                dueTimeDialog.show();
            }
        }
    }
}

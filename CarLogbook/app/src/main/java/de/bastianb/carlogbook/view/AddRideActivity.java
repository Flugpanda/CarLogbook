package de.bastianb.carlogbook.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.bastianb.carlogbook.R;
import de.bastianb.carlogbook.control.DatabaseHelper;
import de.bastianb.carlogbook.model.Driver;
import de.bastianb.carlogbook.model.Ride;

public class AddRideActivity extends OrmLiteBaseActivity<DatabaseHelper> {

    // UI References
    // get all the components from the view
    private EditText arrivalInputText;
    private EditText dateInputText;
    private EditText startTimeInputText;
    private EditText endTimeInputText;
    private EditText distanceStartText;
    private EditText distanceEndText;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog fromTimePickerDialog;
    private TimePickerDialog toTimePickerDialog;

    private Spinner driverSpinner;

    // Values
    private HashMap<String, Driver> driverDictionary;
    private String selection = "";

    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat timeFormatter;

    private Double distanceStart = 0.00;
    private Double distanceEnd = 0.00;
    private Date day = null;
    private Date startTime = null;
    private Date endTime = null;
    private String goal = "";
    private Driver driver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ride);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.GERMAN);
        timeFormatter = new SimpleDateFormat("HH:mm", Locale.GERMAN);
        driverDictionary = new HashMap<String, Driver>();

        findViewsById();
        setDateField();
        setTimeFeald();

        initDefaultValues();
    }

    /**
     * find all resources and assign it to the local variables
     */
    private void findViewsById() {
        arrivalInputText = (EditText) findViewById(R.id.zielInputText);
        dateInputText = (EditText) findViewById(R.id.dateInputText);
        startTimeInputText = (EditText) findViewById(R.id.startTimeInputText);
        endTimeInputText = (EditText) findViewById(R.id.endTimeInputText);
        distanceStartText = (EditText) findViewById(R.id.distanceStartText);
        distanceEndText = (EditText) findViewById(R.id.distacneEndText);
        driverSpinner = (Spinner) findViewById(R.id.spinnerDrivers);

        // disable the keyboard for editing
        dateInputText.setKeyListener(null);
        startTimeInputText.setKeyListener(null);
        endTimeInputText.setKeyListener(null);

        driverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // An item was selected. You can retrieve the selected item using
                selection = String.valueOf(parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Set the current time and date to the default values of the view
     */
    private void initDefaultValues() {
        Calendar cal = Calendar.getInstance();

        dateInputText.setText(dateFormatter.format(cal.getTime()));
        startTimeInputText.setText(timeFormatter.format(cal.getTime()));
        endTimeInputText.setText(timeFormatter.format(cal.getTime()));

        // get the dao
        RuntimeExceptionDao<Driver, Integer> driverDao = getHelper().getDriverRuntimeDao();
        // get all drivers from the db
        List<Driver> dbDriverEntries = driverDao.queryForAll();

        if (dbDriverEntries.isEmpty()) // check if there are any entries for drivers at the db
        {
            return;
        }

        // copy all the results
        for (Driver dbEntry : dbDriverEntries) {
            String entry = dbEntry.getLastName() + ", " + dbEntry.getSureName();
            driverDictionary.put(entry, dbEntry);
        }

        // add the elements to the listView
        List<String> keyList = new ArrayList<String>(driverDictionary.keySet());

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, keyList);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        driverSpinner.setAdapter(adapter);
    }

    /**
     * Define the DatePickerDialoag
     */
    private void setDateField() {
        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dateInputText.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Define the TimePickerDoalogs
     */
    private void setTimeFeald() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        fromTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                startTimeInputText.setText(selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, true);//Yes 24 hour time
        fromTimePickerDialog.setTitle("Select Time");

        toTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                endTimeInputText.setText(selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, true);//Yes 24 hour time
        fromTimePickerDialog.setTitle("Select Time");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * handle the click events to set date and time from the view
     *
     * @param view the element that has been clicked
     */
    public void setDateTime(View view) {
        if (view == startTimeInputText) {
            fromTimePickerDialog.show();
        } else if (view == endTimeInputText) {
            toTimePickerDialog.show();
        } else if (view == dateInputText) {
            datePickerDialog.show();
            ;
        }
    }

    /**
     * Convert the inputs and validate them
     *
     * @return boolean      true if successful, false if not
     */
    private boolean checkInputs() {

        boolean success = true;

        // get the day
        try {
            day = dateFormatter.parse(dateInputText.getText().toString());
        } catch (Exception ex) {
            success = false;
            Toast.makeText(this, "Das eingegebene Datum ist ungültig. Es muss in der From DD-MM-JJJJ erfolgen.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // get the distance
        try {
            distanceStart = Double.parseDouble(distanceStartText.getText().toString());
            distanceEnd = Double.parseDouble(distanceEndText.getText().toString());
        } catch (Exception ex) {
            success = false;
            Toast.makeText(this, "Der KM Stand für Anfang oder Ende ist nicht gültig", Toast.LENGTH_SHORT).show();
            return false;
        }

        // get the time
        try {
            startTime = timeFormatter.parse(startTimeInputText.getText().toString());
            endTime = timeFormatter.parse(endTimeInputText.getText().toString());

        } catch (Exception ex) {
            success = false;
            Toast.makeText(this, "Die Eingaben für die Zeit sind nicht gültig. Sie muss im Format hh:mm erfolgen.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (selection.isEmpty()) {
            success = false;
            Toast.makeText(getApplicationContext(), "Es kein Fahrer ausgewählt. Es muss zuerst ein Fahrer erstellt werden.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // get the driver
        try {
            driver = driverDictionary.get(selection);
        } catch (Exception ex) {
            success = false;
            Toast.makeText(this, "Der gewählte Fahrer wurde nicht gefunden", Toast.LENGTH_SHORT).show();
            return false;
        }

        // get the goal
        goal = arrivalInputText.getText().toString();

        // validate the inputs
        if (distanceEnd <= distanceStart) // check the distance
        {
            success = false;
            Toast.makeText(getApplicationContext(), "Der End-KM Stand darf nicht keiner sein als der Start", Toast.LENGTH_SHORT).show();
        } else if (endTime.getTime() <= startTime.getTime()) // check the time
        {
            success = false;
            Toast.makeText(getApplicationContext(), "Die Ankunftszeit darf nicht keiner oder gleich der Startzeit sein. ", Toast.LENGTH_SHORT).show();
        } else if (goal.isEmpty()) // check the text of the goal
        {
            success = false;
            Toast.makeText(getApplicationContext(), "Es wurde kein Zeil eingegeben.", Toast.LENGTH_SHORT).show();
        }

        return success;
    }

    /**
     * Reset all input values back to their defaults
     */
    private void resetValues() {
        distanceStart = 0.00;
        distanceEnd = 0.00;
        startTime = null;
        endTime = null;
        day = null;
        selection = "";
        driverSpinner = null;
    }

    /**
     * Reset all input fields of the view
     */
    private void resetView() {
        initDefaultValues();
        distanceStartText.setText(distanceEndText.getText().toString());
        distanceEndText.setText("");
        arrivalInputText.setText("");
    }

    /**
     * Process the click of the Save-Button
     *
     * @param view the element that has been clicked
     */
    public void saveData(View view) {
        if (checkInputs()) {

            try {
                // create the new object
                Ride newRide = new Ride(day, startTime, endTime, distanceEnd, distanceStart, goal, driver);

                // get the dao
                RuntimeExceptionDao<Ride, Integer> rideDao = getHelper().getRideRuntimeDao();

                // persist the data
                rideDao.createOrUpdate(newRide);

                // set the result code
                setResult(0);

                dispose();

                finish();
            } catch (Exception ex) {
                return;
            }
        }
    }

    /**
     * free resources
     */
    private void dispose() {
        resetValues();
        resetView();
        driverDictionary.clear();
        driverDictionary = null;
        driver = null;
    }
}

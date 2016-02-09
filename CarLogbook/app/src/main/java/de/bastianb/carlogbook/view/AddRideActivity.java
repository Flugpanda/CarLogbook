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
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.text.ParseException;
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

/**
 * This activity is used to add, edit and delete rides
 */
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

    private ArrayAdapter adapter;
    private Spinner driverSpinner;

    // Values
    private HashMap<String, Driver> driverDictionary;
    private String selection = "";

    private Ride editOrRemoveRide = null;

    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat timeFormatter;

    private Double distanceStart = 0.00;
    private Double distanceEnd = 0.00;
    private String day = null;
    private String startTime = null;
    private String endTime = null;
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

        if (getIntent().getExtras() == null) {
            return;
        }

        try {
            int idRide = (int) getIntent().getExtras().get(String.valueOf(R.string.query_identifier_ride));
            int idDriver = (int) getIntent().getExtras().get(String.valueOf(R.string.query_identifier_driver));
            editOrRemoveRide = getRideByID(idRide, idDriver);
            initEditing();
        } catch (Exception ex) {
            editOrRemoveRide = null;
        }
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
                view.setSelected(true);
                selection = String.valueOf(parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * Fill all the fields with the values from the ride
     */
    private void initEditing() {
        if (editOrRemoveRide == null) {
            initDefaultValues();
            Toast.makeText(this, "Die gewählte Fahrt konnte in der Datenbank nicht gefunden werden", Toast.LENGTH_LONG).show();
            return;
        }

        arrivalInputText.setText(editOrRemoveRide.getGoal());
        dateInputText.setText(editOrRemoveRide.getDay());
        startTimeInputText.setText(editOrRemoveRide.getStartTime());
        endTimeInputText.setText(editOrRemoveRide.getEndTime());
        distanceStartText.setText(editOrRemoveRide.getDistanceStart().toString());
        distanceEndText.setText(editOrRemoveRide.getDistanceEnd().toString());

        try {
            driverSpinner.setSelection(adapter.getPosition(editOrRemoveRide.getDriver().getLastName() + ", " + editOrRemoveRide.getDriver().getSureName()));
        } catch (Exception ex) {

        }

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
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, keyList);

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
     * query the ride by the given id
     *
     * @param idRide   the id of the ride
     * @param idDriver the id of the driver
     * @return the ride object from the db with the joint driver
     */
    private Ride getRideByID(int idRide, int idDriver) {
        Ride queriedRide = null;

        // get the daos
        RuntimeExceptionDao<Ride, Integer> rideDao = getHelper().getRideRuntimeDao();
        RuntimeExceptionDao<Driver, Integer> driverDao = getHelper().getDriverRuntimeDao();

        // Because ORMLite is not able to do a right join
        // web have to query the objects secretly
        queriedRide = rideDao.queryForId(idRide);
        try {
            // try to set the driver object
            queriedRide.setDriver(driverDao.queryForId(idDriver));
        } catch (Exception e) {
            // creset the orl foreign key
            queriedRide.getDriver().setId(idDriver);
            e.printStackTrace();
        }

        return queriedRide;
    }

    /**
     * Convert the inputs and validate them
     *
     * @return boolean      true if successful, false if not
     */
    private boolean checkInputs() {

        boolean success = true;
        Date timeStart = null;
        Date timeEnd = null;

        // get the day
        try {
            day = dateInputText.getText().toString();
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
            startTime = startTimeInputText.getText().toString();
            endTime = endTimeInputText.getText().toString();

            timeStart = timeFormatter.parse(startTime);
            timeEnd = timeFormatter.parse(endTime);

        } catch (ParseException pex) {
            success = false;
            Toast.makeText(this, "Die Eingaben für die Zeit sind nicht gültig. Sie muss im Format hh:mm erfolgen.", Toast.LENGTH_SHORT).show();
            return false;
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
        } else if (timeEnd.getTime() <= timeStart.getTime()) // check the time
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
    public void saveRide(View view) {
        if (checkInputs()) {

            try {
                if (editOrRemoveRide == null) // check if the object already exists
                {
                    // create the new object
                    editOrRemoveRide = new Ride(day, startTime, endTime, distanceEnd, distanceStart, goal, driver);
                } else {
                    editOrRemoveRide.setDay(day);
                    editOrRemoveRide.setStartTime(startTime);
                    editOrRemoveRide.setEndTime(endTime);
                    editOrRemoveRide.setDistanceStart(distanceStart);
                    editOrRemoveRide.setDistanceEnd(distanceEnd);
                    editOrRemoveRide.setGoal(goal);
                    editOrRemoveRide.setDriver(driver);
                }


                // get the dao
                RuntimeExceptionDao<Ride, Integer> rideDao = getHelper().getRideRuntimeDao();

                // persist the data
                rideDao.createOrUpdate(editOrRemoveRide);

                // close
                close();
            } catch (Exception ex) {
                return;
            }
        }
    }

    /**
     * Delete the ride from the db
     *
     * @param view the element of the view that has been clicked
     */
    public void deleteRide(View view){
        if (editOrRemoveRide == null){
            Toast.makeText(this, "Der aktuelle Eintrag ist noch nicht gespeicher und kann daher nicht gelöscht werden.", Toast.LENGTH_SHORT).show();
            return;
        }

        // get the dao
        RuntimeExceptionDao<Ride, Integer> rideDao = getHelper().getRideRuntimeDao();

        // delete the entry
        rideDao.delete(editOrRemoveRide);

        // close
        close();
    }

    /**
     * close this activity
     */
    private void close(){
        // set the result code
        setResult(0);

        // reset the ride object
        editOrRemoveRide = null;

        // close the activity
        finish();
    }
}

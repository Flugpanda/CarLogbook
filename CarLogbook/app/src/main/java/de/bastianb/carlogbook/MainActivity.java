package de.bastianb.carlogbook;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // UI References
    // get all the components from the view
    private EditText zielInputText;
    private EditText dateInputText;
    private EditText startTimeInputText;
    private EditText endTimeInputText;
    private EditText distanceStartText;
    private EditText distaceEndText;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog fromTimePickerDialog;
    private TimePickerDialog toTimePickerDialog;

    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat timeFormatter;

    private Double distanceStart = 0.00;
    private Double distanceEnd = 0.00;
    private Date startTime = null;
    private Date endTime = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.GERMANY);
        timeFormatter = new SimpleDateFormat("hh:mm", Locale.GERMANY);

        findViewsById();
        setDateField();
        setTimeFeald();

        initDefaultValues();
    }

    /**
     * find all resources and assign it to the local variables
     */
    private void findViewsById() {
        zielInputText = (EditText) findViewById(R.id.zielInputText);
        dateInputText = (EditText) findViewById(R.id.dateInputText);
        startTimeInputText = (EditText) findViewById(R.id.startTimeInputText);
        endTimeInputText = (EditText) findViewById(R.id.endTimeInputText);
        distanceStartText = (EditText) findViewById(R.id.distanceStartText);
        distaceEndText = (EditText) findViewById(R.id.distacneEndText);

        // disable the keyboard for editing
        dateInputText.setKeyListener(null);
        startTimeInputText.setKeyListener(null);
        endTimeInputText.setKeyListener(null);
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
    private void initDefaultValues(){
        Calendar cal = Calendar.getInstance();

        dateInputText.setText(dateFormatter.format(cal.getTime()));
        startTimeInputText.setText(timeFormatter.format(cal.getTime()));
        endTimeInputText.setText(timeFormatter.format(cal.getTime()));
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

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
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
                startTimeInputText.setText(timeFormatter.format(selectedHour) + ":" + timeFormatter.format(selectedMinute));
            }
        }, hour, minute, true);//Yes 24 hour time
        fromTimePickerDialog.setTitle("Select Time");

        toTimePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                endTimeInputText.setText(timeFormatter.format(selectedHour) + ":" + timeFormatter.format(selectedMinute));
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
     * @param view  the element that has been clicked
     */
    public void setDateTime(View view) {
        if(view == startTimeInputText) {
            fromTimePickerDialog.show();
        } else if(view == endTimeInputText) {
            toTimePickerDialog.show();
        }else if(view == dateInputText){
            datePickerDialog.show();;
        }
    }

    /**
     * Convert the inputs and validate them
     *
     * @return boolean      true if successful, false if not
     */
    private boolean checkInputs(){

        // get the distance
        try {
            distanceStart = Double.parseDouble(distanceStartText.getText().toString());
            distanceEnd = Double.parseDouble(distaceEndText.getText().toString());
        } catch (Exception ex) {
            Toast.makeText(this, "Der KM Stand für Anfang oder Ende ist nicht gültig", Toast.LENGTH_SHORT).show();
            return false;
        }

        // get the time
        try
        {
            startTime = timeFormatter.parse(startTimeInputText.getText().toString());
            endTime = timeFormatter.parse(endTimeInputText.getText().toString());

        }catch (Exception ex){
            Toast.makeText(this, "Die Eingaben für die Zeit sind nicht gültig. Angabe muss in hh:mm erfolgen.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (distanceEnd <= distanceStart ) // check the distance
        {
            Toast.makeText(this, "Der End-KM Stand darf nicht keiner sein als der Start", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(endTime.getTime() <= startTime.getTime()) // check the time
        {
            Toast.makeText(this, "Die Ankunftszeit darf nicht keiner sein als die Startzeit. ", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * Reset all inputvalues back to there defaults
     */
    private void resetValues(){
        distanceStart = 0.00;
        distanceEnd = 0.00;
        startTime = null;
        endTime = null;
    }

    /**
     * Process the click of the Save-Button
     *
     * @param view      the element that has been clicked
     */
    public void saveData(View view){
        checkInputs();
    }
}

package de.bastianb.carlogbook.view;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import de.bastianb.carlogbook.R;
import de.bastianb.carlogbook.control.DatabaseHelper;
import de.bastianb.carlogbook.model.Driver;

/**
 * This is used to add or edit a driver and store it to the db
 */
public class AddDriverActivity extends OrmLiteBaseActivity<DatabaseHelper> {

    private EditText driverForName;
    private EditText driverLastName;
    private EditText driverCarSign;

    private String forname;
    private String lastname;
    private String carsign;

    private Driver editOrRemoveDriver;

    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_driver);

        findViewsById();

        if (getIntent().getExtras() == null){
            return;
        }

        try {
            int id = (int) getIntent().getExtras().get(String.valueOf(R.string.query_identifier_ride));
            editOrRemoveDriver = getDriverByID(id);
            initEditing();
        } catch (Exception ex) {
            editOrRemoveDriver = null;
        }
    }

    private Driver getDriverByID(int id){
        // get our dao
        RuntimeExceptionDao<Driver, Integer> driverDao = getHelper().getDriverRuntimeDao();

        return driverDao.queryForId(id);
    }

    /**
     * get all the resources
     */
    private void findViewsById() {
        driverForName = (EditText) findViewById(R.id.editTextFirstName);
        driverLastName = (EditText) findViewById(R.id.editTextLastName);
        driverCarSign = (EditText) findViewById(R.id.editTextCarsign);
    }

    /**
     * Init all the fields with the data of the received driver
     */
    private void initEditing() {
        driverForName.setText(editOrRemoveDriver.getSureName());
        driverLastName.setText(editOrRemoveDriver.getLastName());
        driverCarSign.setText(editOrRemoveDriver.getCarsign());
    }

    /**
     * Handle the click from the save button and store new driver at the db.
     *
     * @param view the view that has been clicked
     */
    public void saveDriver(View view) {
        if (checkInputs()) {

            // Create the driver object
            Driver driver;

            if (editOrRemoveDriver == null) // check if the driver already exists
            {
                driver = new Driver(forname, lastname, carsign);
            } else {
                driver = editOrRemoveDriver;
                driver.setSureName(forname);
                driver.setLastName(lastname);
                driver.setCarsign(carsign);
            }


            // get our dao
            RuntimeExceptionDao<Driver, Integer> driverDao = getHelper().getDriverRuntimeDao();

            // store the new object at the database
            driverDao.createOrUpdate(driver);

            // set the result code
            setResult(0);

            // free the memory
            driver = null;
            editOrRemoveDriver = null;

            // close this activity
            finish();
        }
    }

    /**
     * Handle the click from the delete button and remove the driver from the db.
     *
     * @param view the view that has been clicked
     */
    public void deleteDriver(View view) {

        if (editOrRemoveDriver == null) // check if the driver already exists
        {
            Toast.makeText(this, "Fahrer ist noch nicht in der Datenbank eingetragen und kann daher nicht gelöscht werden.", Toast.LENGTH_SHORT).show();
        } else {
            // get the dao
            RuntimeExceptionDao<Driver, Integer> driverDao = getHelper().getDriverRuntimeDao();

            try {
                // find the driver
                driverDao.delete(editOrRemoveDriver);

                // set the result code
                setResult(0);

                // free the memory
                editOrRemoveDriver = null;

                // close this activity
                finish();
            } catch (Exception ex){
                Toast.makeText(this, "Der fahrer konnte nicht gelöscht werden", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Check all the inputs
     *
     * @return boolean    true if successful, false if not
     */
    private boolean checkInputs() {

        forname = driverForName.getText().toString();
        lastname = driverLastName.getText().toString();
        carsign = driverCarSign.getText().toString();

        if (forname.isEmpty()) {
            Toast.makeText(this, "Der Vorname darf nicht lehr sein", Toast.LENGTH_SHORT).show();
            resetReceivedInputs();
            return false;
        }

        if (lastname.isEmpty()) {
            Toast.makeText(this, "Der Nachname darf nicht lehr sein", Toast.LENGTH_SHORT).show();
            resetReceivedInputs();
            return false;
        }

        if (carsign.isEmpty()) {
            Toast.makeText(this, "Das Kennzeichen darf nicht lehr sein", Toast.LENGTH_SHORT).show();
            resetReceivedInputs();
            return false;
        }

        return true;
    }

    /**
     * Reset the local variables to theire default values
     */
    private void resetReceivedInputs() {
        forname = "";
        lastname = "";
        carsign = "";
    }
}

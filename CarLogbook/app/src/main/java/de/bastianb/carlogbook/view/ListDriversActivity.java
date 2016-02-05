package de.bastianb.carlogbook.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.bastianb.carlogbook.R;
import de.bastianb.carlogbook.control.DatabaseHelper;
import de.bastianb.carlogbook.model.Driver;

public class ListDriversActivity extends OrmLiteBaseActivity<DatabaseHelper> {

    private ListView listedDrivers;

    private HashMap<String, Driver> driverDictionary;
    private String selection;

    /**
     * Initialize the fiew
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_drivers);

        driverDictionary = new HashMap<String, Driver>();

        findViewById();
        initElements();
    }

    /**
     * Bind all the resources
     */
    private void findViewById() {
        listedDrivers = (ListView) findViewById(R.id.listViewDrivers);
        listedDrivers.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long arg3) {
                view.setSelected(true);
                selection = String.valueOf(parent.getItemAtPosition(position));
            }
        });
    }

    /**
     * list the elements
     */
    private void initElements() {
        // reset Entries
        driverDictionary.clear();

        // get the dao
        RuntimeExceptionDao<Driver, Integer> driverDao = getHelper().getDriverRuntimeDao();
        // get all drivers from the db
        List<Driver> dbDriverEntries = driverDao.queryForAll();

        // copy all the results
        for (Driver dbEntry : dbDriverEntries) {
            String entry = dbEntry.getLastName() + ", " + dbEntry.getSureName();
            driverDictionary.put(entry, dbEntry);
        }

        // add the elements to the listView
        List<String> keyList = new ArrayList<String>(driverDictionary.keySet());
        ListAdapter elementsToList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, keyList);
        listedDrivers.setAdapter(elementsToList);
    }

    // Call Back method  to get the Message form other Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 0
        if (resultCode == 0){
            initElements();
        }
    }

    /**
     * Open the AddDriverActivity for a result
     * @param view
     */
    public void openAddDriverActivity(View view) {
        Intent intent = new Intent(this, AddDriverActivity.class);
        startActivityForResult(intent,0);
    }

    /**
     *
     * @param view
     */
    public void openEditDriverActivity(View view) {

        if (selection.isEmpty()){
            Toast.makeText(this, "Es wurde kein Fahrer ausgewählt.", Toast.LENGTH_SHORT).show();
            return;
        }

        // get the selected item
        Driver toEdit = driverDictionary.get(selection);

        // prepate the intent with the id of the driver as payload
        Intent intent = new Intent(this, AddDriverActivity.class);
        intent.putExtra("DRIVER_ID",toEdit.getId());

        // reset the selection
        selection = "";

        // call the activity
        startActivityForResult(intent, 0);
    }
}

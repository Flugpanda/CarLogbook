package de.bastianb.carlogbook.view;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.bastianb.carlogbook.R;
import de.bastianb.carlogbook.control.DatabaseHelper;
import de.bastianb.carlogbook.control.RidesExpandableListAdapter;
import de.bastianb.carlogbook.model.Ride;

/**
 * This activity is used to list all the rides day by day
 */
public class ListRidesActivity extends OrmLiteBaseActivity<DatabaseHelper> {

    private EditText editTextfromDate;
    private EditText editTextToDate;
    private ExpandableListView listViewRides;
    private RidesExpandableListAdapter ridesAdapter;

    private DatePickerDialog datePickerDialogfromDate;
    private DatePickerDialog datePickerDialogtoDate;

    private Date fromDate = null;
    private Date toDate = null;

    private int selectionGroupPosition = -1;
    private int selectionChildPosition = -1;

    private HashMap<String, List<Ride>> storedRides;

    private SimpleDateFormat simpleDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_rides);

        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.GERMAN);

        // get the resources
        findViewsById();

        // setup the date picker dialogs
        setDateField();

        // initialize the default values
        initDates();
        initView();
        initList();
    }

    /**
     * bind all the resources
     */
    private void findViewsById() {
        editTextfromDate = (EditText) findViewById(R.id.editTextFromDate);
        editTextToDate = (EditText) findViewById(R.id.editTextToDate);
        listViewRides = (ExpandableListView) findViewById(R.id.expandableListViewRides);


        // Listview on child click listener
        listViewRides.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // highlight the selected item
                v.setSelected(true);

                // save the selection
                selectionGroupPosition = groupPosition;
                selectionChildPosition = childPosition;

                return false;
            }
        });

        // Disable manual input
        editTextfromDate.setKeyListener(null);
        editTextToDate.setKeyListener(null);

    }

    /**
     * initialize the date in the edit text fields
     */
    private void initDates() {
        Calendar cal = Calendar.getInstance();
        toDate = fromDate = cal.getTime();
    }

    /**
     * display the values for the date
     */
    private void initView() {
        editTextfromDate.setText(simpleDateFormat.format(fromDate));
        editTextToDate.setText(simpleDateFormat.format(toDate));
    }

    /**
     * initialize the list with the elements from the database
     */
    private void initList() {
        // update all the data to list
        updateDataByDateSelection();
        // create the adapter with the new data
        ridesAdapter = new RidesExpandableListAdapter(this, storedRides);
        // setting list adapter
        listViewRides.setAdapter(ridesAdapter);
    }

    /**
     * update the data selected date
     */
    private void updateDataByDateSelection(){
        storedRides = selectRidesFromDB(fromDate, toDate);
    }

    /**
     * upate the list with the currently selected data
     */
    private void updateList(){
        updateDataByDateSelection();
        ridesAdapter.updateMapAndGroups(storedRides);
        ridesAdapter.notifyDataSetChanged();
    }

    /**
     * handle the click events to set date and time from the view
     *
     * @param view the element that has been clicked
     */
    public void setDateForRideSelection(View view) {
        if (view == editTextfromDate) {
            datePickerDialogfromDate.show();
        } else if (view == editTextToDate) {
            datePickerDialogtoDate.show();
        }
    }

    /**
     * Define the DatePickerDialoag
     */
    private void setDateField() {
        Calendar newCalendar = Calendar.getInstance();

        datePickerDialogfromDate = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                fromDate = newDate.getTime();
                updateList();
                editTextfromDate.setText(simpleDateFormat.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialogtoDate = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                toDate = newDate.getTime();
                updateList();
                editTextToDate.setText(simpleDateFormat.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Select all the rides between the borders from the db
     *
     * @param from lower limit for the selection
     * @param to   upper limit for the selection
     * @return all the in a HashMap with the date as key wit a list of rides
     */
    private HashMap<String, List<Ride>> selectRidesFromDB(Date from, Date to) {

        HashMap<String, List<Ride>> groupedRides = new HashMap<>();

        // get the dao
        RuntimeExceptionDao<Ride, Integer> rideDao = getHelper().getRideRuntimeDao();

        // build the query
        QueryBuilder<Ride, Integer> rideIntegerQueryBuilder = rideDao.queryBuilder();

        List<Ride> queriedRides = null;

        try {
            queriedRides = rideIntegerQueryBuilder.orderBy("endTime", true).where().between("day", simpleDateFormat.format(from.getTime()), simpleDateFormat.format(to.getTime())).query();
            Toast.makeText(this, "found the entries", Toast.LENGTH_SHORT).show();
        } catch (SQLException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }

        if (queriedRides != null) // check if there were any entries at the db
        {
            // group the elements by their day
            for (Ride ride : queriedRides) {
                if (!groupedRides.keySet().contains(ride.getDay())) // check if the group already exists
                {
                    groupedRides.put(ride.getDay(), new ArrayList<Ride>());
                }
                // add the entry to the group
                groupedRides.get(ride.getDay()).add(ride);
            }
        }

        return groupedRides;
    }

    /**
     * edit a selected ride
     * @param view
     */
    public void editTheSelectedRide(View view) {
        Ride selectedRide = (Ride) ridesAdapter.getChild(selectionGroupPosition, selectionChildPosition);

        if (selectedRide != null){
            Intent intent = new Intent(this, AddRideActivity.class);
            intent.putExtra(String.valueOf(R.string.query_identifier_ride), selectedRide.getId());
            intent.putExtra(String.valueOf(R.string.query_identifier_driver), selectedRide.getDriver().getId());

            // reset the selection
            selectionGroupPosition = -1;
            selectionChildPosition = -1;

            // start the acvtivity
            startActivityForResult(intent, 0);
        }
    }

    // Call Back method  to get the Message form other Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 0
        if (resultCode == 0){
            updateList();
        }
    }
}

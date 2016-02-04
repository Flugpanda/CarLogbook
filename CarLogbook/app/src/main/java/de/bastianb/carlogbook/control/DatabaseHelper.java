package de.bastianb.carlogbook.control;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.Date;

import de.bastianb.carlogbook.R;
import de.bastianb.carlogbook.model.Driver;
import de.bastianb.carlogbook.model.Ride;

/**
 * This class handel's the database connection and provides the doas for the objects to persist them
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "carRides.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 1;

    // the DAO object we use to access the SimpleData table
    private Dao<Driver, Integer> driverDao = null;
    private Dao<Ride, Integer> rideDao = null;

    private RuntimeExceptionDao<Driver, Integer> driverRuntimeDao = null;
    private RuntimeExceptionDao<Ride, Integer> rideRuntimeDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, Driver.class);
            TableUtils.createTable(connectionSource, Ride.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }

//        testInsertion();
    }

    /**
     * Insert some Data to test the creation of the tables
     */
    private void testInsertion(){
        // here we try inserting data in the on-create as a test
        RuntimeExceptionDao<Driver, Integer> daoDriver = getDriverRuntimeDao();
        RuntimeExceptionDao<Ride, Integer> daoRide = getRideRuntimeDao();

        // messure time to create the objects and persist them
        long millis = System.currentTimeMillis();

        // create some entries in the onCreate
        Driver driver = new Driver("X-XX-000", "Frist", "Entry");
        Ride ride = new Ride(new Date(), new Date(), 0.00, 1.00, "TestEntry");
        try {
            driverDao.create(driver);
            rideDao.create(ride);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Log.i(DatabaseHelper.class.getName(), "created new entries in onCreate: " + millis);
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
//        try {
//            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
//            TableUtils.dropTable(connectionSource, SimpleData.class, true);
//            // after we drop the old databases, we create the new ones
//            onCreate(db, connectionSource);
//        } catch (SQLException e) {
//            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
//            throw new RuntimeException(e);
//        }
    }

    /**
     * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
     * value.
     */
    public Dao<Driver, Integer> getDriverDao() throws SQLException {
        if (driverDao == null) {
            driverDao = getDao(Driver.class);
        }
        return driverDao;
    }

    /**
     * Returns the Database Access Object (DAO) for our SimpleData class. It will create it or just give the cached
     * value.
     */
    public Dao<Ride, Integer> getRideDao() throws SQLException {
        if (rideDao == null) {
            rideDao = getDao(Ride.class);
        }
        return rideDao;
    }

    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     */
    public RuntimeExceptionDao<Driver, Integer> getDriverRuntimeDao() {
        if (driverRuntimeDao == null) {
            driverRuntimeDao = getRuntimeExceptionDao(Driver.class);
        }
        return driverRuntimeDao;
    }

    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our SimpleData class. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     */
    public RuntimeExceptionDao<Ride, Integer> getRideRuntimeDao() {
        if (rideRuntimeDao == null) {
            rideRuntimeDao = getRuntimeExceptionDao(Ride.class);
        }
        return rideRuntimeDao;
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        driverDao = null;
        rideDao = null;

        driverRuntimeDao = null;
        rideRuntimeDao = null;
    }
}

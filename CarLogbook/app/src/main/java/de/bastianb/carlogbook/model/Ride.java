package de.bastianb.carlogbook.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * This class represents the model for all the rides in the database
 * mapped with ormlite to sqlite
 */
@DatabaseTable(tableName = "Rides")
public class Ride {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private Double distanceStart = 0.00;
    @DatabaseField
    private Double distanceEnd = 0.00;
    @DatabaseField
    private String startTime = null;
    @DatabaseField
    private String endTime = null;
    @DatabaseField
    private String day = null;
    @DatabaseField
    private String goal = "";
    @DatabaseField(foreign = true)
    private Driver driver;

    public Ride(String day, String startTime, String endTime, Double distanceEnd, Double distanceStart, String goal, Driver driver) {
        this.day = day;
        this.startTime = startTime;
        this.distanceEnd = distanceEnd;
        this.distanceStart = distanceStart;
        this.endTime = endTime;
        this.goal = goal;
        this.driver = driver;
    }

    public Ride() {
    }

    public Double getDistanceEnd() {
        return distanceEnd;
    }

    public void setDistanceEnd(Double distanceEnd) {
        this.distanceEnd = distanceEnd;
    }

    public Double getDistanceStart() {
        return distanceStart;
    }

    public void setDistanceStart(Double distanceStart) {
        this.distanceStart = distanceStart;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}

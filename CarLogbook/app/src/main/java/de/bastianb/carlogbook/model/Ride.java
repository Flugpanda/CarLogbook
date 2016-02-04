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
    private Long id;
    @DatabaseField
    private Double distanceStart = 0.00;
    @DatabaseField
    private Double distanceEnd = 0.00;
    @DatabaseField
    private Date startTime = null;
    @DatabaseField
    private Date endTime = null;
    @DatabaseField
    private String goal = "";

    public Ride(Date startTime, Double distanceEnd, Double distanceStart, Date endTime, String goal) {
        this.startTime = startTime;
        this.distanceEnd = distanceEnd;
        this.distanceStart = distanceStart;
        this.endTime = endTime;
        this.goal = goal;
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

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
}

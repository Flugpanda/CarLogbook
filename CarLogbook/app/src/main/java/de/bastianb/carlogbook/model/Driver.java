package de.bastianb.carlogbook.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * This class represents the model of the drivers
 */
@DatabaseTable(tableName = "Driver")
public class Driver {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String sureName = "";
    @DatabaseField
    private String lastName = "";
    @DatabaseField
    private String carsign = "";

    public Driver (){

    }

    public Driver(String sureName, String lastName, String carsign) {
        this.carsign = carsign;
        this.lastName = lastName;
        this.sureName = sureName;
    }

    public String getCarsign() {
        return carsign;
    }

    public void setCarsign(String carsign) {
        this.carsign = carsign;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSureName() {
        return sureName;
    }

    public void setSureName(String sureName) {
        this.sureName = sureName;
    }
}

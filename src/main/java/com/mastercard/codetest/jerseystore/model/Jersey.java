package com.mastercard.codetest.jerseystore.model;

import com.google.gson.annotations.Expose;

import java.sql.Timestamp;

public class Jersey {
    @Expose
    private String id;
    @Expose
    private JerseySize size;
    @Expose
    private String brand;
    @Expose
    private String club;
    @Expose
    private String year;
    @Expose
    private JerseyType type;
    @Expose
    private JerseyCut cut;
    @Expose
    private JerseyMaterial material;
    @Expose
    private int amount;

    private Timestamp createDate;

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public JerseyMaterial getMaterial() {
        return material;
    }

    public void setMaterial(JerseyMaterial material) {
        this.material = material;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JerseySize getSize() {
        return size;
    }

    public void setSize(JerseySize size) {
        this.size = size;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getClub() {
        return club;
    }

    public void setClub(String club) {
        this.club = club;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public JerseyType getType() {
        return type;
    }

    public void setType(JerseyType type) {
        this.type = type;
    }

    public JerseyCut getCut() {
        return cut;
    }

    public void setCut(JerseyCut cut) {
        this.cut = cut;
    }

    public Jersey() {
    }
}

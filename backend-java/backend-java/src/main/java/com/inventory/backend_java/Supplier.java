package com.inventory.backend_java;

public class Supplier {

    private String supplierName;
    private String contactPerson;
    private String phone;
    private String email;
    private String address;

    public Supplier(String supplierName, String contactPerson, String phone, String email, String address) {
        this.supplierName = supplierName;
        this.contactPerson = contactPerson;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }
}
package com.inventory.backend_java;

// DEPRECATED: Use com.inventory.backend_java.model.Supplier instead
@Deprecated(since = "1.0", forRemoval = true)
public class Supplier extends com.inventory.backend_java.model.Supplier {
    public Supplier(String supplierName, String contactPerson, String phone, String email, String address) {
        super(supplierName, contactPerson, phone, email, address);
    }
}
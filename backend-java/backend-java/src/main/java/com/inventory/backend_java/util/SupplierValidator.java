package com.inventory.backend_java.util;

import com.inventory.backend_java.exception.ValidationException;
import com.inventory.backend_java.model.Supplier;

public final class SupplierValidator {

    private SupplierValidator() {
    }

    public static void validate(Supplier supplier) {
        if (supplier == null) {
            throw new ValidationException("Supplier data is required");
        }
        if (isBlank(supplier.getSupplierName())) {
            throw new ValidationException("Supplier name is required");
        }
        if (isBlank(supplier.getContactPerson())) {
            throw new ValidationException("Contact person is required");
        }
        if (isBlank(supplier.getPhone())) {
            throw new ValidationException("Phone is required");
        }
        if (isBlank(supplier.getEmail())) {
            throw new ValidationException("Email is required");
        }
        if (!supplier.getEmail().contains("@")) {
            throw new ValidationException("Valid email is required");
        }
        if (isBlank(supplier.getAddress())) {
            throw new ValidationException("Address is required");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

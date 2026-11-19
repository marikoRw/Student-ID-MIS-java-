package com.auca.department;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.teacher.Teacher;

public class Department implements Identifiable, Registrable {
    private String departmentId;
    private String departmentName;
    private String departmentCode;
    private String officeLocation;
    private String contactEmail;
    private Teacher headTeacher; // Optional on initial creation

    // Constructor chaining example (satisfies Section 5)
    public Department(String departmentId, String departmentName, String departmentCode,
                      String officeLocation, String contactEmail) {
        this(departmentId, departmentName, departmentCode, officeLocation, contactEmail, null);
    }

    public Department(String departmentId, String departmentName, String departmentCode,
                      String officeLocation, String contactEmail, Teacher headTeacher) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.departmentCode = departmentCode;
        this.officeLocation = officeLocation;
        this.contactEmail = contactEmail;
        this.headTeacher = headTeacher;
    }

    @Override
    public String getId() { return departmentId; }

    @Override
    public String getDisplayName() { return "[" + departmentCode + "] " + departmentName; }

    @Override
    public boolean isActive() { return true; }

    @Override
    public void validate() throws ValidationException {
        if (departmentId == null || departmentId.trim().length() < MIN_ID_LENGTH) {
            throw new ValidationException("Department ID must be at least 4 characters.");
        }
        if (departmentName == null || departmentName.trim().length() < MIN_NAME_LENGTH) {
            throw new ValidationException("Department name must be 2-60 characters.");
        }
        if (departmentCode == null || departmentCode.trim().isEmpty()) {
            throw new ValidationException("Department code is required.");
        }
        if (contactEmail == null || !contactEmail.contains("@")) {
            throw new ValidationException("Valid contact email is required.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println(getRegistrationSummary());
    }

    @Override
    public String getRegistrationSummary() {
        String head = (headTeacher != null) ? headTeacher.getDisplayName() : "Unassigned";
        return "Department: " + getDisplayName() + " | Office: " + officeLocation + " | Head: " + head;
    }

    @Override
    public String toString() { return getDisplayName(); }

    // Getters & Setters
    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public String getDepartmentCode() { return departmentCode; }
    public void setDepartmentCode(String departmentCode) { this.departmentCode = departmentCode; }
    public String getOfficeLocation() { return officeLocation; }
    public void setOfficeLocation(String officeLocation) { this.officeLocation = officeLocation; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public Teacher getHeadTeacher() { return headTeacher; }
    public void setHeadTeacher(Teacher headTeacher) { this.headTeacher = headTeacher; }
}
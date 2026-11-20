package com.auca.administrator;

import com.auca.abstracts.Person;
import com.auca.exceptions.ValidationException;
import java.time.LocalDate;

public class Administrator extends Person {
    private String staffNumber;
    private String jobTitle;
    private String assignedOffice;
    private LocalDate employmentDate;
    private int accessLevel; // 1 (lowest) to 5 (highest)
    private boolean active;

    public Administrator(String personId, String firstName, String lastName, String email,
                         String phoneNumber, LocalDate dateOfBirth,
                         String staffNumber, String jobTitle, String assignedOffice,
                         LocalDate employmentDate, int accessLevel, boolean active) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth);
        this.staffNumber = staffNumber;
        this.jobTitle = jobTitle;
        this.assignedOffice = assignedOffice;
        this.employmentDate = employmentDate;
        this.accessLevel = accessLevel;
        this.active = active;
    }

    @Override
    public String getRoleDescription() { return "Administrative Staff - " + jobTitle; }

    @Override
    public boolean isActive() { return active; }

    @Override
    public void validate() throws ValidationException {
        super.validate();
        if (staffNumber == null || staffNumber.trim().length() < MIN_ID_LENGTH
                || staffNumber.trim().length() > MAX_ID_LENGTH) {
            throw new ValidationException("Staff number must be 4-30 characters.");
        }
        if (jobTitle == null || jobTitle.trim().isEmpty()) {
            throw new ValidationException("Job title is required.");
        }
        if (assignedOffice == null || assignedOffice.trim().isEmpty()) {
            throw new ValidationException("Assigned office is required.");
        }
        if (employmentDate == null || employmentDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Employment date cannot be in the future.");
        }
        if (accessLevel < 1 || accessLevel > 5) {
            throw new ValidationException("Access level must be between 1 and 5.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println(getRegistrationSummary());
    }

    @Override
    public String getRegistrationSummary() {
        return "[" + staffNumber + "] " + getDisplayName() + " | " + jobTitle +
               " | Office: " + assignedOffice + " | Access: " + accessLevel +
               " | Active: " + active;
    }

    @Override
    public String toString() { return getRegistrationSummary(); }

    // Getters & Setters
    public String getStaffNumber() { return staffNumber; }
    public void setStaffNumber(String staffNumber) { this.staffNumber = staffNumber; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getAssignedOffice() { return assignedOffice; }
    public void setAssignedOffice(String assignedOffice) { this.assignedOffice = assignedOffice; }
    public LocalDate getEmploymentDate() { return employmentDate; }
    public void setEmploymentDate(LocalDate employmentDate) { this.employmentDate = employmentDate; }
    public int getAccessLevel() { return accessLevel; }
    public void setAccessLevel(int accessLevel) { this.accessLevel = accessLevel; }
    public void setActive(boolean active) { this.active = active; }
}
package com.auca.abstracts;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import java.time.LocalDate;

public abstract class Person implements Identifiable, Registrable {
    private String personId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;

    public Person(String personId, String firstName, String lastName,
                  String email, String phoneNumber, LocalDate dateOfBirth) {
        this.personId = personId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
    }

    public abstract String getRoleDescription();

    @Override
    public String getId() { return personId; }

    @Override
    public String getDisplayName() { return firstName + " " + lastName; }

    @Override
    public void validate() throws ValidationException {
        if (personId == null || personId.trim().length() < MIN_ID_LENGTH) {
            throw new ValidationException("Person ID must be at least " + MIN_ID_LENGTH + " characters.");
        }
        if (firstName == null || firstName.trim().length() < MIN_NAME_LENGTH || firstName.trim().length() > MAX_NAME_LENGTH) {
            throw new ValidationException("First name must be 2-60 characters.");
        }
        if (lastName == null || lastName.trim().length() < MIN_NAME_LENGTH || lastName.trim().length() > MAX_NAME_LENGTH) {
            throw new ValidationException("Last name must be 2-60 characters.");
        }
        if (email == null || !email.contains("@") || !email.contains(".") || email.length() > MAX_EMAIL_LENGTH) {
            throw new ValidationException("Invalid email format.");
        }
        if (dateOfBirth == null || !dateOfBirth.isBefore(LocalDate.now())) {
            throw new ValidationException("Date of birth must be in the past.");
        }
    }

    // Standard Getters & Setters
    public String getPersonId() { return personId; }
    public void setPersonId(String personId) { this.personId = personId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
}
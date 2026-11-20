package com.auca.teacher;

import com.auca.abstracts.AcademicMember;
import com.auca.department.Department;
import com.auca.exceptions.ValidationException;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Teacher extends AcademicMember {
    private String employeeNumber;
    private String specialization;
    private LocalDate hireDate;
    private BigDecimal salary;
    private String officeNumber;
    private String employmentType; // FULL_TIME, PART_TIME or CONTRACT

    public Teacher(String personId, String firstName, String lastName, String email,
                   String phoneNumber, LocalDate dateOfBirth, String memberNumber,
                   Department department, LocalDate joinDate, String academicStatus,
                   String institutionalEmail, String advisorName,
                   String employeeNumber, String specialization, LocalDate hireDate,
                   BigDecimal salary, String officeNumber, String employmentType) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth,
              memberNumber, department, joinDate, academicStatus, institutionalEmail, advisorName);
        this.employeeNumber = employeeNumber;
        this.specialization = specialization;
        this.hireDate = hireDate;
        this.salary = salary;
        this.officeNumber = officeNumber;
        this.employmentType = employmentType;
    }

    @Override
    public String getRoleDescription() { return "Teacher / Lecturer"; }

    @Override
    public String getAcademicCategory() { return "Teacher - " + employmentType; }

    @Override
    public void validate() throws ValidationException {
        super.validate();
        if (employeeNumber == null || employeeNumber.trim().length() < MIN_ID_LENGTH
                || employeeNumber.trim().length() > MAX_ID_LENGTH) {
            throw new ValidationException("Employee number must be 4-30 characters.");
        }
        if (specialization == null || specialization.trim().isEmpty()) {
            throw new ValidationException("Specialization is required.");
        }
        if (hireDate == null || hireDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Hire date cannot be in the future.");
        }
        if (salary == null || salary.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Salary must be greater than zero.");
        }
        if (officeNumber == null || officeNumber.trim().isEmpty()) {
            throw new ValidationException("Office number is required.");
        }
        if (employmentType == null ||
            !(employmentType.equals("FULL_TIME") || employmentType.equals("PART_TIME")
              || employmentType.equals("CONTRACT"))) {
            throw new ValidationException("Employment type must be FULL_TIME, PART_TIME or CONTRACT.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println(getRegistrationSummary());
    }

    @Override
    public String getRegistrationSummary() {
        return "[" + employeeNumber + "] " + getDisplayName() + " | " + specialization +
               " | Dept: " + getDepartment().getDepartmentName() + " | " + employmentType;
    }

    @Override
    public String toString() { return getRegistrationSummary(); }

    // Getters & Setters
    public String getEmployeeNumber() { return employeeNumber; }
    public void setEmployeeNumber(String employeeNumber) { this.employeeNumber = employeeNumber; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }
    public String getOfficeNumber() { return officeNumber; }
    public void setOfficeNumber(String officeNumber) { this.officeNumber = officeNumber; }
    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }
}
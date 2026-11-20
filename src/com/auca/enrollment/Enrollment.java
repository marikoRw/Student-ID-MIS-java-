package com.auca.enrollment;

import com.auca.course.Course;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.student.Student;
import java.time.LocalDate;

public class Enrollment implements Identifiable, Registrable {
    private String enrollmentId;
    private Student student;
    private Course course;
    private LocalDate enrollmentDate;
    private String semester;          // FALL, SPRING or SUMMER
    private String enrollmentStatus;  // ACTIVE, COMPLETED or DROPPED

    public Enrollment(String enrollmentId, Student student, Course course,
                      LocalDate enrollmentDate, String semester, String enrollmentStatus) {
        this.enrollmentId = enrollmentId;
        this.student = student;
        this.course = course;
        this.enrollmentDate = enrollmentDate;
        this.semester = semester;
        this.enrollmentStatus = enrollmentStatus;
    }

    @Override
    public String getId() { return enrollmentId; }

    @Override
    public String getDisplayName() {
        return student.getDisplayName() + " in " + course.getCourseCode() + " (" + semester + ")";
    }

    @Override
    public boolean isActive() { return "ACTIVE".equals(enrollmentStatus); }

    @Override
    public void validate() throws ValidationException {
        if (enrollmentId == null || enrollmentId.trim().length() < MIN_ID_LENGTH
                || enrollmentId.trim().length() > MAX_ID_LENGTH) {
            throw new ValidationException("Enrollment ID must be 4-30 characters.");
        }
        if (student == null) {
            throw new ValidationException("Student is required.");
        }
        if (course == null) {
            throw new ValidationException("Course is required.");
        }
        if (enrollmentDate == null || enrollmentDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Enrollment date cannot be in the future.");
        }
        if (semester == null || !(semester.equals("FALL") || semester.equals("SPRING")
                || semester.equals("SUMMER"))) {
            throw new ValidationException("Semester must be FALL, SPRING or SUMMER.");
        }
        if (enrollmentStatus == null || !(enrollmentStatus.equals("ACTIVE")
                || enrollmentStatus.equals("COMPLETED") || enrollmentStatus.equals("DROPPED"))) {
            throw new ValidationException("Status must be ACTIVE, COMPLETED or DROPPED.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println(getRegistrationSummary());
    }

    @Override
    public String getRegistrationSummary() {
        return "[" + enrollmentId + "] " + student.getDisplayName() + " -> " +
               course.getDisplayName() + " | " + semester + " | " + enrollmentStatus +
               " | " + enrollmentDate;
    }

    @Override
    public String toString() { return getRegistrationSummary(); }

    public String getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(String enrollmentId) { this.enrollmentId = enrollmentId; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }
    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    public String getEnrollmentStatus() { return enrollmentStatus; }
    public void setEnrollmentStatus(String enrollmentStatus) { this.enrollmentStatus = enrollmentStatus; }
}
package com.auca.assessment;

import com.auca.enrollment.Enrollment;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import java.time.LocalDate;

public class Assessment implements Identifiable, Registrable {
    private String assessmentId;
    private Enrollment enrollment;
    private String assessmentType; // QUIZ, ASSIGNMENT, MIDTERM or FINAL
    private double maximumScore;
    private double obtainedScore;
    private LocalDate assessmentDate;

    public Assessment(String assessmentId, Enrollment enrollment, String assessmentType,
                      double maximumScore, double obtainedScore, LocalDate assessmentDate) {
        this.assessmentId = assessmentId;
        this.enrollment = enrollment;
        this.assessmentType = assessmentType;
        this.maximumScore = maximumScore;
        this.obtainedScore = obtainedScore;
        this.assessmentDate = assessmentDate;
    }

    @Override
    public String getId() { return assessmentId; }

    @Override
    public String getDisplayName() { return assessmentType + " - " + enrollment.getDisplayName(); }

    @Override
    public boolean isActive() { return enrollment != null && enrollment.isActive(); }

    @Override
    public void validate() throws ValidationException {
        if (assessmentId == null || assessmentId.trim().length() < MIN_ID_LENGTH
                || assessmentId.trim().length() > MAX_ID_LENGTH) {
            throw new ValidationException("Assessment ID must be 4-30 characters.");
        }
        if (enrollment == null) {
            throw new ValidationException("Enrollment is required.");
        }
        if (!enrollment.isActive()) {
            throw new ValidationException("Only an ACTIVE enrollment can be assessed.");
        }
        if (assessmentType == null || !(assessmentType.equals("QUIZ")
                || assessmentType.equals("ASSIGNMENT") || assessmentType.equals("MIDTERM")
                || assessmentType.equals("FINAL"))) {
            throw new ValidationException("Type must be QUIZ, ASSIGNMENT, MIDTERM or FINAL.");
        }
        if (maximumScore <= 0) {
            throw new ValidationException("Maximum score must be greater than zero.");
        }
        if (obtainedScore < 0 || obtainedScore > maximumScore) {
            throw new ValidationException("Obtained score must be between 0 and " + maximumScore + ".");
        }
        if (assessmentDate == null || assessmentDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Assessment date cannot be in the future.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println(getRegistrationSummary());
    }

    @Override
    public String getRegistrationSummary() {
        return "[" + assessmentId + "] " + assessmentType + " | " +
               enrollment.getStudent().getDisplayName() + " | " +
               enrollment.getCourse().getCourseCode() + " | Score: " +
               obtainedScore + "/" + maximumScore + " | " + assessmentDate;
    }

    @Override
    public String toString() { return getRegistrationSummary(); }

    public String getAssessmentId() { return assessmentId; }
    public void setAssessmentId(String assessmentId) { this.assessmentId = assessmentId; }
    public Enrollment getEnrollment() { return enrollment; }
    public void setEnrollment(Enrollment enrollment) { this.enrollment = enrollment; }
    public String getAssessmentType() { return assessmentType; }
    public void setAssessmentType(String assessmentType) { this.assessmentType = assessmentType; }
    public double getMaximumScore() { return maximumScore; }
    public void setMaximumScore(double maximumScore) { this.maximumScore = maximumScore; }
    public double getObtainedScore() { return obtainedScore; }
    public void setObtainedScore(double obtainedScore) { this.obtainedScore = obtainedScore; }
    public LocalDate getAssessmentDate() { return assessmentDate; }
    public void setAssessmentDate(LocalDate assessmentDate) { this.assessmentDate = assessmentDate; }
}
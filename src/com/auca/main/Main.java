package com.auca.main;

import com.auca.abstracts.AcademicMember;
import com.auca.abstracts.Person;
import com.auca.administrator.Administrator;
import com.auca.assessment.Assessment;
import com.auca.bookloan.BookLoan;
import com.auca.classroom.Classroom;
import com.auca.course.Course;
import com.auca.department.Department;
import com.auca.enrollment.Enrollment;
import com.auca.exceptions.BookUnavailableException;
import com.auca.exceptions.DuplicateRecordException;
import com.auca.exceptions.PrerequisiteMissingException;
import com.auca.exceptions.RecordNotFoundException;
import com.auca.exceptions.ValidationException;
import com.auca.feepayment.FeePayment;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.librarybook.LibraryBook;
import com.auca.student.Student;
import com.auca.teacher.Teacher;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    // ONE shared Scanner for the whole program (Section 6)
    private static final Scanner scanner = new Scanner(System.in);

    // Special return values used by the selector
    private static final int CANCELLED = -1;
    private static final int REGISTER_NEW = -2;

    // In-memory storage: one typed list per entity
    private static final List<Department> departments = new ArrayList<>();
    private static final List<Student> students = new ArrayList<>();
    private static final List<Teacher> teachers = new ArrayList<>();
    private static final List<Administrator> administrators = new ArrayList<>();
    private static final List<Course> courses = new ArrayList<>();
    private static final List<Classroom> classrooms = new ArrayList<>();
    private static final List<Enrollment> enrollments = new ArrayList<>();
    private static final List<Assessment> assessments = new ArrayList<>();
    private static final List<FeePayment> payments = new ArrayList<>();
    private static final List<LibraryBook> books = new ArrayList<>();
    private static final List<BookLoan> loans = new ArrayList<>();

    // =====================================================
    // MAIN MENU
    // =====================================================
    public static void main(String[] args) {
        int choice = -1;

        do {
            printMainMenu();

            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("! Please enter a menu number from 0 to 19.");
                choice = -1;
                continue;
            }

            // Safety net: a failure inside one workflow must not close the program
            try {
                switch (choice) {
                    case 1 -> registerDepartmentForm();
                    case 2 -> registerStudentForm();
                    case 3 -> registerTeacherForm();
                    case 5 -> registerCourseForm();
                    case 7 -> registerEnrollmentForm();
                    case 13 -> viewAllRecords();
                    case 19 -> demonstratePolymorphism();
                    case 4 -> registerAdministratorForm();
                    case 6 -> registerClassroomForm();
                    case 8 -> registerAssessmentForm();
                    case 9 -> registerFeePaymentForm();
                    case 10 -> registerLibraryBookForm();
                    case 11 -> borrowBookForm();
                    case 12 -> returnBookForm();
                    case 16 -> showStudentAcademicSummary();
                    case 17 -> showPaymentHistory();
                    case 18 -> showAvailableBooks();
                    case 14 -> searchRecordById();
                    case 15 -> updateRecordForm();
                    case 0 -> System.out.println("Exiting AUCA_ID. Goodbye!");
                    default -> System.out.println("! Invalid option. Choose 0-19.");
                }
            } catch (RuntimeException e) {
                System.out.println("! Something went wrong: " + e.getMessage());
                System.out.println("  Returning to the main menu.");
            }
        } while (choice != 0);

        scanner.close();
    }

    private static void printMainMenu() {
        System.out.println("\n============== AUCA_ID SCHOOL MANAGEMENT ==============");
        System.out.println(" 1. Register Department       11. Borrow Library Book");
        System.out.println(" 2. Register Student          12. Return Library Book");
        System.out.println(" 3. Register Teacher          13. View All Records");
        System.out.println(" 4. Register Administrator    14. Search Record by ID");
        System.out.println(" 5. Register Course           15. Update Existing Record");
        System.out.println(" 6. Register Classroom        16. Student Academic Summary");
        System.out.println(" 7. Enroll Student            17. Student Payment History");
        System.out.println(" 8. Record Assessment         18. View Available Books");
        System.out.println(" 9. Record Fee Payment        19. Demonstrate Polymorphism");
        System.out.println("10. Register Library Book      0. Exit");
        System.out.print("Select an option: ");
    }

    // =====================================================
    // SMART FORM: DEPARTMENT
    // =====================================================
    private static Department registerDepartmentForm() {
        System.out.println("\n+------------------------------------------------------------+");
        System.out.println("| AUCA_ID / SCHOOL MANAGEMENT                                |");
        System.out.println("| DEPARTMENT REGISTRATION                                    |");
        System.out.println("| Type CANCEL at any prompt to discard the unsaved record.   |");
        System.out.println("+------------------------------------------------------------+");

        String id = promptUnique("01 Department ID [String | unique | 4-30 chars] : ", 4, 30, "ID");
        if (id == null) return null;

        String name = promptText("02 Department name [String | 2-60 chars] : ", 2, 60);
        if (name == null) return null;

        String code = promptUnique("03 Department code [String | unique | 2-10 chars] : ", 2, 10, "DEPTCODE");
        if (code == null) return null;

        String office = promptText("04 Office location [String | 1-60 chars] : ", 1, 60);
        if (office == null) return null;

        String email = promptEmail("05 Contact email [String | email] : ");
        if (email == null) return null;

        System.out.println("06 Head teacher [Teacher] : not assigned yet (assign later via Update)");

        Department dept = new Department(id, name, code, office, email);

        if (!confirmSave("REVIEW DEPARTMENT", dept)) {
            System.out.println("Cancelled. Nothing was saved.");
            return null;
        }

        try {
            checkUnique("ID", id);            // re-check right before saving
            checkUnique("DEPTCODE", code);
            dept.validate();
            departments.add(dept);
            System.out.println("+ Department " + dept.getId() + " registered successfully.");
            return dept;
        } catch (ValidationException | DuplicateRecordException e) {
            System.out.println("! Could not save: " + e.getMessage());
            return null;
        }
    }

    // =====================================================
    // SMART FORM: STUDENT
    // =====================================================
    private static Student registerStudentForm() {
        System.out.println("\n+------------------------------------------------------------+");
        System.out.println("| AUCA_ID / SCHOOL MANAGEMENT                                |");
        System.out.println("| STUDENT REGISTRATION                                       |");
        System.out.println("| [1] Personal  [2] Academic  [3] Student  [4] Review        |");
        System.out.println("| Type CANCEL at any prompt to discard the unsaved student.  |");
        System.out.println("+------------------------------------------------------------+");

        System.out.println("\n[STEP 1/4] PERSONAL DETAILS");
        String personId = promptUnique("01 Person ID [String | unique | 4-30 chars] : ", 4, 30, "ID");
        if (personId == null) return null;
        String firstName = promptText("02 First name [String | 2-60 chars] : ", 2, 60);
        if (firstName == null) return null;
        String lastName = promptText("03 Last name [String | 2-60 chars] : ", 2, 60);
        if (lastName == null) return null;
        String email = promptUniqueEmail("04 Email [String | unique | email] : ", "EMAIL");
        if (email == null) return null;
        String phone = promptPhone("05 Phone [String | 10-15 digits] : ");
        if (phone == null) return null;
        LocalDate dob = promptDate("06 Date of birth [LocalDate | YYYY-MM-DD] : ", true);
        if (dob == null) return null;

        System.out.println("\n[STEP 2/4] ACADEMIC DETAILS");
        String memberNumber = promptUnique("07 Member number [String | unique | 4-30] : ", 4, 30, "MEMBER");
        if (memberNumber == null) return null;

        Department dept = selectOrCreateDepartment();   // field 08
        if (dept == null) {
            System.out.println("Student registration cancelled.");
            return null;
        }
        System.out.println("08 Department : " + dept.getDisplayName());

        LocalDate joinDate = promptDate("09 Join date [LocalDate | YYYY-MM-DD] : ", false);
        if (joinDate == null) return null;
        String status = promptChoice("10 Academic status:", "ACTIVE", "INACTIVE");
        if (status == null) return null;
        String instEmail = promptUniqueEmail("11 Institutional email [String | unique | email] : ", "INSTEMAIL");
        if (instEmail == null) return null;
        String advisor = promptText("12 Advisor name [String | 2-120 chars] : ", 2, 120);
        if (advisor == null) return null;

        System.out.println("\n[STEP 3/4] STUDENT DETAILS");
        String studentNumber = promptUnique("13 Student number [String | unique | 4-30] : ", 4, 30, "STUDENTNO");
        if (studentNumber == null) return null;
        String program = promptText("14 Program name [String | 2-120 chars] : ", 2, 120);
        if (program == null) return null;
        LocalDate admissionDate = promptDate("15 Admission date [LocalDate | YYYY-MM-DD] : ", false);
        if (admissionDate == null) return null;
        int studyYear = promptIntRange("16 Study year [int | 1-6] : ", 1, 6);
        if (studyYear == CANCELLED) return null;
        String guardianPhone = promptPhone("17 Guardian phone [String | 10-15 digits] : ");
        if (guardianPhone == null) return null;
        Boolean scholarship = promptYesNo("18 Scholarship:");
        if (scholarship == null) return null;

        Student student = new Student(personId, firstName, lastName, email, phone, dob,
                memberNumber, dept, joinDate, status, instEmail, advisor,
                studentNumber, program, admissionDate, studyYear, guardianPhone, scholarship);

        System.out.println("\n[STEP 4/4] REVIEW & CONFIRM");
        if (!confirmSave("REVIEW STUDENT", student)) {
            System.out.println("Cancelled. Nothing was saved.");
            return null;
        }

        try {
            checkUnique("ID", personId);
            checkUnique("STUDENTNO", studentNumber);
            student.validate();
            students.add(student);
            System.out.println("+ Student " + student.getStudentNumber() + " registered successfully.");
            return student;
        } catch (ValidationException | DuplicateRecordException e) {
            System.out.println("! Could not save: " + e.getMessage());
            return null;
        }
    }

    // =====================================================
    // SMART FORM: TEACHER
    // =====================================================
    private static Teacher registerTeacherForm() {
        System.out.println("\n+------------------------------------------------------------+");
        System.out.println("| AUCA_ID / SCHOOL MANAGEMENT                                |");
        System.out.println("| TEACHER REGISTRATION                                       |");
        System.out.println("| Type CANCEL at any prompt to discard the unsaved teacher.  |");
        System.out.println("+------------------------------------------------------------+");

        System.out.println("\n[STEP 1/4] PERSONAL DETAILS");
        String personId = promptUnique("01 Person ID [String | unique | 4-30 chars] : ", 4, 30, "ID");
        if (personId == null) return null;
        String firstName = promptText("02 First name [String | 2-60 chars] : ", 2, 60);
        if (firstName == null) return null;
        String lastName = promptText("03 Last name [String | 2-60 chars] : ", 2, 60);
        if (lastName == null) return null;
        String email = promptUniqueEmail("04 Email [String | unique | email] : ", "EMAIL");
        if (email == null) return null;
        String phone = promptPhone("05 Phone [String | 10-15 digits] : ");
        if (phone == null) return null;
        LocalDate dob = promptDate("06 Date of birth [LocalDate | YYYY-MM-DD] : ", true);
        if (dob == null) return null;

        System.out.println("\n[STEP 2/4] ACADEMIC DETAILS");
        String memberNumber = promptUnique("07 Member number [String | unique | 4-30] : ", 4, 30, "MEMBER");
        if (memberNumber == null) return null;

        Department dept = selectOrCreateDepartment();   // field 08
        if (dept == null) {
            System.out.println("Teacher registration cancelled.");
            return null;
        }
        System.out.println("08 Department : " + dept.getDisplayName());

        LocalDate joinDate = promptDate("09 Join date [LocalDate | YYYY-MM-DD] : ", false);
        if (joinDate == null) return null;
        String status = promptChoice("10 Academic status:", "ACTIVE", "INACTIVE");
        if (status == null) return null;
        String instEmail = promptUniqueEmail("11 Institutional email [String | unique | email] : ", "INSTEMAIL");
        if (instEmail == null) return null;
        String advisor = promptText("12 Advisor name [String | 2-120 chars] : ", 2, 120);
        if (advisor == null) return null;

        System.out.println("\n[STEP 3/4] TEACHER DETAILS");
        String employeeNumber = promptUnique("13 Employee number [String | unique | 4-30] : ", 4, 30, "EMPLOYEENO");
        if (employeeNumber == null) return null;
        String specialization = promptText("14 Specialization [String | 2-120 chars] : ", 2, 120);
        if (specialization == null) return null;
        LocalDate hireDate = promptDate("15 Hire date [LocalDate | YYYY-MM-DD] : ", false);
        if (hireDate == null) return null;
        BigDecimal salary = promptMoney("16 Salary [BigDecimal | greater than 0] : ");
        if (salary == null) return null;
        String officeNumber = promptText("17 Office number [String | 1-30 chars] : ", 1, 30);
        if (officeNumber == null) return null;
        String employmentType = promptChoice("18 Employment type:", "FULL_TIME", "PART_TIME", "CONTRACT");
        if (employmentType == null) return null;

        Teacher teacher = new Teacher(personId, firstName, lastName, email, phone, dob,
                memberNumber, dept, joinDate, status, instEmail, advisor,
                employeeNumber, specialization, hireDate, salary, officeNumber, employmentType);

        System.out.println("\n[STEP 4/4] REVIEW & CONFIRM");
        if (!confirmSave("REVIEW TEACHER", teacher)) {
            System.out.println("Cancelled. Nothing was saved.");
            return null;
        }

        try {
            checkUnique("ID", personId);
            checkUnique("EMPLOYEENO", employeeNumber);
            teacher.validate();
            teachers.add(teacher);
            System.out.println("+ Teacher " + teacher.getEmployeeNumber() + " registered successfully.");
            return teacher;
        } catch (ValidationException | DuplicateRecordException e) {
            System.out.println("! Could not save: " + e.getMessage());
            return null;
        }
    }

    // =====================================================
    // SMART FORM: COURSE (needs Department AND Teacher)
    // =====================================================
    private static Course registerCourseForm() {
        System.out.println("\n+------------------------------------------------------------+");
        System.out.println("| AUCA_ID / SCHOOL MANAGEMENT                                |");
        System.out.println("| COURSE REGISTRATION (needs a Department and a Teacher)     |");
        System.out.println("| Type CANCEL at any prompt to discard the unsaved course.   |");
        System.out.println("+------------------------------------------------------------+");

        // Ask for the related objects FIRST, so missing prerequisites are found early
        System.out.println("\n[STEP 1/3] SELECT RELATED RECORDS");
        Department dept = selectOrCreateDepartment();
        if (dept == null) {
            System.out.println("Course registration cancelled.");
            return null;
        }
        Teacher teacher = selectOrCreateTeacher();
        if (teacher == null) {
            System.out.println("Course registration cancelled.");
            return null;
        }

        System.out.println("\n[STEP 2/3] COURSE DETAILS");
        String id = promptUnique("01 Course ID [String | unique | 4-30 chars] : ", 4, 30, "ID");
        if (id == null) return null;
        String code = promptUnique("02 Course code [String | unique | 3-15 chars] : ", 3, 15, "COURSECODE");
        if (code == null) return null;
        String title = promptText("03 Course title [String | 2-100 chars] : ", 2, 100);
        if (title == null) return null;
        int credits = promptIntRange("04 Credit hours [int | 1-6] : ", 1, 6);
        if (credits == CANCELLED) return null;
        System.out.println("05 Department : " + dept.getDisplayName());
        System.out.println("06 Assigned teacher : " + teacher.getDisplayName());

        Course course = new Course(id, code, title, credits, dept, teacher);

        System.out.println("\n[STEP 3/3] REVIEW & CONFIRM");
        if (!confirmSave("REVIEW COURSE", course)) {
            System.out.println("Cancelled. Nothing was saved.");
            return null;
        }

        try {
            checkUnique("ID", id);
            checkUnique("COURSECODE", code);
            course.validate();
            courses.add(course);
            System.out.println("+ Course " + course.getCourseCode() + " registered successfully.");
            return course;
        } catch (ValidationException | DuplicateRecordException e) {
            System.out.println("! Could not save: " + e.getMessage());
            return null;
        }
    }

    // =====================================================
    // SMART FORM: ENROLLMENT (needs Student AND Course)
    // =====================================================
    private static Enrollment registerEnrollmentForm() {
        System.out.println("\n+------------------------------------------------------------+");
        System.out.println("| AUCA_ID / SCHOOL MANAGEMENT                                |");
        System.out.println("| ENROLL STUDENT (needs a Student and a Course)              |");
        System.out.println("| Type CANCEL at any prompt to discard the unsaved record.   |");
        System.out.println("+------------------------------------------------------------+");

        System.out.println("\n[STEP 1/3] SELECT RELATED RECORDS");
        Student student = selectOrCreateStudent();
        if (student == null) {
            System.out.println("Enrollment cancelled.");
            return null;
        }
        Course course = selectOrCreateCourse();
        if (course == null) {
            System.out.println("Enrollment cancelled.");
            return null;
        }

        System.out.println("\n[STEP 2/3] ENROLLMENT DETAILS");
        String id = promptUnique("01 Enrollment ID [String | unique | 4-30 chars] : ", 4, 30, "ID");
        if (id == null) return null;
        System.out.println("02 Student : " + student.getDisplayName());
        System.out.println("03 Course : " + course.getDisplayName());
        LocalDate date = promptDate("04 Enrollment date [LocalDate | YYYY-MM-DD] : ", false);
        if (date == null) return null;
        String semester = promptChoice("05 Semester:", "FALL", "SPRING", "SUMMER");
        if (semester == null) return null;
        String status = promptChoice("06 Enrollment status:", "ACTIVE", "COMPLETED", "DROPPED");
        if (status == null) return null;

        Enrollment enrollment = new Enrollment(id, student, course, date, semester, status);

        System.out.println("\n[STEP 3/3] REVIEW & CONFIRM");
        if (!confirmSave("REVIEW ENROLLMENT", enrollment)) {
            System.out.println("Cancelled. Nothing was saved.");
            return null;
        }

        try {
            checkUnique("ID", id);
            if (enrollment.isActive()) {
                checkEnrollmentNotDuplicate(student, course, semester, null);
            }
            enrollment.validate();
            enrollments.add(enrollment);
            System.out.println("+ Enrollment " + enrollment.getId() + " registered successfully.");
            return enrollment;
        } catch (ValidationException | DuplicateRecordException e) {
            System.out.println("! Could not save: " + e.getMessage());
            return null;
        }
    }

    // =====================================================
    // FORM BANNER (used by the forms added in this phase)
    // =====================================================
    private static void printBanner(String title) {
        System.out.println("\n+------------------------------------------------------------+");
        System.out.println("| AUCA_ID / SCHOOL MANAGEMENT");
        System.out.println("| " + title);
        System.out.println("| Type CANCEL at any prompt to discard the unsaved record.");
        System.out.println("+------------------------------------------------------------+");
    }

    // =====================================================
    // SMART FORM: ADMINISTRATOR (no prerequisites)
    // =====================================================
    private static Administrator registerAdministratorForm() {
        printBanner("ADMINISTRATOR REGISTRATION");

        System.out.println("\n[STEP 1/3] PERSONAL DETAILS");
        String personId = promptUnique("01 Person ID [String | unique | 4-30 chars] : ", 4, 30, "ID");
        if (personId == null) return null;
        String firstName = promptText("02 First name [String | 2-60 chars] : ", 2, 60);
        if (firstName == null) return null;
        String lastName = promptText("03 Last name [String | 2-60 chars] : ", 2, 60);
        if (lastName == null) return null;
        String email = promptUniqueEmail("04 Email [String | unique | email] : ", "EMAIL");
        if (email == null) return null;
        String phone = promptPhone("05 Phone [String | 10-15 digits] : ");
        if (phone == null) return null;
        LocalDate dob = promptDate("06 Date of birth [LocalDate | YYYY-MM-DD] : ", true);
        if (dob == null) return null;

        System.out.println("\n[STEP 2/3] ADMINISTRATOR DETAILS");
        String staffNumber = promptUnique("07 Staff number [String | unique | 4-30] : ", 4, 30, "STAFFNO");
        if (staffNumber == null) return null;
        String jobTitle = promptText("08 Job title [String | 2-60 chars] : ", 2, 60);
        if (jobTitle == null) return null;
        String office = promptText("09 Assigned office [String | 1-60 chars] : ", 1, 60);
        if (office == null) return null;
        LocalDate employmentDate = promptDate("10 Employment date [LocalDate | YYYY-MM-DD] : ", false);
        if (employmentDate == null) return null;
        int accessLevel = promptIntRange("11 Access level [int | 1-5] : ", 1, 5);
        if (accessLevel == CANCELLED) return null;
        Boolean active = promptYesNo("12 Active:");
        if (active == null) return null;

        Administrator admin = new Administrator(personId, firstName, lastName, email, phone, dob,
                staffNumber, jobTitle, office, employmentDate, accessLevel, active);

        System.out.println("\n[STEP 3/3] REVIEW & CONFIRM");
        if (!confirmSave("REVIEW ADMINISTRATOR", admin)) {
            System.out.println("Cancelled. Nothing was saved.");
            return null;
        }

        try {
            checkUnique("ID", personId);
            checkUnique("STAFFNO", staffNumber);
            admin.validate();
            administrators.add(admin);
            System.out.println("+ Administrator " + admin.getStaffNumber() + " registered successfully.");
            return admin;
        } catch (ValidationException | DuplicateRecordException e) {
            System.out.println("! Could not save: " + e.getMessage());
            return null;
        }
    }

    // =====================================================
    // SMART FORM: CLASSROOM (no prerequisites)
    // =====================================================
    private static Classroom registerClassroomForm() {
        printBanner("CLASSROOM REGISTRATION");

        System.out.println("\n[STEP 1/2] CLASSROOM DETAILS");
        String id = promptUnique("01 Classroom ID [String | unique | 4-30 chars] : ", 4, 30, "ID");
        if (id == null) return null;
        String building = promptText("02 Building name [String | 1-60 chars] : ", 1, 60);
        if (building == null) return null;
        String room = promptText("03 Room number [String | 1-20 chars] : ", 1, 20);
        if (room == null) return null;
        int capacity = promptIntRange("04 Capacity [int | 1-1000] : ", 1, 1000);
        if (capacity == CANCELLED) return null;
        int floor = promptIntRange("05 Floor number [int | 0-100] : ", 0, 100);
        if (floor == CANCELLED) return null;
        Boolean available = promptYesNo("06 Available:");
        if (available == null) return null;

        Classroom classroom = new Classroom(id, building, room, capacity, floor, available);

        System.out.println("\n[STEP 2/2] REVIEW & CONFIRM");
        if (!confirmSave("REVIEW CLASSROOM", classroom)) {
            System.out.println("Cancelled. Nothing was saved.");
            return null;
        }

        try {
            checkUnique("ID", id);
            classroom.validate();
            classrooms.add(classroom);
            System.out.println("+ Classroom " + classroom.getId() + " registered successfully.");
            return classroom;
        } catch (ValidationException | DuplicateRecordException e) {
            System.out.println("! Could not save: " + e.getMessage());
            return null;
        }
    }

    // =====================================================
    // SMART FORM: LIBRARY BOOK (no prerequisites)
    // =====================================================
    private static LibraryBook registerLibraryBookForm() {
        printBanner("LIBRARY BOOK REGISTRATION");

        System.out.println("\n[STEP 1/2] BOOK DETAILS");
        String id = promptUnique("01 Book ID [String | unique | 4-30 chars] : ", 4, 30, "ID");
        if (id == null) return null;
        String isbn = promptUnique("02 ISBN [String | unique | 10-17 chars] : ", 10, 17, "ISBN");
        if (isbn == null) return null;
        String title = promptText("03 Title [String | 1-150 chars] : ", 1, 150);
        if (title == null) return null;
        String author = promptText("04 Author [String | 2-100 chars] : ", 2, 100);
        if (author == null) return null;
        int total = promptIntRange("05 Total copies [int | 0-1000] : ", 0, 1000);
        if (total == CANCELLED) return null;
        // availableCopies can never be more than totalCopies
        int available = promptIntRange("06 Available copies [int | 0-" + total + "] : ", 0, total);
        if (available == CANCELLED) return null;

        LibraryBook book = new LibraryBook(id, isbn, title, author, total, available);

        System.out.println("\n[STEP 2/2] REVIEW & CONFIRM");
        if (!confirmSave("REVIEW LIBRARY BOOK", book)) {
            System.out.println("Cancelled. Nothing was saved.");
            return null;
        }

        try {
            checkUnique("ID", id);
            checkUnique("ISBN", isbn);
            book.validate();
            books.add(book);
            System.out.println("+ Book " + book.getId() + " registered successfully.");
            return book;
        } catch (ValidationException | DuplicateRecordException e) {
            System.out.println("! Could not save: " + e.getMessage());
            return null;
        }
    }

    // =====================================================
    // SMART FORM: ASSESSMENT (needs an ACTIVE Enrollment)
    // =====================================================
    private static Assessment registerAssessmentForm() {
        printBanner("RECORD ASSESSMENT (needs an ACTIVE enrollment)");

        System.out.println("\n[STEP 1/3] SELECT RELATED RECORD");
        Enrollment enrollment = selectOrCreateActiveEnrollment();
        if (enrollment == null) {
            System.out.println("Assessment cancelled.");
            return null;
        }

        System.out.println("\n[STEP 2/3] ASSESSMENT DETAILS");
        String id = promptUnique("01 Assessment ID [String | unique | 4-30 chars] : ", 4, 30, "ID");
        if (id == null) return null;
        System.out.println("02 Enrollment : " + enrollment.getDisplayName());
        String type = promptChoice("03 Assessment type:", "QUIZ", "ASSIGNMENT", "MIDTERM", "FINAL");
        if (type == null) return null;
        Double maximum = promptDouble("04 Maximum score [double | greater than 0] : ", 0, 1000, false);
        if (maximum == null) return null;
        Double obtained = promptDouble("05 Obtained score [double | 0-" + maximum + "] : ", 0, maximum, true);
        if (obtained == null) return null;
        LocalDate date = promptDate("06 Assessment date [LocalDate | YYYY-MM-DD] : ", false);
        if (date == null) return null;

        Assessment assessment = new Assessment(id, enrollment, type, maximum, obtained, date);

        System.out.println("\n[STEP 3/3] REVIEW & CONFIRM");
        if (!confirmSave("REVIEW ASSESSMENT", assessment)) {
            System.out.println("Cancelled. Nothing was saved.");
            return null;
        }

        try {
            checkUnique("ID", id);
            assessment.validate();   // also rejects a non-ACTIVE enrollment
            assessments.add(assessment);
            System.out.println("+ Assessment " + assessment.getId() + " recorded successfully.");
            return assessment;
        } catch (ValidationException | DuplicateRecordException e) {
            System.out.println("! Could not save: " + e.getMessage());
            return null;
        }
    }

    // =====================================================
    // SMART FORM: FEE PAYMENT (needs a Student)
    // =====================================================
    private static FeePayment registerFeePaymentForm() {
        printBanner("RECORD FEE PAYMENT (needs a Student)");

        System.out.println("\n[STEP 1/3] SELECT RELATED RECORD");
        Student student = selectOrCreateStudent();
        if (student == null) {
            System.out.println("Payment cancelled.");
            return null;
        }

        System.out.println("\n[STEP 2/3] PAYMENT DETAILS");
        String id = promptUnique("01 Payment ID [String | unique | 4-30 chars] : ", 4, 30, "ID");
        if (id == null) return null;
        System.out.println("02 Student : " + student.getDisplayName());
        BigDecimal amount = promptMoney("03 Amount [BigDecimal | greater than 0] : ");
        if (amount == null) return null;
        LocalDate date = promptDate("04 Payment date [LocalDate | YYYY-MM-DD] : ", false);
        if (date == null) return null;
        String method = promptChoice("05 Payment method:", "CASH", "MOBILE_MONEY", "BANK_TRANSFER", "CARD");
        if (method == null) return null;
        String receipt = promptUnique("06 Receipt number [String | unique | 3-30 chars] : ", 3, 30, "RECEIPT");
        if (receipt == null) return null;

        FeePayment payment = new FeePayment(id, student, amount, date, method, receipt);

        System.out.println("\n[STEP 3/3] REVIEW & CONFIRM");
        if (!confirmSave("REVIEW FEE PAYMENT", payment)) {
            System.out.println("Cancelled. Nothing was saved.");
            return null;
        }

        try {
            checkUnique("ID", id);
            checkUnique("RECEIPT", receipt);
            payment.validate();
            payments.add(payment);
            System.out.println("+ Payment " + payment.getId() + " recorded. Receipt: " + receipt);
            return payment;
        } catch (ValidationException | DuplicateRecordException e) {
            System.out.println("! Could not save: " + e.getMessage());
            return null;
        }
    }

    // =====================================================
    // BORROW A BOOK (creates a BookLoan, stock goes DOWN exactly once)
    // =====================================================
    private static BookLoan borrowBookForm() {
        printBanner("BORROW LIBRARY BOOK (needs a Student and a Library Book)");

        System.out.println("\n[STEP 1/3] SELECT RELATED RECORDS");
        Student student = selectOrCreateStudent();
        if (student == null) {
            System.out.println("Borrowing cancelled.");
            return null;
        }

        // Keep asking until the chosen book has a copy available (or the user cancels)
        LibraryBook book = null;
        while (book == null) {
            LibraryBook chosen = selectOrCreateBook();
            if (chosen == null) {
                System.out.println("Borrowing cancelled.");
                return null;
            }
            try {
                checkBookAvailable(chosen);
                book = chosen;
            } catch (BookUnavailableException e) {
                System.out.println("! " + e.getMessage() + " Please choose another book.");
            }
        }

        System.out.println("\n[STEP 2/3] LOAN DETAILS");
        String id = promptUnique("01 Loan ID [String | unique | 4-30 chars] : ", 4, 30, "ID");
        if (id == null) return null;
        System.out.println("02 Student : " + student.getDisplayName());
        System.out.println("03 Book : " + book.getDisplayName()
                + " (available: " + book.getAvailableCopies() + ")");
        LocalDate borrowed = promptDate("04 Borrowed date [LocalDate | YYYY-MM-DD] : ", false);
        if (borrowed == null) return null;
        LocalDate due = promptDueDate("05 Due date [LocalDate | YYYY-MM-DD | after borrowed date] : ", borrowed);
        if (due == null) return null;
        System.out.println("06 Returned date : (empty until the book is returned)");

        BookLoan loan = new BookLoan(id, student, book, borrowed, due, null);

        System.out.println("\n[STEP 3/3] REVIEW & CONFIRM");
        if (!confirmSave("REVIEW BOOK LOAN", loan)) {
            System.out.println("Cancelled. Nothing was saved and stock is unchanged.");
            return null;
        }

        try {
            checkUnique("ID", id);
            checkBookAvailable(book);   // check again right before changing stock
            loan.validate();
            loans.add(loan);
            book.setAvailableCopies(book.getAvailableCopies() - 1);   // exactly once
            System.out.println("+ Loan " + loan.getId() + " created. Copies left for \""
                    + book.getTitle() + "\": " + book.getAvailableCopies());
            return loan;
        } catch (ValidationException | DuplicateRecordException | BookUnavailableException e) {
            System.out.println("! Could not save: " + e.getMessage());
            return null;
        }
    }

    // =====================================================
    // RETURN A BOOK (updates the EXISTING loan, stock goes UP exactly once)
    // =====================================================
    private static void returnBookForm() {
        printBanner("RETURN LIBRARY BOOK");

        List<BookLoan> openLoans = activeLoans();
        if (openLoans.isEmpty()) {
            System.out.println("No active loan exists. Borrow a book first (menu option 11).");
            return;
        }

        int pick = pickExisting("Active Loan", openLoans);
        if (pick == CANCELLED) {
            System.out.println("Return cancelled. Nothing changed.");
            return;
        }
        BookLoan loan = openLoans.get(pick);

        LocalDate returnDate = promptReturnDate("Return date [LocalDate | YYYY-MM-DD] : ",
                loan.getBorrowedDate());
        if (returnDate == null) {
            System.out.println("Return cancelled. Nothing changed.");
            return;
        }

        if (!confirmAction("Return \"" + loan.getBook().getTitle() + "\" for "
                + loan.getStudent().getDisplayName() + " on " + returnDate + "?")) {
            System.out.println("Return cancelled. Nothing changed.");
            return;
        }

        LibraryBook book = loan.getBook();
        loan.setReturnedDate(returnDate);
        try {
            loan.validate();
            if (book.getAvailableCopies() >= book.getTotalCopies()) {
                throw new ValidationException("All copies are already in the library; stock cannot go up.");
            }
            book.setAvailableCopies(book.getAvailableCopies() + 1);   // exactly once
            System.out.println("+ Book returned. Copies now available for \"" + book.getTitle()
                    + "\": " + book.getAvailableCopies());
        } catch (ValidationException e) {
            loan.setReturnedDate(null);   // undo, so nothing is half-changed
            System.out.println("! Could not return the book: " + e.getMessage());
        }
    }

    private static void checkBookAvailable(LibraryBook book) throws BookUnavailableException {
        if (book.getAvailableCopies() <= 0) {
            throw new BookUnavailableException("No copy of \"" + book.getTitle() + "\" is available right now.");
        }
    }

    private static List<BookLoan> activeLoans() {
        List<BookLoan> open = new ArrayList<>();
        for (BookLoan loan : loans) {
            if (loan.isActive()) open.add(loan);
        }
        return open;
    }

    private static List<Enrollment> activeEnrollments() {
        List<Enrollment> active = new ArrayList<>();
        for (Enrollment e : enrollments) {
            if (e.isActive()) active.add(e);
        }
        return active;
    }

    // =====================================================
    // REPORTS: menu options 16, 17, 18
    // =====================================================
    private static void showStudentAcademicSummary() {
        System.out.println("\n--- STUDENT ACADEMIC SUMMARY ---");
        Student student = selectExistingStudent();
        if (student == null) return;

        System.out.println("\n" + student.getRegistrationSummary());
        System.out.println("Advisor: " + student.getAdvisorName()
                + " | Scholarship: " + (student.isScholarshipStatus() ? "YES" : "NO"));
        System.out.println("\nEnrollments:");

        int count = 0;
        for (Enrollment e : enrollments) {
            if (e.getStudent() != student) continue;
            count++;
            System.out.println("  " + e.getRegistrationSummary());

            double obtained = 0;
            double maximum = 0;
            int assessmentCount = 0;
            for (Assessment a : assessments) {
                if (a.getEnrollment() == e) {
                    assessmentCount++;
                    obtained += a.getObtainedScore();
                    maximum += a.getMaximumScore();
                    System.out.println("      - " + a.getAssessmentType() + ": "
                            + a.getObtainedScore() + "/" + a.getMaximumScore());
                }
            }
            if (assessmentCount == 0) {
                System.out.println("      (no assessments yet)");
            } else {
                System.out.println("      Total: " + obtained + "/" + maximum + " = "
                        + String.format("%.1f", obtained / maximum * 100) + "%");
            }
        }
        if (count == 0) {
            System.out.println("  (this student has no enrollments yet)");
        }
    }

    private static void showPaymentHistory() {
        System.out.println("\n--- STUDENT PAYMENT HISTORY ---");
        Student student = selectExistingStudent();
        if (student == null) return;

        System.out.println("\nPayments for " + student.getDisplayName() + ":");
        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        for (FeePayment p : payments) {
            if (p.getStudent() == student) {
                p.printDetails();
                total = total.add(p.getAmount());   // BigDecimal needs add(), not +
                count++;
            }
        }
        if (count == 0) {
            System.out.println("  (no payments recorded for this student)");
        } else {
            System.out.println("Total paid: " + total + " across " + count + " payment(s).");
        }
    }

    private static void showAvailableBooks() {
        System.out.println("\n--- AVAILABLE BOOKS ---");
        int count = 0;
        for (LibraryBook b : books) {
            if (b.getAvailableCopies() > 0) {
                b.printDetails();
                count++;
            }
        }
        if (count == 0) {
            System.out.println("  (no books are available right now)");
        }
    }

    // =====================================================
    // NESTED PREREQUISITE SELECTORS (Section 6 and 16.4)
    // Each one: show a numbered list, or register a new record,
    // then return the REAL object (never the menu number).
    // =====================================================
    private static Department selectOrCreateDepartment() {
        while (true) {
            int pick = pickIndex("Department", departments);
            if (pick == CANCELLED) return null;
            if (pick == REGISTER_NEW) {
                Department created = registerDepartmentForm();
                if (created != null) {
                    System.out.println("Returning to the previous form with your entries intact...");
                    return created;
                }
            } else {
                return departments.get(pick);
            }
        }
    }

    private static Teacher selectOrCreateTeacher() {
        while (true) {
            int pick = pickIndex("Teacher", teachers);
            if (pick == CANCELLED) return null;
            if (pick == REGISTER_NEW) {
                Teacher created = registerTeacherForm();   // this may open the Department form too
                if (created != null) {
                    System.out.println("Returning to the previous form with your entries intact...");
                    return created;
                }
            } else {
                return teachers.get(pick);
            }
        }
    }

    private static Student selectOrCreateStudent() {
        while (true) {
            int pick = pickIndex("Student", students);
            if (pick == CANCELLED) return null;
            if (pick == REGISTER_NEW) {
                Student created = registerStudentForm();
                if (created != null) {
                    System.out.println("Returning to the previous form with your entries intact...");
                    return created;
                }
            } else {
                return students.get(pick);
            }
        }
    }

    private static Course selectOrCreateCourse() {
        while (true) {
            int pick = pickIndex("Course", courses);
            if (pick == CANCELLED) return null;
            if (pick == REGISTER_NEW) {
                Course created = registerCourseForm();
                if (created != null) {
                    System.out.println("Returning to the previous form with your entries intact...");
                    return created;
                }
            } else {
                return courses.get(pick);
            }
        }
    }

    private static LibraryBook selectOrCreateBook() {
        while (true) {
            int pick = pickIndex("Library Book", books);
            if (pick == CANCELLED) return null;
            if (pick == REGISTER_NEW) {
                LibraryBook created = registerLibraryBookForm();
                if (created != null) {
                    System.out.println("Returning to the previous form with your entries intact...");
                    return created;
                }
            } else {
                return books.get(pick);
            }
        }
    }

    // Only ACTIVE enrollments can be assessed (Section 6)
    private static Enrollment selectOrCreateActiveEnrollment() {
        while (true) {
            List<Enrollment> active = activeEnrollments();
            if (active.isEmpty()) {
                System.out.println("No ACTIVE enrollment exists. Enroll a student before recording an assessment.");
            }
            int pick = pickIndex("Enrollment", active);
            if (pick == CANCELLED) return null;
            if (pick == REGISTER_NEW) {
                Enrollment created = registerEnrollmentForm();
                if (created != null) {
                    if (created.isActive()) {
                        System.out.println("Returning to the previous form with your entries intact...");
                        return created;
                    }
                    System.out.println("! That enrollment is not ACTIVE, so it cannot be assessed.");
                }
            } else {
                return active.get(pick);
            }
        }
    }

    // A list with NO "register new" option (used for reports and for returning books)
    private static int pickExisting(String label, List<?> list) {
        while (true) {
            System.out.println("\n+------------ CHOOSE " + label.toUpperCase() + " ------------+");
            for (int i = 0; i < list.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + list.get(i));
            }
            System.out.println("  0. Cancel");
            System.out.print("Select option: ");

            try {
                int number = Integer.parseInt(scanner.nextLine().trim());
                if (number == 0) return CANCELLED;
                if (number >= 1 && number <= list.size()) return number - 1;
                System.out.println("! Choose a number between 0 and " + list.size() + ".");
            } catch (NumberFormatException e) {
                System.out.println("! Please enter a valid number.");
            }
        }
    }

    private static Student selectExistingStudent() {
        if (students.isEmpty()) {
            System.out.println("No student exists. Register a student first (menu option 2).");
            return null;
        }
        int pick = pickExisting("Student", students);
        if (pick == CANCELLED) return null;
        return students.get(pick);
    }

    // List<?> means "a list of anything". We only print each item (toString).
    // Returns: index in the list, REGISTER_NEW, or CANCELLED.
    private static int pickIndex(String label, List<?> list) {
        while (true) {
            System.out.println("\n+------------ CHOOSE " + label.toUpperCase() + " ------------+");
            if (list.isEmpty()) {
                System.out.println("No " + label + " exists yet. Use the [+] option to register one, or 0 to cancel.");
            }
            for (int i = 0; i < list.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + list.get(i));
            }
            int registerOption = list.size() + 1;
            System.out.println("  " + registerOption + ". [+] Register a new " + label);
            System.out.println("  0. Cancel");
            System.out.print("Select option: ");

            try {
                int number = Integer.parseInt(scanner.nextLine().trim());
                if (number == 0) return CANCELLED;
                if (number >= 1 && number <= list.size()) return number - 1;
                if (number == registerOption) return REGISTER_NEW;
                System.out.println("! Choose a number between 0 and " + registerOption + ".");
            } catch (NumberFormatException e) {
                System.out.println("! Please enter a valid number.");
            }
        }
    }

    // =====================================================
    // REPORTS
    // =====================================================
    private static void viewAllRecords() {
        printList("DEPARTMENTS", departments);
        printList("STUDENTS", students);
        printList("TEACHERS", teachers);
        printList("ADMINISTRATORS", administrators);
        printList("COURSES", courses);
        printList("CLASSROOMS", classrooms);
        printList("ENROLLMENTS", enrollments);
        printList("ASSESSMENTS", assessments);
        printList("FEE PAYMENTS", payments);
        printList("LIBRARY BOOKS", books);
        printList("BOOK LOANS", loans);
    }

    private static void printList(String title, List<? extends Registrable> list) {
        System.out.println("\n--- " + title + " (" + list.size() + ") ---");
        if (list.isEmpty()) {
            System.out.println("  (no records yet)");
        }
        for (Registrable item : list) {
            item.printDetails();
        }
    }

    private static void demonstratePolymorphism() {
        System.out.println("\n--- POLYMORPHISM DEMONSTRATION ---");

        // Superclass polymorphism: one List<Person> holds three different child types
        List<Person> people = allPeople();
        System.out.println("1. List<Person> (" + people.size() + " people):");
        if (people.isEmpty()) System.out.println("   (register a student, teacher or administrator first)");
        for (Person p : people) {
            System.out.println("   - " + p.getDisplayName() + " -> " + p.getRoleDescription());
        }

        // Interface polymorphism: unrelated classes, one shared interface
        List<Identifiable> everything = allRecords();
        System.out.println("\n2. List<Identifiable> (" + everything.size() + " records):");
        if (everything.isEmpty()) System.out.println("   (no records yet)");
        for (Identifiable item : everything) {
            System.out.println("   - " + item.getId() + " | " + item.getDisplayName()
                    + " | active: " + item.isActive());
        }
    }

    // =====================================================
    // COLLECTION HELPERS
    // =====================================================
    private static List<Person> allPeople() {
        List<Person> people = new ArrayList<>();
        people.addAll(students);
        people.addAll(teachers);
        people.addAll(administrators);
        return people;
    }

    private static List<Identifiable> allRecords() {
        List<Identifiable> all = new ArrayList<>();
        all.addAll(departments);
        all.addAll(students);
        all.addAll(teachers);
        all.addAll(administrators);
        all.addAll(courses);
        all.addAll(classrooms);
        all.addAll(enrollments);
        all.addAll(assessments);
        all.addAll(payments);
        all.addAll(books);
        all.addAll(loans);
        return all;
    }

    // =====================================================
    // UNIQUENESS RULES (throws DuplicateRecordException)
    // IDs are unique across ALL record types, so "Search by ID" is never ambiguous.
    // =====================================================
    private static void checkUnique(String kind, String value) throws DuplicateRecordException {
        boolean taken = false;

        switch (kind) {
            case "ID" -> {
                for (Identifiable r : allRecords()) {
                    if (r.getId().equalsIgnoreCase(value)) taken = true;
                }
            }
            case "DEPTCODE" -> {
                for (Department d : departments) {
                    if (d.getDepartmentCode().equalsIgnoreCase(value)) taken = true;
                }
            }
            case "MEMBER" -> {
                for (Student s : students) {
                    if (s.getMemberNumber().equalsIgnoreCase(value)) taken = true;
                }
                for (Teacher t : teachers) {
                    if (t.getMemberNumber().equalsIgnoreCase(value)) taken = true;
                }
            }
            case "STUDENTNO" -> {
                for (Student s : students) {
                    if (s.getStudentNumber().equalsIgnoreCase(value)) taken = true;
                }
            }
            case "EMPLOYEENO" -> {
                for (Teacher t : teachers) {
                    if (t.getEmployeeNumber().equalsIgnoreCase(value)) taken = true;
                }
            }
            case "COURSECODE" -> {
                for (Course c : courses) {
                    if (c.getCourseCode().equalsIgnoreCase(value)) taken = true;
                }
            }
            case "EMAIL" -> {
                for (Person p : allPeople()) {
                    if (p.getEmail().equalsIgnoreCase(value)) taken = true;
                }
            }
            case "INSTEMAIL" -> {
                for (Student s : students) {
                    if (s.getInstitutionalEmail().equalsIgnoreCase(value)) taken = true;
                }
                for (Teacher t : teachers) {
                    if (t.getInstitutionalEmail().equalsIgnoreCase(value)) taken = true;
                }
            }
            case "STAFFNO" -> {
                for (Administrator a : administrators) {
                    if (a.getStaffNumber().equalsIgnoreCase(value)) taken = true;
                }
            }
            case "ISBN" -> {
                for (LibraryBook b : books) {
                    if (b.getIsbn().equalsIgnoreCase(value)) taken = true;
                }
            }
            case "RECEIPT" -> {
                for (FeePayment p : payments) {
                    if (p.getReceiptNumber().equalsIgnoreCase(value)) taken = true;
                }
            }
            default -> { }
        }

        if (taken) {
            throw new DuplicateRecordException("'" + value + "' already exists (" + kind + ").");
        }
    }

    private static void checkEnrollmentNotDuplicate(Student student, Course course, String semester,
                                                    Enrollment ignore)
            throws DuplicateRecordException {
        for (Enrollment e : enrollments) {
            if (e != ignore && e.isActive() && e.getStudent() == student && e.getCourse() == course
                    && e.getSemester().equals(semester)) {
                throw new DuplicateRecordException(student.getDisplayName()
                        + " already has an ACTIVE enrollment in " + course.getCourseCode()
                        + " for " + semester + ".");
            }
        }
    }

    // =====================================================
    // REVIEW SCREEN
    // =====================================================
    private static boolean confirmSave(String title, Registrable record) {
        System.out.println("\n+---------------- " + title + " ----------------+");
        System.out.println(record.getRegistrationSummary());
        System.out.println("+------------------------------------------------------------+");

        while (true) {
            System.out.println("[1] Save   [0] Cancel");
            System.out.print("Select option: ");
            String input = scanner.nextLine().trim();
            if (input.equals("1")) return true;
            if (input.equals("0") || input.equalsIgnoreCase("CANCEL")) return false;
            System.out.println("! Enter 1 to save or 0 to cancel.");
        }
    }

    // =====================================================
    // FINDING RECORDS (throws RecordNotFoundException)
    // =====================================================
    private static Identifiable findById(String id) throws RecordNotFoundException {
        for (Identifiable record : allRecords()) {
            if (record.getId().equalsIgnoreCase(id)) {
                return record;
            }
        }
        throw new RecordNotFoundException("No record found with ID '" + id + "'.");
    }

    private static String recordTypeName(Identifiable r) {
        if (r instanceof Student) return "Student";
        if (r instanceof Teacher) return "Teacher";
        if (r instanceof Administrator) return "Administrator";
        if (r instanceof Department) return "Department";
        if (r instanceof Course) return "Course";
        if (r instanceof Classroom) return "Classroom";
        if (r instanceof Enrollment) return "Enrollment";
        if (r instanceof Assessment) return "Assessment";
        if (r instanceof FeePayment) return "Fee Payment";
        if (r instanceof LibraryBook) return "Library Book";
        if (r instanceof BookLoan) return "Book Loan";
        return "Record";
    }

    // =====================================================
    // SEARCH BY ID (menu option 14)
    // =====================================================
    private static void searchRecordById() {
        printBanner("SEARCH RECORD BY ID");
        String id = promptText("Enter the record ID to search for : ", 1, 30);
        if (id == null) {
            System.out.println("Search cancelled.");
            return;
        }
        try {
            Identifiable record = findById(id);
            System.out.println("\n+ Found a " + recordTypeName(record) + ":");
            printFullRecord(record);
        } catch (RecordNotFoundException e) {
            System.out.println("! " + e.getMessage());
        }
    }

    private static void printFullRecord(Identifiable record) {
        List<String> lines = describeRecord(record);
        for (int i = 0; i < lines.size(); i++) {
            System.out.println(String.format("  %02d %s", i + 1, lines.get(i)));
        }
    }

    // =====================================================
    // UPDATE (menu option 15)
    // The record is looked up by ID, its fields are listed with numbers,
    // and the user edits one field at a time. Every change is validated when typed
    // and needs explicit confirmation before it is applied.
    // =====================================================
    private static void updateRecordForm() {
        printBanner("UPDATE EXISTING RECORD");
        String id = promptText("Enter the ID of the record to update : ", 1, 30);
        if (id == null) {
            System.out.println("Update cancelled.");
            return;
        }

        Identifiable record;
        try {
            record = findById(id);
        } catch (RecordNotFoundException e) {
            System.out.println("! " + e.getMessage());
            return;
        }

        while (true) {
            List<String> lines = describeRecord(record);
            System.out.println("\n+---------- UPDATE " + recordTypeName(record).toUpperCase() + " ----------+");
            printFullRecord(record);
            System.out.println("Enter a field number to edit, or 0 to finish.");
            int number = promptIntRange("Field number [0-" + lines.size() + "] : ", 0, lines.size());
            if (number == CANCELLED || number == 0) {
                break;
            }
            boolean changed = editRecordField(record, number);
            System.out.println(changed ? "+ Field updated." : "No change was made.");
        }

        // Final re-check of the whole record
        try {
            ((Registrable) record).validate();
            System.out.println("+ Update finished. Record is valid: " + ((Registrable) record).getRegistrationSummary());
        } catch (ValidationException e) {
            System.out.println("! The record is not valid: " + e.getMessage());
            System.out.println("  Use option 15 again to fix it.");
        }
    }

    private static boolean editRecordField(Identifiable r, int n) {
        if (r instanceof Student) return editStudentField((Student) r, n);
        if (r instanceof Teacher) return editTeacherField((Teacher) r, n);
        if (r instanceof Administrator) return editAdministratorField((Administrator) r, n);
        if (r instanceof Department) return editDepartmentField((Department) r, n);
        if (r instanceof Course) return editCourseField((Course) r, n);
        if (r instanceof Classroom) return editClassroomField((Classroom) r, n);
        if (r instanceof Enrollment) return editEnrollmentField((Enrollment) r, n);
        if (r instanceof Assessment) return editAssessmentField((Assessment) r, n);
        if (r instanceof FeePayment) return editFeePaymentField((FeePayment) r, n);
        if (r instanceof LibraryBook) return editLibraryBookField((LibraryBook) r, n);
        if (r instanceof BookLoan) return editBookLoanField((BookLoan) r, n);
        return false;
    }

    private static boolean changeConfirmed(String label, Object oldValue, Object newValue) {
        return confirmAction("Change " + label + ":  " + oldValue + "  ->  " + newValue);
    }

    private static String yesNo(boolean value) {
        return value ? "YES" : "NO";
    }

    private static void printLocked(String why) {
        System.out.println("! This field cannot be edited: " + why);
    }

    // =====================================================
    // DEPARTMENT HEAD TEACHER (optional at creation, assigned later)
    // Uses PrerequisiteMissingException when no Teacher exists yet.
    // =====================================================
    private static boolean assignHeadTeacher(Department dept) {
        try {
            if (teachers.isEmpty()) {
                throw new PrerequisiteMissingException(
                        "No teacher exists. Register a teacher before assigning a head teacher.");
            }
        } catch (PrerequisiteMissingException e) {
            System.out.println("! " + e.getMessage());
            System.out.println("[1] Register prerequisite now   [2] Return to menu");
            String choice = "";
            while (choice != null && !choice.equals("1") && !choice.equals("2")) {
                choice = promptText("Select option [1/2] : ", 1, 1);
                if (choice != null && !choice.equals("1") && !choice.equals("2")) {
                    System.out.println("! Enter 1 or 2.");
                }
            }
            if (choice == null || choice.equals("2")) {
                return false;
            }
            if (registerTeacherForm() == null) {
                System.out.println("No teacher was registered, so no head teacher was assigned.");
                return false;
            }
            System.out.println("Teacher saved. Returning to the head teacher selection...");
        }

        int pick = pickExisting("Head Teacher", teachers);
        if (pick == CANCELLED) return false;
        Teacher chosen = teachers.get(pick);
        String oldName = (dept.getHeadTeacher() == null) ? "Unassigned" : dept.getHeadTeacher().getDisplayName();
        if (changeConfirmed("Head teacher", oldName, chosen.getDisplayName())) {
            dept.setHeadTeacher(chosen);
            return true;
        }
        return false;
    }

    // =====================================================
    // FIELD DESCRIPTIONS (numbered in the same order as the forms)
    // =====================================================
    private static List<String> describeRecord(Identifiable r) {
        if (r instanceof Student) return describeStudent((Student) r);
        if (r instanceof Teacher) return describeTeacher((Teacher) r);
        if (r instanceof Administrator) return describeAdministrator((Administrator) r);
        if (r instanceof Department) return describeDepartment((Department) r);
        if (r instanceof Course) return describeCourse((Course) r);
        if (r instanceof Classroom) return describeClassroom((Classroom) r);
        if (r instanceof Enrollment) return describeEnrollment((Enrollment) r);
        if (r instanceof Assessment) return describeAssessment((Assessment) r);
        if (r instanceof FeePayment) return describeFeePayment((FeePayment) r);
        if (r instanceof LibraryBook) return describeLibraryBook((LibraryBook) r);
        if (r instanceof BookLoan) return describeBookLoan((BookLoan) r);
        return new ArrayList<>();
    }

    private static List<String> personLines(Person p) {
        List<String> lines = new ArrayList<>();
        lines.add("Person ID          : " + p.getPersonId());
        lines.add("First name         : " + p.getFirstName());
        lines.add("Last name          : " + p.getLastName());
        lines.add("Email              : " + p.getEmail());
        lines.add("Phone              : " + p.getPhoneNumber());
        lines.add("Date of birth      : " + p.getDateOfBirth());
        return lines;
    }

    private static List<String> academicLines(AcademicMember m) {
        List<String> lines = new ArrayList<>();
        lines.add("Member number      : " + m.getMemberNumber());
        lines.add("Department         : " + m.getDepartment().getDisplayName());
        lines.add("Join date          : " + m.getJoinDate());
        lines.add("Academic status    : " + m.getAcademicStatus());
        lines.add("Institutional email: " + m.getInstitutionalEmail());
        lines.add("Advisor name       : " + m.getAdvisorName());
        return lines;
    }

    private static List<String> describeStudent(Student s) {
        List<String> lines = personLines(s);
        lines.addAll(academicLines(s));
        lines.add("Student number     : " + s.getStudentNumber());
        lines.add("Program name       : " + s.getProgramName());
        lines.add("Admission date     : " + s.getAdmissionDate());
        lines.add("Study year         : " + s.getStudyYear());
        lines.add("Guardian phone     : " + s.getGuardianPhone());
        lines.add("Scholarship        : " + yesNo(s.isScholarshipStatus()));
        return lines;
    }

    private static List<String> describeTeacher(Teacher t) {
        List<String> lines = personLines(t);
        lines.addAll(academicLines(t));
        lines.add("Employee number    : " + t.getEmployeeNumber());
        lines.add("Specialization     : " + t.getSpecialization());
        lines.add("Hire date          : " + t.getHireDate());
        lines.add("Salary             : " + t.getSalary());
        lines.add("Office number      : " + t.getOfficeNumber());
        lines.add("Employment type    : " + t.getEmploymentType());
        return lines;
    }

    private static List<String> describeAdministrator(Administrator a) {
        List<String> lines = personLines(a);
        lines.add("Staff number       : " + a.getStaffNumber());
        lines.add("Job title          : " + a.getJobTitle());
        lines.add("Assigned office    : " + a.getAssignedOffice());
        lines.add("Employment date    : " + a.getEmploymentDate());
        lines.add("Access level       : " + a.getAccessLevel());
        lines.add("Active             : " + yesNo(a.isActive()));
        return lines;
    }

    private static List<String> describeDepartment(Department d) {
        List<String> lines = new ArrayList<>();
        lines.add("Department ID      : " + d.getDepartmentId());
        lines.add("Department name    : " + d.getDepartmentName());
        lines.add("Department code    : " + d.getDepartmentCode());
        lines.add("Office location    : " + d.getOfficeLocation());
        lines.add("Contact email      : " + d.getContactEmail());
        lines.add("Head teacher       : "
                + (d.getHeadTeacher() == null ? "Unassigned" : d.getHeadTeacher().getDisplayName()));
        return lines;
    }

    private static List<String> describeCourse(Course c) {
        List<String> lines = new ArrayList<>();
        lines.add("Course ID          : " + c.getCourseId());
        lines.add("Course code        : " + c.getCourseCode());
        lines.add("Course title       : " + c.getCourseTitle());
        lines.add("Credit hours       : " + c.getCreditHours());
        lines.add("Department         : " + c.getDepartment().getDisplayName());
        lines.add("Assigned teacher   : " + c.getAssignedTeacher().getDisplayName());
        return lines;
    }

    private static List<String> describeClassroom(Classroom c) {
        List<String> lines = new ArrayList<>();
        lines.add("Classroom ID       : " + c.getClassroomId());
        lines.add("Building name      : " + c.getBuildingName());
        lines.add("Room number        : " + c.getRoomNumber());
        lines.add("Capacity           : " + c.getCapacity());
        lines.add("Floor number       : " + c.getFloorNumber());
        lines.add("Available          : " + yesNo(c.isAvailable()));
        return lines;
    }

    private static List<String> describeEnrollment(Enrollment e) {
        List<String> lines = new ArrayList<>();
        lines.add("Enrollment ID      : " + e.getEnrollmentId());
        lines.add("Student            : " + e.getStudent().getDisplayName());
        lines.add("Course             : " + e.getCourse().getDisplayName());
        lines.add("Enrollment date    : " + e.getEnrollmentDate());
        lines.add("Semester           : " + e.getSemester());
        lines.add("Enrollment status  : " + e.getEnrollmentStatus());
        return lines;
    }

    private static List<String> describeAssessment(Assessment a) {
        List<String> lines = new ArrayList<>();
        lines.add("Assessment ID      : " + a.getAssessmentId());
        lines.add("Enrollment         : " + a.getEnrollment().getDisplayName());
        lines.add("Assessment type    : " + a.getAssessmentType());
        lines.add("Maximum score      : " + a.getMaximumScore());
        lines.add("Obtained score     : " + a.getObtainedScore());
        lines.add("Assessment date    : " + a.getAssessmentDate());
        return lines;
    }

    private static List<String> describeFeePayment(FeePayment p) {
        List<String> lines = new ArrayList<>();
        lines.add("Payment ID         : " + p.getPaymentId());
        lines.add("Student            : " + p.getStudent().getDisplayName());
        lines.add("Amount             : " + p.getAmount());
        lines.add("Payment date       : " + p.getPaymentDate());
        lines.add("Payment method     : " + p.getPaymentMethod());
        lines.add("Receipt number     : " + p.getReceiptNumber());
        return lines;
    }

    private static List<String> describeLibraryBook(LibraryBook b) {
        List<String> lines = new ArrayList<>();
        lines.add("Book ID            : " + b.getBookId());
        lines.add("ISBN               : " + b.getIsbn());
        lines.add("Title              : " + b.getTitle());
        lines.add("Author             : " + b.getAuthor());
        lines.add("Total copies       : " + b.getTotalCopies());
        lines.add("Available copies   : " + b.getAvailableCopies());
        return lines;
    }

    private static List<String> describeBookLoan(BookLoan l) {
        List<String> lines = new ArrayList<>();
        lines.add("Loan ID            : " + l.getLoanId());
        lines.add("Student            : " + l.getStudent().getDisplayName());
        lines.add("Book               : " + l.getBook().getDisplayName());
        lines.add("Borrowed date      : " + l.getBorrowedDate());
        lines.add("Due date           : " + l.getDueDate());
        lines.add("Returned date      : "
                + (l.getReturnedDate() == null ? "(not returned)" : l.getReturnedDate().toString()));
        return lines;
    }

    // =====================================================
    // FIELD EDITORS
    // Each editor: shows the current value in the prompt, validates the new value,
    // asks for confirmation, then calls the setter. Returns true only if something changed.
    // Fields that link to other records, or that are managed by another workflow, are locked.
    // =====================================================
    private static boolean editPersonField(Person p, int n) {
        switch (n) {
            case 1 -> {
                String v = promptUnique("New person ID (current: " + p.getPersonId() + ") [4-30 chars] : ", 4, 30, "ID");
                if (v != null && changeConfirmed("Person ID", p.getPersonId(), v)) {
                    p.setPersonId(v);
                    return true;
                }
            }
            case 2 -> {
                String v = promptText("New first name (current: " + p.getFirstName() + ") [2-60 chars] : ", 2, 60);
                if (v != null && changeConfirmed("First name", p.getFirstName(), v)) {
                    p.setFirstName(v);
                    return true;
                }
            }
            case 3 -> {
                String v = promptText("New last name (current: " + p.getLastName() + ") [2-60 chars] : ", 2, 60);
                if (v != null && changeConfirmed("Last name", p.getLastName(), v)) {
                    p.setLastName(v);
                    return true;
                }
            }
            case 4 -> {
                String v = promptUniqueEmail("New email (current: " + p.getEmail() + ") : ", "EMAIL");
                if (v != null && changeConfirmed("Email", p.getEmail(), v)) {
                    p.setEmail(v);
                    return true;
                }
            }
            case 5 -> {
                String v = promptPhone("New phone (current: " + p.getPhoneNumber() + ") [10-15 digits] : ");
                if (v != null && changeConfirmed("Phone", p.getPhoneNumber(), v)) {
                    p.setPhoneNumber(v);
                    return true;
                }
            }
            case 6 -> {
                LocalDate v = promptDate("New date of birth (current: " + p.getDateOfBirth() + ") [YYYY-MM-DD] : ", true);
                if (v != null && changeConfirmed("Date of birth", p.getDateOfBirth(), v)) {
                    p.setDateOfBirth(v);
                    return true;
                }
            }
            default -> System.out.println("! Invalid field number.");
        }
        return false;
    }

    private static boolean editAcademicField(AcademicMember m, int n) {
        switch (n) {
            case 7 -> {
                String v = promptUnique("New member number (current: " + m.getMemberNumber() + ") [4-30] : ", 4, 30, "MEMBER");
                if (v != null && changeConfirmed("Member number", m.getMemberNumber(), v)) {
                    m.setMemberNumber(v);
                    return true;
                }
            }
            case 8 -> {
                Department v = selectOrCreateDepartment();
                if (v != null && changeConfirmed("Department", m.getDepartment().getDisplayName(), v.getDisplayName())) {
                    m.setDepartment(v);
                    return true;
                }
            }
            case 9 -> {
                LocalDate v = promptDate("New join date (current: " + m.getJoinDate() + ") [YYYY-MM-DD] : ", false);
                if (v != null && changeConfirmed("Join date", m.getJoinDate(), v)) {
                    m.setJoinDate(v);
                    return true;
                }
            }
            case 10 -> {
                String v = promptChoice("New academic status (current: " + m.getAcademicStatus() + "):", "ACTIVE", "INACTIVE");
                if (v != null && changeConfirmed("Academic status", m.getAcademicStatus(), v)) {
                    m.setAcademicStatus(v);
                    return true;
                }
            }
            case 11 -> {
                String v = promptUniqueEmail("New institutional email (current: " + m.getInstitutionalEmail() + ") : ", "INSTEMAIL");
                if (v != null && changeConfirmed("Institutional email", m.getInstitutionalEmail(), v)) {
                    m.setInstitutionalEmail(v);
                    return true;
                }
            }
            case 12 -> {
                String v = promptText("New advisor name (current: " + m.getAdvisorName() + ") [2-120 chars] : ", 2, 120);
                if (v != null && changeConfirmed("Advisor name", m.getAdvisorName(), v)) {
                    m.setAdvisorName(v);
                    return true;
                }
            }
            default -> System.out.println("! Invalid field number.");
        }
        return false;
    }

    private static boolean editStudentField(Student s, int n) {
        if (n <= 6) return editPersonField(s, n);
        if (n <= 12) return editAcademicField(s, n);
        switch (n) {
            case 13 -> {
                String v = promptUnique("New student number (current: " + s.getStudentNumber() + ") [4-30] : ", 4, 30, "STUDENTNO");
                if (v != null && changeConfirmed("Student number", s.getStudentNumber(), v)) {
                    s.setStudentNumber(v);
                    return true;
                }
            }
            case 14 -> {
                String v = promptText("New program name (current: " + s.getProgramName() + ") [2-120 chars] : ", 2, 120);
                if (v != null && changeConfirmed("Program name", s.getProgramName(), v)) {
                    s.setProgramName(v);
                    return true;
                }
            }
            case 15 -> {
                LocalDate v = promptDate("New admission date (current: " + s.getAdmissionDate() + ") [YYYY-MM-DD] : ", false);
                if (v != null && changeConfirmed("Admission date", s.getAdmissionDate(), v)) {
                    s.setAdmissionDate(v);
                    return true;
                }
            }
            case 16 -> {
                int v = promptIntRange("New study year (current: " + s.getStudyYear() + ") [1-6] : ", 1, 6);
                if (v != CANCELLED && changeConfirmed("Study year", s.getStudyYear(), v)) {
                    s.setStudyYear(v);
                    return true;
                }
            }
            case 17 -> {
                String v = promptPhone("New guardian phone (current: " + s.getGuardianPhone() + ") [10-15 digits] : ");
                if (v != null && changeConfirmed("Guardian phone", s.getGuardianPhone(), v)) {
                    s.setGuardianPhone(v);
                    return true;
                }
            }
            case 18 -> {
                Boolean v = promptYesNo("New scholarship (current: " + yesNo(s.isScholarshipStatus()) + "):");
                if (v != null && changeConfirmed("Scholarship", yesNo(s.isScholarshipStatus()), yesNo(v))) {
                    s.setScholarshipStatus(v);
                    return true;
                }
            }
            default -> System.out.println("! Invalid field number.");
        }
        return false;
    }

    private static boolean editTeacherField(Teacher t, int n) {
        if (n <= 6) return editPersonField(t, n);
        if (n <= 12) return editAcademicField(t, n);
        switch (n) {
            case 13 -> {
                String v = promptUnique("New employee number (current: " + t.getEmployeeNumber() + ") [4-30] : ", 4, 30, "EMPLOYEENO");
                if (v != null && changeConfirmed("Employee number", t.getEmployeeNumber(), v)) {
                    t.setEmployeeNumber(v);
                    return true;
                }
            }
            case 14 -> {
                String v = promptText("New specialization (current: " + t.getSpecialization() + ") [2-120 chars] : ", 2, 120);
                if (v != null && changeConfirmed("Specialization", t.getSpecialization(), v)) {
                    t.setSpecialization(v);
                    return true;
                }
            }
            case 15 -> {
                LocalDate v = promptDate("New hire date (current: " + t.getHireDate() + ") [YYYY-MM-DD] : ", false);
                if (v != null && changeConfirmed("Hire date", t.getHireDate(), v)) {
                    t.setHireDate(v);
                    return true;
                }
            }
            case 16 -> {
                BigDecimal v = promptMoney("New salary (current: " + t.getSalary() + ") [greater than 0] : ");
                if (v != null && changeConfirmed("Salary", t.getSalary(), v)) {
                    t.setSalary(v);
                    return true;
                }
            }
            case 17 -> {
                String v = promptText("New office number (current: " + t.getOfficeNumber() + ") [1-30 chars] : ", 1, 30);
                if (v != null && changeConfirmed("Office number", t.getOfficeNumber(), v)) {
                    t.setOfficeNumber(v);
                    return true;
                }
            }
            case 18 -> {
                String v = promptChoice("New employment type (current: " + t.getEmploymentType() + "):",
                        "FULL_TIME", "PART_TIME", "CONTRACT");
                if (v != null && changeConfirmed("Employment type", t.getEmploymentType(), v)) {
                    t.setEmploymentType(v);
                    return true;
                }
            }
            default -> System.out.println("! Invalid field number.");
        }
        return false;
    }

    private static boolean editAdministratorField(Administrator a, int n) {
        if (n <= 6) return editPersonField(a, n);
        switch (n) {
            case 7 -> {
                String v = promptUnique("New staff number (current: " + a.getStaffNumber() + ") [4-30] : ", 4, 30, "STAFFNO");
                if (v != null && changeConfirmed("Staff number", a.getStaffNumber(), v)) {
                    a.setStaffNumber(v);
                    return true;
                }
            }
            case 8 -> {
                String v = promptText("New job title (current: " + a.getJobTitle() + ") [2-60 chars] : ", 2, 60);
                if (v != null && changeConfirmed("Job title", a.getJobTitle(), v)) {
                    a.setJobTitle(v);
                    return true;
                }
            }
            case 9 -> {
                String v = promptText("New assigned office (current: " + a.getAssignedOffice() + ") [1-60 chars] : ", 1, 60);
                if (v != null && changeConfirmed("Assigned office", a.getAssignedOffice(), v)) {
                    a.setAssignedOffice(v);
                    return true;
                }
            }
            case 10 -> {
                LocalDate v = promptDate("New employment date (current: " + a.getEmploymentDate() + ") [YYYY-MM-DD] : ", false);
                if (v != null && changeConfirmed("Employment date", a.getEmploymentDate(), v)) {
                    a.setEmploymentDate(v);
                    return true;
                }
            }
            case 11 -> {
                int v = promptIntRange("New access level (current: " + a.getAccessLevel() + ") [1-5] : ", 1, 5);
                if (v != CANCELLED && changeConfirmed("Access level", a.getAccessLevel(), v)) {
                    a.setAccessLevel(v);
                    return true;
                }
            }
            case 12 -> {
                Boolean v = promptYesNo("New active status (current: " + yesNo(a.isActive()) + "):");
                if (v != null && changeConfirmed("Active", yesNo(a.isActive()), yesNo(v))) {
                    a.setActive(v);
                    return true;
                }
            }
            default -> System.out.println("! Invalid field number.");
        }
        return false;
    }

    private static boolean editDepartmentField(Department d, int n) {
        switch (n) {
            case 1 -> {
                String v = promptUnique("New department ID (current: " + d.getDepartmentId() + ") [4-30 chars] : ", 4, 30, "ID");
                if (v != null && changeConfirmed("Department ID", d.getDepartmentId(), v)) {
                    d.setDepartmentId(v);
                    return true;
                }
            }
            case 2 -> {
                String v = promptText("New department name (current: " + d.getDepartmentName() + ") [2-60 chars] : ", 2, 60);
                if (v != null && changeConfirmed("Department name", d.getDepartmentName(), v)) {
                    d.setDepartmentName(v);
                    return true;
                }
            }
            case 3 -> {
                String v = promptUnique("New department code (current: " + d.getDepartmentCode() + ") [2-10 chars] : ", 2, 10, "DEPTCODE");
                if (v != null && changeConfirmed("Department code", d.getDepartmentCode(), v)) {
                    d.setDepartmentCode(v);
                    return true;
                }
            }
            case 4 -> {
                String v = promptText("New office location (current: " + d.getOfficeLocation() + ") [1-60 chars] : ", 1, 60);
                if (v != null && changeConfirmed("Office location", d.getOfficeLocation(), v)) {
                    d.setOfficeLocation(v);
                    return true;
                }
            }
            case 5 -> {
                String v = promptEmail("New contact email (current: " + d.getContactEmail() + ") : ");
                if (v != null && changeConfirmed("Contact email", d.getContactEmail(), v)) {
                    d.setContactEmail(v);
                    return true;
                }
            }
            case 6 -> {
                return assignHeadTeacher(d);
            }
            default -> System.out.println("! Invalid field number.");
        }
        return false;
    }

    private static boolean editCourseField(Course c, int n) {
        switch (n) {
            case 1 -> {
                String v = promptUnique("New course ID (current: " + c.getCourseId() + ") [4-30 chars] : ", 4, 30, "ID");
                if (v != null && changeConfirmed("Course ID", c.getCourseId(), v)) {
                    c.setCourseId(v);
                    return true;
                }
            }
            case 2 -> {
                String v = promptUnique("New course code (current: " + c.getCourseCode() + ") [3-15 chars] : ", 3, 15, "COURSECODE");
                if (v != null && changeConfirmed("Course code", c.getCourseCode(), v)) {
                    c.setCourseCode(v);
                    return true;
                }
            }
            case 3 -> {
                String v = promptText("New course title (current: " + c.getCourseTitle() + ") [2-100 chars] : ", 2, 100);
                if (v != null && changeConfirmed("Course title", c.getCourseTitle(), v)) {
                    c.setCourseTitle(v);
                    return true;
                }
            }
            case 4 -> {
                int v = promptIntRange("New credit hours (current: " + c.getCreditHours() + ") [1-6] : ", 1, 6);
                if (v != CANCELLED && changeConfirmed("Credit hours", c.getCreditHours(), v)) {
                    c.setCreditHours(v);
                    return true;
                }
            }
            case 5 -> {
                Department v = selectOrCreateDepartment();
                if (v != null && changeConfirmed("Department", c.getDepartment().getDisplayName(), v.getDisplayName())) {
                    c.setDepartment(v);
                    return true;
                }
            }
            case 6 -> {
                Teacher v = selectOrCreateTeacher();
                if (v != null && changeConfirmed("Assigned teacher", c.getAssignedTeacher().getDisplayName(), v.getDisplayName())) {
                    c.setAssignedTeacher(v);
                    return true;
                }
            }
            default -> System.out.println("! Invalid field number.");
        }
        return false;
    }

    private static boolean editClassroomField(Classroom c, int n) {
        switch (n) {
            case 1 -> {
                String v = promptUnique("New classroom ID (current: " + c.getClassroomId() + ") [4-30 chars] : ", 4, 30, "ID");
                if (v != null && changeConfirmed("Classroom ID", c.getClassroomId(), v)) {
                    c.setClassroomId(v);
                    return true;
                }
            }
            case 2 -> {
                String v = promptText("New building name (current: " + c.getBuildingName() + ") [1-60 chars] : ", 1, 60);
                if (v != null && changeConfirmed("Building name", c.getBuildingName(), v)) {
                    c.setBuildingName(v);
                    return true;
                }
            }
            case 3 -> {
                String v = promptText("New room number (current: " + c.getRoomNumber() + ") [1-20 chars] : ", 1, 20);
                if (v != null && changeConfirmed("Room number", c.getRoomNumber(), v)) {
                    c.setRoomNumber(v);
                    return true;
                }
            }
            case 4 -> {
                int v = promptIntRange("New capacity (current: " + c.getCapacity() + ") [1-1000] : ", 1, 1000);
                if (v != CANCELLED && changeConfirmed("Capacity", c.getCapacity(), v)) {
                    c.setCapacity(v);
                    return true;
                }
            }
            case 5 -> {
                int v = promptIntRange("New floor number (current: " + c.getFloorNumber() + ") [0-100] : ", 0, 100);
                if (v != CANCELLED && changeConfirmed("Floor number", c.getFloorNumber(), v)) {
                    c.setFloorNumber(v);
                    return true;
                }
            }
            case 6 -> {
                Boolean v = promptYesNo("New availability (current: " + yesNo(c.isAvailable()) + "):");
                if (v != null && changeConfirmed("Available", yesNo(c.isAvailable()), yesNo(v))) {
                    c.setAvailable(v);
                    return true;
                }
            }
            default -> System.out.println("! Invalid field number.");
        }
        return false;
    }

    private static boolean editEnrollmentField(Enrollment en, int n) {
        switch (n) {
            case 1 -> {
                String v = promptUnique("New enrollment ID (current: " + en.getEnrollmentId() + ") [4-30 chars] : ", 4, 30, "ID");
                if (v != null && changeConfirmed("Enrollment ID", en.getEnrollmentId(), v)) {
                    en.setEnrollmentId(v);
                    return true;
                }
            }
            case 2, 3 -> printLocked("the student and course are fixed once enrolled (drop and re-enroll instead).");
            case 4 -> {
                LocalDate v = promptDate("New enrollment date (current: " + en.getEnrollmentDate() + ") [YYYY-MM-DD] : ", false);
                if (v != null && changeConfirmed("Enrollment date", en.getEnrollmentDate(), v)) {
                    en.setEnrollmentDate(v);
                    return true;
                }
            }
            case 5 -> {
                String v = promptChoice("New semester (current: " + en.getSemester() + "):", "FALL", "SPRING", "SUMMER");
                if (v == null) return false;
                try {
                    if (en.isActive()) {
                        checkEnrollmentNotDuplicate(en.getStudent(), en.getCourse(), v, en);
                    }
                } catch (DuplicateRecordException ex) {
                    System.out.println("! " + ex.getMessage());
                    return false;
                }
                if (changeConfirmed("Semester", en.getSemester(), v)) {
                    en.setSemester(v);
                    return true;
                }
            }
            case 6 -> {
                String v = promptChoice("New enrollment status (current: " + en.getEnrollmentStatus() + "):",
                        "ACTIVE", "COMPLETED", "DROPPED");
                if (v == null) return false;
                try {
                    if (v.equals("ACTIVE")) {
                        checkEnrollmentNotDuplicate(en.getStudent(), en.getCourse(), en.getSemester(), en);
                    }
                } catch (DuplicateRecordException ex) {
                    System.out.println("! " + ex.getMessage());
                    return false;
                }
                if (changeConfirmed("Enrollment status", en.getEnrollmentStatus(), v)) {
                    en.setEnrollmentStatus(v);
                    return true;
                }
            }
            default -> System.out.println("! Invalid field number.");
        }
        return false;
    }

    private static boolean editAssessmentField(Assessment a, int n) {
        switch (n) {
            case 1 -> {
                String v = promptUnique("New assessment ID (current: " + a.getAssessmentId() + ") [4-30 chars] : ", 4, 30, "ID");
                if (v != null && changeConfirmed("Assessment ID", a.getAssessmentId(), v)) {
                    a.setAssessmentId(v);
                    return true;
                }
            }
            case 2 -> printLocked("an assessment stays linked to the enrollment it was recorded for.");
            case 3 -> {
                String v = promptChoice("New assessment type (current: " + a.getAssessmentType() + "):",
                        "QUIZ", "ASSIGNMENT", "MIDTERM", "FINAL");
                if (v != null && changeConfirmed("Assessment type", a.getAssessmentType(), v)) {
                    a.setAssessmentType(v);
                    return true;
                }
            }
            case 4 -> {
                // The new maximum can never be lower than the score already obtained
                Double v = promptDouble("New maximum score (current: " + a.getMaximumScore()
                        + ") [at least " + a.getObtainedScore() + "] : ",
                        a.getObtainedScore(), 1000, a.getObtainedScore() > 0);
                if (v != null && changeConfirmed("Maximum score", a.getMaximumScore(), v)) {
                    a.setMaximumScore(v);
                    return true;
                }
            }
            case 5 -> {
                Double v = promptDouble("New obtained score (current: " + a.getObtainedScore()
                        + ") [0-" + a.getMaximumScore() + "] : ", 0, a.getMaximumScore(), true);
                if (v != null && changeConfirmed("Obtained score", a.getObtainedScore(), v)) {
                    a.setObtainedScore(v);
                    return true;
                }
            }
            case 6 -> {
                LocalDate v = promptDate("New assessment date (current: " + a.getAssessmentDate() + ") [YYYY-MM-DD] : ", false);
                if (v != null && changeConfirmed("Assessment date", a.getAssessmentDate(), v)) {
                    a.setAssessmentDate(v);
                    return true;
                }
            }
            default -> System.out.println("! Invalid field number.");
        }
        return false;
    }

    private static boolean editFeePaymentField(FeePayment p, int n) {
        switch (n) {
            case 1 -> {
                String v = promptUnique("New payment ID (current: " + p.getPaymentId() + ") [4-30 chars] : ", 4, 30, "ID");
                if (v != null && changeConfirmed("Payment ID", p.getPaymentId(), v)) {
                    p.setPaymentId(v);
                    return true;
                }
            }
            case 2 -> printLocked("a payment stays linked to the student who paid.");
            case 3 -> {
                BigDecimal v = promptMoney("New amount (current: " + p.getAmount() + ") [greater than 0] : ");
                if (v != null && changeConfirmed("Amount", p.getAmount(), v)) {
                    p.setAmount(v);
                    return true;
                }
            }
            case 4 -> {
                LocalDate v = promptDate("New payment date (current: " + p.getPaymentDate() + ") [YYYY-MM-DD] : ", false);
                if (v != null && changeConfirmed("Payment date", p.getPaymentDate(), v)) {
                    p.setPaymentDate(v);
                    return true;
                }
            }
            case 5 -> {
                String v = promptChoice("New payment method (current: " + p.getPaymentMethod() + "):",
                        "CASH", "MOBILE_MONEY", "BANK_TRANSFER", "CARD");
                if (v != null && changeConfirmed("Payment method", p.getPaymentMethod(), v)) {
                    p.setPaymentMethod(v);
                    return true;
                }
            }
            case 6 -> {
                String v = promptUnique("New receipt number (current: " + p.getReceiptNumber() + ") [3-30 chars] : ", 3, 30, "RECEIPT");
                if (v != null && changeConfirmed("Receipt number", p.getReceiptNumber(), v)) {
                    p.setReceiptNumber(v);
                    return true;
                }
            }
            default -> System.out.println("! Invalid field number.");
        }
        return false;
    }

    private static boolean editLibraryBookField(LibraryBook b, int n) {
        switch (n) {
            case 1 -> {
                String v = promptUnique("New book ID (current: " + b.getBookId() + ") [4-30 chars] : ", 4, 30, "ID");
                if (v != null && changeConfirmed("Book ID", b.getBookId(), v)) {
                    b.setBookId(v);
                    return true;
                }
            }
            case 2 -> {
                String v = promptUnique("New ISBN (current: " + b.getIsbn() + ") [10-17 chars] : ", 10, 17, "ISBN");
                if (v != null && changeConfirmed("ISBN", b.getIsbn(), v)) {
                    b.setIsbn(v);
                    return true;
                }
            }
            case 3 -> {
                String v = promptText("New title (current: " + b.getTitle() + ") [1-150 chars] : ", 1, 150);
                if (v != null && changeConfirmed("Title", b.getTitle(), v)) {
                    b.setTitle(v);
                    return true;
                }
            }
            case 4 -> {
                String v = promptText("New author (current: " + b.getAuthor() + ") [2-100 chars] : ", 2, 100);
                if (v != null && changeConfirmed("Author", b.getAuthor(), v)) {
                    b.setAuthor(v);
                    return true;
                }
            }
            case 5 -> {
                // Copies on loan keep their count; the shelf stock adjusts with the new total
                int onLoan = b.getTotalCopies() - b.getAvailableCopies();
                int v = promptIntRange("New total copies (current: " + b.getTotalCopies()
                        + ", on loan: " + onLoan + ") [" + onLoan + "-1000] : ", onLoan, 1000);
                if (v != CANCELLED && changeConfirmed("Total copies", b.getTotalCopies(), v)) {
                    b.setTotalCopies(v);
                    b.setAvailableCopies(v - onLoan);
                    return true;
                }
            }
            case 6 -> printLocked("available copies change only through Borrow and Return.");
            default -> System.out.println("! Invalid field number.");
        }
        return false;
    }

    private static boolean editBookLoanField(BookLoan loan, int n) {
        switch (n) {
            case 1 -> {
                String v = promptUnique("New loan ID (current: " + loan.getLoanId() + ") [4-30 chars] : ", 4, 30, "ID");
                if (v != null && changeConfirmed("Loan ID", loan.getLoanId(), v)) {
                    loan.setLoanId(v);
                    return true;
                }
            }
            case 2, 3, 4 -> printLocked("the student, book and borrowed date are fixed for a loan.");
            case 5 -> {
                LocalDate v = promptDueDate("New due date (current: " + loan.getDueDate()
                        + ") [YYYY-MM-DD | after " + loan.getBorrowedDate() + "] : ", loan.getBorrowedDate());
                if (v != null && changeConfirmed("Due date", loan.getDueDate(), v)) {
                    loan.setDueDate(v);
                    return true;
                }
            }
            case 6 -> printLocked("use menu option 12 (Return Library Book) so the stock stays correct.");
            default -> System.out.println("! Invalid field number.");
        }
        return false;
    }

    // =====================================================
    // REUSABLE INPUT HELPERS
    // Every helper: reads a whole line, validates, re-asks ONLY that field,
    // and returns null (or CANCELLED) if the user types CANCEL.
    // =====================================================
    private static String promptText(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("CANCEL")) return null;
            if (input.length() >= min && input.length() <= max) return input;
            if (input.isEmpty()) {
                System.out.println("! Error: this field cannot be blank.");
            } else {
                System.out.println("! Error: must be " + min + "-" + max + " characters.");
            }
        }
    }

    private static String promptUnique(String prompt, int min, int max, String kind) {
        while (true) {
            String value = promptText(prompt, min, max);
            if (value == null) return null;
            try {
                checkUnique(kind, value);
                return value;
            } catch (DuplicateRecordException e) {
                System.out.println("! " + e.getMessage() + " Please enter a different value.");
            }
        }
    }

    private static String promptEmail(String prompt) {
        while (true) {
            String input = promptText(prompt, 5, 100);
            if (input == null) return null;
            if (input.matches("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")) return input;
            System.out.println("! Error: enter a valid email (example: alice@example.com).");
        }
    }

    private static String promptUniqueEmail(String prompt, String kind) {
        while (true) {
            String email = promptEmail(prompt);
            if (email == null) return null;
            try {
                checkUnique(kind, email);
                return email;
            } catch (DuplicateRecordException e) {
                System.out.println("! " + e.getMessage() + " Please enter a different email.");
            }
        }
    }

    private static String promptPhone(String prompt) {
        while (true) {
            String input = promptText(prompt, 1, 20);
            if (input == null) return null;
            if (input.matches("\\+?[0-9]{10,15}")) return input;
            System.out.println("! Error: phone must be 10-15 digits (optional leading +).");
        }
    }

    // mustBePast = true  -> date must be before today (date of birth)
    // mustBePast = false -> date must not be in the future
    private static LocalDate promptDate(String prompt, boolean mustBePast) {
        while (true) {
            String input = promptText(prompt, 1, 20);
            if (input == null) return null;
            try {
                LocalDate date = LocalDate.parse(input);
                if (mustBePast && !date.isBefore(LocalDate.now())) {
                    System.out.println("! Error: date must be in the past.");
                } else if (!mustBePast && date.isAfter(LocalDate.now())) {
                    System.out.println("! Error: date cannot be in the future.");
                } else {
                    return date;
                }
            } catch (DateTimeParseException e) {
                System.out.println("! Invalid date: use YYYY-MM-DD (example: 2004-05-16).");
                System.out.println("  Please re-enter this field.");
            }
        }
    }

    private static int promptIntRange(String prompt, int min, int max) {
        while (true) {
            String input = promptText(prompt, 1, 10);
            if (input == null) return CANCELLED;
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) return value;
                System.out.println("! Error: enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("! Error: enter a valid whole number.");
            }
        }
    }

    private static BigDecimal promptMoney(String prompt) {
        while (true) {
            String input = promptText(prompt, 1, 20);
            if (input == null) return null;
            try {
                BigDecimal value = new BigDecimal(input);
                if (value.compareTo(BigDecimal.ZERO) > 0) return value;
                System.out.println("! Error: amount must be greater than zero.");
            } catch (NumberFormatException e) {
                System.out.println("! Error: enter a valid amount (example: 450000.50).");
            }
        }
    }

    // A decimal number between min and max. minInclusive=false means "greater than min".
    private static Double promptDouble(String prompt, double min, double max, boolean minInclusive) {
        while (true) {
            String input = promptText(prompt, 1, 20);
            if (input == null) return null;
            try {
                double value = Double.parseDouble(input);
                boolean aboveMin = minInclusive ? value >= min : value > min;
                if (aboveMin && value <= max) return value;
                String lower = minInclusive ? "at least " : "greater than ";
                System.out.println("! Error: value must be " + lower + min + " and at most " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("! Error: enter a valid number (example: 17.5).");
            }
        }
    }

    // Due date: any valid date, but it must come AFTER the borrowed date
    private static LocalDate promptDueDate(String prompt, LocalDate borrowed) {
        while (true) {
            String input = promptText(prompt, 1, 20);
            if (input == null) return null;
            try {
                LocalDate due = LocalDate.parse(input);
                if (due.isAfter(borrowed)) return due;
                System.out.println("! Error: due date must be after the borrowed date (" + borrowed + ").");
            } catch (DateTimeParseException e) {
                System.out.println("! Invalid date: use YYYY-MM-DD (example: 2026-12-01).");
            }
        }
    }

    // Return date: not in the future and never before the borrowed date
    private static LocalDate promptReturnDate(String prompt, LocalDate borrowed) {
        while (true) {
            String input = promptText(prompt, 1, 20);
            if (input == null) return null;
            try {
                LocalDate returned = LocalDate.parse(input);
                if (returned.isBefore(borrowed)) {
                    System.out.println("! Error: return date cannot be before the borrowed date (" + borrowed + ").");
                } else if (returned.isAfter(LocalDate.now())) {
                    System.out.println("! Error: return date cannot be in the future.");
                } else {
                    return returned;
                }
            } catch (DateTimeParseException e) {
                System.out.println("! Invalid date: use YYYY-MM-DD (example: 2026-11-20).");
            }
        }
    }

    // Explicit confirmation before an important change
    private static boolean confirmAction(String message) {
        System.out.println(message);
        while (true) {
            System.out.println("[1] Confirm   [0] Cancel");
            System.out.print("Select option: ");
            String input = scanner.nextLine().trim();
            if (input.equals("1")) return true;
            if (input.equals("0") || input.equalsIgnoreCase("CANCEL")) return false;
            System.out.println("! Enter 1 to confirm or 0 to cancel.");
        }
    }

    // Shows numbered options and returns the chosen TEXT (varargs: any number of Strings)
    private static String promptChoice(String label, String... options) {
        while (true) {
            System.out.println(label);
            for (int i = 0; i < options.length; i++) {
                System.out.println("     [" + (i + 1) + "] " + options[i]);
            }
            String input = promptText("     Select : ", 1, 5);
            if (input == null) return null;
            try {
                int number = Integer.parseInt(input);
                if (number >= 1 && number <= options.length) return options[number - 1];
                System.out.println("! Error: choose 1-" + options.length + ".");
            } catch (NumberFormatException e) {
                System.out.println("! Error: enter a number.");
            }
        }
    }

    private static Boolean promptYesNo(String label) {
        String answer = promptChoice(label, "YES", "NO");
        if (answer == null) return null;
        return answer.equals("YES");
    }
}
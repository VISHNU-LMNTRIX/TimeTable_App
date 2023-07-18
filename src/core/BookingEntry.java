package core;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public class BookingEntry {
    private String facultyName;
    private String subject;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String session;
    private LocalTime startTime;
    private LocalTime endTime;

    public BookingEntry() {
        // Default constructor
    }

    public BookingEntry(String facultyName, String subject, LocalDate date, String session, LocalTime startTime, LocalTime endTime) {
        this.facultyName = facultyName;
        this.subject = subject;
        this.date = date;
        this.session = session;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and setters

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}

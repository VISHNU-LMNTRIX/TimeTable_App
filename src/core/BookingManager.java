package core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class BookingManager {
    private static final String FILE_PATH = "src/data/bookings.json";
    private ObjectMapper objectMapper;

    public BookingManager() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public void createBookingEntry(String facultyName, String subject, LocalDate date, LocalTime startTime, LocalTime endTime) {
        BookingEntry bookingEntry = new BookingEntry(facultyName, subject, date, startTime, endTime);
        List<BookingEntry> bookings = readBookingsFromFile();
        bookings.add(bookingEntry);
        saveBookingsToFile(bookings);
    }

    public void deleteBookingEntry(String facultyName, LocalDate date, LocalTime startTime, LocalTime endTime) {
        List<BookingEntry> bookings = readBookingsFromFile();
        boolean found = false;
        for (BookingEntry bookingEntry : bookings) {
            if (bookingEntry.getFacultyName().equals(facultyName) && bookingEntry.getDate().equals(date) &&
                    bookingEntry.getStartTime().equals(startTime) && bookingEntry.getEndTime().equals(endTime)) {
                bookings.remove(bookingEntry);
                found = true;
                break;
            }
        }
        if (found) {
            saveBookingsToFile(bookings);
        } else {
            System.out.println("No object found");
        }
    }

    public void modifyBookingEntry(String facultyName, LocalDate date, LocalTime startTime, LocalTime endTime, LocalTime newStartTime, LocalTime newEndTime) {
        List<BookingEntry> bookings = readBookingsFromFile();
        boolean found = false;
        for (BookingEntry bookingEntry : bookings) {
            if (bookingEntry.getFacultyName().equals(facultyName) && bookingEntry.getDate().equals(date) &&
                    bookingEntry.getStartTime().equals(startTime) && bookingEntry.getEndTime().equals(endTime)) {
                bookingEntry.setStartTime(newStartTime);
                bookingEntry.setEndTime(newEndTime);
                found = true;
                break;
            }
        }
        if (found) {
            saveBookingsToFile(bookings);
        } else {
            System.out.println("No object found");
        }
    }

    private List<BookingEntry> readBookingsFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(file, new TypeReference<List<BookingEntry>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void saveBookingsToFile(List<BookingEntry> bookings) {
        try {
            // Enable pretty-printing
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.writeValue(new File(FILE_PATH), bookings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

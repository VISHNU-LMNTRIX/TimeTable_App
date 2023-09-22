package core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class BookingManager {
    private static final String FILE_PATH = "src/data/bookings.json";
    private ObjectMapper objectMapper;

    public BookingManager() {
        this.objectMapper = new ObjectMapper();
        // objectMapper is registered with new JavaTimeModule() to handle LocalTime/LocalDate serialization and deserialization, when accessing bookings.json file.
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public void createBookingEntry(BookingEntry bookingEntry) {
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

    public List<BookingEntry> readBookingData() {
        List<BookingEntry> bookingList = new ArrayList<>();
        try (FileInputStream fileInputStream = new FileInputStream("src/data/bookings.json"); 
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream); 
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            bookingList = objectMapper.readValue(bufferedReader, new TypeReference<List<BookingEntry>>(){});
            return bookingList;
        } catch(IOException e) {
            e.printStackTrace();
            System.out.println("An error occured while accessing/reading the booking details.");
            return bookingList;
        }
    }
}

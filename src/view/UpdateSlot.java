package view;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import core.BookingEntry;
import core.BookingManager;
import core.Faculty;
import core.JsonUpdateListener;

public class UpdateSlot {
    //Global Identifiers
    ObjectMapper objectMapper;
    JComboBox<String> dateComboBox;
    JComboBox<String> nameComboBox;
    JComboBox<String> timeComboBox;
    JComboBox<Integer> newDayComboBox;
    JComboBox<Integer> newYearComboBox;
    JComboBox<String> newMonthComboBox;
    JComboBox<Integer> startTimeComboBox;
    JComboBox<Integer> endTimeComboBox;
    JComboBox<String> sessionComboBox;
    List<BookingEntry> bookingList = new ArrayList<>();
    BookingManager bookingManager = new BookingManager();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");

    public UpdateSlot(JFrame parent, String facultyName, String privilege, JsonUpdateListener callback){
        //Placed at the top because the try catch block makes the variables local to them.
        List<Faculty> facultyList = new ArrayList<>();

        //Creating the Dialog box
        JDialog updateDialog = new JDialog(parent, "Update Slot", Dialog.ModalityType.APPLICATION_MODAL);
        updateDialog.setLayout(null);
        updateDialog.setSize(420,360);
        updateDialog.setResizable(false);

        objectMapper = new ObjectMapper();
        try (FileInputStream fileInputStream = new FileInputStream("src/data/login.json"); 
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream); 
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            facultyList = objectMapper.readValue(bufferedReader, new TypeReference<List<Faculty>>(){});
        } catch(IOException e) {
            e.printStackTrace();
            System.out.println("An error occurred while accessing/reading the login details.");
        }

        // objectMapper is registered with new JavaTimeModule() to handle LocalTime/LocalDate serialization and deserialization, when accessing bookings.json file.
        objectMapper.registerModule(new JavaTimeModule());
        bookingList = bookingManager.readBookingData(); // Read the booking data from bookings.json

        // Add the components
        JLabel name = new JLabel("Name :");
        name.setBounds(70,40,70,25);
        updateDialog.add(name);

        nameComboBox = new JComboBox<>();
        nameComboBox.setBounds(175,40,175,25);
        if(privilege.equals("admin")){
            for(Faculty faculty : facultyList){
                nameComboBox.addItem(faculty.getName());
            }
        }
        else if(privilege.equals("moderator")){
            nameComboBox.addItem(facultyName);
        }
        updateDialog.add(nameComboBox);

        JLabel date = new JLabel("Date :");
        date.setBounds(70,75,70,25);
        updateDialog.add(date);

        dateComboBox = new JComboBox<>();
        dateComboBox.setBounds(175,75,175,25);
        updateDateComboBox();
        updateDialog.add(dateComboBox);

        JLabel time = new JLabel("Time :");
        time.setBounds(70,110,70,25);
        updateDialog.add(time);

        timeComboBox = new JComboBox<>();
        timeComboBox.setBounds(175,110,175,25);
        updateTimeComboBox();
        updateDialog.add(timeComboBox);

        JLabel newDate = new JLabel("New Date :");
        newDate.setBounds(70,145,70,25);
        updateDialog.add(newDate);

        newDayComboBox = new JComboBox<>();
        newDayComboBox.setBounds(175,145,50,25);
        updateDialog.add(newDayComboBox);

        newMonthComboBox = new JComboBox<>();
        newMonthComboBox.setBounds(230,145,55,25);
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep",
        "Oct", "Nov", "Dec"};
        for(String month : months){
            newMonthComboBox.addItem(month);
        }
        newMonthComboBox.setSelectedItem(Timetable.getSelectedMonth());
        updateDialog.add(newMonthComboBox);

        Integer[] years = {2021, 2022, 2023, 2024, 2025};
        newYearComboBox = new JComboBox<>(years);
        newYearComboBox.setBounds(290,145,60,25);
        newYearComboBox.setSelectedItem(Timetable.getSelectedYear());
        updateDialog.add(newYearComboBox);
        // Since dayComboBox items depends on monthComboBox and yearComboBox items, dayComboBox items are added below:
        updateNoOfDays();

        JLabel session = new JLabel("New Session :");
        session.setBounds(70,180,90,25);
        updateDialog.add(session);

        String[] sessions = {"Forenoon","Afternoon","Evening"};
        sessionComboBox = new JComboBox<>(sessions);
        sessionComboBox.setBounds(175,180,175,25);
        updateDialog.add(sessionComboBox);

        JLabel newTime = new JLabel("New Time :");
        newTime.setBounds(70,215,80,25);
        updateDialog.add(newTime);

        startTimeComboBox = new JComboBox<>();
        startTimeComboBox.setBounds(175,215,75,25);
        updateDialog.add(startTimeComboBox);
        
        JLabel to = new JLabel("to");
        to.setBounds(258,215,15,25);
        updateDialog.add(to);

        endTimeComboBox = new JComboBox<>();
        endTimeComboBox.setBounds(275,215,75,25);
        updateDialog.add(endTimeComboBox);
        updateStartandEndTime();

        JButton submit = new JButton("Update");
        submit.setBounds(165,270,90,25);
        updateDialog.add(submit);

        //Event Listeners
        submit.addActionListener(e -> {
            //If there are no slots booked
            if((String)dateComboBox.getSelectedItem() == null){
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(updateDialog, "The Slot Is Already Empty.", "Updation Status", JOptionPane.WARNING_MESSAGE);
                });
            }
            else{
                LocalDate bookingDate = LocalDate.of((Integer)newYearComboBox.getSelectedItem(), newMonthComboBox.getSelectedIndex()+1, (Integer)newDayComboBox.getSelectedItem());

                //StringTokenizer is used to seperate the time from the string obtained from timeComboBox, which contains string like "09:00 to 10:00" .The seperated strings can be accessed using .nextToken() method.
                StringTokenizer timeString = new StringTokenizer((String)timeComboBox.getSelectedItem()," to ");
                LocalTime startTime = LocalTime.parse(timeString.nextToken());
                LocalTime endTime = LocalTime.parse(timeString.nextToken());
                LocalDate deleteDate = LocalDate.parse((String)dateComboBox.getSelectedItem(), formatter);

                //fetch the subject of updating slot from the bookings.json
                String subject = "";
                bookingList = bookingManager.readBookingData();
                for (BookingEntry bookingEntry : bookingList) {
                    if (bookingEntry.getFacultyName().equals(facultyName) && bookingEntry.getDate().equals(deleteDate) && bookingEntry.getStartTime().equals(startTime) && bookingEntry.getEndTime().equals(endTime)) {
                        subject = bookingEntry.getSubject();
                        break;
                    }
                }

                BookingEntry bookingEntry = new BookingEntry((String)nameComboBox.getSelectedItem(), subject, bookingDate, (String)sessionComboBox.getSelectedItem(), LocalTime.of((Integer)startTimeComboBox.getSelectedItem(), 0), LocalTime.of((Integer)endTimeComboBox.getSelectedItem(), 0));

                // If slot is avalible for booking on new date, then continue deleting the old slot
                if(isSlotAvailable(bookingEntry, updateDialog)){
                    BookingManager bookingManager = new BookingManager();
                    // Delete the old slot
                    bookingManager.deleteBookingEntry((String)nameComboBox.getSelectedItem(), deleteDate, startTime, endTime);
                    callback.updateCalendarView(deleteDate);

                    // Book the new slot
                    bookingManager.createBookingEntry(bookingEntry);
                    callback.updateCalendarView(bookingDate);

                    bookingList = bookingManager.readBookingData();
                    updateDateComboBox();
                    updateTimeComboBox();
                    
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(updateDialog, "Slot Updated Succesfully", "Updation Status", JOptionPane.INFORMATION_MESSAGE);
                    });
                }
            }
        });

        newMonthComboBox.addActionListener(e -> {
            updateNoOfDays();
        });

        newYearComboBox.addActionListener(e -> {
            updateNoOfDays();
        });

        sessionComboBox.addActionListener(e -> {
            updateStartandEndTime();
        });

        nameComboBox.addActionListener(e -> {
            updateDateComboBox();
        });

        dateComboBox.addActionListener(e -> {
            updateTimeComboBox();
        });

        // Calculate the center coordinates
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (int) (screenSize.getWidth() - updateDialog.getWidth()) / 2;
        int centerY = (int) (screenSize.getHeight() - updateDialog.getHeight()) / 2;

        
        updateDialog.setLocation(centerX, centerY);  // Set the window location
        updateDialog.setVisible(true);
    }

    private void updateDateComboBox() {
        dateComboBox.removeAllItems();
        String facultyName = (String)nameComboBox.getSelectedItem();

        // Create a set to keep track of unique date strings
        Set<String> uniqueDateStrings = new HashSet<>();

        for(BookingEntry booking : bookingList){
            if(booking.getFacultyName().equals(facultyName)){
                String dateString = booking.getDate().format(formatter);
                
                // Check if the date string is not already in the set before adding it
                if (!uniqueDateStrings.contains(dateString)) {
                    uniqueDateStrings.add(dateString);
                    dateComboBox.addItem(dateString);
                }
            }
        }
    }

    private void updateTimeComboBox() {
        timeComboBox.removeAllItems();
        String facultyName = (String) nameComboBox.getSelectedItem();
        String selectedDate = (String) dateComboBox.getSelectedItem();
    
        if (selectedDate != null) {
            // Define the expected format of the string
            LocalDate date = LocalDate.parse(selectedDate, formatter);
            for (BookingEntry booking : bookingList) {
                if (booking.getFacultyName().equals(facultyName) && booking.getDate().equals(date)) {
                    timeComboBox.addItem(booking.getStartTime().toString() + " to " + booking.getEndTime().toString());
                }
            }
        }
    }

    //Function to update the no:of days when month/year is changed.
    private void updateNoOfDays() {
        Object selectedItem = newDayComboBox.getSelectedItem();
        int selectedDay = selectedItem != null ? (int) selectedItem : 1;
        newDayComboBox.removeAllItems();
        int year = (Integer) newYearComboBox.getSelectedItem();
        int month = newMonthComboBox.getSelectedIndex() + 1;
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();
    
        for (int day = 1; day <= daysInMonth; day++) {
            newDayComboBox.addItem(day);
        }
    
        // Check if the year and month match the current year and month
        if (year == LocalDate.now().getYear() && month == LocalDate.now().getMonthValue()) {
            newDayComboBox.setSelectedItem(LocalDate.now().getDayOfMonth());
        }

        if(selectedDay != 1){
            newDayComboBox.setSelectedItem(selectedDay);
        }
    }

    private void updateStartandEndTime() {
        startTimeComboBox.removeAllItems();
        endTimeComboBox.removeAllItems();
        String SelectedSession = (String)sessionComboBox.getSelectedItem();
        if(SelectedSession.equals("Evening")){
            for(int i=6;i<=9;i++){
                startTimeComboBox.addItem(i); //Autoboxing
                endTimeComboBox.addItem(i+1);
            }
        }
        else if(SelectedSession.equals("Afternoon")){
            for(int i=1;i<=5;i++){
                startTimeComboBox.addItem(i);
                endTimeComboBox.addItem(i+1);
            }
        }
        else{
            for(int i=9;i<=11;i++){
                startTimeComboBox.addItem(i);
                endTimeComboBox.addItem(i+1);
            }
        }
    }

    private boolean isSlotAvailable(BookingEntry newBooking, Component parent) {
        bookingList = bookingManager.readBookingData();
        
        for (BookingEntry booking : bookingList) {
            // Check if the dates and sessions match
            if (booking.getDate().equals(newBooking.getDate()) && booking.getSession().equals(newBooking.getSession())) {
                // Check if there is any overlap in the time range
                if (booking.getStartTime().isBefore(newBooking.getEndTime()) && booking.getEndTime().isAfter(newBooking.getStartTime())) {
                    //Message to be displayed when slot is not available
                    String message = "Sorry, The slot is already taken by " + booking.getFacultyName() + " From " + booking.getStartTime().toString() + " to " + booking.getEndTime().toString() + ".";

                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(parent, message, "Booking Status", JOptionPane.ERROR_MESSAGE);
                    });
                    return false; // Slot is not available
                }
            }
        }
        return true; // Slot is available
    }
}
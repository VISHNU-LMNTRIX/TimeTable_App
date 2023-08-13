package view;

import java.awt.Component;
import java.awt.Dialog;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

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

public class BookSlot {
    //Global Identifiers
    JComboBox<Integer> dayComboBox;
    JComboBox<String> monthComboBox;
    JComboBox<Integer> yearComboBox;
    JComboBox<String> sessionComboBox;
    JComboBox<Integer> startTimeComboBox;
    JComboBox<Integer> endTimeComboBox;
    ObjectMapper objectMapper;
    JDialog bookDialog;
    JLabel bookingStatus;
    List<BookingEntry> bookingList = new ArrayList<>();
    BookingManager bookingManager = new BookingManager();

    public BookSlot(JFrame parent, String facultyName, String privilege, JsonUpdateListener callback){
        //Placed at the top because the try catch block makes the variables local to them.
        List<Faculty> facultyList = new ArrayList<>();

        //Creating the Dialog box
        bookDialog = new JDialog(parent, "Book Slot", Dialog.ModalityType.APPLICATION_MODAL);
        bookDialog.setLayout(null);
        bookDialog.setSize(400,335);
        bookDialog.setResizable(false);

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

        // Add the components
        JLabel name = new JLabel("Name :");
        name.setBounds(70,40,70,25);
        bookDialog.add(name);

        JComboBox<String> nameComboBox = new JComboBox<>();
        nameComboBox.setBounds(155,40,175,25);
        if(privilege.equals("admin")){
            for(Faculty faculty : facultyList){
            nameComboBox.addItem(faculty.getName());
            }
        }
        else if(privilege.equals("moderator")){ // "else" not used because different privileges can be added later
            nameComboBox.addItem(facultyName);
        }
        bookDialog.add(nameComboBox);

        JLabel date = new JLabel("Date :");
        date.setBounds(70,75,70,25);
        bookDialog.add(date);

        dayComboBox = new JComboBox<>();
        dayComboBox.setBounds(155,75,50,25);
        bookDialog.add(dayComboBox);

        monthComboBox = new JComboBox<>();
        monthComboBox.setBounds(210,75,55,25);
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep",
        "Oct", "Nov", "Dec"};
        for(String month : months){
            monthComboBox.addItem(month);
        }
        monthComboBox.setSelectedItem(Timetable.getSelectedMonth());
        bookDialog.add(monthComboBox);

        Integer[] years = {2021, 2022, 2023, 2024, 2025};
        yearComboBox = new JComboBox<>(years);
        yearComboBox.setBounds(270,75,60,25);
        yearComboBox.setSelectedItem(Timetable.getSelectedYear());
        bookDialog.add(yearComboBox);
        // Since dayComboBox items depends on monthComboBox and yearComboBox items, dayComboBox items are added below:
        updateNoOfDays();

        JLabel session = new JLabel("Session :");
        session.setBounds(70,110,70,25);
        bookDialog.add(session);

        String[] sessions = {"Forenoon","Afternoon","Evening"};
        sessionComboBox = new JComboBox<>(sessions);
        sessionComboBox.setBounds(155,110,175,25);
        bookDialog.add(sessionComboBox);

        JLabel time = new JLabel("Time :");
        time.setBounds(70,145,70,25);
        bookDialog.add(time);

        startTimeComboBox = new JComboBox<>();
        startTimeComboBox.setBounds(155,145,75,25);
        bookDialog.add(startTimeComboBox);
        
        JLabel to = new JLabel("to");
        to.setBounds(238,145,15,25);
        bookDialog.add(to);

        endTimeComboBox = new JComboBox<>();
        endTimeComboBox.setBounds(255,145,75,25);
        bookDialog.add(endTimeComboBox);
        updateStartandEndTime();

        JLabel subject = new JLabel("Subject");
        subject.setBounds(70,180,70,25);
        bookDialog.add(subject);

        String[] subjects = {"Computer Architecture","Programming Paradigms","Data Structures & Algorithms","Computer Networks","Mathematical Foundns of CS"}; //Add new subjects within this array.
        JComboBox<String> subjectComboBox = new JComboBox<>(subjects);
        subjectComboBox.setBounds(155,180,175,25);
        bookDialog.add(subjectComboBox);

        JButton submit = new JButton("Book");
        submit.setBounds(155,240,90,25);
        bookDialog.add(submit);

        //Event Listeners
        submit.addActionListener(e -> {
            LocalDate bookingDate = LocalDate.of((Integer)yearComboBox.getSelectedItem(), monthComboBox.getSelectedIndex()+1, (Integer)dayComboBox.getSelectedItem());

            BookingEntry bookingEntry = new BookingEntry((String)nameComboBox.getSelectedItem(), (String)subjectComboBox.getSelectedItem(), bookingDate, (String)sessionComboBox.getSelectedItem(), LocalTime.of((Integer)startTimeComboBox.getSelectedItem(), 0), LocalTime.of((Integer)endTimeComboBox.getSelectedItem(), 0));

            if(isSlotAvailable(bookingEntry, bookDialog)){
                BookingManager bookingManager = new BookingManager();
                bookingManager.createBookingEntry(bookingEntry);
                callback.updateCalendarView(bookingDate);

                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(bookDialog, "Slot Booked Succesfully", "Booking Status", JOptionPane.INFORMATION_MESSAGE);
                });
                
            }
        });

        monthComboBox.addActionListener(e -> {
            updateNoOfDays();
        });

        yearComboBox.addActionListener(e -> {
            updateNoOfDays();
        });

        sessionComboBox.addActionListener(e -> {
            updateStartandEndTime();
        });

         // Calculate the center coordinates
        int centerX = parent.getX() + (parent.getWidth() - bookDialog.getWidth()) / 2;
        int centerY = parent.getY() + (parent.getHeight() - bookDialog.getHeight()) / 2;
 
         bookDialog.setLocation(centerX, centerY);  // Set the window location
         bookDialog.setVisible(true);
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

    //Function to update the no:of days when month/year is changed.
    private void updateNoOfDays() {
        Object selectedItem = dayComboBox.getSelectedItem();
        int selectedDay = selectedItem != null ? (int) selectedItem : 1;
        dayComboBox.removeAllItems();
        int year = (Integer) yearComboBox.getSelectedItem();
        int month = monthComboBox.getSelectedIndex() + 1;
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();
    
        for (int day = 1; day <= daysInMonth; day++) {
            dayComboBox.addItem(day);
        }
    
        // Check if the year and month match the current year and month
        if (year == LocalDate.now().getYear() && month == LocalDate.now().getMonthValue()) {
            dayComboBox.setSelectedItem(LocalDate.now().getDayOfMonth());
        }

        if(selectedDay != 1){
            dayComboBox.setSelectedItem(selectedDay);
        }
    }
    
}
package view;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import core.BookingEntry;
import core.BookingManager;
import core.Faculty;

public class BookSlot {
    //Global Identifiers
    JComboBox<Integer> dayComboBox;
    JComboBox<String> monthComboBox;
    JComboBox<Integer> yearComboBox;
    JComboBox<String> sessionComboBox;
    JComboBox<Integer> startTimeComboBox;
    JComboBox<Integer> endTimeComboBox;
    JLabel bookingStatus;

    public BookSlot(JFrame parent, String facultyName, String privilege){
        //Placed at the top because the try catch block makes the variables local to them.
        List<Faculty> facultyList = new ArrayList<>();
        List<BookingEntry> bookingList = new ArrayList<>();

        //Creating the Dialog box
        JDialog bookDialog = new JDialog(parent, "Book Slot", Dialog.ModalityType.APPLICATION_MODAL);
        bookDialog.setLayout(null);
        bookDialog.setSize(400,335);
        bookDialog.setResizable(false);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
             facultyList = objectMapper.readValue(new File("src/data/login.json"), new TypeReference<List<Faculty>>() {});
        } catch (Exception e) {
            
            e.printStackTrace();
            System.out.println("An error occured while accessing/reading the login details.");
        }

        objectMapper.registerModule(new JavaTimeModule());
        try {
            bookingList = objectMapper.readValue(new File("src/data/bookings.json"), new TypeReference<List<BookingEntry>>() {});
            System.out.println(bookingList);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occured while accessing/reading the booking details.");
        }

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

        String[] sessions = {"Morning","Afternoon","Evening"};
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

        String[] subjects = {"Computer Architecture","Programming Paradigms","Data Structures and Algorithms","Computer Networks","Mathematical Foundations of CS"};
        JComboBox<String> subjectComboBox = new JComboBox<>(subjects);
        subjectComboBox.setBounds(155,180,175,25);
        bookDialog.add(subjectComboBox);

        bookingStatus = new JLabel("");
        bookingStatus.setBounds(128,210,150,25);
        bookingStatus.setForeground(new Color(24, 130, 14));
        bookDialog.add(bookingStatus);

        JButton submit = new JButton("Book");
        submit.setBounds(155,240,90,25);
        bookDialog.add(submit);

        //Event Listeners
        submit.addActionListener(e -> {
            bookingStatus.setText("Slot Booked Succesfully.");
            BookingManager bookingManager = new BookingManager();
            LocalDate bookingDate = LocalDate.of((Integer)yearComboBox.getSelectedItem(), monthComboBox.getSelectedIndex()+1, (Integer)dayComboBox.getSelectedItem());
            bookingManager.createBookingEntry((String)nameComboBox.getSelectedItem(), (String)subjectComboBox.getSelectedItem(), bookingDate, LocalTime.of((Integer)startTimeComboBox.getSelectedItem(), 0), LocalTime.of((Integer)endTimeComboBox.getSelectedItem(), 0));
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
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         int centerX = (int) (screenSize.getWidth() - bookDialog.getWidth()) / 2;
         int centerY = (int) (screenSize.getHeight() - bookDialog.getHeight()) / 2;
 
         
         bookDialog.setLocation(centerX, centerY);  // Set the window location
         bookDialog.setVisible(true);
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
        dayComboBox.removeAllItems();
        int year = (Integer)yearComboBox.getSelectedItem();
        int month = monthComboBox.getSelectedIndex() + 1;
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();
        for(int day = 1; day<= daysInMonth; day++){
            dayComboBox.addItem(day);
        }
    }
}

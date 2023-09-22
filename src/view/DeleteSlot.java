package view;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalTime;
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

public class DeleteSlot {
    //Global Identifers
    JComboBox<String> nameComboBox;
    JComboBox<String> dateComboBox;
    JComboBox<String> timeComboBox;
    ObjectMapper objectMapper;
    JDialog deleteDialog;
    List<BookingEntry> bookingList = new ArrayList<>();
    BookingManager bookingManager = new BookingManager();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");

    public DeleteSlot(JFrame parent, String facultyName, String privilege, JsonUpdateListener callback){
        //Placed at the top because the try catch block makes the variables local to them.
        List<Faculty> facultyList = new ArrayList<>();

        //Creating the Dialog box
        deleteDialog = new JDialog(parent, "Delete Slot", Dialog.ModalityType.APPLICATION_MODAL);
        deleteDialog.setLayout(null);
        deleteDialog.setSize(400,265);
        deleteDialog.setResizable(false);

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
        deleteDialog.add(name);

        nameComboBox = new JComboBox<>();
        nameComboBox.setBounds(155,40,175,25);
        if(privilege.equals("admin")){
            for(Faculty faculty : facultyList){
            nameComboBox.addItem(faculty.getName());
            }
        }
        else if(privilege.equals("moderator")){
            nameComboBox.addItem(facultyName);
        }
        deleteDialog.add(nameComboBox);

        JLabel date = new JLabel("Date :");
        date.setBounds(70,75,70,25);
        deleteDialog.add(date);

        dateComboBox = new JComboBox<>();
        dateComboBox.setBounds(155,75,175,25);
        updateDateComboBox();
        deleteDialog.add(dateComboBox);

        JLabel time = new JLabel("Time :");
        time.setBounds(70,110,70,25);
        deleteDialog.add(time);

        timeComboBox = new JComboBox<>();
        timeComboBox.setBounds(155,110,175,25);
        updateTimeComboBox();
        deleteDialog.add(timeComboBox);

        JButton submit = new JButton("Delete");
        submit.setBounds(155,160,90,25);
        deleteDialog.add(submit);

        //Event Listeners
        nameComboBox.addActionListener(e -> {
            updateDateComboBox();
        });

        dateComboBox.addActionListener(e -> {
            updateTimeComboBox();
        });

        submit.addActionListener(e -> {
            //If there are no slots booked
            if((String)dateComboBox.getSelectedItem() == null){
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(deleteDialog, "The Slot Is Already Empty.", "Deletion Status", JOptionPane.WARNING_MESSAGE);
                });
            }
            else {
                //StringTokenizer is used to seperate the time from the string obtained from timeComboBox, which contains string like "09:00 to 10:00" .The seperated strings can be accessed using .nextToken() method.
                StringTokenizer timeString = new StringTokenizer((String)timeComboBox.getSelectedItem()," to ");
                LocalTime startTime = LocalTime.parse(timeString.nextToken());
                LocalTime endTime = LocalTime.parse(timeString.nextToken());
                LocalDate deleteDate = LocalDate.parse((String)dateComboBox.getSelectedItem(), formatter);

                bookingManager.deleteBookingEntry((String)nameComboBox.getSelectedItem(), deleteDate, startTime, endTime);

                bookingList = bookingManager.readBookingData();
                updateDateComboBox();
                updateTimeComboBox();
                callback.updateCalendarView(deleteDate); //Updates the calendar view after deleting a slot
                
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(deleteDialog, "Slot Deleted Succesfully", "Deletion Status", JOptionPane.INFORMATION_MESSAGE);
                });
            }
        });

        // Calculate the center coordinates
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (int) (screenSize.getWidth() - deleteDialog.getWidth()) / 2;
        int centerY = (int) (screenSize.getHeight() - deleteDialog.getHeight()) / 2;

        deleteDialog.setLocation(centerX, centerY);  // Set the window location
        deleteDialog.setVisible(true);
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
}

//Pending works: Add session details on the timeComboBox and update time fetching accordingly.
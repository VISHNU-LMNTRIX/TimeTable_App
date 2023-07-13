package view;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class BookSlot {
    public BookSlot(JFrame parent){
        JDialog bookDialog = new JDialog(parent, "Book Slot", Dialog.ModalityType.APPLICATION_MODAL);
        bookDialog.setLayout(null);
        bookDialog.setSize(400,330);
        bookDialog.setResizable(false);

        // Add the components
        JLabel name = new JLabel("Name :");
        name.setBounds(70,40,70,25);
        bookDialog.add(name);

        JComboBox<String> nameComboBox = new JComboBox<>();
        nameComboBox.setBounds(155,40,175,25);
        bookDialog.add(nameComboBox);

        JLabel date = new JLabel("Date :");
        date.setBounds(70,75,70,25);
        bookDialog.add(date);

        JComboBox<Integer> dayComboBox = new JComboBox<>();
        dayComboBox.setBounds(155,75,50,25);
        bookDialog.add(dayComboBox);

        JComboBox<Integer> monthComboBox = new JComboBox<>();
        monthComboBox.setBounds(210,75,55,25);
        bookDialog.add(monthComboBox);

        JComboBox<Integer> yearComboBox = new JComboBox<>();
        yearComboBox.setBounds(270,75,60,25);
        bookDialog.add(yearComboBox);

        JLabel session = new JLabel("Session :");
        session.setBounds(70,110,70,25);
        bookDialog.add(session);

        JComboBox<String> sessionComboBox = new JComboBox<>();
        sessionComboBox.setBounds(155,110,175,25);
        bookDialog.add(sessionComboBox);

        JLabel time = new JLabel("Time :");
        time.setBounds(70,145,70,25);
        bookDialog.add(time);

        JComboBox<Integer> startTimeComboBox = new JComboBox<>();
        startTimeComboBox.setBounds(155,145,75,25);
        bookDialog.add(startTimeComboBox);
        
        JLabel to = new JLabel("to");
        to.setBounds(238,145,15,25);
        bookDialog.add(to);

        JComboBox<Integer> endTimeComboBox = new JComboBox<>();
        endTimeComboBox.setBounds(255,145,75,25);
        bookDialog.add(endTimeComboBox);

        JLabel subject = new JLabel("Subject");
        subject.setBounds(70,180,70,25);
        bookDialog.add(subject);

        JComboBox<String> subjectComboBox = new JComboBox<>();
        subjectComboBox.setBounds(155,180,175,25);
        bookDialog.add(subjectComboBox);

        JButton submit = new JButton("Book");
        submit.setBounds(155,235,90,25);
        bookDialog.add(submit);

         // Calculate the center coordinates
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         int centerX = (int) (screenSize.getWidth() - bookDialog.getWidth()) / 2;
         int centerY = (int) (screenSize.getHeight() - bookDialog.getHeight()) / 2;
 
         
         bookDialog.setLocation(centerX, centerY);  // Set the window location
         bookDialog.setVisible(true);
    }
}

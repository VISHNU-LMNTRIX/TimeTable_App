package view;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class UpdateSlot {
    public UpdateSlot(JFrame parent){
        JDialog updateDialog = new JDialog(parent, "Update Slot", Dialog.ModalityType.APPLICATION_MODAL);
        updateDialog.setLayout(null);
        updateDialog.setSize(420,360);
        updateDialog.setResizable(false);

        // Add the components
        JLabel name = new JLabel("Name :");
        name.setBounds(70,40,70,25);
        updateDialog.add(name);

        JComboBox<String> nameComboBox = new JComboBox<>();
        nameComboBox.setBounds(175,40,175,25);
        updateDialog.add(nameComboBox);

        JLabel date = new JLabel("Date :");
        date.setBounds(70,75,70,25);
        updateDialog.add(date);

        JComboBox<String> dateComboBox = new JComboBox<>();
        dateComboBox.setBounds(175,75,175,25);
        updateDialog.add(dateComboBox);

        JLabel time = new JLabel("Time :");
        time.setBounds(70,110,70,25);
        updateDialog.add(time);

        JComboBox<String> timeComboBox = new JComboBox<>();
        timeComboBox.setBounds(175,110,175,25);
        updateDialog.add(timeComboBox);

        JLabel newDate = new JLabel("New Date :");
        newDate.setBounds(70,145,70,25);
        updateDialog.add(newDate);

        JComboBox<Integer> newDayComboBox = new JComboBox<>();
        newDayComboBox.setBounds(175,145,50,25);
        updateDialog.add(newDayComboBox);

        JComboBox<String> newMonthComboBox = new JComboBox<>();
        newMonthComboBox.setBounds(230,145,55,25);
        updateDialog.add(newMonthComboBox);

        JComboBox<Integer> newYearComboBox = new JComboBox<>();
        newYearComboBox.setBounds(290,145,60,25);
        updateDialog.add(newYearComboBox);

        JLabel session = new JLabel("New Session :");
        session.setBounds(70,180,80,25);
        updateDialog.add(session);

        JComboBox<String> sessionComboBox = new JComboBox<>();
        sessionComboBox.setBounds(175,180,175,25);
        updateDialog.add(sessionComboBox);

        JLabel newTime = new JLabel("New Time :");
        newTime.setBounds(70,215,80,25);
        updateDialog.add(newTime);

        JComboBox<Integer> startTimeComboBox = new JComboBox<>();
        startTimeComboBox.setBounds(175,215,75,25);
        updateDialog.add(startTimeComboBox);
        
        JLabel to = new JLabel("to");
        to.setBounds(258,215,15,25);
        updateDialog.add(to);

        JComboBox<Integer> endTimeComboBox = new JComboBox<>();
        endTimeComboBox.setBounds(275,215,75,25);
        updateDialog.add(endTimeComboBox);

        JButton submit = new JButton("Update");
        submit.setBounds(165,270,90,25);
        updateDialog.add(submit);

         // Calculate the center coordinates
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         int centerX = (int) (screenSize.getWidth() - updateDialog.getWidth()) / 2;
         int centerY = (int) (screenSize.getHeight() - updateDialog.getHeight()) / 2;
 
         
         updateDialog.setLocation(centerX, centerY);  // Set the window location
         updateDialog.setVisible(true);
    }
}

package view;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class DeleteSlot {
    public DeleteSlot(JFrame parent){
        JDialog deleteDialog = new JDialog(parent, "Delete Slot", Dialog.ModalityType.APPLICATION_MODAL);
        deleteDialog.setLayout(null);
        deleteDialog.setSize(400,265);
        deleteDialog.setResizable(false);

        // Add the components
        JLabel name = new JLabel("Name :");
        name.setBounds(70,40,70,25);
        deleteDialog.add(name);

        JComboBox<String> nameComboBox = new JComboBox<>();
        nameComboBox.setBounds(155,40,175,25);
        deleteDialog.add(nameComboBox);

        JLabel date = new JLabel("Date :");
        date.setBounds(70,75,70,25);
        deleteDialog.add(date);

        JComboBox<String> dateComboBox = new JComboBox<>();
        dateComboBox.setBounds(155,75,175,25);
        deleteDialog.add(dateComboBox);

        JLabel time = new JLabel("Time :");
        time.setBounds(70,110,70,25);
        deleteDialog.add(time);

        JComboBox<Integer> timeComboBox = new JComboBox<>();
        timeComboBox.setBounds(155,110,175,25);
        deleteDialog.add(timeComboBox);

        JButton submit = new JButton("Delete");
        submit.setBounds(155,160,90,25);
        deleteDialog.add(submit);

         // Calculate the center coordinates
         Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
         int centerX = (int) (screenSize.getWidth() - deleteDialog.getWidth()) / 2;
         int centerY = (int) (screenSize.getHeight() - deleteDialog.getHeight()) / 2;
 
         
         deleteDialog.setLocation(centerX, centerY);  // Set the window location
         deleteDialog.setVisible(true);
    }
}

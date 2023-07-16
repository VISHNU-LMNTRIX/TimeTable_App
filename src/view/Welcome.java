package view;

import java.awt.Toolkit;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JFrame;

public class Welcome {
    public Welcome(){
        JFrame frame = new JFrame();
        frame.setTitle("Welcome");
        frame.setLayout(null);
        frame.setSize(400,250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        JButton studentButton = new JButton("Student");
        JButton facultyButton = new JButton("Faculty");

        studentButton.setBounds(100,55,200,35);
        facultyButton.setBounds(100,125,200,35);
        studentButton.setFocusable(false);
        facultyButton.setFocusable(false);

        frame.add(studentButton);
        frame.add(facultyButton);

        // Action Listener for the studentButton
        studentButton.addActionListener(e -> {
            System.out.println("Student logged in.");
            new Timetable("Student", "none");
            frame.dispose();
        });

        // Action Listener for the facultyButton
        facultyButton.addActionListener(e -> {
            System.out.println("Faculty trying to login...");
            new Login();
            frame.dispose();
        });

        // Calculate the center coordinates
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (int) (screenSize.getWidth() - frame.getWidth()) / 2;
        int centerY = (int) (screenSize.getHeight() - frame.getHeight()) / 2;

        
        frame.setLocation(centerX, centerY);    // Set the window location
        frame.setVisible(true);
    }
    
}

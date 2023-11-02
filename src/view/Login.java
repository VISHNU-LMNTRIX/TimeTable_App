package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import core.Faculty;

public class Login {
    private List<Faculty> facultyList;
    private JComboBox<String> userNameComboBox;
    private JPasswordField passwordField;
    private JFrame frame;
    private JLabel passwordStatusLabel;

    Login(){
        frame = new JFrame();
        frame.setTitle("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(null);
        frame.setSize(400,250);


        //Reading data from login.json
        ObjectMapper objectMapper = new ObjectMapper();
        try (FileInputStream fileInputStream = new FileInputStream("src/data/login.json"); 
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream); 
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            facultyList = objectMapper.readValue(bufferedReader, new TypeReference<List<Faculty>>(){});
        } catch(IOException e) {
            e.printStackTrace();
            System.out.println("An error occurred while accessing/reading the login details.");
        }

        
        //Declaring the components
        JLabel userNameLabel = new JLabel("Username :");
        userNameComboBox = new JComboBox<String>();
        JLabel passwordLabel = new JLabel("Password :");
        passwordField = new JPasswordField();
        passwordStatusLabel = new JLabel("");
        JButton backButton = new JButton("Back"); 
        JButton loginButton = new JButton("Login");


        //Adding items to the userNameComboBox
        for(Faculty faculty : facultyList){
            userNameComboBox.addItem(faculty.getName());
        }


        //Action listeners
        backButton.addActionListener(e -> onBack());
        loginButton.addActionListener(e -> onLogin());

        
        //Position and Size of the components
        userNameLabel.setBounds(50,50,100,25);
        userNameComboBox.setBounds(150,50,200,25);
        passwordLabel.setBounds(50,95,100,25);
        passwordField.setBounds(150,95,200,25);
        passwordStatusLabel.setBounds(155,123,195,15);
        backButton.setBounds(50,145,150,25);
        loginButton.setBounds(200,145,150,25);
        
        
        //Adding Components to the frame
        frame.add(userNameLabel);
        frame.add(userNameComboBox);
        frame.add(passwordLabel);
        frame.add(passwordField);
        frame.add(passwordStatusLabel);
        frame.add(backButton);
        frame.add(loginButton);

        // Calculate the center coordinates
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (int) (screenSize.getWidth() - frame.getWidth()) / 2;
        int centerY = (int) (screenSize.getHeight() - frame.getHeight()) / 2;

        frame.setLocation(centerX, centerY);    // Set the window location
        frame.setVisible(true);
        
    }

    private void onLogin(){
        String selectedName = (String) userNameComboBox.getSelectedItem();
        String password = String.valueOf(passwordField.getPassword());

        for(Faculty faculty : facultyList){
            if(faculty.getName().equals(selectedName) && faculty.getPassword().equals(password)){
                //call TimeTable and pass the name and privilege
                SwingUtilities.invokeLater(() -> {
                    new Timetable(selectedName, faculty.getPrivilege());
                    frame.dispose();
                });
                return;

            }
        }

        //Password doesn't match
        passwordStatusLabel.setForeground(Color.RED);
        passwordStatusLabel.setText("Incorrect Password.");
    }

    private void onBack(){
        SwingUtilities.invokeLater(() -> {
            new Welcome();
            frame.dispose();
        });
    }
}

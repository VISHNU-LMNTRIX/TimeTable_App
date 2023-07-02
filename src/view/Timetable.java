package view;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import java.awt.Toolkit;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;

public class Timetable {
    Timetable(String name,String privilege){
        JFrame frame = new JFrame();
        frame.setTitle("TimeTable 2023 by Vishnu and Drushti");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100,650);

        // MenuBar =========================================================>

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu viewMenu = new JMenu("View");
        JMenu toolsMenu = new JMenu("Tools");
        JMenu helpMenu = new JMenu("Help");
        JMenuItem fileMenuItemLogout = new JMenuItem("Logout");
        JMenuItem fileMenuItemExit = new JMenuItem("Exit");
        JMenuItem viewMenuItemDay = new JMenuItem("Day View");
        JMenuItem viewMenuItemWeek = new JMenuItem("Week View");
        JMenuItem viewMenuItemMonth = new JMenuItem("Month View");
        JMenuItem toolsMenuItemGetReport = new JMenuItem("Get Report");
        JMenuItem helpMenuItemAbout = new JMenuItem("About");

        fileMenu.add(fileMenuItemLogout);
        fileMenu.add(fileMenuItemExit);

        viewMenu.add(viewMenuItemDay);
        viewMenu.add(viewMenuItemWeek);
        viewMenu.add(viewMenuItemMonth);

        toolsMenu.add(toolsMenuItemGetReport);

        helpMenu.add(helpMenuItemAbout);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);

        menuBar.setPreferredSize(new Dimension(1100,20));

        // Event Listeners
        fileMenuItemLogout.addActionListener(e -> {
            System.out.println("Successfully Logged out.");
            new Welcome();
            frame.dispose();
        });

        fileMenuItemExit.addActionListener(e -> {
            System.out.println("Exiting out of Application");
            System.exit(0);
        });

        // MainPanel ========================================================>

        JPanel mainPanel = new JPanel(); // holds the calendar view and footer.
        mainPanel.setLayout(new BorderLayout());

        JPanel calendarPanel = new JPanel();
        calendarPanel.setLayout(new CardLayout());
        // calendarPanel.setBackground(Color.RED);

        JPanel footerPanel = new JPanel();
        // footerPanel.setBackground(Color.ORANGE);
        footerPanel.setPreferredSize(new Dimension(800,40));

        mainPanel.add(calendarPanel,BorderLayout.CENTER);
        mainPanel.add(footerPanel,BorderLayout.SOUTH);

        // SidePanel ========================================================>

        JPanel sidePanel = new JPanel();
        sidePanel.setPreferredSize(new Dimension(300, 0));
        sidePanel.setBackground(new Color(0, 93, 170));

        //===================================================================>

        // Sets window location to centre
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (int) (screenSize.getWidth() - frame.getWidth()) / 2;
        int centerY = (int) (screenSize.getHeight() - frame.getHeight()) / 2;
        frame.setLocation(centerX, centerY);

        // Add components to frame
        frame.setJMenuBar(menuBar);
        frame.add(mainPanel,BorderLayout.CENTER);
        frame.add(sidePanel,BorderLayout.EAST);
        
        frame.setVisible(true);
    }
}

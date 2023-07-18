package view;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.Toolkit;
import java.time.LocalDate;
import java.time.YearMonth;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

public class Timetable {
    //Global identifiers
    JPanel monthViewMainPanel;
    JPanel monthViewPanelDays;
    JPanel monthViewHeadingPanel;
    static JComboBox<String> monthComboBox;
    static JComboBox<Integer> yearComboBox;

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

        // MainPanel ========================================================>

        // mainPanel contains the calendar view and the buttons to book, update and delete slots.
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // calendarPanel holds the calendar heading and calendar view.
        JPanel calendarPanel = new JPanel();
        CardLayout cardLayoutObject = new CardLayout();
        calendarPanel.setLayout(cardLayoutObject);
        JPanel dayPanel = new JPanel();
        JPanel weekPanel = new JPanel();
        JPanel monthPanel = new JPanel();
        dayPanel.setLayout(new BorderLayout());
        weekPanel.setLayout(new BorderLayout());
        monthPanel.setLayout(new BorderLayout());
        calendarPanel.add(dayPanel, "dayPanel");
        calendarPanel.add(weekPanel, "weekPanel");
        calendarPanel.add(monthPanel, "monthPanel");
        // default view of calendarPanel
        cardLayoutObject.show(calendarPanel, "monthPanel");
        mainPanel.add(calendarPanel,BorderLayout.CENTER);

        //monthPanel
        monthViewHeadingPanel = new JPanel(); // Holds the heading (like "July 2023") and date chooser.
        monthViewHeadingPanel.setPreferredSize(new Dimension(0, 80));
        monthViewMainPanel = new JPanel(); // Holds the week headings and the total days of the month
        monthViewMainPanel.setLayout(new BorderLayout());
        monthPanel.add(monthViewHeadingPanel, BorderLayout.NORTH);
        monthPanel.add(monthViewMainPanel, BorderLayout.CENTER);
        
        JPanel monthViewPanelWeeks = new JPanel(new GridLayout(1, 7));
        
        String[] weeks = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
        for(String week : weeks)
        {
            JLabel label = new JLabel(week);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            if(week.equals("Sunday")){
                label.setForeground(Color.RED);
            }
            monthViewPanelWeeks.add(label);
        }
        monthViewPanelWeeks.setBackground(Color.WHITE);
        monthViewPanelWeeks.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(200,200,200)));
        monthViewPanelWeeks.setPreferredSize(new Dimension(0, 25));

        monthViewPanelDays = new JPanel(); // Holds the total days of the month.
        monthViewPanelDays.setLayout(new GridLayout(0, 7));
        updateMonthCalendar(LocalDate.now());

        monthViewMainPanel.add(monthViewPanelWeeks, BorderLayout.NORTH);
        monthViewMainPanel.add(monthViewPanelDays, BorderLayout.CENTER);

        if (privilege.equals("admin") || privilege.equals("moderator")) {
            //-------Footer Panel
            JPanel footerPanel = new JPanel();
            footerPanel.setPreferredSize(new Dimension(800,35));
            footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200,200,200)));
            footerPanel.setLayout(new GridLayout(1,3));
            JPanel bookSlotPanel = new JPanel();
            JPanel updateSlotPanel = new JPanel();
            JPanel deleteSlotPanel = new JPanel();
            JPanel[] footerPanels = {bookSlotPanel, updateSlotPanel, deleteSlotPanel};
            JButton bookSlotBtn = new JButton("Book Slot");
            JButton updateSlotBtn = new JButton("Update Slot");
            JButton deleteSlotBtn = new JButton("Delete Slot");
            JButton[] footerButtons = {bookSlotBtn, updateSlotBtn, deleteSlotBtn};

            for(JPanel footerSubPanel : footerPanels)
            {
                footerSubPanel.setBackground(new Color(240,240,240));
                footerPanel.add(footerSubPanel);
            }

            for(JButton footerButton : footerButtons)
            {
                footerButton.setFocusable(false);
            }

            bookSlotPanel.add(bookSlotBtn);
            updateSlotPanel.add(updateSlotBtn);
            deleteSlotPanel.add(deleteSlotBtn);
            mainPanel.add(footerPanel,BorderLayout.SOUTH);

            //Event Listeners for Footer Buttons
            bookSlotBtn.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> {
                    new BookSlot(frame, name, privilege);
                });
            });

            updateSlotBtn.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> {
                    new UpdateSlot(frame, name, privilege);
                });
            });

            deleteSlotBtn.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> {
                    new DeleteSlot(frame, name, privilege);
                });
            });
        }

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

        // Event Listeners

        //-------MenuBar
        fileMenuItemLogout.addActionListener(e -> {
            System.out.println("Successfully Logged out.");
            SwingUtilities.invokeLater(() -> {
                new Welcome();
            });
            frame.dispose();
        });

        fileMenuItemExit.addActionListener(e -> {
            System.out.println("Exiting out of Application");
            System.exit(0);
        });

        viewMenuItemMonth.addActionListener(e -> {
            cardLayoutObject.show(calendarPanel, "monthPanel");
        });

        viewMenuItemWeek.addActionListener(e -> {
            cardLayoutObject.show(calendarPanel, "weekPanel");
        });

        viewMenuItemDay.addActionListener(e -> {
            cardLayoutObject.show(calendarPanel, "dayPanel");
        });

        // Add components to frame
        frame.setJMenuBar(menuBar);
        frame.add(mainPanel,BorderLayout.CENTER);
        frame.add(sidePanel,BorderLayout.EAST);
        
        frame.setVisible(true);
    }

    private void updateMonthCalendar(LocalDate date) {
        YearMonth yearMonth = YearMonth.from(date);

        // MonthView Heading Panel -------------------------------------------
        monthViewHeadingPanel.removeAll();
        monthViewHeadingPanel.setLayout(new BorderLayout());

        JPanel monthYearHeading = new JPanel(new BorderLayout());
        JLabel monthYearLabel = new JLabel(yearMonth.getMonth().toString() + " " + yearMonth.getYear());
        monthYearLabel.setFont(new Font("Bitstream Charter",Font.PLAIN,34));
        monthYearLabel.setHorizontalAlignment(JLabel.CENTER);
        monthYearHeading.add(monthYearLabel);

        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep",
                "Oct", "Nov", "Dec"};

        Integer[] years = {2021, 2022, 2023, 2024, 2025};

        JPanel monthYearChooser = new JPanel();
        monthComboBox = new JComboBox<>(months);
        yearComboBox = new JComboBox<>(years);
        String monthName = yearMonth.getMonth().name().toLowerCase();
        monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1,3);
        monthComboBox.setSelectedItem(monthName);
        yearComboBox.setSelectedItem(Integer.valueOf(yearMonth.getYear()));
        monthComboBox.setFocusable(false);
        yearComboBox.setFocusable(false);
        monthYearChooser.add(monthComboBox);
        monthYearChooser.add(yearComboBox);

        monthViewHeadingPanel.add(monthYearHeading, BorderLayout.CENTER);
        monthViewHeadingPanel.add(monthYearChooser, BorderLayout.EAST);

        // Event Listeners
        monthComboBox.addActionListener(e -> {
            LocalDate newDate = LocalDate.of((int)yearComboBox.getSelectedItem(),monthComboBox.getSelectedIndex()+1,1);
            updateMonthCalendar(newDate);
        });

        yearComboBox.addActionListener(e -> {
            LocalDate newDate = LocalDate.of((int)yearComboBox.getSelectedItem(),monthComboBox.getSelectedIndex()+1,1);
            updateMonthCalendar(newDate);
        });

        //--------------------------------------------------------------------

        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        int startDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue();
        int numberOfDays = yearMonth.lengthOfMonth();

        monthViewPanelDays.removeAll();
        monthViewPanelDays.setBackground(Color.WHITE);
        // If startDayOfWeek is Sunday, Empty label not required.
        if(startDayOfWeek != 7){
            for (int i = 1; i <= startDayOfWeek; i++) {
                monthViewPanelDays.add(new JLabel(""));
            }
        }

        for (int i = 1; i <= numberOfDays; i++) {
            JPanel dayPanel = new JPanel();
            JLabel dayLabel = new JLabel(Integer.toString(i));
            dayPanel.setBackground(Color.WHITE);
            if (yearMonth.getMonthValue() == LocalDate.now().getMonthValue() && i == LocalDate.now().getDayOfMonth()) {
                dayLabel.setForeground(Color.RED);
                dayPanel.setBackground(new Color(230,230,230));
            }
            dayPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(216,216,216)));
            dayPanel.add(dayLabel);
            monthViewPanelDays.add(dayPanel);
        }
        monthViewPanelDays.revalidate();
        monthViewPanelDays.repaint();

    }

    public static String getSelectedMonth() {
        return (String)monthComboBox.getSelectedItem();
    }

    public static Integer getSelectedYear() {
        return (Integer)yearComboBox.getSelectedItem();
    } 

}

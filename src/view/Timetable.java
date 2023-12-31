package view;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.event.MouseEvent;


import core.BookingEntry;
import core.BookingManager;
import core.CustomFontUtil;
import core.JsonUpdateListener;

import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

public class Timetable implements JsonUpdateListener{
    // Global Identifiers ===================================================>
    // Fonts
    Font interMediumForEvents;
    Font interLightForHeading;
    Font sidePanelWeekDayFont;
    Font sidePanelDateFont;
    Font sidePanelHeadingFont;
    Font sidePanelHeadings;
    Font sidePanelSubjectFacultyFont;
    // MonthPanel
    JPanel monthViewMainPanel;
    JPanel monthViewPanelDays;
    JPanel monthViewHeadingPanel;
    static JComboBox<String> monthComboBox;
    static JComboBox<Integer> yearComboBox;
    // Side Panel
    JPanel sidePanel;
    JLabel sidePanelDateLabel;
    // Miscellaneous
    BookingManager bookingManager = new BookingManager();
    List<BookingEntry> bookingList = new ArrayList<>();
    private final Map<String, String> subjectShortNames = new HashMap<>();
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H");
    DateTimeFormatter sidePanelTimeFormatter = DateTimeFormatter.ofPattern("H:mm");
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy");

    //=======================================================================>

    Timetable(String name,String privilege){

        //Custom fonts used within the application
        interMediumForEvents = CustomFontUtil.loadFont("src/resources/fonts/Inter-Medium.otf", 12);
        interLightForHeading = CustomFontUtil.loadFont("src/resources/fonts/Inter-Light.otf", 30);
        sidePanelWeekDayFont = CustomFontUtil.loadFont("src/resources/fonts/Inter-Light.otf", 30);
        sidePanelDateFont = CustomFontUtil.loadFont("src/resources/fonts/Inter-Medium.otf", 15);
        sidePanelHeadingFont = CustomFontUtil.loadFont("src/resources/fonts/Inter-Bold.otf", 13);
        sidePanelSubjectFacultyFont = CustomFontUtil.loadFont("src/resources/fonts/Inter-SemiBold.otf", 13);

        JFrame frame = new JFrame();
        frame.setTitle("TimeTable 2023 by Vishnu and Drushti");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100,650);

        //Initialize the subjectShortNames here:
        subjectShortNames.put("Computer Networks", "CN");
        subjectShortNames.put("Programming Paradigms", "PP");
        subjectShortNames.put("Computer Architecture", "CA");
        subjectShortNames.put("Mathematical Foundns of CS", "MF");
        subjectShortNames.put("Data Structures & Algorithms", "DS");

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
        if(privilege.equals("admin") || privilege.equals("moderator")){
            menuBar.add(toolsMenu);
        }
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
        monthViewHeadingPanel.setPreferredSize(new Dimension(0, 50));
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
        updateMonthPanel(LocalDate.now());

        monthViewMainPanel.add(monthViewPanelWeeks, BorderLayout.NORTH);
        monthViewMainPanel.add(monthViewPanelDays, BorderLayout.CENTER);

        // FooterPanel ========================================================>
        
        if (privilege.equals("admin") || privilege.equals("moderator")) {
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
                    new BookSlot(frame, name, privilege, this);
                });
            });

            updateSlotBtn.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> {
                    new UpdateSlot(frame, name, privilege, this);
                });
            });

            deleteSlotBtn.addActionListener(e -> {
                SwingUtilities.invokeLater(() -> {
                    new DeleteSlot(frame, name, privilege, this);
                });
            });
        }

        // SidePanel ========================================================>

        sidePanel = new JPanel();
        sidePanel.setPreferredSize(new Dimension(300, 0));
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.PAGE_AXIS));
        sidePanel.setBackground(Color.WHITE); // Backup color: new Color(0, 93, 170)
        sidePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 2, 0, 0, new Color(216, 216, 216)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        updateSidePanel(LocalDate.now());

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
                frame.dispose();
            });
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

        helpMenuItemAbout.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Streamlined Scheduling Made Easy: A Brainchild of Vishnu Prasad and Drushti.\n\nWelcome to a smarter way of managing timetables. Our application empowers faculties to book and     \nadapt slots based on their availability, fostering a dynamic scheduling environment. Students gain\ninsight into booked slots and associated subjects, facilitating better planning and collaboration.\n\n", "About", JOptionPane.INFORMATION_MESSAGE);
        });

        // Add components to frame
        frame.setJMenuBar(menuBar);
        frame.add(mainPanel,BorderLayout.CENTER);
        frame.add(sidePanel,BorderLayout.EAST);
        
        frame.setVisible(true);
    }

    private void updateSidePanel(LocalDate currentDate) {
        sidePanel.removeAll();

        String weekDay = currentDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        JLabel sidePanelWeekDayLabel = new JLabel(weekDay);
        sidePanelWeekDayLabel.setFont(sidePanelWeekDayFont);

        String formattedDate = currentDate.format(dateFormatter);
        sidePanelDateLabel = new JLabel(formattedDate);
        sidePanelDateLabel.setFont(sidePanelDateFont);

        sidePanelWeekDayLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidePanelDateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidePanel.add(sidePanelWeekDayLabel);
        sidePanel.add(sidePanelDateLabel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 15)));
        updateCurrentDayEvents(currentDate);
        sidePanel.add(Box.createRigidArea(new Dimension(0,25)));
        updateNextWeekEvents(currentDate);

        sidePanel.revalidate();
        sidePanel.repaint();
    }

    private void updateCurrentDayEvents(LocalDate currentDate) {
        JLabel todayLabel = new JLabel("Events of the day");
        todayLabel.setFont(sidePanelHeadingFont);
        todayLabel.setForeground(new Color(85, 85, 85));
        todayLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        sidePanel.add(todayLabel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 7)));

        JPanel currentDayEventPanel = new JPanel();
        currentDayEventPanel.setLayout(new BoxLayout(currentDayEventPanel, BoxLayout.PAGE_AXIS));
        currentDayEventPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        currentDayEventPanel.setBackground(Color.WHITE);

        List<BookingEntry> bookingsForDay = getBookingsForDay(currentDate);

        for (BookingEntry booking : bookingsForDay) {
            JPanel bookingPanel = new JPanel();
            bookingPanel.setLayout(new BoxLayout(bookingPanel, BoxLayout.PAGE_AXIS));
            bookingPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            setSidePanelBookingPanelDimensions(bookingPanel);
            
            String startTimeStr = booking.getStartTime().format(sidePanelTimeFormatter);
            String endTimeStr = booking.getEndTime().format(sidePanelTimeFormatter);
            JLabel eventSubjectFacultyLabel = new JLabel(booking.getSubject() + " - " + booking.getFacultyName());
            JLabel eventSessionTimeLabel = new JLabel(booking.getSession() + " - " + startTimeStr + " to " + endTimeStr);
            eventSubjectFacultyLabel.setFont(sidePanelSubjectFacultyFont);
            eventSessionTimeLabel.setFont(interMediumForEvents);
            bookingPanel.add(eventSubjectFacultyLabel);
            bookingPanel.add(eventSessionTimeLabel);

            setSidePanelBookingPanelColors(booking, eventSubjectFacultyLabel, eventSessionTimeLabel, bookingPanel);

            bookingPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            currentDayEventPanel.add(bookingPanel);
            currentDayEventPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        sidePanel.add(currentDayEventPanel);
    }

    private void updateNextWeekEvents(LocalDate sidePanelDate) {
        JLabel nextWeekLabel = new JLabel("Next Week");
        nextWeekLabel.setFont(sidePanelHeadingFont);
        nextWeekLabel.setForeground(new Color(85, 85, 85));
        nextWeekLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        sidePanel.add(nextWeekLabel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 7)));

        JPanel nextWeekEventPanel = new JPanel();
        nextWeekEventPanel.setLayout(new BoxLayout(nextWeekEventPanel, BoxLayout.PAGE_AXIS));
        nextWeekEventPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        nextWeekEventPanel.setBackground(Color.WHITE);

        for (int i = 1; i <= 6; i++) {
            LocalDate currentDate = sidePanelDate.plusDays(i);

            List<BookingEntry> bookingsForDay = getBookingsForDay(currentDate);

            for (BookingEntry booking : bookingsForDay) {
                JPanel bookingPanel = new JPanel();
                bookingPanel.setLayout(new BoxLayout(bookingPanel, BoxLayout.PAGE_AXIS));
                bookingPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                setSidePanelBookingPanelDimensions(bookingPanel);
                
                String startTimeStr = booking.getStartTime().format(sidePanelTimeFormatter);
                String endTimeStr = booking.getEndTime().format(sidePanelTimeFormatter);
                String weekDayStr = booking.getDate().format(DateTimeFormatter.ofPattern("E, MMM d"));
                JLabel eventSubjectFacultyLabel = new JLabel(booking.getSubject() + " - " + booking.getFacultyName());
                JLabel eventSessionTimeLabel = new JLabel(weekDayStr + " : " + booking.getSession() + " - " + startTimeStr + " to " + endTimeStr);
                eventSubjectFacultyLabel.setFont(sidePanelSubjectFacultyFont);
                eventSessionTimeLabel.setFont(interMediumForEvents);
                bookingPanel.add(eventSubjectFacultyLabel);
                bookingPanel.add(eventSessionTimeLabel);

                setSidePanelBookingPanelColors(booking, eventSubjectFacultyLabel, eventSessionTimeLabel, bookingPanel);

                bookingPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                nextWeekEventPanel.add(bookingPanel);
                nextWeekEventPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        sidePanel.add(nextWeekEventPanel);
    }

    private void setSidePanelBookingPanelDimensions(JPanel bookingPanel) {
        bookingPanel.setMinimumSize(new Dimension(270,50));
        bookingPanel.setPreferredSize(new Dimension(270,55));
        bookingPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 55));
    }

    private void setSidePanelBookingPanelColors(BookingEntry booking, JLabel subjectLabel, JLabel timeLabel, JPanel bookingPanel) {
        if(booking.getSession().equals("Forenoon")){
            subjectLabel.setForeground(new Color(60, 80, 43));
            timeLabel.setForeground(new Color(60, 80, 43));
            bookingPanel.setBackground(new Color(217, 237, 200));
        }
        else if(booking.getSession().equals("Afternoon")){
            subjectLabel.setForeground(new Color(60, 62, 159));
            timeLabel.setForeground(new Color(60, 62, 159));
            bookingPanel.setBackground(new Color(230, 230, 255));
        }
        else{
            subjectLabel.setForeground(new Color(105, 46, 39));
            timeLabel.setForeground(new Color(105, 46, 39));
            bookingPanel.setBackground(new Color(253, 206, 200));
        }
    }

    private void updateMonthPanel(LocalDate date) {
        YearMonth yearMonth = YearMonth.from(date);

        // Updates the heading panel of month view.
        updateMonthViewHeadingPanel(yearMonth);

        // Updates the day panels of the month view.
        updateMonthViewDaysPanel(yearMonth);
    }

    // Function to update the heading panel of month view.
    private void updateMonthViewHeadingPanel(YearMonth yearMonth) {
        monthViewHeadingPanel.removeAll();
        monthViewHeadingPanel.setLayout(new BorderLayout());

        JPanel monthYearHeading = new JPanel(new BorderLayout());
        JLabel monthYearLabel = new JLabel(yearMonth.getMonth().toString() + " " + yearMonth.getYear());
        monthYearLabel.setFont(new Font("Bitstream Charter",Font.PLAIN,34));
        monthYearLabel.setFont(interLightForHeading);
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
            updateMonthPanel(newDate);
        });

        yearComboBox.addActionListener(e -> {
            LocalDate newDate = LocalDate.of((int)yearComboBox.getSelectedItem(),monthComboBox.getSelectedIndex()+1,1);
            updateMonthPanel(newDate);
        });
    }

    // Function to update the day panels of the month view.
    private void updateMonthViewDaysPanel(YearMonth yearMonth) {
        LocalDate firstDayOfMonth = yearMonth.atDay(1);
        int startDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue();
        int numberOfDays = yearMonth.lengthOfMonth();

        monthViewPanelDays.removeAll();
        monthViewPanelDays.setBackground(Color.WHITE);

        // Read the booking data from bookings.json
        bookingList = bookingManager.readBookingData();

        // If startDayOfWeek is Sunday, Empty label not required.
        if(startDayOfWeek != 7){
            for (int i = 1; i <= startDayOfWeek; i++) {
                monthViewPanelDays.add(new JLabel(""));
            }
        }

        for (int i = 1; i <= numberOfDays; i++) {
            JPanel dayPanel = new JPanel();
            dayPanel.setLayout(new BorderLayout());
            dayPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(216,216,216)));

            JPanel dayLabelPanel = new JPanel(); // Holds the day number.
            dayLabelPanel.setBackground(Color.WHITE);
            JLabel dayLabel = new JLabel(Integer.toString(i));
            dayLabelPanel.add(dayLabel);

            if (yearMonth.getMonthValue() == LocalDate.now().getMonthValue() && i == LocalDate.now().getDayOfMonth() && yearMonth.getYear() == LocalDate.now().getYear()) {
                dayLabel.setForeground(new Color(0, 93, 170));
                dayPanel.setBorder(BorderFactory.createMatteBorder(4, 0, 0, 0, new Color(0, 93, 170)));
            }

            JPanel dayEventsPanel = new JPanel(); // Holds the event details
            dayEventsPanel.setLayout(new BoxLayout(dayEventsPanel, BoxLayout.Y_AXIS));
            dayEventsPanel.setBackground(Color.WHITE);
            dayEventsPanel.setBorder(BorderFactory.createEmptyBorder(0,3,3,3));

            // Get the bookings for the current day
            LocalDate currentDate = LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), i);
            List<BookingEntry> bookingsForDay = getBookingsForDay(currentDate);

            for (BookingEntry booking : bookingsForDay) {
                JPanel bookingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                bookingPanel.setMinimumSize(new Dimension(0,15));
                bookingPanel.setPreferredSize(new Dimension(0,18));
                bookingPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
                //get the short forms for subjects, session, startTime and endTime
                String subjectName = subjectShortNames.getOrDefault(booking.getSubject(), booking.getSubject());
                String session = booking.getSession();
                String sessionShortForm = (session.equals("Forenoon")?"FN":(session.equals("Afternoon")?"AN":"EV"));
                String startTimeStr = booking.getStartTime().format(timeFormatter);
                String endTimeStr = booking.getEndTime().format(timeFormatter);
                JLabel bookingLabel = new JLabel(subjectName + " : " + startTimeStr + " to " + endTimeStr + " " + sessionShortForm);
                bookingLabel.setFont(interMediumForEvents);
                bookingPanel.add(bookingLabel);

                // Background colour of events
                if(booking.getSession().equals("Forenoon")){
                    bookingLabel.setForeground(new Color(60, 80, 43));
                    bookingPanel.setBackground(new Color(217, 237, 200));
                }
                else if(booking.getSession().equals("Afternoon")){
                    bookingLabel.setForeground(new Color(60, 62, 159));
                    bookingPanel.setBackground(new Color(230, 230, 255));
                }
                else{
                    bookingLabel.setForeground(new Color(105, 46, 39));
                    bookingPanel.setBackground(new Color(253, 206, 200));
                }
                dayEventsPanel.add(bookingPanel);
                dayEventsPanel.add(Box.createRigidArea(new Dimension(0, 3)));
            }

            dayPanel.add(dayLabelPanel, BorderLayout.NORTH);
            dayPanel.add(dayEventsPanel, BorderLayout.CENTER);
            monthViewPanelDays.add(dayPanel);

            // Event Listeners for dayPanel
            dayPanel.addMouseListener(new MouseAdapter() {
                private Color originalBackgroundColor = dayLabelPanel.getBackground();

                @Override
                public void mouseEntered(MouseEvent e) {
                    dayLabelPanel.setBackground(new Color(238, 238, 238));
                    dayEventsPanel.setBackground(new Color(238, 238, 238));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    dayLabelPanel.setBackground(originalBackgroundColor);
                    dayEventsPanel.setBackground(originalBackgroundColor);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    int day = Integer.parseInt(dayLabel.getText());
                    updateSidePanel(yearMonth.atDay(day));
                }
            });
        }
        monthViewPanelDays.revalidate();
        monthViewPanelDays.repaint();
    }

    // Method to get the bookings for a specific day
    private List<BookingEntry> getBookingsForDay(LocalDate date) {
        List<BookingEntry> bookingsForDay = new ArrayList<>();
        for (BookingEntry booking : bookingList) {
            if (date.equals(booking.getDate())) {
                bookingsForDay.add(booking);
            }
        }
        // Sort the bookings first by session (forenoon -> afternoon -> evening) and then by startTime
            bookingsForDay.sort((b1, b2) -> {
                String session1 = b1.getSession();
                String session2 = b2.getSession();
                int sessionOrder1 = getSessionOrder(session1);
                int sessionOrder2 = getSessionOrder(session2);

                if (sessionOrder1 != sessionOrder2) {
                    return Integer.compare(sessionOrder1, sessionOrder2);
                }

                LocalTime startTime1 = b1.getStartTime();
                LocalTime startTime2 = b2.getStartTime();
                return startTime1.compareTo(startTime2);
            });

        return bookingsForDay;
    }

    // Method to determine the session order (forenoon: 1, afternoon: 2, evening: 3)
    private int getSessionOrder(String session) {
        switch (session) {
            case "Forenoon":
                return 1;
            case "Afternoon":
                return 2;
            case "Evening":
                return 3;
            default:
                return 0; // Use 0 for any other cases (to handle unexpected data)
        }
    }

    public static String getSelectedMonth() {
        return (String)monthComboBox.getSelectedItem();
    }

    public static Integer getSelectedYear() {
        return (Integer)yearComboBox.getSelectedItem();
    }

    // Method used as a callback to update the calendar view after slot booking/deletion.
    @Override
    public void updateCalendarView(LocalDate selectedDate) {
        YearMonth yearMonth = YearMonth.of((int)yearComboBox.getSelectedItem(), (int)monthComboBox.getSelectedIndex()+1);
        // If the booking/deleting YearMonth is the same YearMonth shown in calendarView
        if(yearMonth.equals(YearMonth.from(selectedDate))){
            updateMonthViewDaysPanel(yearMonth);
        }

        LocalDate sidePanelDate = LocalDate.parse(sidePanelDateLabel.getText(), dateFormatter);
        if(!selectedDate.isBefore(sidePanelDate) && !selectedDate.isAfter(sidePanelDate.plusDays(6))){
            updateSidePanel(sidePanelDate);
        }
    } 

}

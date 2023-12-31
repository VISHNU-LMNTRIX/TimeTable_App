package core;

import java.time.LocalDate;

// Interface used as a callback to update the calendar view when a slot is booked/deleted.
public interface JsonUpdateListener {
    void updateCalendarView(LocalDate selectedDate);
}

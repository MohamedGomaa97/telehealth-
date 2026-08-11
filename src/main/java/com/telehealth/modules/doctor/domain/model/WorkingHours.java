package com.telehealth.modules.doctor.domain.model;
import java.time.LocalTime;
import java.util.List;
public record WorkingHours(LocalTime startTime, LocalTime endTime, List<String> workingDays) {
    public boolean isWorkingNow() {
        LocalTime now = LocalTime.now();
        String today = java.time.DayOfWeek.from(java.time.LocalDate.now()).name();
        return workingDays.contains(today) && now.isAfter(startTime) && now.isBefore(endTime);
    }
}

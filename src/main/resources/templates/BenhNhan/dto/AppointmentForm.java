package com.example.baitapluyen.dto;

import java.time.LocalDate;

public class AppointmentForm {
    private Long doctorId;
    private Long scheduleId;
    private LocalDate appointmentDate;
    private String timeSlot;
    private String symptoms;

    public Long getDoctorId(){return doctorId;} public void setDoctorId(Long v){doctorId=v;}
    public Long getScheduleId(){return scheduleId;} public void setScheduleId(Long v){scheduleId=v;}
    public LocalDate getAppointmentDate(){return appointmentDate;} public void setAppointmentDate(LocalDate v){appointmentDate=v;}
    public String getTimeSlot(){return timeSlot;} public void setTimeSlot(String v){timeSlot=v;}
    public String getSymptoms(){return symptoms;} public void setSymptoms(String v){symptoms=v;}
}

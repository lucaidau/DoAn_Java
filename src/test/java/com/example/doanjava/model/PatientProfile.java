package com.carehealth.model;

import jakarta.persistence.*;

@Entity
@Table(name = "patient_profiles")
public class PatientProfile {
    @Id
    private Long accountId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(length = 200)
    private String address;

    @Column(length = 4)
    private String bloodType;

    @Column(length = 200)
    private String allergies;

    @Column(length = 500)
    private String medicalHistory;

    @Column(length = 20)
    private String healthInsuranceNumber;

    @Column(length = 120)
    private String emergencyContactName;

    @Column(length = 15)
    private String emergencyContactPhone;

    protected PatientProfile() {}

    public PatientProfile(Account account) { this.account = account; }

    public Long getAccountId() { return accountId; }
    public Account getAccount() { return account; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }
    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }
    public String getHealthInsuranceNumber() { return healthInsuranceNumber; }
    public void setHealthInsuranceNumber(String healthInsuranceNumber) { this.healthInsuranceNumber = healthInsuranceNumber; }
    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }
    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }
}

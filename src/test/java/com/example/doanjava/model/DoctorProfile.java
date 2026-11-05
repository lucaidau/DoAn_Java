package com.carehealth.model;

import jakarta.persistence.*;

@Entity
@Table(name = "doctor_profiles")
public class DoctorProfile {
    @Id
    private Long accountId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(length = 120)
    private String specialty;

    @Column(length = 150)
    private String hospital;

    @Column(length = 120)
    private String workplace;

    @Column(length = 50)
    private String licenseNumber;

    private Integer experienceYears;

    @Column(length = 1500)
    private String introduction;

    @Column(length = 400)
    private String education;

    protected DoctorProfile() {}

    public DoctorProfile(Account account) { this.account = account; }

    public Long getAccountId() { return accountId; }
    public Account getAccount() { return account; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public String getHospital() { return hospital; }
    public void setHospital(String hospital) { this.hospital = hospital; }
    public String getWorkplace() { return workplace; }
    public void setWorkplace(String workplace) { this.workplace = workplace; }
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }
    public String getIntroduction() { return introduction; }
    public void setIntroduction(String introduction) { this.introduction = introduction; }
    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }
}

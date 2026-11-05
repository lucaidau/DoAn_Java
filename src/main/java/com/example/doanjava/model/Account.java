package com.carehealth.model;
import jakarta.persistence.*;
import java.time.LocalDate;
@Entity @Table(name="accounts",uniqueConstraints={@UniqueConstraint(columnNames="phone"),@UniqueConstraint(columnNames="email")})
public class Account {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=120) private String fullName;
 @Column(nullable=false,length=15) private String phone;
 @Column(length=180) private String email;
 @Column(nullable=false) private String passwordHash;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private Role role;
 private LocalDate birthDate;
 private String gender;
 protected Account(){}
 public Account(String fullName,String phone,String email,String passwordHash,Role role,LocalDate birthDate,String gender){this.fullName=fullName;this.phone=phone;this.email=email;this.passwordHash=passwordHash;this.role=role;this.birthDate=birthDate;this.gender=gender;}
 public void setPasswordHash(String passwordHash){this.passwordHash=passwordHash;}
 public Long getId(){return id;} public String getFullName(){return fullName;} public String getPhone(){return phone;} public String getEmail(){return email;} public String getPasswordHash(){return passwordHash;} public Role getRole(){return role;} public LocalDate getBirthDate(){return birthDate;} public String getGender(){return gender;}
}

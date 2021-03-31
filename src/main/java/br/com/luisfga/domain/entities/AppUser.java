package br.com.luisfga.domain.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "app_user")
public class AppUser implements Serializable {
    
    @Id
    @Email
    private String username;
    
    @NotBlank
    @Column(nullable = false)
    private String password;
    
    @NotBlank
    private String name;
    
    private byte[] thumbnail;

    @NotNull
    @Column(columnDefinition = "DATE")
    private LocalDate birthday;
    
    @Column(name="join_time")
    private OffsetDateTime joinTime;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @NotEmpty
    private String status;
    
    @OneToOne(mappedBy = "appUser", orphanRemoval = true, cascade = CascadeType.ALL)
    private AppUserOperationWindow operationWindow;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role", 
            joinColumns = @JoinColumn(name = "username"), 
            inverseJoinColumns = @JoinColumn(name = "role_name"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"username","role_name"})
    )
    private Set<AppRole> roles = new HashSet<>();
    
    public AppUser() {
    }

    public AppUser(String username, String password, String name, LocalDate birthday, String status) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.birthday = birthday;
        this.joinTime = OffsetDateTime.now();
        this.status = status;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public OffsetDateTime getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(OffsetDateTime joinTime) {
        this.joinTime = joinTime;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AppUserOperationWindow getOperationWindow() {
        return operationWindow;
    }

    public void setOperationWindow(AppUserOperationWindow operationWindow) {
        this.operationWindow = operationWindow;
    }

    public Set<AppRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<AppRole> roles) {
        this.roles = roles;
    }

    public void add(AppRole appRole){
        appRole.getUsers().add(this);
        this.roles.add(appRole);
    }

}
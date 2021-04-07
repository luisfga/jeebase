package br.com.luisfga.domain.entities;

import java.io.Serializable;
import java.time.OffsetDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="app_user_operation_window")
public class AppUserOperationWindow implements Serializable {
    
    @Id
    private String username;
    
    @OneToOne
    @MapsId
    private AppUser appUser;
    
    @NotNull
    @Column(name = "window_token", nullable = false)
    private String windowToken;
    
    @NotNull
    @Column(name="init_time", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime initTime;

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public String getWindowToken() {
        return windowToken;
    }

    public void setWindowToken(String windowToken) {
        this.windowToken = windowToken;
    }

    public OffsetDateTime getInitTime() {
        return initTime;
    }

    public void setInitTime(OffsetDateTime initTime) {
        this.initTime = initTime;
    }
    
}
package br.com.luisfga.controller.jsf.util;

import java.io.Serializable;
import java.util.UUID;

/**
 *
 * @author luisfga
 */
public class FloatingMessage implements Serializable{

    public enum Severity {
        INFO, WARN, ERROR
    };
    
    public static final String MESSAGES_LIST_SESSION_KEY = "floatingMessagesList";
    
    private String uuid;
    private Severity severity;
    private String message;
    private long postTime;
    private long timeToLive;

    public FloatingMessage(Severity severity, String message, long timeToLive) {
        this.severity = severity;
        this.message = message;
        this.timeToLive = timeToLive;
        this.postTime = System.currentTimeMillis();
        this.uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    public FloatingMessage(Severity severity, String message) {
        this.severity = severity;
        this.message = message;
        this.timeToLive = 5000;
        this.postTime = System.currentTimeMillis();
        this.uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }
    
    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getPostTime() {
        return postTime;
    }

    public void setPostTime(long postTime) {
        this.postTime = postTime;
    }

    public long getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    
    public boolean isExpired(){
        return (System.currentTimeMillis() - this.postTime) > this.timeToLive;
    }
    
    public long getTimeLeft(){
        return (this.postTime + this.timeToLive) - System.currentTimeMillis();
    }
}

package br.com.luisfga.domain.entities;

import java.io.Serializable;
import java.util.Arrays;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Image implements Serializable {
    
    @Id
    @GeneratedValue
    private long id;
    
    @Column(name = "file_name")
    private String fileName;
    
    private byte[] data;
    
    @Column(name = "time_in_millis")
    private long timeInMillis;

    public Image(){
    }
    
    public Image(byte[] data, long timeInMillis){
        this.data = data;
        this.timeInMillis = timeInMillis;
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Image other = (Image) obj;
        return Arrays.equals(this.data, other.data);
    }

}

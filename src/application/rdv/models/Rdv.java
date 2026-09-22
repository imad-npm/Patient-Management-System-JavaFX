package application.rdv.models;

import java.time.LocalDateTime;

public class Rdv {
    private int patientId;
    private int id ;
    private LocalDateTime dateTime;
    
    public Rdv(int patientId, LocalDateTime dateTime) {
        this.patientId = patientId;
        this.dateTime = dateTime;
    }
    public Rdv(int id,int patientId, LocalDateTime dateTime) {
        this.patientId = patientId;
        this.dateTime = dateTime;
     this.id=id ;
    }
    
    public int getId() {
        return id;
    }
    
    public int getPatientId() {
        return patientId;
    }
    
    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }
    
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}

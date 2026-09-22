package application.consultations.models;

import java.time.LocalDate;

public class Consultation {
    private int id;
    private int patientId;
    private LocalDate date;
    private String motif;
    private String diagnostic ;

    public Consultation( int patientId, LocalDate date, String motif,String diagnostic) {
   
        this.patientId = patientId;
        this.date = date;
        this.motif = motif;
        this.diagnostic=diagnostic ;
    }
    public Consultation(int id, int patientId, LocalDate date, String motif,String diagnostic) {
        this.id = id;
        this.patientId = patientId;
        this.date = date;
        this.motif = motif;
        this.diagnostic=diagnostic ;

    }

    public void setId(int id) {
        this.id=id;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getMotif() {
        return motif;
    }
    public String getDiagnostic() {
        return diagnostic;
    }


    public void setMotif(String motif) {
        this.motif = motif;
    }
}

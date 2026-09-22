package application.consultations.models;

public class Prescription {
    private int id;
    private int consultationId;
    private int medicamentId;
    private String medicament;
    private String observation;
    private int quantity;
    
    public Prescription(int consultationId, int medicamentId, String observation, int quantity) {
        this.consultationId = consultationId;
        this.medicamentId = medicamentId;
        this.observation = observation;
        this.quantity = quantity;
    }
    public Prescription(String medicament, String observation, int quantite) {
        this.medicament = medicament;
        this.observation = observation;
        this.quantity = quantite;
    }
    
    public String getMedicament() {
        return medicament;
    }

    public void setMedicament(String medicament) {
        this.medicament = medicament;
    }

	public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getConsultationId() {
        return consultationId;
    }
    
    public void setConsultationId(int consultationId) {
        this.consultationId = consultationId;
    }
    
    public int getMedicamentId() {
        return medicamentId;
    }
    
    public void setMedicamentId(int medicamentId) {
        this.medicamentId = medicamentId;
    }
    
    public String getObservation() {
        return observation;
    }
    
    public void setObservation(String observation) {
        this.observation = observation;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

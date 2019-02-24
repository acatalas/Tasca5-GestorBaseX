package empresa;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe que representa un departament
 * Els atributs codi i nom son obligatoris, mentre que els atributs localitat i
 * empleats poden estar buits.
 */
public class Dept {
    private String codi;
    private String nom;
    private String localitat;
    private List<Emp> empleats;

    public Dept(String codi, String nom) {
        this(codi, nom, null);
    }

    public Dept(String codi, String nom, String localitat) {
        this(codi, nom, localitat, null);
    }
    
    public Dept(String codi, String nom, String localitat, List<Emp> empleats) {
        this.codi = codi;
        this.nom = nom;
        this.localitat = localitat;
        //Ens asseguram que la llista d'empleats, en cas que sigui null, estigui 
        //inicialitzada
        if(empleats == null){
            this.empleats = new ArrayList<>();
        } else {
            this.empleats = empleats;
        }
    }
    
    public String getCodi(){
        return codi;
    }

    public String getNom() {
        return nom;
    }

    public String getLocalitat() {
        return localitat;
    }
    
    public List<Emp> getEmpleats(){
        return empleats;
    }
    
    public void setEmpleats(List<Emp> empleats){
        this.empleats = empleats;
    }
    
    public void addEmpleat(Emp empleat){
        empleats.add(empleat);
    }
    
    private String getInfoEmpleats(){
        String infoEmpleats = "";
        if (empleats == null){
            return null;
        } 
        for (Emp empleat : empleats){
            infoEmpleats += empleat.toString() + "\n";   
        }
        return infoEmpleats;
    }
    
    @Override
    public String toString() {
        return "Dept{" + "codi = " + codi + ",\nnom = " + nom + ",\nlocalitat = " + localitat + 
                ",\nempleats = {\n" + getInfoEmpleats() + "}";
    }
    
    
    
    
}

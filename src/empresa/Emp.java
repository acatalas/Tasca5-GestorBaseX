package empresa;

/**
* Classe que representa un empleat.
* Els atributs codi, codiDep i cognom són obligatoris, els altres són opcionals
 */
public class Emp {
    private String codi;
    private String codiDep;
    private String codiCap;
    private String cognom;
    private String ofici;
    private String dataAlta;
    private Long salari;
    private Long comissio;
    
    public Emp(String codi, String codiDep, String codiCap, String cognom, 
            String ofici, String dataAlta, Long salari, Long comissio) {
        this.codi = codi;
        this.codiDep = codiDep;
        this.codiCap = codiCap;
        this.cognom = cognom;
        this.ofici = ofici;
        this.dataAlta = dataAlta;
        this.salari = salari;
        this.comissio = comissio;
    }
    
    public Emp(String codi, String codiDep, String cognom){
        this(codi, codiDep, cognom, null, null, null, null, null);
    }
    
    //Mètodes getters
    public String getCodi() {
        return codi;
    }

    public String getCodiDep() {
        return codiDep;
    }

    public String getCodiCap() {
        return codiCap;
    }

    public String getCognom() {
        return cognom;
    }

    public String getOfici() {
        return ofici;
    }

    public String getDataAlta() {
        return dataAlta;
    }

    public Long getSalari() {
        return salari;
    }

    public Long getComissio() {
        return comissio;
    }
    
    @Override
    public String toString() {
        return "Emp{" + "codi = " + codi + ",\ncodiDep = " + codiDep + ",\ncodiCap = " + codiCap + 
                ",\ncognom = " + cognom + ",\nofici = " + ofici + ",\ndataAlta = " + dataAlta + 
                ",\nsalari = " + salari + ",\ncomissio = " + comissio + '}';
    }
    
    
    
}

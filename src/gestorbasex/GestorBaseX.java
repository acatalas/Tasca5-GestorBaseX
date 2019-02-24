package gestorbasex;

import empresa.Dept;

public class GestorBaseX {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        GestorDB gestor = new GestorDB("localhost", 1984, "admin", "admin");
        //Recuperar departament sense empleats
        /*Dept departament = gestor.getDeptSenseEmp("d10");
        System.out.println(departament);*/
        //Recuperar departament amb empleats
        /*Dept departament = gestor.getDeptAmbEmp("d10");
        System.out.println(departament);*/
        //Inserir un departament
        /*List<Emp> empleats = new ArrayList<>();
        empleats.add(new Emp("e6666", "d50", null, 
                "Suarez", "El gran Jefe", "24-07-2018", 
                new Long(6666), new Long(7777)));
        try {
            empleats.add(gestor.getEmpleat("e7369"));
        } catch (Exception ex) {
            System.err.println("Aquest empleat no existeix a la BD");
        }
        Dept departamentNou = new Dept("d50", "Administracio", "Mallorca", empleats);
        gestor.insertDept(departamentNou);
        Dept departament = gestor.getDeptAmbEmp("d50");
        System.out.println(departament);*/
        //Eliminar un departament
        /*Dept departament = gestor.getDeptAmbEmp("d50");
        gestor.deleteDept(departament);
        departament = gestor.getDeptAmbEmp("d50");
        try {
            gestor.getEmpleat("e6666");
        } catch (Exception ex) {
            System.err.println(ex);
        }*/
        //Eliminar un departament i assignar els empleats a l'altre
        /*Dept departament10 = gestor.getDeptAmbEmp("d10");
        Dept departament20 = gestor.getDeptAmbEmp("d20");
        System.out.println("Departament d10 abans d'eliminar");
        System.out.println(departament10);
        System.out.println("Departament d20 abans d'inserir els empleats de d10");
        System.out.println(departament20);
        gestor.deleteDept(departament10, departament20);
        departament10 = gestor.getDeptAmbEmp("d10");
        departament20 = gestor.getDeptAmbEmp("d20");
        System.out.println("Departament d10 despres d'eliminar");
        System.out.println(departament10);
        System.out.println("Departament d20 despres d'inserir els empleats de d10");
        System.out.println(departament20);*/
        //Reemplaçar un departament
        Dept departamentAntic = gestor.getDeptAmbEmp("d10");
        Dept departamentNou = new Dept("d77", "Producció", "Murcia");
        gestor.replaceDept(departamentNou, departamentAntic);
        System.out.println("Departament 10");
        System.out.println(gestor.getDeptAmbEmp("d10"));
        System.out.println("Departament 77");
        System.out.println(gestor.getDeptAmbEmp("d77"));
        
        
    }

}

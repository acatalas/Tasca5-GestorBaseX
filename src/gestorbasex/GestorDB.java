package gestorbasex;

import empresa.Dept;
import empresa.Emp;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.basex.api.client.ClientQuery;
import org.basex.api.client.ClientSession;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Ale
 */
public class GestorDB {

    private final String username;
    private final String password;
    private final int port;
    private final String host;
    private ClientSession clientSession;
    
    /**
     * Constructor que crea la connexió amb la base de dades
     * @param host Adreça on es troba la BD
     * @param port Port de la BD
     * @param username Usuari
     * @param password Contrassenya
     */
    public GestorDB(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;

        try {
            clientSession = new ClientSession(host, port, username, password);
            clientSession.execute("OPEN empresa");
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    /**
     * Tancla la connexió amb la BD
     */
    public void tancarSessio() {
        try {
            clientSession.execute("CLOSE");
            clientSession.close();
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    /**
     * Recupera un departament de la BD sense els seus empleats.
     * @param codiDept Codi del departament que volem recuperar
     * @return Objecte Departament.
     */
    public Dept getDeptSenseEmp(String codiDept) {
        ClientQuery query;
        Dept departament = null;
        try {
            //Recuperam el nom del departament
            query = clientSession.query("data(/empresa/departaments/dept[@codi = \""
                    + codiDept + "\"]/nom)");
            String nom = query.execute();
            query.close();

            //Si la consulta no retorna res, com que el nom és un camp obligatori,
            //el departament no existeix
            if (nom.equals("")) {
                throw new Exception("No existeix cap departament amb aquest codi");
            }

            //Recuperam la localitat del departament
            query = clientSession.query("data(/empresa/departaments/dept[@codi = \""
                    + codiDept + "\"]/localitat)");
            String localitat = query.execute();
            query.close();
            
            if (localitat.equals("")){
                localitat = null;
            }

            //Cream el departament
            departament = new Dept(codiDept, nom, localitat);

        } catch (Exception ex) {
            System.err.println(ex);
        }
        return departament;
    }

    /**
     * Recupera un departament de la BD amb tots els seus empleats.
     * @param codiDept Codi del departament que volem recuperar
     * @return Objecte Departament amb tots els seus empleats. Si no té
     * empleats, empleats estarà null
     */
    public Dept getDeptAmbEmp(String codiDept) {
        Dept departament = null;
        ClientQuery query;
        try {
            departament = getDeptSenseEmp(codiDept);

            if (departament == null) {
                throw new Exception("S'ha produït un error al recuperar el departament"
                        + "de la base de dades.");
            }

            List<String> codiEmpleats = getCodisEmpleatsPerDep(codiDept);

            //Comprova si el departament te empleats
            if (codiEmpleats != null) {
                //Afegeix els empleats al departament
                for (String codi : codiEmpleats) {
                    departament.addEmpleat(getEmpleat(codi));
                }
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
        return departament;
    }

    /**
     * Retorna un objecte Emp de la BD a partir del codi de l'empleat
     * @param codiEmp Codi de l'empleat que volem recuperar.
     * @return Empleat de la BD que té el codi passat per paràmetre
     * @throws Exception
     */
    public Emp getEmpleat(String codiEmp) throws Exception {
        return new Emp(codiEmp, getCodiDep(codiEmp), getCodiCap(codiEmp), getCognom(codiEmp),
                getOfici(codiEmp), getDataAlta(codiEmp), getSalari(codiEmp),
                getComissio(codiEmp));
    }

    /**
     * Recupera el codi del departament d'un empleat de la BD
     * @param codiEmp Codi de l'empleat del qual volem obtenir el codi del
     * departament.
     * @return String amb el codi del departament. Si no troba l'empleat, llança
     * una excepció, ja que el codi del departament és obligatori.
     * @throws IOException
     */
    public String getCodiDep(String codiEmp) throws Exception {
        ClientQuery query = clientSession.query("data(/empresa/empleats/emp[@codi = \""
                + codiEmp + "\"]/@dept)");
        String codiDep = query.execute();
        query.close();
        if (codiDep.equals("")) {
            
            throw new Exception("Aquest empleat no existeix a la base de dades");
        }
        return codiDep;
    }

    /**
     * Recupera el codi del cap d'un empleat de la BD
     * @param codiEmp Codi de l'empleat del qual volem obtenir el codi del cap.
     * @return String amb el codi del cap. Si l'empleat no té cap, retorna null.
     * @throws Exception
     */
    public String getCodiCap(String codiEmp) throws Exception {
        ClientQuery query = clientSession.query("data(/empresa/empleats/emp[@codi = \""
                + codiEmp + "\"]/@cap)");
        String codiCap = query.execute();
        query.close();
        if(codiCap.equals("")){
            codiCap = null;
        }
        return codiCap;
    }

    /**
     * Recupera el cognom d'un empleat de la BD
     * @param codiEmp Codi de l'empleat del qual volem obtenir el cognom.
     * @return String amb el cognom. Si no troba l'empleat, llança una excepció,
     * ja que el cognom és obligatori.
     * @throws IOException
     */
    public String getCognom(String codiEmp) throws Exception {
        ClientQuery query = clientSession.query("data(/empresa/empleats/emp[@codi=\"" + codiEmp + "\"]/cognom)");
        String cognom = query.execute();
        query.close();
        if (cognom.equals("")) {
            throw new Exception("Aquest empleat no existeix a la base de dades");
        }
        return cognom;
    }

    /**
     * Recupera l'ofici d'un empleat de la BD
     * @param codiEmp Codi de l'empleat del qual volem obtenir l'ofici.
     * @return String amb l'ofici. Si l'empleat no té ofici, retorna null.
     * @throws IOException
     */
    public String getOfici(String codiEmp) throws IOException {
        ClientQuery query = clientSession.query("data(/empresa/empleats/emp[@codi=\"" + codiEmp + "\"]/ofici)");
        String ofici = query.execute();
        query.close();
        if(ofici.equals("")){
            ofici = null;
        }
        return ofici;
    }

    /**
     * Recupera la data d'alta d'un empleat de la BD
     * @param codiEmp Codi de l'empleat del qual volem obtenir la data d'alta.
     * @return String amb la data d'alta. Si l'empleat no té data d'alta,
     * retorna null.
     * @throws IOException
     */
    public String getDataAlta(String codiEmp) throws IOException {
        ClientQuery query = clientSession.query("data(/empresa/empleats/emp[@codi=\"" + codiEmp + "\"]/dataAlta)");
        String dataAlta = query.execute();
        query.close();
        if(dataAlta.equals("")){
            dataAlta = null;
        }
        return dataAlta;
    }

    /**
     * Recupera el salari d'un empleat de la BD
     * @param codiEmp Codi de l'empleat del qual volem obtenir el salari.
     * @return Long amb el salari. Si l'empleat no te salari, retorna null.
     * @throws IOException
     */
    public Long getSalari(String codiEmp) throws IOException {
        Long salari = null;
        ClientQuery query = clientSession.query("data(/empresa/empleats/emp[@codi=\"" + codiEmp + "\"]/salari)");
        String result = query.execute();
        query.close();
        if (!result.equals("")) {
            salari = Long.valueOf(result);
        }
        return salari;
    }

    /**
     * Recupera la comissió d'un empleat de la BD
     * @param codiEmp Codi de l'empleat del qual volem obtenir la comissió.
     * @return Long amb la comissio. Si l'empleat no te comissió, retorna null.
     * @throws IOException
     */
    public Long getComissio(String codiEmp) throws IOException {
        Long comissio = null;
        ClientQuery query = clientSession.query("data(/empresa/empleats/emp[@codi=\"" + codiEmp + "\"]/comissio)");
        String result = query.execute();
        query.close();
        if (!result.equals("")) {
            comissio = Long.valueOf(result);
        }
        return comissio;
    }

    /**
     * Torna una llista amb el codi de cada empleat del departament especificat
     * @param codiDept Departament del qual volem recuperar el empleats
     * @return LLista amb els codis dels empleats
     */
    private List<String> getCodisEmpleatsPerDep(String codiDept) {
        List<String> codisEmpleats = null;
        try {
            //Retorna tots els codis dels empleats separats per un salt de linia
            ClientQuery query = clientSession.query("for $empleat in /empresa/empleats/emp[@dept=\""
                    + codiDept + "\"]\n" + "return (data($empleat/@codi))");
            String codis = query.execute();

            //Comprova que el departament té empleats
            if (!codis.equals("")) {
                codisEmpleats = new ArrayList<>(Arrays.asList(codis.split("\\r?\\n")));
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
        return codisEmpleats;
    }

    /**
     * Converteix un objecte empleat en XML.
     * @param empleat Empleat que volem convertir en XML
     * @return String que representa l'empleat en XML
     */
    public String generateEmpXml(Emp empleat) {
        String xml = null;
        try {
            //Construim el Document DOM
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            //Afegim l'element arrel al DOM
            Element empElement = doc.createElement("emp");
            doc.appendChild(empElement);

            //Afegim els atributs de l'element emp
            Attr codiAttr = doc.createAttribute("codi");
            codiAttr.setValue(empleat.getCodi());
            empElement.setAttributeNode(codiAttr);

            Attr deptAttr = doc.createAttribute("dept");
            deptAttr.setValue(empleat.getCodiDep());
            empElement.setAttributeNode(deptAttr);

            //Comprovam que l'atribut codiCap no estigui null abans de crear
            //l'atribut cap
            if (empleat.getCodiCap() != null) {
                Attr capAttr = doc.createAttribute("cap");
                capAttr.setValue(empleat.getCodiCap());
                empElement.setAttributeNode(capAttr);
            }

            //Cream l'element obligatori del cognom
            Element cognomElement = doc.createElement("cognom");
            cognomElement.appendChild(doc.createTextNode(empleat.getCognom()));
            empElement.appendChild(cognomElement);
            
            if (empleat.getOfici() != null) {
                Element oficiElement = doc.createElement("ofici");
                oficiElement.appendChild(doc.createTextNode(empleat.getOfici()));
                empElement.appendChild(oficiElement);
            }

            if (empleat.getDataAlta() != null) {
                Element dataElement = doc.createElement("dataAlta");
                dataElement.appendChild(doc.createTextNode(empleat.getDataAlta()));
                empElement.appendChild(dataElement);
            }

            if (empleat.getSalari() != null) {
                Element salariElement = doc.createElement("salari");
                salariElement.appendChild(doc.createTextNode(empleat.getSalari().toString()));
                empElement.appendChild(salariElement);
            }

            if (empleat.getComissio() != null) {
                Element comissioElement = doc.createElement("comissio");
                comissioElement.appendChild(doc.createTextNode(empleat.getComissio().toString()));
                empElement.appendChild(comissioElement);
            }

            //Retorna el String amb l'xml a partir del document DOM.
            xml = convertDocToString(empElement);

        } catch (ParserConfigurationException | TransformerException | DOMException e) {
            System.err.println(e);
        }
        return xml;
    }

    /**
     * Converteix un objecte departament en XML.
     * @param departament Departament que volem convertir en XML
     * @return String que representa el departament en XML
     */
    public String generateDeptXml(Dept departament) {
        String xml = null;
        try {
            //Construim el Document DOM
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            
            //Afegim l'element arrel al DOM
            Element deptElement = doc.createElement("dept");
            doc.appendChild(deptElement);

            //Afegim el codi del departament a l'etiqueta dep
            Attr codiAttr = doc.createAttribute("codi");
            codiAttr.setValue(departament.getCodi());
            deptElement.setAttributeNode(codiAttr);
            
            //Afegim l'element nom a l'element dep
            Element nomElement = doc.createElement("nom");
            nomElement.appendChild(doc.createTextNode(departament.getNom()));
            deptElement.appendChild(nomElement);
            
            //Afegeix l'element localitat si el departament el té
            if (departament.getLocalitat() != null) {
                Element localitatElement = doc.createElement("localitat");
                localitatElement.appendChild(doc.createTextNode(departament.getLocalitat()));
                deptElement.appendChild(localitatElement);
            }
            
            xml = convertDocToString(deptElement);

        } catch (ParserConfigurationException | TransformerException | DOMException ex) {
            System.err.println(ex);
        }
        return xml;
    }

    /**
     * Converteix una estructura de dades DOM (elements i atributs) en una
     * cadena.
     * @param rootElement Element arrel de l'estructura DOM
     * @return String XML de l'estructura DOM
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    private String convertDocToString(Element rootElement) throws TransformerConfigurationException, TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        StringWriter stringWriter = new StringWriter();
        transformer.transform(new DOMSource(rootElement), new StreamResult(stringWriter));
        return stringWriter.getBuffer().toString();
    }
    
    public boolean existeixDept(String codiDept){
        ClientQuery query;
        try {
            query = clientSession.query("data(/empresa/departaments/dept[@codi = \""
                    + codiDept + "\"])");
            String dept = query.execute();
            query.close();
            if(!dept.equals("")){
                return true;
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
        return false;
    }
    
    public boolean existeixEmp(String codiEmp){
        ClientQuery query;
        try {
            query = clientSession.query("data(/empresa/empleats/emp[@codi = \""
                    + codiEmp + "\"])");
            String emp = query.execute();
            query.close();
            if(!emp.equals("")){
                return true;
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
        return false;
    }

    /**
     * Insereix un departament a la BD si el departament no existeix a la BD.
     * També insereix els empleats del departament a la BD que no estàn ja
     * guardats
     * @param departament Departament que es vol inserir a la BD
     */
    public void insertDept(Dept departament) {
        String codiDep = departament.getCodi();
        try {
            //Comprovam que el departament no es troba a la BD
            if (existeixDept(codiDep)) {
                throw new Exception("El departament ja existeis a la base de dades");
            }

            //Guarda els empleats que no estan inserits a la BD
            List<Emp> empleatsAInserir = new ArrayList<>();
            for (Emp empleat : departament.getEmpleats()) {
                if (!existeixEmp(empleat.getCodi())) {
                    empleatsAInserir.add(empleat);
                }
            }

            //Guarda l'empresa a la BD
            String insertDept = generateDeptXml(departament);
            ClientQuery query = clientSession.query("insert node " + insertDept
                    + " as last into /empresa/departaments");
            query.execute();
            query.close();

            //Guarda els empleats a la BD
            if (empleatsAInserir.size() > 0) {
                for (Emp empleat : empleatsAInserir) {
                    String insertEmp = generateEmpXml(empleat);
                    query = clientSession.query("insert node " + insertEmp
                            + "as last into /empresa/empleats");
                    query.execute();
                    query.close();
                }
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    /**
     * Elimina el departament de la BD i tots els seus empleats
     * @param departament Departament a eliminar de la BD
     */
    public void deleteDept(Dept departament) {
        String codiDep = departament.getCodi();
        try {
            if (!existeixDept(codiDep)) {
                throw new Exception("El departament no existeix a la BD");
            }

            //Elimina el departament de la BD
            ClientQuery query = clientSession.query("delete node /empresa/departaments/dept[@codi=\""
                    + codiDep + "\"]");
            query.execute();
            query.close();

            //Elimina els empleats del departament
            query = clientSession.query("for $empresa in /empresa/empleats/emp[@dept=\""
                    + codiDep + "\"] return (delete node $empresa)");
            query.execute();
            query.close();
        } catch (Exception ex) {
            System.err.println(ex);
        }

    }

    /**
     * Elimina un departament de la BD i assigna tots els seus empleats a un
     * altre departament
     * @param departament Departament a eliminar
     * @param departamentNou Departament on volem assignar els empleats del
     * departament eliminat
     */
    public void deleteDept(Dept departament, Dept departamentNou) {
        String codiDep = departament.getCodi();
        try {
            if (existeixDept(codiDep)) {
                //Elimina el departament de la BD
                ClientQuery query = clientSession.query("delete node /empresa/departaments/dept[@codi=\""
                    + codiDep + "\"]");
                query.execute();
                query.close();

                String codiDepNou = departamentNou.getCodi();
                if (!existeixDept(codiDepNou)) {
                    throw new Exception("El departament nou no existeix a la BD");
                }
                
                query = clientSession.query("for $empleat in /empresa/empleats/emp[@dept = \""
                        + codiDep + "\"]/@dept"
                        + "\nreturn (replace value of node $empleat with \"" + codiDepNou + "\")");
                query.execute();
                query.close();
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
    
    /**Reemplaça un departament a la BD per un altre. Insereix el departament nou
     * i elimina el departament reemplaçat, assignant els empleats del departament
     * reemplaçat al departament nou.
     * @param depAInserir
     * @param depAReemplacar 
     */
    public void replaceDept(Dept depAInserir, Dept depAReemplacar) {
        insertDept(depAInserir);
        deleteDept(depAReemplacar, depAInserir);
    }
}

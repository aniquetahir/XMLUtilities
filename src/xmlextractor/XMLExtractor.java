package xmlextractor;
import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 *
 * @author AniqueTahir
 */
public class XMLExtractor {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        File xmlFile = null;
        ArrayList<String> tags;
        
        if(args.length>0){
            xmlFile = new File(args[0]);
        }else{
            System.out.println("Enter the path to an xml file");
            return;
        }
        
        javax.xml.parsers.DocumentBuilderFactory dbfactory 
                = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        try{
            DocumentBuilder dbuilder=dbfactory.newDocumentBuilder();
        
            Document doc = dbuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            //System.out.println("XML Root:"+doc.getFirstChild().getFirstChild().getNodeName());
            tags = getDistinctTags(doc.getFirstChild());
            
            tags.stream().forEach((tag) -> {
                System.out.println(tag);
            });
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    
    private static ArrayList<String> getDistinctTags(Node node){
        ArrayList tags = new ArrayList();
        NodeList childNodes = node.getChildNodes();
        //Base Case childNodes.length = 0
        for(int i=0;i<childNodes.getLength();i++){
            //Add tags from children
            ArrayList<String> childTags = getDistinctTags(childNodes.item(i));
            childTags.stream().filter((childTag) -> (!tags.contains(childTag))).forEach((childTag) -> {
                tags.add(childTag);
            });
            //Add tag from this Node
            String thisNodeName = childNodes.item(i).getNodeName();
            if(!tags.contains(thisNodeName)){
                tags.add(thisNodeName);
            }
        }
        return tags;
    }
    
}

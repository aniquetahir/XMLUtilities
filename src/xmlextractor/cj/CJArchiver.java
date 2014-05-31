package xmlextractor.cj;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.sql.Connection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author AniqueTahir<aniquetahir@gmail.com>
 */
public class CJArchiver {
    private String devKey;
    Connection con = null;
    public CJArchiver(String devKey){
        this.devKey = devKey;
    }
    
    public void extractProducts(String pubId) throws Exception{
        con=null;
        try{
        con = 
            java.sql.DriverManager.getConnection("jdbc:mysql://localhost:3306/mysql?zeroDateTimeBehavior=convertToNull", "root", "");
        }catch(Exception e){
            System.out.println("Failed to establish connection to MySQL");
            return;
        }
        
        //Get all data from webservice
        java.net.URL url = new java.net.URL(
            "https://product-search.api.cj.com/v2/product-search?website-id=1234567&advertiser-ids="+pubId+"&records-per-page=1000"
        );
        HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
        urlCon.addRequestProperty(
                "Authorization", 
                this.devKey
        );
        urlCon.connect();
        InputStream iStream = urlCon.getInputStream();
        java.io.BufferedReader br;
        br = new java.io.BufferedReader(new InputStreamReader(iStream));
        
        StringBuilder respBuilder = new StringBuilder();
        while(true){
            String thisLine = br.readLine();
            if(thisLine == null){
                break;
            }
            respBuilder.append(thisLine+"\n");
        }
        
        
        System.out.println(respBuilder.toString());
        
        try{
            //Reset previous data
            con.prepareStatement("update gameservicedata.wsdata set product_status=0").executeUpdate();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        //ResultSet rs = con.prepareStatement("select * from gameservicedata.wsdata").executeQuery();
        addProducts(respBuilder.toString());
    }
    
    private void addProducts(String xml){
        DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
        try{
            DocumentBuilder dbuilder = dbf.newDocumentBuilder();
            Document doc = dbuilder.parse(new InputSource(new StringReader(xml)));
            Node requiredNode = doc.getFirstChild().getFirstChild();
            NodeList nList = requiredNode.getChildNodes();
            for(int i=0;i<nList.getLength();i++){
                Node pNode = nList.item(i);
                NodeList attrList = pNode.getChildNodes();
                Product p = new Product();
                for(int j=0;j<attrList.getLength();j++){
                    Node attrNode = attrList.item(j);
                    
                    if(attrNode.getNodeName().equals("ad-id")){
                    
                    }else if(attrNode.getNodeName().equals("ad-id")){
                    
                    }else if(attrNode.getNodeName().equals("ad-id")){
                    
                    }
                }
            }
            
            System.out.println(requiredNode.getNodeName());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    private class Product{
        public String 
        ad_id,advertiser_id, 
        advertiser_name, 
        advertiser_category, 
        buy_url,
        catalog_id, 
        currency, 
        description,  
        image_url, 
        in_stock,  
        isbn, 
        manufacturer_name,
        manufacturer_sku, 
        name, 
        price,
        retail_price, 
        sale_price, 
        sku,
        upc, 
        product_status;
    }
}

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
            ArrayList<Product> productList = new ArrayList();
            for(int i=0;i<nList.getLength();i++){
                Node pNode = nList.item(i);
                NodeList attrList = pNode.getChildNodes();
                Product p = new Product();
                for(int j=0;j<attrList.getLength();j++){
                    Node attrNode = attrList.item(j);
                    
                    switch (attrNode.getNodeName()) {
                        case "ad-id":
                            p.ad_id = attrNode.getNodeValue();
                            break;
                        case "advertiser-id":
                            p.advertiser_id = attrNode.getNodeValue();
                            break;
                        case "advertiser-name":
                            p.advertiser_name = attrNode.getNodeValue();
                            break;
                        case "advertiser-category":
                            p.advertiser_category = attrNode.getNodeValue();
                            break;
                        case "buy-url":
                            p.buy_url = attrNode.getNodeValue();
                            break;
                        case "catalog-id":
                            p.catalog_id = attrNode.getNodeValue();
                            break;
                        case "currency":
                            p.currency = attrNode.getNodeValue();
                            break;
                        case "description":
                            p.description = attrNode.getNodeValue();
                            break;
                        case "image-url":
                            p.image_url = attrNode.getNodeValue();
                            break;
                        case "in-stock":
                            p.in_stock = attrNode.getNodeValue();
                            break;
                        case "isbn":
                            p.isbn = attrNode.getNodeValue();
                            break;
                        case "manufacturer-name":
                            p.manufacturer_name= attrNode.getNodeValue();
                            break;
                        case "manufacturer-sku":
                            p.manufacturer_sku= attrNode.getNodeValue();
                            break;
                        case "name":
                            p.name = attrNode.getNodeValue();
                            break;
                        case "price":
                            p.price= attrNode.getNodeValue();
                            break;
                        case "retail-price":
                            p.retail_price= attrNode.getNodeValue();
                            break;
                        case "sale-price":
                            p.sale_price= attrNode.getNodeValue();
                            break;
                        case "sku":
                            p.sku= attrNode.getNodeValue();
                            break;
                        case "upc":
                            p.upc= attrNode.getNodeValue();
                            break;
                        case "product_status":
                            p.product_status= attrNode.getNodeValue();
                            break;
                    }
                }
                productList.add(p);
            }
            
            PreparedStatement psInsertProducts = con.prepareStatement("insert into"
                    + " gameservice.wsdata("
                    +"ad-id,"
                    +"advertiser-id," 
                    +"advertiser-name,"
                    +"advertiser-category,"
                    +"buy-url,"
                    +"catalog-id," 
                    +"currency," 
                    +"description,"  
                    +"image-url," 
                    +"in_stock,"  
                    +"isbn," 
                    +"manufacturer_name,"
                    +"manufacturer_sku," 
                    +"name," 
                    +"price,"
                    +"retail_price," 
                    +"sale_price," 
                    +"sku,"
                    +"upc," 
                    +"product_status) "
                    + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,1) "
                    + "on duplicate key update product_status=1");
            for(Product p:productList){
                psInsertProducts.setInt(1, Integer.parseInt(p.ad_id));
                psInsertProducts.setInt(2, Integer.parseInt(p.advertiser_id));
                psInsertProducts.setString(3, p.advertiser_name);
                psInsertProducts.setString(4, p.advertiser_category);
                psInsertProducts.setString(5, p.buy_url);
                psInsertProducts.setString(6, p.catalog_id);
                psInsertProducts.setString(7, p.currency);
                psInsertProducts.setString(8, p.description);
                psInsertProducts.setString(9, p.image_url);
                psInsertProducts.setBoolean(10, p.in_stock.equals("true")?true:false);
                psInsertProducts.setString(11, p.isbn);
                psInsertProducts.setString(12, p.manufacturer_name);
                psInsertProducts.setString(13, p.manufacturer_sku);
                psInsertProducts.setString(14, p.name);
                psInsertProducts.setFloat(15, Float.parseFloat(p.price));
                psInsertProducts.setFloat(16, Float.parseFloat(p.retail_price));
                psInsertProducts.setFloat(17, Float.parseFloat(p.sale_price));
                psInsertProducts.setString(18, p.sku);
                psInsertProducts.setInt(19, Integer.parseInt(p.upc));
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

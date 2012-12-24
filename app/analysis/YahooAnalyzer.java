package analysis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import models.Item;

public class YahooAnalyzer implements Analyzer {
	DefaultHttpClient httpclient = new DefaultHttpClient();
	@Override
	public AnalysisResult anaylze(Item item) {
		return null;
	}
	
	public Query queryContentAnalysis(String text){
		Query query = null;
		HttpPost post = new HttpPost("http://query.yahooapis.com/v1/public/yql");
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair("q", "select * from contentanalysis.analyze where text='"+text+"'"));
		try {
			post.setEntity(new UrlEncodedFormEntity(nvps));
			HttpResponse response = httpclient.execute(post);
			HttpEntity responseEntity = response.getEntity();
			String responseText = consumeEntity(responseEntity);
			query = parseXml(responseText);
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return query;
	}
	
	private String consumeEntity(HttpEntity reponseEntity){
		StringBuilder sb = new StringBuilder();
		try {
			InputStream is = reponseEntity.getContent();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while( (line = br.readLine()) !=null ){
				sb.append(line);
			}
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	private Query parseXml(String text){
		Query query = new Query();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			 InputSource is = new InputSource(new StringReader(text));
		     Document doc =  builder.parse(is);
		     XPathFactory xPathfactory = XPathFactory.newInstance();
		     XPath xpath = xPathfactory.newXPath();
		     XPathExpression expr = xpath.compile("/query/results/yctCategories/yctCategory");
		     
		     NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		     double score = 0.0;
		     String categoryStr = "";
		     for(int i = 0; i < nodeList.getLength(); i ++){
		    	 Element element = (Element) nodeList.item(i);
		    	 score = Double.parseDouble(element.getAttribute("score"));
		    	 categoryStr = element.getTextContent();
		    	 Category category = new Category(categoryStr, score);
		    	 query.addCategory(category);
		     }
		     
		     expr = xpath.compile("/query/results/entities/entity");
		     nodeList = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
		     for(int i =0; i < nodeList.getLength(); i++){
		    	 Element element = (Element)nodeList.item(i);
		    	 score = Double.parseDouble(element.getAttribute("score"));
		    	 
		    	 String textValue = element.getElementsByTagName("text").item(0).getTextContent();
		    	 
		    	 Entity entity = new Entity(textValue, score);
		    	 
		    	 NodeList typeList = (NodeList)element.getElementsByTagName("types");
		    	 for(int j = 0; j < typeList.getLength(); j++){
		    		 Element typeElement = (Element)typeList.item(j);
		    		 entity.addType(typeElement.getTextContent());
		    	 }
		    	 query.addEntity(entity);
		     }
		     
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       return query;
	}
	
	public static void main(String[] args) {
		YahooAnalyzer analyzer = new YahooAnalyzer();
		analyzer.queryContentAnalysis("I want to learn how to play guitar. Guitar is a very nice instrument to play. Fender is best guitar brand and very hight quality");
	}
	
	private static class Query{
		List<Category> categoryList = new ArrayList<YahooAnalyzer.Category>();
		List<Entity> entityList = new ArrayList<YahooAnalyzer.Entity>();
		
		public void addCategory(Category category){
			this.categoryList.add(category);
		}
		
		public void addEntity(Entity entity){
			this.entityList.add(entity);
		}
		
		@Override
		public String toString(){
			StringBuilder sb = new StringBuilder();
			sb.append("CategoryList: ").append(categoryList).append(", ");
			sb.append("EntityList: ").append(entityList);
			return sb.toString();
		}
	}
	
	private static class Result {
		protected double score;
		protected String name;
		public Result(String name, double score){
			this.name = name;
			this.score = score;
		}
		
		@Override
		public String toString(){
			StringBuilder sb = new StringBuilder();
			sb.append("score: ").append(score).append(", ");
			sb.append("name: ").append(name);
			return sb.toString();
		}
	}
	
	private static class Category extends Result{
		public Category(String name, double score){
			super(name,score);
		}
	}
	private static class Entity extends Result {
		List<String> typeList = new ArrayList<String>();
		public Entity(String name, double score){
			super(name,score);
		}
		
		public void addType(String type){
			this.typeList.add(type);
		}
	}

}

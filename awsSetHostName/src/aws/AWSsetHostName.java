package aws;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * This class will call the internal meta data aws webservice to get the public dns name and update the following ephesoft properties
 * 
 * Batch.properties file http base URL : 
 * 
 */


public class AWSsetHostName {

	private static final String EPHESOFT_DIRECTORY = "D:\\Ephesoft\\";

	
	public static void main(String args[])
	{
	
		//Get the EC2 host Name
		String HostName = getBatchInstanceList();
		System.out.println("Got the Host name " + HostName);
		
		//update the propeties file 
		
		//upate batch.properties File 
		String httpURL = "http://"+ HostName +":8080/dcma-batches";
		String pathToBatchPro = EPHESOFT_DIRECTORY + "Application\\WEB-INF\\classes\\META-INF\\dcma-batch\\dcma-batch.properties";
		setPropertyValue("batch.base_http_url", httpURL, pathToBatchPro);
		
		
	}
	

	private static String getBatchInstanceList() {
        int PORT = 80;
  
        HttpClient client = new HttpClient();
        // URL path to be hit for getting the batch instance identifier list having status specified.
        String url = "http://169.254.169.254/latest/meta-data/public-hostname";
   
            GetMethod getMethod = new GetMethod(url);
        int statusCode;
        try {
        statusCode = client.executeMethod(getMethod);
        if (statusCode == 200) {
            System.out.println("Web service executed successfully.");
            String responseBody = getMethod.getResponseBodyAsString();
            return responseBody;
        } else if (statusCode == 403) {
            System.out.println("Invalid username/password.");
        } else {
            System.out.println(getMethod.getResponseBodyAsString());
        }
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (getMethod != null) {
            getMethod.releaseConnection();
        }
    }
		return null;
}
	

	public static Properties loadpropertyFile(String pathToPropertiesFile)
	{	
		Properties prop = new Properties();			
			try {
				prop.load(new FileInputStream(pathToPropertiesFile));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
			return prop;	
	}

	public static void setPropertyValue(String propertykey, String newPropertyValue, String pathToPropertiesFile)
	{
		
		Properties prop = loadpropertyFile(pathToPropertiesFile);
		prop.setProperty(propertykey, newPropertyValue);
		
		System.out.println("Setting " + pathToPropertiesFile + " propKey " + propertykey + " value " + newPropertyValue);
		
		File file = new File(pathToPropertiesFile);
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(file);
			try {
				prop.store(fileOut, "AwsSetMetaData");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				fileOut.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	
	
}

package test.BusTUC;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import test.BusTUC.R;

public class HttpFormat {
    HttpFormat()
    {
    	
    }
    public String[] request(HttpResponse response){
        String result = "";
        String[] contentArray = null;
        try{
            InputStream in = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder str = new StringBuilder();
            StringBuilder content = new StringBuilder(); 
            String line = null;
             System.out.println("IN HTTPFORMAT");
            
          while((line = reader.readLine()) != null){
            	System.out.println("LINE " + line);
            	if(line.endsWith("</body>"))
            	{
            		
            		contentArray = line.split("<br>");
            		content.append(line + "\n");
            	}
                str.append(line + "\n");
            }
            in.close();
            result = str.toString();
            System.out.println("RESULT: " + result + "  " +result.length());
        }catch(Exception ex){
            result = "Error";
            System.out.println("ERROR IN HTTPFORMAT!!!!!!!!!!!!!!");
        }
        for(int i=0; i< contentArray.length; i++)
        {
        	System.out.println("Contentarray: " + contentArray[i]);
        }
        return contentArray;
    }
}
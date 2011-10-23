package test.BusTUC.Queries;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;

import android.widget.Toast;
import test.BusTUC.R;

public class HttpFormat {
    HttpFormat()
    {
    	
    }
    public String[] request(HttpResponse response){
        String result = "";
        String[] contentArray = null;
        try{
        	//System.out.println("FIRST");
            InputStream in = response.getEntity().getContent();
            // System.out.println("SECOND");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "ISO-8859-1"));
            StringBuilder str = new StringBuilder();
            StringBuilder content = new StringBuilder(); 
            String line = null;
            //  System.out.println("IN HTTPFORMAT");
            
          while((line = reader.readLine()) != null)
          {
        	  System.out.println("LINE " + line);
        	  if(line.contains("Beklager") || line.contains("Vennligst") || line.contains("Uforståelig"))
        	  {
        		  result = "Error";
        		  contentArray = new String[5];
        		  contentArray[0] = result;
        		  System.out.println("Error in if in HttpFormat");
        		 
        		  
        		  break;
        	  }
            	if(line.endsWith("</body>"))
            	{
            		
            		contentArray = line.split("<br>");
            		content.append(line + "\n");
            	}
                str.append(line + "\n");
            }
            in.close();
            reader.close();
            result = str.toString();
            System.out.println("RESULT: " + result + "  " +result.length());
        }
        
        
        catch(Exception ex){
            result = "Error";
            System.out.println("ERROR IN HTTPFORMAT!!!!!!!!!!!!!!");
            ex.printStackTrace();
            // Just add "error" as value. Detect in BusTUCApp
            contentArray = new String[5];
            contentArray[0] = result;
        }
        for(int i=0; i< contentArray.length; i++)
        {
        	System.out.println("Contentarray: " + contentArray[i]);
        }
        return contentArray;
    }
}
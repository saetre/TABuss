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
             
            while((line = reader.readLine()) != null){
            	if(line.endsWith("</body>"))
            	{
            		contentArray = line.split("<br>");
            		content.append(line + "\n");
            	}
                str.append(line + "\n");
            }
            in.close();
            result = str.toString();
        }catch(Exception ex){
            result = "Error";
        }
    
        return contentArray;
    }
}
/**
 * Copyright (C) 2010-2012 Magnus Raaum, Lars Moland Eliassen, Christoffer Jun Marcussen, Rune Sætre
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * README:
 * 
 */

package test.BusTUC.Queries;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
//import com.google.gson.Gson;
import org.apache.http.HttpResponse;

import android.widget.Toast;
import test.BusTUC.R;

public class HttpFormat {
	HttpFormat()
	{

	}

	public String requestServer(HttpResponse response)
	{
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
				str.append(line);
			}
			in.close();
			reader.close();
			result = str.toString();
			// Have now the reponse as a string;
			System.out.println("RESULT: " + result + "  " +result.length());

		}


		catch(Exception ex){
			result = "Error";
			ex.printStackTrace();
		}

		return result;
	}


	public StringBuffer requestStandard(HttpResponse response){
		String result = "";
		String[] contentArray = null;
		StringBuffer retBuffer = new StringBuffer();
		try{
			//System.out.println("FIRST");
			InputStream in = response.getEntity().getContent();
			// System.out.println("SECOND");
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "ISO-8859-1"));

			String line = null;
			//  System.out.println("IN HTTPFORMAT");

			while((line = reader.readLine()) != null)
			{
				retBuffer.append(line +"\n");
			}
			in.close();
			reader.close();
		
		
			return retBuffer;
		}
		catch(Exception ex){
			ex.printStackTrace();
		
		}
		return null;
		
	}



}
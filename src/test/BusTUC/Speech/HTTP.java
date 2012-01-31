package test.BusTUC.Speech;

import java.io.File;
import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.Environment;

public class HTTP
{
	public void sendPostByteArray(byte [] buf)
	{
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(
				"http://vm-6114.idi.ntnu.no:1337/SpeechServer/sst");

		try
		{
			MultipartEntity entity = new MultipartEntity();
			//entity.addPart("speechinput", new FileBody((buf, "application/zip"));
			entity.addPart("speechinput", new ByteArrayBody(buf, "Jun.wav"));

			httppost.setEntity(entity);
			String response = EntityUtils.toString(httpclient.execute(httppost)
					.getEntity(), "UTF-8");
			System.out.println("RESPONSE: " + response);
		} catch (ClientProtocolException e)
		{
		} catch (IOException e)
		{
		}
	}
	
	public DummyObj sendPost(String filePath)
	{
		String response = "Fant ikke noe";
		long first = System.nanoTime();
		Calc calc = new Calc();
		DummyObj dummy = new DummyObj();
		HttpClient httpclient = new DefaultHttpClient();
		long second = System.nanoTime() - first;
		//System.out.println("TIME: " + second/1000000000.0);
		File file = new File(Environment.getExternalStorageDirectory(),
				filePath);
		HttpPost httppost = new HttpPost(
				"http://vm-6114.idi.ntnu.no:1337/SpeechServer/sst");

		try
		{
			MultipartEntity entity = new MultipartEntity();
			entity.addPart("speechinput", new FileBody(file, "application/zip"));
			httppost.setEntity(entity);
		   response = EntityUtils.toString(httpclient.execute(httppost)
					.getEntity(), "UTF-8");
			System.out.println("RESPONSE: " + response);
		    dummy = calc.parse(response);
		} catch (ClientProtocolException e)
		{
		} catch (IOException e)
		{
		}
		return dummy;

	}
}
package test.BusTUC.Speech;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.Environment;

public class HTTP
{
	public void sendPostByteArray(byte[] buf)
	{
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(
				"http://vm-6114.idi.ntnu.no:1337/SpeechServer/sst");

		try
		{
			MultipartEntity entity = new MultipartEntity();
			// entity.addPart("speechinput", new FileBody((buf,
			// "application/zip"));
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

	public void sendPostTTS(String input)
	{
		long first = System.nanoTime();
		HttpClient client = new DefaultHttpClient();

		try
		{
			HttpGet httpget = new HttpGet(
					"http://vm-6114.idi.ntnu.no:1337/SpeechServer/tts?textInput="
							+ URLEncoder.encode(input, "UTF-8"));
			HttpResponse response = client.execute(httpget);
			ByteArrayOutputStream outstream = new ByteArrayOutputStream();
			response.getEntity().writeTo(outstream);
			byte [] responseBody = outstream.toByteArray();
			File sdCard = Environment.getExternalStorageDirectory();
			File dir = new File (sdCard.getAbsolutePath() + "/tts");
			if(!dir.exists())dir.mkdirs();
			File file = new File(dir, "tmp.wav");
			FileOutputStream fos = new FileOutputStream(file);
            fos.write(responseBody);
			
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
		// System.out.println("TIME: " + second/1000000000.0);
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
package test.BusTUC.Speech;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.media.AudioRecord;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Toast;

public class Listener implements RecognitionListener
{
	private static final int TIMER_INTERVAL = 120;
	private RandomAccessFile randomAccessWriter;
	private short nChannels = 1;
	private int sRate = 16000;
	private short bSamples = 16;
	private int framePeriod = sRate * TIMER_INTERVAL / 1000;
	private byte[] buf;// [framePeriod * bSamples / 8 *
											// nChannels];
	private byte[] retBuf;
	private int sigPos = 0;
	private int lenCount;
	private Context context;
	private String filePath = "/sdcard/dictionary/liverpool.wav";
	private String filePath2 = "/dictionary/liverpool.wav";
	private String fileName = "liverpool.wav";
	private String directory = "dictionary";
	
	public Listener(Context context)
	{
		this.context = context;
	}
	public byte[] getBuf()
	{
		return buf;
	}

	public void onReadyForSpeech(Bundle params)
	{
		lenCount = 0;
		buf = new byte[100000];	 
		retBuf = new byte[100000];
	}

	public void onBeginningOfSpeech()
	{
	}

	public void onRmsChanged(float rmsdB)
	{
	}

	public void onBufferReceived(byte[] buffer)
	{
		lenCount += buffer.length;
		
		System.out.println("lencount: " + lenCount);
		System.out.println("buflen: " + buf.length);
		if (buf.length > lenCount)
		{
			System.arraycopy(buffer, 0, buf, sigPos, buffer.length);
			sigPos += buffer.length;
			System.out.println("Received bytes");
		}
		retBuf = java.nio.ByteBuffer.wrap(buf).order(java.nio.ByteOrder.LITTLE_ENDIAN).array();

	}

	public void onEndOfSpeech()
	{
		prepare(filePath);
		try
		{
			HTTP http = new HTTP();
			SDCard sd = new SDCard();
			randomAccessWriter.write(retBuf);
			stop();
			System.out.println("File written");
			// Sends a byte array
			//http.sendPostByteArray(buf);
			// Sends a .wav file
			DummyObj response = http.sendPost(filePath2);
			System.out.println("File sent, will now delete from SD-card..");
		//	sd.deleteFileFromSD(fileName, directory);
			Toast.makeText(context, response.getAnswer(), Toast.LENGTH_SHORT).show();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onError(int error)
	{

	}

	public void onResults(Bundle results)
	{
		String str = new String();
		ArrayList data = results
				.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
		for (int i = 0; i < data.size(); i++)
		{
			str += data.get(i);
			System.out.println("results: " + str);

		}
	}

	public void onPartialResults(Bundle partialResults)
	{
	}

	public void onEvent(int eventType, Bundle params)
	{
	}

	public void prepare(String filePath)
	{
		try
		{

			// write file header
			randomAccessWriter = new RandomAccessFile(filePath, "rw");
			randomAccessWriter.setLength(0); // Set file length to
												// 0, to prevent
												// unexpected
												// behavior in case
												// the file already
												// existed
			randomAccessWriter.writeBytes("RIFF");
			randomAccessWriter.writeInt(0); // Final file size not
											// known yet, write 0
			randomAccessWriter.writeBytes("WAVE");
			randomAccessWriter.writeBytes("fmt ");
			randomAccessWriter.writeInt(Integer.reverseBytes(16)); // Sub-chunk
																	// size,
																	// 16
																	// for
																	// PCM
			randomAccessWriter.writeShort(Short.reverseBytes((short) 1)); // AudioFormat,
																			// 1
																			// for
																			// PCM
			randomAccessWriter.writeShort(Short.reverseBytes(nChannels));// Number
																			// of
																			// channels,
																			// 1
																			// for
																			// mono,
																			// 2
																			// for
																			// stereo
			randomAccessWriter.writeInt(Integer.reverseBytes(sRate)); // Sample
																		// rate
			randomAccessWriter.writeInt(Integer.reverseBytes(sRate * bSamples
					* nChannels / 8)); // Byte rate,
										// SampleRate*NumberOfChannels*BitsPerSample/8
			randomAccessWriter.writeShort(Short.reverseBytes((short) (nChannels
					* bSamples / 8))); // Block
										// align,
										// NumberOfChannels*BitsPerSample/8
			randomAccessWriter.writeShort(Short.reverseBytes(bSamples)); // Bits
																			// per
																			// sample
			randomAccessWriter.writeBytes("data");
			randomAccessWriter.writeInt(0); // Data chunk size not
											// known yet, write 0

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void stop()
	{
		try
		{
			randomAccessWriter.seek(4); // Write size to RIFF header
			randomAccessWriter.writeInt(Integer.reverseBytes(36 + lenCount));

			randomAccessWriter.seek(40); // Write size to Subchunk2Size
											// field
			randomAccessWriter.writeInt(Integer.reverseBytes(lenCount));

			randomAccessWriter.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}



}

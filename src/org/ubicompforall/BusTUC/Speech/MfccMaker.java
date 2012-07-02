package org.ubicompforall.BusTUC.Speech;

/**
 * Android Speech Diarization - Calculates the amount of time spent speaking
 * 								by each speaker in a conversation
 * 
 * Copyright (C) 2011  Daniel Di Matteo
 *
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.cmu.sphinx.frontend.Data;
import edu.cmu.sphinx.frontend.DataEndSignal;
import edu.cmu.sphinx.frontend.DoubleData;
import edu.cmu.sphinx.frontend.FloatData;
import edu.cmu.sphinx.frontend.FrontEnd;
import edu.cmu.sphinx.frontend.util.StreamDataSource;
import edu.cmu.sphinx.util.props.ConfigurationManager;

public class MfccMaker
{
	String configFile;
	String inputAudioFile;
	String outputMfccFile;
	FrontEnd frontEnd;

	public MfccMaker(String config, String audio, String outputMfc)
	{
		configFile = config;
		inputAudioFile = audio;
		outputMfccFile = outputMfc;
		frontEnd = null;
	}

	public boolean setupSphinx()
	{
		StreamDataSource audioSource = null;
		List<float[]> allFeatures;
		int featureLength = -1;
		System.out.println("CONFIG: " + configFile);
		ConfigurationManager cm = new ConfigurationManager(configFile);
		try
		{
			frontEnd = (FrontEnd) cm.lookup("mfcFrontEnd");
			audioSource = (StreamDataSource) cm.lookup("streamDataSource");

			audioSource.setInputStream(new FileInputStream(inputAudioFile),
					"audio");
			return true;
		} catch (Exception e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return false;
	}

	public void produceFeatures()
	{
		List<float[]> allFeatures;
		int featureLength = -1;
		allFeatures = new LinkedList<float[]>();
		// write the MFCC features to binary file
		DataOutputStream outStream = null;
		FileOutputStream fos = null;
		// get features from audio
		try
		{
			assert (allFeatures != null);
			Data feature = frontEnd.getData();
			while (!(feature instanceof DataEndSignal))
			{
				if (feature instanceof DoubleData)
				{
					double[] featureData = ((DoubleData) feature).getValues();
					if (featureLength < 0)
					{
						featureLength = featureData.length;
					}
					float[] convertedData = new float[featureData.length];
					for (int i = 0; i < featureData.length; i++)
					{
						convertedData[i] = (float) featureData[i];
					}
					allFeatures.add(convertedData);
				} else if (feature instanceof FloatData)
				{
					float[] featureData = ((FloatData) feature).getValues();
					if (featureLength < 0)
					{
						featureLength = featureData.length;
						// logger.info("Feature length: " + featureLength);
					}
					allFeatures.add(featureData);
				}
				feature = frontEnd.getData();
			}

			/*int size = 0; // fos.write(buffer.array()); // buffer.clear();
			for (float[] f : allFeatures)
			{
				size += f.length;

			}
			ByteBuffer buffer = ByteBuffer.allocate(size + 4);
			buffer.order(ByteOrder.LITTLE_ENDIAN);
			fos = new FileOutputStream(outputMfccFile);
			buffer.putInt(allFeatures.size() * featureLength);
			for (float[] f : allFeatures)
			{
				for (float val : f)
				{
					buffer.putFloat(val); //
					fos.write(buffer.array()); // buffer.clear();
				}

			}
			fos.write(buffer.array());*/

			
			 outStream = new DataOutputStream(new FileOutputStream(
			 outputMfccFile)); outStream.writeInt(allFeatures.size() *
			 featureLength);
			 
			 for (float[] f : allFeatures) { for (float val : f) {
			 outStream.writeFloat(val);
			 
			 }
			 
			 }
			

		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{

				if (outStream != null)
				{
					outStream.close();
				}

			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}

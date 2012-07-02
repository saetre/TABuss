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

package org.ubicompforall.BusTUC.Speech;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Calc
{
	public DummyObj parse(String jsonString)
	{
		DummyObj dummy = new DummyObj();
		JSONObject json_obj;
		try
		{
			json_obj = new JSONObject(jsonString);
			dummy.setAnswer(json_obj.getString("theAnswer"));
			dummy.setSoundInfo(json_obj.getString("soundInfo"));
		} catch (JSONException e)
		{
			e.printStackTrace();
		} catch (NullPointerException ex)
		{
			ex.printStackTrace();
		}
		return dummy;
	}

}

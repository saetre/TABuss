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

	package test.BusTUC.Speech;

public class DummyObj
{
	private String answer;
	// I am lazy. Could store sound info in separate variables. 
	private String soundInfo;
	
	public DummyObj()
	{
		
	}

	public String getAnswer()
	{
		return answer;
	}

	public void setAnswer(String answer)
	{
		this.answer = answer;
	}

	public String getSoundInfo()
	{
		return soundInfo;
	}

	public void setSoundInfo(String soundInfo)
	{
		this.soundInfo = soundInfo;
	}
	
	
}

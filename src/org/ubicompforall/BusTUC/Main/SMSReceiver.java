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

package org.ubicompforall.BusTUC.Main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver
{
	public static String receivedMessage;

	@Override
	public void onReceive(Context context, Intent intent) 
	{
		Bundle bundle = intent.getExtras();

		Object messages[] = (Object[]) bundle.get("pdus");
		SmsMessage smsMessage[] = new SmsMessage[messages.length];
		for (int i = 0; i<messages.length; i++) 
		{
			smsMessage[i] = SmsMessage.createFromPdu((byte[]) messages[i]);
		}
		// Get message
		if(smsMessage[0].getOriginatingAddress().equals("2027"))
		{
			receivedMessage = smsMessage[0].getMessageBody();

			Intent answer = new Intent(Homescreen.context, Answer.class);
			answer.putExtra("sms", receivedMessage);
			System.out.println("Started activity");
			//intent.putExtra("test", buf);
			Homescreen.context.startActivity(answer);
		}

	}



}

package test.BusTUC.Main;

import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver
{
	String receivedMessage;

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
		Toast toast = Toast.makeText(context,
		"Received SMS: " + smsMessage[0].getMessageBody(), Toast.LENGTH_LONG);
		receivedMessage = smsMessage[0].getMessageBody();
		toast.show();
		
	}

}

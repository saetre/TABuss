package test.BusTUC.Main;

import java.util.ArrayList;

import test.BusTUC.Favourites.SDCard;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.widget.EditText;
import android.widget.Toast;

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
		receivedMessage = smsMessage[0].getMessageBody();
		Intent answer = new Intent(Homescreen.context, Answer.class);
		answer.putExtra("sms", receivedMessage);
		System.out.println("Started activity");
		//intent.putExtra("test", buf);
		Homescreen.context.startActivity(answer);
		
	}
	
	

}

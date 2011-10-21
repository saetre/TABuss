package test.BusTUC.Main;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import test.BusTUC.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
public class Database extends SQLiteOpenHelper 
{

	public Database(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	/*
    public static final String DATABASE_NAME = "testi";
    protected Context context;

	
	public Database(Context context) 
	{
        super(context, DATABASE_NAME, null, 1);
        System.out.println("In Constructor");
        this.context = context;
	}


    @Override
    public void onCreate(SQLiteDatabase db) {
            String s;
            System.out.println("In onCreate()");
            try {
                   // Toast.makeText(this, "1", 2000).show();
                    InputStream in = context.getResources().openRawResource(R.drawable.sql2);
                    System.out.println("File opened");
                    DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document doc = builder.parse(in, null);
						System.out.println(doc.getAttributes());
					
                    NodeList statements = doc.getElementsByTagName("statement");
                    System.out.println("Numstatements: " + statements.getLength());
                    for (int i=0; i<statements.getLength(); i++) {
                    	System.out.println("In for-loop");
                            s = statements.item(i).getChildNodes().item(0).getNodeValue();
                            db.execSQL(s);
                    }
                    System.out.println("DB created!");
            } catch (Exception e) {
            	e.printStackTrace();
             //       Toast.makeText(context, t.toString(), 50000).show();
            }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS case");
            System.out.println("OnUpgrade()");
            onCreate(db);
    }*/

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
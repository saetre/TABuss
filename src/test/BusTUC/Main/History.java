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

package test.BusTUC.Main;


import test.BusTUC.R;
import test.BusTUC.Database.DatabaseHelper;
import android.app.ListActivity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
public class History extends ListActivity {
	public static String ID;
	DatabaseHelper dbHelper;
	Button deletebutton;
	String[] columns;
	int [] to;
	SimpleCursorAdapter mAdapter;
	ListView listView;
    @Override
    public void onCreate(Bundle savedInstance)
    {
    	super.onCreate(savedInstance);

          setContentView(R.layout.list);
          dbHelper = new DatabaseHelper(this);
          Cursor cursor = dbHelper.getAllQueries();
          startManagingCursor(cursor);
          columns = new String[] { DatabaseHelper.destination};
          to = new int[] {R.id.destination_entry};

          mAdapter = new SimpleCursorAdapter(this, R.layout.history, cursor, columns, to);
          this.setListAdapter(mAdapter);
    }
}

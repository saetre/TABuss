package test.BusTUC;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

class MapOverlay extends ItemizedOverlay
{
	private Context m_Context;
	private List items;
	private Drawable drawable;
	
	public MapOverlay(Drawable defaultMarker) {
		super(defaultMarker);
		drawable = defaultMarker;
		items = new ArrayList();
		// TODO Auto-generated constructor stub
	}

	public MapOverlay(Drawable defaultMarker, Context context) {
		super(defaultMarker);
		drawable = defaultMarker;
		m_Context = context;
		items = new ArrayList();
		// TODO Auto-generated constructor stub
	}
	
	public void addItem(OverlayItem item)
	{
		items.add(item);
		populate();
		System.out.println("ADDITEMSIZE: " + items.size());
	}
	
	@Override
	protected boolean onTap(int index)
	{
		OverlayItem item = (OverlayItem)items.get(index);
		 /* AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		  dialog.setTitle(item.getTitle());
		  dialog.setMessage(item.getSnippet());
		  dialog.show();*/
		
			Intent intent = new Intent(m_Context, BusList.class);
			m_Context.startActivity(intent);
			BusList.ID = item.getTitle();
			return true;
		
	}
	


	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return (OverlayItem)items.get(i);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return items.size();
	}
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		super.draw(canvas, mapView, shadow);
		boundCenterBottom(drawable);
	}
	
}
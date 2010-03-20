package no.ctryti.dagensatuio;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.text.style.UpdateLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomeActivity extends Activity {

	
	private static final int REFRESH_ID = 1;
	private static final int CLEAR_DB_ID = 2;
	
	private static final String TAG = "HomeActivity";
	
	DatabaseAdapter mDbAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_activity);
		
		String[] weekDays = getResources().getStringArray(R.array.weekdays);
		/* Date textfield 
		Calendar today = Calendar.getInstance(); 

		String[] months = getResources().getStringArray(R.array.months);
		String buildToday = new String(""+weekDays[today.get(Calendar.DAY_OF_WEEK) - 1] +", " 
				+today.get(Calendar.DAY_OF_MONTH) +"." +months[today.get(Calendar.MONTH)]);
		TextView tv = (TextView) findViewById(R.id.period);
		tv.setText(buildToday);
		*/
		
		/* The list of dishes */
		mDbAdapter = new DatabaseAdapter(this);
		//ArrayList<DinnerItem> items = mDbAdapter.getAllFromPlace("SV Kafeen");
		ArrayList<DinnerItem> items = mDbAdapter.getAllFromPlaceAtDay("SV Kafeen", "Mandag");
		TextView top_tv = (TextView)findViewById(R.id.period);
		
		top_tv.setText(items.get(0).getPeriod());
		
		System.out.println("Items:");
		for(DinnerItem item : items)
			Log.i(TAG, item.getDescription());
		
		ListView list = (ListView)findViewById(R.id.dish_list);
	
		if(list != null) {
			DinnerItemAdapter adapter = new DinnerItemAdapter(this, R.layout.custom_list_row, items);
			list.setAdapter(adapter);
		}

		/*Grid of days*/
		ArrayList<String> icons = new ArrayList<String>();
		for (int i = 1; i < 6; i++) {
			icons.add(weekDays[i]);
		}
		GridView days = (GridView) findViewById(R.id.days_list);
		days.setAdapter(new ImageAdapter(this, R.layout.days_item, icons));
		days.setOnItemClickListener(new GridItemListener(this));
	}

	private class ImageAdapter extends BaseAdapter {
		private Context mCtx;
		private int mRowResID;
		private List<String> mList;
		
		public ImageAdapter(Context ctx, int rowResID, List<String> list){
			this.mCtx = ctx;
			this.mRowResID = rowResID;
			this.mList = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v;
			String day = mList.get(position);
			if(convertView==null){
				v = View.inflate(mCtx, R.layout.days_item, null);
				TextView tv = (TextView) v.findViewById(R.id.day_text);
				tv.setText(day);

			} else {
				v = convertView;
			}
			return v;
		}

	}

	protected class DinnerItemAdapter extends BaseAdapter {

		private Context mCtx;
		private List<DinnerItem> mList;
		
		public DinnerItemAdapter(Context ctx, int rowResID, List<DinnerItem> list) {
			mCtx = ctx;
			mList = list;
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return mList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int pos, View convertView, ViewGroup parent) {
			DinnerItem item = mList.get(pos);
			View row = convertView;  
			if(row == null)	
				row = View.inflate(mCtx, R.layout.custom_list_row, null);
			
			TextView type = (TextView)row.findViewById(R.id.type);
			type.setText(item.getType());
			TextView desc = (TextView)row.findViewById(R.id.desc);
			desc.setText(item.getDescription());
			return row;
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, REFRESH_ID, Menu.NONE, "Refresh");
		menu.add(Menu.NONE, CLEAR_DB_ID, Menu.NONE, "Clear database");
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {

		case REFRESH_ID:
			new RefreshDbTask(mDbAdapter).execute();
			break;
		case CLEAR_DB_ID:
			mDbAdapter.reCreateDatabase();
			break;
		}
		return true;
	}
	
	private class GridItemListener implements AdapterView.OnItemClickListener {
		Context mCtx;
		
		public GridItemListener(Context ctx) {
			mCtx = ctx;
		}

		@Override
		public void onItemClick(AdapterView parent, View txtView, int pos,
				long rowID) {
			String[] mDays = getResources().getStringArray(R.array.weekdays);
			ArrayList<DinnerItem> items = mDbAdapter.getAllFromPlaceAtDay("SV Kafeen", mDays[pos+1]);
			ListView list = (ListView)findViewById(R.id.dish_list);
		
			if(list != null) {
				DinnerItemAdapter adapter = new DinnerItemAdapter(mCtx, R.layout.custom_list_row, items);
				list.setAdapter(adapter);
			}
			
		}
		
	}
}

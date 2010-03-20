package no.ctryti.dagensatuio;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.SimpleAdapter.ViewBinder;

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

		/* The list of dishes */
		mDbAdapter = new DatabaseAdapter(this);
		//ArrayList<DinnerItem> items = mDbAdapter.getAllFromPlace("SV Kafeen");


		if (mDbAdapter == null) { 
			System.out.println("The Database is empty");
			new RefreshDbTask(mDbAdapter).execute();
		} else if (mDbAdapter != null) {
			ArrayList<DinnerItem> items = mDbAdapter.getAllFromPlaceAtDay("SV Kafeen", "Mandag");
			if (items.size() > 0) {
				TextView home_bottom = (TextView) findViewById(R.id.home_bottom);
				home_bottom.setText("SV Kafeen");
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

				/*Grid of days */
				ArrayList<String> icons = new ArrayList<String>();
				for (int i = 1; i < 6; i++) {
					icons.add(weekDays[i]);
				}
				GridView days = (GridView) findViewById(R.id.days_list);
				days.setAdapter(new ImageAdapter(this, R.layout.days_item, icons));
				days.setOnItemClickListener(new GridItemListener(this));

				/* Café button (home icon) */
				RelativeLayout right_layout = (RelativeLayout) findViewById(R.id.right_button_layout);
				ImageButton home_button = (ImageButton) findViewById(R.id.right_button);
				home_button.setOnClickListener(new HomeButtonListener(this));
			}
		}

	}

	private class HomeButtonListener implements View.OnClickListener {
		private Context mCtx;


		public HomeButtonListener(Context ctx) {
			this.mCtx = ctx;
		}


		/* TODO: Needs severe improvement */
		@Override
		public void onClick(View v) {
			AlertDialog.Builder placesDialog = new AlertDialog.Builder(mCtx);
			String[] places = getResources().getStringArray(R.array.placesnames);
			placesDialog.setTitle("Velg et sted");
			placesDialog.setItems(places, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					String[] places = getResources().getStringArray(R.array.placesnames);
					ArrayList<DinnerItem> items = mDbAdapter.getAllFromPlaceAtDay(places[which], "Mandag");
					ListView list = (ListView)findViewById(R.id.dish_list);
					if(list != null) {
						DinnerItemAdapter adapter = new DinnerItemAdapter(mCtx, R.layout.custom_list_row, items);
						list.setAdapter(adapter);
						TextView home_bottom = (TextView) findViewById(R.id.home_bottom);
						home_bottom.setText(places[which]);
					}
					dialog.cancel();
				}
			});

			System.out.println("Home Button Clicked");
			AlertDialog al = placesDialog.create();
			al.show();
		}

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
				TextView txt = (TextView) v.findViewById(R.id.day_text);
				txt.setText(day);
				//b1.setOnTouchListener(new ButtonListener(mCtx, day));

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
		menu.add(Menu.NONE, REFRESH_ID, Menu.NONE, "Refresh").setIcon(R.drawable.refresh);
		menu.add(Menu.NONE, CLEAR_DB_ID, Menu.NONE, "Clear database").setIcon(R.drawable.trashcan);
		return true; 
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {

		case REFRESH_ID:
			RefreshDbTask rDT = new RefreshDbTask(mDbAdapter);
			rDT.execute(null);
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
		public void onItemClick(AdapterView parent, View v, int pos,
				long rowID) {
			RelativeLayout rl = (RelativeLayout) v;
			TextView txt = (TextView) rl.getChildAt(0);
			String day = txt.getText().toString();
			TextView tv = (TextView) findViewById(R.id.home_bottom);
			String place = tv.getText().toString();
			ArrayList<DinnerItem> items = mDbAdapter.getAllFromPlaceAtDay(place, day);
			ListView list = (ListView)findViewById(R.id.dish_list);
			if(list != null) {
				DinnerItemAdapter adapter = new DinnerItemAdapter(mCtx, R.layout.custom_list_row, items);
				list.setAdapter(adapter);
			}
		}
	}
}

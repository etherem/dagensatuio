package no.ctryti.dagensatuio;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class HomeActivity extends Activity {

	DatabaseAdapter mDbAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.home_activity);

		mDbAdapter = new DatabaseAdapter(this);
		mDbAdapter.open();

		ArrayList<DinnerItem> items = mDbAdapter.getAllFromPlace("Frederikke kafé");

		System.out.println("Items:");
		for(DinnerItem item : items)
			System.out.println(item.getDescription());

		ListView innerList = (ListView)findViewById(R.id.dish_list);

		if(innerList != null) {

			DinnerItemAdapter adapter = new DinnerItemAdapter(this, R.layout.custom_list_row, items);
			innerList.setAdapter(adapter);
		}

		/*Test code start here*/
		ArrayList<Integer> icons = new ArrayList<Integer>();
		icons.add(R.drawable.icon_monday);
		icons.add(R.drawable.icon_tuesday);
		icons.add(R.drawable.icon_wednesday);
		icons.add(R.drawable.icon_thursday);
		icons.add(R.drawable.icon_friday);
		GridView days = (GridView) findViewById(R.id.days_list);
		days.setAdapter(new ImageAdapter(this, R.layout.day_icon, icons));

	}

	private class ImageAdapter extends BaseAdapter {
		private Context mCtx;
		private int mRowResID;
		private List<Integer> mList;
		
		public ImageAdapter(Context ctx, int rowResID, List<Integer> list){
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
			int iconDrawableID = mList.get(position);
			if(convertView==null){
				v = View.inflate(mCtx, R.layout.day_icon, null);
				ImageView iv = (ImageView)v.findViewById(R.id.day_icon);
				iv.setImageResource(iconDrawableID);
				iv.setAdjustViewBounds(true);

			} else {
				v = convertView;
			}
			return v;
		}

	}

	private class DinnerItemAdapter extends BaseAdapter {

		private Context mCtx;
		private List<DinnerItem> mList;
		private int mRowResID;

		public DinnerItemAdapter(Context ctx, int rowResID, List<DinnerItem> list) {
			mCtx = ctx;
			mRowResID = rowResID;
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
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			DinnerItem item = mList.get(arg0);
			//LayoutInflater inflater = LayoutInflater.from(mCtx);

			View inflatedView = View.inflate(mCtx, R.layout.custom_list_row, null);

			//View v = inflater.inflate(mRowResID, arg2);
			TextView type = (TextView)inflatedView.findViewById(R.id.type);
			type.setText(item.getType());
			TextView desc = (TextView)inflatedView.findViewById(R.id.desc);
			desc.setText(item.getDescription());

			return inflatedView;
		}

	}
}

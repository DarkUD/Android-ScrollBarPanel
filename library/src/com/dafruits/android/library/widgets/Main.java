package com.dafruits.android.library.widgets;

import com.dafruits.android.library.widgets.ExtendedListView.OnPositionChangedListener;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class Main extends Activity implements OnPositionChangedListener {
    /** Called when the activity is first created. */
	
	private ExtendedListView mListView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mListView = (ExtendedListView) findViewById(android.R.id.list);
		mListView.setAdapter(new DummyAdapter());
		mListView.setCacheColorHint(Color.TRANSPARENT);
		mListView.setOnPositionChangedListener(this);
    }
    
    private class DummyAdapter extends BaseAdapter {

		private int mNumDummies = 100;

		public int getCount() {
			return mNumDummies;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(Main.this).inflate(R.layout.list_item, parent,
						false);
			}

			TextView textView = (TextView) convertView;
			textView.setText("" + position);

			return convertView;
		}
	}
	
	public void onPositionChanged(ExtendedListView listView, int firstVisiblePosition, View scrollBarPanel) {
		
		DafruitsPanel panel = ((DafruitsPanel) scrollBarPanel);
		panel.setText("Position " + firstVisiblePosition);
		
		panel.setPosition(firstVisiblePosition);
		panel.setTotal(listView.getCount());
		
	}
}
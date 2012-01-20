package ch.almana.android.stillmeter.view.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.database.DataSetObserver;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import ch.almana.android.stillmeter.helper.Formater;

public class TimeSpinnerAdapter implements SpinnerAdapter {

	private static final long MIN_IN_MILLIES = 1000 * 60;

	private final Context ctx;
	private final ArrayList<Long> timeArray;

	private final LayoutInflater layoutInflator;

	public TimeSpinnerAdapter(Context ctx, long time) {
		this.ctx = ctx;
		this.layoutInflator = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.timeArray = new ArrayList<Long>();
		int idx = 0;
		timeArray.add(idx++, time);
		for (long i = 1; i < 16; i++) {
			timeArray.add(idx++, i * MIN_IN_MILLIES);
		}
		for (long i = 1; i < 9; i++) {
			timeArray.add(idx++, i * 5 * MIN_IN_MILLIES + 15 * MIN_IN_MILLIES);
		}
	}

	@Override
	public int getCount() {
		return timeArray.size();
	}

	@Override
	public Object getItem(int position) {
		return timeArray.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view = (convertView != null) ? (TextView) convertView : createView(parent);
		view.setText(getFormatedItem(position));
		return view;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		TextView view = (convertView != null) ? (TextView) convertView : createView(parent);
		view.setText(getFormatedItem(position));
		return view;
	}

	private CharSequence getFormatedItem(int position) {
		Long time = timeArray.get(position);
		return Formater.timeElapsed(time);
	}

	private TextView createView(ViewGroup parent) {
		TextView item;
		if (parent instanceof Spinner) {
			item = (TextView) layoutInflator.inflate(android.R.layout.simple_spinner_item, parent, false);
		} else {
			item = (TextView) layoutInflator.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
		}
		item.setSingleLine();
		item.setEllipsize(TextUtils.TruncateAt.END);
		return item;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub

	}

}

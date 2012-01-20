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
import ch.almana.android.stillmeter.model.BreastModel.Position;

public class BreastSpinnerAdapter implements SpinnerAdapter {

	private final Context ctx;
	private final LayoutInflater layoutInflator;
	private final ArrayList<Position> breastArray;

	public BreastSpinnerAdapter(Context ctx) {
		super();
		this.ctx = ctx;
		this.layoutInflator = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.breastArray = new ArrayList<Position>();
		breastArray.add(0, Position.none);
		breastArray.add(1, Position.left);
		breastArray.add(2, Position.right);
	}

	@Override
	public int getCount() {
		return breastArray.size();
	}

	public int getPositionId(Position p) {
		for (int i = 0; i < breastArray.size(); i++) {
			if (p == breastArray.get(i)) {
				return i;
			}
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return breastArray.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView view = (convertView != null) ? (TextView) convertView : createView(parent);
		view.setText(breastArray.get(position).toString());
		return view;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		TextView view = (convertView != null) ? (TextView) convertView : createView(parent);
		view.setText(breastArray.get(position).toString());
		return view;
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

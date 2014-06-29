package com.example.chat;

import android.content.Context;
import android.renderscript.Element;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ChatInfoAdapter extends BaseAdapter {

	public Context myContext;
	public String[] Elements;
	public int resourceID;
	TextView childValues;

	public ChatInfoAdapter(Context context, String[] elems, int resource) {

		this.myContext = context;
		this.Elements = elems;
		this.resourceID = resource;
	}
	
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return Elements[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null){
			LayoutInflater inflator = (LayoutInflater)myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflator.inflate(resourceID, parent);
		}
		TextView view = (TextView)convertView;
		view.setText(Elements[position]);
		
		return null;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Elements.length;
	}
	
	/*
	 * @Override public View getChildView(int groupPosition, int childPosition,
	 * boolean isLastChild, View convertView, ViewGroup parent) { // TODO
	 * Auto-generated method stub
	 * 
	 * if(convertView==null){
	 * 
	 * LayoutInflater inflator =
	 * (LayoutInflater)myContext.getSystemService(Context
	 * .LAYOUT_INFLATER_SERVICE); convertView =
	 * inflator.inflate(R.layout.child_rows, parent);
	 * 
	 * } childValues = (TextView)convertView.findViewById(R.id.rowValues);
	 * childValues.setText(childElements[groupPosition][childPosition]);
	 * 
	 * return convertView; }
	 */

	/*
	 * @Override public int getChildrenCount(int groupPosition) { // TODO
	 * Auto-generated method stub return groupElements[groupPosition].length();
	 * }
	 */

	/*
	 * @Override public Object getGroup(int groupPosition) { // TODO
	 * Auto-generated method stub return groupElements[groupPosition]; }
	 */

	/*
	 * @Override public int getGroupCount() { // TODO Auto-generated method stub
	 * return groupElements.length; }
	 */

	/*
	 * @Override public long getGroupId(int groupPosition) { // TODO
	 * Auto-generated method stub return 0; }
	 */

	/*
	 * @Override public View getGroupView(int groupPosition, boolean isExpanded,
	 * View convertView, ViewGroup parent) { // TODO Auto-generated method stub
	 * 
	 * if(convertView==null){ LayoutInflater inflator =
	 * (LayoutInflater)myContext
	 * .getSystemService(Context.LAYOUT_INFLATER_SERVICE); convertView =
	 * inflator.inflate(R.layout.group_rows, null); } TextView group =
	 * (TextView)convertView.findViewById(R.id.groupId);
	 * group.setText(groupElements[groupPosition]);
	 * 
	 * return convertView; }
	 */

	/*
	 * @Override public boolean hasStableIds() { // TODO Auto-generated method
	 * stub return false; }
	 */

	/*
	 * @Override public boolean isChildSelectable(int groupPosition, int
	 * childPosition) { // TODO Auto-generated method stub return true; }
	 */

}

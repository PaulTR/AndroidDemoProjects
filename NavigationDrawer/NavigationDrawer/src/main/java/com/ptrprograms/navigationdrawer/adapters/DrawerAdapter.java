package com.ptrprograms.navigationdrawer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ptrprograms.navigationdrawer.R;
import com.ptrprograms.navigationdrawer.models.DrawerItem;

/**
 * Created by PaulTR on 5/12/14.
 */
public class DrawerAdapter extends ArrayAdapter<DrawerItem> {

	public DrawerAdapter( Context context ) {
		this( context, 0 );
	}

	public DrawerAdapter(Context context, int resource) {
		super(context, resource);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if( convertView == null ) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from( getContext() ).inflate( R.layout.view_drawer_item, parent, false );
			holder.drawerItemLabel = (TextView) convertView.findViewById( R.id.drawer_text );
			holder.drawerItemIcon = (ImageView) convertView.findViewById( R.id.drawer_icon );
			convertView.setTag( holder );
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.drawerItemLabel.setText( getItem( position ).getDrawerText() );
		holder.drawerItemIcon.setImageResource( getItem( position ).getDrawerIcon() );

		return convertView;
	}

	public class ViewHolder {
		public TextView drawerItemLabel;
		public ImageView drawerItemIcon;
	}
}

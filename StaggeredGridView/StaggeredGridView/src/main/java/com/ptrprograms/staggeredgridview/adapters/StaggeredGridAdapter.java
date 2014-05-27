package com.ptrprograms.staggeredgridview.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.etsy.android.grid.util.DynamicHeightImageView;
import com.ptrprograms.staggeredgridview.R;
import com.ptrprograms.staggeredgridview.models.GridItem;
import com.squareup.picasso.Picasso;

/**
 * Created by PaulTR on 5/12/14.
 */
public class StaggeredGridAdapter extends ArrayAdapter<GridItem> {

	public StaggeredGridAdapter( Context context ) {
		this( context, 0 );
	}

	public StaggeredGridAdapter(Context context, int resource) {
		super(context, resource);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		StaggeredViewHolder holder;
		if( convertView == null ) {
			convertView = LayoutInflater.from(getContext()).inflate( R.layout.view_staggered_item, parent, false );
			holder = new StaggeredViewHolder();
			holder.imageView = (DynamicHeightImageView) convertView.findViewById( R.id.dynamic_image_view );
			holder.titleTextView = (TextView) convertView.findViewById( R.id.title_text_view );
			holder.subtitleTextView = (TextView) convertView.findViewById( R.id.subtitle_text_view );
			convertView.setTag( holder );
		} else {
			holder = (StaggeredViewHolder) convertView.getTag();
		}

		Picasso.with(getContext())
				.load( getItem( position ).getUrl() )
				.into( holder.imageView );

		holder.titleTextView.setText( getItem( position ).getTitle() );
		holder.subtitleTextView.setText( getItem( position ).getSubtitle() );
		holder.imageView.setHeightRatio( getItem( position ).getRatio() );

		final String itemText = holder.titleTextView.getText().toString();
		convertView.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Toast.makeText( getContext(), itemText, Toast.LENGTH_SHORT ).show();
			}
		});

		return convertView;
	}

	private class StaggeredViewHolder {
		public DynamicHeightImageView imageView;
		public TextView titleTextView;
		public TextView subtitleTextView;
	}
}
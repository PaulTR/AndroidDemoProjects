package com.ptrprograms.staggeredgridview.utils;

import com.ptrprograms.staggeredgridview.models.GridItem;
import java.util.Random;

/**
 * Created by PaulTR on 5/12/14.
 */
public class ItemGenerator {

	public static GridItem getItem( int num ) {
		GridItem item = new GridItem();

		Random random = new Random();
		switch( random.nextInt( 5 ) ) {
			case 0: {
				item.setUrl( "http://www.duffzone.org/references/T2/terminator5.gif" );
				break;
			}
			case 1: {
				item.setUrl( "http://i268.photobucket.com/albums/jj27/whatistechnoagain/simpsons-bombardment.png" );
				break;
			}
			case 2: {
				item.setUrl( "http://ramroddynastyleague.files.wordpress.com/2011/04/i_choo-choo-choose_you-748156.gif" );
				break;
			}
			case 3: {
				item.setUrl( "http://img3.wikia.nocookie.net/__cb20100807100438/lossimpson/es/images/9/95/LisaFloreda.png" );
				break;
			}
			case 4: {
				item.setUrl( "http://img1.wikia.nocookie.net/__cb20120318040846/springfieldbound/images/7/7d/Mr._Plow_(Official_Image).png" );
			}
		}

		item.setTitle( "Title: " + num );
		item.setSubtitle( "Subtitle: " + num );
		item.setRatio( ( new Random().nextDouble() / 2.0 ) + 1 );
		return item;
	}
}
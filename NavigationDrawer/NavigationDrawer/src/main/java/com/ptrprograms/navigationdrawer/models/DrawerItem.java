package com.ptrprograms.navigationdrawer.models;

/**
 * Created by PaulTR on 5/12/14.
 */
public class DrawerItem {
	private int drawerIcon;
	private String drawerText;

	public DrawerItem( String drawerText, int drawerIcon ) {
		this.drawerIcon = drawerIcon;
		this.drawerText = drawerText;
	}

	public int getDrawerIcon() {
		return drawerIcon;
	}

	public void setDrawerIcon(int drawerIcon) {
		this.drawerIcon = drawerIcon;
	}

	public String getDrawerText() {
		return drawerText;
	}

	public void setDrawerText(String drawerText) {
		this.drawerText = drawerText;
	}
}

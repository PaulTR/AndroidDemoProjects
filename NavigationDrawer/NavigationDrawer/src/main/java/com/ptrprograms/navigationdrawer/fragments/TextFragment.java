package com.ptrprograms.navigationdrawer.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ptrprograms.navigationdrawer.R;

/**
 * Created by PaulTR on 5/12/14.
 */
public class TextFragment extends Fragment {

	public static Fragment getInstance() {
		TextFragment fragment = new TextFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_text, container, false);
	}
}

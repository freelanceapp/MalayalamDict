package com.elamblakatt.dict_eng_malayalam;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

public class TabAdapter extends FragmentStatePagerAdapter {

	private int mCount = 3;
	private List<Fragment> mFragments;

	public TabAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);

		mFragments=fragments;
	}

	@Override
	public Fragment getItem(int index)
	{
		return mFragments.get(index);
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return mFragments.size();
	}


}
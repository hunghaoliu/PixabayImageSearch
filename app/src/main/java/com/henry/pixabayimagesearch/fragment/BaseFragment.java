/*******************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.henry.pixabayimagesearch.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.AbsListView;

import com.henry.pixabayimagesearch.Constants;
import com.henry.pixabayimagesearch.Downloader.BackgroundDownloadTask;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public abstract class BaseFragment extends Fragment {
	private static final String LOG_TAG = "Pixabay";
	protected AbsListView listView;
	protected String[] mURLs = null;
	private BroadcastReceiver mDownloadFinishedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(LOG_TAG, "onReceive");

			mURLs = BackgroundDownloadTask.mURLs.toArray(new String[0]);
			onDataSetChanged();
		}
	};
	IntentFilter intentFilter = new IntentFilter(Constants.INTENT);

	abstract void onDataSetChanged();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getActivity().registerReceiver(mDownloadFinishedReceiver, intentFilter);

	}

	@Override
	public void onStop() {
		super.onStop();
		getActivity().unregisterReceiver(mDownloadFinishedReceiver);
	}
}

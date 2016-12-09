package com.henry.pixabayimagesearch.Downloader;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.henry.pixabayimagesearch.Constants;
import com.henry.pixabayimagesearch.Network.HttpHelper;
import com.henry.pixabayimagesearch.Pixabay.PixabayModel;

import java.util.ArrayList;

/**
 * Created by hunghao on 2016/12/9.
 */

public class BackgroundDownloadTask extends AsyncTask<String, Void, ArrayList<String>> {
    public static ArrayList<String> mURLs = null;
    protected Context mContext;

    public BackgroundDownloadTask(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected ArrayList<String> doInBackground(String... url) {
        ArrayList<String> urls = new ArrayList<String>();

        PixabayModel model = HttpHelper.loadJSON(url[0]);

        final int nPatterns = model.getHits().size();

        try {
            for (int i = 0; i < nPatterns; i++) {
                urls.add(model.getHits().get(i).getPreviewURL());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return urls;
    }

    @Override
    protected void onPostExecute(ArrayList<String> urls) {
        mURLs = urls;

        Intent intentFilter = new Intent(Constants.INTENT);
        mContext.sendBroadcast(intentFilter);
        Log.d("Back", "send broadcast");
        //PixabayListAdapter adapter = new PixabayListAdapter(MainActivity.this, urls);
        //mListView.setAdapter(adapter);
        //mGridView.setAdapter(adapter);
    }
}

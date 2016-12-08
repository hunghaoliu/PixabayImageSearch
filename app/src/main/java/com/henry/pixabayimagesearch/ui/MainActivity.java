package com.henry.pixabayimagesearch.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.henry.pixabayimagesearch.Adapter.PixabayListAdapter;
import com.henry.pixabayimagesearch.App;
import com.henry.pixabayimagesearch.Bitmap.PixabayListLoader;
import com.henry.pixabayimagesearch.Network.HttpHelper;
import com.henry.pixabayimagesearch.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lucasr.smoothie.AsyncGridView;
import org.lucasr.smoothie.AsyncListView;
import org.lucasr.smoothie.ItemManager;

import java.util.ArrayList;

import uk.co.senab.bitmapcache.BitmapLruCache;

public class MainActivity extends AppCompatActivity {
    private AsyncListView mListView;
    private AsyncGridView mGridView;
    private boolean isGrid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isGrid = !isGrid;
                updateLayout();
            }
        });

        mListView = (AsyncListView) findViewById(R.id.list);
        mGridView = (AsyncGridView) findViewById(R.id.grid);
        updateLayout();

        BitmapLruCache cache = App.getInstance(this).getBitmapCache();
        PixabayListLoader loader = new PixabayListLoader(cache);

        ItemManager.Builder builder = new ItemManager.Builder(loader);
        builder.setPreloadItemsEnabled(true).setPreloadItemsCount(5);
        builder.setThreadPoolSize(4);

        mListView.setItemManager(builder.build());

        mGridView.setItemManager(builder.build());

        new LoadPatternsListTask().execute();
    }

    private void updateLayout() {
        if (!isGrid) {
            mListView.setVisibility(View.VISIBLE);
            mGridView.setVisibility(View.GONE);
        } else {
            mListView.setVisibility(View.GONE);
            mGridView.setVisibility(View.VISIBLE);
        }
    }

    private class LoadPatternsListTask extends AsyncTask<Void, Void, ArrayList<String>> {
        static final int NUM_PATTERNS = 100;
        static final String URL = "http://www.colourlovers.com/api/patterns/new?format=json&numResults=" + NUM_PATTERNS;

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            ArrayList<String> urls = new ArrayList<String>();

            JSONArray patternsArray = HttpHelper.loadJSON(URL);
            final int nPatterns = patternsArray.length();

            try {
                for (int i = 0; i < nPatterns; i++) {
                    JSONObject patternInfo = (JSONObject) patternsArray.get(i);
                    urls.add(patternInfo.getString("imageUrl"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return urls;
        }

        @Override
        protected void onPostExecute(ArrayList<String> urls) {
            PixabayListAdapter adapter = new PixabayListAdapter(MainActivity.this, urls);
            mListView.setAdapter(adapter);
            mGridView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

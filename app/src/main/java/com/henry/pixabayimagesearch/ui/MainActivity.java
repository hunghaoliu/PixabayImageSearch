package com.henry.pixabayimagesearch.ui;

import android.app.SearchManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
    private static final String LOG_TAG = "PixabayImageSearch";
    private AsyncListView mListView;
    private AsyncGridView mGridView;
    private SearchView searchView;
    private boolean isGrid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mListView = (AsyncListView) findViewById(R.id.list);
        mGridView = (AsyncGridView) findViewById(R.id.grid);
        updateLayout();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isGrid = !isGrid;
                updateLayout();
            }
        });

        BitmapLruCache cache = App.getInstance(this).getBitmapCache();
        PixabayListLoader loader = new PixabayListLoader(cache);

        ItemManager.Builder builder = new ItemManager.Builder(loader);
        builder.setPreloadItemsEnabled(true).setPreloadItemsCount(5);
        builder.setThreadPoolSize(4);
        ItemManager manager = builder.build();

        mListView.setItemManager(manager);
        mGridView.setItemManager(manager);
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);

        if (searchItem != null) {
            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    //some operation
                    Log.d(LOG_TAG, "onClose is clicked");

                    return false;
                }
            });
            searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //some operation
                }
            });
            EditText searchPlate = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            searchPlate.setHint("Search");
            View searchPlateView = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
            searchPlateView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
            // use this method for search process
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // use this method when query submitted
                    //Toast.makeText(getApplicationContext(), query, Toast.LENGTH_SHORT).show();
                    new LoadPatternsListTask().execute();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // use this method for auto complete search process
                    return false;
                }
            });
            SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        }
        return super.onCreateOptionsMenu(menu);
    }

}

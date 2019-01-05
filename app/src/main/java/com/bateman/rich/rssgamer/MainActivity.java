package com.bateman.rich.rssgamer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bateman.rich.rmblibrary.persistence.SharedAppData;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AsyncTaskRssDownload.AsyncDownloadComplete,
    RssArticleRecyclerViewItemClickListener.OnItemClickListener {

    private static final String TAG = "MainActivity";
    private final SharedAppData m_sharedAppData = new SharedAppData();

    private final RssFeedManager m_rssFeedManager = new RssFeedManager();
    private RssArticleRecyclerViewAdapter m_rssAdapter;

    private ProgressBar m_progressBar;
    private TextView m_progressTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: start");
        super.onCreate(savedInstanceState);

        // Below didn't seem to help when I was dealing with Picasso runtime crashes... but it's also possible
        // my log was so full of entries that no message appeared?  During debugging, I never hit this breakpoint.
//        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//            @Override
//            public void uncaughtException(Thread thread, Throwable ex) {
//                Log.e(TAG, "uncaughtException: " + ex.getMessage() );
//            }
//        });

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        m_progressBar = findViewById(R.id.m_progressBar);
        m_progressTextView = findViewById(R.id.m_txtProgress);

        updateRssFeedManagerWithEnabledSources();
        setupRecyclerView();
        downloadRssData();
        Log.d(TAG, "onCreate: end");
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: start");
        super.onResume();
        restoreSavedMenuData();
        Log.d(TAG, "onResume: end");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        m_sharedAppData.load(getApplicationContext());

        // Inflate the menu; this adds items to the action bar if it is present.
        // use code like below if you want to use a designed menu.
        // getMenuInflater().inflate(R.menu.menu_main, menu);

        for(RssSource source : m_rssFeedManager.getRssSourceList()) {
            int id = source.getId();
            String userFriendlyName = source.getFriendlyKey();

            MenuItem menuItem = menu.add(Menu.NONE, id, Menu.NONE, userFriendlyName);
            menuItem.setCheckable(true);

            boolean isChecked = true;
            if(m_sharedAppData.hasKey(userFriendlyName)) {
                isChecked = m_sharedAppData.getBoolean(userFriendlyName);
            }
            menuItem.setChecked(isChecked);

            // Code for keeping the menu OPEN after user checks an item (otherwise menu closes after
            // each click, which is annoying)
            // https://stackoverflow.com/questions/52176838/how-to-hold-the-overflow-menu-after-i-click-it/52177919#52177919
            // setShowAsAction... marks the item as having expandable/collapsible behavior so it will call setOnActionExpandListener
            // setActionView.... just provides a dummy view we will never let expand.
            // In the onActionExpandListener, always return false to suppress expansion and collapsing.
            // Menu remains open.
            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
            menuItem.setActionView(new View(this));

            menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem menuItem) {
                    return false;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                    return false;
                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int menuId = item.getItemId();
        boolean isChecked = item.isChecked();
        isChecked = !isChecked;
        item.setChecked(isChecked);

        String key = m_rssFeedManager.getKeyFromId(menuId);
        m_sharedAppData.load(getApplicationContext());
        m_sharedAppData.putBoolean(key, isChecked);

        RssSource rssSource = m_rssFeedManager.getRssSourceList().get(menuId - 1);
        rssSource.setEnabled(isChecked);

        if(isChecked) {
            // redownload from the feed.
            downloadRssData(rssSource);
        } else {
            m_rssAdapter.purgeEntriesFromSource(key);
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView() {
        Log.d(TAG, "setupRecyclerView: start");

        RecyclerView recyclerView = findViewById(R.id.m_recyclerViewRssEntries);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnItemTouchListener(new RssArticleRecyclerViewItemClickListener(this, recyclerView, this));

        m_rssAdapter = new RssArticleRecyclerViewAdapter(this);
        recyclerView.setAdapter(m_rssAdapter);

        Log.d(TAG, "setupRecyclerView: end");
    }

    private void downloadRssData() {
        Log.d(TAG, "downloadRssData: start");
        AsyncTaskRssDownload downloader = new AsyncTaskRssDownload(this);

        ArrayList<RssSource> sourceList = m_rssFeedManager.getEnabledRssSourcesList();
        int numItems = sourceList.size();
        Log.d(TAG, "downloadRssData: there are " + numItems + " rss feed(s) about to download.");
        m_progressTextView.setText("Downloading " + numItems + " rss feed(s)");
        m_progressBar.setMax(numItems);
        m_progressBar.setProgress(0);


        downloader.execute(sourceList);
        Log.d(TAG, "downloadRssData: end");
    }

    private void downloadRssData(RssSource source) {
        AsyncTaskRssDownload downloader = new AsyncTaskRssDownload(this);
        ArrayList<RssSource> sourceList = new ArrayList<>();
        sourceList.add(source);
        downloader.execute(sourceList);
    }

    @Override
    public void onRssDownloadProgress(int progress) {
        m_progressTextView.setText(progress + " out of " + m_progressBar.getMax() + " feed(s) downloaded.");
        m_progressBar.setProgress(progress);
    }

    @Override
    public void onRssDownloadComplete(ArrayList<RssSource> rssSources) {
        Log.d(TAG, "onRssDownloadComplete: start");

        m_progressBar.setVisibility(View.GONE);
        m_progressTextView.setVisibility(View.GONE);

        for(RssSource rssSource : rssSources) {
            RssParser rssParser = new RssParser();
            rssParser.parse(rssSource);
            ArrayList<RssEntry> rssEntryList = rssParser.getRssEntries();
            m_rssAdapter.addRssEntries(rssEntryList);
        }

        Log.d(TAG, "onRssDownloadComplete: end");
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "onItemClick: start for position " + position);
        RssEntry rssEntry = m_rssAdapter.getRssEntry(position);
        String url = rssEntry.getLink();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    private void restoreSavedMenuData() {
        m_sharedAppData.load(getApplicationContext());

        for(RssSource source : m_rssFeedManager.getRssSourceList()) {
            String sourceKey = source.getFriendlyKey();
            if(m_sharedAppData.hasKey(sourceKey)) {
                source.setEnabled(m_sharedAppData.getBoolean(sourceKey));
            }
        }
    }

    private void updateRssFeedManagerWithEnabledSources() {
        m_sharedAppData.load(getApplicationContext());
        for(RssSource source : m_rssFeedManager.getRssSourceList()) {
            String userFriendlyName = source.getFriendlyKey();

            boolean isChecked = true;
            if(m_sharedAppData.hasKey(userFriendlyName)) {
                isChecked = m_sharedAppData.getBoolean(userFriendlyName);
            }
            source.setEnabled(isChecked);
        }
    }
}

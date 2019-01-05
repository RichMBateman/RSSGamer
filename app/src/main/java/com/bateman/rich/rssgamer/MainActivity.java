package com.bateman.rich.rssgamer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AsyncTaskRssDownload.AsyncDownloadComplete,
    RssArticleRecyclerViewItemClickListener.OnItemClickListener {

    private static final String TAG = "MainActivity";

    private final RssFeedManager m_rssFeedManager = new RssFeedManager();
    private RssArticleRecyclerViewAdapter m_rssAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: start");
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                Log.e(TAG, "uncaughtException: " + ex.getMessage() );
            }
        });

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupRecyclerView();
        downloadRssData();
        Log.d(TAG, "onCreate: end");
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
        downloader.execute(m_rssFeedManager.getRssSourceList());
        Log.d(TAG, "downloadRssData: end");
    }

    @Override
    public void onRssDownloadComplete(ArrayList<RssSource> rssSources) {
        Log.d(TAG, "onRssDownloadComplete: start");

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
}

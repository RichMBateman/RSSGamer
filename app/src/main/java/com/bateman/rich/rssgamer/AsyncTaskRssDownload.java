package com.bateman.rich.rssgamer;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Generic Information:
 *  1) The information passed IN will be of type ArrayList<RssSource>
 *  2) Second type is related to publishing progress.  It is the type of parameter for progress published.
 *  3) The final type is the type of the result, our RSS data.
 */
public class AsyncTaskRssDownload extends AsyncTask<ArrayList<RssSource>, Integer, ArrayList<RssSource>> {

    interface AsyncDownloadComplete {
        void onRssDownloadProgress(int progress);
        void onRssDownloadComplete(ArrayList<RssSource> rssSources);
    }

    private static final String TAG = "AsyncTaskRssDownload";
    private AsyncDownloadComplete m_asyncDownloadCompleteCb;

    public AsyncTaskRssDownload(AsyncDownloadComplete asyncDownloadCompleteCb) {
        m_asyncDownloadCompleteCb = asyncDownloadCompleteCb;
    }

   /**
    * Downloads the rss feeds from a list of rssSources.  Only looks at the first supplied list.
    * Additional lists are ignored.
    * @return
    */
    @Override
    protected ArrayList<RssSource> doInBackground(ArrayList<RssSource>... rssSources) {
        Log.d(TAG, "doInBackground: start");
        Log.d(TAG, "doInBackground: there are " + rssSources[0].size() + " rss feed(s) to download asynchronously.");
        int progress = 0;
        for(RssSource rssSource : rssSources[0]) {

            String url = rssSource.getUrl();
            String rssFeed = downloadXML(url);
            if(rssFeed == null) {
                // Log.d (debug) are removed when we release our app; Log.e (error) is preserved.
                Log.e(TAG, "doInBackground: Error downloading for feed url: " + url);
            } else {
                rssSource.setRssFeedXml(rssFeed);
            }
            progress++;
            publishProgress(progress);
        }
        return rssSources[0];
    }

    /**
     * Runs on main UI thread once background task is finished.
     */
    @Override
    protected void onPostExecute(ArrayList<RssSource> rssSources) {
        super.onPostExecute(rssSources);
        m_asyncDownloadCompleteCb.onRssDownloadComplete(rssSources);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        m_asyncDownloadCompleteCb.onRssDownloadProgress(values[0]);
    }

    /**
     * Given a urlPath pointing to an RSS Feed, download it, and return a string representing the content.
     * @param urlPath
     * @return
     */
    private String downloadXML(String urlPath) {
        StringBuilder xmlResult = new StringBuilder();
        String returnval = null;
        try {
            URL url = new URL(urlPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int response = connection.getResponseCode();
            // Example response codes:
            // 404 for unable to find a page
            // 401 Unauthorized, 400 Bad request, etc
            Log.d(TAG, "downloadXML: The response code was " + response);
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            int charsRead;
            char[] inputBuffer = new char[500]; // read 500 characters at a time.
            while(true) {
                charsRead = reader.read(inputBuffer);
                if(charsRead < 0) {
                    break;
                }
                if(charsRead > 0) {
                    xmlResult.append(String.copyValueOf(inputBuffer, 0, charsRead));
                }
            }
            reader.close(); // also closes other two io objects

            returnval = xmlResult.toString();
        } catch (MalformedURLException e) {
            Log.e(TAG, "downloadXML: Invalid URL " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "downloadXML: IO Exception reading data " + e.getMessage());
        } catch (SecurityException e) {
            Log.e(TAG, "downloadXML: Security Exception.  Needs permission?" + e.getMessage() );
            e.printStackTrace();
        }

        return returnval;
    }
}

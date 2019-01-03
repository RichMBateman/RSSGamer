package com.bateman.rich.rssgamer;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A RecyclerView adapter for RSS Entries.
 */
public class RssArticleRecyclerViewAdapter extends RecyclerView.Adapter<RssArticleRecyclerViewAdapter.RssEntryViewHolder> {
    private static final String TAG = "RssArticleRecyclerViewA";
    private Context m_context;
    private final ArrayList<RssEntry> m_entryList = new ArrayList<>();

    public RssArticleRecyclerViewAdapter(Context context) {
        m_context=context;
        if(m_context == null) {
            throw new RuntimeException("You must supply the context to the adapter.");
        }
    }

    /**
     * returns the RssEntry at the specified position.
     */
    public RssEntry getRssEntry(int position) {
        return m_entryList.get(position);
    }

    /**
     * Updates the collection of RSS Entries.
     * @param newEntries
     */
    public void addRssEntries(ArrayList<RssEntry> newEntries) {
        m_entryList.addAll(newEntries);
        notifyDataSetChanged();
    }

    /**
     * Called by the layout manager when it needs a new view.
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RssEntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rss_article_entry, parent, false);
        return new RssEntryViewHolder(view);
    }

    /**
     * Called by the layout manager when it wants new data in an existing view (row)
     */
    @Override
    public void onBindViewHolder(RssEntryViewHolder viewHolder, int position) {
        RssEntry entry = m_entryList.get(position);
        viewHolder.m_labelTitle.setText(entry.getTitle());
        // It seems like the http links are getting redirected to https.
        // If you paste the original link into the browser, this is what happens.
        // So for now, just using https instead of http, if http is there.
        String imgSrc = entry.getImgSrc();
        if(imgSrc != null && imgSrc.length() > 0) {
            imgSrc = imgSrc.replace("http:", "https:");
            Log.d(TAG, "onBindViewHolder: imgSrc: " + imgSrc);

            // This is a way to do error handling.  If you just use "Picasso.with", you're missing out.
            Picasso.Builder builder = new Picasso.Builder(m_context);
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    Log.d(TAG, "onImageLoadFailed: An image failed to load: " + uri.getPath());
                    exception.printStackTrace();
                }
            });
            builder.build().setLoggingEnabled(true);
            builder.build()
                    .load(imgSrc)
                    .fit().centerCrop()
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(viewHolder.m_imageView);

            // An alternative way to use Picasso, although in this example you lose the advantage of printing a stack trace on failure.
//        Picasso.with(m_context).setLoggingEnabled(true);
//        Picasso.with(m_context)
//                .load(imgSrc)
//                .fit().centerCrop()
//                .error(R.drawable.placeholder)
//                .placeholder(R.drawable.placeholder)
//                .into(viewHolder.m_imageView);
        }
    }

    @Override
    public int getItemCount() {
        int itemCount = m_entryList.size();
        return itemCount;
    }

    /**
     * View Holder class to hold RSS Entries.
     */
    static class RssEntryViewHolder extends RecyclerView.ViewHolder {
        private TextView m_labelTitle;
        private ImageView m_imageView;

        public RssEntryViewHolder(View itemView) {
            super(itemView);
            m_labelTitle = itemView.findViewById(R.id.m_rssArticleTitle);
            m_imageView = itemView.findViewById(R.id.m_rssArticleImg);
        }
    }
}

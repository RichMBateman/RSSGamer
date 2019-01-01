package com.bateman.rich.rssgamer;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RssArticleRecyclerViewItemClickListener extends RecyclerView.SimpleOnItemTouchListener {
    private static final String TAG = "RssArticleRecyclerViewI";

    interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private final OnItemClickListener m_listener;
    private final GestureDetectorCompat m_gestureDetector;

    public RssArticleRecyclerViewItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
        Log.d(TAG, "RssArticleRecyclerViewItemClickListener: start");
        m_listener=listener;
        m_gestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d(TAG, "onSingleTapUp: start");
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if(childView != null && m_listener != null) {
                    Log.d(TAG, "onSingleTapUp: calling listener.onItemClick");
                    m_listener.onItemClick(childView, recyclerView.getChildAdapterPosition(childView));
                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.d(TAG, "onLongPress: start");
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if(childView != null && m_listener != null) {
                    Log.d(TAG, "onLongPress: calling listener.onItemLongClick");
                    m_listener.onItemClick(childView, recyclerView.getChildAdapterPosition(childView));
                }
            }
        });

        Log.d(TAG, "RssArticleRecyclerViewItemClickListener: end");
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        Log.d(TAG, "onInterceptTouchEvent: starts");
        if (m_gestureDetector != null) {
            boolean result = m_gestureDetector.onTouchEvent(e);
            Log.d(TAG, "onInterceptTouchEvent(): returned: " + result);
            return result;
        } else {
            Log.d(TAG, "onInterceptTouchEvent(): returned: false (should never see this)");
            return false;
        }
    }
}

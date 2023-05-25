package com.runin.runinapp.utils;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Documentation
 * Created by Usuario on 29/09/2016.
 */

public class RecyclerMusicClickListener implements RecyclerView.OnItemTouchListener{
    private final OnItemClickListener mListener;

    public interface OnItemClickListener {
        @SuppressWarnings("UnusedParameters")
        void onItemClick(View view, int position);
    }

    private final GestureDetector gestureDetector;

    public RecyclerMusicClickListener(Context context, OnItemClickListener listener){
        mListener = listener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent event){
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View childView = rv.findChildViewUnder(e.getX(),e.getY());
        if (childView != null && mListener != null && gestureDetector.onTouchEvent(e)){
            mListener.onItemClick(childView, rv.getChildAdapterPosition(childView));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }
}

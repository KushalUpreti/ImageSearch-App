package com.example.imagesearch;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

class RecyclerItemClickListner extends RecyclerView.SimpleOnItemTouchListener {
    private static final String TAG = "RecyclerItemClickListen";

    interface OnRecyclerClickListner{
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }
    private final OnRecyclerClickListner mListner;
    private final GestureDetectorCompat gestureDetector;

    public RecyclerItemClickListner(Context context, final RecyclerView recyclerView , final OnRecyclerClickListner mListner) {
        this.mListner = mListner;
        gestureDetector = new GestureDetectorCompat(context,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d(TAG, "onSingleTapUp: Starts");
                View clickedItem = recyclerView.findChildViewUnder(e.getX(),e.getY());
                if(clickedItem!= null && mListner != null){
                    mListner.onItemClick(clickedItem, recyclerView.getChildAdapterPosition(clickedItem));
                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.d(TAG, "onLongPress: Starts");
                View clickedItem = recyclerView.findChildViewUnder(e.getX(),e.getY());
                if(clickedItem!= null && mListner != null){
                    mListner.onItemLongClick(clickedItem, recyclerView.getChildAdapterPosition(clickedItem));
                }
                super.onLongPress(e);
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        Log.d(TAG, "onInterceptTouchEvent: Starts");
        if (gestureDetector != null) {
            boolean result = gestureDetector.onTouchEvent(e);
            Log.d(TAG, "onInterceptTouchEvent: returned " + result);
            return result;
        }else {
            return false;
        }

    }
}

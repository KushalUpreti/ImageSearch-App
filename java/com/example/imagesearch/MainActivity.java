package com.example.imagesearch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

enum DownloadStatus {IDLE, PROCESSING, NOT_INITIALIZED, FAILED_OR_EMPTY, OK}

public class MainActivity extends AppCompatActivity implements GetFlickrJsonData.OnDataAvailable,
                                RecyclerItemClickListner.OnRecyclerClickListner{

    private static final String TAG = "MainActivity";
    private FlickrRecyclerViewAdapter flickrRecyclerViewAdapter;
    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListner(this, recyclerView, this));

        flickrRecyclerViewAdapter = new FlickrRecyclerViewAdapter(new ArrayList<Photo>(), this);
        recyclerView.setAdapter(flickrRecyclerViewAdapter);

    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: starts");
        super.onResume();
        Log.d(TAG, "onResume: Ends");
    }

    @Override
    public void onDataAvailable(List<Photo> mPhotos, DownloadStatus downloadStatus) {
        if (downloadStatus == DownloadStatus.OK) {
            Log.d(TAG, "onDataAvailable: "+ mPhotos);
            flickrRecyclerViewAdapter.loadNewDataSet(mPhotos);
        } else {
            Log.e(TAG, "onDataAvailable failed with status " + downloadStatus);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "onItemClick: starts");
        Intent intent = new Intent(this, PhotoDetail.class);
        intent.putExtra("Photo",flickrRecyclerViewAdapter.getPhoto(position));
        startActivity(intent);
        Toast.makeText(MainActivity.this,"Normal tap at " + position, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Log.d(TAG, "onItemLongClick: Starts");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search,menu);
        MenuItem mSearchView = menu.findItem(R.id.searchWidget);
        this.searchView = (SearchView)mSearchView.getActionView();


        if(searchView != null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    GetFlickrJsonData getFlickrJsonData = new GetFlickrJsonData("flickr.com/services/feeds/photos_public.gne"
                            , "en-us", true, MainActivity.this);
                    getFlickrJsonData.execute(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.about){

            openDialog();
        }
        return true;
    }

    public void openDialog(){
        Dialog d = new Dialog();
        d.show(getSupportFragmentManager(),"ok");
    }

}
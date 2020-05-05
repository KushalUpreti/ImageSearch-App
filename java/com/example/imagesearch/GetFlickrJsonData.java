package com.example.imagesearch;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class GetFlickrJsonData extends AsyncTask<String, Void, List<Photo>> implements Download.OnDownloadComplete {
    private static final String TAG = "GetFlickrJsonData";
    private List<Photo> photos;
    private String mBaseUrl;
    private String mLanguage;
    private boolean mMatchAll;

    private final OnDataAvailable mCallBack;
    private boolean runningOnSameThread;

    interface OnDataAvailable {
        void onDataAvailable(List<Photo> mPhotos, DownloadStatus downloadStatus);
    }

    public GetFlickrJsonData(String mBaseUrl, String mLanguage, boolean mMatchAll, OnDataAvailable mCallBack) {
        Log.d(TAG, "GetFlickrJsonData: Constructor called");
        this.mBaseUrl = mBaseUrl;
        this.mLanguage = mLanguage;
        this.mMatchAll = mMatchAll;
        this.mCallBack = mCallBack;
        photos = new ArrayList<>();
    }

    void executeOnSameThread(String searchCriteria) {
        runningOnSameThread = true;
        Log.d(TAG, "executeOnSameThread: Starts");
        String destinationUri = createUri(searchCriteria, mLanguage, mMatchAll);
        destinationUri = "https://www." + destinationUri;
        Download download = new Download(this);
        download.execute(destinationUri);
        Log.d(TAG, "executeOnSameThread: Ends");
    }

    @Override
    protected List<Photo> doInBackground(String... params) {
        Log.d(TAG, "doInBackground: Starts");
        String destinationUri = createUri(params[0], mLanguage, mMatchAll);
        destinationUri = "https://www." + destinationUri;
        Download download = new Download(this);
        download.runInsameThread(destinationUri);
        return photos;
    }

    @Override
    protected void onPostExecute(List<Photo> photos) {
        if (mCallBack != null) {
            mCallBack.onDataAvailable(photos, DownloadStatus.OK);
        }
        Log.d(TAG, "onPostExecute: ENDS");
    }

    @Override
    public void onDownloadComplete(String s, DownloadStatus status) {
        if (status == DownloadStatus.OK) {
            try {
                JSONObject jsonData = new JSONObject(s);
                Log.d(TAG, "onDownloadComplete: Parsing");
                JSONArray itemArray = jsonData.getJSONArray("items");
                for (int i = 0; i < itemArray.length(); i++) {
                    JSONObject jsonPhoto = itemArray.getJSONObject(i);
                    String title = jsonPhoto.getString("title");
                    String author = jsonPhoto.getString("author");
                    String authorId = jsonPhoto.getString("author_id");
                    String tags = jsonPhoto.getString("tags");

                    JSONObject jsonMedia = jsonPhoto.getJSONObject("media");
                    String photoUrl = jsonMedia.getString("m");
                    String link = photoUrl.replaceFirst("_m.", "_b.");
                    Photo photoObject = new Photo(title, author, authorId, link, tags, photoUrl);
                    photos.add(photoObject);

                    Log.d(TAG, "onDownloadComplete: " + photoObject.toString());
                    Log.d(TAG, "onDownloadComplete: Parsing DONE");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "onDownloadComplete: Failed" + e.getMessage());
                status = DownloadStatus.FAILED_OR_EMPTY;
            }
            if (runningOnSameThread && mCallBack != null) {
                mCallBack.onDataAvailable(photos, status);
            }
            Log.d(TAG, "onDownloadComplete: Ends");
        }
    }

    private String createUri(String searchCriteria, String lang, boolean matchall) {
        Log.d(TAG, "createUri: starts");
        return Uri.parse(mBaseUrl).buildUpon()
                .appendQueryParameter("tags", searchCriteria)
                .appendQueryParameter("tagmode", matchall ? "ALL" : "ANY")
                .appendQueryParameter("lang", lang)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1").build().toString();
    }
}

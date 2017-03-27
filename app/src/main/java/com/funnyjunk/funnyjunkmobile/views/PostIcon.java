package com.funnyjunk.funnyjunkmobile.views;

import android.os.AsyncTask;

import com.funnyjunk.funnyjunkmobile.Utilities;

import java.io.IOException;

/**
 * Created by kyle on 3/29/16.
 */
public class PostIcon {
    //Needs: Title, Thumbs, and Picture
    public String postURL;
    public String title;
    public String pictureURL;
    public String html;

    public PostIcon(String postURL, String title, String pictureURL) {
        this.title = title;
        this.postURL = postURL;
        this.pictureURL = pictureURL;


    }

    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return Utilities.getHtml(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //decompilePosts(result, posts, "<div id=\"hometop24h_content\">", "class=\"thumbnail_link\"");
            //Log.d("EH", "" + posts.size());
        }
    }
}

package com.funnyjunk.funnyjunkmobile;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.funnyjunk.funnyjunkmobile.views.PostIcon;

import java.io.IOException;
import java.util.ArrayList;

public class Home extends AppCompatActivity {
    private final String funnyjunkURL = "http://www.funnyjunk.com";

    ArrayList<PostIcon> posts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        this.posts = new ArrayList<PostIcon>();

        new DownloadWebpageTask().execute(Utilities.highestRatedURL + "1");
    }

    //start: <div id="hometop24h_content">
    //check: class="thumbnail_link"
    private void decompilePosts(String html, ArrayList<PostIcon> posts, String start, String check) {
        html = html.substring(html.indexOf(start));

        while(html.indexOf(check) > -1) {
            html = html.substring(html.indexOf(check));

            String postHrefCheck = "href=\"";
            String titleCheck = "title=\"";
            String picHrefCheck = "src=\"";

            try {
                html = html.substring(html.indexOf(postHrefCheck) + postHrefCheck.length());
                String postHref = html.substring(0, html.indexOf('\"'));

                html = html.substring(html.indexOf(titleCheck) + titleCheck.length());
                String title = html.substring(0, html.indexOf("\""));

                html = html.substring(html.indexOf(picHrefCheck) + picHrefCheck.length());
                String picHref = html.substring(0, html.indexOf('\"'));

                posts.add(new PostIcon(postHref, title, picHref));
            }catch(Exception ex) {
                Log.d("WHAT", "" + html.length());
            }
        }
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
            decompilePosts(result, posts, "<div id=\"hometop24h_content\">", "class=\"thumbnail_link\"");
            Log.d("EH", "" + posts.size());
        }
    }
}

package com.funnyjunk.funnyjunkmobile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by kyle on 3/29/16.
 */
public class Utilities {
    public static final String highestRatedURL = "http://www.funnyjunk.com/funny_content/pages/hometop24h/desc/80/thumbs/";
    public static final String newestURL = "http://www.funnyjunk.com/funny_content/pages/homenewest24h/desc/40/date/";

    public static String getHtml(String url) throws IOException {
        // Build and set timeout values for the request.
        URLConnection connection = (new URL(url)).openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.connect();

        // Read and store the result line by line then return the entire string.
        InputStream in = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder html = new StringBuilder();
        for (String line; (line = reader.readLine()) != null; ) {
            html.append(line);
        }
        in.close();

        return html.toString();
    }
}

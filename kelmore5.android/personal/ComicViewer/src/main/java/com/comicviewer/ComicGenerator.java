package com.comicviewer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class ComicGenerator 
{
	static String html;
	static
	{
		html = "";
	}

	public abstract String getImageURL();
	public abstract String getHTML();
	public abstract String getTitle();
	public abstract String getComicSite();
	public abstract String nextComic();
	public abstract String previousComic();
	public abstract String firstComic();
	public abstract String randomComic();
	
	public static String downloadHTML(String siteURL)
	{
		try 
        {
            URL url = new URL(siteURL);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("User-Agent", "");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.connect();
             
             InputStream in = connection.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(in));
             StringBuilder str = new StringBuilder();
             String line = null;
             while((line = reader.readLine()) != null)
             {
          	   str.append(line);
             }
             in.close();
             html = str.toString();
        } 
        catch (IOException e) 
        {
             // TODO Auto-generated catch block
             e.printStackTrace();
        }
		return html;
	}
	
	public static String downloadString(String siteURL, String string)
	{
		try 
        {
            URL url = new URL(siteURL);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("User-Agent", "");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.connect();
             
             InputStream in = connection.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(in));
             String line = null;
             while((line = reader.readLine()) != null)
             {
            	 if(line.contains(string))
            	 {
            		 string = line;
            		 break;
            	 }
             }
             in.close();
        } 
        catch (IOException e) 
        {
             // TODO Auto-generated catch block
             e.printStackTrace();
        }
		return string;
	}

}

package com.comicviewer;

import java.util.Random;

public class XKCDGenerator extends ComicGenerator
{
    String html;
    int currentComicNumber;
    int newestComicNumber;
    
    XKCDGenerator()
    {
    	html = ComicGenerator.downloadHTML("http://xkcd.com/1048/");
    	currentComicNumber = currentComicNumber();
    	newestComicNumber = currentComicNumber;
    }
	
	XKCDGenerator(String siteURL)
	{
		html = ComicGenerator.downloadHTML("http://xkcd.com/1048/");
		currentComicNumber = currentComicNumber();
		XKCDGenerator generator = new XKCDGenerator();
		newestComicNumber = generator.currentComicNumber();
	}
	
	public int currentComicNumber()
	{
		String numberString = html.substring(html.indexOf("Permanent link"));
        numberString = numberString.substring((numberString.indexOf("Permanent link")+30), numberString.indexOf("</h3>"));
		numberString = numberString.substring(16);
		numberString = numberString.substring(0, numberString.indexOf("/"));
		return Integer.parseInt(numberString);
	}
	
	public String getImageURL()
	{
		String newImageURLString = html.substring(html.indexOf("Image URL"));
        newImageURLString = newImageURLString.substring((newImageURLString.indexOf("Image URL")+38), newImageURLString.indexOf("</h3>"));
        return newImageURLString;
	}
	public String getHTML()
	{
		return html;
	}
	public String getTitle()
	{
		String title = html.substring(html.indexOf("<title>")+7, html.indexOf("</title>"));
		title = title.substring(6);
		title = currentComicNumber() + " - " + title;
		return title;
	}
	
	public boolean isNumbered()
	{
		return true;
	}
	
	public String getComicSite()
	{
		return "http://xkcd.com/";
	}
	
	public String nextComic()
	{
		currentComicNumber += 1;
		String nextComic = "http://xkcd.com/" + currentComicNumber + "/";
		return nextComic;
	}
	
	public String previousComic()
	{
		currentComicNumber -= 1;
		String previousComic = "http://xkcd.com/" + currentComicNumber + "/";
		return previousComic;
	}
	
	public String firstComic()
	{
		return "http://xkcd.com/1/";
	}
	
	public String randomComic()
	{
		Random r = new Random();
		currentComicNumber = (r.nextInt(newestComicNumber + 1) - 1);
		String randomComic = "http://xkcd.com/" + currentComicNumber + "/";
		return randomComic;
	}
	
	public String getNumberedComic(int number)
	{
		if((number < 1) || (number > newestComicNumber) || (number == 404))
		{
			return "Comic does not exist";
		}
		else
		{
			return "http://xkcd.com/" + number + "/";
		}
	}

}

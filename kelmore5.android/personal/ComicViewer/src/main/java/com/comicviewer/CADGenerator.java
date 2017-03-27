package com.comicviewer;

import java.util.ArrayList;

public class CADGenerator
{
	public ArrayList<String> getImageArchive(ArrayList<String> archives)
	{
		ArrayList<String> imageArchive = new ArrayList<String>();
		for(String c: archives)
		{
			String imageHTML = ComicGenerator.downloadString(c, "http://v.cdn.cad-comic.com/comics/");
			String imageURL = imageHTML.substring(imageHTML.indexOf("http://v.cdn.cad-comic.com/comics/"));
			imageURL = imageURL.substring(0, imageURL.indexOf("\""));
			imageArchive.add(imageURL);
		}
		return imageArchive;
	}

	public ArrayList<String> getTitleArchive(ArrayList<String> archives)
	{
		ArrayList<String> titleArchive = new ArrayList<String>();
		for(String c: archives)
		{
			String title = ComicGenerator.downloadString(c, "<title>");
			title = title.substring(title.indexOf("<title>")+7, title.indexOf("</title>"));
			title = title.substring(15);
			titleArchive.add(title);
		}
		return titleArchive;
	}

	public void getNewestArchives(ArrayList<String> archives, ArrayList<String> titles)
	{
		ArrayList<String> archiveList = new ArrayList<String>();
		String archive = ComicGenerator.downloadHTML("http://www.cad-comic.com/cad/archive");
		archive = archive.substring(archive.indexOf("class=\"post\""), (archive.indexOf(archives.get(0))-6));
		archive = archive.replace(" ", "");
		archive = archive.replace("\t", "");
		while (archive.contains("ahref"))
		{
			archive = archive.substring(archive.indexOf("ahref") + 7);
			String cad = "http://www.cad-comic.com" + archive.substring(0,13);
			if(!(cad.contains("archive")))
			{
				archiveList.add(cad);
			}
		}
		if(!archiveList.isEmpty())
		{
			String newestArchive = archiveList.get(0).substring(25);
			ArrayList<String> newArchiveList = getImageArchive(archiveList);
			ArrayList<String> newestTitles = getTitleArchive(archiveList);
			archives.addAll(1, newArchiveList);
			archives.set(0, newestArchive);
			titles.addAll(0, newestTitles);
		}
		else
		{
			return;
		}
	}
}

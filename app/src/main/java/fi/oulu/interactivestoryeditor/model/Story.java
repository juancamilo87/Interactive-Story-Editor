package fi.oulu.interactivestoryeditor.model;

import java.util.ArrayList;

/**
 * Created by JuanCamilo on 5/7/2015.
 */
public class Story {

    private Author author;
    private String title;
    private String summary;
    private ArrayList<Chapter> chapters;

    public Story() {
        chapters = new ArrayList<>();
    }

    public Story(Author author, String title, String summary) {
        this.author = author;
        this.title = title;
        this.summary = summary;
        chapters = new ArrayList<>();
    }

    public Author getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public ArrayList<Chapter> getChapters() {
        return chapters;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void addChapter(Chapter chapter)
    {
        chapters.add(chapter);
    }

    public void moveChapter(Chapter chapter, int position)
    {
        int index = chapters.indexOf(chapter);
        if(index != -1)
        {
            chapters.remove(index);
            chapters.add(position,chapter);
        }
    }
}

package fi.oulu.interactivestoryeditor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JuanCamilo on 5/7/2015.
 */
public class Story {

    private Author author;
    private String title;
    private String summary;
    private List<Chapter> chapters;
    private long story_id;

    public Story() {
        chapters = new ArrayList<>();
        this.story_id = -1;
    }

    public Story(Author author, String title, String summary) {
        this.author = author;
        this.title = title;
        this.summary = summary;
        chapters = new ArrayList<>();
        this.story_id = -1;
    }

    public Story(Author author, String title, String summary, long story_id) {
        this.author = author;
        this.title = title;
        this.summary = summary;
        this.story_id = story_id;
        chapters = new ArrayList<>();
    }

    public long getStory_id(){
        return  story_id;
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

    public List<Chapter> getChapters() {
        return chapters;
    }

    public void setStory_id(int story_id){
        this.story_id = story_id;
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

    public void setChapters(List<Chapter> chapters)
    {
        this.chapters = chapters;
    }

    public void moveChapter(Chapter chapter, int position) {
        int index = chapters.indexOf(chapter);
        if(index != -1)
        {
            chapters.remove(index);
            chapters.add(position,chapter);
        }
    }

    @Override
    public String toString() { return title; }

}

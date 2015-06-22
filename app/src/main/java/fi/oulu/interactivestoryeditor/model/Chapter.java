package fi.oulu.interactivestoryeditor.model;

/**
 * Created by JuanCamilo on 5/7/2015.
 */
public class Chapter {

    private String title;
    private String text;
    private String imageUrl;
    private String videoUrl;
    private String audioUrl;
    private Interaction interaction;
    private long chapter_id;

    public Chapter() {
    }

    public Chapter(String title, String text, String imageUrl, String videoUrl, String audioUrl) {
        this.title = title;
        this.text = text;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.audioUrl = audioUrl;
        chapter_id = -1;
    }

    public Chapter(String title, String text, String imageUrl, String videoUrl, String audioUrl, long chapter_id) {
        this.title = title;
        this.text = text;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.audioUrl = audioUrl;
        this.chapter_id = chapter_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public Interaction getInteraction() {
        return interaction;
    }

    public void setInteraction(Interaction interaction) {
        this.interaction = interaction;
    }

    public long getChapter_id(){
        return chapter_id;
    }

    @Override
    public String toString() { return title; }
}

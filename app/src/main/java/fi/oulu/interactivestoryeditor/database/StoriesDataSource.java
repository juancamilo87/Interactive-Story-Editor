package fi.oulu.interactivestoryeditor.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import fi.oulu.interactivestoryeditor.model.Author;
import fi.oulu.interactivestoryeditor.model.Chapter;
import fi.oulu.interactivestoryeditor.model.Story;

/**
 * Created by JuanCamilo on 5/7/2015.
 */
public class StoriesDataSource {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private Context context;


    private String[] allColumns = {
            MySQLiteHelper.COLUMN_STORIES_ID,
            MySQLiteHelper.COLUMN_STORIES_NAME,
            MySQLiteHelper.COLUMN_STORIES_LAST_NAME,
            MySQLiteHelper.COLUMN_STORIES_WEBSITE,
            MySQLiteHelper.COLUMN_STORIES_EMAIL,
            MySQLiteHelper.COLUMN_STORIES_TITLE,
            MySQLiteHelper.COLUMN_STORIES_SUMMARY
            };

    public StoriesDataSource(Context context) {
        this.context = context;
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Story createStory(Story story) {
        if(story.getStory_id() != -1) {
            return updateStory(story);
        }
        else {
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_STORIES_NAME, story.getAuthor().getName());
            values.put(MySQLiteHelper.COLUMN_STORIES_NAME, story.getAuthor().getLast_name());
            values.put(MySQLiteHelper.COLUMN_STORIES_NAME, story.getAuthor().getWebsite());
            values.put(MySQLiteHelper.COLUMN_STORIES_NAME, story.getAuthor().getEmail());
            values.put(MySQLiteHelper.COLUMN_STORIES_NAME, story.getTitle());
            values.put(MySQLiteHelper.COLUMN_STORIES_NAME, story.getSummary());
            long insertId = database.insert(MySQLiteHelper.TABLE_STORIES, null,
                    values);
            Cursor cursor = database.query(MySQLiteHelper.TABLE_STORIES,
                    allColumns, MySQLiteHelper.COLUMN_STORIES_ID + " = " + insertId, null,
                    null, null, null);
            cursor.moveToFirst();
            Story newStory = cursorToStory(cursor);

            ChaptersDataSource chaptersDataSource = new ChaptersDataSource(context);
            chaptersDataSource.open();
            for(int i = 0; i<story.getChapters().size(); i++)
            {
                newStory.addChapter(chaptersDataSource.createChapter(story.getChapters().get(i),insertId,i+1));
            }
            chaptersDataSource.close();
            cursor.close();
            return newStory;
        }
    }

    public void deleteStory(Story story) {
        long id = story.getStory_id();
        database.delete(MySQLiteHelper.TABLE_STORIES, MySQLiteHelper.COLUMN_STORIES_ID
                + " = " + id, null);
        ChaptersDataSource chaptersDataSource = new ChaptersDataSource(context);
        chaptersDataSource.open();
        for(int i = 0; i<story.getChapters().size(); i++)
        {
            chaptersDataSource.deleteChapter(story.getChapters().get(i));
        }
        chaptersDataSource.close();
    }

    public List<Story> getAllStories() {
        List<Story> stories = new ArrayList<Story>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_STORIES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Story story = cursorToStory(cursor);
            ChaptersDataSource chaptersDataSource = new ChaptersDataSource(context);
            chaptersDataSource.open();
            story.setChapters(chaptersDataSource.getAllChapters(story));
            chaptersDataSource.close();
            stories.add(story);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return stories;
    }

    public List<Story> getAllSimpleStories() {
        List<Story> stories = new ArrayList<Story>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_STORIES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Story story = cursorToStory(cursor);
            stories.add(story);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return stories;
    }

    public Story updateStory(Story story){
        if(story.getStory_id() != -1) {
            return null;
        }
        else {
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_STORIES_NAME, story.getAuthor().getName());
            values.put(MySQLiteHelper.COLUMN_STORIES_NAME, story.getAuthor().getLast_name());
            values.put(MySQLiteHelper.COLUMN_STORIES_NAME, story.getAuthor().getWebsite());
            values.put(MySQLiteHelper.COLUMN_STORIES_NAME, story.getAuthor().getEmail());
            values.put(MySQLiteHelper.COLUMN_STORIES_NAME, story.getTitle());
            values.put(MySQLiteHelper.COLUMN_STORIES_NAME, story.getSummary());
            String strFilter = MySQLiteHelper.COLUMN_STORIES_ID + "=" + story.getStory_id();
            long insertId = database.update(MySQLiteHelper.TABLE_STORIES,values, strFilter,null);
            Cursor cursor = database.query(MySQLiteHelper.TABLE_STORIES,
                    allColumns, MySQLiteHelper.COLUMN_STORIES_ID + " = " + insertId, null,
                    null, null, null);
            cursor.moveToFirst();
            Story newStory = cursorToStory(cursor);

            ChaptersDataSource chaptersDataSource = new ChaptersDataSource(context);
            chaptersDataSource.open();
            for(int i = 0; i<story.getChapters().size(); i++)
            {
                newStory.addChapter(chaptersDataSource.createChapter(story.getChapters().get(i),insertId,i+1));
            }
            chaptersDataSource.close();
            cursor.close();
            return newStory;
        }
    }

    public Story getStory(long story_id)
    {
        Cursor cursor = database.query(MySQLiteHelper.TABLE_STORIES,
                allColumns, MySQLiteHelper.COLUMN_STORIES_ID + " = " + story_id, null,
                null, null, null);

        if(cursor.moveToFirst())
        {
            Story story = cursorToStory(cursor);
            ChaptersDataSource chaptersDataSource = new ChaptersDataSource(context);
            chaptersDataSource.open();
            story.setChapters(chaptersDataSource.getAllChapters(story));
            chaptersDataSource.close();
            cursor.close();
            return story;
        }
        return null;
    }

    public Story getSimpleStory(long story_id)
    {
        Cursor cursor = database.query(MySQLiteHelper.TABLE_STORIES,
                allColumns, MySQLiteHelper.COLUMN_STORIES_ID + " = " + story_id, null,
                null, null, null);

        if(cursor.moveToFirst())
        {
            Story story = cursorToStory(cursor);
            cursor.close();
            return story;
        }
        return null;
    }

    private Story cursorToStory(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_STORIES_ID));
        String name = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_STORIES_NAME));
        String last_name = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_STORIES_LAST_NAME));
        String website = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_STORIES_WEBSITE));
        String email = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_STORIES_EMAIL));
        String title = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_STORIES_TITLE));
        String summary = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_STORIES_SUMMARY));

        Author author = new Author(name, last_name, website, email);
        Story story = new Story(author,title,summary,id);
        return story;
    }


}

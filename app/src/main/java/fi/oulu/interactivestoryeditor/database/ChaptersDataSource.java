package fi.oulu.interactivestoryeditor.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import fi.oulu.interactivestoryeditor.model.Chapter;
import fi.oulu.interactivestoryeditor.model.Interaction;
import fi.oulu.interactivestoryeditor.model.Story;

/**
 * Created by JuanCamilo on 5/7/2015.
 */
public class ChaptersDataSource {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private Context context;

    private String[] allColumns = {
            MySQLiteHelper.COLUMN_CHAPTERS_ID,
            MySQLiteHelper.COLUMN_CHAPTERS_STORY_ID,
            MySQLiteHelper.COLUMN_CHAPTERS_NUMBER,
            MySQLiteHelper.COLUMN_CHAPTERS_TITLE,
            MySQLiteHelper.COLUMN_CHAPTERS_TEXT,
            MySQLiteHelper.COLUMN_CHAPTERS_IMAGE_URL,
            MySQLiteHelper.COLUMN_CHAPTERS_VIDEO_URL,
            MySQLiteHelper.COLUMN_CHAPTERS_AUDIO_URL
    };

    public ChaptersDataSource(Context context) {
        this.context = context;
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Chapter createChapter(Chapter chapter, long story_id, int number){
        if(chapter.getChapter_id()!=-1)
        {
            return updateChapter(chapter, story_id, number);
        }
        else
        {
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_CHAPTERS_STORY_ID, story_id);
            values.put(MySQLiteHelper.COLUMN_CHAPTERS_NUMBER, number);
            values.put(MySQLiteHelper.COLUMN_CHAPTERS_TITLE, chapter.getTitle());
            values.put(MySQLiteHelper.COLUMN_CHAPTERS_TEXT, chapter.getText());
            values.put(MySQLiteHelper.COLUMN_CHAPTERS_IMAGE_URL, chapter.getImageUrl());
            values.put(MySQLiteHelper.COLUMN_CHAPTERS_VIDEO_URL, chapter.getVideoUrl());
            values.put(MySQLiteHelper.COLUMN_CHAPTERS_AUDIO_URL, chapter.getAudioUrl());

            Cursor cursor = database.query(MySQLiteHelper.TABLE_CHAPTERS,
                    allColumns, MySQLiteHelper.COLUMN_CHAPTERS_STORY_ID + " = " + story_id + " AND " +
                            MySQLiteHelper.COLUMN_CHAPTERS_NUMBER + " = " + number, null,
                    null, null, null);

            if(cursor.moveToFirst())
            {
                long deleteId = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CHAPTERS_ID));
                cursor.close();
                deleteChapter(deleteId);
            }
            long insertId = database.insert(MySQLiteHelper.TABLE_CHAPTERS, null, values);
            Interaction interaction = null;
            if(chapter.getInteraction()!=null)
            {
                InteractionsDataSource interactionsDataSource = new InteractionsDataSource(context);
                interactionsDataSource.open();
                interaction = interactionsDataSource.createInteraction(chapter.getInteraction(),insertId);
                interactionsDataSource.close();
            }
            Cursor otherCursor = database.query(MySQLiteHelper.TABLE_CHAPTERS,
                    allColumns, MySQLiteHelper.COLUMN_CHAPTERS_ID + " = " + insertId, null, null, null, null);
            otherCursor.moveToFirst();
            Chapter newChapter = cursorToChapter(otherCursor);
            otherCursor.close();
            newChapter.setInteraction(interaction);
            return newChapter;
        }
    }

    public Chapter updateChapter(Chapter chapter, long story_id, int number) {
        if(chapter.getChapter_id() == -1)
        {
            return null;
        }
        else
        {
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_CHAPTERS_STORY_ID, story_id);
            values.put(MySQLiteHelper.COLUMN_CHAPTERS_NUMBER, number);
            values.put(MySQLiteHelper.COLUMN_CHAPTERS_TITLE, chapter.getTitle());
            values.put(MySQLiteHelper.COLUMN_CHAPTERS_TEXT, chapter.getText());
            values.put(MySQLiteHelper.COLUMN_CHAPTERS_IMAGE_URL, chapter.getImageUrl());
            values.put(MySQLiteHelper.COLUMN_CHAPTERS_VIDEO_URL, chapter.getVideoUrl());
            values.put(MySQLiteHelper.COLUMN_CHAPTERS_AUDIO_URL, chapter.getAudioUrl());
            Cursor cursor = database.query(MySQLiteHelper.TABLE_CHAPTERS,
                    allColumns, MySQLiteHelper.COLUMN_CHAPTERS_STORY_ID + " = " + story_id + " AND " +
                            MySQLiteHelper.COLUMN_CHAPTERS_NUMBER + " = " + number + " AND " +
                    MySQLiteHelper.COLUMN_CHAPTERS_ID + " != " + chapter.getChapter_id(), null,
                    null, null, null);

            if(cursor.moveToFirst())
            {

                long deleteId = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CHAPTERS_ID));
                cursor.close();
                if(deleteId != chapter.getChapter_id())
                {
                    deleteChapter(deleteId);
                }
            }
            Cursor check_cursor = database.query(MySQLiteHelper.TABLE_CHAPTERS,
                    allColumns, MySQLiteHelper.COLUMN_CHAPTERS_ID + " = " + chapter.getChapter_id(), null,
                    null, null, null);
            if(check_cursor.moveToFirst())
            {
                String strFilter = MySQLiteHelper.COLUMN_CHAPTERS_ID + "=" + chapter.getChapter_id();
                database.update(MySQLiteHelper.TABLE_CHAPTERS, values, strFilter, null);
            }
            else
            {
                long insertId = database.insert(MySQLiteHelper.TABLE_CHAPTERS, null, values);
            }


            Interaction interaction = null;

            if(chapter.getInteraction()!=null)
            {
                InteractionsDataSource interactionsDataSource = new InteractionsDataSource(context);
                interactionsDataSource.open();
                interaction = interactionsDataSource.createInteraction(chapter.getInteraction(),chapter.getChapter_id());
                interactionsDataSource.close();
            }


            Cursor otherCursor = database.query(MySQLiteHelper.TABLE_CHAPTERS,
                    allColumns, MySQLiteHelper.COLUMN_CHAPTERS_ID + " = " + chapter.getChapter_id(), null, null, null, null);
            otherCursor.moveToFirst();
            Chapter newChapter = cursorToChapter(otherCursor);
            otherCursor.close();
            newChapter.setInteraction(interaction);
            return newChapter;
        }
    }

    public List<Chapter> getAllChapters(Story story)
    {
        List<Chapter> chapters = new ArrayList<Chapter>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_CHAPTERS,
                allColumns, MySQLiteHelper.COLUMN_CHAPTERS_STORY_ID + " = " + story.getStory_id(), null,
                null, null, null);

        cursor.moveToFirst();
        InteractionsDataSource interactionsDataSource = new InteractionsDataSource(context);
        interactionsDataSource.open();
        while (!cursor.isAfterLast()) {
            Chapter chapter = cursorToChapter(cursor);
            chapter.setInteraction(interactionsDataSource.getInteraction(chapter));
            chapters.add(chapter);
            cursor.moveToNext();
        }
        interactionsDataSource.close();
        // make sure to close the cursor
        cursor.close();
        return chapters;
    }

    public void deleteChapter(Chapter chapter) {
        long id = chapter.getChapter_id();
        database.delete(MySQLiteHelper.TABLE_CHAPTERS, MySQLiteHelper.COLUMN_CHAPTERS_ID
                + " = " + id, null);
        InteractionsDataSource interactionsDataSource = new InteractionsDataSource(context);
        interactionsDataSource.open();
        if(chapter.getInteraction()!=null)
        {
            interactionsDataSource.deleteInteraction(chapter.getInteraction());
        }
        interactionsDataSource.close();
    }

    public void deleteChapter(long id) {
        Chapter chapter = getChapter(id);
        InteractionsDataSource interactionsDataSource = new InteractionsDataSource(context);
        interactionsDataSource.open();
        if(chapter.getInteraction()!=null)
        {
            interactionsDataSource.deleteInteraction(chapter.getInteraction());
        }
        interactionsDataSource.close();
        database.delete(MySQLiteHelper.TABLE_CHAPTERS, MySQLiteHelper.COLUMN_CHAPTERS_ID
                + " = " + id, null);
    }

    public Chapter getChapter(long chapter_id)
    {
        Cursor cursor = database.query(MySQLiteHelper.TABLE_CHAPTERS,
                allColumns, MySQLiteHelper.COLUMN_CHAPTERS_ID + " = " + chapter_id, null,
                null, null, null);

        if(cursor.moveToFirst())
        {
            InteractionsDataSource interactionsDataSource = new InteractionsDataSource(context);
            interactionsDataSource.open();
            Chapter chapter = cursorToChapter(cursor);
            chapter.setInteraction(interactionsDataSource.getInteraction(chapter));
            cursor.close();
            interactionsDataSource.close();
            return chapter;
        }
        return null;
    }

    public Chapter getChapter(long story_id, int number)
    {
        Cursor cursor = database.query(MySQLiteHelper.TABLE_CHAPTERS,
                allColumns, MySQLiteHelper.COLUMN_CHAPTERS_STORY_ID + " = " + story_id + " AND " +
                        MySQLiteHelper.COLUMN_CHAPTERS_NUMBER + " = " + number, null,
                null, null, null);

        if(cursor.moveToFirst())
        {
            InteractionsDataSource interactionsDataSource = new InteractionsDataSource(context);
            interactionsDataSource.open();
            Chapter chapter = cursorToChapter(cursor);
            chapter.setInteraction(interactionsDataSource.getInteraction(chapter));
            cursor.close();
            interactionsDataSource.close();
            return chapter;
        }
        return null;
    }

    private Chapter cursorToChapter(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CHAPTERS_ID));
        String title = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CHAPTERS_TITLE));
        String text = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CHAPTERS_TEXT));
        String image_url = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CHAPTERS_IMAGE_URL));
        String video_url = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CHAPTERS_VIDEO_URL));
        String audio_url = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CHAPTERS_AUDIO_URL));

        Chapter chapter = new Chapter(title, text, image_url, video_url, audio_url, id);
        return chapter;
    }

}

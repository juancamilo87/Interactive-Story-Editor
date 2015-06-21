package fi.oulu.interactivestoryeditor.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by JuanCamilo on 5/7/2015.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "stories.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_STORIES = "stories";
    public static final String COLUMN_STORIES_ID = "_id";
    public static final String COLUMN_STORIES_NAME = "name";
    public static final String COLUMN_STORIES_LAST_NAME = "last_name";
    public static final String COLUMN_STORIES_WEBSITE = "website";
    public static final String COLUMN_STORIES_EMAIL = "email";
    public static final String COLUMN_STORIES_TITLE = "title";
    public static final String COLUMN_STORIES_SUMMARY = "summary";

    private static final String CREATE_TABLE_STORIES = "create table "
            + TABLE_STORIES + "(" +
            COLUMN_STORIES_ID + " integer primary key autoincrement, " +
            COLUMN_STORIES_NAME + " text not null, " +
            COLUMN_STORIES_LAST_NAME + " text, " +
            COLUMN_STORIES_WEBSITE + " text, " +
            COLUMN_STORIES_EMAIL + " text, " +
            COLUMN_STORIES_TITLE + " text not null, " +
            COLUMN_STORIES_SUMMARY + " text);";

    public static final String TABLE_CHAPTERS = "chapters";
    public static final String COLUMN_CHAPTERS_ID = "_id";
    public static final String COLUMN_CHAPTERS_STORY_ID = "story_id";
    public static final String COLUMN_CHAPTERS_NUMBER = "number";
    public static final String COLUMN_CHAPTERS_TITLE = "title";
    public static final String COLUMN_CHAPTERS_TEXT = "text";
    public static final String COLUMN_CHAPTERS_IMAGE_URL = "image_url";
    public static final String COLUMN_CHAPTERS_VIDEO_URL = "video_url";
    public static final String COLUMN_CHAPTERS_AUDIO_URL = "audio_url";

    private static final String CREATE_TABLE_CHAPTERS = "create table "
            + TABLE_CHAPTERS + "(" +
            COLUMN_CHAPTERS_ID + " integer primary key autoincrement, " +
            COLUMN_CHAPTERS_STORY_ID + " integer not null, " +
            COLUMN_CHAPTERS_NUMBER + " integer not null, " +
            COLUMN_CHAPTERS_TITLE + " text not null, " +
            COLUMN_CHAPTERS_TEXT + " text, " +
            COLUMN_CHAPTERS_IMAGE_URL + " text, " +
            COLUMN_CHAPTERS_VIDEO_URL + " text, " +
            COLUMN_CHAPTERS_AUDIO_URL + " text);";

    public static final String TABLE_INTERACTIONS = "interactions";
    public static final String COLUMN_INTERACTIONS_ID = "_id";
    public static final String COLUMN_INTERACTIONS_CHAPTER_ID = "chapter_id";
    public static final String COLUMN_INTERACTIONS_TYPE = "type";
    public static final String COLUMN_INTERACTIONS_INSTRUCTIONS = "instructions";
    public static final String COLUMN_INTERACTIONS_POSITIVE_FEEDBACK = "positive_feedback";
    public static final String COLUMN_INTERACTIONS_NEGATIVE_FEEDBACK = "negative_feedback";
    public static final String COLUMN_INTERACTIONS_POSITIVE_AUDIO_URL = "positive_audio_url";
    public static final String COLUMN_INTERACTIONS_NEGATIVE_AUDIO_URL = "negative_audio_url";
    public static final String COLUMN_INTERACTIONS_LATITUDE = "latitude";
    public static final String COLUMN_INTERACTIONS_LONGITUDE = "longitude";
    public static final String COLUMN_INTERACTIONS_SECRET_CODE = "secret_code";
    public static final String COLUMN_INTERACTIONS_QUESTION = "question";
    public static final String COLUMN_INTERACTIONS_CORRECT_ANSWER = "correct_answet";
    public static final String COLUMN_INTERACTIONS_ANSWER_1 = "answer_1";
    public static final String COLUMN_INTERACTIONS_ANSWER_2 = "answer_2";
    public static final String COLUMN_INTERACTIONS_ANSWER_3 = "answer_3";
    public static final String COLUMN_INTERACTIONS_ANSWER_4 = "answer_4";
    public static final String COLUMN_INTERACTIONS_WORD = "word";

    private static final String CREATE_TABLE_INTERACTIONS = "create table "
            + TABLE_INTERACTIONS + "(" +
            COLUMN_INTERACTIONS_ID + " integer primary key autoincrement, " +
            COLUMN_INTERACTIONS_CHAPTER_ID + " integer not null, " +
            COLUMN_INTERACTIONS_TYPE + " integer not null, " +
            COLUMN_INTERACTIONS_INSTRUCTIONS + " text, " +
            COLUMN_INTERACTIONS_POSITIVE_FEEDBACK + " text, " +
            COLUMN_INTERACTIONS_NEGATIVE_FEEDBACK + " text, " +
            COLUMN_INTERACTIONS_POSITIVE_AUDIO_URL + " text, " +
            COLUMN_INTERACTIONS_NEGATIVE_AUDIO_URL + " text, " +
            COLUMN_INTERACTIONS_LATITUDE + " real, " +
            COLUMN_INTERACTIONS_LONGITUDE + " real, " +
            COLUMN_INTERACTIONS_SECRET_CODE + " text, " +
            COLUMN_INTERACTIONS_QUESTION + " text, " +
            COLUMN_INTERACTIONS_CORRECT_ANSWER + " text, " +
            COLUMN_INTERACTIONS_ANSWER_1 + " text, " +
            COLUMN_INTERACTIONS_ANSWER_2 + " text, " +
            COLUMN_INTERACTIONS_ANSWER_3 + " text, " +
            COLUMN_INTERACTIONS_ANSWER_4 + " text, " +
            COLUMN_INTERACTIONS_WORD + " text);";

    
    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_STORIES);
        sqLiteDatabase.execSQL(CREATE_TABLE_CHAPTERS);
        sqLiteDatabase.execSQL(CREATE_TABLE_INTERACTIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        // on upgrade drop older tables
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_STORIES);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAPTERS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_INTERACTIONS);

        // create new tables
        onCreate(sqLiteDatabase);
    }
}

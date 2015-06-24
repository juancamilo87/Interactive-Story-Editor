package fi.oulu.interactivestoryeditor.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import fi.oulu.interactivestoryeditor.model.Chapter;
import fi.oulu.interactivestoryeditor.model.GPSInteraction;
import fi.oulu.interactivestoryeditor.model.Interaction;
import fi.oulu.interactivestoryeditor.model.NFCInteraction;
import fi.oulu.interactivestoryeditor.model.QRCodeInteraction;
import fi.oulu.interactivestoryeditor.model.QuizInteraction;
import fi.oulu.interactivestoryeditor.model.SpellCheckInteraction;

/**
 * Created by JuanCamilo on 5/7/2015.
 */
public class InteractionsDataSource {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;

    private String[] allColumns = {
            MySQLiteHelper.COLUMN_INTERACTIONS_ID,
            MySQLiteHelper.COLUMN_INTERACTIONS_CHAPTER_ID,
            MySQLiteHelper.COLUMN_INTERACTIONS_TYPE,
            MySQLiteHelper.COLUMN_INTERACTIONS_INSTRUCTIONS,
            MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_FEEDBACK,
            MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_FEEDBACK,
            MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_AUDIO_URL,
            MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_AUDIO_URL,
            MySQLiteHelper.COLUMN_INTERACTIONS_LATITUDE,
            MySQLiteHelper.COLUMN_INTERACTIONS_LONGITUDE,
            MySQLiteHelper.COLUMN_INTERACTIONS_SECRET_CODE,
            MySQLiteHelper.COLUMN_INTERACTIONS_QUESTION,
            MySQLiteHelper.COLUMN_INTERACTIONS_CORRECT_ANSWER,
            MySQLiteHelper.COLUMN_INTERACTIONS_ANSWER_1,
            MySQLiteHelper.COLUMN_INTERACTIONS_ANSWER_2,
            MySQLiteHelper.COLUMN_INTERACTIONS_ANSWER_3,
            MySQLiteHelper.COLUMN_INTERACTIONS_ANSWER_4,
            MySQLiteHelper.COLUMN_INTERACTIONS_WORD
    };

    public InteractionsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Interaction createInteraction(Interaction interaction, long chapter_id)
    {
       switch (interaction.getInteractionType())
        {
            case Interaction.GPS_INTERACTION:
                if(interaction instanceof GPSInteraction)
                {
                    return createGPSInteraction((GPSInteraction) interaction, chapter_id);
                }
                break;
            case Interaction.NFC_INTERACTION:
                if(interaction instanceof NFCInteraction)
                {
                    return createNFCInteraction((NFCInteraction) interaction, chapter_id);
                }
                break;
            case Interaction.QR_INTERACTION:
                if(interaction instanceof QRCodeInteraction)
                {
                    return createQRCodeInteraction((QRCodeInteraction) interaction, chapter_id);
                }
                break;
            case Interaction.QUIZ_INTERACTION:
                if(interaction instanceof QuizInteraction)
                {
                    return createQuizInteraction((QuizInteraction) interaction, chapter_id);
                }
                break;
            case Interaction.SPELL_INTERACTION:
                if(interaction instanceof SpellCheckInteraction)
                {
                    return createSpellCheckInteraction((SpellCheckInteraction) interaction, chapter_id);
                }
                break;
            default:
                return null;
        }
        return null;
    }

    public Interaction updateInteraction(Interaction interaction, long chapter_id)
    {
        switch (interaction.getInteractionType())
        {
            case Interaction.GPS_INTERACTION:
                if(interaction instanceof GPSInteraction)
                {
                    return updateInteraction((GPSInteraction) interaction, chapter_id);
                }
                break;
            case Interaction.NFC_INTERACTION:
                if(interaction instanceof NFCInteraction)
                {
                    return updateInteraction((NFCInteraction) interaction, chapter_id);
                }
                break;
            case Interaction.QR_INTERACTION:
                if(interaction instanceof QRCodeInteraction)
                {
                    return updateInteraction((QRCodeInteraction) interaction, chapter_id);
                }
                break;
            case Interaction.QUIZ_INTERACTION:
                if(interaction instanceof QuizInteraction)
                {
                    return updateInteraction((QuizInteraction) interaction, chapter_id);
                }
                break;
            case Interaction.SPELL_INTERACTION:
                if(interaction instanceof SpellCheckInteraction)
                {
                    return updateInteraction((SpellCheckInteraction) interaction, chapter_id);
                }
                break;
            default:
                return null;
        }
        return null;
    }

    public GPSInteraction createGPSInteraction(GPSInteraction interaction, long chapter_id)
    {
        if(interaction.getInteraction_id()!=-1)
        {
            return updateInteraction(interaction, chapter_id);
        }
        else
        {
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_CHAPTER_ID, chapter_id);
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_TYPE, interaction.getInteractionType());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_INSTRUCTIONS, interaction.getInstructions());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_FEEDBACK, interaction.getPositiveTextFeedback());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_FEEDBACK, interaction.getNegativeTextFeedback());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_AUDIO_URL, interaction.getPositiveAudioFeedbackUrl());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_AUDIO_URL, interaction.getNegativeAudioFeedbackUrl());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_LATITUDE, interaction.getLatitude());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_LONGITUDE, interaction.getLongitude());

            Cursor cursor = database.query(MySQLiteHelper.TABLE_INTERACTIONS,
                    allColumns, MySQLiteHelper.COLUMN_INTERACTIONS_CHAPTER_ID + " = " + chapter_id, null,
                    null, null, null);

            if(cursor.moveToFirst())
            {
                long deleteId = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_ID));
                cursor.close();
                deleteInteraction(deleteId);
            }
            long insertId = database.insert(MySQLiteHelper.TABLE_INTERACTIONS, null, values);

            Cursor otherCursor = database.query(MySQLiteHelper.TABLE_INTERACTIONS,
                    allColumns, MySQLiteHelper.COLUMN_INTERACTIONS_ID + " = " + insertId, null, null, null, null);
            otherCursor.moveToFirst();
            GPSInteraction newInteraction = cursorToGPSInteraction(otherCursor);
            otherCursor.close();
            return newInteraction;
        }
    }

    public GPSInteraction updateInteraction(GPSInteraction interaction, long chapter_id)
    {
        if(interaction.getInteraction_id() == -1)
        {
            return null;
        }
        else
        {
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_CHAPTER_ID, chapter_id);
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_TYPE, interaction.getInteractionType());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_INSTRUCTIONS, interaction.getInstructions());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_FEEDBACK, interaction.getPositiveTextFeedback());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_FEEDBACK, interaction.getNegativeTextFeedback());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_AUDIO_URL, interaction.getPositiveAudioFeedbackUrl());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_AUDIO_URL, interaction.getNegativeAudioFeedbackUrl());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_LATITUDE, interaction.getLatitude());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_LONGITUDE, interaction.getLongitude());

            Cursor cursor = database.query(MySQLiteHelper.TABLE_INTERACTIONS,
                    allColumns, MySQLiteHelper.COLUMN_INTERACTIONS_CHAPTER_ID + " = " + chapter_id, null,
                    null, null, null);

            if(cursor.moveToFirst())
            {

                long deleteId = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_ID));
                cursor.close();
                if(deleteId != interaction.getInteraction_id())
                {
                    deleteInteraction(deleteId);
                }
            }

            String strFilter = MySQLiteHelper.COLUMN_INTERACTIONS_ID + "=" + interaction.getInteraction_id();
            database.update(MySQLiteHelper.TABLE_INTERACTIONS, values, strFilter, null);

            Cursor otherCursor = database.query(MySQLiteHelper.TABLE_INTERACTIONS,
                    allColumns, MySQLiteHelper.COLUMN_INTERACTIONS_ID + " = " + interaction.getInteraction_id(), null, null, null, null);
            otherCursor.moveToFirst();
            GPSInteraction newInteraction = cursorToGPSInteraction(otherCursor);
            otherCursor.close();
            return newInteraction;
        }
    }

    public NFCInteraction createNFCInteraction(NFCInteraction interaction, long chapter_id)
    {
        if(interaction.getInteraction_id()!=-1)
        {
            return updateInteraction(interaction, chapter_id);
        }
        else
        {
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_CHAPTER_ID, chapter_id);
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_TYPE, interaction.getInteractionType());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_INSTRUCTIONS, interaction.getInstructions());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_FEEDBACK, interaction.getPositiveTextFeedback());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_FEEDBACK, interaction.getNegativeTextFeedback());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_AUDIO_URL, interaction.getPositiveAudioFeedbackUrl());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_AUDIO_URL, interaction.getNegativeAudioFeedbackUrl());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_SECRET_CODE, interaction.getSecretCode());


            Cursor cursor = database.query(MySQLiteHelper.TABLE_INTERACTIONS,
                    allColumns, MySQLiteHelper.COLUMN_INTERACTIONS_CHAPTER_ID + " = " + chapter_id, null,
                    null, null, null);

            if(cursor.moveToFirst())
            {
                long deleteId = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_ID));
                cursor.close();
                deleteInteraction(deleteId);
            }
            long insertId = database.insert(MySQLiteHelper.TABLE_INTERACTIONS, null, values);

            Cursor otherCursor = database.query(MySQLiteHelper.TABLE_INTERACTIONS,
                    allColumns, MySQLiteHelper.COLUMN_INTERACTIONS_ID + " = " + insertId, null, null, null, null);
            otherCursor.moveToFirst();
            NFCInteraction newInteraction = cursorToNFCInteraction(otherCursor);
            otherCursor.close();
            return newInteraction;
        }
    }

    public NFCInteraction updateInteraction(NFCInteraction interaction, long chapter_id)
    {
        if(interaction.getInteraction_id() == -1)
        {
            return null;
        }
        else
        {
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_CHAPTER_ID, chapter_id);
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_TYPE, interaction.getInteractionType());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_INSTRUCTIONS, interaction.getInstructions());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_FEEDBACK, interaction.getPositiveTextFeedback());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_FEEDBACK, interaction.getNegativeTextFeedback());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_AUDIO_URL, interaction.getPositiveAudioFeedbackUrl());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_AUDIO_URL, interaction.getNegativeAudioFeedbackUrl());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_SECRET_CODE, interaction.getSecretCode());

            Cursor cursor = database.query(MySQLiteHelper.TABLE_INTERACTIONS,
                    allColumns, MySQLiteHelper.COLUMN_INTERACTIONS_CHAPTER_ID + " = " + chapter_id, null,
                    null, null, null);

            if(cursor.moveToFirst())
            {

                long deleteId = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_ID));
                cursor.close();
                if(deleteId != interaction.getInteraction_id())
                {
                    deleteInteraction(deleteId);
                }
            }

            String strFilter = MySQLiteHelper.COLUMN_INTERACTIONS_ID + "=" + interaction.getInteraction_id();
            database.update(MySQLiteHelper.TABLE_INTERACTIONS, values, strFilter, null);

            Cursor otherCursor = database.query(MySQLiteHelper.TABLE_INTERACTIONS,
                    allColumns, MySQLiteHelper.COLUMN_INTERACTIONS_ID + " = " + interaction.getInteraction_id(), null, null, null, null);
            otherCursor.moveToFirst();
            NFCInteraction newInteraction = cursorToNFCInteraction(otherCursor);
            otherCursor.close();
            return newInteraction;
        }
    }

    public QRCodeInteraction createQRCodeInteraction(QRCodeInteraction interaction, long chapter_id)
    {
        if(interaction.getInteraction_id()!=-1)
        {
            return updateInteraction(interaction, chapter_id);
        }
        else
        {
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_CHAPTER_ID, chapter_id);
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_TYPE, interaction.getInteractionType());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_INSTRUCTIONS, interaction.getInstructions());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_FEEDBACK, interaction.getPositiveTextFeedback());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_FEEDBACK, interaction.getNegativeTextFeedback());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_AUDIO_URL, interaction.getPositiveAudioFeedbackUrl());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_AUDIO_URL, interaction.getNegativeAudioFeedbackUrl());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_SECRET_CODE, interaction.getSecretCode());


            Cursor cursor = database.query(MySQLiteHelper.TABLE_INTERACTIONS,
                    allColumns, MySQLiteHelper.COLUMN_INTERACTIONS_CHAPTER_ID + " = " + chapter_id, null,
                    null, null, null);

            if(cursor.moveToFirst())
            {
                long deleteId = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_ID));
                cursor.close();
                deleteInteraction(deleteId);
            }
            long insertId = database.insert(MySQLiteHelper.TABLE_INTERACTIONS, null, values);

            Cursor otherCursor = database.query(MySQLiteHelper.TABLE_INTERACTIONS,
                    allColumns, MySQLiteHelper.COLUMN_INTERACTIONS_ID + " = " + insertId, null, null, null, null);
            otherCursor.moveToFirst();
            QRCodeInteraction newInteraction = cursorToQRCodeInteraction(otherCursor);
            otherCursor.close();
            return newInteraction;
        }
    }

    public QRCodeInteraction updateInteraction(QRCodeInteraction interaction, long chapter_id)
    {
        if(interaction.getInteraction_id() == -1)
        {
            return null;
        }
        else
        {
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_CHAPTER_ID, chapter_id);
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_TYPE, interaction.getInteractionType());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_INSTRUCTIONS, interaction.getInstructions());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_FEEDBACK, interaction.getPositiveTextFeedback());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_FEEDBACK, interaction.getNegativeTextFeedback());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_AUDIO_URL, interaction.getPositiveAudioFeedbackUrl());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_AUDIO_URL, interaction.getNegativeAudioFeedbackUrl());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_SECRET_CODE, interaction.getSecretCode());

            Cursor cursor = database.query(MySQLiteHelper.TABLE_INTERACTIONS,
                    allColumns, MySQLiteHelper.COLUMN_INTERACTIONS_CHAPTER_ID + " = " + chapter_id, null,
                    null, null, null);

            if(cursor.moveToFirst())
            {

                long deleteId = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_ID));
                cursor.close();
                if(deleteId != interaction.getInteraction_id())
                {
                    deleteInteraction(deleteId);
                }
            }

            String strFilter = MySQLiteHelper.COLUMN_INTERACTIONS_ID + "=" + interaction.getInteraction_id();
            database.update(MySQLiteHelper.TABLE_INTERACTIONS, values, strFilter, null);

            Cursor otherCursor = database.query(MySQLiteHelper.TABLE_INTERACTIONS,
                    allColumns, MySQLiteHelper.COLUMN_INTERACTIONS_ID + " = " + interaction.getInteraction_id(), null, null, null, null);
            otherCursor.moveToFirst();
            QRCodeInteraction newInteraction = cursorToQRCodeInteraction(otherCursor);
            otherCursor.close();
            return newInteraction;
        }
    }

    public QuizInteraction createQuizInteraction(QuizInteraction interaction, long chapter_id)
    {
        if(interaction.getInteraction_id()!=-1)
        {
            return updateInteraction(interaction, chapter_id);
        }
        else
        {
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_CHAPTER_ID, chapter_id);
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_TYPE, interaction.getInteractionType());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_INSTRUCTIONS, interaction.getInstructions());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_FEEDBACK, interaction.getPositiveTextFeedback());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_FEEDBACK, interaction.getNegativeTextFeedback());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_AUDIO_URL, interaction.getPositiveAudioFeedbackUrl());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_AUDIO_URL, interaction.getNegativeAudioFeedbackUrl());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_QUESTION, interaction.getQuestion());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_CORRECT_ANSWER, interaction.getCorrectAnswer());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_ANSWER_1, interaction.getAnswer1());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_ANSWER_2, interaction.getAnswer2());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_ANSWER_3, interaction.getAnswer3());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_ANSWER_4, interaction.getAnswer4());


            Cursor cursor = database.query(MySQLiteHelper.TABLE_INTERACTIONS,
                    allColumns, MySQLiteHelper.COLUMN_INTERACTIONS_CHAPTER_ID + " = " + chapter_id, null,
                    null, null, null);

            if(cursor.moveToFirst())
            {
                long deleteId = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_ID));
                cursor.close();
                deleteInteraction(deleteId);
            }
            long insertId = database.insert(MySQLiteHelper.TABLE_INTERACTIONS, null, values);

            Cursor otherCursor = database.query(MySQLiteHelper.TABLE_INTERACTIONS,
                    allColumns, MySQLiteHelper.COLUMN_INTERACTIONS_ID + " = " + insertId, null, null, null, null);
            otherCursor.moveToFirst();
            QuizInteraction newInteraction = cursorToQuizInteraction(otherCursor);
            otherCursor.close();
            return newInteraction;
        }
    }

    public QuizInteraction updateInteraction(QuizInteraction interaction, long chapter_id)
    {
        if(interaction.getInteraction_id() == -1)
        {
            return null;
        }
        else
        {
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_CHAPTER_ID, chapter_id);
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_TYPE, interaction.getInteractionType());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_INSTRUCTIONS, interaction.getInstructions());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_FEEDBACK, interaction.getPositiveTextFeedback());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_FEEDBACK, interaction.getNegativeTextFeedback());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_AUDIO_URL, interaction.getPositiveAudioFeedbackUrl());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_AUDIO_URL, interaction.getNegativeAudioFeedbackUrl());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_QUESTION, interaction.getQuestion());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_CORRECT_ANSWER, interaction.getCorrectAnswer());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_ANSWER_1, interaction.getAnswer1());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_ANSWER_2, interaction.getAnswer2());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_ANSWER_3, interaction.getAnswer3());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_ANSWER_4, interaction.getAnswer4());

            Cursor cursor = database.query(MySQLiteHelper.TABLE_INTERACTIONS,
                    allColumns, MySQLiteHelper.COLUMN_INTERACTIONS_CHAPTER_ID + " = " + chapter_id, null,
                    null, null, null);

            if(cursor.moveToFirst())
            {

                long deleteId = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_ID));
                cursor.close();
                if(deleteId != interaction.getInteraction_id())
                {
                    deleteInteraction(deleteId);
                }
            }

            String strFilter = MySQLiteHelper.COLUMN_INTERACTIONS_ID + "=" + interaction.getInteraction_id();
            database.update(MySQLiteHelper.TABLE_INTERACTIONS, values, strFilter, null);

            Cursor otherCursor = database.query(MySQLiteHelper.TABLE_INTERACTIONS,
                    allColumns, MySQLiteHelper.COLUMN_INTERACTIONS_ID + " = " + interaction.getInteraction_id(), null, null, null, null);
            otherCursor.moveToFirst();
            QuizInteraction newInteraction = cursorToQuizInteraction(otherCursor);
            otherCursor.close();
            return newInteraction;
        }
    }

    public SpellCheckInteraction createSpellCheckInteraction(SpellCheckInteraction interaction, long chapter_id)
    {
        if(interaction.getInteraction_id()!=-1)
        {
            return updateInteraction(interaction, chapter_id);
        }
        else
        {
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_CHAPTER_ID, chapter_id);
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_TYPE, interaction.getInteractionType());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_INSTRUCTIONS, interaction.getInstructions());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_FEEDBACK, interaction.getPositiveTextFeedback());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_FEEDBACK, interaction.getNegativeTextFeedback());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_AUDIO_URL, interaction.getPositiveAudioFeedbackUrl());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_AUDIO_URL, interaction.getNegativeAudioFeedbackUrl());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_WORD, interaction.getWord());

            Cursor cursor = database.query(MySQLiteHelper.TABLE_INTERACTIONS,
                    allColumns, MySQLiteHelper.COLUMN_INTERACTIONS_CHAPTER_ID + " = " + chapter_id, null,
                    null, null, null);

            if(cursor.moveToFirst())
            {
                long deleteId = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_ID));
                cursor.close();
                deleteInteraction(deleteId);
            }
            long insertId = database.insert(MySQLiteHelper.TABLE_INTERACTIONS, null, values);

            Cursor otherCursor = database.query(MySQLiteHelper.TABLE_INTERACTIONS,
                    allColumns, MySQLiteHelper.COLUMN_INTERACTIONS_ID + " = " + insertId, null, null, null, null);
            otherCursor.moveToFirst();
            SpellCheckInteraction newInteraction = cursorToSpellCheckInteraction(otherCursor);
            otherCursor.close();
            return newInteraction;
        }
    }

    public SpellCheckInteraction updateInteraction(SpellCheckInteraction interaction, long chapter_id)
    {
        if(interaction.getInteraction_id() == -1)
        {
            return null;
        }
        else
        {
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_CHAPTER_ID, chapter_id);
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_TYPE, interaction.getInteractionType());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_INSTRUCTIONS, interaction.getInstructions());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_FEEDBACK, interaction.getPositiveTextFeedback());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_FEEDBACK, interaction.getNegativeTextFeedback());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_AUDIO_URL, interaction.getPositiveAudioFeedbackUrl());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_AUDIO_URL, interaction.getNegativeAudioFeedbackUrl());
            values.put(MySQLiteHelper.COLUMN_INTERACTIONS_WORD, interaction.getWord());

            Cursor cursor = database.query(MySQLiteHelper.TABLE_INTERACTIONS,
                    allColumns, MySQLiteHelper.COLUMN_INTERACTIONS_CHAPTER_ID + " = " + chapter_id, null,
                    null, null, null);

            if(cursor.moveToFirst())
            {

                long deleteId = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_ID));
                cursor.close();
                if(deleteId != interaction.getInteraction_id())
                {
                    deleteInteraction(deleteId);
                }
            }

            String strFilter = MySQLiteHelper.COLUMN_INTERACTIONS_ID + "=" + interaction.getInteraction_id();
            database.update(MySQLiteHelper.TABLE_INTERACTIONS, values, strFilter, null);

            Cursor otherCursor = database.query(MySQLiteHelper.TABLE_INTERACTIONS,
                    allColumns, MySQLiteHelper.COLUMN_INTERACTIONS_ID + " = " + interaction.getInteraction_id(), null, null, null, null);
            otherCursor.moveToFirst();
            SpellCheckInteraction newInteraction = cursorToSpellCheckInteraction(otherCursor);
            otherCursor.close();
            return newInteraction;
        }
    }

    public void deleteInteraction(Interaction interaction) {
        long id = interaction.getInteraction_id();
        database.delete(MySQLiteHelper.TABLE_INTERACTIONS, MySQLiteHelper.COLUMN_INTERACTIONS_ID
                + " = " + id, null);
    }

    public void deleteInteraction(long id) {
        database.delete(MySQLiteHelper.TABLE_INTERACTIONS, MySQLiteHelper.COLUMN_INTERACTIONS_ID
                + " = " + id, null);
    }

    private Cursor getCursorById(long interaction_id)
    {
        Cursor cursor = database.query(MySQLiteHelper.TABLE_INTERACTIONS,
                allColumns, MySQLiteHelper.COLUMN_INTERACTIONS_ID + " = " + interaction_id, null,
                null, null, null);

        return cursor;
    }

    private Cursor getCursorByChapterId(long chapter_id)
    {
        Cursor cursor = database.query(MySQLiteHelper.TABLE_INTERACTIONS,
                allColumns, MySQLiteHelper.COLUMN_INTERACTIONS_CHAPTER_ID + " = " + chapter_id, null,
                null, null, null);

        return cursor;
    }

    public Interaction getInteraction(long chapter_id)
    {
        Cursor cursor = getCursorByChapterId(chapter_id);

        if(cursor.moveToFirst())
        {
            Interaction interaction = cursorToInteraction(cursor);
            cursor.close();
            return interaction;

        }
        return null;
    }

    public Interaction getInteraction(Chapter chapter)
    {
        if(chapter.getChapter_id()!=-1)
        {
            Cursor cursor = getCursorByChapterId(chapter.getChapter_id());

            if(cursor.moveToFirst())
            {
                Interaction interaction = cursorToInteraction(cursor);
                cursor.close();
                return interaction;
            }
        }
        return null;
    }

    public GPSInteraction getGPSInteraction(Interaction interaction)
    {
        if(interaction.getInteraction_id()!= -1)
        {
            Cursor cursor = getCursorById(interaction.getInteraction_id());

            if(cursor.moveToFirst())
            {
                GPSInteraction gpsInteraction = cursorToGPSInteraction(cursor);
                cursor.close();
                return gpsInteraction;
            }
        }
        return null;
    }

    public NFCInteraction getNFCInteraction(Interaction interaction)
    {
        if(interaction.getInteraction_id()!= -1)
        {
            Cursor cursor = getCursorById(interaction.getInteraction_id());

            if(cursor.moveToFirst())
            {
                NFCInteraction nfcInteraction = cursorToNFCInteraction(cursor);
                cursor.close();
                return nfcInteraction;
            }
        }
        return null;
    }

    public QRCodeInteraction getQRCodeInteraction(Interaction interaction)
    {
        if(interaction.getInteraction_id()!= -1)
        {
            Cursor cursor = getCursorById(interaction.getInteraction_id());

            if(cursor.moveToFirst())
            {
                QRCodeInteraction qrCodeInteraction = cursorToQRCodeInteraction(cursor);
                cursor.close();
                return qrCodeInteraction;
            }
        }
        return null;
    }

    public QuizInteraction getQuizInteraction(Interaction interaction)
    {
        if(interaction.getInteraction_id()!= -1)
        {
            Cursor cursor = getCursorById(interaction.getInteraction_id());

            if(cursor.moveToFirst())
            {
                QuizInteraction quizInteraction = cursorToQuizInteraction(cursor);
                cursor.close();
                return quizInteraction;
            }
        }
        return null;
    }

    public SpellCheckInteraction getSpellCheckInteraction(Interaction interaction)
    {
        if(interaction.getInteraction_id()!= -1)
        {
            Cursor cursor = getCursorById(interaction.getInteraction_id());

            if(cursor.moveToFirst())
            {
                SpellCheckInteraction spellCheckInteraction = cursorToSpellCheckInteraction(cursor);
                cursor.close();
                return spellCheckInteraction;
            }
        }
        return null;
    }

    private GPSInteraction cursorToGPSInteraction(Cursor cursor) {

        long id = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_ID));
        int type = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_TYPE));
        String instructions = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_INSTRUCTIONS));
        String positive_feedback = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_FEEDBACK));
        String negative_feedback = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_FEEDBACK));
        String positive_url = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_AUDIO_URL));
        String negative_url = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_AUDIO_URL));

        float latitude = cursor.getFloat(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_LATITUDE));
        float longitude = cursor.getFloat(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_LONGITUDE));

        GPSInteraction interaction = new GPSInteraction(type, instructions, positive_feedback, negative_feedback, positive_url, negative_url, latitude, longitude, id);
        return interaction;
    }

    private NFCInteraction cursorToNFCInteraction(Cursor cursor) {

        long id = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_ID));
        int type = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_TYPE));
        String instructions = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_INSTRUCTIONS));
        String positive_feedback = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_FEEDBACK));
        String negative_feedback = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_FEEDBACK));
        String positive_url = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_AUDIO_URL));
        String negative_url = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_AUDIO_URL));

        String secret_code = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_SECRET_CODE));

        NFCInteraction interaction = new NFCInteraction(type, instructions, positive_feedback, negative_feedback, positive_url, negative_url, secret_code, id);
        return interaction;
    }

    private QRCodeInteraction cursorToQRCodeInteraction(Cursor cursor) {

        long id = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_ID));
        int type = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_TYPE));
        String instructions = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_INSTRUCTIONS));
        String positive_feedback = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_FEEDBACK));
        String negative_feedback = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_FEEDBACK));
        String positive_url = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_AUDIO_URL));
        String negative_url = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_AUDIO_URL));

        String secret_code = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_SECRET_CODE));

        QRCodeInteraction interaction = new QRCodeInteraction(type, instructions, positive_feedback, negative_feedback, positive_url, negative_url, secret_code, id);
        return interaction;
    }

    private QuizInteraction cursorToQuizInteraction(Cursor cursor) {

        long id = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_ID));
        int type = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_TYPE));
        String instructions = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_INSTRUCTIONS));
        String positive_feedback = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_FEEDBACK));
        String negative_feedback = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_FEEDBACK));
        String positive_url = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_AUDIO_URL));
        String negative_url = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_AUDIO_URL));

        String question = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_QUESTION));
        String correct_answer = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_CORRECT_ANSWER));
        String answer_1 = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_ANSWER_1));
        String answer_2 = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_ANSWER_2));
        String answer_3 = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_ANSWER_3));
        String answer_4 = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_ANSWER_4));

        QuizInteraction interaction = new QuizInteraction(type, instructions, positive_feedback, negative_feedback, positive_url, negative_url, question, correct_answer, answer_1, answer_2, answer_3, answer_4, id);
        return interaction;
    }

    private SpellCheckInteraction cursorToSpellCheckInteraction(Cursor cursor) {

        long id = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_ID));
        int type = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_TYPE));
        String instructions = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_INSTRUCTIONS));
        String positive_feedback = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_FEEDBACK));
        String negative_feedback = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_FEEDBACK));
        String positive_url = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_AUDIO_URL));
        String negative_url = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_AUDIO_URL));

        String word = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_WORD));

        SpellCheckInteraction interaction = new SpellCheckInteraction(type, instructions, positive_feedback, negative_feedback, positive_url, negative_url, word, id);
        return interaction;
    }

    private Interaction cursorToInteraction(Cursor cursor) {

        long id = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_ID));
        int type = cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_TYPE));
        String instructions = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_INSTRUCTIONS));
        String positive_feedback = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_FEEDBACK));
        String negative_feedback = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_FEEDBACK));
        String positive_url = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_POSITIVE_AUDIO_URL));
        String negative_url = cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_INTERACTIONS_NEGATIVE_AUDIO_URL));

        Interaction interaction = new Interaction(type, instructions, positive_feedback, negative_feedback, positive_url, negative_url, id);
        return interaction;
    }
}

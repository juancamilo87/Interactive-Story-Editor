package fi.oulu.interactivestoryeditor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import fi.oulu.interactivestoryeditor.model.Author;
import fi.oulu.interactivestoryeditor.model.Chapter;
import fi.oulu.interactivestoryeditor.model.Story;


public class AddStoryActivity extends Activity {


    private Author author;
    private List<Chapter> chapters;

    private EditText title_edit;
    private EditText summary_edit;
    private EditText name_edit;
    private EditText lastname_edit;
    private EditText website_edit;
    private EditText email_edit;
    private ListView chapters_list;
    private Button btn_save;
    private Button btn_add_chapter;

    private String title;
    private String summary;
    private String author_name;
    private String author_lastname;
    private String author_website;
    private String author_email;

    private static final int ADD_CHAPTER = 1;

    ArrayAdapter<Chapter> arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_story);

        author = new Author();
        chapters = new ArrayList<Chapter>();

        title_edit = (EditText) findViewById(R.id.story_edt_title);
        summary_edit = (EditText) findViewById(R.id.story_edt_summary);
        name_edit = (EditText) findViewById(R.id.author_edt_name);
        lastname_edit = (EditText) findViewById(R.id.author_edt_lastname);
        website_edit = (EditText) findViewById(R.id.author_edt_website);
        email_edit = (EditText) findViewById(R.id.author_edt_email);

        chapters_list = (ListView) findViewById(R.id.chapters_list);
        btn_save = (Button) findViewById(R.id.story_btn_save);
        btn_add_chapter = (Button) findViewById(R.id.story_btn_add_chapter);

        title = title_edit.getText().toString();
        summary = summary_edit.getText().toString();
        author_name = name_edit.getText().toString();
        author_lastname = lastname_edit.getText().toString();
        author_website = website_edit.getText().toString();
        author_email = email_edit.getText().toString();

        author.setName(author_name);
        author.setLast_name(author_lastname);
        author.setWebsite(author_website);
        author.setEmail(author_email);

        arrayAdapter = new ArrayAdapter<Chapter>(
                this,
                android.R.layout.simple_list_item_1,
                chapters);

        chapters_list.setAdapter(arrayAdapter);

        //Take into account edit of a chapter and not just the creation of one
        if(getIntent().getParcelableExtra("old_story")!= null)
        {
            Story story = (Story) getIntent().getParcelableExtra("old_story");

            title_edit.setText(story.getTitle());
            summary_edit.setText(story.getSummary());
            name_edit.setText(story.getAuthor().getName());
            lastname_edit.setText(story.getAuthor().getLast_name());
            website_edit.setText(story.getAuthor().getWebsite());
            email_edit.setText(story.getAuthor().getEmail());

            chapters = story.getChapters();
            arrayAdapter = new ArrayAdapter<Chapter>(
                    this,
                    android.R.layout.simple_list_item_1,
                    chapters);

            chapters_list.setAdapter(arrayAdapter);
        }

        btn_add_chapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(), AddChapterActivity.class);
                startActivityForResult(i, ADD_CHAPTER);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (verifyFields()) {
                    Story story = new Story(author, title, summary);

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("story", (Parcelable) story);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean verifyFields()
    {
        if(!title.trim().equals(""))
        {
            title = title_edit.getText().toString().trim();
        }
        else
        {
            return false;
        }

        if(!summary.trim().equals(""))
        {
            summary = summary_edit.getText().toString().trim();
        }
        else
        {
            return false;
        }

        if(!author_name.trim().equals(""))
        {
            author_name = name_edit.getText().toString().trim();
        }
        else
        {
            return false;
        }

        if(!author_lastname.trim().equals(""))
        {
            author_lastname = lastname_edit.getText().toString().trim();
        }
        else
        {
            return false;
        }

        if(!author_website.trim().equals(""))
        {
            author_website = website_edit.getText().toString().trim();
        }
        else
        {
            return false;
        }

        if(!author_email.trim().equals(""))
        {
            author_email = email_edit.getText().toString().trim();
        }
        else
        {
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {

            switch(requestCode) {

                case ADD_CHAPTER:
                    Chapter chapter = (Chapter) data.getParcelableExtra("chapter");
                    chapters.add(chapter);
                    arrayAdapter.notifyDataSetChanged();

                    break;


            }
        }

    }


}

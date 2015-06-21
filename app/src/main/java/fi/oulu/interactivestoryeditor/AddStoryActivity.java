package fi.oulu.interactivestoryeditor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import fi.oulu.interactivestoryeditor.model.Author;
import fi.oulu.interactivestoryeditor.model.Chapter;
import fi.oulu.interactivestoryeditor.model.Story;


public class AddStoryActivity extends Activity {


    private Author author;
    private Story story;
    private ArrayList<Chapter> chapters;

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

        ArrayAdapter<Chapter> arrayAdapter = new ArrayAdapter<Chapter>(
                this,
                android.R.layout.simple_list_item_1,
                chapters);

        chapters_list.setAdapter(arrayAdapter);

        btn_add_chapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(), AddChapterActivity.class);
                startActivity(i);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (verifyFields()) {
                    story = new Story(author, title, summary);

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("story", (Parcelable) story);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
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


}

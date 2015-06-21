package fi.oulu.interactivestoryeditor;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import fi.oulu.interactivestoryeditor.model.Author;


public class AddStoryActivity extends Activity {


    private Author author;

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

        title_edit = (EditText) findViewById(R.id.story_edt_title);
        summary_edit = (EditText) findViewById(R.id.story_edt_summary);
        name_edit = (EditText) findViewById(R.id.author_edt_name);
        lastname_edit = (EditText) findViewById(R.id.author_edt_lastname);
        website_edit = (EditText) findViewById(R.id.author_edt_website);
        email_edit = (EditText) findViewById(R.id.author_edt_email);

        chapters_list = (ListView) findViewById(R.id.chapters_list);
        btn_save = (Button) findViewById(R.id.story_btn_save);
        btn_add_chapter = (Button) findViewById(R.id.story_btn_add_chapter);


    }

}

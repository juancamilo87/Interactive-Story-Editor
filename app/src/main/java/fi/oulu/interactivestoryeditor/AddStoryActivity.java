package fi.oulu.interactivestoryeditor;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

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

    private long story_id;

    private Chapter editingChapter;

    private static final int ADD_CHAPTER = 1;
    private static final int EDIT_CHAPTER = 2;

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



        arrayAdapter = new ArrayAdapter<Chapter>(
                this,
                R.layout.list_cell,
                chapters);

        chapters_list.setAdapter(arrayAdapter);

        //Take into account edit of a chapter and not just the creation of one
        if(getIntent().getSerializableExtra("old_story")!= null)
        {
            Story story = (Story) getIntent().getSerializableExtra("old_story");

            title_edit.setText(story.getTitle());
            summary_edit.setText(story.getSummary());
            name_edit.setText(story.getAuthor().getName());
            lastname_edit.setText(story.getAuthor().getLast_name());
            website_edit.setText(story.getAuthor().getWebsite());
            email_edit.setText(story.getAuthor().getEmail());

            chapters = story.getChapters();
            arrayAdapter = new ArrayAdapter<Chapter>(
                    this,
                    R.layout.list_cell,
                    chapters);

            chapters_list.setAdapter(arrayAdapter);
            story_id = story.getStory_id();
        }
        else
        {
            getStoryId();
        }

        chapters_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                editingChapter = arrayAdapter.getItem(i);
                Intent intent = new Intent(getApplicationContext(), AddChapterActivity.class);
                intent.putExtra("old_chapter", (Serializable) editingChapter);
                intent.putExtra("story_id",story_id);
                intent.putExtra("chapter_number",i+1);
                startActivityForResult(intent, EDIT_CHAPTER);
            }
        });

        title = title_edit.getText().toString();
        summary = summary_edit.getText().toString();
        author_name = name_edit.getText().toString();
        author_lastname = lastname_edit.getText().toString();
        author_website = website_edit.getText().toString();
        author_email = email_edit.getText().toString();

        btn_add_chapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), AddChapterActivity.class);
                i.putExtra("story_id", story_id);
                i.putExtra("chapter_number",chapters.size()+1);
                startActivityForResult(i, ADD_CHAPTER);
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (verifyFields()) {
                    author = new Author(author_name,author_lastname,author_website,author_email);
                    Story story = new Story(author, title, summary, story_id);
                    story.setChapters(chapters);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("story", (Serializable) story);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getStoryId() {
        new GetStoryIdTask().execute();
    }

    private boolean verifyFields()
    {
        if(!title_edit.getText().toString().trim().equals(""))
        {
            title = title_edit.getText().toString().trim();
        }
        else
        {
            return false;
        }

        if(!summary_edit.getText().toString().trim().equals(""))
        {
            summary = summary_edit.getText().toString().trim();
        }
        else
        {
            return false;
        }

        if(!name_edit.getText().toString().trim().equals(""))
        {
            author_name = name_edit.getText().toString().trim();
        }
        else
        {
            return false;
        }

        if(!lastname_edit.getText().toString().trim().equals(""))
        {
            author_lastname = lastname_edit.getText().toString().trim();
        }
        else
        {
            return false;
        }

        author_website = website_edit.getText().toString().trim();

        author_email = email_edit.getText().toString().trim();


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
            Chapter chapter;
            switch(requestCode) {

                case ADD_CHAPTER:
                    chapter = (Chapter) data.getSerializableExtra("chapter");
                    chapters.add(chapter);
                    arrayAdapter.notifyDataSetChanged();
                    break;

                case EDIT_CHAPTER:

                    chapter = (Chapter) data.getSerializableExtra("chapter");
                    int index = chapters.indexOf(editingChapter);
                    chapters.remove(editingChapter);
                    chapters.add(index, chapter);
                    arrayAdapter.notifyDataSetChanged();
                    break;


            }
        }

    }

    private class GetStoryIdTask extends AsyncTask<Void, Void, String> {

        protected String doInBackground(Void... params) {
            String jsonResponse = null;

            HttpClient client = new DefaultHttpClient();
            String URL = "http://memoryhelper.netne.net/interactivestory/index.php/stories/create_new_story";

            try
            {
                // Create Request to server and get response
                StringBuilder builder = new StringBuilder();
                HttpGet httpGet = new HttpGet(URL);

                HttpResponse response = client.execute(httpGet);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(content));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    Log.v("Getter", "Your data: " + builder.toString());
                    jsonResponse = builder.toString().split("divider")[0];
                } else {
                    Log.e("Getter", "Failed to download file");
                }
            }
            catch(Exception ex)
            {
                Log.e("Getter", "Failed"); //response data
            }


            return jsonResponse;
        }

        protected void onPostExecute(String result) {
            try
            {
                JSONObject jObject = new JSONObject(result);
                long id = jObject.getLong("data");
                story_id = id;
            }
            catch(Exception e)
            {
                Log.e("JSON", "Failed"); //response data
            }
        }
    }

}

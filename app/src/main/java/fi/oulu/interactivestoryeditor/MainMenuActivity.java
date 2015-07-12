package fi.oulu.interactivestoryeditor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fi.oulu.interactivestoryeditor.model.Author;
import fi.oulu.interactivestoryeditor.model.Chapter;
import fi.oulu.interactivestoryeditor.model.GPSInteraction;
import fi.oulu.interactivestoryeditor.model.Interaction;
import fi.oulu.interactivestoryeditor.model.NFCInteraction;
import fi.oulu.interactivestoryeditor.model.QRCodeInteraction;
import fi.oulu.interactivestoryeditor.model.QuizInteraction;
import fi.oulu.interactivestoryeditor.model.SpellCheckInteraction;
import fi.oulu.interactivestoryeditor.model.Story;
import fi.oulu.interactivestoryeditor.database.StoriesDataSource;


public class MainMenuActivity extends Activity {

    private static final int ADD_STORY = 1;
    private static final int EDIT_STORY = 2;

    private static final String postURL = "http://memoryhelper.netne.net/interactivestory/index.php/stories/add_new_story";

    private ArrayAdapter<Story> adapter;
    private List<Story> stories;

    private Context context;

    private Story editingStory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main_menu);
        context = this;
        setContentView(R.layout.activity_show_story_list);
        Log.d("SP", "Test log works?");
        stories = new ArrayList<>();
        get_story_list();
//        showPopup(MainMenuActivity.this);

        ListView storyList = (ListView) findViewById(R.id.story_list);
        adapter = new ArrayAdapter<>(this,
                R.layout.list_cell,
                stories);
        storyList.setEmptyView(findViewById(R.id.story_empty_view));

        storyList.setAdapter(adapter);

        storyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                editingStory = adapter.getItem(i);
                Intent intent = new Intent(context, AddStoryActivity.class);
                intent.putExtra("old_story", (Serializable) editingStory);
                startActivityForResult(intent, EDIT_STORY);
            }
        });

        storyList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Story theStory = adapter.getItem(i);

                displayDialog(theStory);
                return true;
            }
        });

        ((Button) findViewById(R.id.story_list_btn_add_story)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddStoryActivity.class);
                startActivityForResult(intent, ADD_STORY);
            }
        });



    }

    private void displayDialog(final Story theStory) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                MainMenuActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        builderSingle.setCustomTitle(inflater.inflate(R.layout.one_story_title, null));
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                MainMenuActivity.this,
                R.layout.list_cell);
        arrayAdapter.add("Delete");
        arrayAdapter.add("Upload");

        builderSingle.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        switch (strName)
                        {
                            case "Delete":
                                StoriesDataSource storiesDataSource = new StoriesDataSource(context);
                                storiesDataSource.open();
                                storiesDataSource.deleteStory(theStory);
                                storiesDataSource.close();
                                get_story_list();
                                adapter.notifyDataSetChanged();
                                break;
                            case "Upload":
                                Toast.makeText(context,"Uploading Story: "+theStory.getTitle(),Toast.LENGTH_SHORT).show();
                                new UploadStoryTask(theStory,context).execute();
                                break;
                        }
                        dialog.dismiss();
                    }
                });
        builderSingle.show();
    }

//    private void showPopup(final Activity context) {
//        RelativeLayout viewGroup = (RelativeLayout) context.findViewById(R.id.no_story_popup);
//        LayoutInflater layoutInflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        final View layout = layoutInflater.inflate(R.layout.popup_no_stories, viewGroup);
//        Button popup_ok_btn = (Button) layout.findViewById(R.id.popup_btn_ok);
//        // Create the popup window
//        final PopupWindow popup = new PopupWindow(context);
//        popup.setContentView(layout);
//        popup.setWidth(600);
//        popup.setHeight(600);
//        popup.setFocusable(true);
//
//        // Displaying the popup centralized
//        //To avoid BadTokenException, you need to defer showing the popup until after all the lifecycle methods are called (-> activity window is displayed):
//        layout.post(new Runnable() {
//            public void run() {
//                popup.showAtLocation(layout, Gravity.CENTER, 0, 0);
//            }
//        });
//
//        popup_ok_btn.setOnClickListener(new Button.OnClickListener() {
//            public void onClick(View v) {
//                popup.dismiss();
//            }
//        });
//    }

    //Some errors, maybe incorrect usage of getAllStories function, or lack of database in emulator.
    //Get list of story using database functions, show the stories or tip if none
    public void get_story_list() {
        StoriesDataSource sds = new StoriesDataSource(this);
        sds.open();
        stories.clear();
        stories.addAll(sds.getAllStories());
        sds.close();
        Log.d("SP", stories.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            StoriesDataSource storiesDataSource = new StoriesDataSource(this);
            Story newStory;
            switch(requestCode) {

                case ADD_STORY:

                    Story story = (Story) data.getSerializableExtra("story");

                    storiesDataSource.open();
                    newStory = storiesDataSource.createStory(story);
                    storiesDataSource.close();
                    stories.add(newStory);
                    adapter.notifyDataSetChanged();
                    break;
                case EDIT_STORY:

                    Story editStory = (Story) data.getSerializableExtra("story");
                    editStory.setStory_id(editingStory.getStory_id());
                    storiesDataSource.open();
                    newStory = storiesDataSource.updateStory(editStory);
                    storiesDataSource.close();
                    int index = stories.indexOf(editingStory);
                    stories.remove(editingStory);
                    stories.add(index,newStory);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    private class UploadStoryTask extends AsyncTask<Void, Void, Boolean> {

        private Story story;
        private Context context;

        public UploadStoryTask(Story story, Context context)
        {
            this.story = story;
            this.context = context;
        }

        protected Boolean doInBackground(Void... params) {
            if(story==null)
            {
                return false;
            }
            JSONObject json = new JSONObject();
            try
            {
                //Author
                Author author = story.getAuthor();
                JSONObject authorJson = new JSONObject();
                authorJson.put("name",author.getName());
                authorJson.put("last_name",author.getLast_name());
                authorJson.put("website",author.getWebsite());
                authorJson.put("email",author.getEmail());
                json.put("author",authorJson);
                //Story
                json.put("story_id",story.getStory_id());
                json.put("title",story.getTitle());
                json.put("summary",story.getSummary());
                //Chapters
                List<Chapter> chapters = story.getChapters();
                JSONArray chaptersJson = new JSONArray();
                for(int i = 0; i < chapters.size();i++)
                {
                    JSONObject chapterJson = new JSONObject();
                    Chapter chapter = chapters.get(i);
                    chapterJson.put("number",i+1);
                    chapterJson.put("title",chapter.getTitle());
                    chapterJson.put("text",chapter.getText());
                    chapterJson.put("image_url",chapter.getImageUrl());
                    chapterJson.put("video_url",chapter.getVideoUrl());
                    chapterJson.put("audio_url",chapter.getAudioUrl());
                    //Interaction
                    if(chapter.getInteraction()!=null)
                    {
                        Interaction interaction = chapter.getInteraction();
                        JSONObject interactionJson = new JSONObject();
                        interactionJson.put("type",interaction.getInteractionType());
                        interactionJson.put("instructions",interaction.getInstructions());
                        interactionJson.put("positive_feedback",interaction.getPositiveTextFeedback());
                        interactionJson.put("negative_feedback",interaction.getNegativeTextFeedback());
                        interactionJson.put("positive_audio_url",interaction.getPositiveAudioFeedbackUrl());
                        interactionJson.put("negative_audio_url",interaction.getNegativeAudioFeedbackUrl());

                        switch (interaction.getInteractionType())
                        {
                            case Interaction.GPS_INTERACTION:
                                interactionJson.put("latitude",((GPSInteraction)interaction).getLatitude());
                                interactionJson.put("longitude",((GPSInteraction)interaction).getLatitude());
                                break;
                            case Interaction.NFC_INTERACTION:
                                interactionJson.put("secret_code",((NFCInteraction)interaction).getSecretCode());
                                break;
                            case Interaction.QR_INTERACTION:
                                interactionJson.put("secret_code",((QRCodeInteraction)interaction).getSecretCode());
                                break;
                            case Interaction.QUIZ_INTERACTION:
                                interactionJson.put("question",((QuizInteraction)interaction).getQuestion());
                                interactionJson.put("correct_answer",((QuizInteraction)interaction).getCorrectAnswer());
                                interactionJson.put("answer_1",((QuizInteraction)interaction).getAnswer1());
                                interactionJson.put("answer_2",((QuizInteraction)interaction).getAnswer2());
                                interactionJson.put("answer_3",((QuizInteraction)interaction).getAnswer3());
                                interactionJson.put("answer_4",((QuizInteraction)interaction).getAnswer4());
                                break;
                            case Interaction.SPELL_INTERACTION:
                                interactionJson.put("word",((SpellCheckInteraction)interaction).getWord());
                                break;
                        }

                        chapterJson.put("interaction",interactionJson);
                    }
                    chaptersJson.put(chapterJson);
                }
                json.put("chapters",chaptersJson);

                Log.d("JSON Story",json.toString());
            }
            catch(Exception e)
            {
                Log.e("Error creating JSON", "JSON could not be created");
                Log.e("Error message",e.getMessage());
            }

            String URL = postURL;
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost(URL);

            try
            {
                AbstractHttpEntity entity = new ByteArrayEntity(json.toString().getBytes("UTF8"));
                entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                request.setEntity(entity);
                HttpResponse response = client.execute(request);

                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            catch(Exception e)
            {
                Log.e("Error uploading story", "HttpPost failed");
                Log.e("Error message",e.getMessage());
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            if(result)
            {
                Toast.makeText(context,"Story " + story.getTitle() + " uploaded",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(context,"Error uploading story " + story.getTitle(),Toast.LENGTH_SHORT).show();
            }
        }
    }
}

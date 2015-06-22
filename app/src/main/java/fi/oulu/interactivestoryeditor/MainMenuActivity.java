package fi.oulu.interactivestoryeditor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
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
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fi.oulu.interactivestoryeditor.model.Story;
import fi.oulu.interactivestoryeditor.database.StoriesDataSource;


public class MainMenuActivity extends Activity {

    private static final int ADD_STORY = 1;
    private static final int EDIT_STORY = 2;

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

        ((Button) findViewById(R.id.story_list_btn_add_story)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddStoryActivity.class);
                startActivityForResult(intent, ADD_STORY);
            }
        });



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
        stories = sds.getAllStories();
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
}

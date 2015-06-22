package fi.oulu.interactivestoryeditor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import java.util.ArrayList;
import java.util.List;

import fi.oulu.interactivestoryeditor.model.Story;
import fi.oulu.interactivestoryeditor.database.StoriesDataSource;


public class MainMenuActivity extends Activity {

    private static final int REQUEST_PICK_FILE = 1;
    private File selectedFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main_menu);
        setContentView(R.layout.activity_show_story_list);
        Log.d("SP", "Test log works?");
        get_story_list();
        showPopup(MainMenuActivity.this);

        Button uploadBtn = (Button)findViewById(R.id.upload_file);

        uploadBtn.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FilePicker.class);
                startActivityForResult(intent, REQUEST_PICK_FILE);
            }
        });

    }

    private void showPopup(final Activity context) {
        RelativeLayout viewGroup = (RelativeLayout) context.findViewById(R.id.no_story_popup);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = layoutInflater.inflate(R.layout.popup_no_stories, viewGroup);
        Button popup_ok_btn = (Button) layout.findViewById(R.id.popup_btn_ok);
        // Create the popup window
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(600);
        popup.setHeight(600);
        popup.setFocusable(true);

        // Displaying the popup centralized
        //To avoid BadTokenException, you need to defer showing the popup until after all the lifecycle methods are called (-> activity window is displayed):
        layout.post(new Runnable() {
            public void run() {
                popup.showAtLocation(layout, Gravity.CENTER, 0, 0);
            }
        });

        popup_ok_btn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                popup.dismiss();
            }
        });
    }

    //Some errors, maybe incorrect usage of getAllStories function, or lack of database in emulator.
    //Get list of story using database functions, show the stories or tip if none
    public void get_story_list() {
        StoriesDataSource sds = new StoriesDataSource(this);
        sds.open();
        List<Story> story_list = sds.getAllStories();
        sds.close();
        Log.d("SP", story_list.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {

            switch(requestCode) {

                case REQUEST_PICK_FILE:

                    if(data.hasExtra(FilePicker.EXTRA_FILE_PATH)) {

                        selectedFile = new File(data.getStringExtra(FilePicker.EXTRA_FILE_PATH));
                        Toast.makeText(getApplicationContext(), selectedFile.getPath(), Toast.LENGTH_LONG).show();

                        Log.d( "external path", Environment.getExternalStorageDirectory().getAbsolutePath() );
                        Log.d( "selected file", selectedFile.getPath() );

                        // gather your request parameters
                        File myFile = new File( selectedFile.getPath().toString() );

                        if ( myFile.exists() ) {
                            RequestParams params = new RequestParams();
                            try {
                                params.put("profile_picture", myFile, "application/octet-stream");
                            } catch(FileNotFoundException e) {}

                            // send request
                            AsyncHttpClient client = new AsyncHttpClient();
                            client.post("http://memoryhelper.netne.net/fileupload/upload.php", params, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] bytes) {
                                    Toast.makeText(getApplicationContext(), "in success", Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] bytes, Throwable throwable) {
                                    Toast.makeText(getApplicationContext(), throwable.getMessage().toString(), Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "file not found", Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
            }
        }

    }
}

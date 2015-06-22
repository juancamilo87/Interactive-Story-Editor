package fi.oulu.interactivestoryeditor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.File;
import java.io.FileNotFoundException;

import fi.oulu.interactivestoryeditor.model.Chapter;
import fi.oulu.interactivestoryeditor.model.Interaction;


public class AddChapterActivity extends Activity {

    private static final int REQUEST_IMAGE_FILE = 1;
    private static final int REQUEST_VIDEO_FILE = 2;
    private static final int REQUEST_AUDIO_FILE = 3;
    private static final int ADD_INTERACTION = 4;

    private Button btn_save;
    private EditText edt_title;
    private ImageButton btn_image;
    private ImageButton btn_video;
    private ImageButton btn_audio;
    private EditText edt_content;
    private Button btn_add_interaction;

    private String title;
    private String content;
    private String image_url;
    private String video_url;
    private String audio_url;

    private boolean image_uploading;
    private boolean video_uploading;
    private boolean audio_uploading;

    private boolean old_image;
    private boolean old_video;
    private boolean old_audio;

    private Interaction interaction;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chapter);

        context = this;

        btn_save = (Button) findViewById(R.id.chapter_btn_save);
        edt_title = (EditText) findViewById(R.id.chapter_edt_title);
        btn_image = (ImageButton) findViewById(R.id.chapter_btn_image);
        btn_video = (ImageButton) findViewById(R.id.chapter_btn_video);
        btn_audio = (ImageButton) findViewById(R.id.chapter_btn_audio);
        edt_content = (EditText) findViewById(R.id.chapter_edt_content);
        btn_add_interaction = (Button) findViewById(R.id.chapter_btn_add_interaction);

        image_url = "";
        video_url = "";
        audio_url = "";

        image_uploading = false;
        video_uploading = false;
        audio_uploading = false;

        old_image = true;
        old_video = true;
        old_audio = true;

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!image_uploading&&!video_uploading&&!audio_uploading)
                {
                    if (verifyFields()) {
                        Chapter chapter = new Chapter(title, content, image_url, video_url, audio_url);
                        if (interaction != null) {
                            chapter.setInteraction(interaction);
                        }

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("chapter", (Parcelable) chapter);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    } else {
                        Toast.makeText(context, "Please complete at least the title and the body of the chapter",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(context, "Please wait a moment till the files finish uploading",Toast.LENGTH_SHORT).show();
                }

            }
        });


        //Take into account edit of a chapter and not just the creation of one
        if(getIntent().getParcelableExtra("old_chapter")!= null)
        {
            Chapter chapter = (Chapter) getIntent().getParcelableExtra("old_chapter");

            edt_title.setText(chapter.getTitle());
            edt_content.setText(chapter.getText());
            image_url = chapter.getImageUrl();
            video_url = chapter.getVideoUrl();
            audio_url = chapter.getAudioUrl();
            interaction = chapter.getInteraction();
            if(interaction != null)
            {
                btn_add_interaction.setText("Edit Interaction");
            }
        }

        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(image_url!=null && !image_url.equals("") && old_image)
                {
                    Toast.makeText(context,"A file is already associated, if you want to change it please click again.", Toast.LENGTH_SHORT).show();
                    old_image = false;
                }
                else
                {
                    image_uploading = true;
                    Intent intent = new Intent(getApplicationContext(), FilePicker.class);
                    startActivityForResult(intent, REQUEST_IMAGE_FILE);
                }

            }
        });

        btn_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(video_url!=null && !video_url.equals("") && old_video)
                {
                    Toast.makeText(context,"A file is already associated, if you want to change it please click again.", Toast.LENGTH_SHORT).show();
                    old_video = false;
                }
                else {
                    video_uploading = true;
                    Intent intent = new Intent(getApplicationContext(), FilePicker.class);
                    startActivityForResult(intent, REQUEST_VIDEO_FILE);
                }
            }
        });

        btn_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(audio_url!=null && !audio_url.equals("") && old_audio)
                {
                    Toast.makeText(context,"A file is already associated, if you want to change it please click again.", Toast.LENGTH_SHORT).show();
                    old_audio = false;
                }
                else {
                    audio_uploading = true;
                    Intent intent = new Intent(getApplicationContext(), FilePicker.class);
                    startActivityForResult(intent, REQUEST_AUDIO_FILE);
                }
            }
        });

        btn_add_interaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: button to create interaction or edit previous interaction
                if(interaction!= null)
                {
                    //Previous value exists
                }
                else
                {
                    
                }
            }
        });

    }

    private boolean verifyFields()
    {
        if(!edt_title.getText().toString().trim().equals(""))
        {
            title = edt_title.getText().toString().trim();
        }
        else
        {
            return false;
        }

        if(!edt_content.getText().toString().trim().equals(""))
        {
            content = edt_content.getText().toString().trim();
        }
        else
        {
            return false;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {

            switch(requestCode) {

                case REQUEST_IMAGE_FILE:

                    if(data.hasExtra(FilePicker.EXTRA_FILE_PATH)) {

                        final File selectedFile = new File(data.getStringExtra(FilePicker.EXTRA_FILE_PATH));
                        //Toast.makeText(getApplicationContext(), selectedFile.getPath(), Toast.LENGTH_LONG).show();

                        Log.d("external path", Environment.getExternalStorageDirectory().getAbsolutePath());
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
                                    image_uploading = false;
                                    image_url = "http://memoryhelper.netne.net/fileupload/" + selectedFile.getPath();
                                    Toast.makeText(getApplicationContext(), "Image file uploaded.", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] bytes, Throwable throwable) {
                                    image_uploading = false;
                                    Toast.makeText(getApplicationContext(), "Error uploading image file. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            //Toast.makeText(getApplicationContext(), "file not found", Toast.LENGTH_LONG).show();
                        }
                    }
                    break;

                case REQUEST_VIDEO_FILE:

                    if(data.hasExtra(FilePicker.EXTRA_FILE_PATH)) {

                        final File selectedFile = new File(data.getStringExtra(FilePicker.EXTRA_FILE_PATH));
                        //Toast.makeText(getApplicationContext(), selectedFile.getPath(), Toast.LENGTH_LONG).show();

                        Log.d("external path", Environment.getExternalStorageDirectory().getAbsolutePath());
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
                                    video_uploading = false;
                                    video_url = "http://memoryhelper.netne.net/fileupload/" + selectedFile.getPath();
                                    Toast.makeText(getApplicationContext(), "Video file uploaded.", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] bytes, Throwable throwable) {
                                    video_uploading = false;
                                    Toast.makeText(getApplicationContext(), "Error uploading video file. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            //Toast.makeText(getApplicationContext(), "file not found", Toast.LENGTH_LONG).show();
                        }
                    }
                    break;

                case REQUEST_AUDIO_FILE:

                    if(data.hasExtra(FilePicker.EXTRA_FILE_PATH)) {

                        final File selectedFile = new File(data.getStringExtra(FilePicker.EXTRA_FILE_PATH));
                        //Toast.makeText(getApplicationContext(), selectedFile.getPath(), Toast.LENGTH_LONG).show();

                        Log.d("external path", Environment.getExternalStorageDirectory().getAbsolutePath());
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
                                    audio_uploading = false;
                                    audio_url = "http://memoryhelper.netne.net/fileupload/" + selectedFile.getPath();
                                    Toast.makeText(getApplicationContext(), "Audio file uploaded.", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] bytes, Throwable throwable) {
                                    audio_uploading = false;
                                    Toast.makeText(getApplicationContext(), "Error uploading audio file. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            //Toast.makeText(getApplicationContext(), "file not found", Toast.LENGTH_LONG).show();
                        }
                    }
                    break;

                case ADD_INTERACTION:

                    //TODO: Interaction gotten from startactivityforresult
                    break;
            }
        }

    }

    @Override
    public void onBackPressed() {
        if(!image_uploading&&!video_uploading&&!audio_uploading)
        {
            setResult(RESULT_CANCELED);
            finish();
        }
        {
            Toast.makeText(this, "Please wait a moment till the files are finished uploading",Toast.LENGTH_SHORT).show();
        }
    }


}

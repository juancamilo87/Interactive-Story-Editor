package fi.oulu.interactivestoryeditor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;

import fi.oulu.interactivestoryeditor.model.Chapter;
import fi.oulu.interactivestoryeditor.model.GPSInteraction;
import fi.oulu.interactivestoryeditor.model.Interaction;
import fi.oulu.interactivestoryeditor.model.NFCInteraction;
import fi.oulu.interactivestoryeditor.model.QRCodeInteraction;
import fi.oulu.interactivestoryeditor.model.QuizInteraction;


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
                        returnIntent.putExtra("chapter", (Serializable) chapter);
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
        if(getIntent().getSerializableExtra("old_chapter")!= null)
        {
            Chapter chapter = (Chapter) getIntent().getSerializableExtra("old_chapter");

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
                if(interaction!= null)
                {
                    showEditInteractionDialog();
                }
                else
                {
                    showInteractionDialog();
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
                    interaction = (Interaction) data.getSerializableExtra("interaction");
                    btn_add_interaction.setText("Edit Interaction");
                    break;
            }
        }
        else {
            switch (requestCode) {
                case ADD_INTERACTION:
                    interaction = null;
                    btn_add_interaction.setText("Add Interaction");
                    break;
                case REQUEST_IMAGE_FILE:
                    image_uploading = false;
                    break;
                case REQUEST_AUDIO_FILE:
                    audio_uploading = false;
                    break;
                case REQUEST_VIDEO_FILE:
                    video_uploading = false;
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
        }else
        {
            Toast.makeText(this, "Please wait a moment till the files are finished uploading",Toast.LENGTH_SHORT).show();
        }
    }


    private void showInteractionDialog()
    {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                AddChapterActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        builderSingle.setCustomTitle(inflater.inflate(R.layout.add_interaction_title, null));
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                AddChapterActivity.this,
                R.layout.list_cell);
        arrayAdapter.add("GPS");
        arrayAdapter.add("NFC");
        arrayAdapter.add("QR Code");
        arrayAdapter.add("Quiz");
        arrayAdapter.add("Spell Check");

        builderSingle.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        Intent intent;
                        switch (strName)
                        {
                            case "GPS":
                                intent = new Intent(context, AddGPSInteraction.class);
                                break;
                            case "NFC":
                                intent = new Intent(context, AddNFCInteraction.class);
                                break;
                            case "QR Code":
                                intent = new Intent(context, AddQRCodeInteraction.class);
                                break;
                            case "Quiz":
                                intent = new Intent(context, AddQuizInteraction.class);
                                break;
                            case "Spell Check":
                                intent = new Intent(context, AddSpellCheckInteraction.class);
                                break;
                            default:
                                intent = new Intent();
                                break;
                        }
                        startActivityForResult(intent,ADD_INTERACTION);
                        dialog.dismiss();
                    }
                });
        builderSingle.show();
    }

    private void showEditInteractionDialog()
    {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                AddChapterActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        builderSingle.setCustomTitle(inflater.inflate(R.layout.edit_interaction_title, null));
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                AddChapterActivity.this,
                R.layout.list_cell);

        arrayAdapter.add("Edit " + interaction.getStringType() + " interaction");
        arrayAdapter.add("Discard and create new");

        builderSingle.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);

                        if (strName.equals("Discard and create new")) {
                            dialog.dismiss();
                            showInteractionDialog();
                        } else {
                            Intent intent;
                            switch (strName)
                            {
                                case "GPS":
                                    intent = new Intent(context, AddGPSInteraction.class);
                                    break;
                                case "NFC":
                                    intent = new Intent(context, AddNFCInteraction.class);
                                    break;
                                case "QR Code":
                                    intent = new Intent(context, AddQRCodeInteraction.class);
                                    break;
                                case "Quiz":
                                    intent = new Intent(context, AddQuizInteraction.class);
                                    break;
                                case "Spell Check":
                                    intent = new Intent(context, AddSpellCheckInteraction.class);
                                    break;
                                default:
                                    intent = new Intent();
                                    break;
                            }
                            intent.putExtra("old_interaction",interaction);
                            startActivityForResult(intent,ADD_INTERACTION);
                        }
                    }
                });
        builderSingle.show();
    }

}

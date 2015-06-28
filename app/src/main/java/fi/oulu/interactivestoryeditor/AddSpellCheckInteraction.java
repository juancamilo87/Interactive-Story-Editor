package fi.oulu.interactivestoryeditor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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
import java.io.Serializable;

import fi.oulu.interactivestoryeditor.model.SpellCheckInteraction;


public class AddSpellCheckInteraction extends Activity {

    private EditText word_edt;
    private EditText instruct_edt;
    private EditText positive_feed_edt;
    private EditText negative_feed_edt;
    private Button btn_save;
    private ImageButton btn_positive;
    private ImageButton btn_negative;
    private Context context;

    private String word;
    private String instruct;
    private String pos_feed;
    private String neg_feed;
    private String positive_url;
    private String negative_url;

    private long story_id;
    private int chapter_number;

    private boolean old_positive;
    private boolean old_negative;

    private boolean positive_uploading;
    private boolean negative_uploading;

    private static final int REQUEST_POSITIVE_FILE = 1;
    private static final int REQUEST_NEGATIVE_FILE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spell_check_interaction);

        context = this;
        story_id = getIntent().getLongExtra("story_id",-1);
        chapter_number = getIntent().getIntExtra("chapter_number",-1);

        word_edt = (EditText) findViewById(R.id.spell_check_word_edt);
        instruct_edt = (EditText) findViewById(R.id.spell_check_instructions_edt);
        positive_feed_edt = (EditText) findViewById(R.id.spell_check_positive_feed_edt);
        negative_feed_edt = (EditText) findViewById(R.id.spell_check_negative_feed_edt);
        btn_positive= (ImageButton) findViewById(R.id.spell_check_btn_positive);
        btn_negative = (ImageButton) findViewById(R.id.spell_check_btn_negative);
        btn_save = (Button) findViewById(R.id.spell_check_btn_save);

        old_positive = true;
        old_negative = true;

        positive_uploading = false;
        negative_uploading = false;

        if(getIntent().getSerializableExtra("old_interaction")!= null) {

            SpellCheckInteraction spellCheckInteraction = (SpellCheckInteraction) getIntent().getSerializableExtra("old_interaction");
            word_edt.setText(spellCheckInteraction.getWord());
            instruct_edt.setText(spellCheckInteraction.getInstructions());
            positive_feed_edt.setText(spellCheckInteraction.getPositiveTextFeedback());
            negative_feed_edt.setText(spellCheckInteraction.getNegativeTextFeedback());
            positive_url = spellCheckInteraction.getPositiveAudioFeedbackUrl();
            negative_url = spellCheckInteraction.getNegativeAudioFeedbackUrl();
        }

        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(positive_url !=null && !positive_url.equals("") && old_positive)
                {
                    Toast.makeText(context, "A file is already associated, if you want to change it please click again.", Toast.LENGTH_SHORT).show();
                    old_positive = false;
                }
                else
                {
                    positive_uploading = true;
                    Intent intent = new Intent(getApplicationContext(), FilePicker.class);
                    startActivityForResult(intent, REQUEST_POSITIVE_FILE);
                }

            }
        });

        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(negative_url !=null && !negative_url.equals("") && old_negative)
                {
                    Toast.makeText(context,"A file is already associated, if you want to change it please click again.", Toast.LENGTH_SHORT).show();
                    old_negative = false;
                } else
                {
                    negative_uploading = true;
                    Intent intent = new Intent(getApplicationContext(), FilePicker.class);
                    startActivityForResult(intent, REQUEST_NEGATIVE_FILE);
                }

            }
        });


        btn_save.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(positive_uploading||negative_uploading)
                {
                    Toast.makeText(getApplicationContext(),"Please wait till the files finish uploading",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (verifyFields()) {
                        SpellCheckInteraction spCheckInt = new SpellCheckInteraction(instruct, pos_feed, neg_feed, positive_url, negative_url, word);

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("interaction", (Serializable) spCheckInt);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private boolean verifyFields()
    {

        if(!word_edt.getText().toString().trim().equals(""))
        {
            word = word_edt.getText().toString().trim();
        }
        else
        {
            return false;
        }

        if(!instruct_edt.getText().toString().trim().equals(""))
        {
            instruct = instruct_edt.getText().toString().trim();
        }
        else
        {
            return false;
        }

        if(!positive_feed_edt.getText().toString().trim().equals(""))
        {
            pos_feed = positive_feed_edt.getText().toString().trim();
        }
        else
        {
            return false;
        }

        if(!negative_feed_edt.getText().toString().trim().equals(""))
        {
            neg_feed = negative_feed_edt.getText().toString().trim();
        }
        else {
            return false;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if(!positive_uploading&&!negative_uploading)
        {
            setResult(RESULT_CANCELED);
            finish();
        }else
        {
            Toast.makeText(this, "Please wait a moment till the files are finished uploading",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            String path;

            switch(requestCode) {

                case REQUEST_POSITIVE_FILE:
                    Toast.makeText(context,"Uploading file",Toast.LENGTH_SHORT).show();
                    path = "media/"+story_id+"/" + chapter_number + "/positive/";
                    uploadFile(data, path, REQUEST_POSITIVE_FILE);
                    break;

                case REQUEST_NEGATIVE_FILE:
                    Toast.makeText(context,"Uploading file",Toast.LENGTH_SHORT).show();
                    path = "media/"+story_id+"/" + chapter_number + "/negative/";
                    uploadFile(data, path, REQUEST_NEGATIVE_FILE);
                    break;
            }
        }
        else {
            switch (requestCode) {
                case REQUEST_POSITIVE_FILE:
                    Toast.makeText(context,"No file chosen",Toast.LENGTH_SHORT).show();
                    positive_uploading = false;
                    break;
                case REQUEST_NEGATIVE_FILE:
                    Toast.makeText(context,"No file chosen",Toast.LENGTH_SHORT).show();
                    negative_uploading = false;
                    break;
            }
        }
    }

    private void uploadFile(Intent data, final String path, final int label) {
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
                    params.put("file", myFile, RequestParams.APPLICATION_OCTET_STREAM);
                    params.put("path",path);
                    params.setContentEncoding("UTF-8");
                    Log.d("Path", path);
                    Log.d("File", selectedFile.getName());
                } catch(FileNotFoundException e) {}
                Log.d("params",params.toString());
                // send request
                AsyncHttpClient client = new AsyncHttpClient();
                client.post(
                        "http://memoryhelper.netne.net/interactivestory/index.php/fileupload/upload_file",
                        params,
                        new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] bytes) {
                                Log.d("status",statusCode+"");
                                Log.d("bytes",new String(bytes));
                                uploadResult(true, label, "http://memoryhelper.netne.net/interactivestory/" + path + selectedFile.getName());
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] bytes, Throwable throwable) {
                                uploadResult(false,label,"");
                            }
                        });
            } else {
                uploadResult(false,label,"");
                //Toast.makeText(getApplicationContext(), "file not found", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void uploadResult(boolean result, int label, String path) {
        switch (label) {
            case REQUEST_POSITIVE_FILE:
                if (result) {
                    positive_uploading = false;
                    old_positive = true;
                    positive_url = path;
                    Toast.makeText(getApplicationContext(), "Positive audio file uploaded.", Toast.LENGTH_SHORT).show();
                } else {
                    positive_uploading = false;
                    Toast.makeText(getApplicationContext(), "Error uploading positive audio file. Please try again.", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_NEGATIVE_FILE:
                if (result) {
                    old_negative = true;
                    negative_uploading = false;
                    negative_url = path;
                    Toast.makeText(getApplicationContext(), "Negative audio file uploaded.", Toast.LENGTH_SHORT).show();
                } else {
                    negative_uploading = false;
                    Toast.makeText(getApplicationContext(), "Error uploading negative audio file. Please try again.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

}

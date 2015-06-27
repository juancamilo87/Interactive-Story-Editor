package fi.oulu.interactivestoryeditor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
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

import fi.oulu.interactivestoryeditor.model.Chapter;
import fi.oulu.interactivestoryeditor.model.Interaction;
import fi.oulu.interactivestoryeditor.model.NFCInteraction;


public class AddNFCInteraction extends Activity {

    private static final int REQUEST_POSITIVE_FILE = 1;
    private static final int REQUEST_NEGATIVE_FILE = 2;

    private Button save_button;
    private EditText edt_instructions;
    private EditText edt_positive;
    private EditText edt_negative;
    private ImageButton btn_positive;
    private ImageButton btn_negative;
    private EditText edt_secret;
    private Button btn_nfc_tag;
    private Context context;

    private String instructions;
    private String positive_feedback;
    private String negative_feedback;
    private String positive_url;
    private String negative_url;
    private String secret_message;

    private boolean old_positive;
    private boolean old_negative;

    private boolean positive_uploading;
    private boolean negative_uploading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_nfc_interaction);

        context = this;

        save_button = (Button) findViewById(R.id.nfc_save_btn);
        edt_instructions = (EditText) findViewById(R.id.edt_instructions);
        edt_positive = (EditText) findViewById(R.id.edt_positive_feedback);
        edt_negative = (EditText) findViewById(R.id.edt_negative_feedback);
        btn_positive = (ImageButton) findViewById(R.id.nfc_btn_positive);
        btn_negative = (ImageButton) findViewById(R.id.nfc_btn_negative);
        edt_secret = (EditText) findViewById(R.id.nfc_input);
        btn_nfc_tag = (Button) findViewById(R.id.nfc_tag_btn);

        old_positive = true;
        old_negative = true;

        positive_uploading = false;
        negative_uploading = false;

        if(getIntent().getSerializableExtra("old_interaction")!= null)
        {
            NFCInteraction interaction = (NFCInteraction) getIntent().getSerializableExtra("old_interaction");

            edt_instructions.setText(interaction.getInstructions());
            edt_positive.setText(interaction.getPositiveTextFeedback());
            edt_negative.setText(interaction.getNegativeTextFeedback());
            positive_url = interaction.getPositiveAudioFeedbackUrl();
            negative_url = interaction.getNegativeAudioFeedbackUrl();
            edt_secret.setText(interaction.getSecretCode());

        }

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(verifyFields())
                {
                    NFCInteraction interaction = new NFCInteraction(
                            Interaction.NFC_INTERACTION,
                            instructions,
                            positive_feedback,
                            negative_feedback,
                            positive_url,
                            negative_url,
                            secret_message);

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("interaction", (Serializable) interaction);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
            }
        });

        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(positive_url!=null && !positive_url.equals("") && old_positive)
                {
                    Toast.makeText(context,"A file is already associated, if you want to change it please click again.", Toast.LENGTH_SHORT).show();
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
                if(negative_url!=null && !negative_url.equals("") && old_negative)
                {
                    Toast.makeText(context,"A file is already associated, if you want to change it please click again.", Toast.LENGTH_SHORT).show();
                    old_negative = false;
                }
                else
                {
                    negative_uploading = true;
                    Intent intent = new Intent(getApplicationContext(), FilePicker.class);
                    startActivityForResult(intent, REQUEST_NEGATIVE_FILE);
                }

            }
        });

        btn_nfc_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Write nfc tag, start a listener for tags, try to write the secret_message and remove the listener
                //TODO: Limit lines of edt
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {

            switch(requestCode) {

                case REQUEST_POSITIVE_FILE:
                    Toast.makeText(context,"Uploading file",Toast.LENGTH_SHORT).show();
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
                                    positive_uploading = false;
                                    old_positive = true;
                                    positive_url = "http://memoryhelper.netne.net/fileupload/" + selectedFile.getPath();
                                    Toast.makeText(getApplicationContext(), "Positive audio file uploaded.", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] bytes, Throwable throwable) {
                                    positive_uploading = false;
                                    Toast.makeText(getApplicationContext(), "Error uploading positive audio file. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            //Toast.makeText(getApplicationContext(), "file not found", Toast.LENGTH_LONG).show();
                        }
                    }
                    break;

                case REQUEST_NEGATIVE_FILE:
                    Toast.makeText(context,"Uploading file",Toast.LENGTH_SHORT).show();
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
                                    old_negative = true;
                                    negative_uploading = false;
                                    negative_url = "http://memoryhelper.netne.net/fileupload/" + selectedFile.getPath();
                                    Toast.makeText(getApplicationContext(), "Negative audio file uploaded.", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] bytes, Throwable throwable) {
                                    negative_uploading = false;
                                    Toast.makeText(getApplicationContext(), "Error uploading negative audio file. Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            //Toast.makeText(getApplicationContext(), "file not found", Toast.LENGTH_LONG).show();
                        }
                    }
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

    private boolean verifyFields() {
        if(!edt_instructions.getText().toString().trim().equals(""))
        {
            instructions = edt_instructions.getText().toString().trim();
        }else
        {
            Toast.makeText(context,"Please input some instructions",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!edt_positive.getText().toString().trim().equals(""))
        {
            positive_feedback = edt_positive.getText().toString().trim();
        }else
        {
            Toast.makeText(context,"Please input some positive feedback",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!edt_negative.getText().toString().trim().equals(""))
        {
            negative_feedback = edt_negative.getText().toString().trim();
        }else
        {
            Toast.makeText(context,"Please input some negative feedback",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!edt_secret.getText().toString().trim().equals(""))
        {
            secret_message = edt_secret.getText().toString().trim();
        }else
        {
            Toast.makeText(context,"Please input the secret message",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}

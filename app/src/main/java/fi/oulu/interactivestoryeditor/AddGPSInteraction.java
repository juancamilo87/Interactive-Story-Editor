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
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;

import fi.oulu.interactivestoryeditor.model.GPSInteraction;


public class AddGPSInteraction extends Activity{

    private static final int REQUEST_GPS_LOCATION = 1;

    private TextView tv_latitude;
    private TextView tv_longitude;

    private double latitude;
    private double longitude;

    private Button pick_location_btn;

    private boolean old_interaction;

    private static final int REQUEST_POSITIVE_FILE = 2;
    private static final int REQUEST_NEGATIVE_FILE = 3;

    private Button save_button;
    private EditText edt_instructions;
    private EditText edt_positive;
    private EditText edt_negative;
    private ImageButton btn_positive;
    private ImageButton btn_negative;

    private Context context;

    private long story_id;
    private int chapter_number;
    private long interaction_id;

    private String instructions;
    private String positive_feedback;
    private String negative_feedback;
    private String positive_url;
    private String negative_url;

    private boolean old_positive;
    private boolean old_negative;

    private boolean positive_uploading;
    private boolean negative_uploading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gps_interaction);
        old_interaction = false;
        tv_latitude = (TextView)  findViewById(R.id.tv_latitude);
        tv_longitude = (TextView)  findViewById(R.id.tv_longitude);
        pick_location_btn = (Button) findViewById(R.id.gps_pick_location);

        interaction_id = -1;
        story_id = getIntent().getLongExtra("story_id",-1);
        chapter_number = getIntent().getIntExtra("chapter_number",-1);

        context = this;

        save_button = (Button) findViewById(R.id.gps_save_btn);
        edt_instructions = (EditText) findViewById(R.id.edt_instructions);
        edt_positive = (EditText) findViewById(R.id.edt_positive_feedback);
        edt_negative = (EditText) findViewById(R.id.edt_negative_feedback);
        btn_positive = (ImageButton) findViewById(R.id.gps_btn_positive);
        btn_negative = (ImageButton) findViewById(R.id.gps_btn_negative);

        if(getIntent().getSerializableExtra("old_interaction")!= null)
        {
            GPSInteraction interaction = (GPSInteraction) getIntent().getSerializableExtra("old_interaction");

            edt_instructions.setText(interaction.getInstructions());
            edt_positive.setText(interaction.getPositiveTextFeedback());
            edt_negative.setText(interaction.getNegativeTextFeedback());
            positive_url = interaction.getPositiveAudioFeedbackUrl();
            negative_url = interaction.getNegativeAudioFeedbackUrl();
            latitude = interaction.getLatitude();
            longitude = interaction.getLongitude();
            interaction_id = interaction.getInteraction_id();

            tv_latitude.setText((double) Math.round(latitude * 100000) / 100000 + "");
            tv_longitude.setText((double) Math.round(longitude * 100000) / 100000 + "");

            old_interaction = true;
        }

        pick_location_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PickGPSLocation.class);
                intent.putExtra("location",old_interaction);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                startActivityForResult(intent, REQUEST_GPS_LOCATION);
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(positive_uploading||negative_uploading)
                {
                    Toast.makeText(getApplicationContext(),"Please wait till the files finish uploading",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(verifyFields())
                    {
                        GPSInteraction interaction;
                        if(interaction_id != -1)
                        {
                            interaction = new GPSInteraction(
                                    instructions,
                                    positive_feedback,
                                    negative_feedback,
                                    positive_url,
                                    negative_url,
                                    (float)latitude,
                                    (float)longitude,
                                    interaction_id);
                        }
                        else
                        {
                            interaction = new GPSInteraction(
                                    instructions,
                                    positive_feedback,
                                    negative_feedback,
                                    positive_url,
                                    negative_url,
                                    (float)latitude,
                                    (float)longitude);
                        }

                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("interaction", (Serializable) interaction);
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
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
                } else
                {
                    negative_uploading = true;
                    Intent intent = new Intent(getApplicationContext(), FilePicker.class);
                    startActivityForResult(intent, REQUEST_NEGATIVE_FILE);
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            String path;

            switch(requestCode) {

                case REQUEST_POSITIVE_FILE:
                    Toast.makeText(context, "Uploading file", Toast.LENGTH_SHORT).show();
                    path = "media/"+story_id+"/" + chapter_number + "/positive/";
                    uploadFile(data, path, REQUEST_POSITIVE_FILE);
                    break;

                case REQUEST_NEGATIVE_FILE:
                    Toast.makeText(context,"Uploading file",Toast.LENGTH_SHORT).show();
                    path = "media/"+story_id+"/" + chapter_number + "/negative/";
                    uploadFile(data, path, REQUEST_NEGATIVE_FILE);
                    break;

                case REQUEST_GPS_LOCATION:
                    latitude = data.getDoubleExtra("latitude",0);
                    longitude = data.getDoubleExtra("longitude",0);
                    tv_latitude.setText((double) Math.round(latitude * 100000) / 100000 + "");
                    tv_longitude.setText((double) Math.round(longitude * 100000) / 100000 + "");
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
                case REQUEST_GPS_LOCATION:
                    Toast.makeText(context,"No location chosen",Toast.LENGTH_SHORT).show();
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

        if(latitude==0||longitude==0)
        {
            Toast.makeText(context,"Please input the secret message",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}

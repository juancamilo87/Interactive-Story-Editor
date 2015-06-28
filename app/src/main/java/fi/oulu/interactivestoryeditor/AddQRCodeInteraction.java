package fi.oulu.interactivestoryeditor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.Header;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;

import fi.oulu.interactivestoryeditor.model.QRCodeInteraction;


public class AddQRCodeInteraction extends Activity implements OnClickListener{

    private static final int SELECTED_PICTURE=1;
    private static final int REQUEST_POSITIVE_FILE = 2;
    private static final int REQUEST_NEGATIVE_FILE = 3;

    private String LOG_TAG = "GenerateQRCode";

    private Bitmap bitmap;

    private EditText edt_instructions;
    private EditText edt_positive;
    private EditText edt_negative;
    private ImageButton btn_positive;
    private ImageButton btn_negative;
    private Context context;
    private long story_id;
    private int chapter_number;

    private String instructions;
    private String positive_feedback;
    private String negative_feedback;
    private String positive_url;
    private String negative_url;

    private boolean old_positive;
    private boolean old_negative;

    private boolean positive_uploading;
    private boolean negative_uploading;

    //Unique to interaction
    private String qr_message;
    private EditText qrInput;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_qr_code_interaction);

        story_id = getIntent().getLongExtra("story_id", -1);
        chapter_number = getIntent().getIntExtra("chapter_number", -1);

        context = this;

        edt_instructions = (EditText) findViewById(R.id.edt_instructions);
        edt_positive = (EditText) findViewById(R.id.edt_positive_feedback);
        edt_negative = (EditText) findViewById(R.id.edt_negative_feedback);
        btn_positive = (ImageButton) findViewById(R.id.nfc_btn_positive);
        btn_negative = (ImageButton) findViewById(R.id.nfc_btn_negative);
        qrInput = (EditText) findViewById(R.id.qrInput);

        old_positive = true;
        old_negative = true;

        positive_uploading = false;
        negative_uploading = false;

        if (getIntent().getSerializableExtra("old_interaction") != null) {
            QRCodeInteraction interaction = (QRCodeInteraction) getIntent().getSerializableExtra("old_interaction");

            edt_instructions.setText(interaction.getInstructions());
            edt_positive.setText(interaction.getPositiveTextFeedback());
            edt_negative.setText(interaction.getNegativeTextFeedback());
            positive_url = interaction.getPositiveAudioFeedbackUrl();
            negative_url = interaction.getNegativeAudioFeedbackUrl();
            qrInput.setText(interaction.getSecretCode());

        }

        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positive_url != null && !positive_url.equals("") && old_positive) {
                    Toast.makeText(context, "A file is already associated, if you want to change it please click again.", Toast.LENGTH_SHORT).show();
                    old_positive = false;
                } else {
                    positive_uploading = true;
                    Intent intent = new Intent(getApplicationContext(), FilePicker.class);
                    startActivityForResult(intent, REQUEST_POSITIVE_FILE);
                }

            }
        });

        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (negative_url != null && !negative_url.equals("") && old_negative) {
                    Toast.makeText(context, "A file is already associated, if you want to change it please click again.", Toast.LENGTH_SHORT).show();
                    old_negative = false;
                } else {
                    negative_uploading = true;
                    Intent intent = new Intent(getApplicationContext(), FilePicker.class);
                    startActivityForResult(intent, REQUEST_NEGATIVE_FILE);
                }

            }
        });


        Button button1 = (Button) findViewById(R.id.qrcode);
        Button share_button = (Button) findViewById(R.id.qr_share);
        Button save_button = (Button) findViewById(R.id.qr_save);
        button1.setOnClickListener(this);
        share_button.setOnClickListener(this);
        save_button.setOnClickListener(this);
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.qrcode:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(qrInput, InputMethodManager.SHOW_IMPLICIT);
                String qrInputText = qrInput.getText().toString();
                Log.v(LOG_TAG, qrInputText);

                //Find screen size
                WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
                Display display = manager.getDefaultDisplay();
                Point point = new Point();
                display.getSize(point);
                int width = point.x;
                int height = point.y;
                int smallerDimension = width < height ? width : height;
                smallerDimension = smallerDimension * 3/4;

                //Encode with a QR Code image
                QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrInputText,
                        null,
                        Contents.Type.TEXT,
                        BarcodeFormat.QR_CODE.toString(),
                        smallerDimension);
                try {
                    bitmap = qrCodeEncoder.encodeAsBitmap();
                    ImageView myImage = (ImageView) findViewById(R.id.qrimage);
                    myImage.setOnClickListener(this);
                    myImage.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                break;

            // Share QR code
            case R.id.qr_share:
                String pathofBmp = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,"QRcode", "QRCode");
                Uri bmpUri = Uri.parse(pathofBmp);
                final Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                shareIntent.setType("image/png");
                startActivity(shareIntent);
                break;

            // Save QR code to local db
            case R.id.qr_save:
                if(verifyFields())
                {
                    QRCodeInteraction interaction = new QRCodeInteraction(
                            instructions,
                            positive_feedback,
                            negative_feedback,
                            positive_url,
                            negative_url,
                            qr_message);

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("interaction", (Serializable) interaction);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }
                break;
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        String path;
        switch (requestCode) {
            case SELECTED_PICTURE:
                if(resultCode==RESULT_OK){
                    Uri uri=data.getData();
                    Intent intentSend = new Intent(Intent.ACTION_SEND);
                    intentSend.setType("image/*");
                    intentSend.putExtra(Intent.EXTRA_STREAM, uri);
                    Intent chooser = Intent.createChooser(intentSend, "Send qr");
                    startActivity(chooser);
                }
                break;
            case REQUEST_POSITIVE_FILE:
                if(resultCode==RESULT_OK) {
                    Toast.makeText(context, "Uploading file", Toast.LENGTH_SHORT).show();
                    path = "media/" + story_id + "/" + chapter_number + "/positive/";
                    uploadFile(data, path, REQUEST_POSITIVE_FILE);
                }
                else
                {
                    Toast.makeText(context,"No file chosen",Toast.LENGTH_SHORT).show();
                    positive_uploading = false;
                }
                break;

            case REQUEST_NEGATIVE_FILE:
                if(resultCode==RESULT_OK){
                    Toast.makeText(context,"Uploading file",Toast.LENGTH_SHORT).show();
                    path = "media/"+story_id+"/" + chapter_number + "/negative/";
                    uploadFile(data, path, REQUEST_NEGATIVE_FILE);
                }else
                {
                    Toast.makeText(context,"No file chosen",Toast.LENGTH_SHORT).show();
                    negative_uploading = false;
                }
                break;

            default:
                break;
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

        if(!qrInput.getText().toString().trim().equals(""))
        {
            qr_message = qrInput.getText().toString().trim();
        }else
        {
            Toast.makeText(context,"Please input the secret message",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

}

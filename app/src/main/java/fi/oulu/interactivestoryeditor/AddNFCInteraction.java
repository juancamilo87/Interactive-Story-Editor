package fi.oulu.interactivestoryeditor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;

import fi.oulu.interactivestoryeditor.model.Chapter;
import fi.oulu.interactivestoryeditor.model.GPSInteraction;
import fi.oulu.interactivestoryeditor.model.Interaction;
import fi.oulu.interactivestoryeditor.model.NFCInteraction;
import fi.oulu.interactivestoryeditor.model.QRCodeInteraction;
import fi.oulu.interactivestoryeditor.model.QuizInteraction;
import fi.oulu.interactivestoryeditor.model.SpellCheckInteraction;


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

    private long story_id;
    private int chapter_number;

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

    private boolean changing_settings;

    private NfcAdapter mNfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_nfc_interaction);

        story_id = getIntent().getLongExtra("story_id",-1);
        chapter_number = getIntent().getIntExtra("chapter_number",-1);

        context = this;

        save_button = (Button) findViewById(R.id.nfc_save_btn);
        edt_instructions = (EditText) findViewById(R.id.edt_instructions);
        edt_positive = (EditText) findViewById(R.id.edt_positive_feedback);
        edt_negative = (EditText) findViewById(R.id.edt_negative_feedback);
        btn_positive = (ImageButton) findViewById(R.id.nfc_btn_positive);
        btn_negative = (ImageButton) findViewById(R.id.nfc_btn_negative);
        edt_secret = (EditText) findViewById(R.id.nfc_input);
        btn_nfc_tag = (Button) findViewById(R.id.nfc_tag_btn);


        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(mNfcAdapter==null)
        {
            btn_nfc_tag.setEnabled(false);
            Toast.makeText(context,"No NFC adapter. You can edit the interaction but not write the tag",Toast.LENGTH_SHORT).show();
        }

        old_positive = true;
        old_negative = true;

        positive_uploading = false;
        negative_uploading = false;

        changing_settings = false;

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
                } else
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
                if(verifyFields())
                {
                    if (!mNfcAdapter.isEnabled()) {
                        //Enable NFC
                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                                AddNFCInteraction.this);
                        LayoutInflater inflater = getLayoutInflater();
                        builderSingle.setCustomTitle(inflater.inflate(R.layout.nfc_dialog_title, null));
                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                AddNFCInteraction.this,
                                R.layout.list_cell);

                        arrayAdapter.add("Change NFC Settings");
                        arrayAdapter.add("Cancel");

                        builderSingle.setAdapter(arrayAdapter,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String strName = arrayAdapter.getItem(which);

                                        if (strName.equals("Change NFC Settings")) {
                                            changing_settings = true;
                                            Intent setnfc = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                                            startActivity(setnfc);
                                            dialog.dismiss();
                                        } else {
                                            Toast.makeText(context,"You have to enable NFC to be able to write the tag",Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    }
                                });
                        builderSingle.show();
                    }
                    else
                    {
                        btn_nfc_tag.setText("Waiting for tag...");
                        Intent nfcIntent = new Intent(context, AddNFCInteraction.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        nfcIntent.putExtra("nfcMessage", secret_message);
                        PendingIntent pi = PendingIntent.getActivity(context, 0, nfcIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);

                        mNfcAdapter.enableForegroundDispatch((Activity)context, pi, new IntentFilter[] {tagDetected}, null);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(changing_settings)
        {
            changing_settings = !changing_settings;
            if (!mNfcAdapter.isEnabled()) {
                btn_nfc_tag.setText("Waiting for tag...");
                Intent nfcIntent = new Intent(context, AddNFCInteraction.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                nfcIntent.putExtra("nfcMessage", secret_message);
                PendingIntent pi = PendingIntent.getActivity(context, 0, nfcIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);

                mNfcAdapter.enableForegroundDispatch((Activity)context, pi, new IntentFilter[] {tagDetected}, null);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {

        if(NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            // When an NFC tag is being written, call the write tag function when an intent is
            // received that says the tag is within range of the device and ready to be written to
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String nfcMessage = intent.getStringExtra("nfcMessage");

            if(nfcMessage != null) {
                btn_nfc_tag.setText("Writing NFC tag...");
                AddNFCInteraction.writeTag(this, tag, nfcMessage);
            }
            btn_nfc_tag.setText("Write NFC tag");
        }
        else
        {
            super.onNewIntent(intent);
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

    private void uploadFile(Intent data, String path, final int label) {
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
                    params.put("url",path);
                } catch(FileNotFoundException e) {}

                // send request
                AsyncHttpClient client = new AsyncHttpClient();
                client.post("http://memoryhelper.netne.net/fileupload/upload.php", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] bytes) {
                        uploadResult(true, label, "http://memoryhelper.netne.net/fileupload/" + selectedFile.getPath());
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

    public static boolean writeTag(Context context, Tag tag, String data) {
        // Record to launch Play Store if app is not installed
        NdefRecord appRecord = NdefRecord.createApplicationRecord("fi.oulu.story");

        // Record with actual data we care about
        NdefRecord relayRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                new String("text/is")
                        .getBytes(Charset.forName("US-ASCII")),
                null, data.getBytes());

        // Complete NDEF message with both records
        NdefMessage message = new NdefMessage(new NdefRecord[] {relayRecord, appRecord});

        try {
            // If the tag is already formatted, just write the message to it
            Ndef ndef = Ndef.get(tag);
            if(ndef != null) {
                ndef.connect();

                // Make sure the tag is writable
                if(!ndef.isWritable()) {
                    Toast.makeText(context,"The tag is not writable",Toast.LENGTH_SHORT).show();
                    return false;
                }

                // Check if there's enough space on the tag for the message
                int size = message.toByteArray().length;
                if(ndef.getMaxSize() < size) {
                    Toast.makeText(context,"The message is to big for the tag",Toast.LENGTH_SHORT).show();
                    return false;
                }
                try {
                    // Write the data to the tag
                    ndef.writeNdefMessage(message);

                    Toast.makeText(context,"The secret message was written",Toast.LENGTH_SHORT).show();
                    return true;
                } catch (TagLostException tle) {
                    Toast.makeText(context,"The tag was lost or pulled away while writing to it",Toast.LENGTH_SHORT).show();
                    return false;
                } catch (IOException ioe) {
                    Toast.makeText(context,"There was an error formatting the NFC tag",Toast.LENGTH_SHORT).show();
                    return false;
                } catch (FormatException fe) {
                    Toast.makeText(context,"There was an error formatting the NFC tag",Toast.LENGTH_SHORT).show();
                    return false;
                }
                // If the tag is not formatted, format it with the message
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if(format != null) {
                    try {
                        format.connect();
                        format.format(message);

                        Toast.makeText(context,"The secret message was written",Toast.LENGTH_SHORT).show();
                        return true;
                    } catch (TagLostException tle) {
                        Toast.makeText(context,"The tag was lost or pulled away while writing to it",Toast.LENGTH_SHORT).show();
                        return false;
                    } catch (IOException ioe) {
                        Toast.makeText(context,"There was an error formatting the NFC tag",Toast.LENGTH_SHORT).show();
                        return false;
                    } catch (FormatException fe) {
                        Toast.makeText(context,"There was an error formatting the NFC tag",Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else {
                    Toast.makeText(context,"The NFC tag does not support NDEF format",Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        } catch(Exception e) {
            Toast.makeText(context,"An unknown error occurred while writing to the NFC tag",Toast.LENGTH_SHORT).show();
        }

        return false;
    }
}

package fi.oulu.interactivestoryeditor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.Serializable;

import fi.oulu.interactivestoryeditor.model.QuizInteraction;
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
    private String pos_feed_url;
    private String neg_feed_url;
    private int interaction_type;

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

        word_edt = (EditText) findViewById(R.id.spell_check_word_edt);
        instruct_edt = (EditText) findViewById(R.id.spell_check_instructions_edt);
        positive_feed_edt = (EditText) findViewById(R.id.spell_check_positive_feed_edt);
        negative_feed_edt = (EditText) findViewById(R.id.spell_check_negative_feed_edt);
        btn_positive= (ImageButton) findViewById(R.id.spell_check_btn_positive);
        btn_negative = (ImageButton) findViewById(R.id.spell_check_btn_positive);
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
            pos_feed_url = spellCheckInteraction.getPositiveAudioFeedbackUrl();
            neg_feed_url = spellCheckInteraction.getNegativeAudioFeedbackUrl();
            interaction_type = spellCheckInteraction.getInteractionType();
        }

        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pos_feed_url!=null && !pos_feed_url.equals("") && old_positive)
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
                if(neg_feed_url!=null && !neg_feed_url.equals("") && old_negative)
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
                if (verifyFields()) {
                    SpellCheckInteraction spCheckInt = new SpellCheckInteraction(instruct, pos_feed, neg_feed, pos_feed_url, neg_feed_url, word);

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("quiz interaction", (Serializable) spCheckInt);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Please fill in all the fields", Toast.LENGTH_SHORT).show();
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

}

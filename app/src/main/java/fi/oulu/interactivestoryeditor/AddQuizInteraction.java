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

import fi.oulu.interactivestoryeditor.model.Author;
import fi.oulu.interactivestoryeditor.model.Interaction;
import fi.oulu.interactivestoryeditor.model.QuizInteraction;
import fi.oulu.interactivestoryeditor.model.Story;


public class AddQuizInteraction extends Activity {

    private static final int REQUEST_POSITIVE_FILE = 1;
    private static final int REQUEST_NEGATIVE_FILE = 2;

    private EditText question_edt;
    private EditText ans1_edt;
    private EditText ans2_edt;
    private EditText ans3_edt;
    private EditText ans4_edt;
    private EditText correct_edt;
    private EditText instruct_edt;
    private EditText positive_feed_edt;
    private EditText negative_feed_edt;
    private Button btn_save;
    private ImageButton btn_positive;
    private ImageButton btn_negative;
    private Context context;

    private String question;
    private String ans1;
    private String ans2;
    private String ans3;
    private String ans4;
    private String correct_ans;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quiz_interaction);

        question_edt = (EditText) findViewById(R.id.question_edt);
        ans1_edt = (EditText) findViewById(R.id.answer1_edt);
        ans2_edt = (EditText) findViewById(R.id.answer2_edt);
        ans3_edt = (EditText) findViewById(R.id.answer3_edt);
        ans4_edt = (EditText) findViewById(R.id.answer4_edt);
        correct_edt = (EditText) findViewById(R.id.correct_ans_edt);
        instruct_edt = (EditText) findViewById(R.id.instructions_edt);
        positive_feed_edt = (EditText) findViewById(R.id.positive_feed_edt);
        negative_feed_edt = (EditText) findViewById(R.id.negative_feed_edt);
        btn_positive= (ImageButton) findViewById(R.id.quiz_btn_positive);
        btn_negative = (ImageButton) findViewById(R.id.quiz_btn_negative);
        btn_save = (Button) findViewById(R.id.quiz_btn_save);

        old_positive = true;
        old_negative = true;

        positive_uploading = false;
        negative_uploading = false;

        if(getIntent().getSerializableExtra("old_interaction")!= null) {

            QuizInteraction quizInteraction = new QuizInteraction();
            question_edt.setText(quizInteraction.getQuestion());
            ans1_edt.setText(quizInteraction.getAnswer1());
            ans2_edt.setText(quizInteraction.getAnswer2());
            ans3_edt.setText(quizInteraction.getAnswer3());
            ans4_edt.setText(quizInteraction.getAnswer4());
            correct_edt.setText(quizInteraction.getCorrectAnswer());
            instruct_edt.setText(quizInteraction.getInstructions());
            positive_feed_edt.setText(quizInteraction.getPositiveTextFeedback());
            negative_feed_edt.setText(quizInteraction.getNegativeTextFeedback());
            pos_feed_url = quizInteraction.getPositiveAudioFeedbackUrl();
            neg_feed_url = quizInteraction.getNegativeAudioFeedbackUrl();
            interaction_type = quizInteraction.getInteractionType();
        }

        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pos_feed_url!=null && !pos_feed_url.equals("") && old_positive)
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
                    QuizInteraction quizInt = new QuizInteraction(instruct, pos_feed, neg_feed, pos_feed_url, neg_feed_url,
                            question, correct_ans, ans1, ans2, ans3, ans4);

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("quiz interaction", (Serializable) quizInt);
                    setResult(RESULT_OK, returnIntent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    // add instructions, positive feedback, negative feedback, positive feedback url, negative feedback url in java and xml files


    private boolean verifyFields()
    {
        if(!question_edt.getText().toString().trim().equals(""))
        {
            question = question_edt.getText().toString().trim();
        }
        else
        {
            return false;
        }

        if(!ans1_edt.getText().toString().trim().equals(""))
        {
            ans1 = ans1_edt.getText().toString().trim();
        }
        else
        {
            return false;
        }

        if(!ans2_edt.getText().toString().trim().equals(""))
        {
            ans2 = ans2_edt.getText().toString().trim();
        }
        else
        {
            return false;
        }

        if(!ans3_edt.getText().toString().trim().equals(""))
        {
            ans3 = ans3_edt.getText().toString().trim();
        }
        else
        {
            return false;
        }

        if(!ans4_edt.getText().toString().trim().equals(""))
        {
            ans4 = ans4_edt.getText().toString().trim();
        }
        else
        {
            return false;
        }

        if(!correct_edt.getText().toString().trim().equals(""))
        {
            correct_ans = correct_edt.getText().toString().trim();
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

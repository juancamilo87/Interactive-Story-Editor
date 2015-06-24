package fi.oulu.interactivestoryeditor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Serializable;

import fi.oulu.interactivestoryeditor.model.Author;
import fi.oulu.interactivestoryeditor.model.QuizInteraction;
import fi.oulu.interactivestoryeditor.model.Story;


public class AddQuizInteraction extends Activity {

    private QuizInteraction quizInteraction;
    private EditText question_edt;
    private EditText ans1_edt;
    private EditText ans2_edt;
    private EditText ans3_edt;
    private EditText ans4_edt;
    private EditText correct_edt;
    private EditText instruct_edt;
    private EditText positive_feed_edt;
    private EditText negative_feed_edt;
    private EditText pos_url_feed_edt;
    private EditText neg_url_feed_edt;
    private Button btn_save;

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
        pos_url_feed_edt= (EditText) findViewById(R.id.positive_feed_url_edt);
        neg_url_feed_edt = (EditText) findViewById(R.id.negative_feed_url_edt);
        btn_save = (Button) findViewById(R.id.quiz_btn_save);

        question = question_edt.getText().toString();
        ans1 = ans1_edt.getText().toString();
        ans2 = ans2_edt.getText().toString();
        ans3 = ans3_edt.getText().toString();
        ans4 = ans4_edt.getText().toString();
        correct_ans = correct_edt.getText().toString();
        instruct = instruct_edt.getText().toString();
        pos_feed = positive_feed_edt.getText().toString();
        neg_feed = negative_feed_edt.getText().toString();
        pos_feed_url = pos_url_feed_edt.getText().toString();
        neg_feed_url = neg_url_feed_edt.getText().toString();

        btn_save.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (verifyFields()) {
                    quizInteraction = new QuizInteraction();
                    quizInteraction.setQuestion(question);
                    quizInteraction.setAnswer1(ans1);
                    quizInteraction.setAnswer2(ans2);
                    quizInteraction.setAnswer3(ans3);
                    quizInteraction.setAnswer4(ans4);
                    quizInteraction.setCorrectAnswer(correct_ans);
                    quizInteraction.setInstructions(instruct);
                    quizInteraction.setNegativeTextFeedback(neg_feed);
                    quizInteraction.setPositiveTextFeedback(pos_feed);
                    quizInteraction.setNegativeAudioFeedbackUrl(neg_feed_url);
                    quizInteraction.setPositiveAudioFeedbackUrl(pos_feed_url);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("quiz interaction", (Serializable) quizInteraction);
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
        else
        {
            return false;
        }

        if(!neg_url_feed_edt.getText().toString().trim().equals(""))
        {
            neg_feed_url = neg_url_feed_edt.getText().toString().trim();
        }
        else
        {
            return false;
        }

        if(!pos_url_feed_edt.getText().toString().trim().equals(""))
        {
            pos_feed_url = pos_url_feed_edt.getText().toString().trim();
        }
        else
        {
            return false;
        }

        return true;
    }

}

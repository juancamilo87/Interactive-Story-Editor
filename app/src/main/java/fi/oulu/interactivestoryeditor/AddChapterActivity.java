package fi.oulu.interactivestoryeditor;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import fi.oulu.interactivestoryeditor.model.Chapter;
import fi.oulu.interactivestoryeditor.model.Interaction;


public class AddChapterActivity extends Activity {

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

    private Interaction interaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chapter);

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

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    setResult(RESULT_CANCELED);
                    finish();
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
                //TODO: button to select image or enter URL taking into account possible values exist before
                if(image_url != null && !image_url.equals(""))
                {
                    //Previous value exists
                }
                else
                {
                    //No previous value exists
                }
            }
        });

        btn_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: button to select video or enter URL taking into account possible values exist before
                if(video_url != null && !video_url.equals(""))
                {
                    //Previous value exists
                }
                else
                {
                    //No previous value exists
                }
            }
        });

        btn_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: button to select audio or enter URL taking into account possible values exist before
                if(audio_url != null && !audio_url.equals(""))
                {
                    //Previous value exists
                }
                else
                {
                    //No previous value exists
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
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }


}

package fi.oulu.interactivestoryeditor;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import fi.oulu.interactivestoryeditor.model.Story;
import fi.oulu.interactivestoryeditor.database.StoriesDataSource;


public class MainMenuActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main_menu);
        setContentView(R.layout.activity_show_story_list);
        Log.d("SP", "Test log works?");
        //get_story_list();
        showPopup(MainMenuActivity.this);
    }

    private void showPopup(final Activity context) {
        RelativeLayout viewGroup = (RelativeLayout) context.findViewById(R.id.no_story_popup);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = layoutInflater.inflate(R.layout.popup_no_stories, viewGroup);
        Button popup_ok_btn = (Button) layout.findViewById(R.id.popup_btn_ok);
        // Create the popup window
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(600);
        popup.setHeight(600);
        popup.setFocusable(true);

        // Displaying the popup centralized
        //To avoid BadTokenException, you need to defer showing the popup until after all the lifecycle methods are called (-> activity window is displayed):
        layout.post(new Runnable() {
            public void run() {
                popup.showAtLocation(layout, Gravity.CENTER, 0, 0);
            }
        });

        popup_ok_btn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                popup.dismiss();
            }
        });
    }

    //Some errors, maybe incorrect usage of getAllStories function, or lack of database in emulator.
    //Get list of story using database functions, show the stories or tip if none
    public void get_story_list() {
        StoriesDataSource sds = new StoriesDataSource(this);
        List<Story> story_list = new ArrayList<Story>();
        story_list = sds.getAllStories();
        Log.d("SP", story_list.toString());
    }

}

package fi.oulu.interactivestoryeditor;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class AddQRCodeInteraction extends Activity implements OnClickListener{

    private String LOG_TAG = "GenerateQRCode";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_qr_code_interaction);

        Button button1 = (Button) findViewById(R.id.qrcode);
        Button save_button = (Button)findViewById(R.id.qr_btn_save);
        button1.setOnClickListener(this);
        save_button.setOnClickListener(this);

    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.qrcode:
                EditText qrInput = (EditText) findViewById(R.id.qrInput);
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
                    Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
                    ImageView myImage = (ImageView) findViewById(R.id.imageView1);
                    myImage.setImageBitmap(bitmap);

                } catch (WriterException e) {
                    e.printStackTrace();
                }
                break;

            // More buttons go here (if any) ...
            case R.id.qr_btn_save:
                Toast.makeText(getApplicationContext(), "QR code has been saved", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}

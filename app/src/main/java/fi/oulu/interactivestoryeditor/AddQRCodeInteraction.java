package fi.oulu.interactivestoryeditor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.provider.MediaStore;
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
        Button share_button = (Button)findViewById(R.id.qr_share);
        Button save_button = (Button)findViewById(R.id.qr__save);
        button1.setOnClickListener(this);
        share_button.setOnClickListener(this);
        save_button.setOnClickListener(this);
    }
    private static final int SELECTED_PICTURE=1;
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
                    MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "qrcode" , "myqrcode");
                    ImageView myImage = (ImageView) findViewById(R.id.qrimage);
                    myImage.setOnClickListener(this);
                    myImage.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                break;

            // Share QR code
            case R.id.qr_share:
                Intent i=new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, SELECTED_PICTURE);
                break;

            // Save QR code to local db
            case R.id.qr__save:
                Toast.makeText(getApplicationContext(), "The QR code has been save!", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

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

            default:
                break;
        }

    }

}

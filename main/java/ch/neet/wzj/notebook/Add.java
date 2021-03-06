package ch.neet.wzj.notebook;


import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.String;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

public class Add extends AppCompatActivity implements OnClickListener {
    String Title, Content, simpleDate;
    Button ButtonAddCancel, ButtonAddSave, ButtonTakePhoto;
    EditText EditTextAddTitle, EditTextAddContent, EditTextAddAuthor;
    ImageView PhotoHere;
    String Author;
    Bitmap imageBitmap;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        ButtonAddCancel = (Button) findViewById(R.id.ButtonAddCancel);
        ButtonAddSave = (Button) findViewById(R.id.ButtonAddSave);
        ButtonTakePhoto = (Button) findViewById(R.id.ButtonTakePhoto);

        EditTextAddContent = findViewById(R.id.EditTextAddContent);
        EditTextAddTitle = findViewById(R.id.EditTextAddTitle);
        EditTextAddAuthor = findViewById(R.id.EditTextAddAuthor);
        PhotoHere = findViewById(R.id.PhotoHere);

        ButtonAddCancel.setOnClickListener(this);
        ButtonAddSave.setOnClickListener(this);
        ButtonTakePhoto.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        MyDataBaseHelper dbHelper = new MyDataBaseHelper(this, "Note.db", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (v.getId()) {
            case R.id.ButtonAddSave:
                Date date = new Date();
                DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");        //??????????????????
                simpleDate = simpleDateFormat.format(date);
                ContentValues values = new ContentValues();
                Title = String.valueOf(EditTextAddTitle.getText());         //????????????????????????
                Content = String.valueOf(EditTextAddContent.getText());
                Log.d("Title", Title);
                if (Title.length() == 0) {               //????????????????????????
                    Toast.makeText(this, "?????????????????????", Toast.LENGTH_LONG).show();
                } else {
                    values.put("title", Title);
                    values.put("content", Content);
                    values.put("date", simpleDate);


                    if (imageBitmap != null) {
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                        values.put("image", os.toByteArray());
                    }
                    db.insert("Note", null, values);


                    //????????????????????????
                    Add.this.setResult(RESULT_OK, getIntent());
                    Add.this.finish();
                }

//                SharedPreferences.Editor editor = getSharedPreferences("data",
//                        MODE_PRIVATE).edit();
//                String name = sharedPreferences.getString("name",);
                pref = getSharedPreferences("data",MODE_PRIVATE);
                String name = pref.getString("name","");      //??????sharedpreferences??????????????????
                //Log.d("MainActivity","name is " + name);
                EditTextAddAuthor.setText(name);
                break;

            case R.id.ButtonAddCancel:
                Add.this.setResult(RESULT_OK, getIntent());
                Add.this.finish();

                break;

            case R.id.ButtonTakePhoto:
                dispatchTakePictureIntent();
                break;
        }

    }

    static final int REQUEST_IMAGE_CAPTURE = 1;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");

            PhotoHere.setImageBitmap(imageBitmap);
        }
    }


    String currentPhotoPath;
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    static final int REQUEST_TAKE_PHOTO = 1;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = PhotoHere.getWidth();
        int targetH = PhotoHere.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        PhotoHere.setImageBitmap(bitmap);
    }
}



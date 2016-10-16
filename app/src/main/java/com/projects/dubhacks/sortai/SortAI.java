package com.projects.dubhacks.sortai;

import android.app.Dialog;
import android.content.Context;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.PopupWindow;
import android.widget.LinearLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.input.image.ClarifaiImage;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class SortAI extends AppCompatActivity {

    static final String MODEL_ID = "b38d23d52ed745209254769074dd980f";

    private static Bitmap photo = null;

    private byte[] image;

    private int PICK_FROM_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort_ai);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // calling the camera app
         this.dispatchTakePictureIntent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sort_ai, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

//    private File createImageFile() throws IOException {
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        storageDir.mkdirs();
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );
//
//        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
//        return image;
//    }

    private void dispatchTakePictureIntent() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            intent.putExtra("return-data", true);
            startActivityForResult(intent, PICK_FROM_CAMERA);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }



//        //camera stuff
//        Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//
////folder stuff
//        File imagesFolder = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        imagesFolder.mkdirs();
//
//        File image = new File(imagesFolder, "QR_" + timeStamp + ".png");
//        Uri uriSavedImage = Uri.fromFile(image);
//        System.out.println(image.getAbsolutePath());
//        mCurrentPhotoPath = image.getAbsolutePath();
//
//        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
//        startActivityForResult(imageIntent, REQUEST_IMAGE_CAPTURE);





//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(this,
//                        "com.example.android.fileprovider",
//                        photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
//            }
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        else {

            Bundle extras = data.getExtras();

            if (extras != null) {
                ImageView mImageView = (ImageView) findViewById(R.id.picture_saving);
                photo = extras.getParcelable("data");
                mImageView.setImageBitmap(photo);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.PNG, 100, bos);
                image = bos.toByteArray();


                // Client interaction
                Client client = new Client();
                client.predictWithModel(ClarifaiInput.forImage(ClarifaiImage.of(image)), MODEL_ID)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<String>() {
                            @Override
                            public void onCompleted() {


                        }
                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(SortAI.this, "Sorry you suck", Toast.LENGTH_LONG).show();
                                System.out.println(e.getMessage() + " Message -------------------->  HERE");
                            }




                        @Override
                        public void onNext(String sortOutput) {

                            Drawable drawable;
                            Resources r = getApplicationContext().getResources();
                            switch(sortOutput) {
                                        case "compost":
                                            drawable = r.getDrawable(R.drawable.compost_popup);
                                            break;
                                        case "recyclable-paper":
                                            drawable = r.getDrawable(R.drawable.mixedpaper_popup);
                                            break;
                                        case "containers":
                                            drawable = r.getDrawable(R.drawable.recycling_popup);
                                            break;
                                        case "garbage":
                                            drawable = r.getDrawable(R.drawable.garbage_popup);
                                            break;
                                        default:
                                            drawable = r.getDrawable(R.drawable.error);
                                            break;
                                    }

                            Dialog dialog = new Dialog(getApplicationContext());
                            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

                            dialog.setContentView(getLayoutInflater().inflate(R.layout.activity_sort_ai, null)); //todo

                            ImageView imageView = (ImageView) findViewById(R.id.picture_saving);
                            imageView.setBackground(drawable);

//                            RelativeLayout relativeLayout = (RelativeLayout) dialog.findViewById(R.id.);
//                            relativeLayout.setBackground(drawable);


//                            PopupWindow popUpWindow = new PopupWindow(getApplicationContext());
//                            LinearLayout mainLayout = new LinearLayout(getApplicationContext());
//                            popUpWindow.showAtLocation(mainLayout, Gravity.BOTTOM, 10, 10);
//                            popUpWindow.update(50, 50, 320, 90);
//
//
//                            View child = getLayoutInflater().inflate(R.layout.compost_popup, null);
//                            mainLayout.addView(child);


//                            Toast.makeText(SortAI.this, sortOutput, Toast.LENGTH_LONG).show();




                        }

                    });
            }

        }


    }

}

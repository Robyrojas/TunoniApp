package com.synappsis.carlos.apptunoni;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class entregaProductos extends AppCompatActivity {

    private final String ruta_fotos = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Tunoni/";
    private File file = new File(ruta_fotos);
    private Button boton;
    private static String tag="entregaProductos";
    /*VARIABLES DE CAMERA*/
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;
    boolean checkPermission = false;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrega_productos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*codigo foto*/
        boton = (Button) findViewById(R.id.btnFoto);
        file.mkdirs();
        boton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                checkPermission = revisarPermisos();
                if(checkPermission)
                {
                    dispatchTakePictureIntent();
                }
            }
        });
    }

    private boolean revisarPermisos() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Toast.makeText(this, "Es una versión anterior del API 23 " + Build.VERSION.SDK_INT, Toast.LENGTH_LONG).show();
            return true;
        } else {
            int hasWriteContactsPermission = checkSelfPermission(android.Manifest.permission.CAMERA);

            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                        REQUEST_CODE_ASK_PERMISSIONS);
                Toast.makeText(this, "Requiriendo permisos", Toast.LENGTH_LONG).show();
            } else if (hasWriteContactsPermission == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "Permisos ya otorgados ", Toast.LENGTH_LONG).show();
                return true;
            }
            return false;
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("ERROR ", "Error:" + ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,getPackageName()+".fileprovider",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

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
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.e(tag, "URL: "+mCurrentPhotoPath);
        return image;
    }
    private void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {
            //setPic();
            galleryAddPic();
            mCurrentPhotoPath = null;
        }

    }
    private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
        /*int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();*/
        int targetW = 300;
        int targetH = 400;

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        }

		/* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		/* Associate the Bitmap to the ImageView */
        /*mImageView.setImageBitmap(bitmap);
        mVideoUri = null;
        mImageView.setVisibility(View.VISIBLE);
        mVideoView.setVisibility(View.INVISIBLE);*/
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(REQUEST_CODE_ASK_PERMISSIONS == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "OK Permisos otorgados",Toast.LENGTH_LONG).show();
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Permisos denegados", Toast.LENGTH_LONG).show();
            }
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            handleBigCameraPhoto();
        }
    }
    /**
     * Metodo privado que genera un codigo unico segun la hora y fecha del sistema
     * @return photoCode
     * */
    @SuppressLint("SimpleDateFormat")
    private String getCode()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        String date = dateFormat.format(new Date() );
        String photoCode = "pic_" + date;
        return photoCode;
    }
}

package com.synappsis.carlos.apptunoni;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class productos extends AppCompatActivity {
    private final String ruta_fotos = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Tunoni/";
    private File file = new File(ruta_fotos);
    private Button boton;
    private Button dialog;
    private Button aceptar;
    private Button btnestados;
    private ImageView fotoimg;
    private static String tag="Productos";
    List<String> list;
    /*VARIABLES DE CAMERA*/
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;
    boolean checkPermission = false;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1 ;
    private String Folio = "C001-";
    /*Variable firma*/
    String mCurrentSignPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);
        list = new ArrayList<String>();
        Tabla tabla = new Tabla(this, (TableLayout)findViewById(R.id.listTable));
        tabla.agregarCabecera(R.array.cabecera_tabla);
        for(int i = 0; i < 15; i++)
        {
            ArrayList<String> elementos = new ArrayList<String>();
            elementos.add(Integer.toString(i));
            elementos.add("Casilla [" + i + ", 0]");
            elementos.add("Casilla [" + i + ", 1]");
            elementos.add("Casilla [" + i + ", 2]");
            elementos.add("Casilla [" + i + ", 3]");
            tabla.agregarFilaTabla(elementos);
        }
        /*codigo selecion de tablas*/
        /*btnestados = (Button) findViewById(R.id.btnFirma);
        btnestados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder estadoBuilder = new AlertDialog.Builder(productos.this);
                View vistaDialog = getLayoutInflater().inflate(R.layout.dialog_estado,null);
                Button mOK1 = (Button) vistaDialog.findViewById(R.id.btnok_estado);
                Button mCancel1 = (Button) vistaDialog.findViewById(R.id.btnok_estado);
                estadoBuilder.setView(vistaDialog);
                AlertDialog alertdialog = estadoBuilder.create();
                mOK1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(),"Guardado",Toast.LENGTH_SHORT).show();
                    }
                });
                mCancel1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dismiss();
                    }
                });

                alertdialog.show();
            }
        });*/
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
        /*codigo firma*/
        dialog = (Button) findViewById(R.id.btnFirma);
        dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder firmaBuilder = new AlertDialog.Builder(productos.this);
                View vistaFirma = getLayoutInflater().inflate(R.layout.dialog_firma,null);
                final DrawingView mDrawingView=new DrawingView(vistaFirma.getContext());
                LinearLayout mDrawingPad=(LinearLayout) vistaFirma.findViewById(R.id.firma);
                mDrawingPad.addView(mDrawingView);
                Button mFirma = (Button) vistaFirma.findViewById(R.id.btnSave);
                Button mLimpiar = (Button) vistaFirma.findViewById(R.id.bntLimpiar);
                firmaBuilder.setView(vistaFirma);
                final AlertDialog dialog = firmaBuilder.create();
                dialog.show();
                mFirma.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        File image =null;
                        Bitmap imgBitmap = mDrawingView.getBitmap();
                        String imageFirma = Folio;// + timeStamp + "_";
                        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                        try {
                            image = File.createTempFile(
                                    imageFirma,  /* prefix */
                                    ".jpg",         /* suffix */
                                    storageDir      /* directory */
                            );
                            mCurrentSignPath = image.getAbsolutePath();
                            Log.e(tag, "Nombre de la firma: "+mCurrentSignPath.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (image == null) {
                            Log.d(tag,"Error creating media file, check storage permissions: ");// e.getMessage());
                            return;
                        }
                        try {
                            FileOutputStream fos = new FileOutputStream(image);
                            imgBitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                            fos.close();
                        } catch (FileNotFoundException e) {
                            Log.d(tag, "File not found: " + e.getMessage());
                        } catch (IOException e) {
                            Log.d(tag, "Error accessing file: " + e.getMessage());
                        }
                        setSign();
                        list.add(0,"1");
                        Toast.makeText(getApplicationContext(),"Se ha guardado la Firma",Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                mLimpiar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDrawingView.limpiar();
                        mDrawingView.invalidate();
                        Toast.makeText(getApplicationContext(),"Puedes volver a poner la Firma",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        aceptar = (Button) findViewById(R.id.btnGuardar);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!list.isEmpty()){
                    if(list.size()==2){
                        //android.os.Process.killProcess(android.os.Process.myPid()); //using this you can exit from the whole activity  for both Eclipse and Android studio
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(),"Te falta agregar foto y/o firma",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Te falta agregar información",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void setSign(){
        int targetW = 300;
        int targetH = 400;

		/* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentSignPath, bmOptions);
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
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentSignPath, bmOptions);
        fotoimg = (ImageView) findViewById(R.id.imageViewFirma);
		/* Associate the Bitmap to the ImageView */
        fotoimg.setImageBitmap(bitmap);
        //mVideoUri = null;
        fotoimg.setVisibility(View.VISIBLE);
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
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = Folio;// + timeStamp + "_";
        Log.e(tag, "Nombre de la foto: "+imageFileName);
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
            setPic();
            galleryAddPic();
            mCurrentPhotoPath = null;
            list.add(0,"2");
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
        fotoimg = (ImageView) findViewById(R.id.imageViewFoto);
		/* Associate the Bitmap to the ImageView */
        fotoimg.setImageBitmap(bitmap);
        //mVideoUri = null;
        fotoimg.setVisibility(View.VISIBLE);
        //mVideoView.setVisibility(View.INVISIBLE);
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

}

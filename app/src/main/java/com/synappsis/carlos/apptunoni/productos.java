package com.synappsis.carlos.apptunoni;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.biometriaaplicada.identitum.exceptions.GZipException;
import com.synappsis.carlos.apptunoni.entidades.Documentos;
import com.synappsis.carlos.apptunoni.entidades.OperacionesBaseDatos;
import com.synappsis.carlos.apptunoni.entidades.Producto;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.biometriaaplicada.identitum.utils.GZipUtils;

public class productos extends AppCompatActivity {
    private final String ruta_fotos = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Tunoni/";
    private File file = new File(ruta_fotos);
    private Button dialog;
    private Button aceptar;
    private ImageButton img1, img2, img3, fotoFinal;
    private ImageView fotoimg;
    private EditText comentario;
    private static String tag="Productos";
    List<String> list;
    /*VARIABLES DE CAMERA*/
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;
    boolean checkPermission = false;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1 ;
    private String Folio = "C001-";
    private int bandera = 0;
    String[] lista1 = {"Completo","Faltante","No Entregado"};
    String[] lista2 = {"Excelente","Regular","Malo"};
    //String[] listaPRO = {"Zanahorias KG","Papas KG","Tortillas KG","Agua LT","Cereal CAJA"};
    List<Producto> LISTAP = new ArrayList<Producto>();
    TableLayout stk;
    /*Variable firma*/
    String mCurrentSignPath;
    OperacionesBaseDatos datos = null;
    List<String> list64 = new ArrayList<>();
    List<String> list64path = new ArrayList<>();
    static boolean errored = false;
    boolean status = false;
    String folioT ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);
        list = new ArrayList<String>();
        datos = OperacionesBaseDatos
                .obtenerInstancia(getApplicationContext());
        init();
        /*codigo aceptar*/
        comentario = (EditText) findViewById(R.id.comentario);
        aceptar = (Button) findViewById(R.id.btnGuardar);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(aceptar.getText().equals("Aceptar")){
                    if(!list.isEmpty()){
                        if(list.size()>=4){
                            AlertDialog.Builder aceptBuilder = new AlertDialog.Builder(productos.this);
                            View vistaAcept = getLayoutInflater().inflate(R.layout.dialog_cliente,null);
                            final EditText userU = (EditText) vistaAcept.findViewById(R.id.userName);
                            final EditText userC = (EditText) vistaAcept.findViewById(R.id.passUser);
                            Button mAcept = (Button) vistaAcept.findViewById(R.id.userAcept);
                            Button mCancel = (Button) vistaAcept.findViewById(R.id.userCancel);
                            aceptBuilder.setView(vistaAcept);
                            final AlertDialog dialog = aceptBuilder.create();
                            dialog.show();
                            mAcept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(!userC.getText().toString().isEmpty() && !userU.getText().toString().isEmpty())
                                    {
                                        actualizarStatus("Send");
                                        new enviarStatus().execute();
                                        Toast.makeText(getApplicationContext(),"Validación Guardada, dar clic en Terminar",Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        aceptar.setText("Terminar");
                                        comentario.setEnabled(false);
                                        enviarDoc();
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(),"Faltan datos",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            mCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });
                        }else{
                            Toast.makeText(getApplicationContext(),"Te falta agregar foto y/o firma",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"Te falta agregar información",Toast.LENGTH_SHORT).show();
                        String testo = obtenerDatos(0);
                        //status = WebService.invokeImagenWS("OC0000001","put0 el angel","Foto1");
                        Log.d("CICL0 ws", "0 "+status);
                        Log.d(tag, testo);
                    }
                }
                else{
                    borrarBase();
                    datos.getDb().close();
                    Toast.makeText(getApplicationContext(),"Información Enviada",Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
        /*codigo foto 1*/
        img1 = (ImageButton) findViewById(R.id.foto1);
        file.mkdirs();
        img1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                checkPermission = revisarPermisos();
                if(checkPermission)
                {
                    bandera=1;
                    dispatchTakePictureIntent();
                }
            }
        });
        /*codigo foto 1*/
        img2 = (ImageButton) findViewById(R.id.foto2);
        file.mkdirs();
        img2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                checkPermission = revisarPermisos();
                if(checkPermission)
                {
                    bandera=2;
                    dispatchTakePictureIntent();
                }
            }
        });
        /*codigo foto 1*/
        img3 = (ImageButton) findViewById(R.id.foto3);
        file.mkdirs();
        img3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                checkPermission = revisarPermisos();
                if(checkPermission)
                {
                    bandera=3;
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
                final AlertDialog dialogAlert = firmaBuilder.create();
                dialogAlert.show();
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
                        //convert64();
                        list.add(0,"1");
                        Toast.makeText(getApplicationContext(),"Se ha guardado la Firma",Toast.LENGTH_SHORT).show();
                        dialog.setEnabled(false);
                        savePath(mCurrentSignPath);
                        dialogAlert.dismiss();
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

    }

    public String getFolio(){
        try {
            Log.e("PRODUCTO", "GUARDANDO FOTOS");
            datos.getDb().beginTransaction();
            Cursor cursor1 =datos.obtenerApp();
            if(cursor1!=null){
                if (cursor1.moveToFirst()) {
                    int columna = cursor1.getColumnIndex("folio");
                    folioT = cursor1.getString(columna);
                }
                Log.e("ESTAD0", "folioT-U: "+folioT);
            }
            datos.getDb().setTransactionSuccessful();
        } finally {
            datos.getDb().endTransaction();
        }
        return folioT;
    }

    public void getProducts(String f){
        try {
            Log.e("PRODUCTO", "get dat0s");
            datos.getDb().beginTransaction();
            Cursor cursor1 =datos.obtenerProducto(f);
            if(cursor1!=null){
                //Nos aseguramos de que existe al menos un registro
                if (cursor1.moveToFirst()) {
                    //Recorremos el cursor hasta que no haya más registros
                    do {
                        Producto p = new Producto();
                        int columna = cursor1.getColumnIndex("producto");
                        p.producto = cursor1.getString(columna);
                        int columna2 = cursor1.getColumnIndex("cantidad");
                        p.cantidad = cursor1.getString(columna2);
                        LISTAP.add(p);
                    } while(cursor1.moveToNext());
                }
            }
            datos.getDb().setTransactionSuccessful();
        } finally {
            datos.getDb().endTransaction();
        }
    }

    private void enviarDoc() {
        convert64();
        if(folioT!=null){
            //if(!folioT.isEmpty()){
            // doc = new Documentos(null,list64.get(0),list64.get(1),list64.get(2),list64.get(3),null, "Entregado","",folioT);
            //datos.insertarDocumentos(doc);//pass=xcvb
            AsyncWS task = new AsyncWS();
            task.execute();
            //}
        }else{

        }
    }

    public void borrarBase(){
        datos.getDb().beginTransaction();
        String folioT ="";
        Cursor cursor =datos.obtenerApp();
        if(cursor!=null){
            if (cursor.moveToFirst()) {
                int columna = cursor.getColumnIndex("folio");
                folioT = cursor.getString(columna);
            }
            Log.e("ESTAD0", "folioT-U: "+folioT);
        }
        datos.actualizarStatus("Sin Enviar",folioT);
        datos.borrar("App");
        datos.borrar("Producto");
        datos.borrar("Documentos");
        datos.borrar("Entrega");
        datos.borrar("Usuario");
        DatabaseUtils.dumpCursor(datos.obtenerApp());
        DatabaseUtils.dumpCursor(datos.obtenerUser());
        Cursor cursor1 =datos.obtenerApp();
        String hola =null;
        if(cursor1!=null){
            if (cursor1.moveToFirst()) {
                int columna = cursor1.getColumnIndex("estatus");
                hola = cursor1.getString(columna);
            }
            Log.e("ESTAD0", "BORRAR: "+hola);
        }
        cursor1 =datos.obtenerUser();
        String hola1 =null;
        if(cursor1!=null){
            if (cursor1.moveToFirst()) {
                int columna = cursor1.getColumnIndex("nonbre");
                hola1 = cursor1.getString(columna);
            }
            Log.e("ESTAD0", "BORRAR-U: "+hola1);
        }
        datos.deleteALL(getApplicationContext());
    }

    public void init(){
        stk = (TableLayout) findViewById(R.id.listTable);
        TableRow tbrow0 = new TableRow(this);
        tbrow0.setBackgroundColor(Color.DKGRAY);
        TextView tv0 = new TextView(this);
        tv0.setText(" Producto ");
        tv0.setTextColor(Color.WHITE);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(this);
        tv1.setText(" Unidad ");
        tv1.setTextColor(Color.WHITE);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setText(" Estado ");
        tv2.setTextColor(Color.WHITE);
        tbrow0.addView(tv2);
        TextView tv3 = new TextView(this);
        tv3.setText(" Faltante ");
        tv3.setTextColor(Color.WHITE);
        tbrow0.addView(tv3);
        stk.addView(tbrow0);
        String f = getFolio();
        getProducts(f);
        if(LISTAP.size() != 0)
        {
            for (int i = 0; i < LISTAP.size(); i++) {
                TableRow tbrow = new TableRow(this);
                //col 1
                TextView t1v = new TextView(this);
                t1v.setText(LISTAP.get(i).producto);
                t1v.setTextColor(Color.BLACK);
                t1v.setGravity(Gravity.CENTER);
                tbrow.addView(t1v);
                //col 2
                TextView t2v = new TextView(this);
                t2v.setText(LISTAP.get(i).cantidad);
                t2v.setTextColor(Color.BLACK);
                t2v.setGravity(Gravity.CENTER);
                tbrow.addView(t2v);
                //col 3
                Spinner list1 = new Spinner(this);
                list1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, lista2));
                list1.setGravity(Gravity.CENTER_HORIZONTAL);
                tbrow.addView(list1);
                //col 4
                Spinner list2 = new Spinner(this);
                list2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, lista1));
                list2.setGravity(Gravity.CENTER_HORIZONTAL);
                tbrow.addView(list2);
                Log.d("tabla tbrow",""+tbrow.getChildCount());
                //
                stk.addView(tbrow);
            }
        }
        Log.d("tabla",""+stk.getChildCount());
    }

    public String obtenerDatos(int index){
        String res="";
        Log.d("tabla",""+stk.getChildCount());
        View view = stk.getChildAt(index+5);
        TableRow t = (TableRow)view;
        Log.d("tabla",""+t.getChildCount());
        if(t!=null) {
            TextView firstTextView = (TextView) t.getChildAt(0);
            TextView secondTextView = (TextView) t.getChildAt(1);
            Spinner mspinner = (Spinner) t.getChildAt(2);
            Spinner mspinner2 = (Spinner) t.getChildAt(3);
            String uno = firstTextView.getText().toString();
            String dos = secondTextView.getText().toString();
            String tres = mspinner.getSelectedItem().toString();
            String cuatro = mspinner2.getSelectedItem().toString();
            res = uno+"," + dos +"," + tres +"," + cuatro;
        }
        return res;
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
        //bandera=3;
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
                Log.e(tag, "empezando activity");
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
            galleryAddPic();
            if(bandera>0)
            {
                setPic2();
                if(bandera == 1)
                    img1.setEnabled(false);
                else if(bandera == 2)
                    img2.setEnabled(false);
                else
                    img3.setEnabled(false);
                savePath(mCurrentPhotoPath);
            }
            else{
                Toast.makeText(this, "Vuelva a intentar a tomar la foto", Toast.LENGTH_LONG).show();
            }

            mCurrentPhotoPath = null;
            list.add(0,"2");
            Log.e(tag, "despues de setpic: " + list.toString());

        }
        else{
            Log.e(tag, "Esta nulo");
            Toast.makeText(this, "Vuelva a intentar a tomar la foto", Toast.LENGTH_LONG).show();
        }
    }

    private void setPic2() {
        /* Get the size of the ImageView */
        /*int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();*/
        int targetW = 150;
        int targetH = 150;

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
        if(bandera ==1)
            fotoFinal = (ImageButton) findViewById(R.id.foto1);
        else if(bandera==2)
            fotoFinal = (ImageButton) findViewById(R.id.foto2);
        else
            fotoFinal = (ImageButton) findViewById(R.id.foto3);
		/* Associate the Bitmap to the ImageView */
        fotoFinal.setImageBitmap(bitmap);
        //mVideoUri = null;
        fotoFinal.setVisibility(View.VISIBLE);
        Log.e(tag, "Colocando");
        //mVideoView.setVisibility(View.INVISIBLE);
    }

    private void convert64(){
        for(int i = 0; i < list64path.size(); i++){
            String xxx = encodeImage(list64path.get(i));
            Log.e(tag, " TAM: " +xxx.length());
            /*try {
                byte[] bytesFinal = GZipUtils.compressBytes(xxx.getBytes(StandardCharsets.UTF_8));
                String text = new String(bytesFinal, StandardCharsets.UTF_8);
                Log.e(tag, " TAM C0MPRESS: " +text.length());
                list64.add(text);
            } catch (GZipException e) {
                e.printStackTrace();
            }*/
            list64.add(xxx);
        }
    }

    private String encodeImage(String path) {
        File imagefile = new File(path);
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(imagefile);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        Bitmap bm = BitmapFactory.decodeStream(fis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,20,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        //Base64.de
        return encImage;

    }

    private void savePath(String path){
        list64path.add(path);
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        Log.e(tag, "Guardando ");
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
            Log.e(tag, "buen resultado");
            handleBigCameraPhoto();
        }
        else
        {Log.e(tag, "mal resultado");
            Toast.makeText(this, "Vuelva a intentar a tomar la foto", Toast.LENGTH_LONG).show();}
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Aún no terminas el proceso", Toast.LENGTH_LONG).show();
        return;
    }

    private void actualizarStatus(String statusNew) {
        try {
            Log.e(tag, "Actualizar");
            datos.getDb().beginTransaction();
            String UserComanda=null;
            Cursor cursor = datos.obtenerEstatus();
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columna = cursor.getColumnIndex("folio");
                    UserComanda = cursor.getString(columna);
                }
                Log.e(tag, "user: " + UserComanda);
                Cursor cursor2 = datos.actualizarStatus(statusNew, UserComanda);
                if (cursor2 != null) {
                    Log.e(tag, "Si hay actualizar estado");
                    if (cursor2.moveToFirst()) {
                        int columna = cursor2.getColumnIndex("folio");
                        String estado = cursor2.getString(columna);
                        Log.d("QUERY", estado);
                    }
                } else {
                    Log.d("QUERY", "Error en query 2");
                }
            } else {
                Log.d("USER", "Error algo vacio");
            }
            datos.getDb().setTransactionSuccessful();
        } finally {
            datos.getDb().endTransaction();
        }
        DatabaseUtils.dumpCursor(datos.obtenerApp());
    }

    public class enviarStatus extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            boolean status = WebService.invokeComanda(Folio, "Entregado");
            return null;
        }
    }

    private class AsyncWS extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            //Call Web Method
            Log.d("imagen ws", list64.size() +"");
            //for(int i = 0; i<4;i++){
            status = WebService.invokeImagenWS(folioT,list64.get(0),"Foto1");Log.d("CICL0 ws", "0 "+status);
            //Log.d("CICL0 ws", list64.get(0));
            status = WebService.invokeImagenWS(folioT,list64.get(1),"Foto2");Log.d("CICL0 ws", "1 "+status);
            //Log.d("CICL0 ws", list64.get(1));
            status = WebService.invokeImagenWS(folioT,list64.get(2),"Foto3");Log.d("CICL0 ws", "2 "+status);
            //Log.d("CICL0 ws", list64.get(2));
            status = WebService.invokeImagenWS(folioT,list64.get(3),"Firma");Log.d("CICL0 ws", "3 "+status  );
            //Log.d("CICL0 ws", list64.get(3));

            Log.d("imagen ws","termine ed enviar");
            return null;
        }

        @Override
        //Once WebService returns response
        protected void onPostExecute(Void result) {
            //Error status is false
            if(status){
                //Error status is true
                Toast.makeText(getApplicationContext(),"Se envío informacación",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),"Aún no se envia información",Toast.LENGTH_SHORT).show();
            }
            //Re-initialize Error Status to False
            status = false;
        }

        @Override
        //Make Progress Bar visible
        protected void onPreExecute() {

        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

}

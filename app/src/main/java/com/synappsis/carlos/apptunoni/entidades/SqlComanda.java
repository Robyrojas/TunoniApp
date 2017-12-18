package com.synappsis.carlos.apptunoni.entidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.provider.BaseColumns;

/**
 * Created by CARLOS on 15/12/2017.
 */

public class SqlComanda extends SQLiteOpenHelper{
    private static final String NOMBRE_BASE_DATOS = "tunoni.db";

    private static final int VERSION_ACTUAL = 1;

    private final Context contexto;
    interface Tablas {
        String TABLE_ENTREGA = "Entrega";
        String TABLE_USUARIO = "Usuario";
        String TABLE_PRODUCTO = "Producto";
        String TABLE_DOCUMENTOS = "Documentos";
    }

    interface Referencias {

        String ID_TABLE_ENTREGAS = String.format("REFERENCES %s(%s)",//"REFERENCES %s(%s) ON DELETE CASCADE"
                Tablas.TABLE_ENTREGA, Comanda.Entrega.USUARIO_NOMBRE);

        String ID_TABLE_DOCUMENTOS = String.format("REFERENCES %s(%s)",
                Tablas.TABLE_DOCUMENTOS, Comanda.Documentos.USUARIO_NOMBRE);

        String ID_TABLE_PRODUCTOS = String.format("REFERENCES %s(%s)",
                Tablas.TABLE_PRODUCTO, Comanda.Producto.USUARIO_NOMBRE);

    }

    public SqlComanda(Context contexto) {
        super(contexto, NOMBRE_BASE_DATOS, null, VERSION_ACTUAL);
        this.contexto = contexto;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(String.format("CREATE TABLE %s (%s TEXT PRIMARY KEY," +
                        "%s TEXT NOT NULL)",
                Tablas.TABLE_USUARIO,
                Comanda.Usuario.NOMBRE,
                Comanda.Usuario.PASS
        ));

        db.execSQL(String.format("CREATE TABLE %s ( %s TEXT PRIMARY KEY," +//FOLI0
                        "%s TEXT NOT NULL," +//ESTATUS
                        "%s TEXT NOT NULL," +//DIR0
                        "%s TEXT NOT NULL," +//FECHA
                        "%s TEXT NOT NULL," +//NOMBRE
                        "%s TEXT NOT NULL," +//DRIDE
                        "%s TEXT NOT NULL," +//FECHA DES
                        "%s TEXT NOT NULL," +//N0MBRE RECEPT0R
                        "%s TEXT," +//INF0
                        "%s TEXT NOT NULL %s" +//user n0mbre
                        ")",
                Tablas.TABLE_ENTREGA,
                Comanda.Entrega.FOLIO,
                Comanda.Entrega.ESTATUS,
                Comanda.Entrega.DIRORIGEN,
                Comanda.Entrega.FECHAORIGEN,
                Comanda.Entrega.NOMBRE,
                Comanda.Entrega.DIRDESTINO,
                Comanda.Entrega.FECHADESTINO,
                Comanda.Entrega.NOMBRERECEPTOR,
                Comanda.Entrega.INFO,
                Comanda.Entrega.USUARIO_NOMBRE, Referencias.ID_TABLE_ENTREGAS
        ));

        db.execSQL(String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY," +//FOLI0
                        "%s TEXT NOT NULL," +//FALTANTE
                        "%s TEXT NOT NULL," +//CANTIDAD
                        "%s TEXT NOT NULL," +//PRODUCTO
                        "%s TEXT NOT NULL," +//ESTADO
                        "%s TEXT NOT NULL %s" +//user n0mbre
                        ")",
                Tablas.TABLE_PRODUCTO, //BaseColumns._ID,
                Comanda.Producto.IDPRODUCTO,
                Comanda.Producto.FALTANTE,
                Comanda.Producto.CANTIDAD,
                Comanda.Producto.PRODUCTO,
                Comanda.Producto.ESTADO,
                Comanda.Producto.USUARIO_NOMBRE, Referencias.ID_TABLE_PRODUCTOS
                ));

        /*db.execSQL(String.format("CREATE TABLE %s ( %s TEXT PRIMARY KEY," +
                        "%s TEXT NOT NULL," +
                        "%s TEXT NOT NULL," +
                        "%s TEXT NOT NULL," +
                        "%s TEXT NOT NULL," +
                        "%s TEXT," +
                        "%s TEXT NOT NULL %s" +
                        ")",
                Tablas.TABLE_DOCUMENTOS,
                Comanda.Documentos.IDDOCUMENTOS,
                Comanda.Documentos.FOTO1,
                Comanda.Documentos.FOTO2,
                Comanda.Documentos.FOTO3,
                Comanda.Documentos.FIRMA,
                Comanda.Documentos.COMENTARIOS,
                Comanda.Documentos.USUARIO_NOMBRE, Referencias.ID_TABLE_DOCUMENTOS
                ));
        */
        db.execSQL(String.format("CREATE TABLE "+Tablas.TABLE_DOCUMENTOS +"("+
                Comanda.Documentos.IDDOCUMENTOS + " TEXT PRIMARY KEY," +
                Comanda.Documentos.FOTO1 + " TEXT NOT NULL," +
                Comanda.Documentos.FOTO2 + " TEXT NOT NULL," +
                Comanda.Documentos.FOTO3 + " TEXT NOT NULL," +
                Comanda.Documentos.FIRMA + " TEXT NOT NULL," +
                Comanda.Documentos.COMENTARIOS + " TEXT," +
                "REFERENCES "+Comanda.Documentos.USUARIO_NOMBRE+"("+Referencias.ID_TABLE_DOCUMENTOS+")"+
                ");"));
    }
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                db.setForeignKeyConstraintsEnabled(true);
            } else {
                db.execSQL("PRAGMA foreign_keys=ON");
            }
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + Tablas.TABLE_DOCUMENTOS);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.TABLE_ENTREGA);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.TABLE_PRODUCTO);
        db.execSQL("DROP TABLE IF EXISTS " + Tablas.TABLE_USUARIO);

        onCreate(db);
    }

}

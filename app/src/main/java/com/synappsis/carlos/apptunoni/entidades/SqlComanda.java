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

        String ID_TABLE_ENTREGAS = String.format("REFERENCES %s(%s) ON DELETE CASCADE",
                Tablas.TABLE_USUARIO, Comanda.Entrega.USUARIO_NOMBRE);

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
                        "%s TEXT NOT NULL,%s TEXT NOT NULL," +
                        "%s INTEGER NOT NULL)",
                Tablas.TABLE_USUARIO, BaseColumns._ID,
                Comanda.Usuario.NOMBRE,
                Comanda.Usuario.PASS
        ));

        db.execSQL(String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY," +
                        "%s TEXT NOT NULL UNIQUE,%s TEXT NOT NULL,%s REAL NOT NULL," +
                        "%s INTEGER NOT NULL CHECK(%s>=0) )",
                Tablas.TABLE_ENTREGA, BaseColumns._ID,
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

        db.execSQL(String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "%s TEXT NOT NULL UNIQUE,%s TEXT NOT NULL,%s TEXT NOT NULL,%s )",
                Tablas.TABLE_PRODUCTO, BaseColumns._ID,
                Comanda.Producto.IDPRODUCTO,
                Comanda.Producto.FALTANTE,
                Comanda.Producto.CANTIDAD,
                Comanda.Producto.PRODUCTO,
                Comanda.Producto.ESTADO,
                Comanda.Producto.USUARIO_NOMBRE, Referencias.ID_TABLE_PRODUCTOS
                ));

        db.execSQL(String.format("CREATE TABLE %s ( %s INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "%s TEXT NOT NULL UNIQUE,%s TEXT NOT NULL )",
                Tablas.TABLE_DOCUMENTOS, BaseColumns._ID,
                Comanda.Documentos.IDDOCUMENTOS,
                Comanda.Documentos.FOTO1,
                Comanda.Documentos.FOTO2,
                Comanda.Documentos.FOTO3,
                Comanda.Documentos.FIRMA,
                Comanda.Documentos.COMENTARIOS,
                Comanda.Documentos.USUARIO_NOMBRE, Referencias.ID_TABLE_DOCUMENTOS
                ));

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

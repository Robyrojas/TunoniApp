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
                Tablas.TABLE_USUARIO, Comanda.Usuario.NOMBRE);
        String ID_TABLE_DOCUMENTOS = String.format("REFERENCES %s(%s,%s)",
                Tablas.TABLE_ENTREGA, Comanda.Entrega.USUARIO_NOMBRE, Comanda.Entrega.FOLIO);
        String ID_TABLE_PRODUCTOS = String.format("REFERENCES %s(%s,%s)",
                Tablas.TABLE_ENTREGA, Comanda.Entrega.USUARIO_NOMBRE, Comanda.Entrega.FOLIO);

    }

    public SqlComanda(Context contexto) {
        super(contexto, NOMBRE_BASE_DATOS, null, VERSION_ACTUAL);
        this.contexto = contexto;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        /*user*/
        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s (%s TEXT NOT NULL PRIMARY KEY," +
                        "%s TEXT NOT NULL)",
                Tablas.TABLE_USUARIO,
                Comanda.Usuario.NOMBRE,
                Comanda.Usuario.PASS
        ));
        /*entrega*/
        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s ( %s TEXT NOT NULL," +//FOLI0
                        "%s TEXT NOT NULL," +//ESTATUS
                        "%s TEXT NOT NULL," +//DIR0
                        "%s TEXT NOT NULL," +//FECHA
                        "%s TEXT NOT NULL," +//NOMBRE
                        "%s TEXT NOT NULL," +//DRIDE
                        "%s TEXT NOT NULL," +//FECHA DES
                        "%s TEXT NOT NULL," +//N0MBRE RECEPT0R
                        "%s TEXT," +//INF0
                        "%s TEXT NOT NULL," +//USER NAME
                        "FOREIGN KEY (%s) %s," +//FOREIGN / REFERENCES
                        "PRIMARY KEY (%s, %s)"+
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
                Comanda.Entrega.USUARIO_NOMBRE,
                Comanda.Entrega.USUARIO_NOMBRE, Referencias.ID_TABLE_ENTREGAS,
                Comanda.Entrega.FOLIO, Comanda.Entrega.USUARIO_NOMBRE
        ));
        /*producto*/
        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s ( %s INTEGER PRIMARY KEY," +//FOLI0
                        "%s TEXT NOT NULL," +//FALTANTE
                        "%s TEXT NOT NULL," +//CANTIDAD
                        "%s TEXT NOT NULL," +//PRODUCTO
                        "%s TEXT NOT NULL," +//ESTADO
                        "%s TEXT NOT NULL," +//user n0mbre
                        "%s TEXT NOT NULL," +//entregafolio
                        "FOREIGN KEY (%s,%s) %s" +//FOREIGN / REFERENCES
                        ")",
                Tablas.TABLE_PRODUCTO, //BaseColumns._ID,
                Comanda.Producto.IDPRODUCTO,
                Comanda.Producto.FALTANTE,
                Comanda.Producto.CANTIDAD,
                Comanda.Producto.PRODUCTO,
                Comanda.Producto.ESTADO,
                Comanda.Producto.USUARIO_NOMBRE,
                Comanda.Producto.ENTREGA_FOLIO,
                Comanda.Producto.USUARIO_NOMBRE, Comanda.Producto.ENTREGA_FOLIO, Referencias.ID_TABLE_PRODUCTOS
                ));
        /*documentacion*/
        db.execSQL(String.format("CREATE TABLE IF NOT EXISTS %s ( %s INTEGER PRIMARY KEY," +//ID
                        "%s TEXT NOT NULL," +//F1
                        "%s TEXT NOT NULL," +//F2
                        "%s TEXT NOT NULL," +//F3
                        "%s TEXT NOT NULL," +//FIRM
                        "%s TEXT NOT NULL," +//c0mentari0
                        "%s TEXT NOT NULL," +//USER
                        "%s TEXT NOT NULL," +//F0LI0
                        "FOREIGN KEY (%s,%s) %s" +//FOREIGN / REFERENCES
                        ")",
                Tablas.TABLE_DOCUMENTOS,
                Comanda.Documentos.IDDOCUMENTOS,
                Comanda.Documentos.FOTO1,
                Comanda.Documentos.FOTO2,
                Comanda.Documentos.FOTO3,
                Comanda.Documentos.FIRMA,
                Comanda.Documentos.COMENTARIOS,
                Comanda.Documentos.USUARIO_NOMBRE,
                Comanda.Documentos.ENTREGA_FOLIO,
                Comanda.Documentos.USUARIO_NOMBRE, Comanda.Documentos.ENTREGA_FOLIO, Referencias.ID_TABLE_DOCUMENTOS
                ));
    }
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                db.setForeignKeyConstraintsEnabled(true);
            } else {
                db.execSQL("PRAGMA foreign_keys=1");
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

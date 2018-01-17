package com.synappsis.carlos.apptunoni.entidades;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.synappsis.carlos.apptunoni.entidades.SqlComanda.Tablas;

/**
 * Created by armando on 17/12/17.
 */

public class OperacionesBaseDatos {
    private static SqlComanda baseDatos;

    private static OperacionesBaseDatos instancia = new OperacionesBaseDatos();

    private OperacionesBaseDatos() {
    }

    public static OperacionesBaseDatos obtenerInstancia(Context contexto) {
        if (baseDatos == null) {
            baseDatos = new SqlComanda(contexto);
        }
        return instancia;
    }
    /*USUARIOS*/

    public String insertarUser(Usuario user) {
        SQLiteDatabase db = baseDatos.getWritableDatabase();
        String Query = "SELECT * FROM Usuario WHERE nombre = '"+user.nombre+"'";
        Cursor cursor = db.rawQuery(Query, null);
        String doble =null;
        if (cursor.moveToFirst()){
            do {
                // Passing values
                doble = cursor.getString(0);
                // Do something Here with values
            } while(cursor.moveToNext());
        }
        if(doble != null) {
                Log.d("Entrega", "Ya existe el user");
                return "Ya existe";
        }
        else
        {
            Log.d("USER","User Nueva");
            ContentValues valores = new ContentValues();
            valores.put(Comanda.Usuario.NOMBRE, user.nombre);
            valores.put(Comanda.Usuario.PASS, user.pass);
            long resultado = db.insertOrThrow(Tablas.TABLE_USUARIO, null, valores);
            if(resultado == -1) {
                return "Hubo un error";
            }
            else {
                return user.nombre;
            }
        }
    }

    public Cursor obtenerUser() {
        SQLiteDatabase db = baseDatos.getReadableDatabase();

        String sql = String.format("SELECT * FROM %s", Tablas.TABLE_USUARIO);

        return db.rawQuery(sql, null);
    }

    public boolean eliminarUser(String idProducto) {
        SQLiteDatabase db = baseDatos.getWritableDatabase();

        String whereClause = String.format("%s=?", Comanda.Usuario.NOMBRE);
        String[] whereArgs = {idProducto};

        int resultado = db.delete(Tablas.TABLE_USUARIO, whereClause, whereArgs);

        return resultado > 0;
    }
    /*FIN USUARIOS*/

    /*ENTREGAS*/
    public String insertarEntrega(Entrega entrega) {
        SQLiteDatabase db = baseDatos.getWritableDatabase();
        String Query = "SELECT * FROM Entrega WHERE folio = '"+entrega.folio+"'";
        Cursor cursor = db.rawQuery(Query, null);
        String doble =null;
        if (cursor.moveToFirst()){
            do {
                // Passing values
                doble = cursor.getString(0);
                // Do something Here with values
            } while(cursor.moveToNext());
        }
        if(doble != null){
                Log.d("Entrega","Ya existe la Entrega");
                return "Ya existe";
        }
        else
        {
            Log.d("Entrega","Entrega Nueva");
            ContentValues valores = new ContentValues();
            // Generar Pk
            String idProducto = entrega.folio;
            valores.put(Comanda.Entrega.FOLIO, idProducto);
            valores.put(Comanda.Entrega.ESTATUS, entrega.estatus);
            valores.put(Comanda.Entrega.DIRORIGEN, entrega.dirorigen);
            valores.put(Comanda.Entrega.FECHAORIGEN, entrega.fechaorigen);
            valores.put(Comanda.Entrega.NOMBRE, entrega.nombre);
            valores.put(Comanda.Entrega.DIRDESTINO, entrega.dirdestino);
            valores.put(Comanda.Entrega.FECHADESTINO, entrega.fechadestino);
            valores.put(Comanda.Entrega.NOMBRERECEPTOR, entrega.nombrereceptor);
            valores.put(Comanda.Entrega.INFO, entrega.info);
            valores.put(Comanda.Entrega.USUARIO_NOMBRE, entrega.usuario_nombre);

            long resultado = db.insertOrThrow(Tablas.TABLE_ENTREGA, null, valores);
            if(resultado == -1) {
                return "Hubo un error";
            }
            else {
                return idProducto;
            }
        }
    }

    public Cursor obtenerEntregas(String id) {
        SQLiteDatabase db = baseDatos.getReadableDatabase();

        //String sql = String.format("SELECT * FROM %s", Tablas.TABLE_ENTREGA);
        //return db.rawQuery(sql, null);
        String query = "select * from " + Tablas.TABLE_ENTREGA + " WHERE usuario_nombre=?";
        Cursor res = db.rawQuery(query, new String[]{id});
        Log.d("QUERY", res.toString());
        return res;
    }

    public boolean eliminarEntregas(String idProducto) {
        SQLiteDatabase db = baseDatos.getWritableDatabase();

        String whereClause = String.format("%s=?", Comanda.Entrega.FOLIO);
        String[] whereArgs = {idProducto};

        int resultado = db.delete(Tablas.TABLE_ENTREGA, whereClause, whereArgs);

        return resultado > 0;
    }
    /*FIN ENTREGAS*/

    /*PRODUCTO*/
    public String insertarProducto(Producto product) {
        SQLiteDatabase db = baseDatos.getWritableDatabase();
        String Query = "SELECT * FROM Producto WHERE idproducto = '"+product.idproducto+"'";
        Cursor cursor = db.rawQuery(Query, null);
        String doble =null;
        if (cursor.moveToFirst()){
            do {
                // Passing values
                doble = cursor.getString(0);
                // Do something Here with values
            } while(cursor.moveToNext());
        }
        if(doble != null){
            Log.d("PR0DUCT0","Ya existe el producto");
            return "Ya existe";
        }
        else{
            Log.d("pr0duct0","pr0duct0 Nueva");
            ContentValues valores = new ContentValues();
            // Generar Pk
            String idProducto = Comanda.Producto.generarIdProducto();
            valores.put(Comanda.Producto.IDPRODUCTO, idProducto);
            valores.put(Comanda.Producto.FALTANTE, product.faltante);
            valores.put(Comanda.Producto.CANTIDAD, product.cantidad);
            valores.put(Comanda.Producto.PRODUCTO, product.producto);
            valores.put(Comanda.Producto.ESTADO, product.estado);
            valores.put(Comanda.Producto.USUARIO_NOMBRE, product.usuario_nombre);
            valores.put(Comanda.Producto.ENTREGA_FOLIO, product.entrega_folio);
            Log.d("SQL",idProducto);
            long resultado = db.insert(Tablas.TABLE_PRODUCTO, null, valores);
            //this.getWritableDatabase().insert(Tablas.TABLE_PRODUCTO, null, valores);
            if(resultado == -1) {
                return "Hubo un error";
            }
            else {
                return idProducto;
            }
        }
    }

    public Cursor obtenerProducto(String user) {
        SQLiteDatabase db = baseDatos.getReadableDatabase();

        String query = "select * from " + Tablas.TABLE_PRODUCTO + " WHERE usuario_nombre=?";
        Cursor res = db.rawQuery(query, new String[]{user});
        return res;
    }

    public boolean eliminarProducto(String idProducto) {
        SQLiteDatabase db = baseDatos.getWritableDatabase();

        String whereClause = String.format("%s=?", Comanda.Producto.IDPRODUCTO);
        String[] whereArgs = {idProducto};

        int resultado = db.delete(Tablas.TABLE_PRODUCTO, whereClause, whereArgs);

        return resultado > 0;
    }
    /*FIN PRODUCTO*/

    /*DOCUMENTOS*/
    public String insertarDocumentos(Documentos doc) {
        SQLiteDatabase db = baseDatos.getWritableDatabase();
        String Query = "SELECT * FROM Documentos WHERE iddocumentos = '"+doc.iddocumentos+"'";
        Cursor cursor = db.rawQuery(Query, null);
        String doble =null;
        if (cursor.moveToFirst()){
            do {
                // Passing values
                doble = cursor.getString(0);
                // Do something Here with values
            } while(cursor.moveToNext());
        }
        if(doble != null){
            Log.d("D0C","Ya existe la documentacion");
            return "Ya existe";
        }
        else{
            Log.d("DOC","D0C Nueva");
            ContentValues valores = new ContentValues();
            // Generar Pk
            String idProducto = Comanda.Documentos.generarIdDocumentos();
            valores.put(Comanda.Documentos.IDDOCUMENTOS, idProducto);
            valores.put(Comanda.Documentos.FOTO1, doc.foto1);
            valores.put(Comanda.Documentos.FOTO2, doc.foto2);
            valores.put(Comanda.Documentos.FOTO3, doc.foto3);
            valores.put(Comanda.Documentos.FIRMA, doc.firma);
            valores.put(Comanda.Documentos.COMENTARIOS, doc.comentarios);
            valores.put(Comanda.Documentos.USUARIO_NOMBRE, doc.usuario_nombre);

        /*long resultado = db.insertOrThrow(Tablas.TABLE_DOCUMENTOS, null, valores);
        if(resultado == -1) {
            return "Hubo un error";
        }
        else {
            return idProducto;
        }*/
            db.insert(Tablas.TABLE_DOCUMENTOS, null, valores);
            return idProducto;
        }
    }

    public Cursor obtenerDocumentos(String usuario_nombre) {
        SQLiteDatabase db = baseDatos.getReadableDatabase();

        //String sql = String.format("SELECT * FROM %s", Tablas.TABLE_DOCUMENTOS);
        //b.rawQuery("SELECT body FROM table1 WHERE title IN ('title1', 'title2', 'title3')");
        String query = "select * from " + Tablas.TABLE_DOCUMENTOS + " WHERE usuario_nombre=?";
        Cursor res = db.rawQuery(query, new String[]{usuario_nombre});
        return res;
    }

    public boolean eliminarDocumentos(String idProducto) {
        SQLiteDatabase db = baseDatos.getWritableDatabase();

        String whereClause = String.format("%s=?", Comanda.Documentos.IDDOCUMENTOS);
        String[] whereArgs = {idProducto};

        int resultado = db.delete(Tablas.TABLE_DOCUMENTOS, whereClause, whereArgs);

        return resultado > 0;
    }
    /*FIN DOCUMENTOS*/

    /*APP*/
    public String insertarApp(App appbase) {
        SQLiteDatabase db = baseDatos.getWritableDatabase();
        String Query = "SELECT * FROM App WHERE folio = '"+appbase.folio+"'";
        Cursor cursor = db.rawQuery(Query, null);
        String doble =null;
        if (cursor.moveToFirst()){
            do {
                // Passing values
                doble = cursor.getString(0);
                // Do something Here with values
            } while(cursor.moveToNext());
        }
        if(doble != null){
            Log.d("D0C","Ya existe la documentacion");
            return "Ya existe";
        }
        else{
            Log.d("DOC","D0C Nueva");
            ContentValues valores = new ContentValues();
            // Generar Pk
           /* String idProducto = Comanda.Documentos.generarIdDocumentos();
            valores.put(Comanda.Documentos.IDDOCUMENTOS, idProducto);
            valores.put(Comanda.Documentos.FOTO1, doc.foto1);
            valores.put(Comanda.Documentos.FOTO2, doc.foto2);
            valores.put(Comanda.Documentos.FOTO3, doc.foto3);
            valores.put(Comanda.Documentos.FIRMA, doc.firma);
            valores.put(Comanda.Documentos.COMENTARIOS, doc.comentarios);
            valores.put(Comanda.Documentos.USUARIO_NOMBRE, doc.usuario_nombre);

        /*long resultado = db.insertOrThrow(Tablas.TABLE_DOCUMENTOS, null, valores);
        if(resultado == -1) {
            return "Hubo un error";
        }
        else {
            return idProducto;
        }*/
            db.insert(Tablas.TABLE_DOCUMENTOS, null, valores);
            return "2";
        }
    }

    public Cursor obtenerApp(String usuario_nombre) {
        SQLiteDatabase db = baseDatos.getReadableDatabase();

        //String sql = String.format("SELECT * FROM %s", Tablas.TABLE_DOCUMENTOS);
        //b.rawQuery("SELECT body FROM table1 WHERE title IN ('title1', 'title2', 'title3')");
        String query = "select * from " + Tablas.TABLE_DOCUMENTOS + " WHERE usuario_nombre=?";
        Cursor res = db.rawQuery(query, new String[]{usuario_nombre});
        return res;
    }

    public boolean eliminarApp(String idProducto) {
        SQLiteDatabase db = baseDatos.getWritableDatabase();

        String whereClause = String.format("%s=?", Comanda.Documentos.IDDOCUMENTOS);
        String[] whereArgs = {idProducto};

        int resultado = db.delete(Tablas.TABLE_DOCUMENTOS, whereClause, whereArgs);

        return resultado > 0;
    }
    /*FIN APP*/

    public SQLiteDatabase getDb() {
        return baseDatos.getWritableDatabase();
    }
}

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

    public static synchronized OperacionesBaseDatos obtenerInstancia(Context contexto) {
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

    public Cursor obtenerUser(int a) {
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        String sql = String.format("SELECT nombre FROM %s", Tablas.TABLE_USUARIO);
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
            //valores.put(Comanda.Entrega.DIRORIGEN, entrega.dirorigen);
            valores.put(Comanda.Entrega.FECHAORIGEN, entrega.fechaorigen);
            valores.put(Comanda.Entrega.NOMBRE, entrega.nombre);
            valores.put(Comanda.Entrega.NOMBREDESTINO, entrega.nombredestino);
            valores.put(Comanda.Entrega.DIRDESTINO, entrega.dirdestino);
            valores.put(Comanda.Entrega.FECHADESTINO, entrega.fechadestino);
            valores.put(Comanda.Entrega.NOMBRERECEPTOR, entrega.nombrereceptor);
            //valores.put(Comanda.Entrega.INFO, entrega.info);
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
        String query = "select * from " + Tablas.TABLE_ENTREGA + " WHERE usuario_nombre=?";
        Cursor res = db.rawQuery(query, new String[]{id});
        return res;
    }

    public Cursor actualizarOrigen(String dirorigen, String folio){
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        Cursor res =null;
        try{
            String query = "UPDATE " + Tablas.TABLE_ENTREGA + " SET dirorigen = '"+ dirorigen +"' WHERE folio='"+folio+"'";
            Log.d("QUERY", query);
            res = db.rawQuery(query, null);
            return res;
        }catch (Exception e){
            return res;
        }
    }

    public Cursor actualizarDestino(String dir, String folio){
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        Cursor res =null;
        try{
            String query = "UPDATE " + Tablas.TABLE_ENTREGA + " SET dirdestino = '"+ dir +"' WHERE folio='"+folio+"'";
            Log.d("QUERY", query);
            res = db.rawQuery(query, null);
            return res;
        }catch (Exception e){
            return res;
        }
    }

    public Cursor actualizarStatusEntregas(String folio, String status){
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        Cursor res =null;
        try{
            String query = "UPDATE " + Tablas.TABLE_ENTREGA + " SET estatus = '"+ status +"' WHERE folio='"+folio+"'";
            Log.d("QUERY", query);
            res = db.rawQuery(query, null);
            return res;
        }catch (Exception e){
            return res;
        }
    }

    public Cursor obtenerEntregas() {
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        String sql = String.format("SELECT * FROM %s", Tablas.TABLE_ENTREGA);
        return db.rawQuery(sql, null);
    }

    public boolean eliminarEntregas(String folio) {
        SQLiteDatabase db = baseDatos.getWritableDatabase();

        String whereClause = String.format("%s=?", Comanda.Entrega.FOLIO);
        String[] whereArgs = {folio};

        int resultado = db.delete(Tablas.TABLE_ENTREGA, whereClause, whereArgs);

        return resultado > 0;
    }
    /*FIN ENTREGAS*/


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
            Log.d("APP","Ya existe el folio");
            return "Ya existe";
        }
        else{
            ContentValues valores = new ContentValues();
            valores.put(Comanda.App.FOLIO, appbase.folio);
            valores.put(Comanda.App.ENVIO, appbase.envio);
            valores.put(Comanda.App.ESTATUS, appbase.estatus);
            valores.put(Comanda.App.ACTUALIZAR, appbase.actualizar);

        /*long resultado = db.insertOrThrow(Tablas.TABLE_DOCUMENTOS, null, valores);
        if(resultado == -1) {
            return "Hubo un error";
        }
        else {
            return idProducto;
        }*/
            db.insert(Tablas.TABLE_APP, null, valores);
            return appbase.folio;
        }
    }

    public Cursor obtenerApp(String folio) {
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        String query = "select * from " + Tablas.TABLE_APP + " WHERE folio=?";
        Cursor res = db.rawQuery(query, new String[]{folio});
        return res;
    }

    public Cursor obtenerApp() {
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        String query = "select * from " + Tablas.TABLE_APP ;
        Cursor res = db.rawQuery(query, null);
        return res;
    }

    public Cursor actualizarFolio(String Folio){
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        Cursor res =null;
        try{
            String query = "UPDATE " + Tablas.TABLE_APP + " SET folio = '"+ Folio +"' WHERE estatus='Sin enviar'";
            Log.d("QUERY", query);
            res = db.rawQuery(query, null);
            return res;
        }catch (Exception e){
            return res;
        }
    }

    public Cursor actualizarStatus(String status, String Folioactual){
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        Cursor res =null;
        try{
            String query = "UPDATE " + Tablas.TABLE_APP + " SET estatus = '"+ status +"' WHERE folio = '"+Folioactual+"'";
            Log.d("QUERY", query);
            res = db.rawQuery(query, null);
            return res;
        }catch (Exception e){
            return res;
        }
    }

    public Cursor obtenerEstatus() {
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        String query = "select * from " + Tablas.TABLE_APP ;
        Cursor res = db.rawQuery(query, null);
        return res;
    }

    public boolean eliminarApp(String folio) {
        SQLiteDatabase db = baseDatos.getWritableDatabase();

        String whereClause = String.format("%s=?", Comanda.App.FOLIO);
        String[] whereArgs = {folio};

        int resultado = db.delete(Tablas.TABLE_DOCUMENTOS, whereClause, whereArgs);

        return resultado > 0;
    }
    /*FIN APP*/

    /*PRODUCTO*/
    public String insertarProducto(Producto product) {
        SQLiteDatabase db = baseDatos.getWritableDatabase();
        String id = "P-" +product.producto+product.entrega_folio;
        String Query = "SELECT * FROM Producto WHERE idproducto = '"+id+"'";
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
            //Log.d("pr0duct0","pr0duct0 Nueva");
            ContentValues valores = new ContentValues();
            // Generar Pk
            String idProducto = Comanda.Producto.generarIdProducto(product.producto ,product.entrega_folio );
            valores.put(Comanda.Producto.IDPRODUCTO, idProducto);
            valores.put(Comanda.Producto.FALTANTE, product.faltante);
            valores.put(Comanda.Producto.CANTIDAD, product.cantidad);
            valores.put(Comanda.Producto.PRODUCTO, product.producto);
            valores.put(Comanda.Producto.ESTADO, product.estado);
            valores.put(Comanda.Producto.USUARIO_NOMBRE, product.usuario_nombre);
            valores.put(Comanda.Producto.ENTREGA_FOLIO, product.entrega_folio);
            //Log.d("SQL",idProducto);
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

    public Cursor obtenerProducto(String folio) {
        SQLiteDatabase db = baseDatos.getReadableDatabase();

        String query = "select * from " + Tablas.TABLE_PRODUCTO + " WHERE entrega_folio=?";
        Cursor res = db.rawQuery(query, new String[]{folio});
        return res;
    }

    public Cursor obtenerProductos(String usuario_nombre) {
        SQLiteDatabase db = baseDatos.getReadableDatabase();

        String query = "select * from " + Tablas.TABLE_PRODUCTO + " WHERE usuario_nombre=?";
        Cursor res = db.rawQuery(query, new String[]{usuario_nombre});
        return res;
    }

    public Cursor actualizarProducto(String producto, String estado, String faltante){
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        Cursor res =null;
        try{
            String query = "UPDATE " + Tablas.TABLE_PRODUCTO + " SET estado = '"+ estado +"', faltante = '"+ faltante +"' WHERE producto = '"+producto+"'";
            Log.d("QUERY", query);
            res = db.rawQuery(query, null);
            return res;
        }catch (Exception e){
            return res;
        }
    }

    public boolean eliminarProducto(String folioT) {
        SQLiteDatabase db = baseDatos.getWritableDatabase();

        String whereClause = String.format("%s=?", Comanda.Producto.ENTREGA_FOLIO);
        String[] whereArgs = {folioT};

        int resultado = db.delete(Tablas.TABLE_PRODUCTO, whereClause, whereArgs);

        return resultado > 0;
    }
    /*FIN PRODUCTO*/

    /*DOCUMENTOS*/
    public String insertarDocumentos(Documentos doc, String folio) {
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
            String idProducto = Comanda.Documentos.generarIdDocumentos(folio);
            valores.put(Comanda.Documentos.IDDOCUMENTOS, idProducto);
            valores.put(Comanda.Documentos.FOTO1, doc.foto1);
            valores.put(Comanda.Documentos.FOTO2, doc.foto2);
            valores.put(Comanda.Documentos.FOTO3, doc.foto3);
            valores.put(Comanda.Documentos.FIRMA, doc.firma);
            valores.put(Comanda.Documentos.COMENTARIOS, doc.comentarios);
            valores.put(Comanda.Documentos.USUARIO_NOMBRE, doc.usuario_nombre);

        long resultado = db.insertOrThrow(Tablas.TABLE_DOCUMENTOS, null, valores);
        if(resultado == -1) {
            return "Hubo un error";
        }
        else {
            return idProducto;
        }
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

    public boolean eliminarDocumentos(String folioT) {
        SQLiteDatabase db = baseDatos.getWritableDatabase();
        String whereClause = String.format("%s=?", Comanda.Documentos.IDDOCUMENTOS);
        String[] whereArgs = {"D-"+folioT};

        int resultado = db.delete(Tablas.TABLE_DOCUMENTOS, whereClause, whereArgs);

        return resultado > 0;
    }

    /*FIN DOCUMENTOS*/
    public long contarRegistros(String nameTable){
        long contador = 0;
        SQLiteDatabase db = baseDatos.getReadableDatabase();
        contador = DatabaseUtils.queryNumEntries(db, nameTable);
        return contador;
    }

    public void borrar(String tabla){
        SQLiteDatabase db = baseDatos.getWritableDatabase();
        String sql = String.format("DELETE FROM %s", tabla);
        db.execSQL(sql);
        //db.execSQL("PRAGMA foreign_keys=1");
    }

    public Cursor verTablas(){
        SQLiteDatabase db = baseDatos.getWritableDatabase();
        String query = "SELECT * FROM sqlite_master where type='table'";
        Cursor res = db.rawQuery(query, null);
        return res;
    }

    public boolean deleteALL(Context ct){
        return baseDatos.deleteDatabase(ct);
    }

    public SQLiteDatabase getDb() {
        return baseDatos.getWritableDatabase();
    }
}

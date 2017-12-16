package com.synappsis.carlos.apptunoni.entidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by CARLOS on 14/12/2017.
 */

public class conexionSQL extends SQLiteOpenHelper {

    public conexionSQL(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Utilidades.CREAR_TABLA_TUNONI);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int versionAntigua, int versionNueva) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS tunoni");
        onCreate(sqLiteDatabase);
    }
}

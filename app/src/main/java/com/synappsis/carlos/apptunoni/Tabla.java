package com.synappsis.carlos.apptunoni;

import android.app.Activity;
        import android.content.res.Resources;
        import android.graphics.Paint;
        import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
        import android.widget.TableRow;
        import android.widget.TextView;

        import java.util.ArrayList;


public class Tabla
{
    // Variables de la clase

    private TableLayout tabla;          // Layout donde se pintará la tabla
    private ArrayList<TableRow> filas;  // Array de las filas de la tabla
    private Activity actividad;
    private Resources rs;
    private int FILAS, COLUMNAS;        // Filas y columnas de nuestra tabla
    String[] lista1 = {"Completo","Faltante","No entregado"};
    String[] lista2 = {"Excelente","Regular","Malo"};
    /**
     * Constructor de la tabla
     * @param actividad Actividad donde va a estar la tabla
     * @param tabla TableLayout donde se pintará la tabla
     */
    public Tabla(Activity actividad, TableLayout tabla)
    {
        this.actividad = actividad;
        this.tabla = tabla;
        rs = this.actividad.getResources();
        FILAS = COLUMNAS = 0;
        filas = new ArrayList<TableRow>();
    }

    /**
     * Añade la cabecera a la tabla
     * @param recursocabecera Recurso (array) donde se encuentra la cabecera de la tabla
     */
    public void agregarCabecera(int recursocabecera)
    {
        TableRow.LayoutParams layoutCelda;
        TableRow fila = new TableRow(actividad);
        TableRow.LayoutParams layoutFila = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        fila.setLayoutParams(layoutFila);

        String[] arraycabecera = rs.getStringArray(recursocabecera);
        COLUMNAS = arraycabecera.length;

        for(int i = 0; i < arraycabecera.length; i++)
        {
            TextView texto = new TextView(actividad);
            layoutCelda = new TableRow.LayoutParams(obtenerAnchoPixelesTexto(arraycabecera[i]), TableRow.LayoutParams.WRAP_CONTENT);
            texto.setText(arraycabecera[i]);
            texto.setGravity(Gravity.CENTER_HORIZONTAL);
            //texto.setTextAppearance(actividad, R.style.estilo_celda);
            //texto.setBackgroundResource(R.drawable.tabla_celda_cabecera);
            texto.setLayoutParams(layoutCelda);

            fila.addView(texto);
        }

        tabla.addView(fila);
        filas.add(fila);

        FILAS++;
    }

    /**
     * Agrega una fila a la tabla
     * @param elementos Elementos de la fila
     */
    public void agregarFilaTabla(ArrayList<String> elementos)
    {
        TableRow.LayoutParams layoutCelda;
        TableRow.LayoutParams layoutFila = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        TableRow fila = new TableRow(actividad);
        fila.setLayoutParams(layoutFila);
        for(int i = 0; i< elementos.size(); i++)
        {
            if(i==0)
            {
                Spinner list1 = new Spinner(actividad);
                list1.setAdapter(new ArrayAdapter<String>(actividad, android.R.layout.simple_spinner_dropdown_item, lista1));
                list1.setGravity(Gravity.CENTER_HORIZONTAL);
                layoutCelda = new TableRow.LayoutParams(sizeSpinner(), TableRow.LayoutParams.WRAP_CONTENT);
                list1.setLayoutParams(layoutCelda);
                fila.addView(list1);
            }
            else if(i==3)
            {
                Spinner list2 = new Spinner(actividad);
                list2.setAdapter(new ArrayAdapter<String>(actividad, android.R.layout.simple_spinner_dropdown_item, lista2));
                list2.setGravity(Gravity.CENTER_HORIZONTAL);
                layoutCelda = new TableRow.LayoutParams(sizeSpinner(), TableRow.LayoutParams.WRAP_CONTENT);
                list2.setLayoutParams(layoutCelda);
                fila.addView(list2);
            }
            else {
                TextView texto = new TextView(actividad);
                texto.setText(String.valueOf(elementos.get(i)));
                texto.setGravity(Gravity.CENTER_HORIZONTAL);

                layoutCelda = new TableRow.LayoutParams(sizeSpinner(), TableRow.LayoutParams.WRAP_CONTENT);
                texto.setLayoutParams(layoutCelda);
                fila.addView(texto);
            }
        }

        tabla.addView(fila);
        filas.add(fila);

        FILAS++;
    }



    public String obtenerDato(int index){
        String texto = "";
        TableRow.LayoutParams layoutFila = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        TableRow fila = new TableRow(actividad);
        fila.setLayoutParams(layoutFila);
        fila.getChildAt(1);
        if(fila!=null){
            Log.d("LISTA",fila.getChildCount()+"");
            Spinner mspinner = (Spinner) fila.getChildAt(0);
            String uno = mspinner.getSelectedItem().toString();
            TextView mTextView = (TextView) fila.getChildAt(1);
            String dos = mTextView.getText().toString();
            TextView mTextView2 = (TextView) fila.getChildAt(2);
            String tres= mTextView2.getText().toString();
            Spinner mspinner2 = (Spinner) fila.getChildAt(3);
            String cuatro = mspinner2.getSelectedItem().toString();
            //mspinner.getSelectedItem()
            texto = uno+dos+tres+cuatro;
        }
        else{
            texto="ERROR";
        }
                //mspinner.toString()+","+mTextView.toString()+","+mTextView2.toString()+","+mspinner2.toString();
        return texto;
    }
    /**
     * Elimina una fila de la tabla
     * @param indicefilaeliminar Indice de la fila a eliminar
     */
    public void eliminarFila(int indicefilaeliminar)
    {
        if( indicefilaeliminar > 0 && indicefilaeliminar < FILAS )
        {
            tabla.removeViewAt(indicefilaeliminar);
            FILAS--;
        }
    }

    /**
     * Devuelve las filas de la tabla, la cabecera se cuenta como fila
     * @return Filas totales de la tabla
     */
    public int getFilas()
    {
        return FILAS;
    }

    /**
     * Devuelve las columnas de la tabla
     * @return Columnas totales de la tabla
     */
    public int getColumnas()
    {
        return COLUMNAS;
    }

    /**
     * Devuelve el número de celdas de la tabla, la cabecera se cuenta como fila
     * @return Número de celdas totales de la tabla
     */
    public int getCeldasTotales()
    {
        return FILAS * COLUMNAS;
    }

    /**
     * Obtiene el ancho en píxeles de un texto en un String
     * @param texto Texto
     * @return Ancho en píxeles del texto
     */
    private int obtenerAnchoPixelesTexto(String texto)
    {
        Paint p = new Paint();
        Rect bounds = new Rect();
        p.setTextSize(50);

        p.getTextBounds(texto, 0, texto.length(), bounds);
        return bounds.width();
    }
    private int sizeSpinner()
    {
        DisplayMetrics metrics = new DisplayMetrics();
        actividad.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels; // ancho absoluto en pixels ;
        width=width/4;
        return width;
    }

}

package com.synappsis.carlos.apptunoni;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.synappsis.carlos.apptunoni.entidades.Entrega;
import com.synappsis.carlos.apptunoni.entidades.OperacionesBaseDatos;
import com.synappsis.carlos.apptunoni.entidades.Producto;
import com.synappsis.carlos.apptunoni.entidades.Usuario;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ViajesAsignados.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ViajesAsignados#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViajesAsignados extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ExpandableListView listViewVar;
    ExpandableListAdapter listAdapter;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    OperacionesBaseDatos datos = null;
    private int lastExpandedPosition = -1;
    private int grupActual = -1;

    private OnFragmentInteractionListener mListener;

    public ViajesAsignados() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ViajesAsignados newInstance(String param1, String param2) {
        ViajesAsignados fragment = new ViajesAsignados();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_viajes_asignados, container, false);
        listViewVar =(ExpandableListView)rootView.findViewById(R.id.listview);
        // preparing list data rootView
        final Button button = rootView.findViewById(R.id.asignarViaje);
        prepareListData();
        datos = OperacionesBaseDatos
                .obtenerInstancia(getContext());
        //new TareaPruebaDatos().execute();
        listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild);
        // setting list adapter
        listViewVar.setAdapter(listAdapter);
        listViewVar.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    listViewVar.collapseGroup(lastExpandedPosition);
                    //listAdapter.disableCheck(lastExpandedPosition, );
                    listAdapter.setGroupViewData(lastExpandedPosition,"0");
                }
                lastExpandedPosition = groupPosition;
                grupActual = groupPosition;
                //listAdapter.enableCheck(groupPosition, );
                listAdapter.setGroupViewData(groupPosition,"1");
            }
        });
        listViewVar.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                listAdapter.setGroupViewData(groupPosition,"0");
                grupActual = -1;
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(grupActual<0){
                    Toast.makeText(getContext(), "No ha seleccionado ninguna entrega", Toast.LENGTH_SHORT).show();
                }
                else{
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                    dialogo1.setTitle("Aviso");
                    dialogo1.setMessage("¿Continuar con el proceso de entrega?");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            Toast.makeText(getContext(), "Ha seleccionado: "+grupActual, Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            //Toast.makeText(getContext(), "Ha seleccionado: "+grupActual, Toast.LENGTH_SHORT).show();
                        }
                    });
                    dialogo1.show();

                }
            }
        });

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    /*
  * Preparing the list data
  */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Entrega F001 07/12/2017");
        listDataHeader.add("Entrega F002 10/12/2017");
        listDataHeader.add("Entrega F003 11/12/2017");

        // Adding child data
        List<String> top250 = new ArrayList<String>();
        top250.add("Estatus: En Proceso");
        top250.add("Dirección de Origen: Origen");
        top250.add("Fecha de Origen: #");
        top250.add("Nombre: Name");
        top250.add("Dirección de Destino: Destino");
        top250.add("Fecha de Destino: #");
        top250.add("Nombre Receptor: Name");
        top250.add("Información Adicional: ---");
        top250.add("boton");

        List<String> nowShowing = new ArrayList<String>();
        nowShowing.add("Estatus: En Proceso");
        nowShowing.add("Dirección de Origen: Origen");
        nowShowing.add("Fecha de Origen: #");
        nowShowing.add("Nombre: Name");
        nowShowing.add("Dirección de Destino: Destino");
        nowShowing.add("Fecha de Destino: #");
        nowShowing.add("Nombre Receptor: Name");
        nowShowing.add("Información Adicional: ---");
        nowShowing.add("boton");

        List<String> comingSoon = new ArrayList<String>();
        comingSoon.add("Estatus: En Proceso");
        comingSoon.add("Dirección de Origen: Origen");
        comingSoon.add("Fecha de Origen: #");
        comingSoon.add("Nombre: Name");
        comingSoon.add("Dirección de Destino: Destino");
        comingSoon.add("Fecha de Destino: #");
        comingSoon.add("Nombre Receptor: Name");
        comingSoon.add("Información Adicional: ---");
        comingSoon.add("boton");

        listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
        listDataChild.put(listDataHeader.get(1), nowShowing);
        listDataChild.put(listDataHeader.get(2), comingSoon);
    }
    /*TEST DE BASE DE DATOS*/
    public class TareaPruebaDatos extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // [INSERCIONES]
            try {
                datos.getDb().beginTransaction();
                //Consultar BD
                String user = "admin";
                List<String> results = new ArrayList<String>();
                Cursor test = datos.obtenerProducto(user);
                if (test != null ) {
                    if  (test.moveToFirst()) {
                        do {
                            String nameP = test.getString(test.getColumnIndex("Producto"));
                            int cantidad = test.getInt(test.getColumnIndex("Cantidad"));
                            results.add("" + nameP + ",Cantidad: " + cantidad);
                        }while (test.moveToNext());
                    }
                }
                Log.d("list", results.toString());
                datos.getDb().setTransactionSuccessful();
            } finally {
                datos.getDb().endTransaction();

            }
            // [QUERIES]
            //DatabaseUtils.dumpCursor(datos.obtenerProducto("F-001"));
            //Log.d("obtenerDocumentos", "obtenerDocumentos");
            DatabaseUtils.dumpCursor(datos.obtenerDocumentos("admin"));
            return null;
        }
    }
}

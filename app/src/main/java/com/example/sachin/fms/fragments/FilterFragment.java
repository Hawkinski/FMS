package com.example.sachin.fms.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sachin.fms.R;
import com.example.sachin.fms.adapterPackage.FilterAdapter;
import com.example.sachin.fms.dataSets.PriorityData;
import com.example.sachin.fms.dataSets.TaskListData;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FilterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FilterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilterFragment extends Fragment implements FilterAdapter.SetOnItemClick {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private HashMap<String, String> filterList;
    private HashMap<String, String> priorityList;
    private List<String> p_code_list;

    private RecyclerView rv1, rv2;
    private OnFragmentInteractionListener mListener;
    private String NameSpace = "http://tempuri.org/";
    private String Soap_Action = "http://tempuri.org/";
    private String URL = "http://192.168.1.46:8085/login.asmx";
    private SoapSerializationEnvelope envelope;
    private SoapObject request;
    private SoapPrimitive response;
    private HttpTransportSE transportSE;
    private ProgressDialog pdialog;
    private SharedPreferences sp;
    private List<PriorityData> data;

    public FilterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FilterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FilterFragment newInstance(String param1, String param2) {
        FilterFragment fragment = new FilterFragment();
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

        View rootView = inflater.inflate(R.layout.fragment_filter, container, false);
        rv1 = (RecyclerView) rootView.findViewById(R.id.list_2);
        pdialog = new ProgressDialog(getContext());
        pdialog.setMessage("Loading...");
        pdialog.setIndeterminate(true);
        pdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdialog.setCancelable(false);

        sp = getContext().getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE);


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

    @Override
    public void itemClick(View v, int position, boolean checked) {

    }

    public boolean parse(String xml) throws XmlPullParserException, IOException {

        ArrayList<PriorityData> products = null;
        PriorityData current = null;
        boolean isSuccess = false;
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();

        InputStream is = new ByteArrayInputStream(xml.getBytes());

        parser.setInput(is, "UTF-8");

        int eventType = parser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name = null;
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    products = new ArrayList<>();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equals("Table")) {
                        current = new PriorityData();
                    } else if (current != null) {
                        switch (name) {
                            case "PRDESC":
                                current.p_desc = parser.nextText();

                                break;
                            case "PRCODE":
                                current.p_code = parser.nextText();

                                break;
                            case "PRHRS":
                                current.p_hrs = parser.nextText();
                                break;

                        }

                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("Table") && current != null) {
                        assert products != null;
                        products.add(current);
                    }
            }
            eventType = parser.next();
            isSuccess = printProducts(products);

        }


        return isSuccess;
    }

    public boolean printProducts(ArrayList<PriorityData> xmlList) {

        data = new ArrayList<>();
        boolean isSuccess = false;
        for (PriorityData current : xmlList) {


            data.add(new PriorityData(current.p_desc, current.p_code, current.p_hrs));


            isSuccess = true;


        }


        return isSuccess;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class GetPriority extends AsyncTask<String, String, String> {

        boolean isSuccess = false;
        List<TaskListData> d = new ArrayList<>();

        public void onPreExecute() {
            pdialog.show();

        }

        public void onPostExecute(String s) {
            pdialog.dismiss();
            if (isSuccess) {
                priorityList = new HashMap<>();
                for (int i = 0; i < data.size(); i++) {
                    priorityList.put(data.get(i).p_code, data.get(i).p_desc);
                    p_code_list.add(data.get(i).p_code);
                }

                List<String> dataList = new ArrayList<>();
                dataList.add("0");
                dataList.add("PPM");
                dataList.add(sp.getString(getString(R.string.work_completed_code), ""));


                filterList = new HashMap<>();
                filterList.put("0", "All");
                filterList.put("PPM", "All PPM Task");
                filterList.put(sp.getString(getString(R.string.work_completed_code), ""), "All Completed Task");

                if (priorityList != null) {
                    for (int i = 0; i < priorityList.size(); i++) {
                        filterList.put(p_code_list.get(i), "All " + priorityList.get(p_code_list.get(i)));
                        dataList.add(p_code_list.get(i));

                    }
                }

                FilterAdapter adaper = new FilterAdapter(getContext(), filterList, dataList);
                rv1.setAdapter(adaper);
                rv1.setLayoutManager(new LinearLayoutManager(getContext()));

            } else {
                Toast.makeText(getContext(), "There is no data", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            PropertyInfo compcode;

            request = new SoapObject(NameSpace, "getPriority");


            compcode = new PropertyInfo();
            compcode.setName("compcode");
            compcode.setType(String.class);
            compcode.setValue(sp.getString(getString(R.string.company_code), ""));
            request.addProperty(compcode);


            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            transportSE = new HttpTransportSE(URL);
            transportSE.debug = true;
            //b = getIntent().getExtras();


            try {

                transportSE.call(Soap_Action + "getPriority", envelope);
                // SoapObject result_1 = (SoapObject) envelope.getResponse();
                //xml = result_1.toString();

                Log.e("HHH", transportSE.responseDump);

                if (transportSE.responseDump != null) {
                    isSuccess = parse(transportSE.responseDump);
                } else {
                    isSuccess = false;
                }


            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }


            return null;
        }
    }
}

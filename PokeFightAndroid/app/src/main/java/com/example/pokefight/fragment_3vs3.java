package com.example.pokefight;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ThreeVsThree#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThreeVsThree extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ThreeVsThree() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ThreeVsThree.
     */
    // TODO: Rename and change types and number of parameters
    public static ThreeVsThree newInstance(String param1, String param2) {
        ThreeVsThree fragment = new ThreeVsThree();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_three_vs_three, container, false);

        Button botonCambiarFragmento = view.findViewById(R.id.ChangeModeButton6vs6);
        botonCambiarFragmento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SixVsSix fragmento = new SixVsSix();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragmento)
                        .commit();
            }
        });
        return view;
    }
}
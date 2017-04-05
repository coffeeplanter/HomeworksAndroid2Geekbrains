package ru.geekbrains.android2.lesson21;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TabFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    private TextView textView;
    private PopupMenu popupMenu;

    public TabFragment() {
        // Required empty public constructor
    }

    /**
     * Метод для создания новых экземпляров этого фрагмента
     * с передачей им параметров.
     *
     * @param param1 Параметр 1.
     * @return новый экземпляр фрагмента класса TabFragment.
     */
    public static TabFragment newInstance(String param1) {
        TabFragment fragment = new TabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab, container, false);
        textView = (TextView) view.findViewById(R.id.text_view);
        if (mParam1 != null) {
            textView.setText(mParam1);
            Linkify.addLinks(textView, Linkify.ALL);
        }
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        initPopupMenu();
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.show();
            }
        });
        registerForContextMenu(textView);
        return view;
    }

    private void initPopupMenu() {
        popupMenu = new PopupMenu(getActivity(), textView);
        popupMenu.inflate(R.menu.activity_main_drawer);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ((MainActivity) getActivity()).menuClickHandler(item.getItemId());
                return true;
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}

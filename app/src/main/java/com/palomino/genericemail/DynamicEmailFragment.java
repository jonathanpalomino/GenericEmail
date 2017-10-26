package com.palomino.genericemail;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.palomino.genericemail.email.EmailMessage;
import com.palomino.genericemail.email.MessageAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DynamicEmailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DynamicEmailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DynamicEmailFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,MessageAdapter.MessageAdapterListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private String folderActual;
    MessageAdapter mAdaptador;
    private List<EmailMessage> mensajes = new ArrayList<>();
    private ActionMode actionMode;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ActionModeCallback actionModeCallback;
    private RecyclerView recyclerView;

    public DynamicEmailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DynamicEmailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DynamicEmailFragment newInstance(String param1, String param2) {
        DynamicEmailFragment fragment = new DynamicEmailFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_dynamic_email, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        mAdaptador = new MessageAdapter(getContext(), mensajes, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(rootView.getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdaptador);

        actionModeCallback = new ActionModeCallback();
        // show loader and fetch messages
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        reloadMessagesbyFolder(folderActual);
                    }
                }
        );

        // Inflate the layout for this fragment
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
    public void onRefresh() {
        reloadMessagesbyFolder(folderActual);
    }

    private void reloadMessagesbyFolder(String folderActual) {
        swipeRefreshLayout.setRefreshing(true);
        mensajes.clear();

        List<EmailMessage> men = new ArrayList<>();
        EmailMessage tmp = new EmailMessage();
        tmp.setFrom("prueba1@gmail.com");
        tmp.setImportant(true);
        tmp.setMessage("Mensaje1");
        tmp.setRead(false);
        tmp.setSubject("Asunto1");
        men.add(tmp);

        tmp = new EmailMessage();
        tmp.setFrom("prueba2@gmail.com");
        tmp.setImportant(true);
        tmp.setMessage("Mensaje2");
        tmp.setRead(false);
        tmp.setSubject("Asunto2");
        men.add(tmp);

        tmp = new EmailMessage();
        tmp.setFrom("prueba3@gmail.com");
        tmp.setImportant(true);
        tmp.setMessage("Mensaje3");
        tmp.setRead(false);
        tmp.setSubject("Asunto3");
        men.add(tmp);

        tmp = new EmailMessage();
        tmp.setFrom("prueba4@gmail.com");
        tmp.setImportant(true);
        tmp.setMessage("Mensaje4");
        tmp.setRead(false);
        tmp.setSubject("Asunto4");
        men.add(tmp);

        for(EmailMessage a : men){
            a.setColor(getRandomMaterialColor("400"));
            mensajes.add(a);
        }
        mAdaptador.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);

    }
    private int getRandomMaterialColor(String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array", getActivity().getPackageName());

        if (arrayId != 0) {
            TypedArray colors = getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }


    @Override
    public void onIconClicked(int position) {
        Toast.makeText(getContext(),"onIconClicked" ,Toast.LENGTH_SHORT).show();
        if (actionMode == null) {
           // actionMode = startSupportActionMode(actionModeCallback);
        }

        toggleSelection(position);
    }

    @Override
    public void onIconImportantClicked(int position) {
        Toast.makeText(getContext(),"onIconImportantClicked" ,Toast.LENGTH_SHORT).show();
        // Star icon is clicked,
        // mark the message as important
        EmailMessage message = mensajes.get(position);
        message.setImportant(!message.isImportant());
        mensajes.set(position, message);
        mAdaptador.notifyDataSetChanged();
    }

    @Override
    public void onMessageRowClicked(int position) {
        Toast.makeText(getContext(),"onMessageRowClicked" ,Toast.LENGTH_SHORT).show();
        // verify whether action mode is enabled or not
        // if enabled, change the row state to activated
        if (mAdaptador.getSelectedItemCount() > 0) {
            enableActionMode(position);
        } else {
            // read the message which removes bold from the row
            EmailMessage message = mensajes.get(position);
            message.setRead(true);
            mensajes.set(position, message);
            mAdaptador.notifyDataSetChanged();

            Toast.makeText(getContext(), "Read: " + message.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRowLongClicked(int position) {
        Toast.makeText(getContext(),"onRowLongClicked" ,Toast.LENGTH_SHORT).show();
        // long press is performed, enable action mode
        enableActionMode(position);
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
    private void toggleSelection(int position) {
        mAdaptador.toggleSelection(position);
        int count = mAdaptador.getSelectedItemCount();

        if (count == 0) {
            //actionMode.finish();
        } else {
            //actionMode.setTitle(String.valueOf(count));
            //actionMode.invalidate();
        }
    }
    private void enableActionMode(int position) {
        if (actionMode == null) {
           // actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }
    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
            // disable swipe refresh if action mode is enabled
            swipeRefreshLayout.setEnabled(false);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    // delete all the selected messages
                    //deleteMessages();
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdaptador.clearSelections();
            swipeRefreshLayout.setEnabled(true);
            actionMode = null;
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    mAdaptador.resetAnimationIndex();
                }
            });
        }
    }

}

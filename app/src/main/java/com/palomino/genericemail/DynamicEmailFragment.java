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
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private String folderActual;
    MessageAdapter mAdaptador;
    private ArrayList<EmailMessage> mensajes = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
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
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mensajes = (ArrayList<EmailMessage>) getArguments().getSerializable("LISTA_EMAILS");
            folderActual = getArguments().getString("ID_ITEM");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dynamic_email, container, false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        mAdaptador = new MessageAdapter(getContext(), mensajes, this);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(rootView.getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdaptador);

        // show loader and fetch messages
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        reloadMessagesbyFolder(folderActual,true);
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
        reloadMessagesbyFolder(folderActual,false);
    }

    private void reloadMessagesbyFolder(String folderActual,boolean primeravez) {
        swipeRefreshLayout.setRefreshing(true);
        if(!primeravez){
            mensajes = mListener.onRefrescarMensajes(folderActual);
            mAdaptador = new MessageAdapter(getContext(), mensajes, this);
            recyclerView.setAdapter(mAdaptador);
        }
        mAdaptador.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onIconClicked(int position) {
       // Toast.makeText(getContext(),"onIconClicked" ,Toast.LENGTH_SHORT).show();
        toggleSelection(position);
    }

    @Override
    public void onIconImportantClicked(int position) {
        //Toast.makeText(getContext(),"onIconImportantClicked" ,Toast.LENGTH_SHORT).show();
        // Star icon is clicked,
        // mark the message as important
        EmailMessage message = mensajes.get(position);
        message.setImportant(!message.isImportant());
        mensajes.set(position, message);
        mAdaptador.notifyDataSetChanged();
    }

    @Override
    public void onMessageRowClicked(int position) {
        //Toast.makeText(getContext(),"onMessageRowClicked" ,Toast.LENGTH_SHORT).show();
        // verify whether action mode is enabled or not
        // if enabled, change the row state to activated
        if (mAdaptador.getSelectedItemCount() > 0) {
            toggleSelection(position);
        } else {
            // read the message which removes bold from the row
            EmailMessage message = mensajes.get(position);
            message.setRead(true);
            mensajes.set(position, message);
            mAdaptador.notifyDataSetChanged();

            //Toast.makeText(getContext(), "Read: " + message.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRowLongClicked(int position) {
        //Toast.makeText(getContext(),"onRowLongClicked" ,Toast.LENGTH_SHORT).show();
        // long press is performed, enable action mode
        toggleSelection(position);
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
        void onFragmentInteraction(Uri uri);
        void onActionItemClicked(int posicionItem,int cantidadItems);
        void onFinalizarBarra();
        ArrayList<EmailMessage> onRefrescarMensajes(String folderActual);
    }

    private void toggleSelection(int position) {
        mAdaptador.toggleSelection(position);
        int count = mAdaptador.getSelectedItemCount();
        if (count == 0) {
            mAdaptador.clearSelections();
            swipeRefreshLayout.setEnabled(true);
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    mAdaptador.resetAnimationIndex();
                }
            });
            mListener.onFinalizarBarra();
        } else {
            mListener.onActionItemClicked(position,count);
        }
    }
    public void deleteMessages() {
        mAdaptador.resetAnimationIndex();
        List<Integer> selectedItemPositions =mAdaptador.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            mAdaptador.removeData(selectedItemPositions.get(i));
        }
        mAdaptador.notifyDataSetChanged();
        mAdaptador.clearSelections();
    }
    public void readMessages(boolean marcaLeido){
        mAdaptador.resetAnimationIndex();
        List<Integer> selectedItemPositions =mAdaptador.getSelectedItems();
        for(int i=0;i<selectedItemPositions.size();i++){
            int posicionActual =selectedItemPositions.get(i);
            EmailMessage message = mensajes.get(posicionActual);
            message.setRead(marcaLeido);
            mensajes.set(posicionActual, message);
        }
        mAdaptador.notifyDataSetChanged();
    }
    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);
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
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    mAdaptador.resetAnimationIndex();
                }
            });
        }
    }

}

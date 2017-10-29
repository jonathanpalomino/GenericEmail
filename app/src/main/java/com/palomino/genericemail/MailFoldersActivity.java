package com.palomino.genericemail;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;
import com.palomino.genericemail.email.EmailMessage;
import java.util.ArrayList;


public class MailFoldersActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,DynamicEmailFragment.OnFragmentInteractionListener {

    String folderActual;
    private ArrayList<EmailMessage> mensajes;
    String cantidadEmails="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_folders);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Menu men = navigationView.getMenu();
        men.add(0,0,1,"Elemento1");
        men.add(0,0,2,"Elemento2");
        men.setGroupCheckable(0, true, true);
        men.setGroupVisible(0, true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(cantidadEmails.equals("")){
            getMenuInflater().inflate(R.menu.mail_folders, menu);
            getSupportActionBar().setTitle("");
            // Associate searchable configuration with the SearchView
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView =  (SearchView) menu.findItem(R.id.action_find).getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setSubmitButtonEnabled(false);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Intent search = new Intent(MailFoldersActivity.this,SearchActivity.class);
                    search.putExtra("SEARCH",query);
                    startActivity(search);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return true;
                }
            });
        }
        else{
            getMenuInflater().inflate(R.menu.menu_action_mode, menu);
            getSupportActionBar().setTitle("Total "+cantidadEmails);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_find) {
           return true;
        }
        else if (id == R.id.action_delete){
            BorrarMensajes();
            Toast.makeText(this,"Borrar Emails",Toast.LENGTH_LONG).show();
            return true;
        }
        else if (id == R.id.action_open){
            Toast.makeText(this,"Abrir Emails",Toast.LENGTH_LONG).show();
            AbrirMensajes();
            return true;
        }
        else if (id == R.id.action_close){
            Toast.makeText(this,"Cerrar Emails",Toast.LENGTH_LONG).show();
            CerrarMensajes();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void BuscarMensajes() {

    }

    private void BorrarMensajes() {
        DynamicEmailFragment articleFrag = (DynamicEmailFragment)getSupportFragmentManager().findFragmentById(R.id.flContent);
        articleFrag.deleteMessages();
        cantidadEmails="";
        supportInvalidateOptionsMenu();
    }
    private void AbrirMensajes() {
        DynamicEmailFragment articleFrag = (DynamicEmailFragment)getSupportFragmentManager().findFragmentById(R.id.flContent);
        articleFrag.readMessages(true);
    }
    private void CerrarMensajes() {
        DynamicEmailFragment articleFrag = (DynamicEmailFragment)getSupportFragmentManager().findFragmentById(R.id.flContent);
        articleFrag.readMessages(false);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        String tituloItem = item.getTitle().toString();
        mensajes = GeneraMensajesEmail(tituloItem);
        invokeDynamicFragment(tituloItem,mensajes);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private ArrayList<EmailMessage> GeneraMensajesEmail2(String tituloItem) {
        ArrayList<EmailMessage> men = new ArrayList<>();

        EmailMessage tmp = new EmailMessage();
        tmp.setFrom("jonathan@gmail.com");
        tmp.setImportant(true);
        tmp.setMessage("Mensaje1 "+tituloItem);
        tmp.setRead(false);
        tmp.setSubject("Asunto1");
        tmp.setColor(getRandomMaterialColor("400"));
        men.add(tmp);

        tmp = new EmailMessage();
        tmp.setFrom("palomino@gmail.com");
        tmp.setImportant(true);
        tmp.setMessage("Mensaje2 "+tituloItem);
        tmp.setRead(false);
        tmp.setSubject("Asunto2");
        tmp.setColor(getRandomMaterialColor("400"));
        men.add(tmp);

        return men;
    }
    private ArrayList<EmailMessage> GeneraMensajesEmail(String tituloItem) {
        ArrayList<EmailMessage> men = new ArrayList<>();

        EmailMessage tmp = new EmailMessage();
        tmp.setFrom("prueba1@gmail.com");
        tmp.setImportant(true);
        tmp.setMessage("Mensaje1 "+tituloItem);
        tmp.setRead(false);
        tmp.setSubject("Asunto1");
        tmp.setColor(getRandomMaterialColor("400"));
        men.add(tmp);

        tmp = new EmailMessage();
        tmp.setFrom("Trueba2@gmail.com");
        tmp.setImportant(true);
        tmp.setMessage("Mensaje2 "+tituloItem);
        tmp.setRead(false);
        tmp.setSubject("Asunto2");
        tmp.setColor(getRandomMaterialColor("400"));
        men.add(tmp);

        tmp = new EmailMessage();
        tmp.setFrom("Erueba3@gmail.com");
        tmp.setImportant(true);
        tmp.setMessage("Mensaje3 "+tituloItem);
        tmp.setRead(false);
        tmp.setSubject("Asunto3");
        tmp.setColor(getRandomMaterialColor("400"));
        men.add(tmp);

        tmp = new EmailMessage();
        tmp.setFrom("Arueba4@gmail.com");
        tmp.setImportant(true);
        tmp.setMessage("Mensaje4 "+tituloItem);
        tmp.setRead(false);
        tmp.setSubject("Asunto4");
        tmp.setColor(getRandomMaterialColor("400"));
        men.add(tmp);

        return men;
    }
    private int getRandomMaterialColor(String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array", getPackageName());

        if (arrayId != 0) {
            TypedArray colors = getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }

    private void invokeDynamicFragment(String tituloItem,ArrayList<EmailMessage> listaMensajes) {
        Fragment fragment = null;
        Class fragmentClass = null;
        Bundle args = new Bundle();
        args.putString("ID_ITEM",tituloItem);
        args.putSerializable("LISTA_EMAILS",listaMensajes);

        fragmentClass = DynamicEmailFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            fragment.setArguments(args);//Asigno argumentos
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Toast.makeText(this,"onFragmentInteraction Invoca un Uri",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActionItemClicked(int posicionItem, int cantidadItems) {
        cantidadEmails=cantidadItems+"";
        supportInvalidateOptionsMenu();
    }

    @Override
    public void onFinalizarBarra() {
        cantidadEmails="";
        supportInvalidateOptionsMenu();
    }

    @Override
    public ArrayList<EmailMessage> onRefrescarMensajes(String folderActual) {
        mensajes = GeneraMensajesEmail2(folderActual);
        return mensajes;
    }

}

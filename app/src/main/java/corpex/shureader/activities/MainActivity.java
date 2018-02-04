package corpex.shureader.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;




import android.content.pm.ActivityInfo;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import corpex.shureader.R;
import corpex.shureader.adapters.ThreadAdapter;
import corpex.shureader.dataModels.ContentItem;
import corpex.shureader.dataModels.ThreadPage;
import corpex.shureader.fragments.ContentFragment;
import corpex.shureader.fragments.LoginFragment;
import corpex.shureader.fragments.ThreadFragment;
import corpex.shureader.utils.Constants;
import corpex.shureader.utils.WebConnections;


public class MainActivity extends AppCompatActivity implements LoginFragment.OnLoginFragmentListener, ContentFragment.OnContentFragmentListener, ThreadFragment.OnThreadFragmentListener{
    private FragmentManager gestor;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private WebConnections webConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gestor = getSupportFragmentManager();
        initViews();
        configureWebConnection();
    }

    private void configureWebConnection() {
        webConnection = new WebConnections(this);
    }

    private void initViews() {
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        loadFragment(Constants.FRAGMENT_CONTENT, null);
    }

    private void loadFragment(int fragment, Bundle argumentos) {
        switch (fragment) {
            case Constants.FRAGMENT_LOGIN:
                navigationView.setVisibility(View.GONE);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                gestor.beginTransaction().replace(R.id.flContent, new LoginFragment()).commitAllowingStateLoss();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR); //impide girar en ese fragmento
                break;
            case Constants.FRAGMENT_CONTENT:
                navigationView.setVisibility(View.VISIBLE);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                gestor.beginTransaction().replace(R.id.flContent, new ContentFragment()).commitAllowingStateLoss();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR); //impide girar en ese fragmento
                break;
            case Constants.FRAGMENT_THREAD:
                ThreadFragment threadFragment = new ThreadFragment();
                if(argumentos != null){
                    threadFragment.setArguments(argumentos);
                }
                navigationView.setVisibility(View.VISIBLE);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                gestor.beginTransaction().replace(R.id.flContent, threadFragment).commitAllowingStateLoss();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR); //impide girar en ese fragmento
                break;
            default:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR); //TODO: poner en resto de fragmentos
                break;
        }
    }

    /**
     private void initIndexRequest(String html) {
     Request request = new Request.Builder()
     .url("http://www.forocoches.com/foro/showthread.php?t=5240393")
     .build();

     try {
     Response response = client.newCall(request).execute();
     if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

     Headers responseHeaders = response.headers();
     for (int i = 0; i < responseHeaders.size(); i++) {
     System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
     }

     String resultado = response.body().string();
     resultado.charAt(1);
     } catch (IOException e) {
     e.printStackTrace();
     }

     }
     **/

    @Override
    public int loginRequest(String username, String password) {
        return webConnection.login(username, password);
    }

    //TODO: Cambiar el contentItem de nombre, ponerle ContentCategoryItem.
    //TODO: Crear una clase padre, ContentItem de la que hereden tanto categoria como el resto
    //TODO: Pasarselo por parametro
    @Override
    public ArrayList<ContentItem> getData(String url, int type) {
        switch(type) {
            case 0: //categoria
                return webConnection.getCategoryData(url);
        }
        return null;
    }

    @Override
    public ThreadPage getThreadPage(String url, int page) {
        url = url + "&page="+page;
        return webConnection.getThreadPage(url);
    }


    @Override
    public void openThreadFragment(String itemUrl, String itemName) {
        Bundle argumentos = new Bundle();
        argumentos.putString(Constants.ARG_URL, itemUrl);
        argumentos.putString(Constants.ARG_NAME, itemName);
        loadFragment(Constants.FRAGMENT_THREAD, argumentos);
    }

    @Override
    public void loadContentFragment() {
        loadFragment(Constants.FRAGMENT_CONTENT, null);
    }


    @Override
    public void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void onBackPressed() {
        loadFragment(Constants.FRAGMENT_CONTENT, null);
    }


}

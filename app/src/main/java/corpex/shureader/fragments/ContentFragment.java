package corpex.shureader.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import corpex.shureader.R;
import corpex.shureader.adapters.ContentAdapter;
import corpex.shureader.dataModels.ContentItem;

public class ContentFragment extends Fragment implements ContentAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    private OnContentFragmentListener mListener;
    private RecyclerView lstContent;
    private ContentAdapter mAdapter;
    private TextView mEmptyView;
    private LinearLayoutManager mLayoutManager;
    private ProgressBar pbAnillo;
    private SwipeRefreshLayout swlPanel;

    public ContentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_content, container, false);
        setupPanel(v);
        return v;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }



    private void initViews() {
        if(getView() != null) {
            Toolbar toolbar = (Toolbar) getView().findViewById(R.id.toolbar);
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

            mEmptyView = (TextView) getView().findViewById(R.id.lblEmpty);
            lstContent = (RecyclerView) getView().findViewById(R.id.lstContent);
            pbAnillo = (ProgressBar) getView().findViewById(R.id.pbAnillo);


            ImageView ivLogo = (ImageView) getView().findViewById(R.id.ivLogo);
            ivLogo.setVisibility(View.VISIBLE);
            Toolbar.LayoutParams layoutParams=new Toolbar.LayoutParams(200, 200);
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            ivLogo.setLayoutParams(layoutParams);

            getData();
        }
    }

    private void getData() {
        if (lstContent != null) {
            pbAnillo.setVisibility(View.VISIBLE);
            lstContent.setVisibility(View.GONE);
            final ArrayList<ContentItem> categoryData = new ArrayList<>();

            new AsyncTask<String,Void,ArrayList<ContentItem>>(){
                @Override
                protected ArrayList<ContentItem> doInBackground(String... params) {
                    return mListener.getData("http://www.forocoches.com/foro/forumdisplay.php?f=2", 0);
                }

                @Override
                protected void onPostExecute(ArrayList<ContentItem> result) {
                     categoryData.addAll(result); //gets General info
                    if(categoryData.size()  > 0) {
                        mAdapter = new ContentAdapter(categoryData); //gets General info
                        mAdapter.setOnItemClickListener(ContentFragment.this);
                        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                            @Override
                            public void onItemRangeInserted(int positionStart, int itemCount) {
                                super.onItemRangeInserted(positionStart, itemCount);
                                checkAdapterIsEmpty();
                            }

                            @Override
                            public void onItemRangeRemoved(int positionStart, int itemCount) {
                                super.onItemRangeRemoved(positionStart, itemCount);
                                checkAdapterIsEmpty();
                            }
                        });

                        pbAnillo.setVisibility(View.GONE);
                        lstContent.setVisibility(View.VISIBLE);
                        lstContent.setHasFixedSize(true);
                        lstContent.setAdapter(mAdapter);
                        checkAdapterIsEmpty();
                        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        lstContent.setLayoutManager(mLayoutManager);
                        lstContent.setItemAnimator(new DefaultItemAnimator());
                    }
                     super.onPostExecute(result);
                }
            }.execute();


        }
    }

    // Shows / hide emptyView if adapter is empty
    private void checkAdapterIsEmpty() {
        mEmptyView.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.INVISIBLE);
    }

    // Item click handler. It opens new fragment to show the selected thread
    @Override
    public void onItemClick(View view, ContentItem item, int position) {
        //Cargamos hilo seleccionado con la url que recuperamos
        mListener.openThreadFragment(item.getItemUrl(), item.getThreadTitle());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnContentFragmentListener) {
            mListener = (OnContentFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnContentFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mListener.openDrawer();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        getActivity().getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.action_search);

        //TODO: Menu deprecado =S buscar otra solucion
        /**MenuItemCompat.setOnActionExpandListener(searchMenuItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem menuItem) { return true; }
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem menuItem) { return true; }
                });
 **/

        super.onCreateOptionsMenu(menu, menuInflater);
    }


    //Configura el SwipeRefreshLayout.
    private void setupPanel(View v) {
        if (v != null) {
            swlPanel = (SwipeRefreshLayout) v.findViewById(R.id.swlPanel);
            // El fragmento actuará como listener del gesto de swipe.
            swlPanel.setOnRefreshListener(this);
            // Se establecen los colores que debe usar la animación.
            swlPanel.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
        }
    }


    @Override
    public void onRefresh() {
        refrescar();
    }

    private void refrescar() {
        // Se activa la animación.
        swlPanel.setRefreshing(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Se carga de nuevo los temas
                getData();
                //Se cancela la animación del panel.
                swlPanel.setRefreshing(false);
            }
        }, 1000);
    }


    public interface OnContentFragmentListener {
        void openDrawer();
        ArrayList<ContentItem> getData(String url, int type);

        void openThreadFragment(String itemUrl, String itemName);
    }



}

package corpex.shureader.fragments;


import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import corpex.shureader.R;
import corpex.shureader.adapters.ContentAdapter;
import corpex.shureader.adapters.ThreadAdapter;
import corpex.shureader.dataModels.ContentItem;
import corpex.shureader.dataModels.ThreadItem;
import corpex.shureader.dataModels.ThreadPage;
import corpex.shureader.utils.Constants;


public class ThreadFragment extends Fragment implements ThreadAdapter.OnItemClickListener {
    private OnThreadFragmentListener mListener;
    private RecyclerView lstThreads;
    private ThreadAdapter mAdapter;
    private TextView mEmptyView;
    private TextView tvNoPermission;
    private LinearLayoutManager mLayoutManager;
    private String threadUrl;
    private String threadName;
    private ProgressBar pbAnillo2;

    public ThreadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_thread, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    private void initViews() {
        if (getView() != null) {
            Toolbar toolbar = (Toolbar) getView().findViewById(R.id.toolbar);
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

            final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(getResources().getColor(R.color.claro), PorterDuff.Mode.SRC_ATOP);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(upArrow);
            mEmptyView = (TextView) getView().findViewById(R.id.lblEmpty);
            tvNoPermission = (TextView) getView().findViewById(R.id.lblNoPermission);

            lstThreads = (RecyclerView) getView().findViewById(R.id.lstThreads);
            pbAnillo2 = (ProgressBar) getView().findViewById(R.id.pbAnillo2);

            threadUrl = getArguments().getString(Constants.ARG_URL);
            threadName = getArguments().getString(Constants.ARG_NAME);
            toolbar.setTitle(threadName);

            confRecyclerView(1);
        }
    }


    private void confRecyclerView(final int page) {
        if (lstThreads != null) {
            pbAnillo2.setVisibility(View.VISIBLE);
            lstThreads.setVisibility(View.GONE);


            new AsyncTask<String,Void,ThreadPage>(){
                @Override
                protected ThreadPage doInBackground(String... params) {
                    return mListener.getThreadPage(threadUrl, page);
                }

                @Override
                protected void onPostExecute(ThreadPage result) {
                    if(result == null) {
                        result = new ThreadPage(1,1, new ArrayList<ThreadItem>());
                    }else if(result.getPosts() == null) {
                        result.setPosts(new ArrayList<ThreadItem>());
                    }
                    //categoryData.addAll(result); //gets General info
                    if(result.getPosts().size()  > 2) {
                        final ThreadPage finalResult = result;
                        mAdapter = new ThreadAdapter(result.getPosts(), result.getCurrentPageNumber(), result.getTotalPageNumber(), new ThreadAdapter.OnPagClickListener() {
                            @Override
                            public void paginationClick(int tipo) {
                                switch(tipo) {
                                    case Constants.BTN_FIRST:
                                        confRecyclerView(1);
                                        break;
                                    case Constants.BTN_BACK:
                                        confRecyclerView(finalResult.getCurrentPageNumber()-1);
                                        break;
                                    case Constants.BTN_NEXT:
                                        confRecyclerView(finalResult.getCurrentPageNumber()+1);
                                        break;
                                    case Constants.BTN_LAST:
                                        confRecyclerView(finalResult.getTotalPageNumber());
                                        break;
                                }
                            }
                        }); //gets General info
                        //mAdapter.setOnItemClickListener(ThreadFragment.this); TODO: ACTIVATE
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

                        pbAnillo2.setVisibility(View.GONE);
                        lstThreads.setVisibility(View.VISIBLE);
                        lstThreads.setHasFixedSize(true);
                        lstThreads.setAdapter(mAdapter);
                        checkAdapterIsEmpty();
                        mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                        lstThreads.setLayoutManager(mLayoutManager);
                        lstThreads.setItemAnimator(new DefaultItemAnimator());
                        mLayoutManager.scrollToPositionWithOffset(1, 0);
                    }
                    else if(result.getPosts().size() == 2) { //Sin permisos
                        pbAnillo2.setVisibility(View.GONE);
                        tvNoPermission.setVisibility(View.VISIBLE);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ContentFragment.OnContentFragmentListener) {
            mListener = (OnThreadFragmentListener) context;
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
    public void onItemClick(View view, ThreadItem item, int position) {
        //Cargamos hilo seleccionado con la url que recuperamos
        //mListener.openThreadFragment(item.getItemUrl());
        //TODO: Implementar un dialog que permita citar, etc?
    }


    public interface OnThreadFragmentListener {
        ThreadPage getThreadPage(String url, int page);
    }

}



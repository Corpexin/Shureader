package corpex.shureader.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.QuoteSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerInitListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import corpex.shureader.R;
import corpex.shureader.dataModels.ThreadItem;
import corpex.shureader.dataModels.ThreadPage;
import corpex.shureader.utils.Constants;
import corpex.shureader.utils.CustomQuoteSpan;

/**
 * Created with love by Corpex on 20/07/2017.
 */
public class ThreadAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_PAG = 0;
    private static final int TYPE_ITEM = 1;

    private final ArrayList<ThreadItem> mData;
    private int currentPage;
    private int totalPage;
    private OnItemClickListener onItemClickListener;
    private OnPagClickListener onPaginationClickListener;

    public ThreadAdapter(ArrayList<ThreadItem> mData, int currentPage, int totalPage, OnPagClickListener onPaginationClickListener) {
        this.mData = mData;
        this.currentPage = currentPage;
        this.totalPage = totalPage;
        this.onPaginationClickListener = onPaginationClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_PAG) {
            View itemView = LayoutInflater.from (parent.getContext ()).inflate (R.layout.thread_pag_item, parent, false);
            return new PagViewHolder(itemView);
        }else if(viewType == TYPE_ITEM){
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.thread_item, parent, false);
            final RecyclerView.ViewHolder viewHolder = new ThreadViewHolder(itemView);

            // ContentItem click handler
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, mData.get(viewHolder.getAdapterPosition()), viewHolder.getAdapterPosition());
                    }
                }
            });
            return viewHolder;
        }
        return null;
    }

    @Override
    public int getItemViewType (int position) {
        if(position == 0 || position == mData.size() - 1 ) { //todo: test this shit
            return TYPE_PAG;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder h, int position) {
        if(h instanceof ThreadViewHolder) {
            final ThreadViewHolder holder = (ThreadViewHolder) h;
            ThreadItem item = mData.get(position);
            holder.lblPostUsername.setText(item.getUsername());
            holder.lblPostUserDescription.setText(item.getUserDescrip());
            holder.lblPostDate.setText(item.getPostDate());
            Picasso.with(holder.imgAvatar.getContext()).load(item.getAvatarURL()).placeholder(holder.imgAvatar.getContext().getResources().getDrawable(R.drawable.usericon_s)).error(holder.imgAvatar.getContext().getResources().getDrawable(R.drawable.usericon_s)).into(holder.imgAvatar);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.lblPostContent.setText(Html.fromHtml(item.getPostContent(), Html.FROM_HTML_MODE_COMPACT));
            } else {
                holder.lblPostContent.setText(Html.fromHtml(item.getPostContent()));
            }

            Spannable spanned = (Spannable) Html.fromHtml(item.getPostContent(),
                    new Html.ImageGetter() {
                        @Override
                        public Drawable getDrawable(String source) {
                            LevelListDrawable d = new LevelListDrawable();
                            Drawable empty = holder.lblPostContent.getResources().getDrawable(R.drawable.placeholder);
                            d.addLevel(0, 0, empty);
                            d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());
                            new ImageGetterAsyncTask(holder.lblPostContent.getContext(), source, d).execute(holder.lblPostContent);

                            return d;
                        }
                    }, null);


            replaceQuoteSpans(spanned, item.getPostContent());
            clickableImages(spanned,  holder.lblPostContent.getContext());
            holder.lblPostContent.setText(spanned);
            holder.lblPostContent.setMovementMethod(LinkMovementMethod.getInstance()); //TODO: ?
        }
        else { //Paginacion
            final PagViewHolder holder = (PagViewHolder) h;
            if(totalPage == 1){
                holder.ivFirst.setVisibility(View.GONE);
                holder.ivNext.setVisibility(View.GONE);
                holder.ivBack.setVisibility(View.GONE);
                holder.ivLast.setVisibility(View.GONE);
            }else if(currentPage == 1){ //ocultamos los botones hacia atras
                holder.ivBack.setImageResource(R.drawable.back_disabled);
                holder.ivFirst.setImageResource(R.drawable.first_disabled);
                holder.ivBack.setEnabled(false);
                holder.ivFirst.setEnabled(false);
            }else if(currentPage == totalPage) { //ocultamos los botones hacia delante
                holder.ivNext.setImageResource(R.drawable.next_disabled);
                holder.ivLast.setImageResource(R.drawable.last_disabled);
                holder.ivNext.setEnabled(false);
                holder.ivLast.setEnabled(false);
            }

            holder.ivFirst.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if(onPaginationClickListener != null)
                        onPaginationClickListener.paginationClick(Constants.BTN_FIRST);
                }
            });

            holder.ivBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if(onPaginationClickListener != null)
                        onPaginationClickListener.paginationClick(Constants.BTN_BACK);
                }
            });

            holder.ivNext.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if(onPaginationClickListener != null)
                        onPaginationClickListener.paginationClick(Constants.BTN_NEXT);
                }
            });

            holder.ivLast.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if(onPaginationClickListener != null)
                        onPaginationClickListener.paginationClick(Constants.BTN_LAST);
                }
            });
            holder.tvPagina.setText(currentPage +" / "+totalPage);
        }
    }

    private void clickableImages(Spannable spanned, final Context context) {
        for (final ImageSpan span : spanned.getSpans(0, spanned.length(), ImageSpan.class)) {
            int flags = spanned.getSpanFlags(span);
            int start = spanned.getSpanStart(span);
            int end = spanned.getSpanEnd(span);

            spanned.setSpan(new URLSpan(span.getSource()) {
                @Override public void onClick(View v) {
                    final Dialog nagDialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar);
                    nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    nagDialog.setCancelable(false);
                    nagDialog.setContentView(R.layout.preview_image);
                    Button btnClose = (Button)nagDialog.findViewById(R.id.btnIvClose);

                    ImageView ivPreview = (ImageView)nagDialog.findViewById(R.id.iv_preview_image);
                    YouTubePlayerView youTubePlayerView = nagDialog.findViewById(R.id.youtube_player_view);

                    //Comprueba que sea un video de youtube o un imagen
                    if(span.getSource().startsWith("http://img.youtube.com/vi/")) {
                        //Implementacion de vista con reproductor para los videos. Deberia hacer un refactor del codigo ya mismo...
                        ivPreview.setVisibility(View.GONE);
                        youTubePlayerView.setVisibility(View.VISIBLE);
                        ((AppCompatActivity) context).getLifecycle().addObserver(youTubePlayerView);
                        youTubePlayerView.initialize(new YouTubePlayerInitListener() {
                            @Override
                            public void onInitSuccess(final YouTubePlayer initializedYouTubePlayer) {
                                initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                                    @Override
                                    public void onReady() {
                                        String videoId = splitIdVideo(span.getSource());
                                        initializedYouTubePlayer.loadVideo(videoId, 0);
                                    }
                                });
                            }
                        }, true);
                    }else{
                        ivPreview.setVisibility(View.VISIBLE);
                        youTubePlayerView.setVisibility(View.GONE);
                        Picasso.with(context).load(span.getSource()).placeholder(context.getResources().getDrawable(R.drawable.placeholder)).error(context.getResources().getDrawable(R.drawable.placeholder)).into(ivPreview);
                    }

                    btnClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            nagDialog.dismiss();
                        }
                    });
                    nagDialog.show();
                }
            }, start, end, flags);
        }
    }

    private String splitIdVideo(String url){
        String[] pr = url.split("/");
        return pr[4];
    }
    //Metodo para cambiar el color de fondo de las citas. Mejor no preguntar que hace =S
    private void replaceQuoteSpans(Spannable spannable, String spanned) {
        QuoteSpan[] quoteSpans = spannable.getSpans(0, spanned.length(), QuoteSpan.class);
        for (QuoteSpan quoteSpan : quoteSpans) {
            int start = spannable.getSpanStart(quoteSpan);
            int end = spannable.getSpanEnd(quoteSpan);
            int flags = spannable.getSpanFlags(quoteSpan);
            spannable.removeSpan(quoteSpan);
            spannable.setSpan(new CustomQuoteSpan(Color.rgb(43,62,69),1,1,1),start,end,flags);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setOnItemClickListener(ThreadAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    private static class ThreadViewHolder extends RecyclerView.ViewHolder {
        private final TextView lblPostUsername;
        private final TextView lblPostUserDescription;
        private final TextView lblPostDate;
        private final TextView lblPostContent;
        private final ImageView imgAvatar;



        ThreadViewHolder(View itemView) {
            super(itemView);
            lblPostUsername = (TextView) itemView.findViewById(R.id.lblPostUsername);
            lblPostUserDescription = (TextView) itemView.findViewById(R.id.lblPostUserDescription);
            lblPostDate = (TextView) itemView.findViewById(R.id.lblPostDate);
            lblPostContent = (TextView) itemView.findViewById(R.id.lblPostContent);
            imgAvatar = (ImageView) itemView.findViewById(R.id.imgAvatar);
        }

    }

    private static class PagViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivFirst;
        private final ImageView ivBack;
        private final ImageView ivNext;
        private final ImageView ivLast;
        private final TextView  tvPagina;


        PagViewHolder(View itemView) {
            super(itemView);
            ivFirst = (ImageView) itemView.findViewById(R.id.ivFirst);
            ivBack = (ImageView) itemView.findViewById(R.id.ivBack);
            ivNext = (ImageView) itemView.findViewById(R.id.ivNext);
            ivLast = (ImageView) itemView.findViewById(R.id.ivLast);
            tvPagina = (TextView) itemView.findViewById(R.id.tvPagina);
        }

    }

    @SuppressWarnings("UnusedParameters")
    public interface OnItemClickListener {
        void onItemClick(View view, ThreadItem item, int position);
    }

    public interface OnPagClickListener {
        void paginationClick(int tipo);
    }
}




class ImageGetterAsyncTask extends AsyncTask<TextView, Void, Bitmap> {


    private LevelListDrawable levelListDrawable;
    private Context context;
    private String source;
    private TextView t;

    public ImageGetterAsyncTask(Context context, String source, LevelListDrawable levelListDrawable) {
        this.context = context;
        this.source = source;
        this.levelListDrawable = levelListDrawable;
    }

    @Override
    protected Bitmap doInBackground(TextView... params) {
        t = params[0];

        //Check for inner images from forocoches
        if(source.startsWith("//")) {
        source = "https:" + source;
        }


        try {
            return Picasso.with(context).load(source).placeholder(context.getResources().getDrawable(R.drawable.placeholder)).error(context.getResources().getDrawable(R.drawable.placeholder)).get();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(final Bitmap bitmap) {
        try {
            LayerDrawable layerDrawable = null;
            Drawable[] imagenes = new Drawable[2];
            imagenes[0] = new BitmapDrawable(context.getResources(), bitmap);

            Point size = new Point();
            int altura;
            int anchura;

            //Check for video overlay
            if(source.startsWith("http://img.youtube.com/vi/")) {
                imagenes[1] = context.getResources().getDrawable(R.drawable.playvideo);
                layerDrawable = new LayerDrawable(imagenes);
            }


            ((Activity) context).getWindowManager().getDefaultDisplay().getSize(size);
            // Lets calculate the ratio according to the screen width in px

            //Modificacion para que la imagen no sobrepase el ancho del dispositivo
            if((size.x * 0.8f) / bitmap.getWidth() <= 1) { //En caso de que la imagen sea mayor que la pantalla, la anchura sera el ancho de la pantalla
                anchura = Math.round(size.x*0.8f);
                altura = Math.round(((size.x*0.8f)/bitmap.getWidth())*bitmap.getHeight());
            }else { //Si la imagen es menor necesitamos obtener el multiplicador
                float multiplicador = (size.x * 0.8f) / bitmap.getWidth();
                if(multiplicador > 3) //Para las imagenes muy pequeñas, solo aumentamos hasta 3 veces
                    multiplicador = 3;
                altura = Math.round(bitmap.getHeight() * multiplicador);
                anchura = Math.round(bitmap.getWidth() * multiplicador);
            }

            if(layerDrawable == null)
                levelListDrawable.addLevel(1, 1, imagenes[0]);
            else
                levelListDrawable.addLevel(1, 1, layerDrawable);
            // Set bounds width  and height according to the bitmap resized size
            levelListDrawable.setBounds(0, 0, anchura, altura);
            levelListDrawable.setLevel(1);
            t.setText(t.getText()); // invalidate() doesn't work correctly...

        } catch (Exception e) { /* Like a null bitmap, etc. */ }
    }


}
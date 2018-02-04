package corpex.shureader.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import corpex.shureader.R;
import corpex.shureader.dataModels.ContentItem;

/**
 * Created with love by Corpex on 28/01/2017.
 */

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {

    private final ArrayList<ContentItem> mData;
    private OnItemClickListener onItemClickListener;

    public ContentAdapter(ArrayList<ContentItem> data) {
        mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(itemView);

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


    @Override
    public void onBindViewHolder(ContentAdapter.ViewHolder holder, int position) {
        ContentItem item = mData.get(position);
        holder.lblThreadTitle.setText(item.getThreadTitle());
        holder.lblUserCreatorName.setText(item.getUserCreatorName());
        holder.lblThreadDescription.setText(item.getThreadDescription());
        holder.lblForumName.setText(item.getForumName());
        holder.lblThreadResponses.setText(item.getThreadResponses());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }





    //<!--.--!>


    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView lblThreadTitle;
        private final TextView lblUserCreatorName;
        private final TextView lblThreadDescription;
        private final TextView lblForumName;
        private final TextView lblThreadResponses;



        public ViewHolder(View itemView) {
            super(itemView);
            lblThreadTitle = (TextView) itemView.findViewById(R.id.lblThreadTitle);
            lblUserCreatorName = (TextView) itemView.findViewById(R.id.lblUserCreatorName);
            lblThreadDescription = (TextView) itemView.findViewById(R.id.lblThreadDescription);
            lblForumName = (TextView) itemView.findViewById(R.id.lblForumName);
            lblThreadResponses = (TextView) itemView.findViewById(R.id.lblThreadResponses);
        }

    }

    @SuppressWarnings("UnusedParameters")
    public interface OnItemClickListener {
        void onItemClick(View view, ContentItem item, int position);
    }
}

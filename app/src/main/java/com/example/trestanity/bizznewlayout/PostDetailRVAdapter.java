package com.example.trestanity.bizznewlayout;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Trestanity on 06/09/2017.
 */
public class PostDetailRVAdapter extends RecyclerView.Adapter<PostDetailRVAdapter.PostDetailViewHolder> {

    ArrayList<PostDetail> postDetails;
    Context context;

    SessionManager sessionManager;

    PostDetailRVAdapter(Context context, ArrayList<PostDetail> postDetails)
    {
        this.postDetails = postDetails;
        this.context = context;
        sessionManager = new SessionManager(context);
    }

    @Override
    public PostDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View postDetailView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_post, parent, false);
        PostDetailViewHolder postDetailViewHolder = new PostDetailViewHolder(postDetailView);

        return postDetailViewHolder;
    }

    @Override
    public void onBindViewHolder(PostDetailViewHolder holder, final int position) {

        holder.tvPostName.setText(postDetails.get(position).getPostPlaceName());
        holder.tvPostMessage.setText(postDetails.get(position).getPostMessage());
        holder.tvPostDate.setText(postDetails.get(position).getPostDate());

        holder.cvPostDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BizzDetails.class);
                intent.putExtra("BizzName", postDetails.get(position).getPostPlaceName());

                String selectedBizz = postDetails.get(position).getPostPlaceName();
                sessionManager.createBizzSession(selectedBizz);
                //tester
                //String click = dbList.get(position).bizz_name;
                //Toast.makeText(BizzRVAdapter.context, "you have clicked " + click, Toast.LENGTH_LONG).show();

                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {

        if(postDetails != null)
        {
            return postDetails.size();
        }

        return 0;
    }

    public static class PostDetailViewHolder extends RecyclerView.ViewHolder
    {
        CardView cvPostDetail;
        TextView tvPostName;
        TextView tvPostDate;
        TextView tvPostMessage;

        public PostDetailViewHolder(View itemView)
        {
            super(itemView);

            cvPostDetail = (CardView) itemView.findViewById(R.id.cvPostDetail);
            tvPostName = (TextView) itemView.findViewById(R.id.tvPostName);
            tvPostDate = (TextView) itemView.findViewById(R.id.tvPostDate);
            tvPostMessage = (TextView) itemView.findViewById(R.id.tvPostMessage);

        }
    }
}

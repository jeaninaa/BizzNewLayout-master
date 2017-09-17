package com.example.trestanity.bizznewlayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Trestanity on 05/03/2017.
 */
public class BizzRVAdapter extends RecyclerView.Adapter<BizzRVAdapter.BizzViewHolder> /*implements Filterable*/ {

    static   List<BizzData> dbList, filteredList;
    static  Context context;

    SessionManager sessionManager;

    BizzRVAdapter(Context context, List<BizzData> dbList)
    {
        this.dbList = new ArrayList<BizzData>();
        this.context = context;
        this.dbList = dbList;
        sessionManager = new SessionManager(context);
    }

    @Override
    public BizzRVAdapter.BizzViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_bizz, parent, false);
        BizzViewHolder pvh = new BizzViewHolder(v);
        return pvh;*/

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_bizz, parent, false);

        BizzViewHolder bizzViewHolder = new BizzViewHolder(itemLayoutView);

        return bizzViewHolder;

    }

    @Override
    public void onBindViewHolder(BizzRVAdapter.BizzViewHolder holder, final int position) {

        holder.tv_bizz_name.setText(dbList.get(position).bizz_name);
        holder.tv_bizz_landmark.setText(dbList.get(position).landmark);
        holder.tv_bizz_number.setText(dbList.get(position).celno);

        holder.cv_bizz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BizzDetails.class);
                intent.putExtra("BizzName", dbList.get(position).bizz_name);

                String selectedBizz = dbList.get(position).bizz_name;
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
        if(dbList != null)
        {
            return dbList.size();
        }
        return 0;
    }

//    @Override
//    public Filter getFilter() {
//        return new Filter() {
//
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//                dbList = (ArrayList<BizzData>) results.values;
//                BizzRVAdapter.this.notifyDataSetChanged();
//            }
//
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                ArrayList<BizzData> filteredResults = null;
//                if(constraint.length()==0)
//                {
//                    filteredResults = (ArrayList<BizzData>) filteredList;
//                }
//                else
//                {
//                    filteredResults = (ArrayList<BizzData>) getFilteredResults(constraint.toString().toLowerCase());
//                }
//
//                FilterResults results = new FilterResults();
//                results.values = filteredResults;
//
//                return results;
//
//            }
//        };
//    }
//
//    protected List<BizzData> getFilteredResults (String constraint)
//    {
//        ArrayList<BizzData> results = new ArrayList<>();
//        for(BizzData item : filteredList)
//        {
//            if(item.bizz_name.toLowerCase().contains(constraint) || item.landmark.toLowerCase().contains(constraint))
//            {
//                results.add(item);
//            }
//        }
//
//        return results;
//    }

    public void setFilter(List<BizzData> countryModels) {
        filteredList = new ArrayList<>();
        filteredList.addAll(countryModels);
        notifyDataSetChanged();
    }

    public static class BizzViewHolder extends RecyclerView.ViewHolder{
        CardView cv_bizz;
        TextView tv_bizz_name;
        TextView tv_bizz_landmark;
        TextView tv_bizz_number;

        public BizzViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            cv_bizz = (CardView) itemView.findViewById(R.id.cv_bizz);
            tv_bizz_name = (TextView) itemView.findViewById(R.id.bizz_name);
            tv_bizz_landmark = (TextView) itemView.findViewById(R.id.bizz_landmark);
            tv_bizz_number = (TextView) itemView.findViewById(R.id.bizz_number);

            //itemLayoutView.setOnClickListener(this);

        }


        /*@Override
        public void onClick(View v) {

            Intent intent = new Intent(context,BizzDetails.class);

            Bundle extras = new Bundle();
            extras.putInt("position",getLayoutPosition());
            intent.putExtras(extras);

            context.startActivity(intent);
            Toast.makeText(BizzRVAdapter.context, "you have clicked Row " + getAdapterPosition(), Toast.LENGTH_LONG).show();

        }*/
    }
}

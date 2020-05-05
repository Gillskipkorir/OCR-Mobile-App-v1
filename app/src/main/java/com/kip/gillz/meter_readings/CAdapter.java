package com.kip.gillz.meter_readings;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class CAdapter extends RecyclerView.Adapter<CAdapter.ProductViewHolder> {
    private Context mCtx;
    private List<Cllient> CList;
    private FragmentManager supportFragmentManager;
    private Dialog dialog;
    PreferenceManager prefManager;

    public CAdapter(Context mCtx, List<Cllient> CList) {
        this.mCtx = mCtx;
        this.CList = CList;
        dialog = new Dialog(mCtx);
    }
    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.item_people_chat, null);
        return new ProductViewHolder(view);

    }
    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {

        String xyz="";
        final Cllient client = CList.get(position);
        holder.tvname.setText(position+1+" | "+client.getFname());
        holder.tvmeterno.setText(String.valueOf(client.getMeterno()));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mCtx,Scanner.class);


                intent.putExtra("meternumber", String.valueOf(client.getMeterno()) );// sent value to the next activity
                intent.putExtra("firstname", client.getFname() );// sent value to the next activity


                mCtx.startActivity(intent);

            }
        });
    }
    @Override
    public int getItemCount() {
        return CList.size();
    }
    public FragmentManager getSupportFragmentManager() {
        return supportFragmentManager;
    }
    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvname,tvmeterno;
        CardView cardView;
        public ProductViewHolder(View itemView) {
            super(itemView);
            tvname = itemView.findViewById(R.id.name);
            tvmeterno = itemView.findViewById(R.id.meterno);
            cardView= itemView.findViewById(R.id.card);
        }
    }

    //This method will filter the list
    //here we are passing the filtered data
    //and assigning it to the list with notifydatasetchanged method
    /*public void filterList(ArrayList<String> filterdNames) {
        this.meter_no = filterdNames;
        notifyDataSetChanged();
    }*/
}
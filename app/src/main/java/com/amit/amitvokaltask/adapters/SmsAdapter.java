package com.amit.amitvokaltask.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amit.amitvokaltask.R;
import com.amit.amitvokaltask.model.SmsItem;

import java.util.ArrayList;

/**
 * Created by readyassist on 2/24/18.
 */

public class SmsAdapter extends RecyclerView.Adapter<SmsAdapter.MyViewHolder>{

    private Context context = null;

    private ArrayList<SmsItem> smsItemArrayList;

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNumber,tvMsg;

        MyViewHolder(View itemView) {
            super(itemView);
            tvNumber = (TextView) itemView.findViewById(R.id.tvNumber);
            tvMsg = (TextView)itemView.findViewById(R.id.tvMsg);
        }

        void bind(final int position, final SmsItem smsItem) {

            tvNumber.setText(smsItem.getNumber());
            tvMsg.setText(smsItem.getMessage());
        }
    }


    public SmsAdapter(Context context,ArrayList<SmsItem> smsItemArrayList) {
        this.smsItemArrayList = smsItemArrayList;
        this.context = context;
    }

    @Override
    public SmsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sms_item, parent, false);

        return new SmsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SmsAdapter.MyViewHolder holder, final int position) {
        holder.bind(position,smsItemArrayList.get(position));

    }

    @Override
    public int getItemCount() {
        return smsItemArrayList.size();
    }


}
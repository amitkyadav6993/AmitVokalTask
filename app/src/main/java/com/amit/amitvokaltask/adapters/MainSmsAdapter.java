package com.amit.amitvokaltask.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amit.amitvokaltask.R;
import com.amit.amitvokaltask.model.MainSMSItem;
import com.amit.amitvokaltask.model.SmsItem;

import java.util.ArrayList;

/**
 * Created by readyassist on 2/24/18.
 */

public class MainSmsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private Context context;
    private ArrayList<MainSMSItem> mainSMSItemArrayList;

    private boolean isLoadingAdded = false;

    public MainSmsAdapter(Context context) {
        this.context = context;
        mainSMSItemArrayList = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                View viewItem = inflater.inflate(R.layout.main_sms_item, parent, false);
                viewHolder = new SMSVH(viewItem);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(viewLoading);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        MainSMSItem mainSMSItem = mainSMSItemArrayList.get(position);

        switch (getItemViewType(position)) {

            case ITEM:
                final SMSVH smsvh = (SMSVH) holder;
                smsvh.tvHours.setText(mainSMSItem.getHours());
                smsvh.rvSMS.setAdapter(null);

                SmsAdapter smsAdapter = new SmsAdapter(context,mainSMSItem.getSmsItemArrayList());
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context.getApplicationContext());
                smsvh.rvSMS.setLayoutManager(layoutManager);
                smsvh.rvSMS.setAdapter(smsAdapter);

               break;

            case LOADING:
                LoadingVH loadingVH = (LoadingVH) holder;
                loadingVH.mProgressBar.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mainSMSItemArrayList == null ? 0 : mainSMSItemArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == mainSMSItemArrayList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }


    private void add(MainSMSItem mainSMSItem) {
        int previousContentSize = mainSMSItemArrayList.size();
        if (mainSMSItemArrayList.size()>0){
            int pstn = mainSMSItemArrayList.size()-1;
            String last = mainSMSItemArrayList.get(pstn).getHours();
            if (last.equalsIgnoreCase(mainSMSItem.getHours())){
                mainSMSItemArrayList.get(pstn).getSmsItemArrayList().addAll(mainSMSItem.getSmsItemArrayList());
            }else {
                mainSMSItemArrayList.add(mainSMSItem);
            }
        }else {
            mainSMSItemArrayList.add(mainSMSItem);
        }

        notifyItemRangeInserted(previousContentSize, mainSMSItemArrayList.size() - previousContentSize);
        //notifyItemInserted(mainSMSItemArrayList.size()-1);

    }

    public void addAll(ArrayList<MainSMSItem> smsResults) {
        for (MainSMSItem result : smsResults) {
            add(result);
        }
    }

    private void remove(MainSMSItem mainSMSItem) {
        int position = mainSMSItemArrayList.indexOf(mainSMSItem);
        if (position > -1) {
            mainSMSItemArrayList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new MainSMSItem());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = mainSMSItemArrayList.size() - 1;
        MainSMSItem mainSMSItem = getItem(position);

        if (mainSMSItem != null) {
            mainSMSItemArrayList.remove(position);
            notifyItemRemoved(position);
        }
    }

    private MainSMSItem getItem(int position) {
        return mainSMSItemArrayList.get(position);
    }


    private class SMSVH extends RecyclerView.ViewHolder {
        private TextView tvHours;
        private RecyclerView rvSMS;

        private SMSVH(View itemView) {
            super(itemView);

            tvHours = (TextView) itemView.findViewById(R.id.tvHours);
            rvSMS = (RecyclerView) itemView.findViewById(R.id.rvSMS);
        }
    }


    private class LoadingVH extends RecyclerView.ViewHolder{
        private ProgressBar mProgressBar;

        private LoadingVH(View itemView) {
            super(itemView);

            mProgressBar = (ProgressBar) itemView.findViewById(R.id.prgLoading);
        }
    }

}

package com.amit.amitvokaltask.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.amit.amitvokaltask.R;
import com.amit.amitvokaltask.adapters.MainSmsAdapter;
import com.amit.amitvokaltask.model.MainSMSItem;
import com.amit.amitvokaltask.model.SmsItem;
import com.amit.amitvokaltask.utils.ScrollListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class BaseActivity extends AppCompatActivity {

    private RecyclerView rvMainSMS;
    private LinearLayoutManager linearLayoutManager;
    private ProgressBar prgLoading;

    private static final int TYPE_INCOMING_MESSAGE = 1;
    private ArrayList<MainSMSItem> savedMainSMSItemArrayList,tempSMSItemArrayList;

    MainSmsAdapter mainSmsAdapter;

    private static final int PAGE_START = 0;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 5;
    private int currentPage = TOTAL_PAGES;

    private FetchSmsThread fetchSmsThread;
    private int currentCount = 0;

    public static int PERMISSION_REQUEST_CODE = 12345;
    String[] PERMISSIONS = {Manifest.permission.READ_SMS};

    int readedSms = 1;
    private CustomHandler customHandler;

    boolean isFirst = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        prgLoading = (ProgressBar)findViewById(R.id.prgLoading);
        rvMainSMS = (RecyclerView)findViewById(R.id.rvMainSMS);

        savedMainSMSItemArrayList = new ArrayList<>();
        tempSMSItemArrayList = new ArrayList<>();

        customHandler = new CustomHandler(this);

        mainSmsAdapter = new MainSmsAdapter(this);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvMainSMS.setLayoutManager(linearLayoutManager);

        if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED) {
            init();
        }else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
        }

    }

    private void init() {

        rvMainSMS.setAdapter(mainSmsAdapter);

        rvMainSMS.addOnScrollListener(new ScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                isFirst = false;
                startThread();
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        fetchInboxMessages();

    }

    private void loadPages() {

        stopThread();
        prgLoading.setVisibility(View.GONE);
        if (isFirst){
            if (savedMainSMSItemArrayList.size()!=0){
                mainSmsAdapter.addAll(savedMainSMSItemArrayList);
                mainSmsAdapter.addLoadingFooter();
            }else {
                isLastPage = true;
            }

        }else {
            mainSmsAdapter.removeLoadingFooter();
            isLoading = false;
            if (savedMainSMSItemArrayList.size()!=0){
                mainSmsAdapter.addAll(savedMainSMSItemArrayList);
                mainSmsAdapter.addLoadingFooter();
            }else {
                isLastPage = true;
            }
        }
    }

    private void fetchInboxMessages() {
        prgLoading.setVisibility(View.VISIBLE);
        isFirst = true;
        startThread();
    }


    public synchronized void startThread() {

        if (fetchSmsThread == null) {
            fetchSmsThread = new FetchSmsThread(currentCount);
            fetchSmsThread.start();
        }else {
            fetchSmsThread.start();
        }
    }

    public synchronized void stopThread() {
        if (fetchSmsThread != null) {
            FetchSmsThread moribund = fetchSmsThread;
            currentCount = fetchSmsThread.tag == 0 ? 1 : 0;
            fetchSmsThread = null;
            moribund.interrupt();
        }
    }

    public class FetchSmsThread extends Thread {

        public int tag = -1;

        public FetchSmsThread(int tag) {
            this.tag = tag;
        }

        @Override
        public void run() {
            savedMainSMSItemArrayList.clear();
            savedMainSMSItemArrayList = fetchInboxSms(TYPE_INCOMING_MESSAGE);
            tempSMSItemArrayList.addAll(savedMainSMSItemArrayList);
            customHandler.sendEmptyMessage(0);
        }

    }

    public ArrayList<MainSMSItem> fetchInboxSms(int type) {

        int count = 0;

        long timeutil = System.currentTimeMillis();
        Date crntDate = new Date(timeutil);

        ArrayList<MainSMSItem> mainSMSItems = new ArrayList<>();

        Uri uriSms = Uri.parse("content://sms");

        Cursor cursor = this.getContentResolver().query(uriSms,
                        new String[] { "_id", "address", "date", "body",
                                "type", "read" }, "type=" + type, null,
                        "date" + " COLLATE LOCALIZED ASC");
        if (cursor != null) {

            readedSms++;
            int totalSMS = cursor.getCount();
            int readingSms = totalSMS-readedSms;

            if (readingSms>0){
                cursor.moveToPosition(readingSms);

                do {

                    String smsdate = cursor.getString(cursor.getColumnIndex("date"));
                    Date smsDate = new Date(Long.parseLong(smsdate));

                    long diffrence = crntDate.getTime() - smsDate.getTime();
                    long hrs = TimeUnit.MILLISECONDS.toHours(diffrence);

                    String hours = hrs+"  hours ago";
                    String number = cursor.getString(cursor.getColumnIndex("address"));
                    String msg = cursor.getString(cursor.getColumnIndex("body"));

                    int tempSize = tempSMSItemArrayList.size();
                    if (tempSize>0){
                        String tempHrs = tempSMSItemArrayList.get(tempSize-1).getHours();
                        tempHrs = tempHrs.replace("  hours ago","");
                        long temphrs = Long.parseLong(tempHrs);
                        if (temphrs<=hrs){
                            if (mainSMSItems.size()==0){
                                ArrayList<SmsItem> smsItems = new ArrayList<>();
                                smsItems.add(new SmsItem(smsdate,number,msg));
                                mainSMSItems.add(new MainSMSItem(hours,smsItems));
                                count++;
                            }else {
                                int size = mainSMSItems.size();
                                String lsthr = mainSMSItems.get(size-1).getHours();
                                if (lsthr.equalsIgnoreCase(hours)){
                                    mainSMSItems.get(size-1).getSmsItemArrayList().add(new SmsItem(smsdate,number,msg));
                                }else {
                                    ArrayList<SmsItem> smsItems = new ArrayList<>();
                                    smsItems.add(new SmsItem(smsdate,number,msg));
                                    mainSMSItems.add(new MainSMSItem(hours,smsItems));
                                    count++;
                                }
                            }
                        }
                    }else {
                        if (mainSMSItems.size()==0){
                            ArrayList<SmsItem> smsItems = new ArrayList<>();
                            smsItems.add(new SmsItem(smsdate,number,msg));
                            mainSMSItems.add(new MainSMSItem(hours,smsItems));
                            count++;
                        }else {
                            int size = mainSMSItems.size();
                            String lsthr = mainSMSItems.get(size-1).getHours();
                            if (lsthr.equalsIgnoreCase(hours)){
                                mainSMSItems.get(size-1).getSmsItemArrayList().add(new SmsItem(smsdate,number,msg));
                            }else {
                                ArrayList<SmsItem> smsItems = new ArrayList<>();
                                smsItems.add(new SmsItem(smsdate,number,msg));
                                mainSMSItems.add(new MainSMSItem(hours,smsItems));
                                count++;
                            }
                        }
                    }

                    readedSms++;
                    if (count<TOTAL_PAGES){
                        cursor.moveToPrevious();
                    }else {
                        currentPage = currentPage+count;
                        return mainSMSItems;
                    }

                } while (cursor.moveToPrevious());

            }

        }

        currentPage = currentPage+count;
        return mainSMSItems;

    }

    static class CustomHandler extends Handler {
        private final WeakReference<BaseActivity> activityHolder;

        CustomHandler(BaseActivity inboxListActivity) {
            activityHolder = new WeakReference<BaseActivity>(inboxListActivity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {

            BaseActivity inboxListActivity = activityHolder.get();
            if (inboxListActivity.fetchSmsThread != null && inboxListActivity.currentCount == inboxListActivity.fetchSmsThread.tag) {
                Log.i("received result", "received result");
                inboxListActivity.fetchSmsThread = null;
                inboxListActivity.loadPages();
                //inboxListActivity.mainSmsAdapter.removeLoadingFooter();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED) {
                init();
            }else {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
            }
        }
    }
}

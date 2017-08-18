package com.auto.watchmen;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.auto.watchmen.bean.BollInfo;
import com.auto.watchmen.bean.DataInfo;
import com.auto.watchmen.bean.ReportInfo;
import com.auto.watchmen.db.DataInfoDb;
import com.auto.watchmen.util.Analytic;
import com.auto.watchmen.util.DataUtils;
import com.auto.watchmen.util.HomeBiz;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class HomeActivity extends Activity {

    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        HomeBiz.getInstance(this).start();
        Log.d("big", "date:" + mSimpleDateFormat.format(new Date(System.currentTimeMillis())));

        new Thread() {
            @Override
            public void run() {
                float current = 1;
                long time = Long.parseLong("1482826288494");
                for (int i = 0; i < 240; i++) {
                    DataInfo info = new DataInfo();
                    info.setName("test");
                    info.setBegin(current);
                    current += 10 + Math.random();
                    info.setEnd(current);
                    info.setMax(Math.max((float) (current + Math.random()), current));
                    info.setMin((float) (current - Math.random()));
                    info.setTime(time);
                    time += 60 * 60 * 1000;
                    DataInfoDb.getInstance(HomeActivity.this).addRecord(info);
                }
                ArrayList<DataInfo> dateInfos = DataInfoDb.getInstance(HomeActivity.this).getWeekRecord();
                for (DataInfo info : dateInfos) {
                    Log.d("big", "info:" + info.toString());
                }
                getData();
            }
        }.start();

    }


    private RecyclerView mRecyclerView;
    private MyAdapter myAdapter;

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.home_recyler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        myAdapter = new MyAdapter();
        mRecyclerView.setAdapter(myAdapter);
        /**test**/
        ArrayList<ReportInfo> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ReportInfo info = new ReportInfo();
            info.setName("TEST_" + i);
            list.add(info);
        }
        myAdapter.setData(list);
    }

    public void getData() {
        List<DataInfo> totalNames = DataInfoDb.getInstance(HomeActivity.this).getTotalRecordName();
        for (DataInfo name : totalNames) {
            if (TextUtils.isEmpty(name.getName())) continue;
            List<DataInfo> list = DataInfoDb.getInstance(HomeActivity.this).getRecord(name.getName());
            List<DataInfo> weekList = Analytic.getWeekDataInfo(list);
            HashMap<Long, BollInfo> weekBolls = Analytic.getBollInfo(99, weekList);
            Analytic.getMacdInfo(1, 1, 1, weekList);
            HashMap<Long, BollInfo> bolls = Analytic.getBollInfo(99, list);
            Analytic.getMacdInfo(1, 1, 1, list);
            Log.d("big", "names:" + name.getName() + ",list:" + list.size() + ",week:" + weekList.size());
//        Analytic.checkBollTrend(list,)
        }

    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private ArrayList<ReportInfo> mList;

        public void setData(ArrayList<ReportInfo> list) {
            this.mList = list;
            notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View content = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_home, viewGroup, false);
            return new MyViewHolder(content);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int i) {
            ReportInfo info = mList.get(i);
            holder.name.setText(info.getName());
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView name;
            View boll;
            View macd;
            View weekBoll;
            View weekMacd;

            public MyViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.item_name);
                boll = itemView.findViewById(R.id.item_boll);
                macd = itemView.findViewById(R.id.item_macd);
                weekBoll = itemView.findViewById(R.id.item_week_boll);
                weekMacd = itemView.findViewById(R.id.item_week_macd);
            }
        }

    }

}

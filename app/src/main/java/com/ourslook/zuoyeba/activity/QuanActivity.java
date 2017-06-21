package com.ourslook.zuoyeba.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.R;

import butterknife.Bind;
public class QuanActivity extends BaseActivity {

    @Bind(R.id.quan_list)
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithDefaultTitle(R.layout.activity_quan, "免费课券列表");

    }

    @Override
    protected void initView() {
        initListData();
        mIvTitleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(HomeActivity.class);
                finish();
            }
        });

    }

    private void initListData() {
        String[] datas = {getString(R.string.quan_detail_message)};
        QuanAdapter quanAdapter = new QuanAdapter(QuanActivity.this,datas);
        mListView.setAdapter(quanAdapter);
    }

    @Override
    public void onBackPressed() {
        openActivity(HomeActivity.class);
        finish();
    }

    class QuanAdapter extends BaseAdapter{
        private String[] datas;
        private Context context;

        public QuanAdapter(Context context, String[] datas) {
            this.datas = datas;
            this.context = context;
        }

        @Override
        public int getCount() {
            return datas.length;
        }

        @Override
        public Object getItem(int position) {
            return datas[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder ;
            if(convertView == null){
                viewHolder =new ViewHolder();
                convertView = LayoutInflater.from(QuanActivity.this).inflate(R.layout.item_quan_message, parent, false);
                viewHolder.message = (TextView) convertView.findViewById(R.id.quan_message);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.message.setText(datas[position]);
            return convertView;
        }

    }
    static class ViewHolder
    {
        public TextView message;
    }


}

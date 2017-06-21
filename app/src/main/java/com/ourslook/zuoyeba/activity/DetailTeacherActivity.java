package com.ourslook.zuoyeba.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ourslook.zuoyeba.BaseActivity;
import com.ourslook.zuoyeba.Constants;
import com.ourslook.zuoyeba.R;
import com.ourslook.zuoyeba.adapter.PickUpTeacherListAdapter;
import com.ourslook.zuoyeba.model.OrderTeacherModel;

import java.util.ArrayList;

public class DetailTeacherActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    TextView leftTime;
    TextView teacherCount;
    ListView listView;

    private ArrayList<OrderTeacherModel> list = new ArrayList<OrderTeacherModel>();
    private PickUpTeacherListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewWithDefaultTitle(R.layout.activity_detail_teacher, "选择老师");
    }

    @Override
    protected void initView() {
        listView =(ListView) findViewById(R.id.detail_list_view);
        list = (ArrayList<OrderTeacherModel>) getIntent().getSerializableExtra(Constants.TEACHER_LIST);
        adapter = new PickUpTeacherListAdapter(list, this, null);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        OrderTeacherModel orderTeacherModel = list.get(position);
        long teacherid = orderTeacherModel.getId();
        Intent intent = new Intent(this, TeacherInfoActivity.class);
        intent.putExtra("teacherid", teacherid);
        startActivity(intent);
    }
}

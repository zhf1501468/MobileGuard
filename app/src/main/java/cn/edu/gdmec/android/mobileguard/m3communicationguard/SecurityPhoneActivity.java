package cn.edu.gdmec.android.mobileguard.m3communicationguard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.edu.gdmec.android.mobileguard.R;
import cn.edu.gdmec.android.mobileguard.m3communicationguard.adapter.BlackContactAdapter;
import cn.edu.gdmec.android.mobileguard.m3communicationguard.db.dao.BlackNumberDao;
import cn.edu.gdmec.android.mobileguard.m3communicationguard.entity.BlackContactInfo;

public class SecurityPhoneActivity extends AppCompatActivity implements View.OnClickListener {
    private FrameLayout mHaveBlackNumber;
    private FrameLayout mNoBlcakNumber;
    private BlackNumberDao dao;
    private ListView mListView;
    private int pagenumber = 0;
    private int pagesize = 10;
    private int totalNumber;
    private List<BlackContactInfo> pageBlackNumber = new ArrayList<BlackContactInfo>();
    private BlackContactAdapter adapter;

    private void fillData(){
        dao = new BlackNumberDao(SecurityPhoneActivity.this);
        totalNumber = dao.getTotalNumber();
        if (totalNumber == 0){
            mHaveBlackNumber.setVisibility(View.GONE);
            mNoBlcakNumber.setVisibility(View.VISIBLE);
        }else if (totalNumber > 0){

            mHaveBlackNumber.setVisibility(View.VISIBLE);
            mNoBlcakNumber.setVisibility(View.GONE);
            pagenumber = 0;
            if (pageBlackNumber.size() > 0){
                pageBlackNumber.clear();
            }
            pageBlackNumber.addAll(dao.getPageBlackNumber(pagenumber, pagesize));
            if (adapter == null){
                adapter = new BlackContactAdapter(pageBlackNumber, SecurityPhoneActivity.this);
                adapter.setCallBack(new BlackContactAdapter.BlackContactCallBack(){
                    @Override
                    public void DataSizeChanged(){
                        fillData();
                    }
                });
                mListView.setAdapter(adapter);
            }else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void initView(){
        findViewById(R.id.rl_titlebar).setBackgroundColor(getResources().getColor(R.color.bright_purple));
        ImageView mLeftImgv = (ImageView) findViewById(R.id.imgv_leftbtn);
        ((TextView) findViewById(R.id.tv_title)).setText("通讯卫士");
        mLeftImgv.setOnClickListener(this);
        mLeftImgv.setImageResource(R.drawable.back);
        mHaveBlackNumber = (FrameLayout) findViewById(R.id.fl_haveblacknumber);
        mNoBlcakNumber = (FrameLayout) findViewById(R.id.fl_noblacknumber);
        findViewById(R.id.btn_addblacknumber).setOnClickListener(this);
        mListView = (ListView) findViewById(R.id.lv_blacknumbers);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(AbsListView absListView,int i){
                switch (i){
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        int lastVisiblePosition = mListView.getLastVisiblePosition();
                        if (lastVisiblePosition == pageBlackNumber.size() - 1){
                            pagenumber++;
                            if (pagenumber * pagesize >= totalNumber){
                                Toast.makeText(SecurityPhoneActivity.this, "没有更多数据了",Toast.LENGTH_LONG).show();
                            }else{
                                pageBlackNumber.addAll(dao.getPageBlackNumber(pagenumber,pagesize));
                                adapter.notifyDataSetChanged();
                            }
                        }
                        break;
                }
            }
            @Override
            public void onScroll(AbsListView absListView, int i, int i1,int i2){

            }
        });
    }

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_security_phone);
        initView();
        fillData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgv_leftbtn:
                finish();
                break;
            case R.id.btn_addblacknumber:
                startActivity(new Intent(this, AddBlackNumberActivity.class));
                overridePendingTransition(R.anim.next_out,   R.anim.abc_fade_out);
                break;
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if (dao.getTotalNumber() > 0){
            mHaveBlackNumber.setVisibility(View.VISIBLE);
            mNoBlcakNumber.setVisibility(View.GONE);
        }else{
            mHaveBlackNumber.setVisibility(View.GONE);
            mNoBlcakNumber.setVisibility(View.VISIBLE);
        }

        pagenumber = 0;
        pageBlackNumber.clear();
        pageBlackNumber.addAll(dao.getPageBlackNumber(pagenumber, pagesize));
        if (adapter != null){
            adapter.notifyDataSetChanged();
        }
    }
}
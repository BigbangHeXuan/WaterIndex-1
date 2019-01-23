package com.jieshuizhibiao.waterindex.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.jieshuizhibiao.waterindex.R;
import com.jieshuizhibiao.waterindex.base.BaseActivity;
import com.jieshuizhibiao.waterindex.beans.FactoryDetailResponseBean;
import com.jieshuizhibiao.waterindex.contract.presenter.FactoryDetailPresenter;
import com.jieshuizhibiao.waterindex.contract.view.FactoryDetailViewImpl;
import com.jieshuizhibiao.waterindex.ui.adapter.FactoryListAdapter;
import com.jieshuizhibiao.waterindex.ui.view.AlertChainDialog;
import com.jieshuizhibiao.waterindex.utils.LogUtils;
import com.jieshuizhibiao.waterindex.utils.StatusBarUtil;
import com.jieshuizhibiao.waterindex.utils.image.GlidImageManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by songxiaotao on 2018/12/20.
 * Class Note:洗涤企业
 */

public class EnterpriseActivity extends BaseActivity implements FactoryDetailViewImpl {

    @BindView(R.id.title_bar)
    View title_bar;
    @BindView(R.id.left_ll)
    LinearLayout left_ll;
    @BindView(R.id.tv_title_center)
    TextView tv_title_center;
    @BindView(R.id.tv_title_left)
    TextView tv_title_left;
    @BindView(R.id.enterpriseImg)
    ImageView enterpriseImg;
    @BindView(R.id.enterpriseList)
    XRecyclerView enterpriseList;
    @BindView(R.id.enterpriseDetail)
    TextView enterpriseDetail;
    private AlertChainDialog alertChainDialog;
    private FactoryDetailPresenter factoryDetailPresenter;
    private FactoryListAdapter factoryListAdapter;
    private List<FactoryDetailResponseBean.WashFatoryCageGory> listEntities = new ArrayList<>();
    private int enterpriseId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setImmersionStatus(this, title_bar);
        factoryDetailPresenter = new FactoryDetailPresenter();
        factoryDetailPresenter.attachView(this);
        alertChainDialog = new AlertChainDialog(EnterpriseActivity.this);
        getData();
        initList();
    }

    @Override
    public void initContentView() {
        setContentView(R.layout.fragment_wash_enterprise);
    }

    @Override
    public void onReNetRefreshData(int viewId) {
        if(factoryDetailPresenter!=null){
            factoryDetailPresenter.getFactoryDetail(EnterpriseActivity.this,enterpriseId);
        }
        dismissLoadingDialog();
    }

    @OnClick({R.id.left_ll})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_ll:
                goBack(view);
                break;
            default:break;
        }
    }

    private void initList(){
        tv_title_center.setText("洗涤企业");
        tv_title_left.setText("返回");
        tv_title_left.setVisibility(View.VISIBLE);
        factoryListAdapter = new FactoryListAdapter(EnterpriseActivity.this,listEntities);
        enterpriseList.setArrowImageView(R.drawable.iconfont_downgrey);
        enterpriseList.setLayoutManager(new LinearLayoutManager(EnterpriseActivity.this));
        enterpriseList.setAdapter(factoryListAdapter);

        enterpriseList.setLoadingMoreEnabled(false);
        enterpriseList.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                if (factoryDetailPresenter != null) {
                    factoryDetailPresenter.getFactoryDetail(EnterpriseActivity.this,enterpriseId);
                }
            }

            @Override
            public void onLoadMore() {
                if (factoryDetailPresenter != null) {
                    factoryDetailPresenter.getFactoryDetail(EnterpriseActivity.this,enterpriseId);
                }
            }
        });
        factoryListAdapter.setOnItemClickListener(new FactoryListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("fsid",listEntities.get(position).getId());
                intent.putExtras(bundle);
                intent.setClass(EnterpriseActivity.this,FactoryServiceActivity.class);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(factoryDetailPresenter!=null){
            factoryDetailPresenter.getFactoryDetail(EnterpriseActivity.this,enterpriseId);
        }
        showLoadingDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(factoryDetailPresenter!=null){
            factoryDetailPresenter.detachView();
        }
    }

    @Override
    public void onFactoryDetailSuccess(FactoryDetailResponseBean factoryDetailResponseBean) {
        dismissLoadingDialog();
        LogUtils.d("factoryDetailResponseBean；"+factoryDetailResponseBean);
        enterpriseDetail.setText(factoryDetailResponseBean.getDetail().getDescription());
        GlidImageManager.getInstance().loadImageView(EnterpriseActivity.this,factoryDetailResponseBean.getDetail().getLogo(),enterpriseImg,R.drawable.ic_default_image);
        listEntities.clear();
        listEntities.addAll(factoryDetailResponseBean.getService_lists());
        factoryListAdapter.notifyDataSetChanged();
        enterpriseList.refreshComplete();
    }

    @Override
    public void onFactoryDetailFailed(String msg) {
        dismissLoadingDialog();
        LogUtils.d("serviceListEntities；"+msg);
    }

    public void getData() {
        if(getIntent()!=null){
            enterpriseId = getIntent().getIntExtra("enterpriseId",-1);
        }
    }
}
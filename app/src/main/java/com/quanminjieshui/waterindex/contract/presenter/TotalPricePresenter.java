package com.quanminjieshui.waterindex.contract.presenter;

import com.quanminjieshui.waterindex.base.BaseActivity;
import com.quanminjieshui.waterindex.beans.TotalPriceResponseBean;
import com.quanminjieshui.waterindex.contract.BasePresenter;
import com.quanminjieshui.waterindex.contract.model.TotalPriceModel;
import com.quanminjieshui.waterindex.contract.view.TotalPriceViewImpl;

/**
 * Created by songxiaotao on 2018/12/26.
 * Class Note:
 */

public class TotalPricePresenter extends BasePresenter<TotalPriceViewImpl> {

    private TotalPriceModel totalPriceModel;

    public TotalPricePresenter(){}

    public TotalPricePresenter(TotalPriceModel totalPriceModel){
        this.totalPriceModel = totalPriceModel;
    }

    public void getTotalPrice(BaseActivity activity, int pay_cate, int uid, String[] trade_detail){
        if (totalPriceModel == null){
            totalPriceModel = new TotalPriceModel();
        }
        totalPriceModel.getTotalPrice(activity, pay_cate, uid, trade_detail, new TotalPriceModel.TotalPriceCallBack() {
            @Override
            public void success(TotalPriceResponseBean totalPriceResponseBean) {
                if (mView!=null){
                    mView.onTotalPriceSuccess(totalPriceResponseBean);
                }
            }

            @Override
            public void failed(String msg) {
                if (mView!=null){
                    mView.onTotalPriceFailed(msg);
                }

            }
        });
    }

}

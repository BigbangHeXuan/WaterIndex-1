package com.quanminjieshui.waterindex.contract.model;

import com.quanminjieshui.waterindex.base.BaseActivity;
import com.quanminjieshui.waterindex.beans.TotalPriceResponseBean;
import com.quanminjieshui.waterindex.http.BaseObserver;
import com.quanminjieshui.waterindex.http.RetrofitFactory;
import com.quanminjieshui.waterindex.http.bean.BaseEntity;
import com.quanminjieshui.waterindex.http.config.HttpConfig;
import com.quanminjieshui.waterindex.http.utils.ObservableTransformerUtils;
import com.quanminjieshui.waterindex.http.utils.RequestUtil;
import com.quanminjieshui.waterindex.utils.LogUtils;

import java.util.HashMap;

/**
 * Created by songxiaotao on 2018/12/26.
 * Class Note:
 */

public class TotalPriceModel {
    public void getTotalPrice(BaseActivity activity, int pay_cate, int uid, String[] trade_detail,final TotalPriceCallBack callBack){

        HashMap<String,Object> params = new HashMap<>();
        params.put("pay_cate",pay_cate);
        params.put("uid",uid);
        params.put("trade_detail",trade_detail);
        RetrofitFactory.getInstance().createService()
                .totalPrice(RequestUtil.getRequestHashBody(params,false))
                .compose(activity.<BaseEntity<TotalPriceResponseBean>>bindToLifecycle())
                .compose(ObservableTransformerUtils.<BaseEntity<TotalPriceResponseBean>>io())
                .subscribe(new BaseObserver<TotalPriceResponseBean>(activity) {
                    @Override
                    protected void onSuccess(TotalPriceResponseBean totalPriceResponseBean) throws Exception {
                        callBack.success(totalPriceResponseBean);
                    }

                    @Override
                    protected void onFailure(Throwable e, boolean isNetWorkError) throws Exception {
                        if (e != null && e.getMessage() != null) {
                            if (isNetWorkError) {
                                LogUtils.e(e.getMessage());
                                callBack.failed(HttpConfig.ERROR_MSG);
                            } else {
                                callBack.failed(e.getMessage());
                            }
                        } else {
                            callBack.failed("");
                        }
                    }

                    @Override
                    protected void onCodeError(String code, String msg) throws Exception {
                        super.onCodeError(code, msg);
                        callBack.failed(msg);
                    }
                });
    }

    public interface TotalPriceCallBack{
        void success(TotalPriceResponseBean totalPriceResponseBean);
        void failed(String msg);
    }
}

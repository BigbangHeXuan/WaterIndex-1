/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: RegisterViewImpl
 * Author: sxt
 * Date: 2018/12/8 1:32 PM
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
package com.quanminjieshui.waterindex.contract.view;

import com.quanminjieshui.waterindex.beans.RegisterResponseBean;
import com.quanminjieshui.waterindex.contract.IBaseViewImpl;

import java.util.Map;

/**
 * @ClassName: RegisterViewImpl
 * @Description: java类作用描述
 * @Author: sxt
 * @Date: 2018/12/8 1:32 PM
 */
public interface RegisterViewImpl extends IBaseViewImpl {
    void onEdtContentsLegal();

    void onEdtContentsIllegal(Map<String, Boolean> verify);

    void onGetSmsSuccess();

    void onGetSmsFailed(String msg);

    void onRegisterSuccess(RegisterResponseBean registerResponseBean);

    void onRegisterFaild(String msg);
}


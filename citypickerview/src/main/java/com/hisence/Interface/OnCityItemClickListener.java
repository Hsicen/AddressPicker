package com.hisence.Interface;

import com.hisence.bean.CityBean;
import com.hisence.bean.DistrictBean;
import com.hisence.bean.ProvinceBean;

/**
 * @2Do:
 * @Author M2
 * @Version v ${VERSION}
 * @Date 2017/7/7 0007.
 */

public abstract class OnCityItemClickListener {

    /**
     * 当选择省市区三级选择器时，需要覆盖此方法
     *
     * @param province
     * @param city
     * @param district
     */
    public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {

    }

    /**
     * 取消
     */
    public void onCancel() {

    }
}

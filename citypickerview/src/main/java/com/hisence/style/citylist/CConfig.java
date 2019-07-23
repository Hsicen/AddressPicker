package com.hisence.style.citylist;

import com.hisence.style.citylist.bean.CityInfoBean;

public class CConfig {

    private static CityInfoBean sCityInfoBean;

    public static void setCity(CityInfoBean city) {
        sCityInfoBean = city;
    }

    public static CityInfoBean getCitySelected() {

        return sCityInfoBean;
    }
}

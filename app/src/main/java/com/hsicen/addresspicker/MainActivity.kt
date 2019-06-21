package com.hsicen.addresspicker

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.lljjcoder.Interface.OnCityItemClickListener
import com.lljjcoder.bean.CityBean
import com.lljjcoder.bean.DistrictBean
import com.lljjcoder.bean.ProvinceBean
import com.lljjcoder.style.cityjd.JDCityPicker
import kotlinx.android.synthetic.main.activity_main.*

/**
 * <p>作者：Hsicen  6/8/2019 10:52 AM
 * <p>邮箱：codinghuang@163.com
 * <p>功能：
 * <p>描述：地址选择测试
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnPick.setOnClickListener {
            JDCityPicker().apply {
                init(this@MainActivity)
                setOnCityItemClickListener(object : OnCityItemClickListener() {
                    override fun onCancel() {
                        tvAddress.text = "取消选择"
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onSelected(province: ProvinceBean?, city: CityBean?, district: DistrictBean?) {
                        tvAddress.text = "${province?.name}  ${city?.name}  ${district?.name}"
                    }
                })
            }.showCityPicker()
        }
    }
}

package com.hisence.hisencepicker

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.hisence.Interface.OnCityItemClickListener
import com.hisence.bean.CityBean
import com.hisence.bean.DistrictBean
import com.hisence.bean.ProvinceBean
import com.hisence.style.cityjd.JDCityPicker
import kotlinx.android.synthetic.main.activity_main.*

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

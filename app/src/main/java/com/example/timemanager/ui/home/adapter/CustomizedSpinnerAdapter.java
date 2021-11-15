package com.example.timemanager.ui.home.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import java.util.List;

/**
 * 定义一个SpinnerAdapter类继承ArrayAdapter
 * 重写以下两个方法
 * */
public class CustomizedSpinnerAdapter<T> extends ArrayAdapter {
    public CustomizedSpinnerAdapter(Context context, int resource, @NonNull List<T> objects) {
        super(context, resource, objects);
    }

    @Override
    public int getCount() {
        //返回数据的统计数量，大于0项则减去1项，从而不显示最后一项
        int i = super.getCount();
        return i>0?i-1:i;
    }
}


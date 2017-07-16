/*
 * Copyright (C) 2017 yyfly, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yyfly.android.citypicker.ui.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yyfly.android.citypicker.R;
import com.yyfly.android.citypicker.entity.City;

import java.util.List;

/**
 * 位置结果适配器
 */
public class ResultListAdapter extends BaseAdapter {
    private Context mContext;
    private List<City> mCities;

    public ResultListAdapter(Context mContext, List<City> mCities) {
        this.mCities = mCities;
        this.mContext = mContext;
    }

    public void changeData(List<City> list) {
        if (mCities == null) {
            mCities = list;
        } else {
            mCities.clear();
            mCities.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mCities == null ? 0 : mCities.size();
    }

    @Override
    public City getItem(int position) {
        return mCities == null ? null : mCities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ResultViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.cp_item_lv_search_result, parent, false);
            holder = new ResultViewHolder();
            holder.name = (TextView) view.findViewById(R.id.tv_result_name);
            view.setTag(holder);
        } else {
            holder = (ResultViewHolder) view.getTag();
        }
        holder.name.setText(mCities.get(position).getName());
        return view;
    }

    public static class ResultViewHolder {
        TextView name;
    }
}

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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yyfly.android.citypicker.R;
import com.yyfly.android.citypicker.entity.City;
import com.yyfly.android.citypicker.service.LocationService;
import com.yyfly.android.citypicker.ui.view.ExpandGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 城市列表适配器
 */
public class CityListAdapter extends BaseAdapter {
    private static final int VIEW_TYPE_COUNT = 3;

    private Context mContext;
    private LayoutInflater inflater;
    private List<City> mCities;
    private HashMap<String, Integer> letterIndexes;
    private OnCityClickListener onCityClickListener;
    private int locateState = LocationService.LOCATION_LOCATING;
    private String locatedCity;

    public CityListAdapter(Context mContext, List<City> mCities) {
        this.mContext = mContext;
        this.mCities = mCities;
        this.inflater = LayoutInflater.from(mContext);
        if (mCities == null) {
            mCities = new ArrayList<>();
        }
        mCities.add(0, new City("定位", "0"));
        mCities.add(1, new City("热门", "1"));
        int size = mCities.size();
        letterIndexes = new HashMap<>();
        for (int index = 0; index < size; index++) {
            //当前城市拼音首字母
            String currentLetter = getFirstLetter(mCities.get(index).getPinyin());
            //上个首字母，如果不存在设为""
            String previousLetter = index >= 1 ? getFirstLetter(mCities.get(index - 1).getPinyin()) : "";
            if (!TextUtils.equals(currentLetter, previousLetter)) {
                letterIndexes.put(currentLetter, index);
            }
        }
    }

    /**
     * 更新定位状态
     *
     * @param state the state
     * @param city  the city
     */
    public void updateLocateState(int state, String city) {
        this.locateState = state;
        this.locatedCity = city;
        notifyDataSetChanged();
    }

    /**
     * 获取字母索引的位置
     *
     * @param letter the letter
     * @return int
     */
    public int getLetterPosition(String letter) {
        Integer integer = letterIndexes.get(letter);
        return integer == null ? -1 : integer;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return position < VIEW_TYPE_COUNT - 1 ? position : VIEW_TYPE_COUNT - 1;
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
    public View getView(final int position, View view, ViewGroup parent) {
        CityViewHolder holder;
        int viewType = getItemViewType(position);
        switch (viewType) {
            case 0:     //定位
                view = inflater.inflate(R.layout.cp_view_locate_city, parent, false);
                ViewGroup container = (ViewGroup) view.findViewById(R.id.layout_locate);
                TextView state = (TextView) view.findViewById(R.id.tv_located_city);
                switch (locateState) {
                    case LocationService.LOCATION_LOCATING:
                        state.setText(mContext.getString(R.string.cp_locating));
                        break;
                    case LocationService.LOCATION_FAILED:
                        state.setText(R.string.cp_located_failed);
                        break;
                    case LocationService.LOCATION_SUCCESS:
                        state.setText(locatedCity);
                        break;
                }
                container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (locateState == LocationService.LOCATION_FAILED) {
                            //重新定位
                            if (onCityClickListener != null) {
                                onCityClickListener.onLocateClick();
                            }
                        } else if (locateState == LocationService.LOCATION_SUCCESS) {
                            //返回定位城市
                            if (onCityClickListener != null) {
                                onCityClickListener.onCityClick(locatedCity);
                            }
                        }
                    }
                });
                break;
            case 1:     //热门
                view = inflater.inflate(R.layout.cp_view_hot_city, parent, false);
                ExpandGridView gridView = (ExpandGridView) view.findViewById(R.id.grid_view_hot_city);
                final HotCityGridAdapter hotCityGridAdapter = new HotCityGridAdapter(mContext);
                gridView.setAdapter(hotCityGridAdapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (onCityClickListener != null) {
                            onCityClickListener.onCityClick(hotCityGridAdapter.getItem(position));
                        }
                    }
                });
                break;
            case 2:     //所有
                if (view == null) {
                    view = inflater.inflate(R.layout.cp_item_lv_city, parent, false);
                    holder = new CityViewHolder();
                    holder.letter = (TextView) view.findViewById(R.id.tv_city_letter);
                    holder.name = (TextView) view.findViewById(R.id.tv_city_name);
                    view.setTag(holder);
                } else {
                    holder = (CityViewHolder) view.getTag();
                }
                if (position >= 1) {
                    final String city = mCities.get(position).getName();
                    holder.name.setText(city);
                    String currentLetter = getFirstLetter(mCities.get(position).getPinyin());
                    String previousLetter = position >= 1 ? getFirstLetter(mCities.get(position - 1).getPinyin()) : "";
                    if (!TextUtils.equals(currentLetter, previousLetter)) {
                        holder.letter.setVisibility(View.VISIBLE);
                        holder.letter.setText(currentLetter);
                    } else {
                        holder.letter.setVisibility(View.GONE);
                    }
                    holder.name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onCityClickListener != null) {
                                onCityClickListener.onCityClick(city);
                            }
                        }
                    });
                }
                break;
        }
        return view;
    }

    public static class CityViewHolder {
        TextView letter;
        TextView name;
    }

    public void setOnCityClickListener(OnCityClickListener listener) {
        this.onCityClickListener = listener;
    }

    public interface OnCityClickListener {
        void onCityClick(String name);

        void onLocateClick();
    }

    /**
     * 获取拼音的首字母（大写）
     *
     * @param pinyin the pinyin
     * @return string
     */
    public static String getFirstLetter(final String pinyin) {
        if (TextUtils.isEmpty(pinyin)) return "定位";
        String c = pinyin.substring(0, 1);
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c).matches()) {
            return c.toUpperCase();
        } else if ("0".equals(c)) {
            return "定位";
        } else if ("1".equals(c)) {
            return "热门";
        }
        return "定位";
    }
}

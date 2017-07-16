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
package com.yyfly.android.citypicker;

import android.content.Context;


/**
 * 应用初始
 */
public class CityPickerApp {

    public static Context mContext;

    /**
     * APP 初始化相关操作
     */
    public static void init(Context context) {
        mContext = context;
    }
}

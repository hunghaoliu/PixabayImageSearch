/*
 * Copyright (C) 2012 Lucas Rocha
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.henry.pixabayimagesearch.Network;

import android.util.Log;

import com.henry.pixabayimagesearch.Pixabay.PixabayModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.Gson;

public class HttpHelper {
    private static final String LOG_TAG = "HttpHelper";

    static private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        try {
            String line = null;

            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return sb.toString();
    }

    static public PixabayModel loadJSON(String url) {
        HttpURLConnection connection = null;
        JSONArray json = null;
        InputStream is = null;
        PixabayModel model = null;

        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(5000);

            is = new BufferedInputStream(connection.getInputStream());

            Gson gson = new Gson();
            model = gson.fromJson(convertStreamToString(is), PixabayModel.class);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            if (connection != null) {
                connection.disconnect();
            }
        }

        return model;
    }

    static public InputStream loadImage(String url) {
        HttpURLConnection connection = null;
        InputStream is = null;

        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(15000);

            is = new BufferedInputStream(connection.getInputStream());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return is;
    }
}

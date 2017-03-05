/*
    This file is part of the dandelion*.

    dandelion* is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    dandelion* is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with the dandelion*.

    If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.dfa.diaspora_android.web;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.github.dfa.diaspora_android.App;
import com.github.dfa.diaspora_android.activity.MainActivity;
import com.github.dfa.diaspora_android.data.DiasporaPodList;

public class CustomWebViewClient extends WebViewClient {
    private final App app;
    private String lastLoadUrl = "";

    public CustomWebViewClient(App app, WebView webView) {
        this.app = app;
    }

    //Open non-diaspora links in customtab/external browser
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        String host = app.getSettings().getPod().getPodUrl().getHost();

        if (url.startsWith("https://" + host)
                || url.startsWith("http://" + host)
                || url.startsWith("https://dia.so/")) {
            return false;
        } else {
            Intent i = new Intent(MainActivity.ACTION_OPEN_EXTERNAL_URL);
            i.putExtra(MainActivity.EXTRA_URL, url);
            LocalBroadcastManager.getInstance(app.getApplicationContext()).sendBroadcast(i);
            return true;
        }
    }

    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        final CookieManager cookieManager = app.getCookieManager();
        String cookies = cookieManager.getCookie(url);
        DiasporaPodList.DiasporaPod pod = app.getSettings().getPod();

        if (cookies != null) {
            cookieManager.setCookie(url, cookies);
            if (pod != null && pod.getPodUrl() != null) {
                cookieManager.setCookie(pod.getPodUrl().getBaseUrl(), cookies);
                cookieManager.setCookie(".dia.so", "pod=" + pod.getPodUrl().getHost());
            }
            for (String c : cookies.split(";")) {
                //AppLog.d(this, "Cookie: " + c.split("=")[0] + " Value:" + c.split("=")[1]);
            }
            //new ProfileFetchTask(app).execute();
        }
    }

}

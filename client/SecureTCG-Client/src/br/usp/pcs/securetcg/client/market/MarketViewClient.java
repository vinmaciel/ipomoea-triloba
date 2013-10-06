package br.usp.pcs.securetcg.client.market;

import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MarketViewClient extends WebViewClient {
	
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		view.loadUrl(url);
		return true;
	}
}

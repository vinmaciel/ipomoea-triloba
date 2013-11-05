package br.usp.pcs.securetcg.client.market;

import br.usp.pcs.securetcg.client.R;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class MarketActivity extends Activity {
	
	/** UI Objects **/
	private WebView webview;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_browser);
		
		getLayoutObjects();
		setLayoutObjects();
	}
	
	public void getLayoutObjects() {
		webview = (WebView) findViewById(R.id.market_webview);
	}
	
	public void setLayoutObjects() {
		webview.setWebViewClient(new MarketViewClient());
	}
}

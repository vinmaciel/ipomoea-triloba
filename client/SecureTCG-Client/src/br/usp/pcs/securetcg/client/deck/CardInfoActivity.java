package br.usp.pcs.securetcg.client.deck;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import br.usp.pcs.securetcg.client.R;
import br.usp.pcs.securetcg.client.database.CardClassDAO;
import br.usp.pcs.securetcg.client.model.CardClass;

public class CardInfoActivity extends Activity {
	
	/* UI Objects */
	private FrameLayout background;
	private LinearLayout overlay;
	private TextView nameText;
	private TextView descriptionText;
	private ImageView image;
	
	/* Info Objects */
	private CardClass card;
	
	/* Control */
	private boolean presenting;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_info_activity);
		
		long cardId = getIntent().getLongExtra(Constants.CARD_SELECTED, -1);
		if(cardId == -1) {
			Toast.makeText(this, "Failed to get card", Toast.LENGTH_SHORT).show();
			finish();
		}
		CardClassDAO classDAO = new CardClassDAO(this);
		card = classDAO.get(cardId);
		if(card == null) {
			Toast.makeText(this, "Failed to get card", Toast.LENGTH_SHORT).show();
			finish();
		}
		
		getLayoutObjects();
		setLayoutObjects();
	}
	
	/* UI methods */
	private void getLayoutObjects() {
		background = (FrameLayout) findViewById(R.id.info_background);
		overlay = (LinearLayout) findViewById(R.id.info_overlay);
		nameText = (TextView) findViewById(R.id.info_name);
		descriptionText = (TextView) findViewById(R.id.info_description);
		image = (ImageView) findViewById(R.id.info_image);
	}
	
	private void setLayoutObjects() {
		background.setOnClickListener(new OnClickShowInfo());
		overlay.setVisibility(View.GONE);
		nameText.setText(card.getName());
		descriptionText.setText(card.getDescription());
		
		try {
			FileInputStream in = new FileInputStream(card.getBitmapPath());
			Bitmap bitmap = BitmapFactory.decodeStream(in);
			image.setImageBitmap(bitmap);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Toast.makeText(this, "Failed to show image", Toast.LENGTH_SHORT).show();
		}
		
		presenting = false;
	}
	
	/* OnClick Listeners */
	private class OnClickShowInfo implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			if(presenting) {
				overlay.setVisibility(View.GONE);
			}
			else {
				overlay.setVisibility(View.VISIBLE);
			}
			
			presenting = !presenting;
		}
		
	}
}

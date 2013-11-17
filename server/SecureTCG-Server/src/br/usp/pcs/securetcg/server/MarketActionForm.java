package br.usp.pcs.securetcg.server;

import org.apache.struts.action.ActionForm;

public class MarketActionForm extends ActionForm {

	private long cardId;
	private boolean downloadImage;

	public long getCardId() {
		return cardId;
	}
	public void setCardId(long cardId) {
		this.cardId = cardId;
	}
	
	public boolean isDownloadImage() {
		return downloadImage;
	}
	public void setDownloadImage(boolean downloadImage) {
		this.downloadImage = downloadImage;
	}
	
}

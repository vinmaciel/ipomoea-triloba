package br.usp.pcs.securetcg.server;

import org.apache.struts.action.ActionForm;

public class WithdrawActionForm extends ActionForm {

	private byte[] cardID;
	private byte[] J;
	private byte[] commitment;
	private byte[] pku;
	private byte[][] tr;
	private byte[][] sr;
	
	public byte[] getCardID() {
		return cardID;
	}
	public void setCardID(byte[] cardID) {
		this.cardID = cardID;
	}
	
	public byte[] getJ() {
		return J;
	}
	public void setJ(byte[] j) {
		J = j;
	}
	
	public byte[] getCommitment() {
		return commitment;
	}
	public void setCommitment(byte[] commitment) {
		this.commitment = commitment;
	}
	
	public byte[] getPku() {
		return pku;
	}
	public void setPku(byte[] pku) {
		this.pku = pku;
	}
	
	public byte[][] getTr() {
		return tr;
	}
	public void setTr(byte[][] tr) {
		this.tr = tr;
	}
	
	public byte[][] getSr() {
		return sr;
	}
	public void setSr(byte[][] sr) {
		this.sr = sr;
	}
}

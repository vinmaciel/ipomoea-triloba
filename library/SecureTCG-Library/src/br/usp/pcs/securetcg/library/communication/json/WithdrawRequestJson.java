package br.usp.pcs.securetcg.library.communication.json;

public class WithdrawRequestJson {

	private long cardID;
	private long J;
	private byte[] commitment;
	private byte[] pku;
	private byte[][] tr;
	
	public long getCardID() {
		return cardID;
	}
	public void setCardID(long cardID) {
		this.cardID = cardID;
	}
	
	public long getJ() {
		return J;
	}
	public void setJ(long j) {
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
	
}

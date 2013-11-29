package br.usp.pcs.securetcg.library.communication.json;

public class WithdrawWalletJson {

	private byte[] signatureA;
	private byte[] signatureE;
	private byte[] signatureV;
	private byte[] serialComponent;
	
	public WithdrawWalletJson() { }

	public byte[] getSignatureA() {
		return signatureA;
	}
	public void setSignatureA(byte[] signatureA) {
		this.signatureA = signatureA;
	}

	public byte[] getSignatureE() {
		return signatureE;
	}
	public void setSignatureE(byte[] signatureE) {
		this.signatureE = signatureE;
	}

	public byte[] getSignatureV() {
		return signatureV;
	}
	public void setSignatureV(byte[] signatureV) {
		this.signatureV = signatureV;
	}

	public byte[] getSerialComponent() {
		return serialComponent;
	}
	public void setSerialComponent(byte[] serialComponent) {
		this.serialComponent = serialComponent;
	}
}

package br.usp.pcs.securetcg.library.communication.json;

public class WithdrawWalletJson {

	private byte[] signature;
	private byte[] signatureRandom;
	private byte[] serialComponent;
	
	public WithdrawWalletJson() { }

	public byte[] getSignature() {
		return signature;
	}
	public void setSignature(byte[] signature) {
		this.signature = signature;
	}

	public byte[] getSignatureRandom() {
		return signatureRandom;
	}
	public void setSignatureRandom(byte[] signatureRandom) {
		this.signatureRandom = signatureRandom;
	}

	public byte[] getSerialComponent() {
		return serialComponent;
	}
	public void setSerialComponent(byte[] serialComponent) {
		this.serialComponent = serialComponent;
	}
}

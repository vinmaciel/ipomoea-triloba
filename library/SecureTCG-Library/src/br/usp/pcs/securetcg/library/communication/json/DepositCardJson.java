package br.usp.pcs.securetcg.library.communication.json;

import java.math.BigInteger;

public class DepositCardJson {
	
	private BigInteger serial;
	private PropertyCardJson[] propertycards;

	public BigInteger getSerial() {
		return serial;
	}

	public void setSerial(BigInteger serial) {
		this.serial = serial;
	}

	public PropertyCardJson[] getInfocards() {
		return propertycards;
	}

	public void setInfocards(PropertyCardJson[] infocards) {
		this.propertycards = infocards;
	}

}

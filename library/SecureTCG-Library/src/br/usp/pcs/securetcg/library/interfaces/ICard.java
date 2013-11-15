package br.usp.pcs.securetcg.library.interfaces;

/**
 * Interface used to generate card (cash) related info, like specified signature and
 * serial id.
 * 
 * @author mmaciel
 *
 */
public abstract class ICard {
	
	private byte[] serial;
	private byte[] properties;
	private long classID;
	
	public byte[] getSerial() {
		return serial;
	}
	public void setSerial(byte[] serial) {
		this.serial = serial;
	}
	
	public byte[] getProperties() {
		return properties;
	}
	public void setProperties(byte[] properties) {
		this.properties = properties;
	}
	
	public long getClassID() {
		return classID;
	}
	public void setClassID(long classID) {
		this.classID = classID;
	}
	
}

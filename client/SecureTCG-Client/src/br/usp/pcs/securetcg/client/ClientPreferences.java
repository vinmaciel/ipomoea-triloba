package br.usp.pcs.securetcg.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import br.usp.pcs.securetcg.client.utils.Constants;
import br.usp.pcs.securetcg.library.clsign.CLPublicKey;
import br.usp.pcs.securetcg.library.ecash.CompactEcash;
import br.usp.pcs.securetcg.library.ecash.model.UPrivateKey;
import br.usp.pcs.securetcg.library.ecash.model.UPublicKey;

public class ClientPreferences {

	private static ClientPreferences instance;
	private Context context;
	
	private ClientPreferences(Context context) {
		this.context = context;
	}
	
	public static ClientPreferences get(Context context) {
		if(instance == null)
			instance = new ClientPreferences(context);
		
		return instance;
	}
	
	public UPrivateKey getPrivateKey() throws IOException {
		UPrivateKey sku = null;
		
		try{
			String path = this.context.getFilesDir().getAbsolutePath();
			path += "/" + Constants.USER_PRIVATE_KEY;
			
			FileInputStream fis = new FileInputStream(path);
			ObjectInputStream ois = new ObjectInputStream(fis);
			sku = (UPrivateKey) ois.readObject();
			ois.close();
			fis.close();
		} catch(FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			throw new IOException();
		} catch (ClassNotFoundException e) {
			throw new IOException();
		}
		
		return sku;
	}
	
	public UPublicKey getPublicKey() throws IOException {
		UPublicKey pku = null;
		
		try{
			String path = this.context.getFilesDir().getAbsolutePath();
			path += "/" + Constants.USER_PUBLIC_KEY;
			
			FileInputStream fis = new FileInputStream(path);
			ObjectInputStream ois = new ObjectInputStream(fis);
			pku = (UPublicKey) ois.readObject();
			ois.close();
			fis.close();
		} catch(FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			throw new IOException();
		} catch (ClassNotFoundException e) {
			throw new IOException();
		}
		
		return pku;
	}
	
	protected void createKey() throws IOException {
		UPublicKey pku = new UPublicKey();
		UPrivateKey sku = new UPrivateKey();
		CompactEcash.uKeyGen(pku, sku);
		
		try{
			String path = this.context.getFilesDir().getAbsolutePath();
			path += "/" + Constants.USER_PRIVATE_KEY;
			
			FileOutputStream fos = new FileOutputStream(path);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(sku);
			oos.close();
			fos.close();
		} catch(IOException e) {
			throw new IOException();
		}
		
		try{
			String path = this.context.getFilesDir().getAbsolutePath();
			path += "/" + Constants.USER_PUBLIC_KEY;
			
			FileOutputStream fos = new FileOutputStream(path);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(pku);
			oos.close();
			fos.close();
		} catch(IOException e) {
			String path = this.context.getFilesDir().getAbsolutePath();
			path += "/" + Constants.USER_PRIVATE_KEY;
			
			File file = new File(path);
			file.delete();
		}
	}
	
	protected void destroyKeys() throws IOException {
		String path = this.context.getFilesDir().getAbsolutePath();
		path += "/" + Constants.USER_PRIVATE_KEY;
		
		File file = new File(path);
		if(file != null) file.delete();
		
		path = this.context.getFilesDir().getAbsolutePath();
		path += "/" + Constants.USER_PUBLIC_KEY;
		
		file = new File(path);
		if(file != null) file.delete();
	}
	
	public CLPublicKey getBankKey() throws IOException {
		//FIXME
		CLPublicKey pkb = null;
		
		try{
			String path = this.context.getFilesDir().getAbsolutePath();
			path += "/" + Constants.BANK_PUBLIC_KEY;
			
			FileInputStream fis = new FileInputStream(path);
			ObjectInputStream ois = new ObjectInputStream(fis);
			pkb = (CLPublicKey) ois.readObject();
			ois.close();
			fis.close();
		} catch(IOException e) {
			throw new IOException();
		} catch (ClassNotFoundException e) {
			throw new IOException();
		}
		
		return pkb;
	}
}

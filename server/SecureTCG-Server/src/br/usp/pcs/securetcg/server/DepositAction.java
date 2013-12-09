package br.usp.pcs.securetcg.server;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.usp.pcs.securetcg.library.communication.json.DepositCardJson;
import br.usp.pcs.securetcg.library.communication.json.PropertyCardJson;

import com.google.gson.Gson;

public class DepositAction extends Action{
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		GenericJsonForm depositForm = (GenericJsonForm) form;
		String json = depositForm.getJson();
		
		DepositCardJson requestJson = new Gson().fromJson(json, DepositCardJson.class);
			
			BigInteger serial = new BigInteger(String.valueOf(requestJson.getSerial()));
			PropertyCardJson[] propertyJson = requestJson.getInfocards();
			
			try {
		  		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		  		for ( int i = 0; i < propertyJson.length; i++){
		  			digest.update(propertyJson[i].getInfo().toByteArray());
			  		digest.update(propertyJson[i].getR().toByteArray());	
				}
		  		BigInteger R = new BigInteger(digest.digest());
		  	} catch (NoSuchAlgorithmException e) {
		  		e.printStackTrace();
		  		return null;
		  	}
			
			// compara R com hash da carta
			
			//verifica se a informação já está cadastrada no banco
			
		  	
		
		
		return null;
	}
}

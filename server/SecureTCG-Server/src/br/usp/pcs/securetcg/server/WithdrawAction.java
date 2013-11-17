package br.usp.pcs.securetcg.server;

import java.io.IOException;
import java.math.BigInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.usp.pcs.securetcg.library.clsign.CLPrivateKey;
import br.usp.pcs.securetcg.library.clsign.CLPublicKey;
import br.usp.pcs.securetcg.library.clsign.CLSignature;
import br.usp.pcs.securetcg.library.communication.json.WithdrawChallengeJson;
import br.usp.pcs.securetcg.library.communication.json.WithdrawWalletJson;
import br.usp.pcs.securetcg.library.ecash.CompactEcash;

import com.google.gson.Gson;

public class WithdrawAction extends Action {
	
	private static final String SESSION_CHALLENGE = "challenge";
	private static final String SESSION_CARD_ID = "card_id";
	private static final String SESSION_WALLET_SIZE = "wallet_size";
	private static final String SESSION_COMMITMENT = "commitment";
	private static final String SESSION_PKU = "pku";
	private static final String SESSION_RANDOM_PROOF = "proof";
	
	/**
	 * Executes a request to /withdraw.do. <br/>
	 * Acceptable methods: GET <br/>
	 * Acceptable queries:
	 * <ul>
	 * <li> quest=request
	 * <li> quest=solve
	 * </ul>
	 * 
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		WithdrawActionForm withdrawForm = (WithdrawActionForm) form;
		HttpSession session = request.getSession();
		
		if(request.getMethod().equals("GET")) {
			String option = request.getParameter("option");
			
			if(option.equals("request")) {
				//Parse form
				session.setAttribute(SESSION_CARD_ID, withdrawForm.getCardID());
				session.setAttribute(SESSION_WALLET_SIZE, withdrawForm.getJ());
				session.setAttribute(SESSION_COMMITMENT, withdrawForm.getCommitment());
				session.setAttribute(SESSION_PKU, withdrawForm.getPku());
				session.setAttribute(SESSION_RANDOM_PROOF, withdrawForm.getTr());
				
				//Generate challenges
				BigInteger[] challengeGen = CompactEcash.withdraw_BankSide_Challenge();
				byte[][] challenge = new byte[challengeGen.length][];
				for(int i = 0; i < challengeGen.length; i++) {
					System.out.println("challenge " + i + ": " + challengeGen[i].toString());
					challenge[i] = challengeGen[i].toByteArray();
				}
				session.setAttribute(SESSION_CHALLENGE, challenge);
				
				//Return JSON
				WithdrawChallengeJson challengeJson = new WithdrawChallengeJson();
				challengeJson.setChallenges(challenge);
				String json = new Gson().toJson(challengeJson, WithdrawChallengeJson.class);
				
				try {
					response.setContentType("application/json");
					response.setStatus(HttpServletResponse.SC_ACCEPTED);
					response.getWriter().write(json);
				} catch (IOException e) {
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
				return null;
			}
			
			else if(option.equals("solve")) {
				//Parse values
				BigInteger	_A = new BigInteger((byte[]) session.getAttribute(SESSION_COMMITMENT)),
							Gu = new BigInteger((byte[]) session.getAttribute(SESSION_PKU)),
							J = new BigInteger((byte[]) session.getAttribute(SESSION_WALLET_SIZE)),
							Q = new BigInteger((byte[]) session.getAttribute(SESSION_CARD_ID));
				
				byte[][]	rand = (byte[][]) session.getAttribute(SESSION_RANDOM_PROOF),
							solution = withdrawForm.getSr(),
							challengeSent = (byte[][]) session.getAttribute(SESSION_CHALLENGE);
							
				BigInteger[]	tr = new BigInteger[rand.length],
								sr = new BigInteger[solution.length],
								challenge = new BigInteger[challengeSent.length];
				
				for(int i = 0; i < tr.length; i++)
					tr[i] = new BigInteger(rand[i]);
				for(int i = 0; i < sr.length; i++)
					sr[i] = new BigInteger(solution[i]);
				for(int i = 0; i < challenge.length; i++)
					challenge[i] = new BigInteger(challengeSent[i]);
				
				//TODO get public and private key
				CLPrivateKey sk = new CLPrivateKey();
				CLPublicKey pk = new CLPublicKey();
				
				//Verify proof
				Object[] proof = CompactEcash.withdraw_BankSide_Proof(_A, Gu, J, Q, tr, sr, challenge, sk, pk);
				if(proof.length == 1 && !(boolean) proof[0]) {
					response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
					return null;
				}
				else if(proof.length == 3 && (boolean) proof[0]) {
					BigInteger _s = (BigInteger) proof[1];
					CLSignature sig = (CLSignature) proof[2];
					
					WithdrawWalletJson walletJson = new WithdrawWalletJson();
					walletJson.setSignature(sig.getA());
					walletJson.setSignatureRandom(sig.getE());
					walletJson.setSerialComponent(_s.toByteArray());
					String json = new Gson().toJson(walletJson, WithdrawWalletJson.class);
					
					try {
						response.setContentType("application/json");
						response.setStatus(HttpServletResponse.SC_OK);
						response.getWriter().write(json);
					} catch (IOException e) {
						response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					}
					return null;
				}
				else {
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					return null;
				}
				
			}
			
			else {
				System.out.println("option: " + option);
				response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
				return null;
			}
		}
		
		else {
			System.out.println("method: " + request.getMethod());
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}
		
		return mapping.findForward("failure");
	}
}

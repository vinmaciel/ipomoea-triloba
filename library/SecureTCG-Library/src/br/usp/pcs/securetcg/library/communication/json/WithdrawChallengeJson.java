package br.usp.pcs.securetcg.library.communication.json;

public class WithdrawChallengeJson {

	private byte[][] challenges;
	
	public WithdrawChallengeJson() { }
	
	public byte[][] getChallenges() {
		return challenges;
	}

	public void setChallenges(byte[][] challenges) {
		this.challenges = challenges;
	}
}

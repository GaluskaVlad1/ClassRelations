class Invocation {
	private long candidateID,senderID,receiverID;
	public Invocation(long c,long s,long r) {
		candidateID=s;
		senderID=c;
		receiverID=r;
	}
	public boolean hasReceiver() {
		if(receiverID==0) return false;
		return true;
	}
	public long getCandidateID() {
		return candidateID;
	}
	public long getSenderID() {
		return senderID;
	}
	public long getReceiverID() {
		return receiverID;
	}
	public String toString() {
		return candidateID+" "+senderID+" "+receiverID;
	}
}

class Inheritance {
	private long SubclassID,SuperclassID;
	public Inheritance(long Sub,long Super) {
		SubclassID=Sub;
		SuperclassID=Super;
	}
	public long getSubclassID() {
		return SubclassID;
	}
	public long getSuperclassID() {
		return SuperclassID;
	}
	public String toString() {
		return ""+SubclassID+" "+SuperclassID;
	}
}

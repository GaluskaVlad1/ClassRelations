class Triplet {
	private int no_calls;
	private int no_accesses;
	private int no_inheritance_relations;
	public void setNoCalls(int n) {
		no_calls=n;
	}
	public void setNoAccesses(int n) {
		no_accesses=n;
	}
	public void setNoInheritanceRelations(int n) {
		no_inheritance_relations=n;
	}
	public String toString() {
		return ""+no_calls+","+no_accesses+","+no_inheritance_relations;
	}
}

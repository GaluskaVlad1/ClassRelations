class Tuplet {
	private int no_calls;
	private int no_accesses;
	private int no_inheritance_relations;
	private int no_returns;
	private int no_declarations;
	private int no_strict_data;
	private int no_all;
	public void setNoCalls(int n) {
		no_calls=n;
	}
	public void setNoAccesses(int n) {
		no_accesses=n;
	}
	public void setNoInheritanceRelations(int n) {
		no_inheritance_relations=n;
	}
	public void setNoReturns(int n){ no_returns=n; }
	public void setNoDeclarations(int n){ no_declarations=n; }
	public void setNoStrictData(int n){ no_strict_data=n; }
	public void setNoAll(int n){ no_all=n; }
	public int getNoCalls(){
		return no_calls;
	}
	public int getNoAccesses(){
		return no_accesses;
	}
	public String toString() {
		return ""+no_calls+","+no_accesses+","+no_inheritance_relations+","+no_returns+","+no_declarations+","+no_strict_data+","+no_all;
	}
}

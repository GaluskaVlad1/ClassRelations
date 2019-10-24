import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

class Method {
	private String kind;
	private long parentType,ID;
	private FamixClass ParentClass;
	private String signature;
	private String modifiers;
	private Set<Method> CalledMethods=new HashSet<Method> ();
	private Set<Attribute> AccessedAttributes=new HashSet<Attribute> ();
	private Set<Method> ProtectedMethods=new HashSet<Method> ();
	private Set<Attribute> ProtectedAttributes=new HashSet<Attribute> ();
	public Method(long ID,long parentType,String signature,String modifiers,String kind) {
		this.kind=kind;
		this.parentType=parentType;
		this.ID=ID;
		this.signature=signature;
		this.modifiers=modifiers;
	}
	public Set<Method> getCalledMethods(){
		return CalledMethods;
	}
	public FamixClass getParent() {
		return ParentClass;
	}
	public Set<Attribute> getAccessedAttributes(){
		return AccessedAttributes;
	}
	public Set<Method> getProtectedMethodsCalled(){
		return ProtectedMethods;
	}
	public Set<Attribute> getProtectedAttributesAccessed(){
		return ProtectedAttributes;
	}
	public boolean isProtected() {
		return (modifiers.contains("protected"));
	}
	public long getID() {
		return ID;
	}
	public void addProtectedMethod(Method m) {
		ProtectedMethods.add(m);
	}
	public void addProtectedAttribute(Attribute a) {
		ProtectedAttributes.add(a);
	}
	public void setClass(FamixClass c) {
		ParentClass=c;
	}
	public String getSignature() {
		return signature;
	}
	public long getParentType() {
		return parentType;
	}
	public int hashCode() {
		return (int) (ID ^ (ID >>>32));
	}
	public boolean equals(Object o) {
		if(o instanceof Method) {
			Method m=(Method) o;
			if(m.getID()==ID) return true;
			return false;
		}
		return false;
	}
	public Set<Attribute> getAttributes(){
		return AccessedAttributes;
	}
	public Set<Method> getMethods(){
		return CalledMethods;
	}
	public void addCalledMethod(Method m) {
		CalledMethods.add(m);
	}
	public void addAccessedAttribute(Attribute a) {
		AccessedAttributes.add(a);
	}
	public String toString() {
		String st="";
		Iterator<Attribute> it=ProtectedAttributes.iterator();
		while(it.hasNext()) {
			Attribute a=it.next();
			st=st+a.getID()+"  ";
		}
		return st;
	}
}

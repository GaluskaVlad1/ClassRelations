import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

class Method {
	private String kind;
	private boolean constr=false;
	private long parentType,ID;
	private FamixClass ParentClass;
	private String signature;
	private String modifiers;
	private int cyclomaticComplexity=1;
	private ArrayList<Parameter> parameters=new ArrayList<Parameter>();
	private ArrayList<LocalVariable> localVariables=new ArrayList<LocalVariable>();
	private Set<Method> CalledMethods=new HashSet<Method> ();
	private Set<Attribute> AccessedAttributes=new HashSet<Attribute> ();
	private Set<Method> ProtectedMethods=new HashSet<Method> ();
	private Set<Attribute> ProtectedAttributes=new HashSet<Attribute> ();
	public Method(long ID,long parentType,String signature,String modifiers,String kind,int cyclomaticComplexity) {
		this.kind=kind;
		this.parentType=parentType;
		this.ID=ID;
		this.signature=signature;
		this.constr=kind.contains("constructor");
		this.modifiers=modifiers;
		this.cyclomaticComplexity=cyclomaticComplexity;
	}
	public void addLocalVariable(LocalVariable lv){
	     localVariables.add(lv);
    }
    public boolean isConstr(){
	    return constr;
    }
	public void addParameter(Parameter p){
	     parameters.add(p);
    }
    public int getNoAttributes(){
	    return AccessedAttributes.size()+ProtectedAttributes.size();
    }
	public int getCyclomaticComplexity(){
	    return cyclomaticComplexity;
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
	public int howManyAttributesAccessesOutOfSet(Set<Attribute> attributes){
	    Iterator<Attribute> it=attributes.iterator();
	    int sum=0;
	    while(it.hasNext()){
            Attribute a=it.next();
            if(AccessedAttributes.contains(a) && !a.isParamType()) {
                sum++;
            }
            if(ProtectedAttributes.contains(a) && !a.isParamType()) sum++;
        }
	    return sum;
    }

	public boolean isUserDefined(){
		if(ParentClass==null) return false;
		return ParentClass.isUserDefined();
	}
	public Attribute getAccessorAttribute(){
	    if(getAccessedAttributesSize()==1 && getProtectedAttributesSize()==1){
            Iterator<Attribute> it=ProtectedAttributes.iterator();
            while(it.hasNext()){
                Attribute a=it.next();
                if(a.isViable()) return a;
            }
        }else{
	        if(getAccessedAttributesSize()==2){
	            Iterator<Attribute> it=AccessedAttributes.iterator();
	            while(it.hasNext()){
	                Attribute a=it.next();
	                if(!a.isParameter() && a.isViable()) return a;
                }
            }
	        if(getAccessedAttributesSize()==1){
	            Iterator<Attribute> it=AccessedAttributes.iterator();
	            while(it.hasNext()){
	                Attribute a=it.next();
	                if(a.isViable()) return a;
                }
            }
        }
	    return null;
    }
    public boolean isPublic(){
	    return modifiers.contains("public");
    }

    public boolean callsMethodFromParentOfType(FamixClass c){
	    Iterator<Method> it=CalledMethods.iterator();
	    while(it.hasNext()){
	        Method m=it.next();
	        if(c.equals(m.getParent())) return true;
        }
	    it=ProtectedMethods.iterator();
	    while(it.hasNext()){
	        Method m=it.next();
	        if(c.equals(m.getParent())) return true;
        }
	    return false;
    }

    public boolean accessesAttributeOfType(FamixClass c){
	    Iterator<Attribute> it=AccessedAttributes.iterator();
	    while(it.hasNext()){
	        Attribute a=it.next();
	        if(c.equals(a.getType())) return true;
        }
	    it=ProtectedAttributes.iterator();
	    while(it.hasNext()){
	        Attribute a=it.next();
	        if(c.equals(a.getType())) return true;
        }
	    return false;
    }

    public int getAccessedAttributesSize(){
	    return (int)AccessedAttributes.stream()
                .filter(Attribute::isViable)
                .count();
    }
    public int getProtectedAttributesSize(){
	    return (int)ProtectedAttributes.stream()
                .filter(Attribute::isViable)
                .count();
    }
	public boolean isAccessor(){
		if((getAccessedAttributesSize()+getProtectedAttributesSize())<=2 && cyclomaticComplexity==1){
            if(CalledMethods.size()+ProtectedMethods.size()==0){
                Attribute a;
                if(getAccessedAttributesSize()!=0) a=AccessedAttributes.stream()
                                                    .findAny()
                                                    .orElse(null);
                else a=ProtectedAttributes.stream()
                        .filter(Attribute::isViable)
                        .findAny()
                        .orElse(null);
                    String sign=signature.toLowerCase();
                    char[] charArray=sign.toCharArray();
                    if(charArray.length>=3) {
                        if (charArray[0] == 'g' && charArray[1] == 'e' && charArray[2] == 't') return (getAccessedAttributesSize()+getProtectedAttributesSize())<=1;
                        if (charArray[0] == 's' && charArray[1] == 'e' && charArray[2] == 't') return (getAccessedAttributesSize()+getProtectedAttributesSize())<=2;
                    }
            }
        }
		return false;
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

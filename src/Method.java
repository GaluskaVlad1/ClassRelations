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
	private Set<Method> methodsThatCallThisMethod = new HashSet<>();
	private boolean isStub=false;
	private boolean isStatic = false;
	private long declaredTypeID;
	private FamixClass declaredType;
	private long length;
	public Method(long ID,long parentType,String signature,String modifiers,String kind,int cyclomaticComplexity,boolean isStub,long declaredType, boolean isStatic, long length) {
		this.kind=kind;
		this.parentType=parentType;
		this.ID=ID;
		this.signature=signature;
		this.constr=kind.contains("constructor");
		this.modifiers=modifiers;
		this.cyclomaticComplexity=cyclomaticComplexity;
		this.isStub=isStub;
		this.declaredTypeID=declaredType;
		this.isStatic = isStatic;
		this.length = length;
	}

	public boolean isStatic(){return isStatic;}
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
		m.addCaller(this);
	}
	public void addCaller(Method m){
		methodsThatCallThisMethod.add(m);
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
	public boolean isNotDefaultConstructor(){
		return !(isStub && constr);
	}
	public boolean equals(Object o) {
		if(o instanceof Method) {
			Method m=(Method) o;
			if(m.getID()==ID) return true;
			return false;
		}
		return false;
	}
	public long getDeclaredTypeID(){
		return declaredTypeID;
	}
	public FamixClass getDeclaredType(){
		return declaredType;
	}
	public void setDeclaredType(FamixClass declaredType){
		this.declaredType=declaredType;
	}
	public Set<Attribute> getAttributes(){
		return AccessedAttributes;
	}
	public Set<Method> getMethods(){
		return CalledMethods;
	}
	public void addCalledMethod(Method m) {
		CalledMethods.add(m);
		m.addCaller(this);
	}
	public void addAccessedAttribute(Attribute a) {
		AccessedAttributes.add(a);
	}
	public int howManyAttributesAccessesOutOfSet(Set<Attribute> attributes){
	    Iterator<Attribute> it=attributes.iterator();
	    int sum=0;
	    while(it.hasNext()){
            Attribute a=it.next();
            if(AccessedAttributes.contains(a) && ParentClass.containsAttribute(a)) {
                sum++;
            }
            if(ProtectedAttributes.contains(a) && ParentClass.containsAttribute(a)) {
            	sum++;
			}
        }
	    if(sum==0) return 0;
	    return 1;
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

	public int getCM(){
        return methodsThatCallThisMethod.size();
    }

    public int getCC(){
	    return (int)methodsThatCallThisMethod.stream()
                .map(Method::getParent)
                .filter(famixClass -> famixClass != null)
				.distinct()
                .count();
    }

    public Set<Method> getCalledExternalMethods(){
	    Set<Method> s = new HashSet<>();
	    Iterator<Method> sit = CalledMethods.iterator();
	    while(sit.hasNext()){
	        Method m = sit.next();
	        if(!m.isAccessor() && m.isNotDefaultConstructor() && m.isUserDefined() && m.getParent()!=ParentClass && m.getParent()!=null){
	            s.add(m);
            }
        }
	    return s;
    }

    public int getFANOUT(){
	    Set<Method> sm = getCalledExternalMethods();
	    return sm.size();
    }

    public int getFANOUT_CLS(){
	    return (int)getCalledExternalMethods().stream()
                .map(Method::getParent)
                .filter(famixClass -> famixClass != null)
                .distinct()
                .count();
    }

	public String isBottleneck(){
		int CM = getCM();
		int CC = getCC();
		int FANOUT = getFANOUT();
		int FANOUT_CLS = getFANOUT_CLS();
	    if(CM>=10 && CC>=5 && FANOUT>=10 && FANOUT_CLS>=5 && cyclomaticComplexity>=4 && length>=50){
	    	return ParentClass.getContainingFile().getFileName()+","+ParentClass.getClassName()+","+signature+",Bottleneck,"+
					"CM = "+CM+",CC = "+CC+",FANOUT = "+FANOUT+",FANOUT_CLS = "+FANOUT_CLS+",cyclo = "+cyclomaticComplexity+",LOC = "+
					length;
		}else return "";
    }

    public Set<Attribute> getExtAttributesAccessed(){
		Set<Attribute> s = new HashSet<>();
		Iterator<Attribute> it = AccessedAttributes.iterator();
		while(it.hasNext()){
			Attribute a = it.next();
			if(a!=null && a.getContainerClass()!=null && a.getContainerClass().isUserDefined() && a.getContainerClass()!=ParentClass && !a.getContainerClass().isRelated(ParentClass))s.add(a);
		}
		Iterator<Method> mit = CalledMethods.iterator();
		while(mit.hasNext()){
			Method m = mit.next();
			if(m.isAccessor() && m.getParent()!=null && m.getParent()!=ParentClass && !m.getParent().isRelated(ParentClass)){
				Attribute a = m.getAccessorAttribute();
				if(a==null || a.getContainerClass()==null || !a.getContainerClass().isUserDefined())continue;
				s.add(a);
			}
		}
		return s;
	}

    public int getATFD(){
		Set<Attribute> s = getExtAttributesAccessed();
		return s.size();
	}

	public int getCPFD(){
		Iterator<Attribute> it = getExtAttributesAccessed().iterator();
		Set<FamixClass> s = new HashSet<>();
		while(it.hasNext()){
			Attribute a = it.next();
			if(a==null) continue;
			FamixClass c = a.getContainerClass();
			if(c!=null) s.add(c);
		}
		return s.size();
	}

	public Set<Method> getMethodsThatCallThisMethod(){
		return methodsThatCallThisMethod;
	}

	public double getLDA(){
		double noBaseClassAttributes = (double)AccessedAttributes.stream()
				.filter(attribute -> attribute!=null)
				.filter(attribute -> attribute.getContainerClass() == ParentClass)
				.distinct()
				.count();
		double noBaseClassProtectedAttributes = (double)ProtectedAttributes.stream()
				.filter(attribute -> attribute!=null)
				.filter(attribute -> attribute.getContainerClass() == ParentClass)
				.distinct()
				.count();
		double extAttributes = getExtAttributesAccessed().size();
		if(noBaseClassAttributes + noBaseClassProtectedAttributes + extAttributes == 0) return 0;
		return (noBaseClassAttributes + noBaseClassProtectedAttributes)/(noBaseClassAttributes + noBaseClassProtectedAttributes + extAttributes);
	}

	public Double round(Double d){
		return ((double) ((int) (d*100)))/100;
	}

    public String isFeatureEnvy(){
		int ATFD = getATFD();
		int CPFD = getCPFD();
		double LDA = round(getLDA());
		if(ATFD>4 && CPFD<=2 && LDA<0.33 && cyclomaticComplexity >=3 && length>=50){
			return ParentClass.getContainingFile().getFileName()+","+ParentClass.getClassName()+","+signature+",Feature Envy,"+
					"ATFD = "+ATFD+",CPFD = "+CPFD+",LDA = "+LDA+",cyclo = "+cyclomaticComplexity+",LOC = "+
					length;
		}else return "";
    }

    public int getNOAV(){
		Set<Attribute> s = new HashSet<>();
		s.addAll(getExtAttributesAccessed());
		s.addAll(AccessedAttributes);
		s.addAll(ProtectedAttributes);
		return (int)s.stream()
				.filter(attribute -> attribute!=null)
				.filter(Attribute::isViable)
				.filter(Attribute::isUserDefined)
				.distinct()
				.count();
	}

	public int getCalls(){
		Set<Method> s = new HashSet<>();
		s.addAll(CalledMethods);
		s.addAll(ProtectedMethods);
		return (int)s.stream()
				.filter(method -> method!=null)
				.filter(method -> !method.isAccessor())
				.filter(Method::isNotDefaultConstructor)
				.filter(method -> method.getParent()!=null && method.getParent().isUserDefined())
				.distinct()
				.count();
	}

    public String isBlob(){
		int NOAV = getNOAV();
		int Calls = getCalls();
		if(NOAV>10 && Calls>=10 && cyclomaticComplexity>=7 && length>=100){
			return ParentClass.getContainingFile().getFileName()+","+ParentClass.getClassName()+","+signature+",Blob,"+
					"NOAV = "+NOAV+",Calls = "+Calls+",cyclo = "+cyclomaticComplexity+",LOC = "+ length;
		}else return "";
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

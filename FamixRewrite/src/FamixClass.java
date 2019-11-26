import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

class FamixClass {
	private long ID;
	private String ClassName;
	private boolean isParametrizedType=false;
	private Set<FamixClass> ParametrizedClasses=new HashSet<FamixClass> ();
	private long containerID;
	private ArrayList<FamixClass> containedTypes=new ArrayList<FamixClass>();
	private String Name;
	private ArrayList<Method> OverrideOrSpecializeMethods=new ArrayList<Method> ();
	private ArrayList<Method> ContainedMethods=new ArrayList<Method> ();
	private ArrayList<Attribute> ContainedAttributes=new ArrayList<Attribute> ();
	private ArrayList<FamixClass> InheritedClasses=new ArrayList<FamixClass> ();
	private Map<FamixClass,Method> OverrideClassAndMethod=new HashMap<FamixClass,Method>();
	private Map<FamixClass,Integer> InheritanceRelations=new HashMap<FamixClass,Integer> ();
	private Set<Method> CalledMethods=new HashSet<Method>();
	private Set<Attribute> AccessedAttributes=new HashSet<Attribute> ();
	private Set<Method> ProtectedCalledMethods=new HashSet<Method> ();
	private Set<Attribute> ProtectedAccessedAttributes=new HashSet<Attribute>();
	private Map<FamixClass,Integer> Calls=new HashMap<FamixClass,Integer> ();
	private Map<FamixClass,Integer> Accesses=new HashMap<FamixClass,Integer> ();
	private Map<FamixClass,Triplet> ClassesRelations=new HashMap<FamixClass,Triplet> ();
	private Map<FamixClass,ArrayList<Method>> OverrideRelations=new HashMap<FamixClass,ArrayList<Method>> ();
	private ContainingFile File;
	private boolean isFromJava=true;
	private boolean Interface;

	public FamixClass(boolean Interface, long ID, String Name,long containerID) {
		this.ID=ID;
		this.Name=Name;
		this.Interface=Interface;
		this.containerID=containerID;
		ClassName=Name;
	}

	public boolean isInterface(){
	    return Interface;
    }
	public void addContainedType(FamixClass c){
        containedTypes.add(c);
    }

    public ArrayList<FamixClass> getContainedTypes(){
	    return containedTypes;
    }
	public long getContainerID(){
	    return containerID;
    }
	public Set<Method> getCalledMethods(){
		return CalledMethods;
	}

	public Set<Attribute> getProtectedAccessedAttributes(){
		return ProtectedAccessedAttributes;
	}

	public Set<Method> getCalledProtectedMethods(){
		return ProtectedCalledMethods;
	}

	public Set<Attribute> getAccessedAttributes(){
		return AccessedAttributes;
	}

	public void addMethod(Method m) {
		ContainedMethods.add(m);
	}

	public void addAttribute(Attribute a) {
		ContainedAttributes.add(a);
	}

	public void setOverrideOrSpecialize() {
		Iterator<Method> it=ContainedMethods.iterator();
		while(it.hasNext()) {
			Method m=it.next();
			if(!m.getSignature().contains("<init>")) {
                FamixClass c = this.isOverrideOrSpecialize(m);
                if (c != null) {
                    OverrideOrSpecializeMethods.add(m);
                    addClassToMap(InheritanceRelations, c, 2);
                    if (OverrideRelations.containsKey(c)) {
                        OverrideRelations.get(c).add(m);
                    } else {
                        ArrayList<Method> adder = new ArrayList<Method>();
                        adder.add(m);
                        OverrideRelations.put(c, adder);
                    }
                }
            }
		}
		setInherits();
		
	}

	public Map<FamixClass,ArrayList<Method>> getOverrideRelations(){
		return OverrideRelations;
	}

	private void setInherits() {
		Iterator<FamixClass> inheritIterator=InheritedClasses.iterator();
		while(inheritIterator.hasNext()) {
			FamixClass c=inheritIterator.next();
			if(!InheritanceRelations.containsKey(c)) {
				InheritanceRelations.put(c,1);
			}
		}
	}

	public void addParametrizedClass(FamixClass c){
		ParametrizedClasses.add(c);
	}

	public Set<FamixClass> getParametrizedClasses(){
		return ParametrizedClasses;
	}

	public ArrayList<Method> getOverrideOrSpecialize(){
		return OverrideOrSpecializeMethods;
	}

	public boolean getContainer(){
		return isParametrizedType;
	}

	public int getContainedNumber(FamixClass c){
		Iterator<FamixClass> it=containedTypes.iterator();
		int cnt=0;
		while(it.hasNext()){
			FamixClass c1=it.next();
			cnt++;
			if(c1.equals(c)) return	cnt;
		}
		return cnt;
	}

	public boolean hasMethod(String signature) {
		Iterator<Method> it=ContainedMethods.iterator();
		while(it.hasNext()) {
			Method m=it.next();
			if(m.getSignature().equals(signature)) return true;
		}
		
		return false;
	}

	public FamixClass isOverrideOrSpecialize(Method m) {
		Iterator<FamixClass> it=InheritedClasses.iterator();
		while(it.hasNext()) {
			FamixClass c=it.next();
			if(c.hasMethod(m.getSignature())) return c;
			FamixClass b=c.isOverrideOrSpecialize(m);
			if(b!=null) return b;
		}
		return null;
	}

	public long getID() {
		return ID;
	}

	public String getName() {
		return Name;
	}

	public void setFile(ContainingFile f) {
		if(f!=null) isFromJava=false;
		File=f;
		Name=f.getFileName()+"/"+Name;
	}

	public void addInheritedClass(FamixClass c) {
		InheritedClasses.add(c);
	}

	public ArrayList<FamixClass> getInheritedClasses(){
		return InheritedClasses;
	}

	public boolean equals(Object o) {
		if(o instanceof FamixClass) {
			FamixClass c=(FamixClass) o;
			if(c.getID()==ID) return true;
		}
		return false;
	}

	public String getClassName(){
		return ClassName;
	}
	public double getAMW(){
	    return ContainedMethods.stream()
				.filter(method-> !method.getSignature().contains("<init>"))
                .mapToInt(Method::getCyclomaticComplexity)
                .average()
                .orElse(0);
    }

    public int getNOAV(){
		return ProtectedAccessedAttributes.size()+AccessedAttributes.size();
	}

	public Double round(Double d){
		return ((double) ((int) (d*100)))/100;
	}

    public int getWMC(){
		return ContainedMethods.stream()
				.filter(method -> !method.getSignature().contains("<init>"))
				.mapToInt(Method::getCyclomaticComplexity)
				.sum();
	}

	public int getNOM(){
		return (int) ContainedMethods.stream()
				.filter(method -> !method.getSignature().contains("<init>"))
				.count();
	}

	public int getNProtMA(){
        return (int)AccessedAttributes.stream()
                .filter(attribute->attribute!=null)
                .filter(attribute -> attribute.isProtected())
                .count()+
                (int)ProtectedAccessedAttributes.stream()
                        .filter(attribute->attribute!=null)
                        .filter(attribute -> attribute.isProtected())
                        .count();
    }

    public int getNProtMM(){
	    return (int)ContainedMethods.stream()
                .filter(method -> method.isProtected())
                .count();
    }
    public int getNProtM(){
	    return getNProtMA()+getNProtMM();
    }
	public String getMetrics(){
		String st="";
		st=st+round(getAMW())+","+getWMC()+","+getNOM()+","+getNOPA()+","+getNOAV()+","+getNProtM();
		return st;
	}


	public FamixClass getExtender(){
	    if(InheritedClasses.size()>=1) return InheritedClasses.get(0);
	    else return null;
    }
	public void setContainer(){
		isParametrizedType=true;
	}

	public int getNOPA(){
		return (int)AccessedAttributes.stream()
				.filter(attribute -> attribute!=null)
				.filter(Attribute::isPublic)
				.count()+
                (int)ProtectedAccessedAttributes.stream()
                        .filter(attribute -> attribute!=null)
                        .filter(Attribute::isPublic)
                        .count();
	}

	public boolean isMethodInherited(Method m) {
		if(OverrideOrSpecializeMethods.contains(m)) return true;
		return false;
	}

	public void set() {
		Iterator<Method> it=ContainedMethods.iterator();
		while(it.hasNext()) {
			Method m=it.next();
			Set<Method> SCM=m.getCalledMethods();
			if(SCM!=null) CalledMethods.addAll(SCM);
			Set<Method> PCM=m.getProtectedMethodsCalled();
			if(PCM!=null) ProtectedCalledMethods.addAll(PCM);
			Set<Attribute> SCA=m.getAccessedAttributes();
			if(SCA!=null) AccessedAttributes.addAll(SCA);
			Set<Attribute> PCA=m.getProtectedAttributesAccessed();
			if(PCA!=null) ProtectedAccessedAttributes.addAll(PCA);
		}
	}

	public void addProtectedAttribute(Attribute a){
	    ProtectedAccessedAttributes.add(a);
    }

    public void addAccessedAttribute(Attribute a){
	    AccessedAttributes.add(a);
    }
	private void setMapForAccessedProtectedAttributes() {
		Iterator<Attribute> it=ProtectedAccessedAttributes.iterator();
		while(it.hasNext()) {
			Attribute a=it.next();
			FamixClass c=a.getType();
			addClassToMap(InheritanceRelations,c,1);
		}
	}

	private void setMapForCalledProtectedMethods() {
		Iterator<Method> it=ProtectedCalledMethods.iterator();
		while(it.hasNext()) {
			Method m=it.next();
			FamixClass c=m.getParent();
			addClassToMap(InheritanceRelations,c,1);
		}
	}

	private void setMapForCall() {
		Iterator<Method> CalledIterator=CalledMethods.iterator();
		while(CalledIterator.hasNext()) {
			Method m=CalledIterator.next();
			FamixClass c=m.getParent();
			addClassToMap(Calls,c,1);
		}
	}

	private void setMapForAccess() {
		Iterator<Attribute> AccessIterator=AccessedAttributes.iterator();
		while(AccessIterator.hasNext()) {
			Attribute a=AccessIterator.next();
			FamixClass c=a.getType();
			addClassToMap(Accesses,c,1);
		}
	}

	private void setTriplets() {
		for(Map.Entry<FamixClass,Integer> entry:Calls.entrySet()) {
			FamixClass c=entry.getKey();
			int i=entry.getValue();
			Triplet t=new Triplet();
			t.setNoCalls(i);
			ClassesRelations.put(c,t);
		}
		for(Map.Entry<FamixClass,Integer> entry:Accesses.entrySet()) {
			FamixClass c=entry.getKey();
			int i=entry.getValue();
			if(ClassesRelations.containsKey(c)) {
				Triplet t=ClassesRelations.get(c);
				t.setNoAccesses(i);
			}
			else {
				Triplet t=new Triplet();
				t.setNoCalls(0);
				t.setNoAccesses(i);
				ClassesRelations.put(c,t);
			}
		}
		for(Map.Entry<FamixClass,Integer> entry:InheritanceRelations.entrySet()) {
			FamixClass c=entry.getKey();
			int i=entry.getValue();
			if(ClassesRelations.containsKey(c)) {
				Triplet t=ClassesRelations.get(c);
				t.setNoInheritanceRelations(i);
			}
			else {
				Triplet t=new Triplet();
				t.setNoCalls(0);
				t.setNoAccesses(0);
				t.setNoInheritanceRelations(i);
				ClassesRelations.put(c,t);
			}
		}
	}

	public ContainingFile getContainingFile(){
		return File;
	}

	public boolean checkIfIsFromJava(){
		return isFromJava;
	}

	private void addClassToMap(Map<FamixClass,Integer> m, FamixClass c, int startValue) {
		if(m.containsKey(c)) {
			m.put(c,m.get(c)+1);
		} else m.put(c, startValue);
	}
	
	public int hashCode() {
		return (int) (ID ^ (ID >>>32));
	}

	public Method getInit(){
	    return ContainedMethods.stream()
                .filter(method -> method.getSignature().equals("<init>"))
                .findFirst()
                .orElse(null);
    }
	
	public String toString() {
		String st="";
		for(Map.Entry<FamixClass,Triplet> entry:ClassesRelations.entrySet()) {
			FamixClass c=entry.getKey();
			Triplet t=entry.getValue();
			if(!this.equals(c)) {
				if (c != null) {
					 if(!c.checkIfIsFromJava() && !this.checkIfIsFromJava())st = st + Name + "," + c.getName() + "," + t + "\n";
				}
			}
		}
		return st;
	}
}

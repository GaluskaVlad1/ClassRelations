import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

class FamixClass {
	private long ID;
	private String Name;
	private ArrayList<Method> OverrideOrSpecializeMethods=new ArrayList<Method> ();
	private ArrayList<Method> ContainedMethods=new ArrayList<Method> ();
	private ArrayList<Attribute> ContainedAttributes=new ArrayList<Attribute> ();
	private ArrayList<FamixClass> InheritedClasses=new ArrayList<FamixClass> ();
	private Map<FamixClass,Integer> InheritanceRelations=new HashMap<FamixClass,Integer> ();
	private Set<Method> CalledMethods=new HashSet<Method>();
	private Set<Attribute> AccessedAttributes=new HashSet<Attribute> ();
	private Set<Method> ProtectedCalledMethods=new HashSet<Method> ();
	private Set<Attribute> ProtectedAccessedAttributes=new HashSet<Attribute>();
	private Map<FamixClass,Integer> Calls=new HashMap<FamixClass,Integer> ();
	private Map<FamixClass,Integer> Accesses=new HashMap<FamixClass,Integer> ();
	private Map<FamixClass,Triplet> ClassesRelations=new HashMap<FamixClass,Triplet> ();
	private ContainingFile File;
	private boolean Interface;

	public FamixClass(boolean Interface, long ID, String Name) {
		this.ID=ID;
		this.Name=Name;
		this.Interface=Interface;
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
			FamixClass c=this.isOverrideOrSpecialize(m);
			if(c!=null) {
				OverrideOrSpecializeMethods.add(m);
				addClassToMap(InheritanceRelations,c,2);
			}
		}
		setInherits();
		
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

	public ArrayList<Method> getOverrideOrSpecialize(){
		return OverrideOrSpecializeMethods;
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
		File=f;
		Name=f.getFileName()+"/"+Name;
	}

	public void addInheritedClass(FamixClass c) {
		InheritedClasses.add(c);
	}

	public boolean equals(Object o) {
		if(o instanceof FamixClass) {
			FamixClass c=(FamixClass) o;
			if(c.getID()==ID) return true;
		}
		return false;
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
		setMapForCall();
		setMapForAccess();
		setMapForCalledProtectedMethods();
		setMapForAccessedProtectedAttributes();
		setTriplets();
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

	private void addClassToMap(Map<FamixClass,Integer> m, FamixClass c, int startValue) {
		if(m.containsKey(c)) {
			m.put(c,m.get(c)+1);
		} else m.put(c, startValue);
	}
	
	public int hashCode() {
		return (int) (ID ^ (ID >>>32));
	}
	
	public String toString() {
		String st="";
		for(Map.Entry<FamixClass,Triplet> entry:ClassesRelations.entrySet()) {
			FamixClass c=entry.getKey();
			Triplet t=entry.getValue();
			if(c!=null)  st=st+Name+","+c.getName()+","+t+"\n";
			else st=st+Name+",ImportedCalls,"+t+"\n";
		}
		return st;
	}
}

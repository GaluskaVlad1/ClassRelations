import java.io.File;
import java.util.*;
class ContainingFile {
	private String FileName;
	private ArrayList<Long> ContainedIDs=new ArrayList<Long> ();
	private ArrayList<FamixClass> ContainedClasses=new ArrayList<FamixClass> ();
	private Set<Method> CalledMethods=new HashSet<>();
	private Set<Method> CalledProtectedMethods=new HashSet<>();
	private Set<Attribute> AccessedAttributes=new HashSet<>();
	private Set<Attribute> AccessedProtectedAttributes=new HashSet<>();
	private Map<ContainingFile,Integer> FileCallRelations=new HashMap<ContainingFile,Integer>();
	private Map<ContainingFile,Integer> FileAccessRelations=new HashMap<ContainingFile,Integer>();
	private Map<ContainingFile,Integer> FileInheritanceRelations=new HashMap<ContainingFile,Integer>();
	private Map<ContainingFile,Triplet> FileRelations=new HashMap<ContainingFile,Triplet>();
	private Map<ContainingFile,Set<Method>>  FileOverrideRelations=new HashMap<ContainingFile,Set<Method>>();
	private void setAllSets(){
		Iterator<FamixClass> it=ContainedClasses.iterator();
		while(it.hasNext()){
			FamixClass c=it.next();
			setCalledMethods(c);
			setCalledProtectedMethods(c);
			setAccessedAttributes(c);
			setAccessedProtectedAttributes(c);
			setInheritanceRelations(c);
			setOverrideRelations(c);
		}
	}

	private void setOverrideRelations(FamixClass c){
		Map<FamixClass,ArrayList<Method>> OverrideRelations=c.getOverrideRelations();
		for(Map.Entry<FamixClass,ArrayList<Method>> entry:OverrideRelations.entrySet()){
			FamixClass extender=entry.getKey();
			ArrayList<Method> methods=entry.getValue();
			ContainingFile f=extender.getContainingFile();
			if(f==null) return;
			if(FileOverrideRelations.containsKey(f)){
				FileOverrideRelations.get(f).addAll(methods);
			}else{
				Set<Method> s=new HashSet<Method>();
				s.addAll(methods);
				FileOverrideRelations.put(f,s);
			}
		}
	}

	private void setOverrideRelationsToFile(){
		for(Map.Entry<ContainingFile,Set<Method>> entry:FileOverrideRelations.entrySet()){
			ContainingFile f=entry.getKey();
			Set<Method> s=entry.getValue();
			if(FileInheritanceRelations.containsKey(f)) {
				FileInheritanceRelations.put(f, FileInheritanceRelations.get(f) + s.size());
			}else{
				FileInheritanceRelations.put(f,s.size()+1);
			}
		}
	}

	public void setEverything(){
		setAllSets();
		setCallRelations();
		setAccessRelations();
		setCalledProtectedMethodRelations();
		setAccessedProtectedAttributeRelations();
		setOverrideRelationsToFile();
		setFileRelations();
	}

	private void setFileRelations(){
		for(Map.Entry<ContainingFile,Integer> entry:FileCallRelations.entrySet()){
			ContainingFile f=entry.getKey();
			int value=entry.getValue();
			Triplet t=new Triplet();
			t.setNoCalls(value);
			FileRelations.put(f,t);
		}
		for(Map.Entry<ContainingFile,Integer> entry:FileAccessRelations.entrySet()){
			ContainingFile f= entry.getKey();
			int value=entry.getValue();
			if(FileRelations.containsKey(f)){
				FileRelations.get(f).setNoAccesses(value);
			}else{
				Triplet t=new Triplet();
				t.setNoAccesses(value);
				FileRelations.put(f,t);
			}
		}
		for(Map.Entry<ContainingFile,Integer> entry:FileInheritanceRelations.entrySet()){
			ContainingFile f=entry.getKey();
			int value=entry.getValue();
			if(FileRelations.containsKey(f)) {
				FileRelations.get(f).setNoInheritanceRelations(value);
			}else{
				Triplet t=new Triplet();
				t.setNoInheritanceRelations(value);
				FileRelations.put(f,t);
			}
		}
	}
	private void setCallRelations(){
		Iterator<Method> it=CalledMethods.iterator();
		while(it.hasNext()){
			Method m=it.next();
			FamixClass c=m.getParent();
			ContainingFile f=null;
			if(c!=null) f=c.getContainingFile();
			if(f!=null && f!=this){
				AddFileToMap(FileCallRelations,f,1);
			}
		}
	}

	private void setAccessRelations(){
		Iterator<Attribute> it=AccessedAttributes.iterator();
		while(it.hasNext()){
			Attribute a=it.next();
			FamixClass c=a.getType();
			ContainingFile f=null;
			if(c!=null) f=c.getContainingFile();
			if(f!=null && f!=this) AddFileToMap(FileAccessRelations,f,1);
		}
	}

	private void setCalledProtectedMethodRelations(){
		Iterator<Method> it=CalledProtectedMethods.iterator();
		while(it.hasNext()){
			Method m=it.next();
			FamixClass c=m.getParent();
			ContainingFile f=null;
			if(c!=null) f=c.getContainingFile();
			if(f!=null && f!=this) AddFileToMap(FileInheritanceRelations,f,1);
		}
	}

	public void setFileName(String name){
		FileName=name;
	}

	private void setAccessedProtectedAttributeRelations(){
		Iterator<Attribute> it=AccessedProtectedAttributes.iterator();
		while(it.hasNext()){
			Attribute a=it.next();
			FamixClass c=a.getType();
			ContainingFile f=null;
			if(c!=null) c.getContainingFile();
			if(f!=null && f!=this) AddFileToMap(FileInheritanceRelations,f,1);
		}
	}

	private void setInheritanceRelations(FamixClass c){
		ArrayList<FamixClass> InheritedClasses=c.getInheritedClasses();
		Iterator<FamixClass> it=InheritedClasses.iterator();
		while(it.hasNext()){
			FamixClass InheritClass=it.next();
			ContainingFile f=InheritClass.getContainingFile();
			if(f!=null && f!=this) AddFileToMap(FileInheritanceRelations,f,1);
		}
	}

	private void AddFileToMap(Map<ContainingFile,Integer> map,ContainingFile target,int baseInput){
		if(target==this) return;
		if(!map.containsKey(target)) map.put(target,baseInput);
		else map.put(target,map.get(target)+1);
	}

	public void setAccessedProtectedAttributes(FamixClass c){
		Set<Attribute> classAccessedProtectedAttributes=c.getProtectedAccessedAttributes();
		AccessedProtectedAttributes.addAll(classAccessedProtectedAttributes);
	}

	private void setAccessedAttributes(FamixClass c){
		Set<Attribute> classAccessedAttributes=c.getAccessedAttributes();
		AccessedAttributes.addAll(classAccessedAttributes);
	}

	private void setCalledMethods(FamixClass c){
		Set<Method> classCalledMethods=c.getCalledMethods();
		CalledMethods.addAll(classCalledMethods);
	}

	private void setCalledProtectedMethods(FamixClass c){
		Set<Method> classProtectedMethods=c.getCalledProtectedMethods();
		CalledProtectedMethods.addAll(classProtectedMethods);
	}

	public void addClass(FamixClass c){
		ContainedClasses.add(c);
	}

	public ContainingFile(String FileName) {
		this.FileName=FileName;
	}

	public void addID(long ID) {
		ContainedIDs.add(ID);
	}

	public String getFileName() {
		return FileName;
	}

	public ArrayList<Long> getContainedIDs(){
		return ContainedIDs;
	}

	public String toString() {
		String st="";
		for(Map.Entry<ContainingFile,Triplet> entry:FileRelations.entrySet()){
			ContainingFile f=entry.getKey();
			Triplet t=entry.getValue();
			st=st+FileName+","+f.getFileName()+","+t+"\n";
		}
		return st;
	}
}

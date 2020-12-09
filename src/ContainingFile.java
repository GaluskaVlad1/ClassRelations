import java.util.*;
class ContainingFile {
	private String FileName;
	private ArrayList<Long> ContainedIDs=new ArrayList<Long> ();
	private ArrayList<FamixClass> ContainedClasses=new ArrayList<FamixClass> ();
	private Set<Method> CalledMethods=new HashSet<>();
	private Set<Method> CalledProtectedMethods=new HashSet<>();
	private Set<Method> ContainedMethods=new HashSet<>();
	private Set<Attribute> AccessedAttributes=new HashSet<>();
	private Set<Attribute> AccessedProtectedAttributes=new HashSet<>();
	private Set<Attribute> DeclaredAttributes=new HashSet<>();
	private Set<Attribute> RealAccessedAttributes=new HashSet<Attribute> ();
	private Map<ContainingFile,Integer> FileCallRelations=new HashMap<ContainingFile,Integer>();
	private Map<ContainingFile,Integer> FileAccessRelations=new HashMap<ContainingFile,Integer>();
	private Map<ContainingFile,Integer> FileInheritanceRelations=new HashMap<ContainingFile,Integer>();
	private Map<ContainingFile,Integer> FileDeclarations=new HashMap<ContainingFile,Integer>();
	private Map<ContainingFile,Integer> FileStrictDataRelations=new HashMap<ContainingFile,Integer>();
	private Map<ContainingFile, Tuplet> FileRelations=new HashMap<ContainingFile, Tuplet>();
	private Map<ContainingFile,Set<Method>>  FileOverrideRelations=new HashMap<ContainingFile,Set<Method>>();
	private Map<ContainingFile,Integer> FileReturnRelations=new HashMap<ContainingFile,Integer>();
	private Map<ContainingFile,Integer> FileAllRelations=new HashMap<ContainingFile,Integer>();
	private Map<FamixClass,Integer> ExternalCalls= new HashMap<>();
	private Map<FamixClass,Integer> ExternalAccesses=new HashMap<>();
	private Map<FamixClass,Integer> ExternalReturns=new HashMap<>();
	private Map<FamixClass,Integer> ExternalDeclarations=new HashMap<>();
	private Map<FamixClass,ExternalTuplet> ExternalRelations=new HashMap<>();
    private static int cnt=0;

	private void setAllSets(){
		Iterator<FamixClass> it=ContainedClasses.iterator();
		while(it.hasNext()){
			FamixClass c=it.next();
			setCalledMethods(c);
			setContainedMethods(c);
			setCalledProtectedMethods(c);
			setAccessedAttributes(c);
			setRealAccessedAttributes(c);
			setAccessedProtectedAttributes(c);
			setInheritanceRelations(c);
			setOverrideRelations(c);
			setDeclaredAttributes(c);
		}
	}

	private void setReturnRelations(){
		setReturnForMethods(CalledMethods);
		setReturnForMethods(CalledProtectedMethods);
		setReturnForMethods(ContainedMethods);
	}

	private void setRealAccessedAttributes(FamixClass c){
		RealAccessedAttributes.addAll(c.getRealAccessedAttributes());
	}

	private void setContainedMethods(FamixClass c){
	    ContainedMethods.addAll(c.getContainedMethods());
    }

    private void setDeclaredAttributes(FamixClass c){
        DeclaredAttributes.addAll(c.getDeclaredAttributes());
    }

	private void setReturnForMethods(Set<Method> s){
		Iterator<Method> it=s.iterator();
		while(it.hasNext()){
			Method m=it.next();
			FamixClass c=m.getDeclaredType();
			if(c!=null){
				if(!c.isViable()){
					c=c.getExtender();
				}
				if(c==null) continue;
				if(c.getContainingFile()!=null && c.getContainingFile()!=this) {
					if (FileReturnRelations.containsKey(c.getContainingFile())) {
						FileReturnRelations.put(c.getContainingFile(), FileReturnRelations.get(c.getContainingFile()) + 1);
					} else {
						FileReturnRelations.put(c.getContainingFile(), 1);
					}
				}
				if(c.getContainingFile()==null && c.getNamespace()!=null){
				    if(ExternalReturns.containsKey(c)){
				        ExternalReturns.put(c,ExternalReturns.get(c)+1);
                    }else{
				        ExternalReturns.put(c,1);
                    }
                }
			}
		}
	}

	private void setOverrideRelations(FamixClass c){
		Map<FamixClass,ArrayList<Method>> OverrideRelations=c.getOverrideRelations();
		for(Map.Entry<FamixClass,ArrayList<Method>> entry:OverrideRelations.entrySet()){
			FamixClass extender=entry.getKey();
			ArrayList<Method> methods=entry.getValue();
			ContainingFile f=extender.getContainingFile();
			if(f==null || f==this) return;
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
		setReturnRelations();
        setDeclarationRelations();
		setAccessRelations();
		setCalledProtectedMethodRelations();
		setAccessedProtectedAttributeRelations();
		setOverrideRelationsToFile();
		setAllRelations();
		setFileRelations();
		setExternals();
	}

	private void setExternals(){
        for(Map.Entry<FamixClass,Integer> entry:ExternalCalls.entrySet()){
            FamixClass c=entry.getKey();
            int value=entry.getValue();
            ExternalTuplet t=new ExternalTuplet();
            t.calls=value;
            ExternalRelations.put(c,t);
        }
        for(Map.Entry<FamixClass,Integer> entry:ExternalAccesses.entrySet()){
            FamixClass c= entry.getKey();
            int value=entry.getValue();
            if(ExternalRelations.containsKey(c)){
                ExternalTuplet t=ExternalRelations.get(c);
                t.accesses=value;
            }else{
                ExternalTuplet t=new ExternalTuplet();
                t.accesses=value;
                ExternalRelations.put(c,t);
            }
        }
        for(Map.Entry<FamixClass,Integer> entry:ExternalReturns.entrySet()){
            FamixClass c= entry.getKey();
            int value=entry.getValue();
            if(ExternalRelations.containsKey(c)){
                ExternalTuplet t=ExternalRelations.get(c);
                t.returns=value;
            }else{
                ExternalTuplet t=new ExternalTuplet();
                t.returns=value;
                ExternalRelations.put(c,t);
            }
        }
        for(Map.Entry<FamixClass,Integer> entry:ExternalDeclarations.entrySet()){
            FamixClass c= entry.getKey();
            int value=entry.getValue();
            if(ExternalRelations.containsKey(c)){
                ExternalTuplet t=ExternalRelations.get(c);
                t.declarations=value;
            }else{
                ExternalTuplet t=new ExternalTuplet();
                t.declarations=value;
                ExternalRelations.put(c,t);
            }
        }
    }

	private void setAllRelations(){
		Iterator<Attribute> it=RealAccessedAttributes.iterator();
		while(it.hasNext()){
			Attribute a=it.next();
			FamixClass c=a.getType();
			ContainingFile f=null;
			if(c!=null) f=c.getContainingFile();
			if(f!=null && f!=this) {
				AddFileToMap(FileAllRelations,f,1);
			}
		}
	}

	private void setDeclarationRelations(){
        Iterator<Attribute> it=DeclaredAttributes.iterator();
        while(it.hasNext()){
            Attribute a=it.next();
            FamixClass c=a.getType();
            if(c==null) continue;
            ContainingFile f=null;
            if(c!=null) f=c.getContainingFile();
            if(f!=null){
                AddFileToMap(FileDeclarations,f,1);
            }
            if(f==null && c.getNamespace()!=null){
                if(ExternalDeclarations.containsKey(c)){
                    ExternalDeclarations.put(c,ExternalDeclarations.get(c)+1);
                }else{
                    ExternalDeclarations.put(c,1);
                }
            }
        }
    }

	private void setFileRelations(){
		for(Map.Entry<ContainingFile,Integer> entry:FileCallRelations.entrySet()){
			ContainingFile f=entry.getKey();
			int value=entry.getValue();
			Tuplet t=new Tuplet();
			t.setNoCalls(value);
			t.setNoAll(value);
			FileRelations.put(f,t);
		}
		for(Map.Entry<ContainingFile,Integer> entry:FileAccessRelations.entrySet()){
			ContainingFile f= entry.getKey();
			int value=entry.getValue();
			if(FileRelations.containsKey(f)){
			    Tuplet t=FileRelations.get(f);
				t.setNoAccesses(value);
				t.setNoAll(t.getNoCalls()+value);
			}else{
				Tuplet t=new Tuplet();
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
				Tuplet t=new Tuplet();
				t.setNoInheritanceRelations(value);
				FileRelations.put(f,t);
			}
		}
		for(Map.Entry<ContainingFile,Integer> entry:FileReturnRelations.entrySet()){
			ContainingFile f= entry.getKey();
			int value=entry.getValue();
			if(FileRelations.containsKey(f)){
				FileRelations.get(f).setNoReturns(value);
			}else{
				Tuplet t=new Tuplet();
				t.setNoReturns(value);
				FileRelations.put(f,t);
			}
		}
		for(Map.Entry<ContainingFile,Integer> entry:FileDeclarations.entrySet()){
		    ContainingFile f=entry.getKey();
		    int value=entry.getValue();
		    if(FileRelations.containsKey(f)){
		        FileRelations.get(f).setNoDeclarations(value);
            }else{
		        Tuplet t=new Tuplet();
		        t.setNoDeclarations(value);
		        FileRelations.put(f,t);
            }
        }
		for(Map.Entry<ContainingFile,Integer> entry:FileStrictDataRelations.entrySet()){
			ContainingFile f=entry.getKey();
			int value=entry.getValue();
			if(FileRelations.containsKey(f)){
				FileRelations.get(f).setNoStrictData(value);
			}else{
				Tuplet t=new Tuplet();
				t.setNoDeclarations(value);
				FileRelations.put(f,t);
			}
		}
		for(Map.Entry<ContainingFile,Integer> entry:FileAllRelations.entrySet()){
			ContainingFile f=entry.getKey();
			int value=entry.getValue();
			if(FileRelations.containsKey(f)){
				Tuplet t=FileRelations.get(f);
				t.setNoAll(t.getNoAccesses()+t.getNoCalls()+value);
			}else{
				Tuplet t=new Tuplet();
				t.setNoAll(value);
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
			if(c==null) continue;
			if(c!=null) f=c.getContainingFile();
			if(f!=null && f!=this && !m.isAccessor() && m.isNotDefaultConstructor()){
				AddFileToMap(FileCallRelations,f,1);
			}
            if(f==null && c.getNamespace()!=null){
                if(ExternalCalls.containsKey(c)){
                    ExternalCalls.put(c,ExternalCalls.get(c)+1);
                }else{
                    ExternalCalls.put(c,1);
                }
            }
		}
	}

	private void setAccessRelations(){
		Iterator<Attribute> it=AccessedAttributes.iterator();
		while(it.hasNext()){
			Attribute a=it.next();
			if(a==null) continue;
			FamixClass c=a.getContainerClass();
			if(c!=null && !c.isViable()) c=c.getExtender();
			ContainingFile f=null;
			if(c==null) continue;
			if(c!=null) f=c.getContainingFile();
			if(f!=null && f!=this) {
				AddFileToMap(FileAccessRelations,f,1);
				if(a.getType()!=null && a.getType().isUserDefined()) AddFileToMap(FileStrictDataRelations,f,1);
			}
            if(f==null && c.getNamespace()!=null){
                if(ExternalAccesses.containsKey(c)){
                    ExternalAccesses.put(c,ExternalAccesses.get(c)+1);
                }else{
                    ExternalAccesses.put(c,1);
                }
            }
		}
	}

	private void setCalledProtectedMethodRelations(){
		Iterator<Method> it=CalledProtectedMethods.iterator();
		while(it.hasNext()){
			Method m=it.next();
			FamixClass c=m.getParent();
			ContainingFile f=null;
			if(c!=null) f=c.getContainingFile();
			if(f!=null && f!=this && m.isNotDefaultConstructor() && !m.isAccessor()) AddFileToMap(FileInheritanceRelations,f,1);
            if(f==null && c.getNamespace()!=null){
                if(ExternalCalls.containsKey(c)){
                    ExternalCalls.put(c,ExternalCalls.get(c)+1);
                }else{
                    ExternalCalls.put(c,1);
                }
            }
		}
	}

	public String getExternals(){
        String st="";
        for(Map.Entry<FamixClass,ExternalTuplet> entry:ExternalRelations.entrySet()){
            if(cnt!=0) st=st+",";
            st=st+"\n\t{";
            cnt++;
            FamixClass c= entry.getKey();
            ExternalTuplet t=entry.getValue();
            st=st+"\n\t\t\"file\": \""+FileName+"\",";
            st=st+"\n\t\t\"dependency\": \""+c.getNamespace()+"."+c.getClassName()+"\",";
            st=st+t;
            st=st+"\n\t}";
        }
        return st;
    }

	public void setFileName(String name){
		FileName=name;
	}

	private void setAccessedProtectedAttributeRelations(){
		Iterator<Attribute> it=AccessedProtectedAttributes.iterator();
		while(it.hasNext()){
			Attribute a=it.next();
			if(a==null) continue;
			FamixClass c=a.getContainerClass();
			ContainingFile f=null;
			if(c!=null) f=c.getContainingFile();
			if(f!=null && f!=this) AddFileToMap(FileInheritanceRelations,f,1);
            if(f==null && c.getNamespace()!=null){
                if(ExternalAccesses.containsKey(c)){
                    ExternalAccesses.put(c,ExternalAccesses.get(c)+1);
                }else{
                    ExternalAccesses.put(c,1);
                }
            }
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
		for(Map.Entry<ContainingFile, Tuplet> entry:FileRelations.entrySet()){
			ContainingFile f=entry.getKey();
			Tuplet t=entry.getValue();
			st=st+FileName+","+f.getFileName()+","+t+"\n";
		}
		return st;
	}
}

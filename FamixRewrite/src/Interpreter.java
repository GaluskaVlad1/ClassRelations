import java.util.*;
class Interpreter {
	private ArrayList<FamixClass> Classes=new ArrayList<FamixClass> ();
	private ArrayList<Access> Accesses=new ArrayList<Access>();
	private ArrayList<Method> Methods=new ArrayList<Method> ();
	private ArrayList<Attribute> Attributes=new ArrayList<Attribute> ();
	private ArrayList<Invocation> Invocations=new ArrayList<Invocation> ();
	private ArrayList<Inheritance> Inherits=new ArrayList<Inheritance> ();
	private ArrayList<ContainingFile> Files=new ArrayList<ContainingFile> ();
	private ArrayList<Parameter> Parameters=new ArrayList<Parameter> ();
	private ArrayList<LocalVariable> LocalVariables=new ArrayList<LocalVariable>();
	private Reader r;
	public void setReader(Reader r) {
		this.r=r;
	}
	
	public void checkForType(String st) throws Exception{
		if(st==null) return;
		if(st.contains("(FAMIX.Class")||st.contains("FAMIX.PrimitiveType")||st.contains("FAMIX.Type")||st.contains("FAMIX.ParameterizableClass")) {
			interpretClass(st);
			return;
		}
		if(st.contains("(FAMIX.IndexedFileAnchor")) {
			interpretFile();
			return;
		}
		if(st.contains("(FAMIX.Method")) {
			interpretMethod(st);
			return;
		}
		if(st.contains("(FAMIX.Invocation")) {
			interpretInvocation();
			return;
		}
		if(st.contains("(FAMIX.Inheritance")) {
			interpretInheritance();
			return;
		}
		if(st.contains("(FAMIX.Attribute")||st.contains("(FAMIX.LocalVariable ") || st.contains("FAMIX.Parameter ")) {
			interpretAttribute(st);
			return;
		}
		if(st.contains("(FAMIX.Access ")){
			interpretAccess();
			return;
		}
	}

	private void interpretAccess() throws Exception{
		String st=r.getNextLine();
		long variableID=0;
		while(st!=null && !st.contains("(FAMIX.")){
			if(st.contains("variable ")) variableID=getID(st.toCharArray());
			st=r.getNextLine();
		}
		Access a=new Access(variableID);
		Accesses.add(a);
		checkForType(st);
	}

	private void interpretLocalVariable(String st) throws Exception{
		long ID=getID(st.toCharArray());
		long containerID=0,declaredID=0;
		st=r.getNextLine();
		while(st!=null && !st.contains("(FAMIX.")){
			if(st.contains("(parentBehaviouralEntity ")) containerID=getID(st.toCharArray());
			if(st.contains("(declaredType ")) declaredID=getID(st.toCharArray());
			st=r.getNextLine();
		}
		LocalVariable lv=new LocalVariable(ID,containerID,declaredID);
		LocalVariables.add(lv);
		checkForType(st);
	}

	private void interpretParameter(String st)throws Exception{
		long ID=getID(st.toCharArray());
		st=r.getNextLine();
		long declaredID=0,parentID=0;
		while(st!=null && !st.contains("FAMIX")){
			if(st.contains("(declaredType ")) declaredID=getID(st.toCharArray());
			if(st.contains("(parentBehaviouralEntity")) parentID=getID(st.toCharArray());
			st=r.getNextLine();
		}
		Parameter p=new Parameter(ID,declaredID,parentID);
		Parameters.add(p);
		checkForType(st);
	}

	private void interpretMethod(String st) throws Exception{
		Long MethodID=getID(st.toCharArray());
		st=r.getNextLine();
		Long parentType=0L;
		String signature="";
		String modifiers="";
		String kind="";
		int cyclomaticComplexity=1;
		while(st!=null && !st.contains("FAMIX.")) {
			if(st.contains("kind")) kind=getName(st.toCharArray());
			if(st.contains("modifiers ")) modifiers=getModifiers(st.toCharArray());
			if(st.contains("parentType")) parentType=getID(st.toCharArray());
			if(st.contains("signature"))signature=getName(st.toCharArray());
			if(st.contains("cyclomaticComplexity"))cyclomaticComplexity=(int)getID(st.toCharArray());
			st=r.getNextLine();
		}
		Method m=new Method(MethodID,parentType,signature,modifiers,kind,cyclomaticComplexity);
		Methods.add(m);
		checkForType(st);
	}
	
	private String getModifiers(char[] chArray) {
		String st="";
		int i;
		for(i=0;i<chArray.length;i++) {
			if(chArray[i]=='\'') {
				i++;
				do {
					st=st+chArray[i];
					i++;
				}while(chArray[i]!='\'');
				st=st+" ";
			}
		}
		return st;
	}
	
	private void interpretInvocation() throws Exception{
		String st=r.getNextLine();
		long candidateID=0;
		long receiverID=0;
		long senderID=0;
		while(st!=null && !st.contains("FAMIX.")) {
			if(st.contains("(candidates (ref:")) candidateID=getID(st.toCharArray());
			if(st.contains("(sender (ref:")) senderID=getID(st.toCharArray());
			if(st.contains("(receiver (ref:")) receiverID=getID(st.toCharArray());
			st=r.getNextLine();
		}
		if(candidateID!=0) {
			Invocation i = new Invocation(candidateID, senderID, receiverID);
			Invocations.add(i);
		}
		checkForType(st);
	}
	
	private void interpretInheritance() throws Exception{
		String st=r.getNextLine();
		while(!st.contains("subclass")) {
			st=r.getNextLine();
		}
		long SubclassID=getID(st.toCharArray());
		st=r.getNextLine();
		while(!st.contains("superclass")) {
			st=r.getNextLine();
		}
		long SuperclassID=getID(st.toCharArray());
		Inheritance i=new Inheritance(SubclassID,SuperclassID);
		Inherits.add(i);
	}
	
	private void interpretAttribute(String st) throws Exception{
		long AttributeID=getID(st.toCharArray());
		Attribute a=new Attribute(AttributeID,st.contains("LocalVariable"));
		st=r.getNextLine();
		long type=0,parentType=0;
		String modifiers="";
		while(st!=null && !st.contains("FAMIX.")) {
			if(st.contains("declaredType")) type=getID(st.toCharArray());
			if(st.contains("modifiers")) modifiers=getModifiers(st.toCharArray());
			if(st.contains("parent")) parentType=getID(st.toCharArray());
			st=r.getNextLine();
		}
		a.setContainerID(parentType);
		a.setType(type);
		a.setModifiers(modifiers);
		Attributes.add(a);
		checkForType(st);
	}
	
	private void interpretFile() throws Exception{
		String st=r.getNextLine();
		long ID=getID(st.toCharArray());
		while(!st.contains("fileName")) {
			st=r.getNextLine();
		}
		String FileName=getName(st.toCharArray());
		Iterator<ContainingFile> it=Files.iterator();
		boolean entered=false;
		while(it.hasNext()) {
			ContainingFile f=it.next();
			if(f.getFileName().equals(FileName)) {
				f.addID(ID);
				entered=true;
			}
		}
		if(entered==false) {
			ContainingFile f=new ContainingFile(FileName);
			f.addID(ID);
			Files.add(f);
		}
	}
	
	private void interpretClass(String st) throws Exception{
		long ClassID=getID(st.toCharArray());
		st=r.getNextLine();
		String ClassName=getName(st.toCharArray());
		st=r.getNextLine();
		boolean Interface=false;
		while(st!=null && !st.contains("isInterface") && !st.contains("FAMIX.")) {
			st = r.getNextLine();
		}
		if(st!=null && st.contains("isInterface")) Interface=true;
		FamixClass c=new FamixClass(Interface,ClassID,ClassName);
		Classes.add(c);
		checkForType(st);
	}
	
	public String getName(char[] chArray) throws Exception{
		ArrayList<Character> Name=new ArrayList<Character>();
		int i;
		boolean found=true;
		for(i=0;found ;i++) {
			if(i>=chArray.length){
				chArray=r.getNextLine().toCharArray();
				i=0;
			}
			if(chArray[i]=='\'') {
				do {
					i++;
					found=false;
					if(i==chArray.length){
						chArray=r.getNextLine().toCharArray();
						i=0;
					}
					if(chArray[i]!='\'') Name.add(chArray[i]);
				}while (chArray[i]!='\'');
			}
		}
		return charListToString(Name);
	}

	private long getID(char[] chArray) {
		ArrayList<Character> IdDigits=new ArrayList<Character>();
		int i;
		for(i=0;i<chArray.length;i++) {
			while(chArray[i]>='0' && chArray[i]<='9') {
				IdDigits.add(chArray[i]);
				i++;
			}
		}
		return digitsListToLong(IdDigits);
	}
	
	private String charListToString(ArrayList<Character> ClassName) {
		Iterator<Character> it=ClassName.iterator();
		String st="";
		while(it.hasNext()) {
			st=st+it.next();
		}
		return st;
	}
	
	private long digitsListToLong(ArrayList<Character> IdDigits) {
		Iterator<Character> it=IdDigits.iterator();
		if(IdDigits.isEmpty()) return 0L;
		String st="";
		while(it.hasNext()) {
		    char c=it.next();
			st=st+c;
		}
		return Long.parseLong(st);
	}

	private FamixClass getClassByID(long ID) {
		Iterator<FamixClass> it=Classes.iterator();
		while(it.hasNext()) {
			FamixClass c=it.next();
			if(c.getID()==ID) return c;
		}
		return null;
	}
	
	public void setClassesMethods() {
		Iterator<Method> mit=Methods.iterator();
		while(mit.hasNext()) {
			Method m=mit.next();
			long ID=m.getParentType();
			FamixClass c=getClassByID(ID);
			if(c!=null) {
				c.addMethod(m);
				m.setClass(c);
			}
		}
	}

	public void setContainingFiles() {
		Iterator<ContainingFile> it=Files.iterator();
		while(it.hasNext()) {
			ContainingFile f=it.next();
			ArrayList<Long> IDs=f.getContainedIDs();
			Iterator<Long> IDiterator=IDs.iterator();
			while(IDiterator.hasNext()) {
				Long ID=IDiterator.next();
				FamixClass c=getClassByID(ID);
				if(c!=null) {
					c.setFile(f);
					f.addClass(c);
				}
			}
		}
	}
	
	public void setInheritanceRelations() {
		Iterator<Inheritance> it=Inherits.iterator();
		while(it.hasNext()) {
			Inheritance i=it.next();
			FamixClass subclass=getClassByID(i.getSubclassID());
			FamixClass superclass=getClassByID(i.getSuperclassID());
			if(subclass!=null && superclass!=null) {
				subclass.addInheritedClass(superclass);
			}
		}
	}
	
	public Method getMethodByID(long ID) {
		Iterator<Method> it=Methods.iterator();
		while(it.hasNext()) {
			Method m=it.next();
			if(m.getID()==ID) return m;
		}
		return null;
	}
	
	public Attribute getAttributeByID(long ID) {
		Iterator<Attribute> it=Attributes.iterator();
		while(it.hasNext()) {
			Attribute a=it.next();
			if(a.getID()==ID) return a;
		}
		return null;
	}
	
	private void setClassToAttribute() {
		Iterator<Attribute> it=Attributes.iterator();
		while(it.hasNext()) {
			Attribute a=it.next();
			long ID=a.getTypeID();
			FamixClass c=getClassByID(ID);
			a.setClass(c);
			Method m=getMethodByID(a.getContainerID());
			a.setContainerMethod(m);
		}
	}

	public void setInvocations() {
		Iterator<Invocation> it=Invocations.iterator();
		while(it.hasNext()) {
			Invocation i=it.next();
			Method candidate=getMethodByID(i.getCandidateID());
			Method sender=getMethodByID(i.getSenderID());
			if(!sender.isProtected()) candidate.addCalledMethod(sender);
			else candidate.addProtectedMethod(sender);
			if(i.hasReceiver()) {
				Attribute receiver=getAttributeByID(i.getReceiverID());
				if(receiver!=null) {
					if (receiver.isProtected()) candidate.addProtectedAttribute(receiver);
					else candidate.addAccessedAttribute(receiver);
				}
			}
		}
        Iterator<Attribute> it1=Attributes.iterator();
		while(it1.hasNext()) {
            Attribute a = it1.next();
            if (a.isLocalVariable()) {
                Method m = a.getContainerMethod();
                m.addAccessedAttribute(a);
            }
        }
	}

	private void setContainerToAttribute(){
		Iterator<Attribute> it=Attributes.iterator();
		while(it.hasNext()){
			Attribute a=it.next();
			FamixClass Container=getClassByID(a.getContainerID());
			if(Container!=null) {
			    Container.addAttribute(a);
            }else{
			    Method m=getMethodByID(a.getContainerID());
			    if(m!=null) {
                    FamixClass c = m.getParent();
                    if(c!=null) {
                        c.addAttribute(a);
                        Container = c;
                    }
                }
            }
			a.setContainer(Container);
		}
	}

	private void setParameterToContainers(){
		Iterator<Parameter> it=Parameters.iterator();
		while(it.hasNext()){
			Parameter p=it.next();
			FamixClass c=getClassByID(p.getDeclaredID());
			p.setDeclaredType(c);
			Method m=getMethodByID(p.getContainerMethodID());
			p.setContainerMethod(m);
			m.addParameter(p);
		}
	}

	private void setLVToContainers(){
		Iterator<LocalVariable> it=LocalVariables.iterator();
		while(it.hasNext()){
			LocalVariable lv=it.next();
			Method m=getMethodByID(lv.getContainerID());
			m.addLocalVariable(lv);
			lv.setContainerMethod(m);
			FamixClass c=getClassByID(lv.getDeclaredID());
			lv.setDeclaredType(c);
		}
	}

	private void setAccess(){
		Iterator<Access> it=Accesses.iterator();
		while(it.hasNext()){
			Access a=it.next();
			Parameter p=getParameterByID(a.getVariableID());
			if(p!=null) p.setAccess();
		}
	}

	private Parameter getParameterByID(long ID){
		Iterator<Parameter> it=Parameters.iterator();
		while(it.hasNext()){
			Parameter p=it.next();
			if(p.getID()==ID) return p;
		}
		return null;
	}

	public void initialize() {
		setContainingFiles();
		setClassesMethods();
		setClassToAttribute();
		setInheritanceRelations();
		setInvocations();
		setContainerToAttribute();
		Iterator<FamixClass> it=Classes.iterator();
		while(it.hasNext()) {
			FamixClass c=it.next();
			c.setOverrideOrSpecialize();
		}
		it=Classes.iterator();
		while(it.hasNext()) {
			FamixClass c=it.next();
			c.set();
		}
		Iterator<ContainingFile> itf=Files.iterator();
		while(itf.hasNext()){
			ContainingFile f=itf.next();
			f.setEverything();
		}
		
	}

	public ArrayList<ContainingFile> getFiles(){
		return Files;
	}

	public String getClassMetrics(){
        String st="file,class,AMW,WMC,NOM,NOPA,NOAV\n";
        Iterator<FamixClass> it=Classes.iterator();
        while(it.hasNext()){
        	FamixClass c=it.next();
        	if(c.getContainingFile()!=null){
        		st=st+c.getContainingFile().getFileName()+","+c.getClassName()+","+c.getMetrics()+"\n";
			}
		}
        return st;
    }

    public void checkParameters(){

	}

	public String toString() {
		String st="source,target,extCalls,extData,hierarchy\n";
		Iterator<ContainingFile> it=Files.iterator();
		while(it.hasNext()) {
			ContainingFile f=it.next();
			st=st+f;
		}
		return st;
	}
}

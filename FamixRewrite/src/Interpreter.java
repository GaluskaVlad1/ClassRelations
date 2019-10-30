import java.util.*;
class Interpreter {
	private ArrayList<FamixClass> Classes=new ArrayList<FamixClass> ();
	private ArrayList<Method> Methods=new ArrayList<Method> ();
	private ArrayList<Attribute> Attributes=new ArrayList<Attribute> ();
	private ArrayList<Invocation> Invocations=new ArrayList<Invocation> ();
	private ArrayList<Inheritance> Inherits=new ArrayList<Inheritance> ();
	private ArrayList<ContainingFile> Files=new ArrayList<ContainingFile> ();
	private Reader r;
	public void setReader(Reader r) {
		this.r=r;
	}
	
	public void checkForType(String st) throws Exception{
		if(st==null) return;
		if(st.contains("(FAMIX.Class")||st.contains("FAMIX.PrimitiveType")||st.contains("FAMIX.Type")) {
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
		if(st.contains("(FAMIX.Attribute")) {
			interpretAttribute(st);
			return;
		}
	}
	
	private void interpretMethod(String st) throws Exception{
		Long MethodID=getID(st.toCharArray());
		st=r.getNextLine();
		Long parentType=0L;
		String signature="";
		String modifiers="";
		String kind="";
		int cyclomaticComplexity=0;
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
		if(st.contains("FAMIX.")) checkForType(st);
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
		Attribute a=new Attribute(AttributeID);
		st=r.getNextLine();
		long type=0;
		String modifiers="";
		while(st!=null && !st.contains("FAMIX.")) {
			if(st.contains("declaredType")) type=getID(st.toCharArray());
			if(st.contains("modifiers")) modifiers=getModifiers(st.toCharArray());
			st=r.getNextLine();
		}
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
		if(st!=null && st.contains("FAMIX")) checkForType(st);
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
	}

	public void initialize() {
		setContainingFiles();
		setClassesMethods();
		setClassToAttribute();
		setInheritanceRelations();
		setInvocations();
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
        String st="file,class,AMW,WMC\n";
        Iterator<FamixClass> it=Classes.iterator();
        while(it.hasNext()){
        	FamixClass c=it.next();
        	if(c.getContainingFile()!=null){
        		st=st+c.getContainingFile().getFileName()+","+c.getName()+","+c.getMetrics()+"\n";
			}
		}
        return st;
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

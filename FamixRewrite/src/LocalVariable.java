class LocalVariable {
    private long ID;
    private long containerID;
    private long declaredID;
    private Method containerMethod;
    private FamixClass declaredType;
    public LocalVariable(long ID,long containerID,long declaredID){
        this.ID=ID;
        this.containerID=containerID;
        this.declaredID=declaredID;
    }
    public void setContainerMethod(Method m){
        this.containerMethod=m;
    }
    public long getID(){
        return ID;
    }
    public long getContainerID(){
        return containerID;
    }
    public String toString(){
        String st="";
        if(declaredType==null) st="this one";
        return ID+","+containerMethod.getSignature()+" "+st;
    }
    public long getDeclaredID(){
        return declaredID;
    }
    public void setDeclaredType(FamixClass c){
        declaredType=c;
    }
    public FamixClass getDeclaredType(){
        return declaredType;
    }
}

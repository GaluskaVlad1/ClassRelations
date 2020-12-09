class Parameter {
    private long ID;
    private long declaredID;
    private long containerMethodID;
    private FamixClass declaredType;
    private Method containerMethod;
    private boolean access=false;
    public Parameter(long ID,long declaredID,long containerMethodID){
        this.ID=ID;
        this.declaredID=declaredID;
        this.containerMethodID=containerMethodID;
    }
    public FamixClass getDeclaredType(){
        return declaredType;
    }
    public void setAccess(){
        access=true;
    }
    public boolean isAccessed(){
        return access;
    }
    public void setDeclaredType(FamixClass c){
        declaredType=c;
    }
    public void setContainerMethod(Method m){
        containerMethod=m;
    }
    public long getDeclaredID(){
        return declaredID;
    }
    public long getContainerMethodID(){
        return containerMethodID;
    }
    public long getID(){
        return ID;
    }
    public String toString(){
        return ID+","+declaredType.getName()+","+containerMethod.getSignature()+","+access;
    }
}

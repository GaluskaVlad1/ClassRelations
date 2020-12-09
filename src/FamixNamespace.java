class FamixNamespace {
    public long ID;
    public String name;
    public long parentID;
    public boolean isStub;
    public FamixNamespace namespace;
    public String toString(){
        if(namespace!=null) return namespace.toString()+"."+name;
        return name;
    }
}

class Access {
    private long variableID;
    private long accessorID;
    public Access(long variableID,long accessorID){
        this.variableID=variableID;
        this.accessorID=accessorID;
    }
    public long getVariableID(){
        return variableID;
    }
    public long getAccessorID(){
        return accessorID;
    }
}

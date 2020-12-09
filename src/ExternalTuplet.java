class ExternalTuplet {
    int calls;
    int accesses;
    int declarations;
    int returns;
    public String toString(){
        String st="\n\t\t\"calls\": "+calls+",";
        st=st+"\n\t\t\"insideAccesses\": "+accesses+",";
        st=st+"\n\t\t\"declarations\": "+declarations+",";
        st=st+"\n\t\t\"returns\": "+returns+"";
        return st;
    }
}

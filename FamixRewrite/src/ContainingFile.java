import java.util.*;
class ContainingFile {
	private String FileName;
	private ArrayList<Long> ContainedIDs=new ArrayList<Long> (); 
	public ContainingFile(String FileName) {
		this.FileName=FileName;
	}
	public void addID(long ID) {
		ContainedIDs.add(ID);
	}
	public String getFileName() {
		return FileName;
	}
	public String toString() {
		return FileName;
	}
	public ArrayList<Long> getContainedIDs(){
		return ContainedIDs;
	}
}

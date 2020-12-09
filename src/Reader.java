import java.io.*;
class Reader {
	private BufferedReader BR;
	private Interpreter interp;
	
	public Reader(BufferedReader BR,Interpreter interp) {
		this.BR=BR;
		this.interp=interp;
	}
	
	public void getToNextLine() throws Exception{
		System.out.println("Begining to parse the file...");
		String st=BR.readLine();
		while(st!=null) {
			if (st == null) return;
			interp.checkForType(st);
			st=BR.readLine();
		}
	}
	
	public String getNextLine() throws Exception{
		String st=BR.readLine();
		return st;
	}
}

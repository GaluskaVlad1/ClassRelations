import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main{

	static String outputFileName(String args) {
		String st="";
		st=args.replace(".mse", "-relations.csv");
		st=st.replace(".MSE", "-relations.csv");
		return st;
	}
	public static void main(String[] args) throws Exception{
		System.out.println();
		File FileName=new File(args[0]);
	
		BufferedReader BR=new BufferedReader(new FileReader(FileName));
		
		Interpreter i=new Interpreter();
		
		Reader r=new Reader(BR,i);
		i.setReader(r);
		
		r.getToNextLine();
		
		i.initialize();
		
		String st=i.toString();
		
		FileWriter fileWriter=new FileWriter(outputFileName(outputFileName(args[0])));
		
		PrintWriter printWriter=new PrintWriter(fileWriter);
		
		printWriter.print(st);
		
		printWriter.close();
	}

}

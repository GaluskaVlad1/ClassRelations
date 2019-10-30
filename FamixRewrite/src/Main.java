import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class Main{
	static void replaceSlashes(ArrayList<ContainingFile> Files){
		Iterator<ContainingFile> it= Files.iterator();
		while(it.hasNext()){
			ContainingFile f=it.next();
			String st=f.getFileName();
			while(st.contains("\\")){
				st=st.replace("\\","/");
			}
			f.setFileName(st);
		}
	}

	static String getCommonFromAll(ArrayList<ContainingFile> files){
		String common="";
		Iterator<ContainingFile> it=files.iterator();
		ContainingFile firstFile=null;
		if(it.hasNext()) {
			firstFile=it.next();
			common=firstFile.getFileName();
		}
		while(it.hasNext()){
			ContainingFile f=it.next();
			if(f.getFileName()!=null) common=getCommonFromTwoStrings(common,f.getFileName());
		}
		return common;
	}

	static String getCommonFromTwoStrings(String a,String b){
		String st="";
		for(int i=0;i<a.length();i++){
			if(i>b.length()) return st;
			if(a.charAt(i)==b.charAt(i)) st=st+a.charAt(i);
			else return st;
		}
		return st;
	}

	static void replaceSame(ArrayList<ContainingFile> files){
		String st=getCommonFromAll(files);
		Iterator<ContainingFile> it=files.iterator();
		while(it.hasNext()){
			ContainingFile f=it.next();
			String name=f.getFileName();
			name=name.replace(st,"");
			f.setFileName(name);
		}
	}

	static String outputRelationsName(String args) {
		String st="";
		st=args.replace(".mse", "-relations.csv");
		st=st.replace(".MSE", "-relations.csv");
		return st;
	}

    static String outputMetricsName(String fileName){
	    String st="";
	    st=fileName.replace(".mse","-metrics.csv");
	    st=st.replace(".mse","-metrics.csv");
	    return st;
    }

    static void beginMetricsComputation(Interpreter i,String fileName) throws Exception{
        FileWriter ClassMetricsWriter=new FileWriter(outputMetricsName(fileName));
        PrintWriter ClassMetricsPrinter=new PrintWriter(ClassMetricsWriter);
        ClassMetricsPrinter.print(i.getClassMetrics());
        ClassMetricsPrinter.close();
    }

	public static void main(String[] args) throws Exception{
		System.out.println();
		File FileName=new File(args[0]);
	
		BufferedReader BR=new BufferedReader(new FileReader(FileName));
		
		Interpreter i=new Interpreter();
		
		Reader r=new Reader(BR,i);

		i.setReader(r);

		r.getToNextLine();

		replaceSlashes(i.getFiles());

		replaceSame(i.getFiles());

		i.initialize();

		String st=i.toString();
		
		FileWriter classRelationsWriter=new FileWriter(outputRelationsName(args[0]));
		
		PrintWriter classRelationsPrinter=new PrintWriter(classRelationsWriter);
		
		classRelationsPrinter.print(st);
		
		classRelationsPrinter.close();

		beginMetricsComputation(i,args[0]);
	}

}

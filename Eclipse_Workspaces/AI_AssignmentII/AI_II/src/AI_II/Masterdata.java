package AI_II;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Masterdata{
	int pregnant;	int glucose;
	int pressure;	int triceps;
	int insulin;	int bmi;
	int pedigree;	int age;
	int diabetes;
	double totEntrop[] = new double[9];
	Masterdata(int p, int g, int pr, int t, int i, int b,int pe,int a,int d){
		 pregnant=p;glucose=g;pressure=pr;triceps=t;insulin=i;bmi=b;
		pedigree=pe;age=a;diabetes=d;
	}
	
	public static void main(String []args){
		String file = "data/diabetes.csv";
		ArrayList<Masterdata> md = new ArrayList<Masterdata>();
		BufferedReader br = null;
		String CSVSplitBy = ",";
		String line="";
		try{
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine())!=null){
				String [] rec = line.split(CSVSplitBy);
				Masterdata records = new Masterdata(
						Integer.parseInt(rec[0]),Integer.parseInt(rec[1]),Integer.parseInt(rec[2]),
						Integer.parseInt(rec[3]),Integer.parseInt(rec[4]),Integer.parseInt(rec[5]),
						Integer.parseInt(rec[6]),Integer.parseInt(rec[7]),Integer.parseInt(rec[8]));
				md.add(records);
			}
		}catch (IOException e){
			e.printStackTrace();
		}
		
		double f = entropy(md,0);
		/*System.out.println("displaying data:");
		System.out.println("  pregnant | glucose | pressure | triceps | insulin | bmi | pedigree | age| diabetes");
		for (int i=0;i<md.size();i++)
			System.out.println("\t"+md.get(i).pregnant+"\t"+md.get(i).glucose);
		*/
		
	}
	static public double entropy(ArrayList<Masterdata> md,int col){
		float entVal=0.0f;
		int positives=0;
		int count=0;
		double []probability;
		ArrayList<Integer> sets = new ArrayList<Integer>();
		ArrayList<Integer>p=new ArrayList<Integer>();
		for (int i=0;i<md.size();i++){
			count+=1;
				/*  Getting unique values in a separate arraylist to calculate entropy*/
				if (sets.indexOf(md.get(i).pregnant)==-1)
					sets.add(md.get(i).pregnant);
				/*else if (sets.indexOf(md.get(i).pressure)==-1)
					sets.add(md.get(i).pressure);*/
				else continue;
			}
		probability = new double[sets.size()];
		for (int i=0;i<sets.size();i++){
			positives=0;
			count=0;
			//System.out.println(sets.get(i));
			for (Masterdata m:md){
				if(m.pregnant==sets.get(i))
				count+=1;
				
				if((m.pregnant==sets.get(i))&& m.diabetes==1){
					//System.out.println("preg: "+m.pregnant+" diab:"+m.diabetes);
					positives+=1;
				}
			}
			probability[i]=(double)positives/count;
			//System.out.println("Positives="+positives+"count="+count+"probability:"+probability[i]);
			
		}
		double calEnt[]=new double[sets.size()];
		/* Now calculate for each value */
		for (int i=0;i< sets.size();i++){
			//calEnt=0.0f;
			//System.out.print("Entropy for "+sets.get(i)+":"+"probability:"+probability[i]); 
			calEnt[i]=(probability[i]*(Math.log10(probability[i])/Math.log10(2))+
			((1-probability[i])*(Math.log10((1-probability[i]))/Math.log10(2))));
			//System.out.println(Math.log10(probability[i])/Math.log10(2));
			calEnt[i]=calEnt[i]*(-1);
			//System.out.println("probability[i]*(Math.log10(probability["+i+"])/Math.log10(2)):"+probability[i]*(Math.log10(probability[i])/Math.log10(2)));
			//System.out.println("(1-probability[i])*(Math.log10(1-probability[i])/Math.log10(2))):"+(1-probability[i])*(Math.log10((1-probability[i]))/Math.log10(2)));
		}
		double entrop=0.0;
		/* Print Entropy */
		for(int i=0;i<calEnt.length;i++){
			if (Double.isNaN(calEnt[i]))
				continue;
			entrop += probability[i]*calEnt[i]; 
		}
		return entrop;
	}
}

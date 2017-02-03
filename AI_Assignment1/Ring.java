import java.util.*;
class Ring{
	private int items[];// possible values 1 for red, 2 for green and blanks for star

	void initialize(int sizeOfElem){
		items = new int[sizeOfElem];
		int rCount=(sizeOfElem-2)/2;  // number of red tiles
		int gCount=rCount;				//number of blue tiles
		Random r = new Random();//random number generator.
		int i=0;
		int tiles[] = {sizeOfElem-(2*rCount),rCount,gCount};
		while (i< sizeOfElem){
				//iterate each element of the array and generate either 0,1,2 and then decrement the count of the respective
				int rIndex =r.nextInt(3);
				//System.out.println(rIndex);
				
				if (tiles[rIndex]>0){
					items[i]=rIndex;
					tiles[rIndex]--;
				}
				else
					continue;
			
			i++;
		}
	}
	void displayConfig(){
		for (int i=0;i<items.length;i++)
			System.out.print(items[i]+" ");
	}

	public static void main(String []args){
		Ring r = new Ring();
		r.initialize(100);
		r.displayConfig();
	}
}
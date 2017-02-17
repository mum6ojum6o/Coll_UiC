package aima.core.search.framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
class ASNode{
	private String state;
	private ASNode parent;
	private int pathCost;
	private int fn;

	public String getState() {
		return this.state;
	}

	public int getPathCost() {
		return this.pathCost;
	}

	public void setParent(ASNode n) {
		this.parent = n;
	}

	public void setState(String stte) {
		this.state = stte;
	}

	public void setPathCost(int cost) {
		this.pathCost = cost;
	}

	public ASNode getParent() {
		return this.parent;
	}
	public void setfn(int heuristicVal){
		fn=this.getPathCost()+heuristicVal;
	}
	public int getfn(){
		return this.fn;
	}
}

public class RingAStar {
	static int TotalPathCost=0;
	static int TotalNodesGenerated=0;
	public int items[];
	public ArrayList<ASNode> exploredSet; // Arraylist to hold the explored
	public ArrayList<ASNode> frontierAStar;										// nodes.
	public ArrayList<ASNode> frontier; // Arraylist to hold the frontier.
	public static int nodeCounter;
	ASNode root;

	RingAStar() {
		frontier = new ArrayList<ASNode>();
		frontierAStar= new ArrayList<ASNode>();
		exploredSet = new ArrayList<ASNode>();
	}

	/***************************************************
	 * Method generates the initial state
	 * 
	 * @param sizeOfElem
	 **************************************************/
	public void init(long seed){
	Random r = new Random();
	r.setSeed(seed);
	int temp=r.nextInt();
	//System.out.println(temp);
	int [] items = new int[8];
	int index=0;
	int blank=2;
		int green=3;
		int red=3;
	while (index<items.length){
		
	//	System.out.println(temp%2);
		if (temp%2==0 && red>0){
			items[index]=1;
			red--;
		}
		else if ((temp%2==1 || temp%2==-1)&& green>0) {
			items[index]=2;
			green--;
		}
	temp=temp/10;
	
	index++;
	}
	System.out.println("initial State:"+Arrays.toString(items) );
	root = new ASNode();
	root.setParent(null);
	root.setPathCost(0);
	root.setState(Arrays.toString(items));
	addToFrontier(root);
	ASNode result=aStar();
	if(result!=null){
		System.out.println("Solution Found found using A Star!!");
		System.out.println("Solution is:");
		
		printSolution(result);
		System.out.println("Solution Cost:"+result.getPathCost());
		System.out.println("Nodes Expanded:"+nodeCounter);
	}
	else
		System.out.println("Solution no found");
	}

	public void initialize(int sizeOfElem) {
		//System.out.println("in initialize");
		items = new int[(sizeOfElem*2)+2];
		System.out.println("in initialize");
		int rCount = sizeOfElem; // number of red tiles
		int gCount = sizeOfElem; // number of blue tiles
		Random r = new Random();// random number generator.
		int i = 0;
		// keeps track of the number of tiles to placed.
		int tiles[] = { 2, rCount, gCount };
		while (i < items.length) {
			//System.out.println("in while");
			// iterate each element of the array and generate either 0,1,2 and
			// then decrement the count of the respective
			int rIndex = r.nextInt(3);
			 //System.out.println(rIndex);

			if (tiles[rIndex] > 0) {
				items[i] = rIndex;
				tiles[rIndex]--;
			} else
				continue;
			i++;
		}
		System.out.println("System initialized");
		System.out.println("Initial State:"+Arrays.toString(items));
		root = new ASNode();
		root.setParent(null);
		root.setPathCost(0);
		root.setState(Arrays.toString(items));
		addToFrontier(root);
		
		ASNode result=aStar();
		if(result!=null){
			System.out.println("Solution Found found using A Star!!");
			System.out.println("Solution is:");
			//System.out.println("Cost:");
			printSolution(result);
			TotalPathCost=TotalPathCost+result.getPathCost();
			//TotalNodeGenerated=TotalNodesExplored+nodeCounter;
			//System.out.println("Nodes generated:"+nodeCounter);
		}
		else
			System.out.println("Solution no found");
	}

	/*****************************
	 * method to add to Frontier
	 * 
	 * @param n
	 ***********************************/
	public void addToFrontier(ASNode n) {
		
		frontier.add(n);
		
	}

	/***************************************************
	 * Function to convert the node state to integer
	 * 
	 * @param n
	 * @return
	 ***************************************************/
	public int[] convert(ASNode n) {
		// System.out.println("In Convert:");
		System.out.println("State:" + n.getState());
		String st = n.getState();
		st = st.replace(",", "");
		st = st.replace("[", "");
		st = st.replace("]", "");
		st = st.replace(" ", "");
		// System.out.println("St:"+st+" st length:"+st.length());
		int[] items = new int[st.length()]; // change the state datatype to
											// String
		// System.out.println("items length:"+items.length);
		for (int i = 0; i < st.length(); i++) {
			items[i] = Integer.parseInt(st.substring(i, i + 1));
		}
		/*
		 * for(int i=0;i<items.length;i++){ System.out.println(i+":"+items[i]);
		 * }
		 */
		return items;
	}

	/*************************
	 * Print the frontier
	 *******************************/
	void printFrontier() {
		for (ASNode temp : frontier) {
			System.out.println(temp.getState());
		}
	}

	boolean isInFrontier(ASNode n) {
		for (ASNode temp : frontier) {
			if (n.getState().equals(temp.getState()))
				return true;
		}
		return false;
	}

	/**********************************************
	 * Function to calculate the heuristic
	 * 
	 * @param n
	 *            is node.
	 * @return the heuristic value
	 ********************************************/

	public int heuristic(ASNode n) {
		/* My heuristic function value is determined on the basis of the number of similar
		 * adjacent tiles.
		 */
		int heuVal=0;
		int []items = convert(n);
		for(int i=0;i<items.length-2;i++){
			if (items[i]==items[i+1] && items[i]!=0)
				heuVal++;
		}
		if (items[0]==items[items.length-1])
				heuVal++;
		return heuVal;
		}
		
	

	/**********************************************
	 * Method to display the initial state.
	 *********************************************/
	void displayConfig() {
		for (int i = 0; i < items.length; i++)
			System.out.print(items[i] + " ");
	}

	/************************************************
	 * Method to check the goal state
	 * 
	 * @param state
	 * @return
	 *******************************************/
	boolean isGoalTest(ASNode popped) {
		int[] items = convert(popped);
		//System.out.println("In goal test");
		if (items[0] == items[items.length-1])
			return false;
		else {
			for (int i = 1; i < items.length-1; i++) {
				if ((items[i - 1] == items[i] || items[i] == items[i + 1]) && items[i] != 0)
					return false;
			}
			return true;
		}
	}

	

	public static void main(String[] args) {
		RingAStar r = new RingAStar();
		for (int i=0;i<2;i++){
		r.initialize(6);
		}
		System.out.println( "Total Path Cost:"+RingAStar.TotalPathCost);
		System.out.println("Total Nodes Explored:"+ RingAStar.nodeCounter);
		//r.init(1);
		//r.init(3);
		//r.init(4);
		//System.out.println("Problem initialized.");
		//r.displayConfig();
		//r.printFrontier();
		// System.out.println("Goal Test of "+ "is "+ r.isGoalTest(testItems));
	}

	/**************************************
	 * Function to generate the nodes
	 * 
	 * @param ASNode
	 *            n
	 **********************************/
	ASNode generateNode(String state, int pathCost, ASNode Parent) {
		ASNode temp = new ASNode();
		temp.setParent(Parent);// check this
		temp.setState(state);
		temp.setPathCost(pathCost + Parent.getPathCost());
		System.out.println("PathCost"+ temp.getPathCost());
		nodeCounter++;
		return temp;
	}

	/**************************************
	 * Funtion to expand the nodes
	 * 
	 * @param ASNode
	 *            n
	 **********************************/
	public void expander(ASNode n) {
		System.out.println("in Expander");
		String s = n.getState();
		s = s.replace(",", "");
		s = s.replace("[", "");
		s = s.replace("]", "");
		s = s.replace(" ", "");
		int zL = s.indexOf('0');
		// System.out.println("blankLoc:"+zL);
		int zLc = s.indexOf('0', zL + 1);
		// System.out.println("blankLocII:"+zLc);
		if (zL == 0 && zLc == zL + 1) {// ok
			System.out.println("1.");
			moveLeft(n, zLc);
			hopLeft(n, zLc);
		} // 00111222
		else if (zL == 1 && zLc == zL + 1) {// ok
			System.out.println("2.");
			moveLeft(n, zLc);
			hopLeft(n, zLc);
			moveRight(n, zL);
		} // 10011222
		else if (zL > 1 && zLc == zL + 1 && zLc < s.length() - 2) {// ok
			System.out.println("3.");
			moveLeft(n, zLc);
			hopLeft(n, zLc);
			moveRight(n, zL);
			hopRight(n, zL);
		} // 11002122 11100222 //11120022
		else if (zL > 1 && zLc == zL + 1 && zLc == s.length() - 2) {
			System.out.println("4.");
			moveLeft(n, zLc);
			moveRight(n, zL);
			hopRight(n, zL);

		} // 11122002
		else if (zLc == s.length() - 1 && zL == zLc - 1) {
			System.out.println("5.");
			moveRight(n, zL);
			hopRight(n, zL);
		} // 11122200
		else if (zL == 0 && zLc == zL + 2) {
			System.out.println("6.");
			moveRight(n, zLc);
			moveLeft(n, zL);
			moveLeft(n, zLc);
			hopLeft(n, zLc);

		} // 01011222
		else if (zL == 1 && zLc == zL + 2) {
			System.out.println("7.");
			moveRight(n, zL);// ok
			moveLeft(n, zL);// ok
			moveLeft(n, zLc);// ok
			moveRight(n, zLc);// ok
			hopLeft(n, zLc);
		} // 10101222
		else if (zL > 1 && zLc == zL + 2 && zLc < s.length() - 2) {
			System.out.println("8.");
			moveRight(n, zL);// ok
			moveLeft(n, zL);// ok
			moveLeft(n, zLc);// ok
			hopRight(n, zL);// ok
			hopLeft(n, zLc);// ok
		} // 11102022
		else if (zL > 2 && zLc == zL + 2 && zLc < s.length() - 2) {
			System.out.println("9.");
			moveRight(n, zL);
			hopRight(n, zL);
			moveLeft(n, zL);
			moveRight(n, zLc);
			hopRight(n, zLc);
		} // 1111020222
		else if (zL > 2 && zLc == zL + 2 && zLc == s.length() - 2) {
			System.out.println("10.");
			moveRight(n, zL);
			hopRight(n, zL);
			moveLeft(n, zL);
			moveRight(n, zLc);
			// hopRight(n,zLc);
		} // 11120202
		else if (zL > 2 && zLc == zL + 2 && zLc == s.length() - 1) {
			System.out.println("10.");
			moveRight(n, zL);
			hopRight(n, zL);
			moveLeft(n, zL);
			moveRight(n, zLc);
		} // 11222010
		else if (zL == 0 && zLc == zL + 3) {
			System.out.println("11.");
			moveLeft(n, zL);
			hopLeft(n, zL);
			moveLeft(n, zLc);
			hopLeft(n, zLc);
			moveRight(n, zLc);
			hopRight(n, zLc);
		} // 01102122
		else if (zL == 1 && zLc == zL + 3) {
			System.out.println("12.");
			moveRight(n, zL);
			// hopRight(n,zL);
			moveLeft(n, zL);
			hopLeft(n, zL);
			moveRight(n, zLc);
			hopRight(n, zLc);
			moveLeft(n, zLc);
			hopLeft(n, zLc);

		} // 10110222
		else if (zL > 1 && zLc == zL + 3 && zLc < s.length() - 2) {
			System.out.println("13.");
			moveLeft(n, zL);
			hopLeft(n, zL);
			moveRight(n, zL);
			hopRight(n, zL);
			moveRight(n, zLc);
			hopRight(n, zLc);
			moveLeft(n, zLc);
			hopLeft(n, zLc);
		} // 11012022
		else if (zL > 1 && zLc == zL + 3 && zLc == s.length() - 2) {
			System.out.println("15.");
			moveLeft(n, zL);
			hopLeft(n, zL);
			moveRight(n, zL);
			hopRight(n, zL);
			moveRight(n, zLc);
			hopRight(n, zLc);
			moveLeft(n, zLc);
			// hopLeft(n,zLc);
		} // 11102202
		else if (zL > 1 && zLc == zL + 3 && zLc == s.length() - 1) {
			System.out.println("16.");
			moveLeft(n, zL);
			hopLeft(n, zL);
			moveRight(n, zL);
			hopRight(n, zL);
			moveRight(n, zLc);
			hopRight(n, zLc);
		} // 11120220
		else if (zL == 0 && zLc == zL + 4) {
			System.out.println("17.");
			moveRight(n, zLc);
			hopRight(n, zLc);
			moveLeft(n, zLc);
			hopLeft(n, zLc);
			moveLeft(n, zL);
			hopLeft(n, zL);
		} // 01110222
		else if (zL == 1 && zLc == zL + 4) {
			System.out.println("18.");
			moveRight(n, zLc);
			hopRight(n, zLc);
			moveLeft(n, zLc);
			hopLeft(n, zLc);
			moveLeft(n, zL);
			hopLeft(n, zL);
			moveRight(n, zL);
		} // 10112022
		else if (zL > 1 && zLc == zL + 4 && zLc < s.length() - 2) {
			System.out.println("19.");
			moveRight(n, zLc);
			hopRight(n, zLc);
			moveLeft(n, zLc);
			hopLeft(n, zLc);
			moveLeft(n, zL);
			hopLeft(n, zL);
			moveRight(n, zL);
			hopRight(n, zL);

		} // 1110212022
		else if (zL > 1 && zLc == zL + 4 && zLc == s.length() - 2) {
			System.out.println("20.");
			moveRight(n, zLc);
			hopRight(n, zLc);
			moveLeft(n, zLc);
			// hopLeft(n,zLc);
			moveLeft(n, zL);
			hopLeft(n, zL);
			moveRight(n, zL);
			hopRight(n, zL);
		} // 11012202
		else if (zL > 1 && zLc == zL + 4 && zLc == s.length() - 1) {
			System.out.println("21.");
			moveRight(n, zLc);
			hopRight(n, zLc);
			moveLeft(n, zL);
			hopLeft(n, zL);
			moveRight(n, zL);
			hopRight(n, zL);
		} // 11102220
		else if (zL == 0 && zLc == zL + 5) {
			System.out.println("22.");
			hopRight(n, zLc);
			moveLeft(n, zL);
			hopLeft(n, zL);
			moveRight(n, zLc);
			hopRight(n, zLc);

		} // 01112022
		else if (zL > 0 && zLc == zL + 5 && zLc < s.length() - 2) {
			System.out.println("23.");
			moveRight(n, zLc);
			hopRight(n, zLc);
			moveLeft(n, zLc);
			moveLeft(n, zL);
			hopLeft(n, zL);
			moveRight(n, zL);
		} // 10112202
		else if (zL > 1 && zLc == zL + 5 && zLc == s.length() - 2) {
			System.out.println("24.");
			moveRight(n, zLc);
			hopRight(n, zLc);
			moveLeft(n, zLc);
			moveLeft(n, zL);
			hopLeft(n, zL);
			moveRight(n, zL);
			moveRight(n, zL);
			hopRight(n, zL);
		} // 1101122022 1101122022 // 1101122022 1101122022
		else if (zL==0 && zLc==zL+6 && zLc<s.length()-2){
				System.out.println("25.");
				moveLeft(n, zL);
				moveLeft(n, zLc);;
				hopLeft(n, zL);
				hopRight(n, zLc);
				moveRight(n, zLc);
				
			}
			else if (zL==0 && zLc==zL+6 && zLc==s.length()-2){
				System.out.println("26.");
				moveLeft(n, zL);
				moveLeft(n, zLc);;
				hopLeft(n, zL);
				hopRight(n, zLc);
				moveRight(n, zLc);
			}
			else if (zL==1 && zLc==zL+6 && zLc==s.length()-2){
				System.out.println("27.");
				moveLeft(n, zL);
				moveLeft(n, zLc);;
				hopLeft(n, zL);
				hopRight(n, zLc);
				moveRight(n, zLc);
			}
			else if(zL==1 && zLc==s.length()-2 && (zLc-zL)>=3){
				System.out.println("28.");
				moveRight(n, zL);
			//hopRight(n,zL);
			moveLeft(n, zL);
			hopLeft(n, zL);
			moveRight(n, zLc);
			moveLeft(n, zLc);;
			hopRight(n, zLc);
			}
			else if(zL>1 && zLc==s.length()-2 && (zLc-zL)>=3){
				System.out.println("29.");
				moveRight(n, zL);
			hopRight(n,zL);
			moveLeft(n, zL);
			hopLeft(n, zL);
			moveRight(n, zLc);
			moveLeft(n, zLc);;
			hopRight(n, zLc);
			}
			else if(zL==0 && zLc==s.length()-1 && (zLc-zL)>=3){
				System.out.println("30.");
				moveRight(n, zLc);
				hopRight(n,zLc);
				moveLeft(n, zL);;
				hopLeft(n, zL);
			}
			else if(zL==0 && zLc==s.length()-1){
				System.out.println("31.");
				moveLeft(n,zL);
				hopLeft(n,zL);
				moveRight(n,zLc);
				hopRight(n,zLc);
			}
	}

	// moves digit to the left
	public void moveLeft(ASNode n, int blankLoc){
		//System.out.println("in Move Left");
		int items[] = convert(n);
		int temp;
		temp = items[blankLoc];
		items[blankLoc] = items[blankLoc + 1];
		items[blankLoc + 1] = temp;
		
		ASNode successor = generateNode(Arrays.toString(items), 1, n);
		successor.setfn(successor.getPathCost()+heuristic(successor));//generated heuristic
		if (!isInExploredSet(successor)||!isInFrontier(successor)){
			addToFrontier(successor);
			System.out.println(successor.getState()+" Added to Frontier");
		}
		else if(frontier.indexOf(successor.getState())!=-1 && successor.getPathCost() < frontier.get(frontier.indexOf(successor.getState())).getPathCost()){
			System.out.println(successor.getState()+" updated in frontier");
			exploredSet.set(frontier.indexOf(successor.getState()),successor);
		}
	}

	// hop digit to left
	public void hopLeft(ASNode n, int blankLoc) {
		int items[] = convert(n);
		int temp;
		temp = items[blankLoc];
		items[blankLoc] = items[blankLoc + 2];
		items[blankLoc + 2] = temp;
		ASNode successor = generateNode(Arrays.toString(items), 2, n);
		successor.setfn(successor.getPathCost()+heuristic(successor));//generated heuristic
		/*
		 * n.setParent(successor); successor.setPathCost(n.getPathCost()+2.0);
		 * System.out.println("Shifted:"+Arrays.toString(items));
		 * successor.setState(Arrays.toString(items));
		 */
		if (!isInExploredSet(successor)||!isInFrontier(successor)){
			addToFrontier(successor);
			System.out.println(successor.getState()+" Added to Frontier");
		}
		else if(frontier.indexOf(successor.getState())!=-1 && successor.getPathCost() < frontier.get(frontier.indexOf(successor.getState())).getPathCost()){
			exploredSet.set(frontier.indexOf(successor.getState()),successor);
		System.out.println(successor.getState()+" Not Added to Frontier");
		}
	}

	// hop digit to right
	public void hopRight(ASNode n, int blankLoc) {
		int items[] = convert(n);
		int temp;
		temp = items[blankLoc];
		items[blankLoc] = items[blankLoc - 2];
		items[blankLoc - 2] = temp;
		ASNode successor = generateNode(Arrays.toString(items), 2, n);
		successor.setfn(successor.getPathCost()+heuristic(successor));//generated heuristic
		if (!isInExploredSet(successor)||!isInFrontier(successor)){
			addToFrontier(successor);
			System.out.println(successor.getState()+" Added to Frontier");
		}
		else if(frontier.indexOf(successor.getState())!=-1 && successor.getPathCost() < frontier.get(frontier.indexOf(successor.getState())).getPathCost()){
			exploredSet.set(frontier.indexOf(successor.getState()),successor);
		System.out.println(successor.getState()+" Not Added to Frontier");
		}
	}

	// move digit to right
	public void moveRight(ASNode n, int blankLoc) {
		int items[] = convert(n);
		int temp;
		temp = items[blankLoc];
		items[blankLoc] = items[blankLoc - 1];
		items[blankLoc - 1] = temp;
		ASNode successor = generateNode(Arrays.toString(items), 1, n);
		successor.setfn(successor.getPathCost()+heuristic(successor));//generated heuristic
		if (!isInExploredSet(successor)||!isInFrontier(successor)){
			addToFrontier(successor);
			System.out.println(successor.getState()+" Added to Frontier");
		}
		else if(frontier.indexOf(successor.getState())!=-1 && successor.getPathCost() < frontier.get(frontier.indexOf(successor.getState())).getPathCost()){
			exploredSet.set(frontier.indexOf(successor.getState()),successor);
		System.out.println(successor.getState()+" Not Added to Frontier");
		}
	}

	public ASNode popFrontier() {
		if (frontier.size() == 1){
			System.out.println("Only one element in the frontier");
			System.out.println(frontier.get(0));
			return frontier.get(0);
		}
		int tempVal = frontier.get(1).getfn();
		System.out.println("tempVal:"+tempVal);
		int smallestIndex = 1;

		for (int i = 2; i < frontier.size(); i++) {
			if (tempVal > frontier.get(i).getPathCost()) {
				tempVal = frontier.get(i).getPathCost();
				smallestIndex = i;
			}

		}
		ASNode ret = frontier.get(smallestIndex);
		frontier.remove(smallestIndex);
		return (ret);
	}

	void printSolution(ASNode n) {
		
		if (n.getParent() != null){
			System.out.println(n.getState());
			printSolution(n.getParent());
		}
		else
			System.out.println("---------------------------------------");
	}

	public ASNode aStar(){
		System.out.println("Starting A Star Search");
		while (!frontier.isEmpty()) {
			ASNode popped = popFrontier();
			if (isGoalTest(popped)) {
				//printSolution(popped);
				return popped;
			}
			exploredSet.add(popped);
			expander(popped);
			
			
		}
		return null;
	}
	public boolean isInExploredSet(ASNode n){
		ASNode temp;
		for (int i=0;i<exploredSet.size();i++) {
			if (n.getState().equals(exploredSet.get(i).getState()))
				return true;
		}
		return false;
}
}

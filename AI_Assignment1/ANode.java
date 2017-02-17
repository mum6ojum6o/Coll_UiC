package aima.core.search.framework;

class ANode {
	private String state;
	private ANode parent;
	private double pathCost;

	public String getState() {
		return this.state;
	}

	public double getPathCost() {
		return this.pathCost;
	}

	public void setParent(ANode n) {
		this.parent = n;
	}

	public void setState(String stte) {
		this.state = stte;
	}

	public void setPathCost(double cost) {
		this.pathCost = cost;
	}

	public ANode getParent() {
		return this.parent;
	}
}

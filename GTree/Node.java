package GTree;

import java.io.Serializable;
import java.util.Vector;

public class Node implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7143809112141024174L;
	public int id;
	public double x;
	public double y;
	public Vector<Integer> adjnodes;
	public Vector<Integer> adjweight;
	public boolean isborder;
	public Vector<Integer> gtreepath; // this is used to do sub-graph locating
	
	
	
	public Node() {
		// TODO Auto-generated constructor stub
		init();
	}
	public Node(int nodeID) {
		// TODO Auto-generated constructor stub
		init();
		this.id=nodeID;
	}
	public void init()
	{
		this.id=-1;
		this.x=0;
		this.y=0;
		this.isborder=false;
		this.adjnodes=new Vector<Integer>();
		this.adjweight=new Vector<Integer>();
		this.gtreepath=new Vector<Integer>();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public Vector<Integer> getAdjnodes() {
		return adjnodes;
	}
	public void setAdjnodes(Vector<Integer> adjnodes) {
		this.adjnodes = adjnodes;
	}
	public Vector<Integer> getAdjweight() {
		return adjweight;
	}
	public void setAdjweight(Vector<Integer> adjweight) {
		this.adjweight = adjweight;
	}
	public boolean isIsborder() {
		return isborder;
	}
	public void setIsborder(boolean isborder) {
		this.isborder = isborder;
	}
	public Vector<Integer> getGtreepath() {
		return gtreepath;
	}
	public void setGtreepath(Vector<Integer> gtreepath) {
		this.gtreepath = gtreepath;
	}

}

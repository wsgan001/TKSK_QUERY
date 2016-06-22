package GTree;

import java.io.Serializable;

public class Status_query implements Comparable<Status_query>,Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6207192090914618643L;
	public int id;
	public boolean isvertex;
	public int lca_pos;
	public int dis;
	public Status_query(int id,boolean isvertex,int lca_pos,int dis) {
		// TODO Auto-generated constructor stub
		this.id=id;
		this.isvertex=isvertex;
		this.lca_pos=lca_pos;
		this.dis=dis;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public int compareTo(Status_query o) {
		// TODO Auto-generated method stub
		if(this.dis>o.dis)
		{
			return 1;
		}else if(this.dis<o.dis)
		{
			return -1;
		}
		else
		{
			return 0;
		}
		
		
	}
	
	
	
	

}

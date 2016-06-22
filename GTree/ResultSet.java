package GTree;

import java.io.Serializable;

public class ResultSet implements Comparable<ResultSet>,Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7799973444697153311L;
	public int id;
	public int dis;
	public ResultSet(int id,int dis) {
		// TODO Auto-generated constructor stub
		this.id=id;
		this.dis=dis;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public int compareTo(ResultSet o) {
		// TODO Auto-generated method stub
		if(this.dis>o.dis)
		{
			return 1;
		}else
		{
			return 0;
		}
		
	}

}

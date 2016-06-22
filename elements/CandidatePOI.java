package elements;

import java.io.Serializable;

public class CandidatePOI  implements Comparable<CandidatePOI>,Serializable{
	public int id;
	public int distance;

	public CandidatePOI(int id,int dist) {
		// TODO Auto-generated constructor stub
		this.id=id;
		this.distance=dist;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	@Override
	public int compareTo(CandidatePOI o) {
		// TODO Auto-generated method stub
		return this.distance-o.distance;
	}

}

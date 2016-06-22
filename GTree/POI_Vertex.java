package GTree;

import java.io.Serializable;
import java.util.HashMap;

public class POI_Vertex implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8283057254722716887L;
	public int id;
	public int vertexID;
	public HashMap<String, Float> kwdScoresMap;

	public POI_Vertex(int id,int vid,HashMap<String, Float> ksm) {
		// TODO Auto-generated constructor stub
		this.id=id;
		this.vertexID=vid;
		this.kwdScoresMap=ksm;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public HashMap<String, Float> getKwdScoresMap() {
		return kwdScoresMap;
	}

	public void setKwdScoresMap(HashMap<String, Float> kwdScoresMap) {
		this.kwdScoresMap = kwdScoresMap;
	}

}

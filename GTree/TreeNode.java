package GTree;

import java.io.Serializable;
import java.util.Vector;

public class TreeNode implements Serializable{
	public int id;
	public Vector<Integer> borders;
	public Vector<Integer> children;
	public boolean isleaf;
	public Vector<Integer> leafnodes;
	public int father;
// ----- min dis -----
	public Vector<Integer> union_borders; // for non leaf node	
	public Vector<Integer> mind; // min dis, row by row of union_borders
// ----- for pre query init, OCCURENCE LIST in paper -----
	public Vector<Integer> nonleafinvlist;
	public Vector<Integer> leafinvlist;
	public Vector<Integer> up_pos;
	public Vector<Integer> current_pos;
	
	public TreeNode() {
		// TODO Auto-generated constructor stub
		init();
		
	}
	public TreeNode(int id) {
		// TODO Auto-generated constructor stub
		init();
		this.id=id;
		
	}
	
	public void init()
	{
		this.id=-1;
		this.borders=new Vector<Integer>();
		this.children=new Vector<Integer>();
		this.leafnodes=new Vector<Integer>();
		this.union_borders=new Vector<Integer>();
		this.mind=new Vector<Integer>();
		this.nonleafinvlist=new Vector<Integer>();
		this.leafinvlist=new Vector<Integer>();
		this.up_pos=new Vector<Integer>();
		this.current_pos=new Vector<Integer>();
		this.isleaf=false;
		this.father=-1;
		
	}
	

	/**
	 * find the node Index if not before value index;
	 * 函数lower_bound()在first和last中的前闭后开区间进行二分查找，返回大于或等于val的第一个元素位置。如果所有元素都小于val，则返回last的位置
	 * 
	 * @param nodeBegin
	 * @param nodeEnd
	 * @param nodeVal
	 */
	public int lower_bound(int nodeVal)
	{
		if(this.leafnodes.size()>0)
		{
			if(this.leafnodes.firstElement()>=nodeVal)
			{
				return 0;
				
			}
			if(this.leafnodes.lastElement()<nodeVal)
			{
				return this.leafnodes.size()-1;
			}
			
			if(this.leafnodes.size()==2)
			{
				if(nodeVal<this.leafnodes.lastElement())
				{
					return 1;
				}
				else
				{
					return 0;
				}
			}
			
			
			
			int beginIndex=0;
			int endIndex=this.leafnodes.size()-1;
			int midIndex=-1;
			int mid=-1;
			while(beginIndex<endIndex)
			{
				midIndex=(int) Math.ceil((endIndex+beginIndex)*1f/2);
				mid=this.leafnodes.get(midIndex);
				if(nodeVal==mid)
				{
					if(nodeVal>this.leafnodes.get(midIndex-1))
					{
						return midIndex;
					}
					else
					{
						endIndex=midIndex;
						
					}
				}else if(nodeVal>mid)
				{
					if(nodeVal<this.leafnodes.get(midIndex+1))
					{
						return midIndex+1;
					}else
					{
						beginIndex=midIndex;
					}
				}else
				{
					if(nodeVal>this.leafnodes.get(midIndex-1))
					{
						return midIndex;
					}else
					{
						endIndex=midIndex;
					}
				}
					
			}
			
			
			return 0;
		}else
		{
			return 0;
		}		
	}
	
	public void addLeafNode(int nodeId)
	{
		this.leafnodes.add(nodeId);
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		TreeNode test=new TreeNode();
		test.addLeafNode(1);
		test.addLeafNode(1);
		test.addLeafNode(2);
		test.addLeafNode(3);
		int pos=test.lower_bound(2);
		System.out.println(pos);
		
//		test.addLeafNode(4);
//		test.addLeafNode(5);
//		int
	}

}

package interfaces;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

import GTree.Node;
import GTree.TreeNode;
import Utils.IGTreeConfigure;


public class LoadBasicGTree {
//	DB db;
//	String DBNAME;
	public Vector<Node> Nodes;
	public Vector<TreeNode> Gtree;
//	public HTreeMap<String, Vector<Node>> db_NodesMap;
//	public HTreeMap<String, Vector<TreeNode>> db_GTreeMap;

	public LoadBasicGTree() {
		// TODO Auto-generated constructor stub
		init();
	}
	
	public void init()
	{
		Nodes=new Vector<Node>();
		Gtree=new Vector<TreeNode>();
//		DBNAME=IGTreeConfigure.DATA_ROOT_FOLDER+IGTreeConfigure.DATA_INDEX_FOLDER+IGTreeConfigure.DATA_INDEX_NAME;
//		createDB();
//		db_NodesMap=db.getHashMap("db_NodesMap");
//		db_GTreeMap=db.getHashMap("db_GTreeMap");
	}

	
	public  void loadBasicGTreeAndNodes() throws IOException
	{
		BufferedReader readerGTree=new BufferedReader(new FileReader(IGTreeConfigure.DATA_ROOT_FOLDER+IGTreeConfigure.DATA_BUILDING_FOLDER+IGTreeConfigure.DATA_GTREE_FILE_NAME));
		BufferedReader readerNodes=new BufferedReader(new FileReader(IGTreeConfigure.DATA_ROOT_FOLDER+IGTreeConfigure.DATA_BUILDING_FOLDER+IGTreeConfigure.DATA_NODES_FILE_NAME));
		String lineGTree;
		String lineNodes;
		while((lineGTree=readerGTree.readLine())!=null)
		{
			String datas[]=lineGTree.split(" ");
			int GtreeNodeID=Integer.parseInt(datas[0]);
			if(Gtree.size()<=GtreeNodeID)
			{
				Gtree.add(new TreeNode(GtreeNodeID));
			}
			String attr=datas[1];
			if(attr.equals("id"))
			{
				Gtree.get(GtreeNodeID).id=GtreeNodeID;
			}else if(attr.equals("borders"))
			{
				if(datas.length>2)
				{
					for(int i=2;i<datas.length;i++)
					{
						Gtree.get(GtreeNodeID).borders.add(Integer.parseInt(datas[i]));
					}
				}
			}else if(attr.equals("children"))
			{
				if(datas.length>2)
				{
					for(int i=2;i<datas.length;i++)
					{
						Gtree.get(GtreeNodeID).children.add(Integer.parseInt(datas[i]));
					}
				}
			}else if(attr.equals("isleaf"))
			{	
				Gtree.get(GtreeNodeID).isleaf=datas[2].equals("0")?false:true;
				
			}else if(attr.equals("leafnodes"))
			{
				if(datas.length>2)
				{
					for(int i=2;i<datas.length;i++)
					{
						Gtree.get(GtreeNodeID).leafnodes.add(Integer.parseInt(datas[i]));
					}
				}
				
			}else if(attr.equals("father"))
			{
				Gtree.get(GtreeNodeID).father=Integer.parseInt(datas[2]);
				
			}else if(attr.equals("union_borders"))
			{
				if(datas.length>2)
				{
					for(int i=2;i<datas.length;i++)
					{
						Gtree.get(GtreeNodeID).union_borders.add(Integer.parseInt(datas[i]));
					}
				}
			}else if(attr.equals("mind"))
			{
				if(datas.length>2)
				{
					for(int i=2;i<datas.length;i++)
					{
						Gtree.get(GtreeNodeID).mind.add(Integer.parseInt(datas[i]));
					}
				}
			}else if(attr.equals("nonleafinvlist"))
			{
				if(datas.length>2)
				{
					for(int i=2;i<datas.length;i++)
					{
						Gtree.get(GtreeNodeID).nonleafinvlist.add(Integer.parseInt(datas[i]));
					}
				}
			}else if(attr.equals("leafinvlist"))
			{
				if(datas.length>2)
				{
					for(int i=2;i<datas.length;i++)
					{
						Gtree.get(GtreeNodeID).leafinvlist.add(Integer.parseInt(datas[i]));
					}
				}
			}else if(attr.equals("up_pos"))
			{
				if(datas.length>2)
				{
					for(int i=2;i<datas.length;i++)
					{
						Gtree.get(GtreeNodeID).up_pos.add(Integer.parseInt(datas[i]));
					}
				}
			}else if(attr.equals("current_pos"))
			{
				if(datas.length>2)
				{
					for(int i=2;i<datas.length;i++)
					{
						Gtree.get(GtreeNodeID).current_pos.add(Integer.parseInt(datas[i]));
					}
				}
			}	
		}
		
		readerGTree.close();
//		int iddd=0;
//		System.out.println("id\t"+Gtree.get(iddd).id);
//		System.out.println("borders\t"+Gtree.get(iddd).borders.toString());
//		System.out.println("children\t"+Gtree.get(iddd).children.toString());
//		System.out.println("isleaf\t"+Gtree.get(iddd).isleaf);
//		System.out.println("leafnodes\t"+Gtree.get(iddd).leafnodes.toString());
//		System.out.println("father\t"+Gtree.get(iddd).father);
//		System.out.println("union_borders\t"+Gtree.get(iddd).union_borders.toString());
//		System.out.println("mind\t"+Gtree.get(iddd).mind.toString());
//		System.out.println("nonleafinvlist\t"+Gtree.get(iddd).nonleafinvlist.toString());
//		System.out.println("leafinvlist\t"+Gtree.get(iddd).leafinvlist.toString());
//		System.out.println("up_pos\t"+Gtree.get(iddd).up_pos.toString());
//		System.out.println("current_pos\t"+Gtree.get(iddd).current_pos.toString());
		
		while((lineNodes=readerNodes.readLine())!=null)
		{
			String datas[]=lineNodes.split(" ");
			int NodeID=Integer.parseInt(datas[0]);
			String attr=datas[1];
			if(Nodes.size()<=NodeID)
			{
				Nodes.add(new Node(NodeID));
				
			}
			if(attr.equals("id"))
			{
				Nodes.get(NodeID).id=NodeID;
				
			}else if(attr.equals("x"))
			{
				Nodes.get(NodeID).x=Double.parseDouble(datas[2]);
			}
			else if(attr.equals("y"))
			{
				Nodes.get(NodeID).y=Double.parseDouble(datas[2]);
			}
			else if(attr.equals("adjnodes"))
			{
				if(datas.length>2)
				{
					for(int i=2;i<datas.length;i++)
					{
						Nodes.get(NodeID).adjnodes.add(Integer.parseInt(datas[i]));
					}
				}
			}
			else if(attr.equals("adjweight"))
			{
				if(datas.length>2)
				{
					for(int i=2;i<datas.length;i++)
					{
						Nodes.get(NodeID).adjweight.add(Integer.parseInt(datas[i]));
					}
				}
			}
			else if(attr.equals("isborder"))
			{
				Nodes.get(NodeID).isborder=datas[2].equals("0")?false:true;
			}
			else if(attr.equals("gtreepath"))
			{
				if(datas.length>2)
				{
					for(int i=2;i<datas.length;i++)
					{
						Nodes.get(NodeID).gtreepath.add(Integer.parseInt(datas[i]));
					}
				}
			}
			
			
		}
		readerNodes.close();
		
	}
	
	
//	public void SaveGTREE()
//	{
//
//		db_GTreeMap.put(IGTreeConfigure.DATA_INDEX_IGT_GTREE_NAME, Gtree);
//		db_NodesMap.put(IGTreeConfigure.DATA_INDEX_IGT_NODE_NAME, Nodes);
//		
//		
//		
//		System.out.println("sad:\t"+db_GTreeMap.get("GTREE").size());
//		db.close();
//		System.out.println("save done!");
//	}
	
//	public void createDB()
//	{
//		System.out.println(DBNAME);
//		 File dbFile = new File(DBNAME);
//	        if(dbFile.exists()){
//	        	dbFile.delete();
//	        }
//	        db = DBMaker.newFileDB(dbFile)
//	        		//.asyncWriteEnable()
//	        		.transactionDisable()
//	        		.freeSpaceReclaimQ(0)
//	        		.asyncWriteEnable()
//	        		
//	        		//.cacheSize(1024*1024*100)
//	        		//.cacheLRUEnable()
//	        		//.cacheDisable()
//	        		//.fullChunkAllocationEnable()
//	        		//.mmapFileEnable()
//	        		//.syncOnCommitDisable()
//	        		.make();
//	                
//	}
	/**
	 * @param args
	 * @throws IOException 
	 * @throws Exception 
	 */
	public static void main(String[] args) throws IOException, Exception {
		// TODO Auto-generated method stub
		LoadBasicGTree test=new LoadBasicGTree();
		test.loadBasicGTreeAndNodes();
//		test.SaveGTREE();
//		test.db_G.wait(10000);
//		test.db_G.close();

	}

}

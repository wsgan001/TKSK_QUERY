package interfaces;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Vector;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

import Utils.IGTreeConfigure;

import GTree.Node;
import GTree.POI_Vertex;
import GTree.ResultSet;
import GTree.Status_query;
import GTree.TreeNode;

public class KNNQuery_V0 {
	
	/*MAP_DB begin*/
	DB db;
	String DBNAME;
	/*MAP_DB end*/
	
	public Vector<Node> Nodes;
	public Vector<TreeNode> Gtree;
	public int noe;
	
	/*priority queue & result set*/
	public PriorityQueue<Status_query> pq;
//	public Vector<Status_query> pq;
//	public Vector<ResultSet> rstset;
	public PriorityQueue<ResultSet> rstset;
	/*upstream*/
	public HashMap<Integer, Vector<Integer>> itm; // intermediate answer, tree node -> array
	
	/**
	 * For each Term ,store the Max score; 
	 */
	public HashMap<Integer, HashMap<String, Float>> GtreeInvertedMap;
//	public  HashMap<Integer, HashMap<String, Float>> VertexInvertedMap;
	
	public HashMap<Integer, HashMap<Integer, Vector<POI_Vertex>>>  GtreeLeafNodeMap;
	public HTreeMap<Integer, HashMap<Integer, Vector<POI_Vertex>>>  db_GtreeLeafNodeMap;
	
	public HashMap<String, Integer> POIkwdCount;
	public HashMap<String, Float> POIkwdScores;
//	public int currentVertexID=-1;
	
	public int locid;
	public int K;

	public KNNQuery_V0(int locid,int K) throws Exception {
		// TODO Auto-generated constructor stub
		this.locid=locid;
		this.K=K;
		init();
		System.out.println("loading data...");
		loadGTreeAndNodes();
		loadPOIToGTree();
		System.out.println("data loaded");
		
		System.out.println("data saving...");
		
		System.out.println("data saved");
		SaveDataDB();
		
	}
	
	
	
	
	public PriorityQueue<ResultSet> knn_query()
	{
		
		//TODO initUpStream
		/**
		 * Init the UPStream
		 */
		int tn, cid, posa, posb, min, dis;
		
		for(int i= Nodes.get(locid).gtreepath.size()-1;i>0;i--)
		{
			tn=Nodes.get(locid).gtreepath.get(i);
//			System.out.println(tn);
			
			if(!itm.containsKey(tn))
			{
				itm.put(tn,new Vector<Integer>());
			}
			itm.get(tn).clear();
			
			if(Gtree.get(tn).isleaf)
			{
				
				//这里的算法还有待讨论
				
				posa=Gtree.get(tn).lower_bound(locid);
//				if(true){return null;}
//				if(true){
//					System.out.println(posa);
//					return null;}
				for(int j=0;j<Gtree.get(tn).borders.size();j++)
				{
					itm.get(tn).add(Gtree.get(tn).mind.get(j*Gtree.get(tn).leafnodes.size()+posa));
				}
			
			}else
			{
				cid=Nodes.get(locid).gtreepath.get(i+1);
				
				for(int j=0;j<Gtree.get(tn).borders.size();j++)
				{
					min=-1;
					posa=Gtree.get(tn).current_pos.get(j);
					
					for(int k=0;k<Gtree.get(cid).borders.size();k++)
					{
						posb=Gtree.get(cid).up_pos.get(k);
						dis=itm.get(cid).get(k)+Gtree.get(tn).mind.get(posa*Gtree.get(tn).union_borders.size()+posb);
						//get min
						if(min==-1)
						{
							min=dis;
						}else
						{
							if(dis<min)
							{
								min=dis;
							}
						}
					}
					//update
					itm.get(tn).add(min);
				}
			}
		}
		
//		System.out.println(itm.keySet());
//		System.out.println("2652SIze:"+itm.get(2652).size());
//		for(int ii=0;ii<itm.get(2652).size();ii++)
//		{
//			System.out.println(itm.get(2652).get(ii));
//		}
		
		// TODO do search
		/**
		 * do search
		 */
		//do search
				Status_query rootstatus=new Status_query(0, false, 0, 0);
				pq.add(rootstatus);
				
				
				Vector<Integer> cands =new Vector<Integer>();
				Vector<Integer> result=new Vector<Integer>();
				int child;
				int son;
				int allmin;
				int vertex;
				
				while(pq.size()>0&&rstset.size()<K)
				{
					
					//原始代码
					//Status_query top = pq[0];//取到第一个最大的数
					//pop_heap( pq.begin(), pq.end(), Status_query_comp() );//将最大的数放到栈尾
					//pq.pop_back();//取得栈尾并删掉
//					if(pq.size()>20)
//					{
//						while(pq.size()>0)
//						{
//							Status_query pp=pq.peek();
//							pq.remove(pp);
//							System.out.println(pp.id+" "+pp.dis);
//						}
//						return null;
//					}
					Status_query top=pq.peek();
					pq.remove(top);
					
					
//						System.out.println(top.id);
//						System.out.println(top.dis);
//						System.out.println(top.isvertex);
//						if(true){return null;}
					
					//判断节点是否是vertex
					if(top.isvertex)
					{
						ResultSet rs=new ResultSet(top.id, top.dis);
						rstset.add(rs);
					}else
					{
						//判断节点是否是叶子节点
						if(Gtree.get(top.id).isleaf)
						{
							// inner of leaf node, do dijkstra  内部也自己点
							if(top.id==Nodes.get(locid).gtreepath.get(top.lca_pos))
							{
								cands.clear();
								for(int i=0;i<Gtree.get(top.id).leafinvlist.size();i++)
								{
									cands.add(Gtree.get(top.id).leafnodes.get(Gtree.get(top.id).leafinvlist.get(i)));
								}
								result=dijkstra_candidate(locid, cands);
								for(int i=0;i<cands.size();i++)
								{
									Status_query status=new Status_query(cands.get(i), true, top.lca_pos, result.get(i));
									pq.add(status);
//									//pq.push_back(status);
//									//push_heap( pq.begin(), pq.end(), Status_query_comp() );
								}
							}else
							{
								for(int i=0;i<Gtree.get(top.id).leafinvlist.size();i++)
								{
									posa=Gtree.get(top.id).leafinvlist.get(i);
									vertex=Gtree.get(top.id).leafnodes.get(posa);
									allmin=-1;
									
									for(int k=0;k<Gtree.get(top.id).borders.size();k++)
									{
										dis=itm.get(top.id).get(k)+Gtree.get(top.id).mind.get(k*Gtree.get(top.id).leafnodes.size()+posa);
										if(allmin==-1)
										{
											allmin=dis;
										}else
										{
											if(dis<allmin)
											{
												allmin=dis;
											}
										}
									}
									Status_query status=new Status_query(vertex, true, top.lca_pos, allmin);
									pq.add(status);
									//pq.push_back(status);
									//push_heap( pq.begin(), pq.end(), Status_query_comp() );
								}
							}

						}else
						{
							for(int i=0;i<Gtree.get(top.id).nonleafinvlist.size();i++)
							{
								child=Gtree.get(top.id).nonleafinvlist.get(i);
								son=Nodes.get(locid).gtreepath.get(top.lca_pos+1);
								//on gtreePath
								if(child==son)
								{
									Status_query status=new Status_query(child, false, top.lca_pos+1, 0);
									pq.add(status);
									//pq.push_back(status);
									//push_heap( pq.begin(), pq.end(), Status_query_comp() );
								}else if(Gtree.get(child).father==Gtree.get(son).father)/*bothers*/
								{
									if(!itm.containsKey(child))
									{
										itm.put(child, new  Vector<Integer>());
									}
									itm.get(child).clear();
									allmin=-1;
									
									for(int j=0;j<Gtree.get(child).borders.size();j++)
									{
										min=-1;
										posa=Gtree.get(child).up_pos.get(j);
										for(int k=0;k<Gtree.get(son).borders.size();k++)
										{
											posb=Gtree.get(son).up_pos.get(k);
											dis=itm.get(son).get(k)+Gtree.get(top.id).mind.get(posa*Gtree.get(top.id).union_borders.size()+posb);
											if(min==-1)
											{
												min=dis;
											}else
											{
												if(dis<min)
												{
													min=dis;
												}
											}
										}
										itm.get(child).add(min);
										//update all min
										if(allmin==-1)
										{
											allmin=min;
										}else if(min<allmin)
										{
											allmin=min;
										}
									}
									Status_query status=new Status_query(child, false, top.lca_pos, allmin);
									pq.add(status);
									//pq.push_back(status);
									//push_heap( pq.begin(), pq.end(), Status_query_comp() );
								}else/*downStream*/
								{
									if(!itm.containsKey(child))
									{
										itm.put(child, new  Vector<Integer>());
									}
									itm.get(child).clear();
									allmin=-1;
									for(int j=0;j<Gtree.get(child).borders.size();j++)
									{
										min=-1;
										posa=Gtree.get(child).up_pos.get(j);
										for(int k=0;k<Gtree.get(top.id).borders.size();k++)
										{
											posb=Gtree.get(top.id).current_pos.get(k);
											dis=itm.get(top.id).get(k)+Gtree.get(top.id).mind.get(posa*Gtree.get(top.id).union_borders.size()+posb);
											if(min==-1)
											{
												min=dis;
											}else
											{
												if(dis<min)
												{
													min=dis;
												}
											}
										}
										itm.get(child).add(min);
										
										//update all min
										if(allmin==-1)
										{
											allmin=min;
										}else if(min<allmin)
										{
											allmin=min;
										}
										
										
									}
									Status_query status=new Status_query(child, false, top.lca_pos, allmin);
									pq.add(status);
								}
								
							}
						}
						
						
						
						
						
					}
					
				}
				
				return rstset;
		
	
	}


	
	
	/**
	 * init priority queue & result set
	 */
	public void init()
	{
		this.pq=new PriorityQueue<Status_query>();
		this.rstset=new PriorityQueue<ResultSet>();
		this.Nodes=new Vector<Node>();
		this.Gtree=new Vector<TreeNode>(); 
		this.itm=new HashMap<Integer, Vector<Integer>>();
		this.GtreeInvertedMap=new HashMap<Integer, HashMap<String,Float>>();
//		this.VertexInvertedMap=new HashMap<Integer, HashMap<String,Float>>();
		this.GtreeLeafNodeMap=new HashMap<Integer, HashMap<Integer,Vector<POI_Vertex>>>();
		this.POIkwdCount=new HashMap<String, Integer>();
		this.POIkwdScores=new HashMap<String, Float>();
		
		
		this.DBNAME=IGTreeConfigure.DATA_ROOT_FOLDER+IGTreeConfigure.DATA_INDEX_FOLDER+IGTreeConfigure.DATA_INDEX_GLEAFPOI_NAME;
		CreateDB();
		db_GtreeLeafNodeMap=db.getHashMap("db_GtreeLeafNodeMap");
		
		
		
		dateClear();
	}
	
	
	public void dateClear()
	{
		this.pq.clear();
		this.rstset.clear();
	}

	
	public Vector<Integer> dijkstra_candidate(int s,Vector<Integer> cands,Vector<Node>Nodes)
	{
		return null;
		
	}
	@SuppressWarnings("unchecked")
	public Vector<Integer> dijkstra_candidate(int s,Vector<Integer> cands)
	{
//		Vector<Integer> todo=new Vector<Integer>();
		HashMap<Integer,Integer> todo=new HashMap<Integer,Integer>();
		todo.clear();
//		todo=(Vector<Integer>) cands.clone();
		for(int i=0;i<cands.size();i++)
		{
			todo.put(cands.get(i), cands.get(i));
		}
		
		
		HashMap<Integer, Integer> result=new HashMap<Integer, Integer>();
		result.clear();
		
		Vector<Integer> visited=new Vector<Integer>();
		visited.clear();
		HashMap<Integer, Integer> q=new HashMap<Integer, Integer>();
		q.clear();
		q.put(s, 0);
		
		//start
		int min=-1;
		int minpos = -1;
		int adjnode=-1;
		int weight=-1;
		while(!todo.isEmpty()&&!q.isEmpty())
		{
			min=-1;
			for(Integer itID:q.keySet())
			{
				if(min==-1)
				{
					minpos=itID;
					min=q.get(itID);
				}else
				{
					if(q.get(itID)<min)
					{
						min=q.get(itID);
						minpos=itID;
					}
				}
			}
			
			// put min to result, add to visited
			result.put(minpos, min);
			visited.add(minpos);
			q.remove(minpos);
			
			//或者是
			if(todo.containsKey(minpos))
//			if(todo.get(minpos)!=todo.lastElement())
			{
				todo.remove(minpos);
			}
			
			//expend
			for(int i=0;i<Nodes.get(minpos).adjnodes.size();i++)
			{
				adjnode=Nodes.get(minpos).adjnodes.get(i);
				if(visited.contains(adjnode))
				{
					continue;
				}
//				if(visited.get(adjnode)!=todo.lastElement())
//				{
//					continue;
//				}
				weight=Nodes.get(minpos).adjweight.get(i);
				if(q.containsKey(adjnode))
				{
					if(min+weight<q.get(adjnode))
					{
						q.put(adjnode,min+weight);
					}
				}else
				{
					q.put(adjnode,min+weight);
				}
			}
	
			
		}
		
		
		//output
			Vector<Integer> output=new Vector<Integer>();
			for(int i=0;i<cands.size();i++)
			{
				output.add(result.get(cands.get(i)));
			}
			
		return output;	
	}
	
	
	public void loadGTreeAndNodes() throws Exception
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
	
	public void TEST_FOR_NODES(int nid)
	{
		int idnn=nid;
		System.out.println("id:\t"+Nodes.get(idnn).id);
		System.out.println("isborder:\t"+Nodes.get(idnn).isborder);
		System.out.println("x:\t"+Nodes.get(idnn).x);
		System.out.println("y:\t"+Nodes.get(idnn).y);
		System.out.println("adjweight:\t"+Nodes.get(idnn).adjweight.toString());
		System.out.println("adjnodes:\t"+Nodes.get(idnn).adjnodes.toString());
		System.out.println("gtreepath:\t"+Nodes.get(idnn).gtreepath.toString());
	}
	public void TEST_FOR_GTREE(int gid)
	{
		int iddd=gid;
		System.out.println("id\t"+Gtree.get(iddd).id);
		System.out.println("borders\t"+Gtree.get(iddd).borders.toString());
		System.out.println("children\t"+Gtree.get(iddd).children.toString());
		System.out.println("isleaf\t"+Gtree.get(iddd).isleaf);
		System.out.println("leafnodes\t"+Gtree.get(iddd).leafnodes.toString());
		System.out.println("father\t"+Gtree.get(iddd).father);
		System.out.println("union_borders\t"+Gtree.get(iddd).union_borders.toString());
		System.out.println("mind\t"+Gtree.get(iddd).mind.toString());
		System.out.println("nonleafinvlist\t"+Gtree.get(iddd).nonleafinvlist.toString());
		System.out.println("leafinvlist\t"+Gtree.get(iddd).leafinvlist.toString());
		System.out.println("up_pos\t"+Gtree.get(iddd).up_pos.toString());
		System.out.println("current_pos\t"+Gtree.get(iddd).current_pos.toString());
	}
	
	public  void loadPOIToGTree() throws IOException
	{
		BufferedReader readerPOI=new BufferedReader(new FileReader(IGTreeConfigure.DATA_POI_FILE_NAME));
		String line;
		int pid=0;
		while((line=readerPOI.readLine())!=null)
		{
			String[] datas=line.split(" ");
			//vertex对应Node
			int vertexIDPOI=Integer.parseInt(datas[0]);
			
			//get  keywordCount
			this.POIkwdCount.clear();
			this.POIkwdScores.clear();
//			this.currentVertexID=vertexIDPOI;
			
			for (int i = 3; i < datas.length; i += 2) {
				String kwd = datas[i];
				
				int freq = Integer.parseInt(datas[i + 1]);
				if (POIkwdCount.containsKey(kwd)) {
					POIkwdCount.put(kwd, POIkwdCount.get(kwd) + freq);
				} else {
					POIkwdCount.put(kwd, freq);
				}
			}
			
			// Convert the frequency of a keyword into a relevance score.
			double sumeOfWeightSquare = 0;
			double weight;
			for (String kwd : POIkwdCount.keySet()) {
				int freq = POIkwdCount.get(kwd);
				weight = 1 + Math.log(freq);
				sumeOfWeightSquare += weight * weight;
			}
			sumeOfWeightSquare = Math.sqrt(sumeOfWeightSquare);	
			for (String kwd : POIkwdCount.keySet()) {
				int freq = POIkwdCount.get(kwd);
				POIkwdScores.put(kwd, (float)((1 + Math.log(freq))/ sumeOfWeightSquare));
			}
			/*get kwdScoreEnd*/
			
			/* add info to Vertex*/
			//step 1 get the Node in Gtree
			int GNode=Nodes.get(vertexIDPOI).gtreepath.lastElement();
			POI_Vertex poiV=new POI_Vertex(pid, vertexIDPOI, POIkwdScores);
			pid++;
			if(!GtreeLeafNodeMap.containsKey(GNode))
			{
				GtreeLeafNodeMap.put(GNode,new HashMap<Integer, Vector<POI_Vertex>>());
			}
			if(!GtreeLeafNodeMap.get(GNode).containsKey(vertexIDPOI))
			{
				GtreeLeafNodeMap.get(GNode).put(vertexIDPOI,new Vector<POI_Vertex>());
			}
			GtreeLeafNodeMap.get(GNode).get(vertexIDPOI).add(poiV);
			
			
			
			
			//根据Vertex  找到其所挂的GTree上的节点的ID
			for(int i=Nodes.get(vertexIDPOI).gtreepath.size()-1;i>=0;i--)
			{
				
//				if(i==-1)
//				{
//					System.out.println(i);
//					System.out.println(Nodes.get(vertexIDPOI).gtreepath.toString());
//					System.exit(-1);
//				}
				
				if(!GtreeInvertedMap.containsKey(Nodes.get(vertexIDPOI).gtreepath.get(i)))
				{
					GtreeInvertedMap.put(Nodes.get(vertexIDPOI).gtreepath.get(i), new HashMap<String, Float>());
				}
				for(String kwd:POIkwdScores.keySet())
				{
					
					if(!GtreeInvertedMap.get(Nodes.get(vertexIDPOI).gtreepath.get(i)).containsKey(kwd))
					{
						GtreeInvertedMap.get(Nodes.get(vertexIDPOI).gtreepath.get(i)).put(kwd, POIkwdScores.get(kwd));
					}else
					{
						//new  bigger update
						if(GtreeInvertedMap.get(Nodes.get(vertexIDPOI).gtreepath.get(i)).get(kwd)<POIkwdScores.get(kwd))
						{
							GtreeInvertedMap.get(Nodes.get(vertexIDPOI).gtreepath.get(i)).put(kwd, POIkwdScores.get(kwd));
						}
					}
					
					
				}
			}
			System.out.println(Nodes.get(vertexIDPOI).gtreepath.toString());
			for(Integer nodeID:GtreeInvertedMap.keySet())
			{
				System.out.println(nodeID+"==="+GtreeInvertedMap.get(nodeID).keySet());
			}
			System.out.println(GtreeLeafNodeMap.get(GNode).get(vertexIDPOI).firstElement().kwdScoresMap.keySet().toString());
			
			System.exit(-1);
			
		}
		
	}
	
	public void TestVertex(int vertexID)
	{
		Node targetNode=Nodes.get(vertexID);
		
		int GtreeNode=targetNode.gtreepath.lastElement();
		
		TreeNode gNode=Gtree.get(GtreeNode);
		int  fathweID=GtreeNode;
		System.out.println(targetNode.gtreepath.toString());
		while(fathweID!=0)
		{
			System.out.println(fathweID);
			fathweID=Gtree.get(fathweID).father;
		}
	}
	
	public void CreateDB()
	{
		System.out.println(DBNAME);
		 File dbFile = new File(DBNAME);
	        if(dbFile.exists()){
	        	dbFile.delete();
	        }
	        db = DBMaker.newFileDB(dbFile)
	        		//.asyncWriteEnable()
	        		.transactionDisable()
	        		.freeSpaceReclaimQ(0)
	        		.asyncWriteEnable()
	        		
	        		//.cacheSize(1024*1024*100)
	        		//.cacheLRUEnable()
	        		//.cacheDisable()
	        		//.fullChunkAllocationEnable()
	        		//.mmapFileEnable()
	        		//.syncOnCommitDisable()
	        		.make();
	}
	
	public void SaveDataDB()
	{
		db_GtreeLeafNodeMap.putAll(GtreeLeafNodeMap);
		db.close();
	}
	
	
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		KNNQuery_V0 test=new KNNQuery_V0(10, 10);
//		test.TestVertex(12);
//		test.TEST_FOR_NODES(102);
//		PriorityQueue<ResultSet> topResult=test.knn_query();
//		while(topResult.size()>0)
//		{
//			ResultSet rst=topResult.poll();
//			System.out.println(rst.id+"   "+rst.dis);
//		}
		
	}

}

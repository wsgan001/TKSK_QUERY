package interfaces;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Vector;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;


import elements.CandidatePOI;

import GTree.Node;
import GTree.POI_Vertex;
import GTree.ResultSet;
import GTree.Status_query;
import GTree.TreeNode;
import Utils.IGTreeConfigure;

public class IGTree_querying {
	public DB db;
	public String DBNAME;
	public int locid;
	public Vector<String> kwds;
	public int K;
	public Vector<Node> Nodes;
	public Vector<TreeNode> Gtree;
	public int noe;
	public String queryFile;
	
	
	/*priority queue & result set*/
	public PriorityQueue<Status_query> pq;

//	public PriorityQueue<ResultSet> rstset;
	public PriorityQueue<CandidatePOI> resultPOIs;
	/*upstream*/
	public HashMap<Integer, Vector<Integer>> itm; // intermediate answer, tree node -> array
	
	public long TimeForIsG=0;
	public long TimeForIsN=0;
	
	
	public int POIAccessed=0;
	
	/**
	 * Map_DB中的db_MAP
	 */
	public HTreeMap<Integer, HashMap<String, Float>> db_GtreeInvertedMap;
	public HTreeMap<Integer, HashMap<Integer, Vector<POI_Vertex>>>  db_GtreeLeafNodeMap;
	public HTreeMap<String, Vector<Node>> db_NodesMap;
	public HTreeMap<String, Vector<TreeNode>> db_GTreeMap;
	
	/**
	 * Gtree中每个节点的Inverted list
	 */
	public HashMap<Integer, HashMap<String, Float>> GtreeInvertedMap;
	/**
	 * Gtree中每个叶子节点所挂的路网中
	 */
	public HashMap<Integer, HashMap<Integer, Vector<POI_Vertex>>>  GtreeLeafNodeMap;
	
	
	
	
	
	public IGTree_querying(String fileName,int topk) {
		// TODO Auto-generated constructor stub
		this.locid=-1;
		this.K=topk;
		this.kwds=new Vector<String>();
		this.queryFile=fileName;
		init();
	}
	
	public void init()
	{
		this.DBNAME=IGTreeConfigure.DATA_ROOT_FOLDER+IGTreeConfigure.DATA_INDEX_FOLDER+IGTreeConfigure.DATA_INDEX_GLEAFPOI_NAME;
		loadDB();
		System.out.println(this.DBNAME);
		this.db_GtreeLeafNodeMap=db.getHashMap("db_GtreeLeafNodeMap");
		this.db_NodesMap=db.getHashMap("db_NodesMap");
		this.db_GTreeMap=db.getHashMap("db_GTreeMap");
		this.db_GtreeInvertedMap=db.getHashMap("db_GtreeInvertedMap");
		
		
		
		this.pq=new PriorityQueue<Status_query>();
		this.resultPOIs=new PriorityQueue<CandidatePOI>();
//		this.rstset=new PriorityQueue<ResultSet>();
		this.Nodes=new Vector<Node>();
		this.Gtree=new Vector<TreeNode>(); 
		this.itm=new HashMap<Integer, Vector<Integer>>();
		
		
		this.GtreeInvertedMap=new HashMap<Integer, HashMap<String,Float>>();
		this.GtreeLeafNodeMap=new HashMap<Integer, HashMap<Integer,Vector<POI_Vertex>>>();
		System.out.println("Hree");
		loadGT();
		System.out.println("Hree2");
		loadGTI();
		System.out.println("Hree3");
		
	}
	
	
	public void loadGTI()
	{
		
//		for(int i=0;i<Gtree.size();i++)
//		{
//			HashMap<String, Float> GnodeInverted=new HashMap<String, Float>();
//			int Gid=Gtree.get(i).id;
//			GnodeInverted=this.db_GtreeInvertedMap.get(Gid);
//			if(GnodeInverted!=null)
//			{
//				GtreeInvertedMap.put(Gid,GnodeInverted);
//			}
//			
//		}
//		for(int i=0;i<Gtree.size();i++)
//		{
//			HashMap<Integer,Vector<POI_Vertex>> GnodeInvertedPOI=new HashMap<Integer, Vector<POI_Vertex>>();
//			int Gid=Gtree.get(i).id;
//			GnodeInvertedPOI=this.db_GtreeLeafNodeMap.get(Gid);
//			if(GnodeInvertedPOI!=null)
//			{
//				GtreeLeafNodeMap.put(Gid,GnodeInvertedPOI);
//			}
//			
//		}
		
	}
	
	
	
	public void loadGT()
	{
		Gtree=db_GTreeMap.get(IGTreeConfigure.DATA_INDEX_IGT_GTREE_NAME);
		Nodes=db_NodesMap.get(IGTreeConfigure.DATA_INDEX_IGT_NODE_NAME);
		if(Gtree==null)
		{
			System.out.println("WHW");
		}
//		System.out.println(Gtree.size());
//		if(Gtree.size()>0)
//		{
//			System.out.println(Gtree.firstElement().children.toString());
//		}
//		if(Nodes.size()>0)
//		{
//			System.out.println(Nodes.firstElement().gtreepath.toString());
//		}
	}
	public void loadDB()
	{
		File dbFile = new File(DBNAME);
		db = DBMaker.newFileDB(dbFile)
				.transactionDisable()
				//.cacheDisable()
                .closeOnJvmShutdown()
                .make();
		
	}
	
	public void run() throws IOException
	{
		BufferedReader reader=new BufferedReader(new FileReader(this.queryFile));
		String line;
		int queryNum=0;
		long totalTime=0;
		while((line=reader.readLine())!=null&&queryNum<200)
		{
			String[] datas=line.split(" ");
			if(datas.length<5)
			{
				System.out.println("Data Passing");
				System.out.println(line);
				continue;
			}
			this.locid=Integer.parseInt(datas[0]);//获得vertexID
			kwds.clear();
			for(int i=3;i<datas.length;i=i+2)
			{
				kwds.add(datas[i]);
			}
			long run_start=System.currentTimeMillis();
			resultPOIs=knn_query();
			long run_end=System.currentTimeMillis();
			queryNum++;
			
			printTOPKPOI();
//			System.out.println("query ID"+this.locid);
//			System.out.println("query works"+this.kwds.toString());
//			System.out.println(queryNum+":\t"+(run_end-run_start));
			totalTime+=run_end-run_start;
		}
		
		
		System.err.println("QUERYNUM:\t"+queryNum);
		
		System.err.println("AVG TIME:"+(totalTime/(queryNum*1.0)));
		
		System.err.println("AVG IT:\t"+(POIAccessed/(queryNum*1.0)));
	}
	public void dataClear()
	{
		this.kwds.clear();
		this.resultPOIs.clear();
	}
	
	
	public void printTOPKPOI()
	{
//		System.out.println("Query vertex:"+this.locid);
//		System.out.println("Query KWD:"+this.kwds.toString());
		System.out.println("result num:"+this.resultPOIs.size());
		while(resultPOIs.size()>0)
		{
			System.out.println(resultPOIs.poll().id);
		}
	}
	
	
	
	
	public PriorityQueue<CandidatePOI> knn_query()
	{
		
		//TODO initUpStream
		/**
		 * Init the UPStream
		 */
		int tn, cid, posa, posb, min, dis;
//		System.out.println("ssss"+Nodes.size());
		
		long start_upStream=System.currentTimeMillis();
		
		if(locid<0||locid>=Nodes.size())
		{
			System.out.println("Loc Error");
			return null;
		}
		
		
		for(int i= Nodes.get(locid).gtreepath.size()-1;i>0;i--)
		{
			tn=Nodes.get(locid).gtreepath.get(i);
//			System.out.println(tn);
			POIAccessed++;
			if(!itm.containsKey(tn))
			{
				itm.put(tn,new Vector<Integer>());
			}
			itm.get(tn).clear();
			
			
			
			
			
			if(Gtree.get(tn).isleaf)
			{
				
				//这里的算法还有待讨论
				
				posa=Gtree.get(tn).lower_bound(locid);
				POIAccessed++;
//				if(true){return null;}
//				if(true){
//					System.out.println(posa);
//					return null;}
				for(int j=0;j<Gtree.get(tn).borders.size();j++)
				{
					itm.get(tn).add(Gtree.get(tn).mind.get(j*Gtree.get(tn).leafnodes.size()+posa));
				}
			
			}else   /*非叶子节点*/
			{
				
				cid=Nodes.get(locid).gtreepath.get(i+1);
				POIAccessed++;
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
		long end_upStream=System.currentTimeMillis();
		System.err.println("Time of Init UpStream:"+(end_upStream-start_upStream));
		
		
		
		
		
//		System.out.println(itm.keySet());
//		System.out.println("2652SIze:"+itm.get(2652).size());
//		for(int ii=0;ii<itm.get(2652).size();ii++)
//		{
//			System.out.println(itm.get(2652).get(ii));
//		}
		long start_doSearch=System.currentTimeMillis();
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
				
				while(pq.size()>0&&resultPOIs.size()<K)
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
						//这里判断
						Vector<CandidatePOI> newResPOI=isVertexSatisfied(Nodes.get(top.id).gtreepath.lastElement(), top.id, top.dis);
						
						
						if(newResPOI.size()>0)
						{
							for(int candi=0;candi<newResPOI.size();candi++)
							{
								
								
								resultPOIs.add(newResPOI.get(candi));
							}
						}
						
						
//						ResultSet rs=new ResultSet(top.id, top.dis);
//						rstset.add(rs);
					}else
					{
						
//						System.out.println(" sss\t"+Gtree.get(top.id).isleaf);
						if(!isGNodeSatisfied(top.id))
						{
							continue;
						}
						
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
				long end_doSearch=System.currentTimeMillis();
				System.err.println("Time of Do Query"+(end_doSearch-start_doSearch));
				
				System.err.println("TimeForIsG"+TimeForIsG);
				System.err.println("TimeForIsN"+TimeForIsN);
				return resultPOIs;
		
	
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
	
	public boolean isGNodeSatisfied(int Gnode)
	{
//		long begin=System.nanoTime();
		
		if(!GtreeInvertedMap.containsKey(Gnode))
		{
			HashMap<String, Float> tmp=db_GtreeInvertedMap.get(Gnode);
			if(tmp==null)
			{
				return false;
			}
			GtreeInvertedMap.put(Gnode, db_GtreeInvertedMap.get(Gnode));
		}
		
		//根据词汇判断
		float scores=0;
		for(int i=0;i<kwds.size();i++)
		{
//			System.out.println(Gnode);
			
			
			if(GtreeInvertedMap.get(Gnode).containsKey(kwds.get(i)))
			{
				scores+=GtreeInvertedMap.get(Gnode).get(kwds.get(i));
			}
		}
//		System.out.println("GGGNNNDDD:"+Gnode+":"+scores);
		if(scores>=IGTreeConfigure.QUERY_TEXT_THRESHOD)
		{
//			System.out.println("GNH:"+Gnode+"\t"+scores);
			return true;
		}
//		long end=System.nanoTime();
//		TimeForIsG+=(end-begin);
		return false;
		
		
	}
	
	public Vector<CandidatePOI> isVertexSatisfied(int nid,int vid,int distance)
	{
//		long begin=System.nanoTime();
		Vector<CandidatePOI> restPOI=new Vector<CandidatePOI>();
		
//		System.out.println("nid:"+nid+"=====vid:"+vid);
//		if(true)return null;
		if(!GtreeLeafNodeMap.containsKey(nid))
		{
//			System.out.println("HERE");
			GtreeLeafNodeMap.put(nid, db_GtreeLeafNodeMap.get(nid));
		}
		
		if(GtreeLeafNodeMap.get(nid).containsKey(vid))
		{
//			System.out.println("HERE2");
			Vector<POI_Vertex> cand=GtreeLeafNodeMap.get(nid).get(vid);
			POIAccessed+=cand.size();
			if(cand!=null)
			{
				for(int i=0;i<cand.size();i++)
				{
					float scores=0;
					for(int j=0;j<kwds.size();j++)
					{
						if(cand.get(i).kwdScoresMap.containsKey(kwds.get(j)))
						{
							scores+=cand.get(i).kwdScoresMap.get(kwds.get(j));
						}
					}
//					System.out.println(cand.get(i).id+"====="+scores);
					if(scores>=IGTreeConfigure.QUERY_TEXT_THRESHOD)
					{
//						System.out.println(cand.get(i).id+"====="+scores);
						restPOI.add(new CandidatePOI(cand.get(i).id,distance));
					}
				}
			}
			
		}
//		long end=System.nanoTime();
//		TimeForIsN+=end-begin;
		return restPOI;
	}
	
	public void printGNL()
	{
		for(Integer gnode:GtreeInvertedMap.keySet())
		{
			System.out.println(gnode);
			for(String no:GtreeInvertedMap.get(gnode).keySet())
			{
				System.out.println(no+"\t====\t"+GtreeInvertedMap.get(gnode).get(no));
			}
		}
	}
	
//	public void printGL()
//	{
//		for(Integer glnode:GtreeLeafNodeMap.keySet())
//		{
//			System.out.println(gnode);
//		}
//	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String queryFile=IGTreeConfigure.DATA_ROOT_FOLDER_PATH+IGTreeConfigure.QUERY_FILE_FOLDER+IGTreeConfigure.dataSize+"/"+IGTreeConfigure.ROAD_NETWORK+"_DataSize_"+IGTreeConfigure.dataSize+"_kwdNum_"+IGTreeConfigure.queryNum+".txt";
		int K=IGTreeConfigure.QUERY_TOP_K;
		System.out.println(queryFile);
		IGTree_querying test=new IGTree_querying(queryFile,K);
//		test.printGNL();
		test.run();
	}

}

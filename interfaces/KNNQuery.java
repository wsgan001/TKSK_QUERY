package interfaces;

import java.util.Collections;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Vector;

import GTree.Node;
import GTree.ResultSet;
import GTree.Status_query;
import GTree.TreeNode;

public class KNNQuery {
	
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
	
	
	public int locid;
	public int K;

	public KNNQuery(int locid,int K) {
		// TODO Auto-generated constructor stub
		this.locid=locid;
		this.K=K;
		
	}
	
	
	
	
	public PriorityQueue<ResultSet> knn_query()
	{
		init();
		//TODO initUpStream
		/**
		 * Init the UPStream
		 */
		int tn, cid, posa, posb, min, dis;
		
		for(int i= Nodes.get(locid).gtreepath.size()-1;i>0;i--)
		{
			tn=Nodes.get(locid).gtreepath.get(i);
			itm.get(tn).clear();
			
			if(Gtree.get(tn).isleaf)
			{
				
				//这里的算法还有待讨论
				posa=Gtree.get(tn).lower_bound(locid);
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
					Status_query top=pq.poll();
					
					if(top.isvertex)
					{
						ResultSet rs=new ResultSet(top.id, top.dis);
						rstset.add(rs);
					}else
					{
						if(Gtree.get(top.id).isleaf)
						{
							// inner of leaf node, do dijkstra
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
		pq=new PriorityQueue<Status_query>();
		rstset=new PriorityQueue<ResultSet>();
		Nodes=new Vector<Node>();
		Gtree=new Vector<TreeNode>(); 
		dateClear();
	}
	
	
	public void dateClear()
	{
		pq.clear();
		rstset.clear();
	}

	
	public Vector<Integer> dijkstra_candidate(int s,Vector<Integer> cands,Vector<Node>Nodes)
	{
		return null;
		
	}
	@SuppressWarnings("unchecked")
	public Vector<Integer> dijkstra_candidate(int s,Vector<Integer> cands)
	{
		Vector<Integer> todo=new Vector<Integer>();
		
		todo.clear();
		todo=(Vector<Integer>) cands.clone();
		
		
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
			if(todo.contains(minpos))
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
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

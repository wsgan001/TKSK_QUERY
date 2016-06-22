package interfaces;

import java.io.File;
import java.util.Vector;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

import GTree.Node;
import Utils.IGTreeConfigure;

public class DBGTree {
	public DB db;
	public String DBName = IGTreeConfigure.DATA_ROOT_FOLDER+IGTreeConfigure.DATA_INDEX_FOLDER+IGTreeConfigure.DATA_INDEX_NAME;;
	public HTreeMap<String, Vector<Node>> NodesMap;
	public DBGTree() {
		// TODO Auto-generated constructor stub
//		NodesMap=db.getHashMap(IGTreeConfigure.DATA_INDEX_NODES);
//		loadDB();
	}

	
	public void loadDB()
	{
		File dbFile = new File(DBName);
		db = DBMaker.newFileDB(dbFile)
				.transactionDisable()
				//.cacheDisable()
                .closeOnJvmShutdown()
                .make();
		System.out.println("LAOD");
		
	}
	
	public void test()
	{
		Vector<Node> nodes=new Vector<Node>();
		nodes=NodesMap.get("NODE");
		System.out.println(nodes.size());
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		DBGTree test=new DBGTree();
//		test.test();

	}

}

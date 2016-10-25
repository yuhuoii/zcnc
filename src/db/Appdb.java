package db;

import java.sql.ResultSet;
import java.sql.SQLException;
//import java.sql.SQLException;

public class Appdb extends ZBaseDB {

	// 应用系统数据库定义
	private final static String DBFILENAME = "zcpic.db";
	private static Appdb adb = new Appdb(DBFILENAME);

	public static Appdb getDb() {
		if (adb == null)
			adb = new Appdb(DBFILENAME);
		return adb;
	}

	private Appdb(String dbname) {
		super(dbname);
	}

	// private static ZBaseDao user = adb.new ZBaseDao("user", new String[][] {
	// _id
	// { "name", "text" }, { "age", "integer" } });;

	private static ZBaseDao task = adb.new ZBaseDao("task", new String[][] {
			// _id
			{ "title", "text" }, 
			{ "url", "text" }, { "id", "text" }, { "vdown", "text" }, { "next", "text" }
			});
	private static ZBaseDao afile = adb.new ZBaseDao("afile", new String[][] {
			// _id
			{ "task", "integer" }, { "url", "text" }, { "fname", "text" }, { "size", "integer" } });
	
	static {
		setTable(new ZBaseDao[] { task,afile });

		setSqls(new String[] {				
		// "insert into user ( name, age ) values('aaa',18 ) "
		});
	}

	// 操作表的接口
	// 任务
	public ResultSet tasks() {
		return task.listRow(null);
	}

	public int newTask(String[][] val) {
		return Integer.parseInt(task.newRow(val));
	}
	
	public void saveTask( long id, String[][] val)
	{
		task.updateRow( id, val );
	}

	public void delTask(long id) {
		task.delRow(id);
	}

	public String getTask( String keyt ) throws SQLException
	{
		ResultSet rs = task.listRow( new String[][] { { "keyt", keyt } } );
		return rs.getString("dis");
		
	}

	/*
	 * 
	 * public ResultSet users() { return user.listRow(null); } public void
	 * newUser(String[][] val) { user.newRow(val); }
	 * 
	 * public void upUser(long aid, String[][] vals) { user.updateRow(aid,
	 * vals); }
	 * 
	 * public ResultSet findUser(String name) { return user.listRow(new
	 * String[][] { { "name", name } }); }
	 * 
	 * public String delUser(String name) { // 先查后删除，要执行两次SQL ResultSet rs =
	 * findUser(name); try { if (rs.next()) { int id = rs.getInt(1);
	 * user.delRow(id); } } catch (SQLException e) { e.printStackTrace(); }
	 * return name;
	 * 
	 * // 或直接用SQL删除 // return
	 * user.exec("delete from user where name='"+name+"'"); }//
	 */

}

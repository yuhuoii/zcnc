package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ZBaseDB {

	private String DATABASE_NAME; 

	private Connection conn = null;
	private Statement db = null;

	public ZBaseDB(String dbname) {
		DATABASE_NAME = dbname;
		
		open();//连接数据库
		
	}
	public ZBaseDB open()  {
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_NAME);		 
			db = conn.createStatement();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return this;
	}

	/**
	 * 建表操作，应该只执行一次
	 */
	public void createDB() {
		for (ZBaseDao at : tables) {
			//at.dropTable();
			String sql = at.createTable();
			System.out.println(sql);
			// Log.w(TAG, "建表SQL:" + sql);
			// db.execSQL( sql );
			// at.createTable( db);
		}
		for (String sql : initsqls)// 初始化数据
		{
			try {
				db.execute(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void close() {
		try {
			db.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected class ZBaseDao {
		public String TNAME;// user
		public String PKEY = "_id";
		public String[][] CNAMES;

		// { {"name","text"},{"age","integer"}}

		public ZBaseDao(String tablen, String[][] cnames) {
			TNAME = tablen;
			CNAMES = cnames;
		}

		public String createTable() {
			StringBuilder sql = new StringBuilder();
			sql.append("create table ");
			sql.append(TNAME);
			sql.append(" ( ");
			sql.append(PKEY);
			sql.append(" integer primary key autoincrement, ");
			for (String cname[] : CNAMES) {
				sql.append(cname[0]);
				sql.append(" ");
				sql.append(cname[1]);
				sql.append(",");
			}
			String tmps = sql.toString();
			tmps = tmps.substring(0, tmps.length() - 1);

			StringBuilder sqlb = new StringBuilder(tmps);
			sqlb.append(" ); ");

			// db.execSQL(sqlb.toString());
			try {
				db.execute(sqlb.toString());
			} catch (SQLException e) {
				e.printStackTrace();
			}

			return sqlb.toString();
		}

		public String dropTable() {
			StringBuilder sql = new StringBuilder("DROP TABLE IF EXISTS ");
			sql.append(TNAME);

			// db.execSQL(sql.toString());
			try {
				db.execute(sql.toString());
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return sql.toString();
		}

		public String newRow(String[][] pvals) {
			StringBuilder ins = new StringBuilder("insert into " + TNAME
					+ " ( ");
			StringBuilder vals = new StringBuilder(" values( ");
			for (String[] cname : pvals) {
				ins.append(cname[0]);
				ins.append(",");

				vals.append("'");
				vals.append(checkStr(cname[1]));
				vals.append("',");
			}
			String tmps = ins.toString();
			tmps = tmps.substring(0, tmps.length() - 1);

			String tmpv = vals.toString();
			tmpv = tmpv.substring(0, tmpv.length() - 1);

			StringBuilder sql = new StringBuilder();
			sql.append(tmps);// insert
			sql.append(" ) ");
			sql.append(tmpv);// values
			sql.append(" ) ");
			
			// db.execSQL(sql.toString());
			try {
				db.execute(sql.toString());
				ResultSet rs = db.executeQuery( "select last_insert_rowid()");
				if( rs.next() )
				{
					return ""+ rs.getLong(1);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return sql.toString();
		}
		

		public String delRow(long id) {
			StringBuilder sql = new StringBuilder("delete from " + TNAME
					+ " where _id=");
			sql.append("" + id);

			// db.execSQL(sql.toString());
			try {
				db.execute(sql.toString());
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return sql.toString();
		}

		public ResultSet findRow(long id) {
			StringBuilder sql = new StringBuilder("select * from " + TNAME
					+ " where _id=");
			sql.append("" + id);

			ResultSet mCursor = null;
			try {
				mCursor = db.executeQuery(sql.toString());
				// if (mCursor != null) mCursor.first();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return mCursor;
			// return sql.toString();
		}

		public ResultSet listRow(String[][] keyVals) {
			/*
			 * ResultSet rs = stmt.executeQuery("select * from user"); while (rs.next())
			 * { String id = rs.getString(1); //String code = rs.getString(2); String
			 * code=""; System.out.println("用户名:" + id + "， 密码:" + code); } rs.close();
			 */
			StringBuilder where = new StringBuilder();
			String tmps = null;
			if (keyVals != null) {
				for (String[] aMap : keyVals) {
					where.append(aMap[0]);
					where.append("='");
					where.append(checkStr(aMap[1]));
					where.append("' and ");
				}
				if (where.length() > 0) {
					tmps = where.toString();
					tmps = tmps.substring(0, tmps.length() - 4);
				}
			}

			StringBuilder sql = new StringBuilder();
			sql.append("select * from ");
			sql.append(TNAME);
			if (where.length() > 0) {
				sql.append(" where ");
				sql.append(tmps);
			}

			ResultSet mCursor = null;
			try {
				mCursor = db.executeQuery(sql.toString());
			} catch (SQLException e) {
				e.printStackTrace();
			}

			return mCursor;
			// return sql.toString ();
		}

		public String updateRow(long id, String[][] vals) {
			StringBuilder upd = new StringBuilder();
			upd.append("update ");
			upd.append(TNAME);
			upd.append(" set ");

			StringBuilder where = new StringBuilder(" where _id=");
			where.append(id);

			for (String[] cname : vals) {
				upd.append(cname[0]);
				upd.append(" ='");
				upd.append(checkStr(cname[1]));
				upd.append("',");
			}
			String tmps = upd.toString();
			tmps = tmps.substring(0, tmps.length() - 1);

			StringBuilder sql = new StringBuilder();
			sql.append(tmps);// upd
			sql.append(where.toString());// where

			try {
				db.execute(sql.toString());
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return sql.toString();
		}
		public String exec( String sql )
		{
			try {
				db.execute(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return sql;
		}
		
		public ResultSet query( String sql )
		{
			ResultSet rs=null;
			try {
				rs = db.executeQuery(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return rs;
		}
		
		public ResultSet query( String sql, String[] param )
		{
			ResultSet rs=null;
			try {
				PreparedStatement sqlca = conn.prepareStatement(sql);
				for( int i =0;i< param.length; i++)
					sqlca.setString(i+1, param[i]);
				rs = sqlca.executeQuery();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return rs;
		}

	};
	
	private String checkStr(String ps)
	{
		if ( ps == null ) return "";
		
		String s = ps.replaceAll("'", "\"");
		//System.out.println( s+"|"+ps );
		
		return s ;
	}
	

	private static ZBaseDao[] tables = {};

	protected static void setTable(ZBaseDao[] t) {
		tables = t;
	}

	private static String[] initsqls;

	protected static void setSqls(String[] sqls) {
		initsqls = sqls;
	}
}
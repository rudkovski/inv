package app;


import oracle.jdbc.pool.OracleDataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Vector;

public class BaseAPI {
	
	private ResultSet rs;
	private Connection conn;
	private Statement stmt;
	private boolean connect=false;
	private String ipport;
	private String basename;
	private String type; //ora/mysql
	
	public BaseAPI()
	{}
	
	public boolean is_connent() {return connect;}
	
	private void log(String s) {System.out.println(s);}
	
	public void setipport(String s) {ipport=s;}
	public void setbasename(String s) {basename=s;}

	public boolean DBConnect(String user,String Password)
	{
		boolean ret=false;
		try
		{
		conn=DriverManager.getConnection("jdbc:mysql://"+ipport+"/"+basename+"?user="+user+"&password="+Password);
		stmt = conn.createStatement();
		ret=true;
		type="mysql";
		}
		catch (SQLException ex)
		{
			log(ex.getMessage());
		}
		connect=ret;
		return ret;
	}

	
	/**
	 * @param pipport - ip:port (default MySQL port:3306)
	 * @param pbasename - Name start database<br>
	 * @param user - DB username<br>
	 * @param Password - DB password<br>
	 * @return true if connect
	 */
	public boolean DBConnect(String pipport,String pbasename, String user,String Password)
	{
		ipport=pipport;
		basename=pbasename;
		return DBConnect(user,Password);
	}
	
	public boolean DBConnectOra(String ipport,String user,String password,String sid)
	{
		boolean ret=false;
		try
		{
			OracleDataSource ods = new OracleDataSource();
			String cs="jdbc:oracle:thin:"+user+"/"+password+"@"+ipport+":"+sid;
			ods.setURL(cs);
			//System.out.println("CS:"+cs);
			//conn=DriverManager.getConnection("jdbc:oracle:thin:"+user+"/"+password+"@//"+ipport+":"+sid);
			conn=ods.getConnection();
			stmt=conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ret=true;
			type="ora";
		}
		catch (SQLException e)
		{
			log(e.getMessage());
		}
		return ret;
	}
	
	public void setAutoCommit(boolean ac)
	{
		try {conn.setAutoCommit(ac);} 
		catch (SQLException e) {e.printStackTrace();}
	}
	
	public void commit()
	{
		try {conn.commit();} 
		catch (SQLException e) {e.printStackTrace();}
	}
	public void addBatch(String sql)
	{
		try {stmt.addBatch(sql);} 
		catch (SQLException e) {e.printStackTrace();}
	}
	public int[] executeBatch()
	{
		try {return stmt.executeBatch();} 
		catch (SQLException e) {e.printStackTrace();}
		return null;
	}
	
	
	public ResultSet GetRS(String sql)
	{

		ResultSet ors=null;
		try
		{
		ors=stmt.executeQuery(sql);
		}
		catch (SQLException ex)
		{
			log(ex.getMessage()+" (in BaseAPI.GetRS)");
			ex.printStackTrace();
		}
		return ors;
	}
	/**
	 * 
	 * @param sql - sql statement
	 * @return v.get(row).get(col) - get value example.
	 */
	public Vector<Vector<String>> getVector(String sql)
	{
		Vector<Vector<String>> ret=new Vector<Vector<String>>();
		//int r=0;
		try
		{
		ResultSet rs=this.GetRS(sql);
		if (type.equals("mysql")) {rs.first();} else {rs.next();}
		String sadd=null;
		if (rs.getRow()!=0)
			{
			do
				{
					//System.out.println("read="+(r++));
					int c=rs.getMetaData().getColumnCount();
					Vector<String> add=new Vector<String>();
					for (int i=0;i<c;i++) {
						sadd=rs.getString(i+1);
						add.add(sadd!=null?sadd:"");
						}
					ret.add(add);
				}
			while (rs.next());
			}
		
		}
		catch (SQLException ex)
		{
			log(ex.getMessage()+" (in BaseAPI.GetVector)");
			ex.printStackTrace();
		}
	
		return ret;
	}
	/**
	 * 
	 * @param sql - sql statement
	 * @return vector with first row of result set
	 */
	public Vector<String> getSingleVector(String sql)
	{
		Vector<Vector<String>> basevec=getVector(sql);
		Vector<String> ret=new Vector<String>();
		for (int i=0;i<basevec.size();i++)
		{
		ret.add(basevec.get(i).get(0));
		}
		return ret;
	}
	
	
	public String Scalar(String sql)
	{
		String ret="";
		try
		{
		rs = stmt.executeQuery(sql);
		//System.out.println("SQL:"+sql+"\r\nFirst row:"+rs.getRow());
		if (type.equals("mysql")) {rs.first();} else {rs.next();}
		ret=rs.getString(1);
		rs.close();
		}
		catch (SQLException ex)
		{
			log(ex.getMessage()+" (in (BaseAPI.Scalar)");
			ex.printStackTrace();
		}
		return ret;
	}
	
	public boolean Command(String sql)
	{
		boolean ret=true;
		try
		{
		stmt.executeUpdate(sql);
		}
		catch (SQLException ex)
		{
			log("SQL:"+sql);
			log(ex.getMessage()+" (in BaseAPI.Command)");
			ex.printStackTrace();
			ret=false;
		}
		return ret;
		
	}
	
	public void close()
	{
		try 
		{
			conn.close();
		} 
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
}

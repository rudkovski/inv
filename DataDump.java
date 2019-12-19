package app;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DataDump {
	private BaseAPI mysql;
	private BaseAPI oracle;
	//private SimpleListener endload;
	private int total=0;
	private int load_cnt=0;
	private boolean running=false;
	private boolean start=false;
	public DataDump(BaseAPI pmysqlBase,BaseAPI poraBase) // модификатор public, метод DataDump, параметры pmysqlBase & poraBase
	{
		mysql=pmysqlBase;
		oracle=poraBase;
		System.out.println("DataDump mysql:"+mysql+" oracle:"+oracle);
		System.out.println("DataDump pmysqlBase:"+pmysqlBase+" poraBase:"+poraBase);
	}
	//full dump
	/*private void dumpitems()
	{
		//System.out.println("Dump items start");
		//mysql.Command("update dbvalues set value=date(now()) where idval=1");
		
		mysql.Command("Delete from items");
		//Vector<Vector<String>> oratab=oraBase.getVector("select Itemid,BibId,Barcode from ITEMDETL2");
		total=Integer.valueOf(oracle.Scalar("select count(*) from ITEMDETL2"));
		
		ResultSet rs=oracle.GetRS("select ITEMDETL2.Itemid,BibId,Barcode,Patron_id,Itemdetl2.location " +
				"from ITEMDETL2 left join CIRCDETL on ITEMDETL2.Itemid=CIRCDETL.Itemid");
		//int c=oratab.size();
		//total=c;
		String s[]=new String[5];
		String g=null;
		//for (int i=0;i<c;i++)
		int i=0;
		try
		{
		while (rs.next()) 
			{
				load_cnt=i++;
				//load_cnt=i;
				//System.out.println("i="+i);
				for (int j=0;j<=4;j++) {
					//s[j]="'"+oratab.get(i).get(j).trim()+"'";
					
					g=rs.getString(j+1);
					s[j]="'"+(g==null?"":g.trim())+"'";
					//oratab.get(i).set(j, null); //gc
					}
				s[3]=s[3].equals("''")?"0":"1";				
				//System.out.println("s[3]="+s[3]);
				mysql.Command("insert into items (ItemId,IdVtls,BarCode,issued,Location) values ("+s[0]+","+s[1]+","+s[2]+","+s[3]+","+s[4]+")");
				//oratab.set(i, null); //gc
			}
		//mysql.Command("Update items,books set c852h=value where "+ 
	//"items.Idvtls=books.idvtls and field=852 and Findex=1 and Subfield='h'");
		mysql.Command("Call PostUpdateItems()");
		}
		catch (SQLException e)
		{
			mysql.Command("update dbvalues set value='Update error' where idval=1");
			e.printStackTrace();
		}
		s=null; //gc
		//oratab=null;
		try
		{
		
		rs.close();
		oracle.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		//mysqlBase.Command("update dbvalues set value=date(now()) where idval=2");
		mysql.Command("update dbvalues set value=date(now()) where idval=1");
		
		//if (endload!=null) {endload.actionPerformed();}
	}*/
	
	public void dumpitems()
	{
		//rec_add=0;
		System.out.println(" Вошли в DataDump dumpitems:");
		total = 0;
		load_cnt = 0;
		int LastId = 0;
		try
		{
			LastId=Integer.valueOf(mysql.Scalar("Select max(ItemId) from vtls.items"));
		}
		catch (Exception e) {}
		// Обьединение таблицы items из mySQL НЕ работает
		//  "select count(*) from ITEMDETL2 left join CIRCDETL on ITEMDETL2.Itemid=CIRCDETL.Itemid " этот запрос выфдаетвсе
		System.out.println("Migrate start:"+System.currentTimeMillis());
		total = Integer.valueOf(oracle.Scalar("select count(*) from ITEMDETL2 left join CIRCDETL on ITEMDETL2.Itemid=CIRCDETL.Itemid where ITEMDETL2.ItemId>"+LastId));
		ResultSet source=oracle.GetRS("select ITEMDETL2.Itemid,BibId,Barcode,Patron_id,Itemdetl2.location " +
				"from ITEMDETL2 left join CIRCDETL on ITEMDETL2.Itemid=CIRCDETL.Itemid where ITEMDETL2.ItemId>"+ LastId 
				+"order by ItemId");
		try
		{
			mysql.setAutoCommit(false);
 			while (source.next()) myadd(source);
			mysql.executeBatch();
			mysql.commit();
			mysql.setAutoCommit(true);
			System.out.println("Migrate done."+System.currentTimeMillis()+" count:"+total);
			mysql.Command("Call PostUpdateItems()");
		}
		catch (SQLException e) {System.out.println(e.getMessage());}
		//return rec_add;
	}
	
	public void myadd(ResultSet rec) throws SQLException
	{
		//rec_add++;
		//System.out.println(" Вошли в DataDump myadd:");
		load_cnt++;
		String g="";
		String[] s=new String[5];
		for (int j=0;j<5;j++) {
			g=rec.getString(j+1);
			s[j]="'"+(g==null?"":g.trim())+"'";
			}
		s[3]=s[3].equals("''")?"0":"1";
		String ins_str = "insert into items2 (ItemId,IdVtls,BarCode,issued,Location) values ("+s[0]+","+s[1]+","+s[2]+","+s[3]+","+s[4]+")";
		mysql.addBatch(ins_str);
		//System.out.println(ins_str);
		//System.out.println("INS:"+s[2]);
		//System.out.println("Выход из DataDump запись недостающих элементов в items2:");
		// запись недостающих экземпляров в таблицу items2
	}
	
	public void statupdump(boolean checkdate)
	{
		start=true; running=true;
		System.out.println(" Вошли в DataDump statupdump:");
		System.out.println("checkdate:"+checkdate);
	
		//String now=mysqlBase.Scalar("Select date(now())");
		//String lastupd=mysqlBase.Scalar("Select value from dbvalues where IdVal=1");
		//System.out.print("dumpdata:");
		//System.out.println(!now.equals(lastupd)||!checkdate);
		//if (!now.equals(lastupd)||!checkdate) {dumpitems();}
		dumpitems();
		running=false;
		//System.gc();
	}
	
	public int get_total() {return total;}
	public int get_cur() {return load_cnt;}
	
	//public void add_endlistener(SimpleListener s) {endload=s;}
	//public void remove_endlistener() {endload=null;}
	public boolean is_running() {return running;}
	public boolean is_start() {return start;}

}

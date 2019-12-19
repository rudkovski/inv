package crutches;
import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JTable;


@SuppressWarnings("serial")
public class EDTM extends DefaultTableModel {
	public int ColCount;
	public boolean ReadOnly;
	private Vector<String> keyindex;
	
	public EDTM()
	{
	ReadOnly=false;
	keyindex=new Vector<String>();
	}
	
	public static int GetCurIndex(JTable tab)
	{
		int row=-1;
		int[] selectedRows = tab.getSelectedRows();
        if (selectedRows.length!=0) {row = selectedRows[0];}
        return row;
	}
	
	
	
	public void SetColCount(ResultSet rs){
		try
		{
		this.ColCount=rs.getMetaData().getColumnCount();
		}
		catch (SQLException ex)
		{
		System.out.println("SQLException: " + ex.getMessage()+"(In EDTM SetColCount)");	
		}
	}
	
	public void Clear(){
		keyindex.clear();
		int c=this.getRowCount();
		int i;
		for (i=1;i<=c;i++)
		{
			this.removeRow(0);
		}
	}
	
	public boolean isCellEditable(int row, int column) {
        return !ReadOnly;
    }
	
	public void FillData(ResultSet rs){
		try
		{
			int i;
			this.Clear();
			//if (ColCount==0) {SetColCount(rs);}
			SetColCount(rs);
			String s[] = new String[ColCount]; 
			rs.first();
			int c=rs.getRow();
			//System.out.println(c);
						
			if (c!=0)
			{
				do
				{
					for (i=1;i<=ColCount;i++) {s[i-1]=rs.getString(i);}
					keyindex.add(rs.getString(0));
					this.addRow(s);
				}
				while (rs.next());
			}
		}
		catch (SQLException ex)
		{
		System.out.println("SQLException: " + ex.getMessage()+"(In EDTM FillData)");	
		}
	}
	
	public void FillData(Vector<Vector<String>> v)
	{
	this.Clear();
	
	for (int i=0;i<v.size();i++)
		{
		this.addRow(v.get(i));
		keyindex.add(v.get(i).get(0));
		}
	}
	
	public void FillSingleVector(Vector<String> v)
	{
		this.Clear();
		int c=v.size();
		String[] s=new String[1];
		for (int i=0;i<c;i++)
			{
			s[0]=v.get(i);
			this.addRow(s);
			keyindex.add(i+"");
			}
	}
	
	public String getKeyIndex(int i)
	{
		return keyindex.get(i);
	}

}

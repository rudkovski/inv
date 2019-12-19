package gui;



import javax.swing.JComboBox;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;





@SuppressWarnings("rawtypes")
public class EJComboBox extends JComboBox{
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	private Vector<String> index;
	public boolean lock=false;
	
	public String getindex()
	{
		int ind=this.getSelectedIndex();
		String ret="-1";
		if (index.size()>=ind+1 && ind>=0) {ret=index.get(ind);}
	//	System.out.println("Get index:"+Integer.toString(index.size())+" Ret:"+ret);
		return ret;
	}
	

	
	public EJComboBox()
	{
		index = new Vector<String>();
	}
	
	public void SelectByIndex(String key)
	{
		int i=index.lastIndexOf(key);
		this.setSelectedIndex(i);
	}
	
		
	@SuppressWarnings("unchecked")
	public void fillcb(ResultSet rs)
	{
		this.lock=true;
		
		try
		{
			this.removeAllItems();
			this.index.clear();
						
			
			
			String s; 
			rs.first();
			int cols=rs.getMetaData().getColumnCount();
			if (cols==2) {
				int c=rs.getRow();
				
							
				if (c!=0)
				{
					do
					{
						s=rs.getString(2);
						this.addItem(s);
						s=rs.getString(1);
						this.index.add(s);
						
						//System.out.println(s); 
					}
					while (rs.next());
				}
			}
			else
			{
				System.out.println("EJBomboBox bad resuls set. Colcount!=2");
				
			}
			this.lock=false;
		}
		catch (SQLException ex)
		{
		System.out.println("SQLException: " + ex.getMessage()+"(In EJComboBox FillData)");	
		}
	}
}

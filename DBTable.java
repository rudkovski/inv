package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;

import app.BaseAPI;

import crutches.IdListener;
import crutches.EDTM;



public class DBTable extends JPanel {
	private static final long serialVersionUID = 1L;
	private EDTM tm;
	private JTable tab;
	private IdListener rowchange=null;
	private LeftCellRenderer LeftRenderer;
	private JScrollPane scrollPane;
	private String sql;
	private BaseAPI base; 
	
	public DBTable()
	{
		DBTableInit(100,100,"");
	}
	
	public DBTable(BaseAPI cbase,int cwidth,int cheigth,String title)
	{
		base=cbase;
		DBTableInit(cwidth,cheigth,title);
	}
	
	private void DBTableInit(int cwidth,int cheigth,String title)
	{
		LeftRenderer=new LeftCellRenderer();
		setLayout(null);
		JLabel lCirc = new JLabel(title);
		lCirc.setBounds(0, 0, title.length()*10, 16);
		add(lCirc);
		
		scrollPane = new JScrollPane();
		setTabSize(cwidth, cheigth);
		add(scrollPane);
		tm = new EDTM();
		tab = new JTable(tm);
		tab.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		tab.getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) {
				int x=arg0.getX();
				int y=arg0.getY();
				int c=tab.getTableHeader().columnAtPoint(new Point(x,y));
				//System.out.println("Click head col "+c);
				setSort(c);
			}
		});
		
		
		scrollPane.setViewportView(tab);
		tm.ReadOnly=true;
		
		tab.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		tab.getSelectionModel().addListSelectionListener(new ListSelectionListener() 
		{
			public void valueChanged(ListSelectionEvent arg0) {
				if (tab.getSelectedRowCount()!=0&&rowchange!=null)
				{
					rowchange.actionPerformed(EDTM.GetCurIndex(tab)+"");
				}
			}
		});
	}
	
	public void setSort(int c)
	{
		if (LeftRenderer.sort!=c) 
			LeftRenderer.sort=c;
			else
			LeftRenderer.decs=!LeftRenderer.decs;
		boolean decs=LeftRenderer.decs;
		
		if (sql!=null) refresh(sql+" order by f"+c+(decs?" desc":""));
	}
	
	public void setSql(String s) {sql=s;}
	
	public void refresh() {refresh(sql);}
	
	public void refresh(String sql)
	{
		this.FillData(base.getVector(sql));
	}
	
	
	public void addRowChangeListener(IdListener a)
	{
		rowchange=a;
	}
	
	public int GetCurIndex()
	{
		return EDTM.GetCurIndex(tab);		
	}
	
	public void FillData(Vector<Vector<String>> v)
	{
		tm.FillData(v);
	}
	
	public void addColumn(String colname)
	{
		tm.addColumn(colname);
	}
	
	
	
	public void setColMaxWidth(int col,int w)
	{
		tab.getColumnModel().getColumn(col).setMaxWidth(w);
	}
	public void setColMinWidth(int col,int w)
	{
		tab.getColumnModel().getColumn(col).setMinWidth(w);
	}
	
	public void setLeftHeaderAligment(int col)
	{
		//tab.getColumn(0).setHeaderRenderer(LeftRenderer);
		tab.getColumn(tab.getColumnName(col)).setHeaderRenderer(LeftRenderer);
	}
	
	public void setTabSize(int cwidth,int cheigth)
	{
		scrollPane.setBounds(0, 20, cwidth, cheigth);
	}
	
	public void removeColumn(int col)
	{
		tab.removeColumn(tab.getColumnModel().getColumn(col));
	}
	public Object getValueAt(int row, int col)
	{
		return tab.getValueAt(row, col);
	}
	public String getKey()
	{
		String ret=null;
		int i=GetCurIndex();
		if (i!=-1) {ret=tm.getKeyIndex(i);}
		return ret;
		
	}
	public EDTM getEDTM()
	{
		return tm;
	}
}

class LeftCellRenderer implements TableCellRenderer {
	protected int sort=0;
	protected boolean decs=false;
	private Color green=new Color(100,255,100);
	private Color red=new Color(255,100,100);
	private Color form=new Color(238,238,238);
	private Vector<JLabel> labels=new Vector<JLabel>();
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (labels.size()-1<column) 
        	{
        	JLabel ln=new JLabel();
        	ln.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
            ln.setHorizontalAlignment(JLabel.LEFT);
        	labels.add(ln);
        	}
        
        
    	JLabel l = labels.get(column);
    	//System.out.println("Render working "+l.getBackground().getRed()+";"+l.getBackground().getGreen()+";"+l.getBackground().getBlue());
        
        if (value != null) {
            l.setText(value.toString());
        }
        if (column==sort) l.setBackground(decs?red:green);
        	else
        	l.setBackground(form);
        
        return l; 
    }
}

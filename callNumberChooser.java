package gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import java.util.Collections;
//import java.util.regex.Pattern;
//import java.util.regex.Matcher;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//import java.util.HashSet;
import java.lang.String;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import crutches.EDTM;

import app.BaseAPI;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class callNumberChooser extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField mask;
	private JTable cnTab; //callnumbers source list
	private EDTM tmcnTab;
	private JTable selTab; //selected callnumbers
	private EDTM tmselTab;
	private Vector<String> callNum; //store unique callnumbers
	private Vector<String> numRes; //search result
	private Vector<String> sel; //selected callnumbers
		
	public callNumberChooser(BaseAPI base)
	{
		sel=new Vector<String>();   //?????  >
		//<for editor>
		System.out.println(" Вошли в callNumberChooser:");
		if (base!=null)
		//</for editor>
		callNum=base.getSingleVector("select distinct call_number from dbadmin.bibliographic_fields");
		Vector <String> twoDistinct = new Vector <String>(callNum.size()); 
		twoDistinct.addAll(callNum);
		String[] two = new String[2];
		String[] answer = new String[twoDistinct.size()];//    ????????????????????????????????????
		//String contrNum = new String("Ч73");
		for (int i = 0; i < answer.length; i++) {
        //System.out.println("i: "+i);
        answer[i] = twoDistinct.elementAt(i);
        //Pattern pattern = Pattern.compile("(.*)(\\s)(.*)(\\d+)"); // если значение два текстовых слова разделенных одним пробелом, иначе не брать в обработку
        //Matcher matcher = pattern.matcher(answer[i]);
        //boolean result = matcher.matches();
        //if (result == true) {
        two = answer[i].split(" "); // взять только первое значение- 852 \h - расстановочный шифр, 852 \j не брать
        answer[i]=two[0];
        //}
        }
		for (int q = 0; q < answer.length; q++) {
			//Pattern pattern = Pattern.compile("Ч73 Б 594"); // если значение два текстовых слова разделенных одним пробелом, иначе не брать в обработку
	        //Matcher matcher = pattern.matcher(answer[q]);
	        //boolean result = matcher.matches();
	        //if (result == true) {
	        //	System.out.println(" q:"+q+"=СРАВНИ answer:"+answer[q]+"==");	
	        //}
		}
        callNum.clear();
        callNum.add(answer[0]);
        int index_ch73=0;
        int index_uniq =0;
        String priznak_uniq = "1";
        String stroka = new String("11111");
        for (int k = 1; k < answer.length; k++) {
        	//if (k == 140) {break;}
        	stroka=answer[k];
        	priznak_uniq = "0";
        	for (int j = 0; j <= index_uniq; j++) {
        		priznak_uniq=(callNum.elementAt(j).equals(stroka)?"0":"1");
        		if (priznak_uniq.equals("0")) {break; }	
        	}
        	if (priznak_uniq.equals("1")) { 
        		callNum.add(stroka);
        		//System.out.println("           Встретился уникальный элемент  callNum index_uniq:"+index_uniq+" callNum:"+callNum.elementAt(index_uniq));
        		//System.out.println("           Все элементы  callNum:"+callNum);
        		index_uniq++;
        	}
        	
        }
        Collections.sort(callNum);
/*
        List listDistinct = new ArrayList();
        for (int i = 0; i < answer.length; i++) {    // 
        	listDistinct.add(answer[i]);
        }
        //System.out.println(":"+listDistinct);
		Set set = new HashSet();
        set.addAll(listDistinct);
        //System.out.println("Количество элементов set:"+set.size()+"Количество элементов listDistinct:"+listDistinct.size());
        //System.out.println("Элементы set:"+set);
        callNum.clear(); // очистить вектор с дубликатами
        for (int i = 0; i < set.size(); i++) {    // 
        	String element=(String) listDistinct.get(i);
        	callNum.insertElementAt(element,i);
        }
        
        
        */
        
		setLayout(null);
		JLabel lmask=new JLabel("Раст.шифр");
		lmask.setBounds(10, 10, 80, 20);
		add(lmask);
		
		mask=new JTextField();
		mask.setBounds(100, 10, 120, 20);
		mask.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent arg0) {
				int key=arg0.getKeyCode();
				if (key==10) {search(mask.getText());}
			}
		});
		add(mask);
		
		
		JScrollPane spcn=new JScrollPane();
		spcn.setBounds(10,30,150,250);
		
		tmcnTab=new EDTM();
		tmcnTab.ColCount=1;
		tmcnTab.ReadOnly=true;
		String[] ctitles={"Шифр"};
		tmcnTab.setColumnIdentifiers(ctitles);
		cnTab=new JTable(tmcnTab);
		cnTab.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		cnTab.addMouseListener(new MouseAdapter(){
				public void mouseReleased(MouseEvent arg0) {
					int sel=EDTM.GetCurIndex(cnTab);
					if (sel!=-1) {seladd((String)cnTab.getValueAt(sel, 0));}
				}
		});
		
		spcn.setViewportView(cnTab);
		add(spcn);
		
		JScrollPane sps=new JScrollPane();
		sps.setBounds(160, 30, 150, 250);
		
		tmselTab=new EDTM();
		tmselTab.ColCount=1;
		tmselTab.ReadOnly=true;
		String[] ctitles2={"Проверять:"};
		tmselTab.setColumnIdentifiers(ctitles2);
		selTab=new JTable(tmselTab);
		selTab.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		selTab.addMouseListener(new MouseAdapter(){
				public void mouseReleased(MouseEvent arg0)
				{
					int sel=EDTM.GetCurIndex(selTab);
					if (sel!=-1) {selremove(sel);}
				}
		});
		sps.setViewportView(selTab);
		add(sps);
		
		JButton btn_AddAll = new JButton("Добавить всё");
		btn_AddAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addAll();
			}
		});
		btn_AddAll.setBounds(10, 283, 150, 25);
		add(btn_AddAll);
		
		JButton btn_RemoveAll = new JButton("Убрать всё");
		btn_RemoveAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				remAll();
			}
		});
		btn_RemoveAll.setBounds(160, 283, 150, 25);
		add(btn_RemoveAll);
		
		JButton btn_search = new JButton("Поиск");
		btn_search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				search(mask.getText());
			}
		});
		btn_search.setBounds(222, 10, 88, 20);
		add(btn_search);
		System.out.println(" Вышли из  callNumberChooser:");
	}
	
	private void search(String s)
	{
		//System.out.println("Search "+callNum.size());
		numRes=getCallNumbersList(s);
		refResTab();
	}
	
	/**
	 *
	 * @param mask 
	 * @return Filtered vector callNum Empty vector if no mathes
	 */
	
	private Vector<String> getCallNumbersList(String mask)
	{
		Vector<String> ret=new Vector<String>();
		int c=callNum.size();
		int ml=mask.length();
		String s;
		String v;
		for (int i=0;i<c;i++)
			{
			v=callNum.get(i);
			s=(ml<=v.length()?v.substring(0, ml):v);
			if (s.equals(mask)) {ret.add(v);}
			}
		return ret;
	}
	
	private void addAll() 
	{
		for (int i = 0 ; i < numRes.size(); i++ ) {
			seladd(numRes.get(i));
		}
		refSelTab();
	}
	
	private void remAll()
	{
		sel.clear();
		refSelTab();
	}
	
	private void refResTab()
	{
		tmcnTab.FillSingleVector(numRes);
	}
	
	private void refSelTab()
	{
		tmselTab.FillSingleVector(sel);
	}
	
	private void seladd(String s)
	{
		if (sel.indexOf(s)==-1) 
			{
			sel.add(s);
			refSelTab();
			}
	}
	
	private void selremove(int s)
	{
		sel.remove(s);
		refSelTab();
	}
	
	public Vector<String> getSelectedCallnumbers()
	{
		return sel;
	}
}

package gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.filechooser.FileFilter;

import crutches.EDTM;
import crutches.EFileFilter;

import app.BaseAPI;
import app.CLinks;
import app.DataDump;

public class MainPan extends JPanel {

	private static final long serialVersionUID = 1L;
	private BaseAPI base;
	private BaseAPI oracle;
	private DataDump dd;
	private JLabel load_ind;
	private Timer load;
	private int c_old=0;
	private JFileChooser fopen;
	private JFileChooser fsave;
	private DBTable tabReport;
	private callNumberChooser cnChooser;
	private EJComboBox cbLocation;
	private JButton b_open;
	private String list_name = "";
	
	
	public MainPan(CLinks links)
	{
		System.out.println(" Вошли в MainPan:");
		base=links.base;
		oracle=links.orabase;
		//orabase=links.orabase;
		dd=links.dd;

		fopen=new JFileChooser();   //  Настроить и вывести на экран диалоговое окно для открытия файла 
		FileFilter fil=new EFileFilter("Text","txt"); // переопределить класс FileFilter назначает фильтрацию на основе типов файлов:
		                                              //fileChooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));
		fopen.setFileFilter(fil);   //Сначала примените фильтр, а затем отобразите диалог
		fopen.setDialogTitle("Открыть");  //
		
		fsave=new JFileChooser();
		fsave.setDialogType(JFileChooser.SAVE_DIALOG);
		fsave.setFileFilter(fil);
		fsave.setDialogTitle("Сохранить отчёт");
		
		Font defFont=new Font("Arial", 0 , 12);
		setFont(defFont); 
		setLayout(null);
		// запрос на все call number во всех записях, поиск и выбор необходимых call number 
        //	запрос к MySQL cnChooser=new callNumberChooser(base);	меняем на запрос cnChooser=new callNumberChooser(oracle);  
		cnChooser=new callNumberChooser(oracle);
		//System.out.println("MainPan обращение к callNumberChooser cnChooser:"+cnChooser);
		cnChooser.setBounds(0,0,350,318);
		add(cnChooser); // Добавляет указанный компонент в конец этого контейнера. 
		System.out.println("MainPan обращение к callNumberChooser cnChooser:"+cnChooser);		
		b_open=new JButton("Открыть список");
		b_open.addActionListener(new ActionListener() { // связывание обработчика с кнопкой Открыть список 
			public void actionPerformed(ActionEvent arg0) {
				fileopen();  // код который выполниться при нажатии кнопки Открыть список
			}
		});
		b_open.setBounds(358, 81, 169, 20);
		add(b_open); // Добавляет указанный компонент в конец этого контейнера.
		
		
		load_ind=new JLabel();
		load_ind.setBounds(358, 12, 300, 20);  //определения ограничивающего прямоугольника компонента
                                               //(x, y, (x, y, width, height), height)  x,y координаты верхнего левого угла, ширина и выста  		
		                                       // Добавляет указанный компонент в конец этого контейнера.
		add(load_ind);                          
		
		tabReport = new DBTable(base,800,300,"Отчёт");
		tabReport.setBounds(12, 330, 810, 330);
		add(tabReport);         // Добавляет указанный компонент в конец этого контейнера.
		String[] tabcol=new String[] {"Баркод","Состояние","На месте?","Шифр","Авт.код","Местоположение","Название"};
		int[] colwidth=new int[] {150,80,100,80,80,200,2000};
		for (int i=0;i<tabcol.length;i++) {tabReport.addColumn(tabcol[i]);}
		for (int i=0;i<colwidth.length;i++) 
			{
			tabReport.setColMinWidth(i, colwidth[i]);
			tabReport.setLeftHeaderAligment(i);
			}
		
		
		
		cbLocation = new EJComboBox();
		cbLocation.setBounds(358, 57, 338, 20);
		add(cbLocation);
		cbLocation.fillcb(base.GetRS("Select Location_Id,Name from location"));
		
		JLabel label = new JLabel("Местоположение");  
		label.setBounds(358, 44, 340, 15);
		add(label);  // Добавляет указанный компонент в конец этого контейнера.
		
		JButton b_save = new JButton("Сохранить");
		b_save.addActionListener(new ActionListener() {  // создание слушателя события
			public void actionPerformed(ActionEvent e) { // обработчик события  
				save_report();
			}
		});
		b_save.setBounds(358, 107, 169, 20);
		add(b_save);
		
		load = new Timer(500,new ActionListener() { // отложенный запуск потока будет вызываться каждые посекунды
			@Override   // переопределить методы родительского класса,
			public void actionPerformed(ActionEvent arg0) {
				load_work();
			}
		});
		load.start();
		System.out.println(" Вышли из MainPan :");
	}
	
	public void resize(int w,int h)
	{
		tabReport.setBounds(10, 312, w-20, 320);
		tabReport.setTabSize(w-30, 300);
	}
	
	public void load_work()
	{
		if (dd!=null)
			{
			if (dd.is_start()) 
				{
				load_ind.setText(
						"Синхронизация:"+dd.get_cur()+"/"+dd.get_total()+" "+((dd.get_cur()-c_old)*2)+"rec/s");
				b_open.setEnabled(false);
				c_old=dd.get_cur();
				}
			if (!dd.is_running()&&dd.is_start()) 
				{
				load_ind.setText(""); 
				b_open.setEnabled(true);
				load.removeActionListener(load.getActionListeners()[0]);
				load.stop();
				}
			}
	}
	
	public void fileopen()
	{
		load_ind.setText("Создаётся отчёт");
		load_ind.repaint();
		FileReader f;
		BufferedReader br;
				
		if (fopen.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)  // APPROVE_OPTION - выбор файла в диалоговом окне прошел успешно; выбранный файл можно получить методом getFile();
		{
			try 
			{
				list_name = fopen.getSelectedFile().getAbsolutePath();
				f = new FileReader(list_name);
				br = new BufferedReader(f);
				String l,ins,issued;
				int i;
				int icount=0;
				base.Command("Call makereport()");
				while ((l=br.readLine())!=null)
				{
				       // Парсинг файла со сканера для выделения баркода 
					    i=l.indexOf("\t");
				        //System.out.println("i:"+i);
				        //System.out.println("l:"+l);
				        if (i!=-1)
						{
						ins=l.substring(i+1);
						ins=ins.trim();
						//System.out.println("IIIInsert:"+ins);
						}
					else
						{
						ins=l;
						}
					if (ins.length()<30) 
						{
						
						//System.out.println("icount:"+(icount++));
						// Экземпляры из полученного списка проверяются на условия выдан на руки - не выдан на руки(на полке)
						// Если экземпляр выдан на руки,IDITEM присутствует в CIRCDETL issued равно 1, если должен быть на полке issued равно 0						
						issued=oracle.Scalar("select count(*) from CIRCDETL,ITEMDETL2 where CIRCDETL.ITEMID=ITEMDETL2.ITEMID and ITEMDETL2.BARCODE='"+ins+"'");
						issued=issued.equals("0")?"0":"1";
						System.out.println("ins.length:"+ins.length());
						System.out.println("ISSUED:"+issued);
						//System.out.println("If ins.length()<30 Insert:"+ins);
						base.Command("Update items set issued="+issued+" where BarCode='"+ins+"'");
						base.Command("Insert into barcodes (barcode) values ('"+ins+"')");
						}
				}
				br.close();
				f.close();
				reportBuild();
				reportRef();
				
			} 
			catch (Exception e) {
				e.printStackTrace();  // диагностика исключений
			}
			
		}
		
		// new
		
		// new
		load_ind.setText("");
	}
	
	private void reportBuild()
	{
		Vector<String> v=cnChooser.getSelectedCallnumbers();
		int c=v.size();
		for (int i=0;i<c;i++)
			{
			base.Command("Insert into callnumbers (callnumber) values ('"+v.get(i)+"')");
			}
		
		base.Command("call updateReport("+cbLocation.getindex()+")");
	}
	
	private void reportRef()
	{
		String locindex=cbLocation.getindex();
		System.out.println("Locindex="+locindex);
		String sql="Select " +
				"barcode as f0," +
				"if (issued=1,'На руках',if (lost=1,'Утеряна','Сдана')) as f1," +
				"concat(if (id_loc<>"+locindex+",'Другая ауд.',if (lost=0 and issued=0,'На месте','')),if(same_cn=0,';другая полка','')) as f2," +
				"calln1 as f3," +
				"calln2 as f4," +
				"location.name as f5," +
				"report.name as f6 " +
				"from report,location where id_loc=Location_id";
		tabReport.setSql(sql);
		tabReport.refresh();
		
	}
	
	private void save_report()	
	{
		FileWriter out=null;
		if (!list_name.equals(""))
			 {
			try
				{
				out=new FileWriter(list_name+"_report.txt");
				EDTM tm=tabReport.getEDTM();
				int c=tm.getRowCount();
				if (c!=0)
					{
					@SuppressWarnings("unchecked")
					Vector<Vector<String>> v=tm.getDataVector();
					int r=v.get(0).size();
					
					for (int i=0;i<c;i++)
						{
							for (int j=0;j<r;j++)
							{
								out.write(v.get(i).get(j));
								if (j<r-1) {out.write("\t");}
							}
							out.write("\r\n");
								
						}
					}
				}
			catch (Exception e) {e.printStackTrace();}
			if (out!=null) {try {out.close();} catch (IOException e) {}}
			}
	}
}

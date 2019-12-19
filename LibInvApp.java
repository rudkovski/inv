package app;

import gui.MainFrame;


public class LibInvApp {
	private DataDump dd;
	
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		
		try
		{
			LibInvApp app=new LibInvApp();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	@SuppressWarnings("unused")
	public LibInvApp()
	{
		CLinks links=new CLinks();
		
		BaseAPI base=new BaseAPI();
		BaseAPI base_back=new BaseAPI();
		
		base.DBConnect("193.233.152.92:3306","vtls","java","kJ3fa9ps!");
		base_back.DBConnect("193.233.152.92:3306","vtls","java","kJ3fa9ps!");
		
		
		//BaseAPI orabase=new BaseAPI();
		BaseAPI orabase_back=new BaseAPI();
		//boolean oracon=orabase.DBConnectOra("libora.kuzstu.ru:1521", "syscli", "ZyZcli!", "vtls01");
		boolean oracon_back=orabase_back.DBConnectOra("193.233.152.34:1521", "syscli", "RyScL2i", "vtls01");
		//if (oracon&&oracon_back) {System.out.println("ORA CONNECTED!");} 
		
		dd=new DataDump(base_back,orabase_back);
		System.out.println("LibInvApp public LibInvApp dd:"+dd+" base_back:"+base_back+" orabase_back:"+orabase_back);
		
		Thread load=new Thread(new Runnable()  {
			public void run() {
				dd.statupdump(true);
				}
		});
		System.out.println("Имя потока:"+load.getName());
		System.out.println("Активность  потока:"+load.isInterrupted());
		links.dd=dd;
		links.base=base;
		links.orabase=orabase_back;
		
		load.start();
		System.out.println("Активность  потока load:"+load.isInterrupted());
		System.out.println("base:"+base+" orabase_back"+orabase_back);
		System.out.println("links.dd:"+links.dd+" links.base:"+links.base+" links.orabase:"+links.orabase);
	
		MainFrame frame=new MainFrame(links);
	
	}
	
	
	
	

}
 
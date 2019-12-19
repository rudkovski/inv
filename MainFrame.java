package gui;

import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;

import app.CLinks;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;


public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private final static int FRAME_WIDHT=900;
	private final static int FRAME_HEIGHT=700;
	private MainPan mainPan;

	public MainFrame(CLinks links)
	{
		DisplayMode dev=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
		int h=dev.getHeight();
		int w=dev.getWidth();
		
		int x=w/2-(FRAME_WIDHT/2);
		int y=h/2-(FRAME_HEIGHT/2);
		System.out.println(" Вошли в MainFrame:");
		setTitle("Инвентаризация изданий НТБ");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainPan = new MainPan(links);
		mainPan.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				resize();
			}
		});
		setContentPane(mainPan);
		setBounds(x,y,FRAME_WIDHT,FRAME_HEIGHT);
		setVisible(true);
		System.out.println(" ВЫШЛИ из MainFrame:");
	}
	
	private void resize()
	{
		mainPan.resize(this.getWidth(), this.getHeight());
	}
}

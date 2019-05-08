package project;

import javax.swing.*;

public class MAIN 
{
	private static FrameBuilder frame=new FrameBuilder();
	private static JFrame LoginFrame=frame.GetLoginFrame();
	public static PanelBuilder Panels=new PanelBuilder();
	
	public static void main(String[] args) {
		Panels.focusinit();
	}
	
	protected void Successful()
	{
		LoginFrame.dispose();
		 GUI gui = new GUI();
	        gui.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		System.out.println("Logged-in");
	}
	
}


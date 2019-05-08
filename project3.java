package project;

import javax.swing.*;

public class project3 {
	private static FrameBuilder frame=new FrameBuilder();
	private static JFrame LoginFrame=frame.GetLoginFrame();
	public static PanelBuilder Panels=new PanelBuilder();
	

    public static void main(String[] args){

        GUI gui = new GUI();
        gui.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        
        
    	
    		Panels.focusinit();
    	
    }
    
    protected void Successful()
	{
		LoginFrame.dispose();
		System.out.println("Logged-in");
	}
}
    	

package main.manager;

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JTextArea;

/**
 * A workaround to ensure the background color of a JTextArea.  Swing's JTextArea doesn't normally paint the background correctly.
 * It ignores your setting and paints it the default white.  This class overrides the paintComponent() method.
 * 
 * @author John McCullock
 * @version 1.0 2017-08-20
 */
@SuppressWarnings("serial")
public class BackgroundAugmentedTextArea extends JTextArea
{
	public BackgroundAugmentedTextArea() { return; }
	
	@Override
	public void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g.create();
		/*
		 * Fill the background with the color we want.
		 */
		g2d.setColor(this.getBackground());
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
		/*
		 * Note that we're not using super.paintComponent() this time.
		 */
		this.getUI().paint(g2d, this);
		g2d.dispose();
		return;
	}
}

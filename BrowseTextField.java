package main.tcservice;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 * A composite component with a JTextField and JButton positioned next to each other.
 * 
 * @author John McCullock
 * @version 1.0 2016-02-19
 */
@SuppressWarnings("serial")
public class BrowseTextField extends JComponent
{
	private final String DEFAULT_BUTTON_CAPTION = "...";
	private final String DEFAULT_BUTTON_TOOLTIP = "Browse...";
	private final Insets DEFAULT_BUTTON_INSETS = new Insets(0, 0, 0, 0);

	private JTextField mTextField = null;
	private JButton mBrowseButton = null;
	
	public BrowseTextField()
	{
		this.initializeMain();
		return;
	}
	
	private void initializeMain()
	{
		this.setLayout(new BrowseTextField.BrowseComponentLayout());
		this.add(this.mTextField = new JTextField());
		this.add(this.mBrowseButton = this.initializeButton());
		return;
	}
	
	private JButton initializeButton()
	{
		this.mBrowseButton = new JButton(DEFAULT_BUTTON_CAPTION);
		this.mBrowseButton.setMargin(DEFAULT_BUTTON_INSETS);
		this.mBrowseButton.setToolTipText(DEFAULT_BUTTON_TOOLTIP);
		return this.mBrowseButton;
	}
	
	public JTextField getTextField()
	{
		return this.mTextField;
	}
	
	public JButton getBrowseButton()
	{
		return this.mBrowseButton;
	}
	
	/**
	 * Remember that the last component added is considered the Browse component.
	 * @author John McCullock
	 * @version 1.0 2016-02-19
	 */
	private class BrowseComponentLayout implements LayoutManager
	{
		private int mMinWidth = 0;
		private int mMinHeight = 0;
		private int mPreferredWidth = 0;
		private int mPreferredHeight = 0;
		private boolean mSizeUnknown = true;
		
		public BrowseComponentLayout() { return; }
		
		public void addLayoutComponent(String name, Component comp) { return; }
		public void removeLayoutComponent(Component comp) { return; }
		
		private void setSizes(Container parent)
		{
			int count = parent.getComponentCount();
			this.mPreferredWidth = 0;
			this.mPreferredHeight = 0;
			this.mMinWidth = 0;
			this.mMinHeight = 0;
			
			for(int i = 0; i < count; i++)
			{
				Component c = parent.getComponent(i);
				if(!c.isVisible()){
					continue;
				}
				if(i == 0){
					this.mPreferredWidth = c.getPreferredSize().width + this.mPreferredHeight;
					this.mPreferredHeight = c.getPreferredSize().height;
					this.mMinWidth = this.mPreferredWidth;
					this.mMinHeight = this.mPreferredHeight;
				}
			}
			return;
		}
		
		public void layoutContainer(Container parent)
		{
			if(this.mSizeUnknown){
				this.setSizes(parent);
			}
			
			int count = parent.getComponentCount();
			for(int i = 0; i < count; i++)
			{
				Component c = parent.getComponent(i);
				if(!c.isVisible()){
					continue;
				}
				
				if(i == 0){
					c.setBounds(0, 0, parent.getWidth() - parent.getHeight(), parent.getHeight());
				}else{
					c.setBounds(parent.getWidth() - parent.getHeight(), 0, parent.getHeight(), parent.getHeight());
				}
			}
			return;
		}
		
		public Dimension minimumLayoutSize(Container parent)
		{
			Dimension result = new Dimension(0, 0);
			result.width = this.mMinWidth + parent.getInsets().left + parent.getInsets().right;
			result.height = this.mMinHeight + parent.getInsets().top + parent.getInsets().bottom;
			this.mSizeUnknown = false;
			return result;
		}
		
		public Dimension preferredLayoutSize(Container parent)
		{
			Dimension result = new Dimension(0, 0);
			
			this.setSizes(parent);
			
			result.width = this.mPreferredWidth + parent.getInsets().left + parent.getInsets().right;
			result.height = this.mPreferredHeight + parent.getInsets().top + parent.getInsets().bottom;
			this.mSizeUnknown = false;
			return result;
		}
	}
}

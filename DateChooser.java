package main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.Font;
import java.awt.Insets;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 * A general use calendar date chooser dialog.
 * 
 * Version 2.0.1 uses the newer java.time classes instead of the older java.util.Calendar and Date classes. And now, a local
 * time zone (ZoneId) value is required to ensure more accurate date values.
 * 
 * Version 2.0 is a complete rewrite with much-needed code improvements.
 * 
 * @author John McCullock
 * @version 2.0.1 2017-09-20
 */
@SuppressWarnings("serial")
public class DateChooser extends JDialog
{
	private static final String PREV_BUTTON_CAPTION = "<<";
	private static final String NEXT_BUTTON_CAPTION = ">>";
	private static final Insets COMPONENT_INSETS = new Insets(5, 5, 5, 5);
	private static final Color DEFAULT_BACKGROUND = Color.WHITE;
	private static final int CALENDAR_ROWS = 6;
	private static final int CALENDAR_COLUMNS = 7;
	
	private Component mAnchor = null;
	private boolean mIsUndecorated = true;
	private JLabel mMonthLabel = null;
	private CalendarButton[] mDayButtons = null;
	private Color mBackgroundColor = DEFAULT_BACKGROUND;
	private Insets mInsets = COMPONENT_INSETS;
	private LocalDate mDateRef = null;
	private LocalDate mResponse = null;
	
	public DateChooser(JFrame owner, Component anchor, ZoneId timeZone)
	{
		super(owner, Dialog.ModalityType.APPLICATION_MODAL);
		this.mAnchor = anchor;
		this.setInitialDate(null, timeZone);			// will default to current date.
		return;
	}
	
	public DateChooser(JDialog owner, Component anchor, ZoneId timeZone)
	{
		super(owner, Dialog.ModalityType.APPLICATION_MODAL);
		this.mAnchor = anchor;
		this.setInitialDate(null, timeZone);			// will default to current date.
		return;
	}
	
	public DateChooser(JFrame owner, Component anchor, boolean isUndecorated, ZoneId timeZone)
	{
		super(owner, Dialog.ModalityType.APPLICATION_MODAL);
		this.mAnchor = anchor;
		this.mIsUndecorated = isUndecorated;
		this.setInitialDate(null, timeZone);			// will default to current date.
		return;
	}
	
	public DateChooser(JDialog owner, Component anchor, boolean isUndecorated, ZoneId timeZone)
	{
		super(owner, Dialog.ModalityType.APPLICATION_MODAL);
		this.mAnchor = anchor;
		this.mIsUndecorated = isUndecorated;
		this.setInitialDate(null, timeZone);			// will default to current date.
		return;
	}
	
	public LocalDate showDialog()
	{
		this.initializeMain();
		this.setVisible(true);
		this.pack();
		
		return this.mResponse;
	}
	
	private void initializeMain()
	{
		this.setUndecorated(this.mIsUndecorated);
		
		JPanel basePanel = new JPanel();
		basePanel.setLayout(new CalendarLayout());
		basePanel.setBackground(this.mBackgroundColor);
		
		CalendarButton prevButton = this.createButtonPanel(PREV_BUTTON_CAPTION);
		CalendarButton nextButton = this.createButtonPanel(NEXT_BUTTON_CAPTION);
		MouseListener prevListener = this.createPreviousButtonListener(basePanel, prevButton);
		MouseListener nextListener = this.createNextButtonListener(basePanel, nextButton);
		prevButton.addMouseListener(prevListener);
		nextButton.addMouseListener(nextListener);
		
		this.mMonthLabel = this.createMonthLabel();
		
		JPanel sundayPanel = this.createWeekTitlePanel("Su");
		JPanel mondayPanel = this.createWeekTitlePanel("Mo");
		JPanel tuesPanel = this.createWeekTitlePanel("Tu");
		JPanel wednPanel = this.createWeekTitlePanel("We");
		JPanel thursPanel = this.createWeekTitlePanel("Th");
		JPanel fridayPanel = this.createWeekTitlePanel("Fr");
		JPanel satPanel = this.createWeekTitlePanel("Sa");
		
		this.mDayButtons = this.createCalendarButtons(basePanel);
		
		basePanel.add(prevButton);
		basePanel.add(this.mMonthLabel);
		basePanel.add(nextButton);
		basePanel.add(sundayPanel);
		basePanel.add(mondayPanel);
		basePanel.add(tuesPanel);
		basePanel.add(wednPanel);
		basePanel.add(thursPanel);
		basePanel.add(fridayPanel);
		basePanel.add(satPanel);
		for(int i = 0; i < this.mDayButtons.length; i++)
		{
			basePanel.add(this.mDayButtons[i]);
		}
		
		KeyStroke ksEscape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		this.getRootPane().registerKeyboardAction(this.createCancelListener(), "cancel", ksEscape, JComponent.WHEN_IN_FOCUSED_WINDOW);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.addWindowListener(this.createDialogWindowListener(basePanel));
		this.getContentPane().add(basePanel);
		this.setResizable(true);
		this.setLocationRelativeTo(this.mAnchor);
		if(this.mAnchor != null){
			this.setLocation(this.mAnchor.getLocationOnScreen().x + this.mAnchor.getWidth(), this.mAnchor.getLocationOnScreen().y);
		}
		this.setMinimumSize(new Dimension(350, 320));
		this.setPreferredSize(new Dimension(400, 370));
		return;
	}
	
	private CalendarButton createButtonPanel(String caption)
	{
		CalendarButton button = new CalendarButton();
		button.setLayout(new GridBagLayout());
		button.setBackground(this.mBackgroundColor);
		button.add(new JLabel(caption), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		return button;
	}
	
	private JLabel createMonthLabel()
	{
		JLabel aLabel = new JLabel();
		aLabel.setFont(new Font(aLabel.getFont().getFontName(), Font.PLAIN, aLabel.getFont().getSize() + 4));
		aLabel.setOpaque(false);
		aLabel.setHorizontalAlignment(JLabel.CENTER);
		return aLabel;
	}
	
	private JPanel createWeekTitlePanel(final String caption)
	{
		JPanel aPanel = new JPanel();
		aPanel.setLayout(new GridBagLayout());
		aPanel.setOpaque(false);
		aPanel.add(new JLabel(caption), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		return aPanel;
	}
	
	private CalendarButton[] createCalendarButtons(JPanel basePanel)
	{
		CalendarButton[] buttons = new CalendarButton[CALENDAR_COLUMNS * CALENDAR_ROWS];
		/*
		 * firstDay is the day of the week (1-7) in first week of the month.
		 */
		int firstDay = this.convertFromISO8601(DayOfWeek.from(this.mDateRef.with(TemporalAdjusters.firstDayOfMonth())));
		int lastDay = this.mDateRef.with(TemporalAdjusters.lastDayOfMonth()).get(ChronoField.DAY_OF_MONTH);
		int count = 0;
		
		/*
		 * Fill in the empty grid cells up to the first day of the month.
		 */
		for(int i = 1; i < firstDay; i++)
		{
			buttons[count] = this.createCalendarBlank();
			count++;
		}
		
		/*
		 * Fill in all days of the month with buttons, using i for the button caption.
		 */
		for(int i = 1; i <= lastDay; i++)
		{
			CalendarButton button = this.createButtonPanel(String.valueOf(i));
			LocalDate value = this.mDateRef.with(ChronoField.DAY_OF_MONTH, i);
			button.date = value;
			MouseListener buttonListener = this.createDayButtonListener(basePanel, button);
			button.addMouseListener(buttonListener);
			buttons[count] = button;
			count++;
		}
		
		/*
		 * Fill in remaining grid cells beyond end of the month.
		 */
		for(int i = (firstDay + lastDay) - 1; i < (CALENDAR_ROWS * CALENDAR_COLUMNS); i++)
		{
			buttons[count] = this.createCalendarBlank();
			count++;
		}
		return buttons;
	}
	
	private CalendarButton createCalendarBlank()
	{
		CalendarButton button = new CalendarButton();
		button.setOpaque(false);
		return button;
	}
	
	private void updateCalendarGUI(JPanel basePanel)
	{
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMMM yyyy");
		this.mMonthLabel.setText(dtf.format(this.mDateRef));
		for(int i = 0; i < this.mDayButtons.length; i++)
		{
			basePanel.remove(this.mDayButtons[i]);
		}
		this.mDayButtons = this.createCalendarButtons(basePanel);
		for(int i = 0; i < this.mDayButtons.length; i++)
		{
			basePanel.add(this.mDayButtons[i]);
		}
		return;
	}
	
	private void rollBackOneMonth()
	{
		this.mDateRef = this.mDateRef.minusMonths(1L);
		this.mResponse = null;
		return;
	}
	
	private void rollForwardOneMonth()
	{
		this.mDateRef = this.mDateRef.plusMonths(1L);
		this.mResponse = null;
		return;
	}
	
	public void setInitialDate(final LocalDate initialDate, final ZoneId timeZone)
	{
		if(initialDate == null){
			this.mDateRef = LocalDate.now(timeZone);
		}else{
			this.mDateRef = initialDate;
		}
		return;
	}
	
	/**
	 * This version of DateChooser only uses Sunday as the first day of the week.  It seems other planets think differently.
	 * @param isoValue DayOfWeek enum value.
	 * @return int.
	 */
	public int convertFromISO8601(DayOfWeek isoValue)
	{
		if(isoValue.equals(DayOfWeek.SUNDAY)){
			return 1;
		}
		if(isoValue.equals(DayOfWeek.MONDAY)){
			return 2;
		}
		if(isoValue.equals(DayOfWeek.TUESDAY)){
			return 3;
		}
		if(isoValue.equals(DayOfWeek.WEDNESDAY)){
			return 4;
		}
		if(isoValue.equals(DayOfWeek.THURSDAY)){
			return 5;
		}
		if(isoValue.equals(DayOfWeek.FRIDAY)){
			return 6;
		}
		if(isoValue.equals(DayOfWeek.SATURDAY)){
			return 7;
		}
		return 0;
	}
	
	@Override
	public void setBackground(Color value)
	{
		this.mBackgroundColor = value;
		super.setBackground(value);
		return;
	}
	
	public void setComponentSpacing(int horizontal, int vertical)
	{
		this.mInsets = new Insets(vertical, horizontal, vertical, horizontal);
		return;
	}
	
	private ActionListener createCancelListener()
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				mResponse = null;
				DateChooser.this.setVisible(false);
				return;
			}
		};
	}
	
	private MouseListener createPreviousButtonListener(JPanel basePanel, CalendarButton button)
	{
		return new MouseAdapter()
		{
			public void mouseEntered(MouseEvent e)
			{
				button.setBackground(Color.LIGHT_GRAY);
				return;
			}
			
			public void mouseExited(MouseEvent e)
			{
				button.setBackground(mBackgroundColor);
				return;
			}
			
			public void mousePressed(MouseEvent e)
			{
				rollBackOneMonth();
				updateCalendarGUI(basePanel);
				return;
			}
		};
	}
	
	private MouseListener createNextButtonListener(JPanel basePanel, CalendarButton button)
	{
		return new MouseAdapter()
		{
			public void mouseEntered(MouseEvent e)
			{
				button.setBackground(Color.LIGHT_GRAY);
				return;
			}
			
			public void mouseExited(MouseEvent e)
			{
				button.setBackground(mBackgroundColor);
				return;
			}
			
			public void mousePressed(MouseEvent e)
			{
				rollForwardOneMonth();
				updateCalendarGUI(basePanel);
				return;
			}
		};
	}
	
	private MouseListener createDayButtonListener(JPanel basePanel, CalendarButton button)
	{
		return new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if(e.getClickCount() > 1){
					mResponse = button.date;
					DateChooser.this.setVisible(false);
				}
			}
			
			public void mouseEntered(MouseEvent e)
			{
				button.setBackground(Color.LIGHT_GRAY);
				return;
			}
			
			public void mouseExited(MouseEvent e)
			{
				button.setBackground(mBackgroundColor);
				return;
			}
		};
	}
	
	
	private WindowListener createDialogWindowListener(JPanel basePanel)
	{
		return new WindowAdapter()
		{
			public void windowOpened(WindowEvent e)
			{
				updateCalendarGUI(basePanel);
				return;
			}
		};
	}
	
	
	private class CalendarButton extends JPanel
	{
		public LocalDate date = null;
	}
	
	private class CalendarLayout implements LayoutManager
	{
		private int mMinWidth = 0;
		private int mMinHeight = 0;
		private int mPreferredWidth = 0;
		private int mPreferredHeight = 0;
		private boolean mSizeUnknown = true;
		
		public void addLayoutComponent(String name, Component comp) { return; }
		public void removeLayoutComponent(Component comp) { return; }
		
		private void setSizes(Container parent)
		{
			this.mPreferredWidth += (parent.getComponent(3).getPreferredSize().width * 7);
			this.mPreferredWidth += (mInsets.left * 8);
			this.mPreferredHeight += (parent.getComponent(3).getPreferredSize().height * 8);
			this.mPreferredHeight += (mInsets.top * 9);
			this.mMinWidth = this.mPreferredWidth;
			this.mMinHeight = this.mPreferredHeight;
			return;
		}
		
		public void layoutContainer(Container parent)
		{
			if(this.mSizeUnknown){
				this.setSizes(parent);
			}
			
			int adjWidth = parent.getWidth() - (mInsets.left * 8);
			int width = (int)Math.floor(adjWidth / 7.0);
			int adjHeight = parent.getHeight() - (mInsets.top * 9);
			int height = (int)Math.floor(adjHeight / 8.0);
			
			parent.getComponent(0).setBounds(mInsets.left, mInsets.top, width, height);
			parent.getComponent(1).setBounds(width + (mInsets.left * 2), mInsets.top, (width * 5) + (mInsets.left * 4), height);
			parent.getComponent(2).setBounds(parent.getWidth() - (width + mInsets.left), mInsets.top, width, height);
			
			int k = 3;
			for(int j = 0; j < 7; j++)
			{
				int y = (mInsets.top + height + mInsets.top) + ((height + mInsets.top) * j);
				for(int i = 0; i < 7; i++)
				{
					Component c = parent.getComponent(k);
					if(!c.isVisible()){
						continue;
					}
					int x = mInsets.left + ((width + mInsets.left) * i);
					c.setBounds(x, y, width, height);
					k++;
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

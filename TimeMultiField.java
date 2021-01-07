package test3;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.util.ArrayList;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * 
 * @author John McCullock
 * @version 1.0 2017-11-03
 */

@SuppressWarnings("serial")
public class TimeMultiField extends JComponent
{
	public enum Format
	{
		TWELVE_HOUR(12), 
		TWENTY_FOUR_HOUR(24);
		
		private int mValue = 0;
		
		private Format(int value)
		{
			this.mValue = value;
			return;
		}
		
		public int getValue()
		{
			return this.mValue;
		}
	};
	
	private static final String DEFAULT_DELIMITER = ":";
	private static final int DEFAULT_HOUR_FIELD_LIMIT = 2;
	private static final int DEFAULT_MINUTE_FIELD_LIMIT = 2;
	private static final String LARGE_PROTOTYPE = "W";
	private static final int LARGE_DELIMITER_FONT_STEP = 1;
	private static final Insets DEFAULT_INSETS = new Insets(2, 0, 2, 0);
	private static final Insets DEFAULT_MARGINS = new Insets(0, 2, 0, 2);
	private static final Insets DEFAULT_COMBOBOX_INSETS = new Insets(0, 4, 0, 2);
	private static final String AM_CAPTION = "AM";
	private static final String PM_CAPTION = "PM";
	private static final Format DEFAULT_FORMAT = Format.TWENTY_FOUR_HOUR;
	
	private JTextField mHoursField = null;
	private JTextField mMinutesField = null;
	private JComboBox<String> mAMPMList = null;
	private Format mFormat = DEFAULT_FORMAT;
	
	public TimeMultiField(TimeMultiField.Format format)
	{
		this.mFormat = format;
		if(format.equals(Format.TWELVE_HOUR)){
			this.initializeFormat12();
		}else{
			this.initializeFormat24();
		}
		return;
	}
	
	private void initializeFormat12()
	{
		this.setLayout(new GridBagLayout());
		this.mHoursField = this.createNumericField(DEFAULT_HOUR_FIELD_LIMIT, 0, this.mFormat.getValue(), LARGE_PROTOTYPE);
		this.mMinutesField = this.createNumericField(DEFAULT_MINUTE_FIELD_LIMIT, 0,	this.mFormat.getValue(), LARGE_PROTOTYPE);
		this.mAMPMList = this.createAMPMList();
		
		this.mHoursField.addKeyListener(this.createHoursKeyListener(this.mHoursField, this.mMinutesField));
		this.mMinutesField.addKeyListener(this.createFormat12MinutesKeyListener(this.mHoursField, this.mMinutesField, this.mAMPMList));
		
		this.add(this.mHoursField, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		JLabel delimiter = this.createDelimiterLabel(DEFAULT_DELIMITER, LARGE_DELIMITER_FONT_STEP);
		this.add(delimiter, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, DEFAULT_MARGINS.left, 0, DEFAULT_MARGINS.right), 0, 0));
		int comboBoxSpace = delimiter.getPreferredSize().width + DEFAULT_MARGINS.left + DEFAULT_MARGINS.right;
		this.add(this.mMinutesField, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, comboBoxSpace), 0, 0));
		this.add(this.mAMPMList, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		
		return;
	}
	
	private void initializeFormat24()
	{
		this.setLayout(new GridBagLayout());
		this.mHoursField = this.createNumericField(DEFAULT_HOUR_FIELD_LIMIT, 0, this.mFormat.getValue(), LARGE_PROTOTYPE);
		this.mMinutesField = this.createNumericField(DEFAULT_MINUTE_FIELD_LIMIT, 0,	this.mFormat.getValue(), LARGE_PROTOTYPE);
		
		this.mHoursField.addKeyListener(this.createHoursKeyListener(this.mHoursField, this.mMinutesField));
		this.mMinutesField.addKeyListener(this.createFormat24MinutesKeyListener(this.mHoursField, this.mMinutesField));
		
		this.add(this.mHoursField, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		JLabel delimiter = this.createDelimiterLabel(DEFAULT_DELIMITER, LARGE_DELIMITER_FONT_STEP);
		this.add(delimiter, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 2, 0, 2), 0, 0));
		this.add(this.mMinutesField, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		return;
	}
	
	private JTextField createNumericField(int maxLength, int lowerRange, int upperRange, String prototype)
	{
		JTextField field = new JTextField();
		field.setMargin(DEFAULT_INSETS);
		int charWidth = field.getFontMetrics(field.getFont()).stringWidth(prototype);
		int charHeight = field.getFontMetrics(field.getFont()).getAscent();
		// Adding one here in order to insure large enough space to contain any characters.
		field.setMinimumSize(new Dimension((charWidth * (maxLength + 1)), charHeight + DEFAULT_INSETS.top + DEFAULT_INSETS.bottom));
		field.setPreferredSize(new Dimension(field.getMinimumSize().width, field.getPreferredSize().height));
		field.setDocument(new IntOnlyDocument(maxLength));
		field.setHorizontalAlignment(JTextField.CENTER);
		return field;
	}
	
	private JComboBox<String> createAMPMList()
	{
		JComboBox<String> list = new JComboBox<String>();
		AMPMComboBoxModel model = new AMPMComboBoxModel();
		model.addItem(AM_CAPTION);
		model.addItem(PM_CAPTION);
		list.setModel(model);
		list.setEditable(false);
		list.setRenderer(new AMPMListCellRenderer<String>(SwingConstants.LEFT, DEFAULT_COMBOBOX_INSETS));
		// Multiplying by 3 here in order to insure large enough space to contain any characters.
		int width = list.getFontMetrics(list.getFont()).stringWidth("W") * 3;
		width += list.getPreferredSize().height;
		list.setPreferredSize(new Dimension(width, list.getPreferredSize().height));
		return list;
	}
	
	private JLabel createDelimiterLabel(String delimiter, int fontStep)
	{
		JLabel aLabel = new JLabel(delimiter);
		aLabel.setOpaque(false);
		aLabel.setFont(new Font(aLabel.getFont().getFontName(), aLabel.getFont().getStyle(), aLabel.getFont().getSize() + fontStep));
		return aLabel;
	}
	
	public JTextField getHoursField()
	{
		return this.mHoursField;
	}
	
	public JTextField getMinutesField()
	{
		return this.mMinutesField;
	}
	
	public JComboBox<String> getAMPMList()
	{
		return this.mAMPMList;
	}
	
	private int getKeyNumeric(int keyCode)
	{
		if(keyCode == KeyEvent.VK_0 || keyCode == KeyEvent.VK_NUMPAD0){
			return 0;
		}
		if(keyCode == KeyEvent.VK_1 || keyCode == KeyEvent.VK_NUMPAD1){
			return 1;
		}
		if(keyCode == KeyEvent.VK_2 || keyCode == KeyEvent.VK_NUMPAD2){
			return 2;
		}
		if(keyCode == KeyEvent.VK_3 || keyCode == KeyEvent.VK_NUMPAD3){
			return 3;
		}
		if(keyCode == KeyEvent.VK_4 || keyCode == KeyEvent.VK_NUMPAD4){
			return 4;
		}
		if(keyCode == KeyEvent.VK_5 || keyCode == KeyEvent.VK_NUMPAD5){
			return 5;
		}
		if(keyCode == KeyEvent.VK_6 || keyCode == KeyEvent.VK_NUMPAD6){
			return 6;
		}
		if(keyCode == KeyEvent.VK_7 || keyCode == KeyEvent.VK_NUMPAD7){
			return 7;
		}
		if(keyCode == KeyEvent.VK_8 || keyCode == KeyEvent.VK_NUMPAD8){
			return 8;
		}
		if(keyCode == KeyEvent.VK_9 || keyCode == KeyEvent.VK_NUMPAD9){
			return 9;
		}
		return -1;
	}
	
	private KeyListener createHoursKeyListener(JTextField hoursField, JTextField minutesField)
	{
		return new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_RIGHT){
					if(hoursField.getCaretPosition() >= hoursField.getText().length()){
						minutesField.requestFocus();
					}
					return;
				}
				
				if(hoursField.getText().length() == DEFAULT_HOUR_FIELD_LIMIT){
					int number = getKeyNumeric(e.getKeyCode());
					if(number > -1){
						minutesField.requestFocus();
						minutesField.setText(String.valueOf(number));
					}
				}
				return;
			}
		};
	}
	
	private KeyListener createFormat12MinutesKeyListener(JTextField hoursField, JTextField minutesField, JComboBox<String> ampmList)
	{
		return new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_RIGHT){
					if(minutesField.getCaretPosition() >= minutesField.getText().length()){
						ampmList.requestFocus();
					}
					return;
				}else if(e.getKeyCode() == KeyEvent.VK_LEFT){
					if(minutesField.getCaretPosition() == 0){
						hoursField.requestFocus();
					}
					return;
				}
				
				if(minutesField.getText().length() == DEFAULT_MINUTE_FIELD_LIMIT){
					int number = getKeyNumeric(e.getKeyCode());
					if(number > -1){
						ampmList.requestFocus();
					}
				}
				return;
			}
		};
	}
	
	private KeyListener createFormat24MinutesKeyListener(JTextField hoursField, JTextField minutesField)
	{
		return new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_LEFT){
					if(minutesField.getCaretPosition() == 0){
						hoursField.requestFocus();
					}
					return;
				}
				return;
			}
		};
	}
	
	public class AMPMComboBoxModel extends AbstractListModel<String> implements ComboBoxModel<String>
	{
		private ArrayList<String> mRows = new ArrayList<String>();
		private String mSelection = null;
		
		public AMPMComboBoxModel()
		{
			this.mRows = new ArrayList<String>();
			return;
		}
		
		public AMPMComboBoxModel(ArrayList<String> rows)
		{
			this.setData(rows);
			return;
		}
		
		public void setData(ArrayList<String> rows)
		{
			this.mRows = rows;
			return;
		}
		
		public void addItem(String item)
		{
			this.mRows.add(item);
			return;
		}
		
		@Override
		public String getElementAt(int index)
		{	
			return this.mRows.get(index);
		}

		@Override
		public int getSize()
		{	
			return this.mRows.size();
		}

		@Override
		public String getSelectedItem()
		{
			return this.mSelection == null ? new String() : this.mSelection;
		}

		@Override
		public void setSelectedItem(Object item)
		{
			this.mSelection = (String)item;
			return;
		}
	}
	
	public class AMPMListCellRenderer<T> extends JLabel implements ListCellRenderer<T>
	{
		private int mAlignment = SwingConstants.LEFT;
		private Insets mMargins = null;
		
		public AMPMListCellRenderer() { return; }
		
		public AMPMListCellRenderer(int alignment, Insets margins)
		{
			this.setAlignment(alignment);
			this.setMargins(margins);
			return;
		}
		
		@Override
		public Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected, boolean cellHasFocus)
		{
			setOpaque(true);
			if(isSelected){
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			}else{
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setHorizontalAlignment(this.mAlignment);
			if(this.mMargins != null){
				setBorder(BorderFactory.createEmptyBorder(this.mMargins.top, this.mMargins.left, this.mMargins.bottom, this.mMargins.right));
			}
			setText(String.valueOf(value));
			setFont(list.getFont());
			return this;
		}
		
		public void setAlignment(int align)
		{
			if(align != SwingConstants.LEFT && align != SwingConstants.CENTER && align != SwingConstants.RIGHT){
				throw new IllegalArgumentException("Can only accept SwingConstants: LEFT, CENTER and RIGHT.");
			}
			this.mAlignment = align;
			return;
		}
		
		public int getAlignment()
		{
			return this.mAlignment;
		}
		
		public void setMargins(Insets margins)
		{
			this.mMargins = margins;
			return;
		}
		
		public Insets getMargins()
		{
			return this.mMargins;
		}
	}
	
	private class IntOnlyDocument extends PlainDocument
	{
		private int mInputLimit = 0;
		
		public IntOnlyDocument(int inputLimit)
		{
			this.mInputLimit = inputLimit;
			return;
		}
		
		@Override
		public void insertString(int offs, String str, AttributeSet a)
		{
			if(!this.lengthIsValid(str)){
				return;
			}
			if(!this.isNumeric(str)){
				return;
			}
			try{
				super.insertString(offs, str, a);
			}catch(BadLocationException ble){
				ble.printStackTrace();
			}
			return;
		}
		
		/**
		 * If no arbitrary input length is specified (i.e.: 0 or -1), then any input length is accepted.
		 * @param input String
		 * @return boolean true if current length is acceptable, false otherwise.
		 */
		private boolean lengthIsValid(String input)
		{
			if(this.mInputLimit <= 0){
				return true;
			}
			if(this.getLength() + input.length() <= this.mInputLimit){
				return true;
			}else{
				return false;
			}
		}
		
		private boolean isNumeric(String input)
		{
			boolean results = false;
			try{
				Integer.parseInt(input);
				results = true;
			}catch(Exception ex){
				results = false;
			}
			return results;
		}
	}
}

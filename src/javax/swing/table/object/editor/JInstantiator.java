package javax.swing.table.object.editor;

import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * 
 * Puts a 'create' and 'delete' buttons into one panel to be displayed in the
 * table cell.
 * 
 * @author Antony Hixson
 *
 */
public class JInstantiator extends JPanel {
	
	private final JButton updateButton;
	private final JButton removeButton;
	
	public JInstantiator() {
		
		this.updateButton = new JButton("Create");
		this.updateButton.setActionCommand("CREATE");
		
		this.removeButton = new JButton("Delete");
		this.removeButton.setActionCommand("DELETE");
		
		super.setLayout(new GridLayout(1, 2));
		super.add(updateButton);
		super.add(removeButton);
	}
	
	public final void setObjectValidMode(boolean objectValid) {
		removeButton.setEnabled(objectValid);
		
		if (objectValid) {
			updateButton.setText("Edit");
			updateButton.setActionCommand("EDIT");
		} else {
			updateButton.setText("Create");
			updateButton.setActionCommand("CREATE");
		}
	}
	
	public JButton getRemoveButton() {
		return removeButton;
	}
	
	public JButton getUpdateButton() {
		return updateButton;
	}
	
	public void addActionListener(ActionListener actionListener) {
		updateButton.addActionListener(actionListener);
		removeButton.addActionListener(actionListener);
	}
	
	public void removeActionListener(ActionListener actionListener) {
		updateButton.removeActionListener(actionListener);
		removeButton.removeActionListener(actionListener);
	}
	
}

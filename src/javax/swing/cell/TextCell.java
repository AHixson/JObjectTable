package javax.swing.cell;

import javax.swing.JObjectTable;
import javax.swing.JTextField;
import javax.swing.table.object.InvokableColumn;

/**
 * 
 * <p>Text cell editor and renderer that is represented as a text box</p>
 * 
 * @author Antony Hixson
 *
 */
public class TextCell extends AbstractCell<JTextField, JTextField> {
	
	/**
	 * Component rendered by default
	 */
	private final JTextField viewComponent;
	
	/**
	 * Component rendered when cell is focused
	 */
	private final JTextField editComponent;
	
	public TextCell() {
		this.viewComponent = new JTextField();
		this.viewComponent.setBorder(null);
		
		this.editComponent = new JTextField();
		this.editComponent.setBorder(null);
		this.editComponent.addActionListener(event -> stopCellEditing());
	}
	
	@Override
	public Object getCellEditorValue() {
		return editComponent.getText();
	}
	
	@Override
	public JTextField getViewComponent() {
		return viewComponent;
	}

	@Override
	public JTextField getEditComponent() {
		return editComponent;
	}

	@Override
	public void prepareRenderer(JObjectTable<?> table, JTextField component, InvokableColumn invokableColumn,
			Object rowObjectInstance, Object value) {

		String text = (String) value;

		component.setEditable(invokableColumn.editable());
		component.setText(text != null && !text.isEmpty() ? text : invokableColumn.placeholder());

	}

	@Override
	public void prepareEditor(JObjectTable<?> table, JTextField component, InvokableColumn invokableColumn,
			Object rowObjectInstance, Object value) {

		String text = (String) value;

		component.setEditable(invokableColumn.editable());
		component.setText(text);
	}

}

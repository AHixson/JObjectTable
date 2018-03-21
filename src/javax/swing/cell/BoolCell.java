package javax.swing.cell;

import javax.swing.JCheckBox;
import javax.swing.JObjectTable;
import javax.swing.table.object.InvokableColumn;

/**
 * 
 * <p>Boolean cell editor and renderer that is represented as a check box</p>
 * 
 * @author Antony Hixson
 *
 */
public class BoolCell extends AbstractCell<JCheckBox, JCheckBox> {
	
	/**
	 * Component rendered by default
	 */
	private final JCheckBox viewComponent;
	
	/**
	 * Component rendered when cell is focused
	 */
	private final JCheckBox editComponent;
	
	public BoolCell() {
		this.viewComponent = new JCheckBox();
		this.viewComponent.setBorder(null);
		this.viewComponent.setHorizontalAlignment(JCheckBox.CENTER);
		
		this.editComponent = new JCheckBox();
		this.editComponent.setBorder(null);
		this.editComponent.addActionListener(event -> stopCellEditing());
		this.editComponent.setHorizontalAlignment(JCheckBox.CENTER);
	}
	
	@Override
	public Object getCellEditorValue() {
		return editComponent.isSelected();
	}
	
	@Override
	public JCheckBox getViewComponent() {
		return viewComponent;
	}

	@Override
	public JCheckBox getEditComponent() {
		return editComponent;
	}

	@Override
	public void prepareRenderer(JObjectTable<?> table, JCheckBox component, InvokableColumn invokableColumn,
			Object rowObjectInstance, Object value) {

		component.setSelected((Boolean) value);
	}

	@Override
	public void prepareEditor(JObjectTable<?> table, JCheckBox component, InvokableColumn invokableColumn,
			Object rowObjectInstance, Object value) {

		component.setSelected((Boolean) value);
	}

}

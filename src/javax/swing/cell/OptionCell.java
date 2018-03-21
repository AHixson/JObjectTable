package javax.swing.cell;

import java.util.Collection;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JObjectTable;
import javax.swing.table.object.InvokableColumn;

/**
 * 
 * <p>Option cell editor and renderer that is represented as a combo box</p>
 * 
 * @author Antony Hixson
 *
 */
public class OptionCell <E> extends AbstractCell<JComboBox<E>, JComboBox<E>> {

	/**
	 * Component rendered by default
	 */
	private final JComboBox<E> viewComponent;
	
	/**
	 * Component rendered when cell is focused
	 */
	private final JComboBox<E> editComponent;
	
	public OptionCell() {
		this.viewComponent = new JComboBox<>();
		this.viewComponent.setBorder(null);
		
		this.editComponent = new JComboBox<>();
		this.editComponent.setBorder(null);
		this.editComponent.addItemListener(event -> stopCellEditing());
	}
	
	@SafeVarargs
	public OptionCell(E... items) {
		this();
		this.viewComponent.setModel(new DefaultComboBoxModel<>(items));
		this.editComponent.setModel(new DefaultComboBoxModel<>(items));
	}

	public OptionCell(Collection<E> collection) {
		this();
		collection.forEach(this::addItem);
	}

	public void addItem(E item) {
		viewComponent.addItem(item);
		editComponent.addItem(item);
	}

	public void removeItem(E item) {
		viewComponent.removeItem(item);
		editComponent.removeItem(item);
	}
	
	public void setSelectedItem(E item) {
		viewComponent.setSelectedItem(item);
		editComponent.setSelectedItem(item);
	}

	@Override
	public Object getCellEditorValue() {
		return editComponent.getSelectedItem();
	}

	@Override
	public JComboBox<E> getViewComponent() {
		return viewComponent;
	}

	@Override
	public JComboBox<E> getEditComponent() {
		return editComponent;
	}

	@Override
	public void prepareRenderer(JObjectTable<?> table, JComboBox<E> component, InvokableColumn iColumn,
			Object rowObjectInstance, Object value) {

		component.setSelectedItem(value);
	}

	@Override
	public void prepareEditor(JObjectTable<?> table, JComboBox<E> component, InvokableColumn iColumn,
			Object rowObjectInstance, Object value) {

		component.setSelectedItem(value);
	}

	/**
	 * Create new {@link OptionCell} for enum and array types
	 * 
	 * @param type
	 *            - Enum type
	 * @return New {@link OptionCell} instance
	 */
	public static final <E> OptionCell<E> newInstance(Class<E> type) {
		
		OptionCell<E> result = null;
		
		if (type == null) {
			throw new NullPointerException("Class type cannot be null");
		} else if (type.isEnum()) {
			result = new OptionCell<>(type.getEnumConstants());
		} else if (type.isArray()) {
			result = new OptionCell<>();
		} else {
			throw new IllegalArgumentException("Class type must be array or enum type");
		}
		
		return result;
	}
}

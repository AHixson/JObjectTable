package javax.swing.cell;

import java.awt.Color;
import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JObjectTable;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.object.InvokableColumn;

/**
 * <p>An abstract cell renderer and editor.</p>
 * 
 * @author Antony Hixson
 *
 */
public abstract class AbstractCell<ViewComponent extends Component, EditComponent extends Component>
		extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {

	/**
	 * 
	 * @return Component to render on view
	 */
	public abstract ViewComponent getViewComponent();

	/**
	 * 
	 * @return Component to render on edit
	 */
	public abstract EditComponent getEditComponent();

	/**
	 * 
	 * @param table
	 *            - Source
	 * @param component
	 *            - View component
	 * @param invokableColumn
	 *            - Invokable column
	 * @param rowObjectInstance
	 *            - Row object instance
	 * @param value
	 *            - Value at column for row
	 */
	public abstract void prepareRenderer(JObjectTable<?> table, ViewComponent component,
			InvokableColumn invokableColumn, Object rowObjectInstance, Object value);

	/**
	 * 
	 * @param table
	 *            - Source
	 * @param component
	 *            - Edit component
	 * @param invokableColumn
	 *            - Invokable column
	 * @param rowObjectInstance
	 *            - Row object instance
	 * @param value
	 *            - Value at column for row
	 */
	public abstract void prepareEditor(JObjectTable<?> table, EditComponent component, InvokableColumn invokableColumn,
			Object rowObjectInstance, Object value);

	/**
	 * Standard rendering behaviour for view
	 */
	@Override
	public final Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int col) throws RuntimeException {

		ViewComponent viewComponent = null;
		JObjectTable<?> source = null;
		Class<?> type = null;
		InvokableColumn invokableColumn = null;
		Object rowObjInstance = null;
		String tooltip = null;

		Object foreground = null;
		Object background = null;

		try {

			/*
			 * Variables
			 */

			viewComponent = getViewComponent();

			source = (JObjectTable<?>) table;
			invokableColumn = source.getInvokableColumn(col);
			type = invokableColumn.getType();
			rowObjInstance = table.getModel().getValueAt(row, 0);
			
			if (!type.equals(void.class) && !type.equals(Runnable.class)) {
				value = invokableColumn.getValue(rowObjInstance);
			}
			
			/*
			 * Behaviour
			 */

			viewComponent.setEnabled(invokableColumn.editable());
			
			if (viewComponent instanceof JComponent) {
				
				tooltip = invokableColumn.tooltip();
				
				assert (tooltip != null);
				
				((JComponent) viewComponent).setToolTipText(tooltip.isEmpty() ? null : tooltip);
			}

			/*
			 * Colours
			 */

			if (isSelected) {
				foreground = "Table.selectionForeground";
				background = "Table.selectionBackground";
			} else if (hasFocus) {
				foreground = "Table.focusCellForeground";
				background = "Table.focusCellBackground";
			} else {
				foreground = "Table.foreground";
				background = "Table.background";
			}

			foreground = UIManager.getColor(foreground);
			background = UIManager.getColor(background);

			viewComponent.setForeground((Color) foreground);
			viewComponent.setBackground((Color) background);

			prepareRenderer(source, viewComponent, invokableColumn, rowObjInstance, value);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return viewComponent;
	}

	/**
	 * Standard rendering behaviour for edit
	 */
	@Override
	public final Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
			int col) throws RuntimeException {

		EditComponent editComponent = null;
		JObjectTable<?> source = null;
		Class<?> type = null;
		InvokableColumn invokableColumn = null;
		Object rowObjInstnace = null;

		try {

			/*
			 * Variables
			 */

			editComponent = getEditComponent();

			source = (JObjectTable<?>) table;
			invokableColumn = source.getInvokableColumn(col);
			type = invokableColumn.getType();
			rowObjInstnace = table.getModel().getValueAt(row, 0);
			
			if (!type.equals(void.class) && !type.equals(Runnable.class)) {
				
				value = invokableColumn.getValue(rowObjInstnace);
			}

			/*
			 * Behaviour
			 */

			editComponent.setEnabled(invokableColumn.editable());

			prepareEditor(source, editComponent, invokableColumn, rowObjInstnace, value);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return editComponent;
	}
}

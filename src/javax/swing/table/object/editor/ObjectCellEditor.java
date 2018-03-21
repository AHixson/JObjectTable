package javax.swing.table.object.editor;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.JObjectTable;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.object.InvokableColumn;

/**
 * 
 * @author Antony Hixson
 *
 */
public class ObjectCellEditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {

	private final JObjectTable<?> source;
	private final JInstantiator viewComponent;
	private final JInstantiator editComponent;
	private final List<RequestListener> requestListenerList;
	private String lastActionCommand;
	
	public ObjectCellEditor(JObjectTable<?> source) {
		this.source = source;
		
		this.viewComponent = new JInstantiator();
		this.editComponent = new JInstantiator();
		this.editComponent.addActionListener(this::stopCellEditing);
		this.requestListenerList = new ArrayList<>();
	}
	
	public boolean stopCellEditing(ActionEvent actionEvent) {
		
		boolean result = false;
		Component component = (Component) actionEvent.getSource();
		
		if (component.isEnabled()) {
			
			lastActionCommand = actionEvent.getActionCommand();
			
			result = stopCellEditing();
			
		} else {
			
			cancelCellEditing();
		}
		
		return result;
	}

	@Override
	public Object getCellEditorValue() {

		Object result = null;
		int editingRow = 0;
		int editingColumn = 0;
		InvokableColumn invocableColumn = null;

		if (lastActionCommand == null) {
			lastActionCommand = "";
		}

		editingRow = source.getEditingRow();
		editingColumn = source.getEditingColumn();

		switch (lastActionCommand) {

		case "CREATE":
			invocableColumn = source.getInvokableColumn(editingColumn);
			result = invocableColumn.newInstance();
			break;

		case "DELETE":
			break;

		default:
			result = source.getValueAt(editingRow, editingColumn);
			break;
		}

		if (lastActionCommand != null && !lastActionCommand.isEmpty()) {
			fireRequestReceived(lastActionCommand, editingRow, editingColumn, invocableColumn, result);
		}

		lastActionCommand = null;

		return result;
	}
	
	public final void fireRequestReceived(String request, int row, int column, InvokableColumn invocableColumn,
			Object instance) {
		requestListenerList.forEach(
				listener -> listener.requestRecieved(lastActionCommand, row, column, invocableColumn, instance));
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		
		value = source.getValueAt(row, column);
		
		viewComponent.setObjectValidMode(value != null);

		return viewComponent;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		
		value = source.getValueAt(row, column);
		
		editComponent.setObjectValidMode(value != null);

		return editComponent;
	}
	
	public void addRequestListener(RequestListener actionListener) {
		requestListenerList.add(actionListener);
	}
	
	public void removeRequestListener(RequestListener actionListener) {
		requestListenerList.remove(actionListener);
	}
	
	public List<RequestListener> getRequestListenerList() {
		return requestListenerList;
	}

	public static interface RequestListener {
		void requestRecieved(String request, int row, int column, InvokableColumn invocableColumn, Object instance);
	}
}

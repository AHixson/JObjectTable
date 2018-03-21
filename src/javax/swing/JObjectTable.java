package javax.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.cell.BoolCell;
import javax.swing.cell.ButtonCell;
import javax.swing.cell.NumberCell;
import javax.swing.cell.OptionCell;
import javax.swing.cell.TextCell;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.object.InvokableColumn;
import javax.swing.table.object.editor.ObjectCellEditor;
import javax.swing.table.object.editor.ObjectCellEditor.RequestListener;

/**
 * <p>{@link JTable} that supports generic types and objects.</p>
 * 
 * @author Antony Hixson
 *
 */
public class JObjectTable <T> extends JTable {
	
	private final Class<T> type;
	private final List<InvokableColumn> invocableColumns;
	private final String[] columnHeaders;
	
	/**
	 * Empty constructor
	 */
	public JObjectTable(Class<T> type) {
		
		if (type == null) {
			throw new NullPointerException("Type cannot be null");
		}
		
		this.type = type;
		this.invocableColumns = InvokableColumn.generateFor(type);
		this.columnHeaders = createHeaders();
		
		initialise();
	}
	
	@SuppressWarnings("unchecked")
	public JObjectTable(Collection<T> collection) {
		this((Class<T>) collection.iterator().next().getClass());
		collection.forEach(this::addValue);
	}
	
	@SuppressWarnings("unchecked")
	public JObjectTable(T[] values) {
		this((Class<T>) values.getClass().getComponentType());
		for (T aValue : values) {
			addValue(aValue);
		}
	}
	
	@SuppressWarnings("unchecked")
	public JObjectTable(T aValue) {
		this((Class<T>) aValue.getClass());
		addValue(aValue);
	}
	
	private final void initialise() {
		
		// 1. Set model
		setModel(new CustomModel());
		
		// 2. Remove first column from view
		removeColumn(getColumnModel().getColumn(0));
		
		// 3. Initialise editors for each of the columns
		List<TableColumn> columns = Collections.list(getColumnModel().getColumns());
		
		// 4. Assign each column with their respective invocable column
		for (int i = 0; i < columns.size(); i++) {
			columns.get(i).setIdentifier(invocableColumns.get(i));
		}
		
		columns.forEach(this::updateColumnEditorAndRenderer);
	}
	
	/**
	 * 
	 * <table>
	 * 	
	 * 	<tr>
	 * 		<th>Data type</th>
	 * 		<th>Cell type</th>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>boolean<br>{@link java.lang.Boolean}</td>
	 * 		<td></td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>byte<br>{@link java.lang.Byte}</td>
	 * 		<td rowspan="6">{@link NumberCell}</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>short<br>{@link java.lang.Short}</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>double<br>{@link java.lang.Double}</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>int<br>{@link java.lang.Integer}</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>long<br>{@link java.lang.Long}</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>float<br>{@link java.lang.Float}</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td>{@link java.lang.String}</td>
	 * 		<td>{@link TextCell}</td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td></td>
	 * 		<td></td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td></td>
	 * 		<td></td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td></td>
	 * 		<td></td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td></td>
	 * 		<td></td>
	 * 	</tr>
	 * 	<tr>
	 * 		<td></td>
	 * 		<td></td>
	 * 	</tr>
	 * 	
	 * </table>
	 * 
	 * 
	 * @param tableColumn
	 */
	@SuppressWarnings("unchecked")
	private <Cell extends TableCellEditor & TableCellRenderer> void updateColumnEditorAndRenderer(TableColumn tableColumn) {
		
		InvokableColumn ic = null;
		Class<?> type = null;
		Cell cell = null;
		
		if (tableColumn == null) {
			throw new NullPointerException();
		}
		
		ic = (InvokableColumn) tableColumn.getIdentifier();
		type = ic.getType();
		
		if (type.equals(boolean.class) || type.equals(Boolean.class)) {
			
			cell = (Cell) new BoolCell();
			
		} else if (type.equals(byte.class)
				|| type.equals(short.class)
				|| type.equals(int.class)
				|| type.equals(long.class)
				|| type.equals(float.class)
				|| type.equals(double.class)
				|| type.equals(Byte.class)
				|| type.equals(Short.class)
				|| type.equals(Integer.class)
				|| type.equals(Long.class)
				|| type.equals(Float.class)
				|| type.equals(Double.class)) {
			
			
			cell = (Cell) NumberCell.newInstance(ic);
			
		} else if (type.equals(String.class)) {
			
			cell = (Cell) new TextCell();
			
		} else if (type.isEnum()) {
			
			cell = (Cell) OptionCell.newInstance(type);
			
		} else if (type.isArray() || type.isInstance(Collection.class)) {
			
			
			
		} else if (type.equals(void.class) || type.equals(Runnable.class)) {
			
			cell = (Cell) new ButtonCell();
			
		} else {
			
			cell = (Cell) new ObjectCellEditor(this);
			
			((ObjectCellEditor) cell).addRequestListener(new RequestListener() {
				
				@Override
				public final void requestRecieved(String request, int row, int column, InvokableColumn invocableColumn, Object instance) {
					
					switch (request) {
					
					case "CREATE":
						break;
						
					case "EDIT":
						show(JObjectTable.this, instance.getClass(), new Object[] {instance});
						break;
						
					case "DELETE":
						break;
					
					}
				}
			});
		}
		
		if (cell != null) {
			tableColumn.setCellEditor(cell);
			tableColumn.setCellRenderer(cell);
		}
	}
	
	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		
		final InvokableColumn invokableColumn = getInvokableColumn(column);
		final Class<?> type = invokableColumn.getType();
		
		Component result = null;
		
		if (type.equals(void.class) || type.equals(Runnable.class)) {
			
			result = renderer.getTableCellRendererComponent(this, null, false, false, row, column);
			
		} else {
			
			result = super.prepareRenderer(renderer, row, column);
		}
		
		return result;
	}
	
	@Override
	public Component prepareEditor(TableCellEditor editor, int row, int column) {
		
		Component result = null;
		final InvokableColumn invokableColumn = getInvokableColumn(column);
		final Class<?> type = invokableColumn.getType();
		
		if (type.equals(void.class) || type.equals(Runnable.class)) {
			
			result = editor.getTableCellEditorComponent(this, null, false, row, column);
			
		} else {
			
			result = super.prepareEditor(editor, row, column);
		}
		
		return result;
	}
	
	
	
	
	
	
	

	/**
	 * Table's operating type
	 * 
	 * @return Generic type to work with
	 */
	public Class<T> getType() {
		return type;
	}
	
	@Override
	public DefaultTableModel getModel() {
		return (DefaultTableModel) super.getModel();
	}
	
	/**
	 * Override the Table's default value setter to prevent incompatible values
	 * being put into the table, or for values to be put in column.
	 */
	@Override
	public final void setValueAt(Object aValue, int row, int column) {
		
		InvokableColumn invokableColumn = null;
		Object rowObjInstance = null;
		boolean required = false;
		
		if (row < 0) {
			throw new IndexOutOfBoundsException("Row cannot be lesser than 0");
		} else if (row >= getRowCount()) {
			throw new IndexOutOfBoundsException("Row cannot be greater than the table row count");
		} else {
			
			invokableColumn = getInvokableColumn(column);
			required = invokableColumn.getColumn().required();

			if (required && aValue == null) {

				JOptionPane.showMessageDialog(this, "Please enter a value", "Error", JOptionPane.ERROR_MESSAGE);

			} else {

				rowObjInstance = getModel().getValueAt(row, 0);
				invokableColumn.setValue(rowObjInstance, aValue);
			}
		}
	}

	@Override
	public Object getValueAt(int row, int column) {
	
		Object value = null;
		Object rowObjInstance = null;
		InvokableColumn invokableColumn = null;

		if (row >= 0 && column >= 0) {
			
			rowObjInstance = getModel().getValueAt(row, 0);
			invokableColumn = getInvokableColumn(column);
			value = invokableColumn.getValue(rowObjInstance);
		}
	
		return value;
	}

	/**
	 * To simplify setting objects in the table.
	 * 
	 * @param aValue
	 *            - Value to add to table
	 * @param row
	 *            - Row to set the value at
	 */
	public void setValueAt(T aValue, int row) {
		super.setValueAt(aValue, row, 0);
	}

	/**
	 * Add value to table.
	 * 
	 * @param aValue
	 *            - any value
	 */
	public void addValue(T aValue) {
		
		if (aValue == null) {
			throw new NullPointerException("No value supplied");
		}
		
		getModel().addRow(new Object[] { aValue });
	}
	
	/**
	 * Remove value from table by index.
	 * 
	 * @param row
	 *            - Row index
	 */
	public void removeValue(int row) {
		
		int rowCount = getModel().getRowCount();
		
		if (row < 0 || row >= rowCount) {
			throw new IndexOutOfBoundsException("Invalid row index supplied (value=" + row + ", min=0, max=" + (rowCount - 1) + ")");
		}
		
		getModel().removeRow(row);
	}
	
	/**
	 * Remove value from table.
	 * 
	 * Note: All values!
	 * 
	 * @param aValue
	 *            - any value
	 */
	public void removeValue(T aValue) {
		
		Object firstValue = null;
		
		if (aValue == null) {
			throw new NullPointerException("No value supplied");
		}
		
		for (int i = getModel().getRowCount()  - 1; i >= 0; i--) {
			
			firstValue = getModel().getValueAt(i, 0);
			
			if (aValue.equals(firstValue)) {
				
				removeValue(i);
			}
		}
	}
	
	/**
	 * Get value stream from model.
	 * 
	 * @return Value stream
	 */
	@SuppressWarnings("unchecked")
	public final Stream<T> getValueStream() {
		return getModel().getDataVector().stream().map(data -> Vector.class.cast(data).elementAt(0));
	}
	
	/**
	 * Get values from model.
	 * 
	 * @return List of values.
	 */
	public final List<T> getValues() {
		return getValueStream().collect(Collectors.toList());
	}
	
	/**
	 * Create array containing column headers.
	 * 
	 * @return Array of column names.
	 */
	private final String[] createHeaders() {
		return invocableColumns.stream().map(InvokableColumn::name).toArray(String[]::new);
	}
	
	/**
	 * Get invokable column by column index.
	 * 
	 * @param column
	 *            - Column index (for view)
	 * @return InvokableColumn for that index
	 */
	public final InvokableColumn getInvokableColumn(int column) {
		return (InvokableColumn) getColumnModel().getColumn(column).getIdentifier();
	}

	/**
	 * Custom cell editors have a "normal" look and feel.
	 */
	@Override
	public final void editingStopped(ChangeEvent e) {
		
		TableCellEditor editor = getCellEditor();
		
		if (editor != null) {
			
			Object value = editor.getCellEditorValue();
			
			setValueAt(value, editingRow, editingColumn);
			
			removeEditor();
		}
	}
	
	/*
	 * STATIC METHODS
	 */
	
	/**
	 * "Un-shifts" an empty string to the start of a string array.
	 * 
	 * @param columnHeaders
	 *            - String array
	 * @return New array with a new first element
	 */
	private static final String[] insertHiddenColumn(String[] columnHeaders) {
		String[] result = null;
		result = new String[columnHeaders.length + 1];
		System.arraycopy(columnHeaders, 0, result, 1, columnHeaders.length);
		result[0] = "";
		return result;
	}

	/**
	 * Simplistic form to create a JObjectTable view quickly, and also used to
	 * provide recursive object exploration.
	 * 
	 * @param parent
	 *            - (optional) component
	 * @param type
	 *            - Class to extract column information from
	 * @param values
	 *            - Default values to supply into the form
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static final <T> JObjectTable<T> show(Component parent, Class<T> type, Object... values) {
		
		JObjectTable objectTable = new JObjectTable<>(type);
		objectTable.getTableHeader().setResizingAllowed(false);
		Arrays.asList(values).forEach(objectTable::addValue);
		
		JScrollPane scrollPane = new JScrollPane(objectTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.add(scrollPane, BorderLayout.CENTER);
		
		JOptionPane.showConfirmDialog(parent, panel, type.getName(),
				JOptionPane.CLOSED_OPTION, JOptionPane.PLAIN_MESSAGE);
		
		return (JObjectTable<T>) objectTable;
	}
	
	/*
	 * OTHER CLASSES
	 */
	
	public final class CustomModel extends DefaultTableModel {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		public CustomModel() {
			super(insertHiddenColumn(columnHeaders), 0);
		}
	}
	
}

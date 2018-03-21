package javax.swing.cell;

import javax.swing.JFormattedTextField;
import javax.swing.JObjectTable;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.object.Column;
import javax.swing.table.object.InvokableColumn;
import javax.xml.bind.TypeConstraintException;

/**
 * 
 * <p>Number cell editor and renderer that is represented as a spinner</p>
 * 
 * @author Antony Hixson
 *
 */
public class NumberCell extends AbstractCell<JSpinner, JSpinner> {
	
	/**
	 * Component rendered by default
	 */
	private final JSpinner viewComponent;
	
	/**
	 * Component rendered when cell is focused
	 */
	private final JSpinner editComponent;
	
	/**
	 * <p>
	 * Two separate models are needed to prevent the view and edit spinners
	 * conflicting.
	 * </p>
	 * <p>
	 * Method private to prevent non {@link SpinnerNumberModel} types.
	 * </p>
	 * 
	 * @param viewModel
	 *            - Model for view component
	 * @param editModel
	 *            - Model for edit component
	 */
	private NumberCell(SpinnerModel viewModel, SpinnerModel editModel) {
		
		if (viewModel == null || editModel == null) {
			throw new NullPointerException("Model cannot be null!");
		}
		
		this.viewComponent = new JSpinner(viewModel);
		this.editComponent = new JSpinner(editModel);
		
		this.viewComponent.setBorder(null);
		this.editComponent.setBorder(null);
		
		// Get the inner text field of the spinner and add action listener
		((JSpinner.DefaultEditor) this.editComponent.getEditor())
			.getTextField()
			.addActionListener(event -> stopCellEditing());
	}

	/**
	 * <p>
	 * Initialises two {@link SpinnerNumberModel} using {@link Number}.
	 * </p>
	 * <p>
	 * Method private to prevent inconsistent number types being passed in.
	 * </p>
	 * 
	 * @param value
	 *            - Starting value
	 * @param minimum
	 *            - Minimum value
	 * @param maximum
	 *            - Maximum value
	 * @param stepSize
	 *            - Value change amount
	 */
	private NumberCell(Number value, Comparable<? extends Number> minimum, Comparable<? extends Number> maximum,
			Number stepSize) {
		this(new SpinnerNumberModel(value, minimum, maximum, stepSize),
				new SpinnerNumberModel(value, minimum, maximum, stepSize));
	}
	
	/**
	 * Byte spinner.
	 * 
	 * @param value
	 *            - Starting value
	 * @param minimum
	 *            - Minimum value
	 * @param maximum
	 *            - Maximum value
	 * @param stepSize
	 *            - Value change amount
	 */
	public NumberCell(byte value, byte minimum, byte maximum, byte stepSize) {
		this((Byte) value, (Byte) minimum, (Byte) maximum, (Byte) stepSize);
	}

	/**
	 * Short spinner.
	 * 
	 * @param value
	 *            - Starting value
	 * @param minimum
	 *            - Minimum value
	 * @param maximum
	 *            - Maximum value
	 * @param stepSize
	 *            - Value change amount
	 */
	public NumberCell(short value, short minimum, short maximum, short stepSize) {
		this((Short) value, (Short) minimum, (Short) maximum, (Short) stepSize);
	}
	
	/**
	 * Integer spinner.
	 * 
	 * @param value
	 *            - Starting value
	 * @param minimum
	 *            - Minimum value
	 * @param maximum
	 *            - Maximum value
	 * @param stepSize
	 *            - Value change amount
	 */
	public NumberCell(int value, int minimum, int maximum, int stepSize) {
		this((Integer) value, (Integer) minimum, (Integer) maximum, (Integer) stepSize);
	}
	
	/**
	 * Long spinner.
	 * 
	 * @param value
	 *            - Starting value
	 * @param minimum
	 *            - Minimum value
	 * @param maximum
	 *            - Maximum value
	 * @param stepSize
	 *            - Value change amount
	 */
	public NumberCell(long value, long minimum, long maximum, long stepSize) {
		this((Long) value, (Long) minimum, (Long) maximum, (Long) stepSize);
	}
	
	/**
	 * Float spinner.
	 * 
	 * @param value
	 *            - Starting value
	 * @param minimum
	 *            - Minimum value
	 * @param maximum
	 *            - Maximum value
	 * @param stepSize
	 *            - Value change amount
	 */
	public NumberCell(float value, float minimum, float maximum, float stepSize) {
		this((Float) value, (Float) minimum, (Float) maximum, (Float) stepSize);
	}
	
	/**
	 * Double spinner.
	 * 
	 * @param value
	 *            - Starting value
	 * @param minimum
	 *            - Minimum value
	 * @param maximum
	 *            - Maximum value
	 * @param stepSize
	 *            - Value change amount
	 */
	public NumberCell(double value, double minimum, double maximum, double stepSize) {
		this((Double) value, (Double) minimum, (Double) maximum, (Double) stepSize);
	}
	
	@Override
	public Object getCellEditorValue() {
		return editComponent.getValue();
	}

	@Override
	public JSpinner getViewComponent() {
		return viewComponent;
	}

	@Override
	public JSpinner getEditComponent() {
		return editComponent;
	}

	@Override
	public void prepareRenderer(JObjectTable<?> table, JSpinner component, InvokableColumn invokableColumn,
			Object rowObj, Object iColumnValue) {

		component.setValue(iColumnValue);
	}

	@Override
	public void prepareEditor(JObjectTable<?> table, JSpinner component, InvokableColumn invokableColumn, Object rowObj,
			Object iColumnValue) {

		JFormattedTextField formattedTextField = ((DefaultEditor) component.getEditor()).getTextField();

		formattedTextField.setEditable(invokableColumn.editable());

		component.setValue(iColumnValue);
	}

	/**
	 * Generates new {@link NumberCell} by {@link InvokableColumn}.
	 * 
	 * @param invokableColumn
	 *            - {@link InvokableColumn}
	 * @return New {@link NumberCell}
	 * @throws NumberFormatException
	 *             One of the default/minimum/maximum/step size values in the
	 *             {@link Column} annotation is incorrectly formatted.
	 */
	public static final NumberCell newInstance(InvokableColumn invokableColumn) throws NumberFormatException {

		Column column = null;
		Number value = null;
		Comparable<? extends Number> minimum = null;
		Comparable<? extends Number> maximum = null;
		Number stepSize = null;
		
		if (invokableColumn == null) {
			throw new NullPointerException("InvokableColumn cannot be null");
		}
		
		column = invokableColumn.getColumn();

		switch (invokableColumn.getType().getName()) {

		case "byte":
		case "java.lang.Byte":
			value = (column.value().isEmpty() ? (byte) 0 : Byte.parseByte(column.value()));
			minimum = (column.min().isEmpty() ? Byte.MIN_VALUE : Byte.parseByte(column.min()));
			maximum = (column.max().isEmpty() ? Byte.MAX_VALUE : Byte.parseByte(column.max()));
			stepSize = (column.step().isEmpty() ? (byte) 1 : Byte.parseByte(column.step()));
			break;

		case "short":
		case "java.lang.Short":
			value = (column.value().isEmpty() ? (short) 0 : Short.parseShort(column.value()));
			minimum = (column.min().isEmpty() ? Short.MIN_VALUE : Short.parseShort(column.min()));
			maximum = (column.max().isEmpty() ? Short.MAX_VALUE : Short.parseShort(column.max()));
			stepSize = (column.step().isEmpty() ? (short) 1 : Short.parseShort(column.step()));
			break;

		case "int":
		case "java.lang.Integer":
			value = (column.value().isEmpty() ? 0 : Integer.parseInt(column.value()));
			minimum = (column.min().isEmpty() ? Integer.MIN_VALUE : Integer.parseInt(column.min()));
			maximum = (column.max().isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(column.max()));
			stepSize = (column.step().isEmpty() ? 1 : Integer.parseInt(column.step()));
			break;

		case "long":
		case "java.lang.Long":
			value = (column.value().isEmpty() ? 0L : Long.parseLong(column.value()));
			minimum = (column.min().isEmpty() ? Long.MIN_VALUE : Long.parseLong(column.min()));
			maximum = (column.max().isEmpty() ? Long.MAX_VALUE : Long.parseLong(column.max()));
			stepSize = (column.step().isEmpty() ? 1L : Long.parseLong(column.step()));
			break;

		case "float":
		case "java.lang.Float":
			value = (column.value().isEmpty() ? 0F : Float.parseFloat(column.value()));
			minimum = (column.min().isEmpty() ? -Float.MAX_VALUE : Float.parseFloat(column.min()));
			maximum = (column.max().isEmpty() ? Float.MAX_VALUE : Float.parseFloat(column.max()));
			stepSize = (column.step().isEmpty() ? 1F : Float.parseFloat(column.step()));
			break;

		// http://stackoverflow.com/questions/3884793/why-is-double-min-value-in-not-negative
		case "double":
		case "java.lang.Double":
			value = (column.value().isEmpty() ? 0D : Double.parseDouble(column.value()));
			minimum = (column.min().isEmpty() ? -Double.MAX_VALUE : Double.parseDouble(column.min()));
			maximum = (column.max().isEmpty() ? Double.MAX_VALUE : Double.parseDouble(column.max()));
			stepSize = (column.step().isEmpty() ? 1D : Double.parseDouble(column.step()));
			break;

		default:
			throw new TypeConstraintException("Class type [" + invokableColumn.getType() + "] is not type of Number.");
		}
		
		return new NumberCell(value, minimum, maximum, stepSize);
	}

}

package javax.swing.cell;

import java.awt.event.ActionEvent;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JButton;
import javax.swing.JObjectTable;
import javax.swing.table.object.InvokableColumn;

/**
 * 
 * <p>Button cell editor and renderer that is represented as a button</p>
 * 
 * @author Antony Hixson
 *
 */
public class ButtonCell extends AbstractCell<JButton, JButton> {
	
	private final JButton viewComponent;
	private final JButton editComponent;
	private final ThreadGroup concurrentPool;
	private final AtomicReference<InvokableColumn> arInvokableColumn;
	private final AtomicReference<Object> arRowObjInstance;
	
	public ButtonCell() {
		this.viewComponent = new JButton();
		this.editComponent = new JButton();
		this.editComponent.setEnabled(false);
		this.editComponent.setFocusable(false);
		this.editComponent.addActionListener(this::execute);
		this.concurrentPool = new ThreadGroup("Concurrent Runnables");
		this.arInvokableColumn = new AtomicReference<>();
		this.arRowObjInstance = new AtomicReference<>();
	}

	public void execute(ActionEvent event) {
		
		InvokableColumn invokableColumn = arInvokableColumn.get();
		Object rowObjInstance = arRowObjInstance.get();
		
		if (invokableColumn != null && rowObjInstance != null) {
			
			invokableColumn.execute(rowObjInstance);
		}
	}
	
	@Override
	public Object getCellEditorValue() {
		return null;
	}
	
	@Override
	public JButton getViewComponent() {
		return viewComponent;
	}
	
	public ThreadGroup getConcurrentPool() {
		return concurrentPool;
	}

	@Override
	public JButton getEditComponent() {
		return editComponent;
	}

	@Override
	public void prepareRenderer(JObjectTable<?> table, JButton component, InvokableColumn invokableColumn,
			Object rowObjInstance, Object value) {

		component.setText(invokableColumn.placeholder());
	}

	@Override
	public void prepareEditor(JObjectTable<?> table, JButton component, InvokableColumn invokableColumn,
			Object rowObjInstance, Object value) {

		component.setText(invokableColumn.placeholder());

		arInvokableColumn.set(invokableColumn);
		arRowObjInstance.set(rowObjInstance);
	}

}

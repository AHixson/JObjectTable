package javax.swing.table.object;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import java.util.Map.Entry;

/**
 * 
 * <p>
 * Bridges the Swing table to the virtual objects.
 * </p>
 * 
 * @author Antony Hixson
 *
 */
public class InvokableColumn {
	
	private final Column column;
	private final Field field;
	private final Method getterMethod;
	private final Method setterMethod;
	private final Class<?> type;
	private final boolean isParameterlessConstructorPresent;

	private InvokableColumn(Column column, Field field, Method getterMethod, Method setterMethod) {
		
		this.column = column;
		this.field = field;
		this.getterMethod = getterMethod;
		this.setterMethod = setterMethod;
		this.type = extractColumnType();
		this.isParameterlessConstructorPresent = hasParameterlessConstructor(type);
	}
	
	@Override
	public final String toString() {
		return String.format(
				"{\n\t\"column\":\"%s\",\n\t\"field\":\"%s\",\n\t\"getterMethod\":\"%s\",\n\t\"setterMethod\":\"%s\"\n}",
				column, field, getterMethod, setterMethod);
	}
	
	public final Column getColumn() {
		return column;
	}
	
	protected final Field getField() {
		return field;
	}
	
	protected final Method getGetterMethod() {
		return getterMethod;
	}
	
	protected final Method getSetterMethod() {
		return setterMethod;
	}
	
	/*
	 * Column delegate methods
	 */
	
	public String name() {
		return column.name();
	}

	public int index() {
		return column.index();
	}

	public boolean editable() {
		return column.editable();
	}

	public boolean required() {
		return column.required();
	}

	public boolean unique() {
		return column.unique();
	}

	public String placeholder() {
		return column.placeholder();
	}

	public String tooltip() {
		return column.tooltip();
	}

	public String value() {
		return column.value();
	}

	public String min() {
		return column.min();
	}

	public String max() {
		return column.max();
	}

	public String step() {
		return column.step();
	}

	public boolean enabled() {
		return column.enabled();
	}

	public boolean concurrent() {
		return column.concurrent();
	}

	/*
	 * Other methods
	 */
	
	public final Class<?> getType() {
		return type;
	}
	
	public final boolean isParameterlessConstructorPresent() {
		return isParameterlessConstructorPresent;
	}
	
	public final boolean isSetable() {
		return field != null || setterMethod != null;
	}
	
	public final Object newInstance() {
		Object result = null;
		try {
			result = type.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean execute(Object instance) {
		
		boolean b = false;
		Class<?> type = null;
		boolean concurrent = false;
		Object value = null;
		Runnable runnable = null;
		
		try {
			
			if (instance == null) {
				throw new NullPointerException("Instance must be defined");
			}
			
			type = getType();
			
			if (type.equals(void.class)) {
				
				getterMethod.invoke(instance);
				
			} else if (type.equals(Runnable.class)) {
				
				if (field != null) {
					
					value = field.get(instance);
					
				} else {
					
					throw new NullPointerException("Runnable variable must have a @Column annotation");
				}

				concurrent = getColumn().concurrent();

				runnable = (Runnable) value;

				if (concurrent) {
					
					new Thread(runnable).start();
					
				} else {
					
					runnable.run();
				}
			}
			
			b = true;
			
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			
			e.printStackTrace();
		}
		
		return b;
	}
	
	public Object getValue(Object instance) {

		Object value = null;

		if (instance != null) {

			if (getterMethod != null) {

				try {

					value = getterMethod.invoke(instance);

				} catch (Exception e) {

					e.printStackTrace();
				}

			} else if (field != null) {

				try {

					value = field.get(instance);

				} catch (Exception e) {

					e.printStackTrace();
				}
			}
		}

		return value;
	}

	public boolean setValue(Object instance, Object value) {

		boolean b = false;

		if (instance != null) {

			if (setterMethod != null) {

				try {

					setterMethod.invoke(instance, value);
					
					b = true;

				} catch (Exception e) {

					e.printStackTrace();
				}

			} else if (field != null) {

				try {

					field.set(instance, value);

					b = true;

				} catch (Exception e) {

					e.printStackTrace();
				}
			}
		}

		return b;
	}
	
	private final Class<?> extractColumnType() {
		Class<?> result = null;
		if (field != null) {
			result = field.getType();
		} else if (getterMethod != null) {
			result = getterMethod.getReturnType();
		} else if (setterMethod != null) {
			result = setterMethod.getParameterTypes()[0];
		} else {
			throw new RuntimeException("No class type for InvocableColumn: " + column.toString());
		}
		return result;
	}

	/**
	 * Get {@link Column} for all declared {@link Field}.
	 * 
	 * @param type
	 *            - Class type to examine
	 * @return Map of {@link Column} and {@link Field}
	 */
	private final static Map<Column, Field> extractColumnsForDeclaredFields(Class<?> type) {

		Map<Column, Field> results = new HashMap<>();

		if (type == null) {
			throw new NullPointerException("Class type cannot be null");
		} else {
			for (Field field : type.getDeclaredFields()) {
				if (field.isAnnotationPresent(Column.class)) {
					field.setAccessible(true);
					results.put(field.getAnnotation(Column.class), field);
				}
			}
		}

		return results;
	}
	
	/**
	 * Get {@link Column} for all declared {@link Method}.
	 * 
	 * @param type
	 *            - Class type to examine
	 * @return Map of {@link Column} and {@link Method}
	 */
	private final static Map<Column, Method> extractColumnsForMethods(Class<?> type) {
		
		Map<Column, Method> results = new HashMap<>();
		
		if (type == null) {
			throw new NullPointerException("Class type cannot be null");
		} else {
			for (Method method : type.getMethods()) {
				if (method.isAnnotationPresent(Column.class)) {
					method.setAccessible(true);
					results.put(method.getAnnotation(Column.class), method);
				}
			}
		}
		
		return results;
	}

	/**
	 * Get {@link ColumnSetter} for all declared {@link Method}.
	 * 
	 * @param type
	 *            - Class type to examine
	 * @return Map of {@link ColumnSetter} and {@link Method}
	 */
	private final static Map<ColumnSetter, Method> extractColumnSettersForMethods(Class<?> type) {
		
		Map<ColumnSetter, Method> results = new HashMap<>();
		
		if (type == null) {
			throw new NullPointerException("Class type cannot be null");
		} else {
			for (Method method : type.getMethods()) {
				if (method.isAnnotationPresent(ColumnSetter.class)) {
					method.setAccessible(true);
					results.put(method.getAnnotation(ColumnSetter.class), method);
				}
			}
		}
		
		return results;
	}
	
	/**
	 * Group variables and methods by {@link Column} and {@link ColumnSetter}
	 * (by column name).
	 * 
	 * <p>
	 * TODO Validation - Prevent multiple variables/methods being assigned the
	 * same column names for the same operations. Attempts to achieve this using
	 * Stream.length() closed the stream.
	 * </p>
	 * <p>
	 * TODO Validation - Ensure the 'setterMethod' has one argument and is of
	 * the same type as the variable/getter method.
	 * </p>
	 * 
	 * @param type
	 *            - Class type to examine
	 * @return List of groupings
	 */
	public static final List<InvokableColumn> generateFor(Class<?> type) {

		List<InvokableColumn> results = new ArrayList<>();
		Set<String> columnNames = null;
		Column column = null;
		Field field = null;
		Method getterMethod = null;
		Method setterMethod = null;
		InvokableColumn mirrorColumn = null;

		Map<Column, Field> columnFieldMap = null; // direct get/set
		Map<Column, Method> columnMethodMap = null; // getters
		Map<ColumnSetter, Method> columnSetterMethodMap = null; // setter
		
		Stream<Entry<Column, Field>> columnFieldMapStream = null;
		Stream<Entry<Column, Method>> columnMethodMapStream = null;
		Stream<Entry<ColumnSetter, Method>> columnSetterMethodMapStream = null;
		
		Entry<Column, Field> columnFieldEntry = null;
		Entry<Column, Method> columnMethodEntry = null;
		Entry<ColumnSetter, Method> columnSetterMethodEntry = null;
		
		if (type == null) {
			throw new NullPointerException("Class type cannot be null");
		} else {

			columnFieldMap = extractColumnsForDeclaredFields(type);
			columnMethodMap = extractColumnsForMethods(type);

			if (columnFieldMap.isEmpty() && columnMethodMap.isEmpty()) {
				// nothing to present
				throw new IllegalStateException(
						"No column annotations have been assigned to members in " + type.toString());
			}

			columnSetterMethodMap = extractColumnSettersForMethods(type);

			// Create a map of gettable columns
			columnNames = new HashSet<>();

			for (Column nextColumn : columnFieldMap.keySet()) {
				columnNames.add(nextColumn.name());
			}

			for (Column nextColumn : columnMethodMap.keySet()) {
				columnNames.add(nextColumn.name());
			}

			// Group field, getter method and setter method by column names
			for (String columnName : columnNames) {

				// Reset
				column = null;
				field = null;
				getterMethod = null;
				setterMethod = null;
				mirrorColumn = null;

				// cannot validate to error on duplicates, because Stream would
				// be acted upon and subsequently closed
				columnFieldMapStream = columnFieldMap.entrySet().stream()
						.filter(entry -> entry.getKey().name().equals(columnName));

				columnMethodMapStream = columnMethodMap.entrySet().stream()
						.filter(entry -> entry.getKey().name().equals(columnName));

				columnSetterMethodMapStream = columnSetterMethodMap.entrySet().stream()
						.filter(entry -> entry.getKey().name().equals(columnName));

				// Group the findings
				columnFieldEntry = columnFieldMapStream.findFirst().orElse(null);
				columnMethodEntry = columnMethodMapStream.findFirst().orElse(null);
				columnSetterMethodEntry = columnSetterMethodMapStream.findFirst().orElse(null);

				assert (columnFieldEntry != null || columnMethodEntry != null);

				// Column annotation from 'getter' method has higher priority
				// than Column annotation from field
				if (columnMethodEntry != null) {
					column = columnMethodEntry.getKey();
					getterMethod = columnMethodEntry.getValue();
				} else if (columnFieldEntry != null) {
					column = columnFieldEntry.getKey();
					field = columnFieldEntry.getValue();
				}

				if (columnSetterMethodEntry != null) {
					setterMethod = columnSetterMethodEntry.getValue();
				}

				mirrorColumn = new InvokableColumn(column, field, getterMethod, setterMethod);

				results.add(mirrorColumn);
			}
			
			Collections.reverse(results);
		}

		return results;
	}
	
	public static final boolean hasParameterlessConstructor(Class<?> clazz) {
		return Stream.of(clazz.getConstructors()).anyMatch((c) -> c.getParameterCount() == 0);
	}
}

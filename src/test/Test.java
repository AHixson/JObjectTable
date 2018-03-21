package test;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JObjectTable;
import javax.swing.JScrollPane;

public class Test {

	private static JFrame frame;
	private static JObjectTable<Employee> table;

	public static void main(String[] args) {
		createUI();
		addContentToTable();
	}

	private static void createUI() {
		table = new JObjectTable<>(Employee.class);
		frame = new JFrame("JObjectTable<Employee>");
		frame.add(new JScrollPane(table), BorderLayout.CENTER);
		frame.add(table.getTableHeader(), BorderLayout.NORTH);
		frame.setSize(512, 512);
		frame.setVisible(true);
	}

	private static void addContentToTable() {
		table.addValue(new Employee("Bob", true, System.currentTimeMillis(), 23, Employee.Occupation.IT_DEVELOPER));
		table.addValue(new Employee("Cindy", true, System.currentTimeMillis() - (1000 * 60 * 60 * 24 * 5), 19, Employee.Occupation.DIRECTOR));
		table.addValue(new Employee("Ralph", true, System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 5), 18, Employee.Occupation.IT_SUPPORT));
		table.addValue(new Employee("Mona", true, System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 15), 26, Employee.Occupation.IT_PROJECT_MANAGER));
	}
}

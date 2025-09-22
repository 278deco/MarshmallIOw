package marshmalliow.core.file.csv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class CSVDocument {

	private List<List<CSVCell>> fileContent;
	private final AtomicBoolean contentModified = new AtomicBoolean(false);
	
	public CSVDocument() {
		this.fileContent = new ArrayList<>();
	}
	
	public CSVDocument(List<List<CSVCell>> fileContent) {
		this.fileContent = fileContent;
	}

	public CSVCell getCell(int row, int column) {
		if(this.fileContent.size() < row) return null;
		
		final List<CSVCell> rowList = this.fileContent.get(row);
		if(rowList.size() < column) return null;
		
		return rowList.get(column);
	}
	
	public List<CSVCell> getColumn(int column) {
		if(this.fileContent.isEmpty()) return Collections.emptyList();
		if(this.fileContent.get(0).size() < column) return Collections.emptyList();
		
		final List<CSVCell> result = new ArrayList<>();
		this.fileContent.forEach(row -> result.add(row.get(column)));
		return result;
	}
	
	public List<CSVCell> getRow(int row) {
		if (this.fileContent.size() < row)
			return Collections.emptyList();

		return Collections.unmodifiableList(this.fileContent.get(row));
	}
	
	/**
	 * Set a cell at a specific position in the CSV Document
	 * @param cell the new cell to set
	 * @param row the row index
	 * @param column the column index
	 * @return true if the cell has been set, false otherwise (if the row or column does not exist)
	 */
	public boolean setCell(CSVCell cell, int row, int column) {
		// If the row does not exist, return false
		if(row >= this.fileContent.size()) return false;
		
		final List<CSVCell> rowList = this.fileContent.get(row);
		
		// If the column does not exist, return false
		if(column >= rowList.size()) return false;
		
		final boolean modified = !rowList.set(column, cell).equals(cell);
		if(modified) this.contentModified.set(true);
		
		return modified;
	}
	
	/**
	 * Set a whole row at a specific position in the CSV Document
	 * @param rowList the new row to set
	 * @param row the row index
	 * @return true if the row has been set, false otherwise (if the row does not exist)
	 */
	public boolean setRow(List<CSVCell> rowList, int row) {
		if(row >= this.fileContent.size()) return false;
		
		final boolean modified = !this.fileContent.set(row, rowList).equals(rowList);
		if(modified) this.contentModified.set(true);
		
		return modified;
	}
	
	/**
	 * Set a whole column at a specific position in the CSV Document
	 * @param columnList the new column to set
	 * @param column the column index
	 * @return true if the column has been set, false otherwise (if the column does not exist or if the size of the column does not match the number of rows)
	 */
	public boolean setColumn(List<CSVCell> columnList, int column) {
		if (this.fileContent.isEmpty())
			return false;
		if (column >= this.fileContent.get(0).size())
			return false;
		if (this.fileContent.size() != columnList.size())
			return false;
		
		this.contentModified.set(true);
		for (int i = 0; i < this.fileContent.size(); i++) {
			this.fileContent.get(i).set(column, columnList.get(i));
		}

		return true;
	}
	
	/**
	 * Add a cell at the end of a specific row
	 * @param cell the cell to add
	 * @param row the row index
	 * @return true if the cell has been added, false otherwise (if the row does not exist)
	 */
	public boolean addCell(CSVCell cell, int row) {
		if (row >= this.fileContent.size())
			return false;

		this.contentModified.set(true);
		return this.fileContent.get(row).add(cell);
	}
	
	/**
	 * Add a whole row at the end of the CSV Document
	 * 
	 * @param rowList the row to add
	 * @return true if the row has been added, false otherwise
	 */
	public boolean addRow(List<CSVCell> rowList) {
		this.contentModified.set(true);
		return this.fileContent.add(rowList);
	}
	
	/**
	 * Add a whole column at the end of the CSV Document
	 * 
	 * @param columnList the column to add
	 * @return true if the column has been added, false otherwise (if the size of
	 *         the column does not match the number of rows)
	 */
	public boolean addColumn(List<CSVCell> columnList) {
		if (this.fileContent.isEmpty()) {
			this.contentModified.set(true);
			for (CSVCell cell : columnList) {
				final List<CSVCell> newRow = new ArrayList<>();
				newRow.add(cell);
				this.fileContent.add(newRow);
			}
			return true;
		}
		if (this.fileContent.size() != columnList.size())
			return false;

		this.contentModified.set(true);
		for (int i = 0; i < this.fileContent.size(); i++) {
			this.fileContent.get(i).add(columnList.get(i));
		}

		return true;
	}
	
	/**
	 * Get the number of rows in the CSV Document
	 * 
	 * @return the number of rows in the CSV Document
	 */
	public int getRowCount() {
		return this.fileContent.size();
	}
	
	/**
	 * Get the number of columns in the CSV Document<br/>
	 * Because a CSV file can't have rows with different number of columns, this method returns the number of columns of the first row.<br/>
	 * If the document is empty, this method returns 0.
	 * 
	 * @return the number of columns in the CSV Document
	 */
	public int getColumnCount() {
		return this.fileContent.isEmpty() ? 0 : this.fileContent.get(0).size();
	}
	
	/**
	 * Sort a CSV Document by choosing a sorting column and rearrange the rows depending on the type of comparison defined
	 * @param columnIndex The index of the column to be used for sorting
	 * @param comparatorKey A function that extract the comparable key from a CSVCell
	 * @see SortComparaison
	 */
	public <U extends Comparable<? super U>> void sortByColumnSelection(int columnIndex, Function<CSVCell, U> comparatorKey) {
		final List<List<CSVCell>> result = new ArrayList<>();
		final List<CSVCell> sortingCol = getColumn(columnIndex);
		
		Collections.sort(sortingCol, Comparator.comparing(comparatorKey));
		
		sortingCol.forEach(cell -> {
			for (List<CSVCell> row : this.fileContent) {
				if (row.contains(cell) && !result.contains(row)) {
					result.add(row);
					break;
				}
			}
		});
		
		if(!this.fileContent.equals(result)) this.contentModified.set(true);
		this.fileContent = result;
	}
	
	public static class SortComparaison {
		
		public static double sortDouble(CSVCell cell) {
			String value = cell.get();
			return (value = value.replaceAll("[^\\d.+]", "")).isEmpty() ? 0 : Double.parseDouble(value);
		}
		
		public static float sortFloat(CSVCell cell) {
			String value = cell.get();
			return (value = value.replaceAll("[^\\d.+]", "")).isEmpty() ? 0 : Float.parseFloat(value);
		}
		
		public static int sortInteger(CSVCell cell) {
			String value = cell.get();
			return (value = value.replaceAll("[^\\d+]", "")).isEmpty() ? 0 : Integer.parseInt(value);
		}
		
		public static long sortLong(CSVCell cell) {
			String value = cell.get();
			return (value = value.replaceAll("[^\\d+]", "")).isEmpty() ? 0 : Long.parseLong(value);
		}
		
		public static String sortString(CSVCell cell) {
			return cell.get();
		}
		
		public static boolean sortBoolean(CSVCell cell) {
			return Boolean.parseBoolean(cell.get());
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof CSVDocument)) return false;
		final CSVDocument document = (CSVDocument) obj;
		return document.fileContent.equals(this.fileContent);
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for(int y = 0; y < this.fileContent.size(); y++) {
			for(int x = 0; x < this.fileContent.get(y).size(); x++) {
				sb.append(x == 0 ? "| " : "").append(this.fileContent.get(y).get(x)).append(x+1 == this.fileContent.get(y).size() ? " |" : ", ");
			}
			sb.append("\n");
		}
		
		return sb.toString();
	}
	
	public void resetModified() {
		this.contentModified.set(false);		
	}
	
	public boolean isModified() {
		return this.contentModified.get();
	}
	
}

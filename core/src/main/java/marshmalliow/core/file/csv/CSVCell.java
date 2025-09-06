package marshmalliow.core.file.csv;

public class CSVCell implements Comparable<CSVCell> {

	private String content;
	private int index;
	
	public CSVCell(final int index) {
		this.index = index;
		this.content = "NaN";
	}
	
	public CSVCell(final int index, final String content) {
		this.index = index;
		this.content = content.trim();
	}
	
	public CSVCell(final int index, final int content) {
		this.index = index;
		this.content = (""+content).trim();
	}
	
	public CSVCell(final int index, final long content) {
		this.index = index;
		this.content = (""+content).trim();
	}
	
	public CSVCell(final int index, final boolean content) {
		this.index = index;
		this.content = (""+content).trim();
	}
	
	public int formatInteger() throws NumberFormatException {
		return Integer.parseInt(this.content);
	}
	
	public long formatLong() throws NumberFormatException {
		return Long.parseLong(this.content);
	}
	
	public double formatDouble() throws NumberFormatException {
		return Double.parseDouble(this.content);
	}
	
	public float formatFloat() throws NumberFormatException {
		return Float.parseFloat(this.content);
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getContent() {
		return content;
	}
	
	public int getSize() {
		return this.content.length();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof CSVCell && ((CSVCell)obj).content.equals(this.content);
	}

	@Override
	public int compareTo(CSVCell o) {
		return this.getContent().compareTo(o.getContent());
	}
	
}

package marshmalliow.core.file.csv;

public class CSVCell implements Comparable<CSVCell> {
	
	private int posX, posY;
	
	private String content;
	
	public CSVCell(int posX, int posY) {
		this.posX = posX;
		this.posY = posY;
		this.content = null;
	}
	
	public CSVCell(int posX, int posY, String content) {
		this.posX = posX;
		this.posY = posY;
		this.content = content;
	}
	
	public CSVCell(int posX, int posY, int content) {
		this(posX, posY, String.valueOf(content));
	}
	
	public CSVCell(int posX, int posY, long content) {
		this(posX, posY, String.valueOf(content));
	}
	
	public CSVCell(int posX, int posY, float content) {
		this(posX, posY, String.valueOf(content));
	}
	
	public CSVCell(int posX, int posY, double content) {
		this(posX, posY, String.valueOf(content));
	}
	
	public CSVCell(int posX, int posY, boolean content) {
		this(posX, posY, String.valueOf(content));
	}
	
	public CSVCell(int posX, int posY, Object content) {
		this(posX, posY, String.valueOf(content));
	}
	
	public String get() {
		return this.content;
	}
	
	public int getAsInt() throws NumberFormatException {
		if(this.content == null) throw new NumberFormatException("Content is null");
		return Integer.parseInt(this.content);
	}
	
	public long getAsLong() throws NumberFormatException {
		if(this.content == null) throw new NumberFormatException("Content is null");
		return Long.parseLong(this.content);
	}
	
	public double getAsDouble() throws NumberFormatException {
		if(this.content == null) throw new NumberFormatException("Content is null");
		return Double.parseDouble(this.content);
	}
	
	public float getAsFloat() throws NumberFormatException {
		if(this.content == null) throw new NumberFormatException("Content is null");
		return Float.parseFloat(this.content);
	}
	
	public boolean getAsBoolean() throws NullPointerException {
		if(this.content == null) throw new NullPointerException("Content is null");
		return Boolean.parseBoolean(this.content);
	}
	
	public int getPosX() {
		return this.posX;
	}
	
	public int getPosY() {
		return this.posY;
	}
	
	public int getSize() {
		return this.content.length();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof CSVCell)) return false;
        final CSVCell cell = (CSVCell) obj;
        return cell.content.equals(this.content) && cell.posX == this.posX && cell.posY == this.posY;
	}

	@Override
	public int compareTo(CSVCell o) {
		return this.get().compareTo(o.get());
	}
	
	@Override
	public String toString() {
		return "[X="+posX+",Y="+posY+"] "+content;
	}
	
}

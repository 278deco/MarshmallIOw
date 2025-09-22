package marshmalliow.core.io;

import java.io.IOException;
import java.io.Writer;

import marshmalliow.core.file.csv.CSVDocument;
import marshmalliow.core.file.csv.CSVProperties;

/**
 * CSVWriter purpose is to write a CSVDocument (the root of a CSV file) with a specific {@link Writer}<br/>
 * <em>See RFC 4180.</em>
 * @author 278deco
 * @version 1.0.0
 */
public class CSVWriter {

	protected CSVDocument source;
    protected CSVProperties properties;
	/**
	 * Constructor of {@link CSVWriter}
	 * @param source The root of a CSV file (represented as a {@link CSVDocument})
	 * @param properties The properties used to write the CSV file
	 * @see CSVProperties
	 */
	public CSVWriter(final CSVDocument source, final CSVProperties properties) {
		this.source = source;
		this.properties = properties;
	}
	
	/**
	 * Write the content of the CSV file with a specific {@link Writer}<br/>
	 * @param writer The writer used to write down the container
	 * @throws IOException
	 */
	public void write(Writer writer) throws IOException {
        for(int y = 0; y < this.source.getRowCount(); y++) {
			for (int x = 0; x < this.source.getRow(y).size(); x++) {
				final String cellContent = this.source.getCell(y, x) != null ? this.source.getCell(y, x).get() : "";

				final boolean mustEscape = cellContent.indexOf(this.properties.getFieldSeparator()) != -1
						|| cellContent.indexOf('\"') != -1 || cellContent.indexOf('\n') != -1 
						|| cellContent.indexOf('\r') != -1;

				if (mustEscape) {
					writer.write("\"" + cellContent.replace("\"", "\"\"") + "\"");
				} else {
					writer.write(cellContent);
				}

				// Write field separator if not the last column
				if (x < this.source.getRow(y).size() - 1) {
					writer.write(this.properties.getFieldSeparator());
				}
			}
			// Write new line if not the last row
			if (y < this.source.getRowCount() - 1) {
				writer.write("\r\n");
			}
        }
    }
	
}

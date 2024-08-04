package marshmalliow.core.binary.data.types.advanced;

import java.io.IOException;
import java.time.ZonedDateTime;

import marshmalliow.core.binary.data.types.DataType;
import marshmalliow.core.binary.registry.DataTypeEnum;
import marshmalliow.core.binary.registry.DataTypeRegistry;
import marshmalliow.core.binary.utils.Charset;
import marshmalliow.core.io.BinaryReader;
import marshmalliow.core.io.BinaryWriter;
import marshmalliow.core.objects.DateTime;

/**
 * Datetime data type corresponding to the format specification.
 * @version	1.0.0
 * @author 278deco
 */
public class DatetimeDataType extends DataType<DateTime> {

	public DatetimeDataType() {
		super();
	}

	public DatetimeDataType(String name) {
		super(name, DateTime.fromZonedDateTime(ZonedDateTime.now()));
	}

	public DatetimeDataType(String name, DateTime value) {
		super(name, value);
	}

	@Override
	public void writeValue(BinaryWriter writer, DataTypeRegistry registry, Charset charset) throws IOException {
		writer.writeDatetime(this.getValue());
	}

	@Override
	public void readValue(BinaryReader reader, DataTypeRegistry registry, Charset charset) throws IOException {
		this.setValue(reader.readDatetime());
	}

	@Override
	public byte getId() {
		return DataTypeEnum.DATETIME.getId();
	}
	
	@Override
	public Category getCategory() {
		return Category.ADVANCED;
	}

}

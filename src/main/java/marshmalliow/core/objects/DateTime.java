package marshmalliow.core.objects;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Optional;

import marshmalliow.core.binary.utils.ComplementaryType;

public class DateTime {

	public static final int FIELDS_NUMBER = 9;

	/**
	 * Date part with:
	 * <ul>
	 *	<li>The year as {@code short}</li>
	 *	<li>The month as {@code byte}</li>
	 *	<li>The dayOfMonth as {@code byte}</li>
	 * </ul>
	 */
	private int year;
	private byte month;
	private byte dayOfMonth;

	/**
	 * Time part with:
	 * <ul>
	 *	<li>The hour as {@code byte}</li>
	 *	<li>The minute as {@code byte}</li>
	 *	<li>The second as {@code byte}</li>
	 * </ul>
	 */
	private byte hour;
	private byte minute;
	private byte second;

	/**
	 * Complementary part with:
	 * <ul>
	 *	<li>The complementary type as {@code byte}</li>
	 *	<li>The complementary data as {@code short}</li>
	 * </ul>
	 * Complementary represent either milliseconds, microseconds or nanoseconds
	 */
	private ComplementaryType complementaryType;
	private short complementary;

	private ZoneId timezone;
	private ZoneOffset timezoneOffset;

	private DateTime(Builder builder) {
		this.year = builder.year;
		this.month = builder.month;
		this.dayOfMonth = builder.dayOfMonth;
		this.hour = builder.hour;
		this.minute = builder.minute;
		this.second = builder.second;
		this.complementaryType = builder.complementaryType;
		this.complementary = builder.complementary;
		this.timezone = builder.timezone;
		this.timezoneOffset = builder.timezoneOffset;
	}

	public Optional<Integer> getYear() {
		return this.year != -1 ? Optional.of(this.year) : Optional.empty();
	}

	public boolean isYearPresent() {
		return this.year != -1;
	}

	public Optional<Byte> getMonth() {
		return this.month != -1 ? Optional.of(this.month) : Optional.empty();
	}

	public boolean isMonthPresent() {
		return this.month != -1;
	}

	public Optional<Byte> getDayOfMonth() {
		return this.dayOfMonth != -1 ? Optional.of(this.dayOfMonth) : Optional.empty();
	}

	public boolean isDayOfMonthPresent() {
		return this.dayOfMonth != -1;
	}

	public Optional<Byte> getHour() {
		return this.hour != -1 ? Optional.of(this.hour) : Optional.empty();
	}

	public boolean isHourPresent() {
		return this.hour != -1;
	}

	public Optional<Byte> getMinute() {
		return this.minute != -1 ? Optional.of(this.minute) : Optional.empty();
	}

	public boolean isMinutePresent() {
		return this.minute != -1;
	}

	public Optional<Byte> getSecond() {
		return this.second != -1 ? Optional.of(this.second) : Optional.empty();
	}

	public boolean isSecondPresent() {
		return this.second != -1;
	}

	public Optional<ComplementaryType> getComplementaryType() {
		return Optional.ofNullable(this.complementaryType);
	}

	public Optional<Short> getComplementaryValue() {
		return this.complementary!= -1 ? Optional.of(this.complementary) : Optional.empty();
	}

	public boolean isComplementaryPresent() {
		return this.complementaryType != null && this.complementary != -1;
	}

	public Optional<ZoneId> getTimezone() {
		return Optional.ofNullable(this.timezone);
	}

	public Optional<ZoneOffset> getZoneOffset() {
		return Optional.ofNullable(this.timezoneOffset);
	}

	public boolean isTimezonePresent() {
		return this.timezone != null && this.timezoneOffset != null;
	}

	public Optional<LocalDate> asLocalDate() {
		if(this.year != -1 && this.month != -1 && this.dayOfMonth != -1) {
			return Optional.of(LocalDate.of(this.year, this.month, this.dayOfMonth));
		}else {
			return Optional.empty();
		}
	}

	public Optional<LocalTime> asLocalTime() {
		if(this.hour != -1 && this.minute != -1 && this.second != -1) {
			if(this.complementaryType != null && this.complementary != -1) {
				return Optional.of(LocalTime.of(this.hour, this.minute, this.second, this.complementary * this.complementaryType.getMultiplierFromNanos()));
			}else {
				return Optional.of(LocalTime.of( this.hour, this.minute, this.second));
			}
		}else {
			return Optional.empty();
		}
	}

	public Optional<LocalDateTime> asLocalDateTime() {
		if(this.year != -1 && this.month != -1 && this.dayOfMonth != -1 && this.hour != -1 && this.minute != -1 && this.second != -1) {
			if(this.complementaryType != null && this.complementary != -1) {
				return Optional.of(LocalDateTime.of(this.year, this.month, this.dayOfMonth, this.hour, this.minute, this.second, this.complementary * this.complementaryType.getMultiplierFromNanos()));
			}else {
				return Optional.of(LocalDateTime.of(this.year, this.month, this.dayOfMonth, this.hour, this.minute, this.second));
			}
		}else {
			return Optional.empty();
		}
	}

	public Optional<ZonedDateTime> asZonedDateTime() {
		final Optional<LocalDateTime> ldt = this.asLocalDateTime();
		if(ldt.isPresent() && this.timezone != null) {
			return Optional.of(ZonedDateTime.of(ldt.get(), this.timezone));
		}else {
			return Optional.empty();
		}
	}

	public static DateTime.Builder builder() {
		return new DateTime.Builder();
	}

	public static final DateTime fromLocalDate(LocalDate date) {
		return DateTime.builder()
				.year(date.getYear())
				.month((byte)date.getMonthValue())
				.dayOfMonth((byte)date.getDayOfMonth())
				.build();
	}

	public static final DateTime fromLocalTime(LocalTime time) {
		return DateTime.builder()
				.hour((byte)time.getHour())
				.minute((byte)time.getMinute())
				.second((byte)time.getSecond())
				.build();
	}

	public static final DateTime fromLocalDateTime(LocalDateTime dt) {
		return fromLocalDateTimeBuilder(dt).build();
	}

	private static final DateTime.Builder fromLocalDateTimeBuilder(LocalDateTime dt) {
		final DateTime.Builder builder = DateTime.builder()
				.year(dt.getYear())
				.month((byte)dt.getMonthValue())
				.dayOfMonth((byte)dt.getDayOfMonth())
				.hour((byte)dt.getHour())
				.minute((byte)dt.getMinute())
				.second((byte)dt.getSecond());

		int value = 0;
		if((value = dt.get(ChronoField.MILLI_OF_SECOND)) != 0) {
			builder.complementary(ComplementaryType.MILLISECONDS, (short)value);
		}else if((value = dt.get(ChronoField.MICRO_OF_SECOND)) != 0) {
			builder.complementary(ComplementaryType.MICROSECONDS, (short)value);
		}else if((value = dt.getNano()) != 0) {
			builder.complementary(ComplementaryType.NANOSECONDS, (short)value);
		}

		return builder;
	}

	public static final DateTime fromZonedDateTime(ZonedDateTime zdt) {
		return fromLocalDateTimeBuilder(zdt.toLocalDateTime())
				.timezone(zdt.getZone(), zdt.getOffset())
				.build();
	}
	
	@Override
	public String toString() {
		return "DateTime[Y:"+this.getYear()
					+", M:"+this.getMonth()
					+", d:"+this.getDayOfMonth()
					+", h:"+this.getHour()
					+", m:"+this.getMinute()
					+", s:"+this.getSecond()
					+", co:{"+getComplementaryType()+","+getComplementaryValue()+"}"
					+"tz:{"+getTimezone()+","+getZoneOffset()+"}]";
	}

	public static class Builder {

		private int year = -1;
		private byte month = -1, dayOfMonth = -1;

		private byte hour = -1, minute = -1, second = -1;

		private ComplementaryType complementaryType;
		private short complementary = -1;

		private ZoneId timezone;
		private ZoneOffset timezoneOffset;

		private Builder() { }

		public Builder year(int year) {
			this.year = year;

			return this;
		}

		public Builder month(byte month) {
			this.month = month;

			return this;
		}

		public Builder dayOfMonth(byte dayOfMonth) {
			this.dayOfMonth = dayOfMonth;

			return this;
		}

		public Builder hour(byte hour) {
			this.hour = hour;

			return this;
		}

		public Builder minute(byte minute) {
			this.minute = minute;

			return this;
		}

		public Builder second(byte second) {
			this.second = second;

			return this;
		}

		public Builder complementary(byte type, short data) {
			this.complementaryType = ComplementaryType.of(type);
			this.complementary = data;

			return this;
		}

		public Builder complementary(ComplementaryType type, short data) {
			this.complementaryType = type;
			this.complementary = data;

			return this;
		}

		public Builder timezone(ZoneId timezone, ZoneOffset offset) {
			this.timezone = timezone;
			this.timezoneOffset = offset;

			return this;
		}

		public DateTime build() {
			return new DateTime(this);
		}
	}
}

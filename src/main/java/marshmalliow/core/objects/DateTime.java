package marshmalliow.core.objects;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Optional;

import marshmalliow.core.binary.MOBFFile;
import marshmalliow.core.binary.utils.ComplementaryType;
import marshmalliow.core.io.BinaryReader;

/**
 * This class is a representation of a date and time object. It is composed of 9 fields:
 * <ul>
 *     <li>Year as {@code short}</li>
 *     <li>Month as {@code byte}</li>
 *     <li>Day of month as {@code byte}</li>
 *     <li>Hour as {@code byte}</li>
 *     <li>Minute as {@code byte}</li>
 *     <li>Second as {@code byte}</li>
 *     <li>Complementary type as {@code byte}</li>
 *     <li>Complementary data as {@code short}</li>
 *     <li>Timezone as {@code ZoneId}</li>
 *     <li>Timezone offset as {@code ZoneOffset}</li>
 * </ul>
 * This class is immutable and can be created using the {@link DateTime.Builder} class.<br/>
 * This class is in compliance with MOBF specifications.
 * 
 * @version 1.0.0
 * @author 278deco
 */
public class DateTime {

	/**
	 * The number of fields in a {@code DateTime} object.
	 */
	public static final int FIELDS_NUMBER = 9;

	/**
	 * Represents the year part of the date stored inside {@link DateTime}.
	 */
	private int year;
	/**
	 * Represents the month part of the date stored inside {@link DateTime}.
	 */
	private byte month;
	/**
	 * Represents the day of the month part of the date stored inside {@link DateTime}.
	 */
	private byte dayOfMonth;

	/**
	 * Represents the hour part of the time stored inside {@link DateTime}.
	 */
	private byte hour;
	/**
	 * Represents the minute part of the time stored inside {@link DateTime}.
	 */
	private byte minute;
	/**
	 * Represents the second part of the time stored inside {@link DateTime}.
	 */
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
	
	/**
	 * Complementary part with:
	 * <ul>
	 *	<li>The complementary type as {@code byte}</li>
	 *	<li>The complementary data as {@code short}</li>
	 * </ul>
	 * Complementary represent either milliseconds, microseconds or nanoseconds
	 */
	private short complementary;

	/**
	 * Timezone part with:
	 * <ul>
	 *	<li>The timezone as {@code ZoneId}</li>
	 *	<li>The timezone offset as {@code ZoneOffset}</li>
	 * </ul>
	 * The timezone is calculated using the offset.
	 */
	private ZoneId timezone;
	
	/**
	 * Timezone part with:
	 * <ul>
	 *	<li>The timezone as {@code ZoneId}</li>
	 *	<li>The timezone offset as {@code ZoneOffset}</li>
	 * </ul>
	 * The timezone is calculated using the offset.
	 */
	private ZoneOffset timezoneOffset;

	/**
	 * Private constructor to create a new instance of {@code DateTime} from a {@code Builder} object.
	 * @param builder the {@code Builder} object
	 */
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

	/**
	 * Get the year part of the {@link DateTime}.
	 * <p>
	 * This attribute's presence depends on the control byte when stored inside a {@link MOBFFile}.
	 * @return an {@link Optional} containing the year or empty if not present
	 * @see BinaryReader#readDatetime()
	 */
	public Optional<Integer> getYear() {
		return this.year != -1 ? Optional.of(this.year) : Optional.empty();
	}

	/**
	 * Check if the year part is present.
	 * @return {@code true} if the year part is present, {@code false} otherwise
	 */
	public boolean isYearPresent() {
		return this.year != -1;
	}

	/**
	 * Get the month part of the {@link DateTime}.
	 * <p>
	 * This attribute's presence depends on the control byte when stored inside a {@link MOBFFile}.
	 * @return an {@link Optional} containing the month or empty if not present
	 * @see BinaryReader#readDatetime()
	 */
	public Optional<Byte> getMonth() {
		return this.month != -1 ? Optional.of(this.month) : Optional.empty();
	}

	/**
	 * Check if the month part is present.
	 * @return {@code true} if the month part is present, {@code false} otherwise
	 */
	public boolean isMonthPresent() {
		return this.month != -1;
	}

	/**
	 * Get the day of the month part of the {@link DateTime}.
	 * <p>
	 * This attribute's presence depends on the control byte when stored inside a {@link MOBFFile}.
	 * @return an {@link Optional} containing the day of the month or empty if not present
	 * @see BinaryReader#readDatetime()
	 */
	public Optional<Byte> getDayOfMonth() {
		return this.dayOfMonth != -1 ? Optional.of(this.dayOfMonth) : Optional.empty();
	}

	/**
	 * Check if the day of the month part is present.
	 * @return {@code true} if the day of the month part is present, {@code false} otherwise
	 */
	public boolean isDayOfMonthPresent() {
		return this.dayOfMonth != -1;
	}

	/**
	 * Get the hour part of the {@link DateTime}.
	 * <p>
	 * This attribute's presence depends on the control byte when stored inside a {@link MOBFFile}.
	 * @return an {@link Optional} containing the hour or empty if not present
	 * @see BinaryReader#readDatetime()
	 */
	public Optional<Byte> getHour() {
		return this.hour != -1 ? Optional.of(this.hour) : Optional.empty();
	}

	/**
	 * Check if the hour part is present.
	 * @return {@code true} if the hour part is present, {@code false} otherwise
	 */
	public boolean isHourPresent() {
		return this.hour != -1;
	}

	/**
	 * Get the minute part of the {@link DateTime}.
	 * <p>
	 * This attribute's presence depends on the control byte when stored inside a {@link MOBFFile}.
	 * @return an {@link Optional} containing the minute or empty if not present
	 * @see BinaryReader#readDatetime()
	 */
	public Optional<Byte> getMinute() {
		return this.minute != -1 ? Optional.of(this.minute) : Optional.empty();
	}

	/**
	 * Check if the minute part is present.
	 * @return {@code true} if the minute part is present, {@code false} otherwise
	 */
	public boolean isMinutePresent() {
		return this.minute != -1;
	}

	/**
	 * Get the second part of the {@link DateTime}.
	 * <p>
	 * This attribute's presence depends on the control byte when stored inside a {@link MOBFFile}.
	 * @return an {@link Optional} containing the second or empty if not present
	 * @see BinaryReader#readDatetime()
	 */
	public Optional<Byte> getSecond() {
		return this.second != -1 ? Optional.of(this.second) : Optional.empty();
	}

	/**
	 * Check if the second part is present.
	 * @return {@code true} if the second part is present, {@code false} otherwise
	 */
	public boolean isSecondPresent() {
		return this.second != -1;
	}
	
	/**
	 * Get the {@link ComplementaryType} stored inside this {@link DateTime}.
	 * <p>
	 * The complementary type is used to store either milliseconds, microseconds or nanoseconds.<br/>
	 * This attribute's presence depends on the control byte when stored inside a {@link MOBFFile}.
	 * @return an {@link Optional} containing the complementary type or empty if not present
	 * @see #getComplementaryValue()
	 */
	public Optional<ComplementaryType> getComplementaryType() {
		return Optional.ofNullable(this.complementaryType);
	}

	/**
	 * Get the complementary value stored inside this {@link DateTime}.
	 * <p>
	 * The complementary value is used to store either milliseconds, microseconds or nanoseconds.<br/>
	 * To interpret this value, you must use the {@link #getComplementaryType()} method.<br/>
	 * This attribute's presence depends on the control byte when stored inside a {@link MOBFFile}.
	 * @return an {@link Optional} containing the complementary value or empty if not present
	 * @see #getComplementaryType()
	 */
	public Optional<Short> getComplementaryValue() {
		return this.complementary!= -1 ? Optional.of(this.complementary) : Optional.empty();
	}

	public boolean isComplementaryPresent() {
		return this.complementaryType != null && this.complementary != -1;
	}
	
	/**
	 * Get the timezone stored inside this {@link DateTime}.
	 * <p>
	 * This attributed is calculated using the {@link #timezoneOffset}.<br/>
	 * This attribute's presence depends on the control byte when stored inside a {@link MOBFFile}.
	 * @return an {@link Optional} containing the timezone or empty if not present
	 */
	public Optional<ZoneId> getTimezone() {
		return Optional.ofNullable(this.timezone);
	}

	/**
	 * Get the timezone offset stored inside this {@link DateTime}.
	 * <p>
	 * The timezone offset is used to store the offset from the UTC timezone.<br/>
	 * This attribute's presence depends on the control byte when stored inside a {@link MOBFFile}.
	 * @return an {@link Optional} containing the timezone offset or empty if not present
	 */
	public Optional<ZoneOffset> getZoneOffset() {
		return Optional.ofNullable(this.timezoneOffset);
	}

	/**
	 * Check if the timezone is present. A timezone is present if both the timezone and the offset are present.
	 * @return {@code true} if the timezone is present, {@code false} otherwise
	 */
	public boolean isTimezonePresent() {
		return this.timezone != null && this.timezoneOffset != null;
	}

	/**
	 * Convert this instance of {@code DateTime} to a {@link LocalDate}. <br/>
	 * If the year, month and dayOfMonth are not present, the method will return an empty {@link Optional}.
	 * @return an {@code Optional} of {@code LocalDate}
	 */
	public Optional<LocalDate> asLocalDate() {
		if(this.year != -1 && this.month != -1 && this.dayOfMonth != -1) {
			return Optional.of(LocalDate.of(this.year, this.month, this.dayOfMonth));
		}else {
			return Optional.empty();
		}
	}

	/**
	 * Convert this instance of {@code DateTime} to a {@link LocalTime}. <br/>
	 * If the hour, minute and second are not present, the method will return an
	 * empty {@link Optional}.<br/>
	 * If the complementary type and value are present, 
	 * the method will return a {@code LocalTime} with the complementary value. (milliseconds, microseconds, nanoseconds).
	 * 
	 * @return an {@code Optional} of {@code LocalTime}
	 */
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

	/**
	 * Convert this instance of {@code DateTime} to a {@link LocalDateTime}.<br/>
	 * If the year, month, dayOfMonth, hour, minute and second are not present, the
	 * method will return an empty {@link Optional}.<br/>
	 * If the complementary type and value are present, the method will return a
	 * {@code LocalDateTime} with the complementary value. (milliseconds,
	 * microseconds, nanoseconds).
	 * 
	 * @return an {@code Optional} of {@code LocalDateTime}
	 */
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

	/**
	 * Convert this instance of {@code DateTime} to a {@link ZonedDateTime}.<br/>
	 * If the year, month, dayOfMonth, hour, minute, second and timezone are not present, the
	 * method will return an empty {@link Optional}.<br/>
	 * If the complementary type and value are present, the method will return a
	 * {@code ZonedDateTime} with the complementary value. (milliseconds,
	 * microseconds, nanoseconds).
	 * 
	 * @see #asLocalDateTime()
	 * @return an {@code Optional} of {@code ZonedDateTime}
	 */
	public Optional<ZonedDateTime> asZonedDateTime() {
		final Optional<LocalDateTime> ldt = this.asLocalDateTime();
		if(ldt.isPresent() && this.timezone != null) {
			return Optional.of(ZonedDateTime.of(ldt.get(), this.timezone));
		}else {
			return Optional.empty();
		}
	}

	/**
	 * Create a new instance of {@code DateTime.Builder}. 
	 * 
	 * @return a new instance of {@code DateTime.Builder}
	 */
	public static DateTime.Builder builder() {
		return new DateTime.Builder();
	}

	/**
	 * Create a new instance of {@code DateTime} from a {@link LocalDate} object.
	 * 
	 * @param date the {@code LocalDate} object
	 * @return a new instance of {@code DateTime}
	 */
	public static final DateTime fromLocalDate(LocalDate date) {
		return DateTime.builder()
				.year(date.getYear())
				.month((byte)date.getMonthValue())
				.dayOfMonth((byte)date.getDayOfMonth())
				.build();
	}

	/**
	 * Create a new instance of {@code DateTime} from a {@link LocalTime} object.
	 * 
	 * @param time the {@code LocalTime} object
	 * @return a new instance of {@code DateTime}
	 */
	public static final DateTime fromLocalTime(LocalTime time) {
		return DateTime.builder()
				.hour((byte)time.getHour())
				.minute((byte)time.getMinute())
				.second((byte)time.getSecond())
				.build();
	}

	/**
	 * Create a new instance of {@code DateTime} from a {@link LocalDateTime}
	 * object.
	 * 
	 * @param dt the {@code LocalDateTime} object
	 * @return a new instance of {@code DateTime}
	 */
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

	/**
	 * Create a new instance of {@code DateTime} from a {@link ZonedDateTime}
	 * object.
	 * 
	 * @param zdt the {@code ZonedDateTime} object
	 * @return a new instance of {@code DateTime}
	 */
	public static final DateTime fromZonedDateTime(ZonedDateTime zdt) {
		return fromLocalDateTimeBuilder(zdt.toLocalDateTime())
				.timezone(zdt.getZone(), zdt.getOffset())
				.build();
	}
	
	/**
	 * Return a string representation of this {@code DateTime} object.
	 * @return a string representation of this instance
	 */
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

		/**
		 * Set the year of the {@code DateTime} object.
		 * @param year the year to set 
		 * @return the current instance of the {@code Builder}
		 */
		public Builder year(int year) {
			this.year = year;

			return this;
		}

		/**
		 * Set the month of the {@code DateTime} object.
		 * @param month the month to set
		 * @return the current instance of the {@code Builder}
		 */
		public Builder month(byte month) {
			this.month = month;

			return this;
		}

		/**
		 * Set the day of the month of the {@code DateTime} object.
		 * @param dayOfMonth the day of the month to set
		 * @return the current instance of the {@code Builder}
		 */
		public Builder dayOfMonth(byte dayOfMonth) {
			this.dayOfMonth = dayOfMonth;

			return this;
		}

		/**
		 * Set the hour of the {@code DateTime} object.
		 * @param hour the hour to set
		 * @return the current instance of the {@code Builder}
		 */
		public Builder hour(byte hour) {
			this.hour = hour;

			return this;
		}

		/**
		 * Set the minute of the {@code DateTime} object.
		 * @param minute the minute to set
		 * @return the current instance of the {@code Builder}
		 */
		public Builder minute(byte minute) {
			this.minute = minute;

			return this;
		}

		/**
		 * Set the second of the {@code DateTime} object.
		 * @param second the second to set
		 * @return the current instance of the {@code Builder}
		 */
		public Builder second(byte second) {
			this.second = second;

			return this;
		}

		/**
		 * Set the complementary part of the {@code DateTime} object.<br/>
		 * The complementary part is used to store either milliseconds, microseconds or nanoseconds.
		 * @param type the complementary type to set
		 * @param data the complementary value to set
		 * @return the current instance of the {@code Builder}
		 */
		public Builder complementary(byte type, short data) {
			this.complementaryType = ComplementaryType.of(type);
			this.complementary = data;

			return this;
		}

		/**
		 * Set the complementary part of the {@code DateTime} object.<br/>
		 * The complementary part is used to store either milliseconds, microseconds or nanoseconds.
		 * @param type The {@link ComplementaryType} to set
		 * @param data The complementary value to set
		 * @return the current instance of the {@code Builder}
		 */
		public Builder complementary(ComplementaryType type, short data) {
			this.complementaryType = type;
			this.complementary = data;

			return this;
		}

		/**
		 * Set the timezone of the {@code DateTime} object.<br/>
		 * @param timezone the {@link ZoneId} to set
		 * @param offset the {@link ZoneOffset} to set
		 * @return the current instance of the {@code Builder}
		 */
		public Builder timezone(ZoneId timezone, ZoneOffset offset) {
			this.timezone = timezone;
			this.timezoneOffset = offset;

			return this;
		}

		/**
		 * Build the {@code DateTime} object.
		 * @return a new instance of {@code DateTime}
		 */
		public DateTime build() {
			return new DateTime(this);
		}
	}
}

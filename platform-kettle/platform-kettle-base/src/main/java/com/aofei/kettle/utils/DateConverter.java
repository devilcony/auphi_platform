package com.aofei.kettle.utils;

import java.util.Date;

import org.springframework.core.convert.converter.Converter;

public class DateConverter implements Converter<String, Date> {
	@Override
	public Date convert(String source) {
		Long l = Long.parseLong(source);
		Date date = new Date();
		date.setTime(l);

		return date;
	}
}

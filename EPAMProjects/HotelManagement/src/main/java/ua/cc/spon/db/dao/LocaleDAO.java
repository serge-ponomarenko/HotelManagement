package ua.cc.spon.db.dao;

import ua.cc.spon.db.entity.Locale;

import java.util.Map;

public interface LocaleDAO {

	Map<String, Locale> findALL();

}

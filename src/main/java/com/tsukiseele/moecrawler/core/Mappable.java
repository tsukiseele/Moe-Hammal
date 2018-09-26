package com.tsukiseele.moecrawler.core;

import com.tsukiseele.moecrawler.utils.TextUtil;

import java.lang.reflect.Field;
import java.util.List;

public abstract class Mappable {
	public abstract String getCatalogUrl();
	public abstract String getExtraUrl();
	private String type;
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
	public boolean hasCatalog() {
		return !TextUtil.isEmpty(getCatalogUrl());
	}
	
	public boolean hasExtra() {
		return !TextUtil.isEmpty(getExtraUrl());
	}
	
	public void fillTo(Mappable mappable) {
		if (mappable == null)
			return;
		Field[] fields = mappable.getClass().getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			try {
				if (field.get(mappable) == null) {
					Object obj = field.get(this);
					if (obj != null) 
						field.set(mappable, obj);
				}
			} catch (Exception e) {

			}
		}
	}
	
	public void fillToAll(List<? extends Mappable> datas) {
		if (datas != null)
			for (Mappable data : datas)
				this.fillTo(data);
	}
}

package com.yoanaydavid.recetas.java;


public class Paso  {
	private String descripcion;
	private String path = null;
	String number;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Paso(String descripcion) {
		this.descripcion = descripcion;
	}

	public Paso(String descripcion, String path) {
		this.descripcion = descripcion;
		this.path = path;
	}

	

}

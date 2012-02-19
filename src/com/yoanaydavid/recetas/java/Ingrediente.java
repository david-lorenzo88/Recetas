package com.yoanaydavid.recetas.java;


public class Ingrediente implements Comparable<Ingrediente> {

	private String cantidad = new String();
	private String nombre;
	private boolean checked = false;

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getCantidad() {
		return cantidad;
	}

	public String getNombre() {
		return nombre;
	}

	public Ingrediente(String nombre, String cantidad) {
		this.cantidad = cantidad;
		this.nombre = nombre;
	}

	public Ingrediente(String nombre) {
		this.nombre = nombre;
	}
	
	@Override
	public int compareTo(Ingrediente ing) {

		return this.nombre.compareToIgnoreCase(ing.getNombre());

	}

	@Override
	public String toString() {
		return getNombre();
	}

	

}

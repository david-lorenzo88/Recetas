package com.yoanaydavid.recetas.java;

import java.util.List;

public abstract class Receta {
	
	List<Ingrediente> ingredientes;
	
	String nombre;
	
	public Receta (String nombre, List<Ingrediente> ings){
		this.ingredientes = ings;
		
		this.nombre = nombre;
	}

	public List<Ingrediente> getIngredientes() {
		return ingredientes;
	}

	public void setIngredientes(List<Ingrediente> ingredientes) {
		this.ingredientes = ingredientes;
	}



	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	

}

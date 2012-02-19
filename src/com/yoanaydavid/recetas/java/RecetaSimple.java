package com.yoanaydavid.recetas.java;

import java.util.List;

public class RecetaSimple extends Receta {
	String elab;
	
	
	public String getElab() {
		return elab;
	}


	public void setElab(String elab) {
		this.elab = elab;
	}


	public RecetaSimple(String nombre, List<Ingrediente> ings, String elab) {
		super(nombre, ings);
		this.elab = elab;
		// TODO Auto-generated constructor stub
	}

}

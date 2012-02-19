package com.yoanaydavid.recetas.java;

import java.util.List;

public class RecetaAdvanced extends Receta {
	List<Paso> pasos;
	
	
	public List<Paso> getPasos() {
		return pasos;
	}


	public void setPasos(List<Paso> pasos) {
		this.pasos = pasos;
	}


	public RecetaAdvanced(String nombre, List<Ingrediente> ings, List<Paso> pasos) {
		super(nombre, ings);
		this.pasos = pasos;
		// TODO Auto-generated constructor stub
	}

}

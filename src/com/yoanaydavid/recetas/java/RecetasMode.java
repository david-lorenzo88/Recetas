package com.yoanaydavid.recetas.java;

public class RecetasMode {
	
	String titulo;
	String texto;
	
	public RecetasMode(String titulo, String texto){
		this.titulo = titulo;
		this.texto = texto;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

}

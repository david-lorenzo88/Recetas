package com.yoanaydavid.recetas.java;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.os.Environment;
import android.util.Log;

public class FileXML {
	String rutaAlmacenamientoExterno;

	public FileXML() {
		this.rutaAlmacenamientoExterno = Environment
				.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator;
	}

	public List<Ingrediente> leerIngredientes() throws IOException {
		List<Ingrediente> lista = new ArrayList<Ingrediente>();

		File file = new File(rutaAlmacenamientoExterno + "ingredientes.xml");
		if (file.exists()) {
			Document doc = getDoc("ingredientes.xml");

			if (doc != null) {

				// Get a list of all elements in the document
				NodeList list = doc.getElementsByTagName("ingrediente");

				for (int i = 0; i < list.getLength(); i++) {
					// Get element
					lista.add(new Ingrediente(list.item(i).getTextContent()));

				}
			}

		}

		return lista;
	}

	public List<String> leerElementos(String archivo, String tag)
			throws IOException {
		List<String> listas = new ArrayList<String>();
		Log.v("test", rutaAlmacenamientoExterno + archivo);
		File file = new File(rutaAlmacenamientoExterno + archivo);
		if (file.exists()) {

			Document doc = getDoc(archivo);

			if (doc != null) {

				// Get a list of all elements in the document
				NodeList list = doc.getElementsByTagName(tag);

				for (int i = 0; i < list.getLength(); i++) {
					// Get element
					Element current = (Element) list.item(i);
					listas.add(current.getAttribute("nombre"));

				}
			}

		}

		return listas;
	}

	public String encontrarNombreSiguiente(String date) throws IOException {
		List<String> listasGuardadas;
		listasGuardadas = leerElementos("listas.xml", "lista");
		int i = 1;
		String nombre = "Lista " + i + " / " + date;
		while (listasGuardadas.contains(nombre)) {

			nombre = "Lista " + i + " / " + date;
			i++;
		}

		return nombre;

	}

	public List<String> leerIngredientesLista(String nombre) throws IOException {
		List<String> ingredientes = new ArrayList<String>();

		File file = new File(rutaAlmacenamientoExterno + "listas.xml");
		if (file.exists()) {
			Document doc = getDoc("listas.xml");

			if (doc != null) {

				// Get a list of all elements in the document
				NodeList list = doc.getElementsByTagName("lista");

				for (int i = 0; i < list.getLength(); i++) {
					// Get element
					Element current = (Element) list.item(i);
					if (current.getAttribute("nombre").equals(nombre.trim())) {
						NodeList ings = current.getChildNodes();
						for (int j = 0; j < ings.getLength(); j++) {
							ingredientes.add(ings.item(j).getTextContent());
						}
					}

				}
			}

		}

		return ingredientes;
	}

	public Receta leerReceta(String nombre) throws IOException {
		Receta receta = null;

		File file = new File(rutaAlmacenamientoExterno + "recetas.xml");
		if (file.exists()) {
			Document doc = getDoc("recetas.xml");

			if (doc != null) {

				// Get a list of all elements in the document
				NodeList list = doc.getElementsByTagName("receta");

				for (int i = 0; i < list.getLength(); i++) {
					// Get element
					Element current = (Element) list.item(i);
					if (current.getAttribute("nombre").equals(nombre.trim())) {

						NodeList ingredientes = current
								.getElementsByTagName("ingrediente");
						List<Ingrediente> ings = new ArrayList<Ingrediente>();
						for (int j = 0; j < ingredientes.getLength(); j++) {
							Element elem = (Element) ingredientes.item(j);
							Ingrediente ing = new Ingrediente(
									elem.getAttribute("nombre"),
									elem.getAttribute("cantidad"));
							ings.add(ing);
						}

						NodeList elaboracionList = current
								.getElementsByTagName("elaboracion");
						// Como solo hay 1 cojo el pos=0
						Element elab = (Element) elaboracionList.item(0);
						// Comprobamos el modo mediante el atributo "mode"
						if (elab.getAttribute("mode").equals("simple")) {
							// Modo simple
							receta = new RecetaSimple(nombre, ings,
									elab.getTextContent());

						} else if (elab.getAttribute("mode").equals("advanced")) {
							// Modo avanzado
							List<Paso> pasosList = new ArrayList<Paso>();
							NodeList pasos = elab.getElementsByTagName("paso");
							for (int j = 0; j < pasos.getLength(); j++) {
								Element elem = (Element) pasos.item(j);
								if (elem.hasAttribute("path")) {
									// El paso tiene imagen asociada
									Paso p = new Paso(elem.getTextContent(),
											elem.getAttribute("path"));
									p.setNumber(elem.getAttribute("numero"));
									pasosList.add(p);
								} else {
									Paso p = new Paso(elem.getTextContent());
									p.setNumber(elem.getAttribute("numero"));
									pasosList.add(p);
								}
							}
							receta = new RecetaAdvanced(nombre, ings, pasosList);
						}
					}

				}

			}
		}

		return receta;
	}

	public boolean borrarListaCompra(String nombre, String file, String tag) {

		Document doc = getDoc(file);

		if (doc != null) {

			// Get a list of all elements in the document
			NodeList list = doc.getElementsByTagName(tag);
			Element element = null;
			for (int i = 0; i < list.getLength(); i++) {
				// Get element
				Element current = (Element) list.item(i);
				if (current.getAttribute("nombre").equals(nombre)) {
					element = current;
					break;
				}
			}
			if (element != null) {
				doc.getDocumentElement().removeChild(element);

				return saveDoc(file, doc);

			}
		}

		return false;
	}

	public boolean borrarIngrediente(String nombre) throws IOException {

		File file = new File(rutaAlmacenamientoExterno + "ingredientes.xml");
		if (!file.exists())
			crearArchivo("ingredientes.xml", "ingredientes");
		Document doc = getDoc("ingredientes.xml");

		if (doc != null) {

			// Get a list of all elements in the document
			NodeList list = doc.getElementsByTagName("ingrediente");
			Node nodeToRemove = null;
			for (int i = 0; i < list.getLength(); i++) {
				// Get element
				if (list.item(i).getTextContent().toLowerCase()
						.equals(nombre.toLowerCase()))
					nodeToRemove = list.item(i);

			}
			if (nodeToRemove != null) {
				doc.getDocumentElement().removeChild(nodeToRemove);
			}

			// write the content into xml file
			return saveDoc("ingredientes.xml", doc);
		}

		return false;
	}

	public boolean escribirArchivo(String nombreArchivo, String nombre)
			throws IOException {

		File file = new File(rutaAlmacenamientoExterno + nombreArchivo);
		if (!file.exists())
			crearArchivo(nombreArchivo, "ingredientes");
		// Create a factory
		Document doc = getDoc(nombreArchivo);

		if (doc != null) {

			// Get a list of all elements in the document
			NodeList list = doc.getElementsByTagName("ingrediente");

			for (int i = 0; i < list.getLength(); i++) {
				// Get element
				if (list.item(i).getTextContent().toLowerCase()
						.equals(nombre.toLowerCase()))
					return false;

			}
			Element ing = doc.createElement("ingrediente");
			// ing.setNodeValue(nombre);
			ing.setTextContent(nombre.substring(0, 1).toUpperCase()
					+ nombre.substring(1).toLowerCase());
			doc.getDocumentElement().appendChild(ing);

			// write the content into xml file
			return saveDoc(nombreArchivo, doc);
		}

		return false;
	}

	public boolean guardarLista(ArrayList<String> ingredientes, String nombre) {
		// Create a factory

		File file = new File(rutaAlmacenamientoExterno + "listas.xml");
		if (!file.exists())
			crearArchivo("listas.xml", "listas");

		Document doc = getDoc("listas.xml");

		if (doc != null) {
			NodeList listas = doc.getElementsByTagName("lista");
			for (int i = 0; i < listas.getLength(); i++) {
				Element current = (Element) listas.item(i);
				if (current.getAttribute("nombre").equals(nombre))
					return false;
			}
			Element lista = doc.createElement("lista");
			lista.setAttribute("nombre", nombre);
			Iterator<String> it = ingredientes.iterator();
			while (it.hasNext()) {
				Element ing = doc.createElement("ingrediente");
				// ing.setNodeValue(nombre);
				ing.setTextContent(it.next());
				lista.appendChild(ing);
			}
			doc.getDocumentElement().appendChild(lista);

			return saveDoc("listas.xml", doc);
		}

		return false;
	}

	public boolean editarLista(ArrayList<String> ingredientes, String nombre) {
		// Create a factory

		File file = new File(rutaAlmacenamientoExterno + "listas.xml");
		if (!file.exists())
			crearArchivo("listas.xml", "listas");

		Document doc = getDoc("listas.xml");

		if (doc != null) {
			NodeList listas = doc.getElementsByTagName("lista");
			for (int i = 0; i < listas.getLength(); i++) {
				Element current = (Element) listas.item(i);
				if (current.getAttribute("nombre").equals(nombre)) {
					borrarListaCompra(nombre, "listas.xml", "lista");
					return guardarLista(ingredientes, nombre);

				}
			}
		}

		return false;
	}

	public boolean guardarRecetaModoSimple(ArrayList<Ingrediente> ingredientes,
			String nombre, String elaboracion) {
		// Create a factory

		File file = new File(rutaAlmacenamientoExterno + "recetas.xml");
		if (!file.exists())
			crearArchivo("recetas.xml", "recetas");

		Document doc = getDoc("recetas.xml");

		if (doc != null) {
			// Compruebo que no hay ninguna lista con el mismo nombre
			NodeList recetas = doc.getElementsByTagName("receta");
			for (int i = 0; i < recetas.getLength(); i++) {
				Element current = (Element) recetas.item(i);
				if (current.getAttribute("nombre").equals(nombre))
					return false;
			}
			Element receta = doc.createElement("receta");
			receta.setAttribute("nombre", nombre);

			// Creo y añado los ingredientes
			Element ingredientesRoot = doc.createElement("ingredientes");

			for (int i = 0; i < ingredientes.size(); i++) {
				Ingrediente ing = ingredientes.get(i);
				Element ingredienteChild = doc.createElement("ingrediente");
				ingredienteChild.setAttribute("nombre", ing.getNombre());
				ingredienteChild.setAttribute("cantidad", ing.getCantidad());
				ingredientesRoot.appendChild(ingredienteChild);
			}
			receta.appendChild(ingredientesRoot);

			// Creo y añado la elaboración

			Element elaboracionRoot = doc.createElement("elaboracion");
			elaboracionRoot.setTextContent(elaboracion);
			elaboracionRoot.setAttribute("mode", "simple");
			receta.appendChild(elaboracionRoot);

			// añado la receta al root element
			doc.getDocumentElement().appendChild(receta);

			return saveDoc("recetas.xml", doc);
		}

		return false;
	}

	public boolean guardarRecetaModoAvanzado(
			ArrayList<Ingrediente> ingredientes, ArrayList<Paso> pasos,
			String nombre) {
		// Create a factory

		File file = new File(rutaAlmacenamientoExterno + "recetas.xml");
		if (!file.exists())
			crearArchivo("recetas.xml", "recetas");

		Document doc = getDoc("recetas.xml");
		if (doc != null) {

			// Compruebo que no hay ninguna lista con el mismo nombre
			NodeList recetas = doc.getElementsByTagName("receta");
			for (int i = 0; i < recetas.getLength(); i++) {
				Element current = (Element) recetas.item(i);
				if (current.getAttribute("nombre").equals(nombre))
					return false;
			}
			Element receta = doc.createElement("receta");
			receta.setAttribute("nombre", nombre);

			// Creo y añado los ingredientes
			Element ingredientesRoot = doc.createElement("ingredientes");

			for (int i = 0; i < ingredientes.size(); i++) {
				Ingrediente ing = ingredientes.get(i);
				Element ingredienteChild = doc.createElement("ingrediente");
				ingredienteChild.setAttribute("nombre", ing.getNombre());
				ingredienteChild.setAttribute("cantidad", ing.getCantidad());
				ingredientesRoot.appendChild(ingredienteChild);
			}
			receta.appendChild(ingredientesRoot);

			// Creo y añado la elaboración

			Element elaboracionRoot = doc.createElement("elaboracion");
			elaboracionRoot.setAttribute("mode", "advanced");
			for (int i = 0; i < pasos.size(); i++) {
				Paso p = pasos.get(i);
				Element paso = doc.createElement("paso");
				paso.setAttribute("numero", p.getNumber());
				if (p.getPath() != null) {
					paso.setAttribute("path", p.getPath());
				}
				paso.setTextContent(p.getDescripcion());
				elaboracionRoot.appendChild(paso);
			}

			
			receta.appendChild(elaboracionRoot);

			// añado la receta al root element
			doc.getDocumentElement().appendChild(receta);

			return saveDoc("recetas.xml", doc);

		}

		return false;
	}

	public void crearArchivo(String nombre, String rootElement) {

		try {
			// ///////////////////////////
			// Creating an empty XML Document

			// We need a Document
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			// //////////////////////
			// Creating the XML tree

			// create the root element and add it to the document
			Element root = doc.createElement(rootElement);
			doc.appendChild(root);

			// ///////////////
			// Output the XML

			// set up a transformer
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "yes"); 
			trans.setOutputProperty(OutputKeys.ENCODING, "utf-8");

			DOMSource source = new DOMSource(doc);
			// Prepare the output file
			File file = new File(rutaAlmacenamientoExterno + nombre);
			Result result = new StreamResult(file);
			trans.transform(source, result);

		} catch (ParserConfigurationException ex) {
			ex.printStackTrace();
		} catch (TransformerConfigurationException ex) {
			ex.printStackTrace();
		} catch (TransformerException ex) {
			ex.printStackTrace();
		}

	}

	public int getRecetaMode(String nombre) {

		File file = new File(rutaAlmacenamientoExterno + "recetas.xml");
		if (file.exists()) {
			// Create a factory
			Document doc = getDoc("recetas.xml");

			if (doc != null) {
				// Get a list of all elements in the document
				NodeList list = doc.getElementsByTagName("receta");

				for (int i = 0; i < list.getLength(); i++) {
					// Get element
					Element current = (Element) list.item(i);
					if (current.getAttribute("nombre").equals(nombre.trim())) {
						NodeList elaboracionList = current
								.getElementsByTagName("elaboracion");
						Element elab = (Element) elaboracionList.item(0);
						if (elab.getAttribute("mode").equals("simple"))
							return Mode.RECETA_SIMPLE_MODE;
						else if (elab.getAttribute("mode").equals("advanced"))
							return Mode.RECETA_ADVANCED_MODE;
					}
				}
			}

		}

		return -1;
	}

	private Document getDoc(String name) {
		try {
			// Create a factory
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			// Use the factory to create a builder
			DocumentBuilder builder;

			builder = factory.newDocumentBuilder();

			Document doc = builder.parse("file://" + rutaAlmacenamientoExterno
					+ name);

			return doc;

		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	private boolean saveDoc(String name, Document doc) {
		// write the content into xml file
		try {
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer;

			transformer = transformerFactory.newTransformer();

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(
					rutaAlmacenamientoExterno + name));
			transformer.transform(source, result);

			return true;
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

}

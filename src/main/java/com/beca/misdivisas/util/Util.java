package com.beca.misdivisas.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.imgscalr.Scalr;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Service
public class Util {

	public static String formatMonto(String montoIn) {
		Locale[] locales = { new Locale("de", "DE") };
		NumberFormat nf = NumberFormat.getNumberInstance(locales[0]);
		DecimalFormat df = (DecimalFormat) nf;
		df.applyPattern("###,###,###,##0.00");

		return df.format(Double.parseDouble(montoIn));
	}

	public int diasHabiles(Calendar fechaInicial, Calendar fechaFinal, List<Date> listaFechasNoLaborables) {
		int diffDays = 0;
		boolean diaHabil = false;

		while (fechaInicial.before(fechaFinal) || fechaInicial.equals(fechaFinal)) {
			if (!listaFechasNoLaborables.isEmpty()) {
				for (Date date : listaFechasNoLaborables) {
					Date fechaNoLaborablecalendar = fechaInicial.getTime();
					if (fechaInicial.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
							&& fechaInicial.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
							&& !fechaNoLaborablecalendar.equals(date)) {

						diaHabil = true;
					} else {
						diaHabil = false;
						break;
					}
				}
			} else {
				if (fechaInicial.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
						&& fechaInicial.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY)
					diffDays++;
			}
			if (diaHabil)
				diffDays++;

			fechaInicial.add(Calendar.DATE, 1);
		}
		return diffDays;
	}

	public static int getDiasHabiles(Calendar fechaInicial, Calendar fechaFinal, List<Date> listaFechasNoLaborables) {
		int diffDays = 0;
		while (fechaInicial.before(fechaFinal)) {
			if (esLaborable(fechaInicial, listaFechasNoLaborables)) {
				diffDays++;
			}
			fechaInicial.add(Calendar.DATE, 1);
		}
		return diffDays;
	}

	public static String diaHabilPrevio(List<Date> listaFechasNoLaborables) {
		boolean hasta = true;
		DateFormat formato = new SimpleDateFormat(Constantes.FORMATO_FECHA_DDMMYYYY);
		Calendar cal = new GregorianCalendar();
		Date fecha;
		try {
			fecha = formato.parse(formato.format(new Date()));
			cal.setTime(fecha);
			cal.add(Calendar.DATE, -1);
			while (hasta) {
				if (esLaborable(cal, listaFechasNoLaborables)) {
					hasta = false;
				} else {
					cal.add(Calendar.DATE, -1);
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return formato.format(cal.getTime());
	}

	public static boolean esLaborable(Calendar fecha, List<Date> listaFechasNoLaborables) {

		fecha.set(Calendar.HOUR_OF_DAY, 0);
		fecha.set(Calendar.MINUTE, 0);
		fecha.set(Calendar.SECOND, 0);
		fecha.set(Calendar.MILLISECOND, 0);
		boolean result = true;

		if (listaFechasNoLaborables != null) {
			Date fechaC = fecha.getTime();
			
			if (fecha.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && fecha.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
				for (int i = 0; i < listaFechasNoLaborables.size(); i++) {
					if (fechaC.equals(listaFechasNoLaborables.get(i))) {
						result = false;
					}
				}				
			} else {
				result = false;
			}
		} else {
			if (fecha.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
					&& fecha.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
				result = true;
			} else {
				result = false;
			}
		}
		return result;
	}
	
	public static String getRemoteIp(HttpServletRequest request) {
		String ip="";
		if(request.getHeader("x-forwarded-for")!= null)
			ip= request.getHeader("x-forwarded-for");
		else
			ip= request.getRemoteAddr();
		
		return ip;
	}
	
	public static long getDateDiff(Timestamp date1, TimeUnit timeUnit) {
        Instant instante = Instant.now();
		Timestamp ahora = Timestamp.from(instante);
	    long diffInMillies = ahora.getTime() - date1.getTime();
	    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
	
	public static Timestamp getTimestamp() {		
		return new Timestamp(new Date().getTime());		
	}
	
	public static String generarCartaPorte() {
		String numbers = "0123456789";
		Random rndmMethod = new Random();
		int length = 3;
		char[] randomNumber = new char[length];
		for (int i = 0; i < length; i++) {
			randomNumber[i] = numbers.charAt(rndmMethod.nextInt(numbers.length()));
		}
		
		Date date = new Date();  
		DateFormat dateFormat = new SimpleDateFormat("YYMMddHHmmss");
		String stringDate = dateFormat.format(date);
		return stringDate.concat(String.valueOf(randomNumber));
	}
	
	public static BufferedImage simpleResizeImage(BufferedImage originalImage, int targetWidth) throws Exception {
		return Scalr.resize(originalImage, targetWidth);
	}
	public static String getNombreArchivo(char pre) {
		Date date = new Date();  
		DateFormat dateFormat = new SimpleDateFormat("YYYYMMddHHmmss");
		String nombre = dateFormat.format(date);
		nombre = pre + nombre+ String.valueOf(generaCincoDigitos()) + ".jpeg";

		return nombre;
	}
	
	public static int generaCincoDigitos() {
	    Random r = new Random( System.currentTimeMillis() );
	    return ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
	}
	
	public static String salvaArchivo(String archivo, String rutaImg, char tipoDoc, int tamanio) throws IOException, Exception {		
		String nombreArchivo = getNombreArchivo(tipoDoc);
		ByteArrayInputStream bais = null;
		try {
			File file = new File(rutaImg+ nombreArchivo);
			BufferedImage image = null;
			bais = new ByteArrayInputStream(Base64.getDecoder().decode(archivo.getBytes()));
			image= Util.simpleResizeImage(ImageIO.read(bais),tamanio);
			ImageIO.write(image, "jpeg", file);
			bais.close();
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}finally {
			bais.close();
		}
		
		return nombreArchivo;
	}
	
	public static String obtenerArchivoStr(String ruta, String archivo){
		byte[] bytes;
		String respuesta="";
		 try {
			File file = ResourceUtils.getFile(ruta);
			if(file!=null) {
				bytes = Files.readAllBytes(file.toPath());
				respuesta=Base64.getEncoder().encodeToString(bytes);
			}
		} catch (Exception e) {
			return null;
		}
		return respuesta;		
	}
	
	public static boolean eliminarArchivo(String ruta, String archivo) throws IOException {
		 File file;
		 boolean retorno=false;
		try {
			file = ResourceUtils.getFile(ruta+archivo);
			retorno = file.delete();
		} catch (FileNotFoundException e) {
			return false;
		}         
	     return retorno; 
	}
	
	public static String formatRif(String caracterRif, int numeroRif) {
	        String nrif=String.valueOf(numeroRif);   
	        nrif = StringUtils.leftPad(nrif, 9, "0");
	        String rif = caracterRif.concat("-" + nrif.substring(0, nrif.length()-1) + '-' + nrif.charAt(nrif.length()-1));
	        return rif;
	    }
	   
	public static BufferedImage convertFile(byte[] archivo, char tipoDoc) throws IOException, Exception {
			int ancho = 0; //Ancho maximo permitido para la imagen
			int limite=0; //Tamaño maximo permitido para mantener el original en KB
			if(tipoDoc=='l') {
				ancho = 200;
				limite=50;
			}else {
				ancho = 400;
				limite=100;
			}

			BufferedImage image = null;
			ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(archivo));
			image = ImageIO.read(bais);
			if (archivo.length > limite * 1024) {
				int width = image.getWidth();
				width = width > ancho ? ancho : width;
				image= Util.simpleResizeImage(image, width);
			}
			bais.close();
			return image;
	}
		
	public static String saveFile(BufferedImage archivo, char tipoDoc, String extension, String pathDestination) throws IOException, Exception {
        String nombreArchivo = Util.getNombreArchivo(tipoDoc);
        if(tipoDoc!='l') {
            ImageIO.write(archivo, extension, new File(pathDestination + nombreArchivo));	               
        }
        	return nombreArchivo;
    }
	
	public static MockMultipartFile convertFiletoMultiPart(String ruta, String archivo) {
        try {
        	File file = ResourceUtils.getFile(ruta);
			if(file!=null) {
				MockMultipartFile multipartFile = new MockMultipartFile(archivo, file.getName(), "image/jpg+gif+png;base64",
	            		Files.readAllBytes(file.toPath()));
	            return multipartFile;
			}            
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
	
	public static String formatoDocumentoIdentidad(String documento) {
		String resp = "";
		if(documento != null && ! documento.isEmpty())
			resp =  String.format("%09d",Integer.parseInt(documento));
		return resp;
	}
	
	public static String formatoDocumentoCompleto(String documento) {
		String resp = "";
		if (documento.length()>1) {
			 resp= documento.substring(0,1) + String.format("%09d",Integer.parseInt(documento.substring(1,documento.length())));
		}
		return resp;
	}
	
	public static String formatoFecha(String formato) {
		DateFormat formatoFecha = new SimpleDateFormat(formato);
		return formatoFecha.format(new Date());
	}
	
	/*public static List<String> invertirArreglo(List<String> arreglo) {
	
	String temporal; // El elemento temporal del arreglo que vamos a intercambiar
	
	int longitudDeArreglo = arreglo.size();
	 // Nota: al dividir entre 2, si es flotante, se pasa al entero anterior. P. ej.
    // 5 / 2 = 2
	
	
    for (int i = 0; i < longitudDeArreglo/ 2; i++) {
    	 // Guardar el actual
        temporal = arreglo.get(i);
        // El índice de la otra mitad
        int indiceContrario = longitudDeArreglo - i - 1;
        // El valor actual es el valor contrario, el de la otra mitad
        arreglo.get(i) = arreglo[indiceContrario];
        
    }

    return n;
	
}*/
}

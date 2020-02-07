package spaceclipse.util;

import java.awt.Component;
import java.awt.Toolkit;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JOptionPane;

public class Util {

	public static void msjTxtError(String txt) {
		System.out.println("Error > "+ txt);
	}

	public static void msjTxtError(Exception e) {
		System.out.println("Error > "+e.toString());
	}

	public static void escribirListaString(String cad[]) {
		for(int i=0; i<cad.length; i++)
			System.out.println(cad[i]);
	}

	public static void pitido() {
		Toolkit.getDefaultToolkit().beep(); // Funciona en jdk 1.5.0_2 en XP
	}

	public static String convF8_f10(String fecha) {
		if (fecha!=null && !fecha.equals(""))
			if (fecha.length() == 8)
				return fecha.substring(6,8)+"/"+fecha.substring(4,6)+"/"+fecha.substring(0,4);

		return "";
	}

	public static String convF10_f8(String fecha) {
		if (fecha!=null && !fecha.equals(""))
			if (fecha.length() == 10)
				return fecha.substring(6,10)+fecha.substring(3,5)+fecha.substring(0,2);

		return "";
	}

	public static boolean SNToBoolean(String sn) {
		return sn.equals("S") ? true : false;
	}

	public static String BooleanToSN(boolean siNo) {
		return siNo ? "S" : "N";
	}

	// indica si una hora es correcta en formato HH:MM:SS
	public static boolean horaCorrecta(String hora) {
		boolean ok = true;
		byte h, m, s;

		if (hora.length() != 8)
			ok = false;
		else
			if (hora.charAt(2)!=':' || hora.charAt(5)!=':')
				ok = false;
			else
				if (!(Character.isDigit(hora.charAt(0)) && Character.isDigit(hora.charAt(1)) &&
						Character.isDigit(hora.charAt(3)) && Character.isDigit(hora.charAt(4)) &&
						Character.isDigit(hora.charAt(6)) && Character.isDigit(hora.charAt(7))))
					ok = false;
				else {
					h = Byte.parseByte(hora.substring(0,2));
					m = Byte.parseByte(hora.substring(3,5));
					s = Byte.parseByte(hora.substring(6,8));
					if (h<0 || h>23 || m<0 || m>59 || s<0 || s>59)
						ok = false;
				}

		return ok;
	}

	// Indica si una fecha es correcta en formato DD:MM:AAAA
	public static boolean fechaCorrecta(String fecha) {
		boolean ok = true;
		byte d, m;
		int a;

		if (fecha.length() != 10)
			ok = false;
		else
			if (fecha.charAt(2)!='/' || fecha.charAt(5)!='/')
				ok = false;
			else
				if (!(Character.isDigit(fecha.charAt(0)) && Character.isDigit(fecha.charAt(1)) &&
						Character.isDigit(fecha.charAt(3)) && Character.isDigit(fecha.charAt(4)) &&
						Character.isDigit(fecha.charAt(6)) && Character.isDigit(fecha.charAt(7)) &&
						Character.isDigit(fecha.charAt(8)) && Character.isDigit(fecha.charAt(9))))
					ok = false;
				else {
					d = Byte.parseByte(fecha.substring(0,2));
					m = Byte.parseByte(fecha.substring(3,5));
					a = Integer.parseInt(fecha.substring(6,10));
					if (d<1 || d>31 || m<1 || m>12 || a<0 || a>9999)
						ok = false;
				}

		return ok;
	}

	// Obtener la hora en formato HH:MM:SS:CCC
	public static String obtenerHora() {
		Calendar hoy = new GregorianCalendar();
		String hora="", horS, minS, segS, milS;
		int hor, min, seg, mil;

		hor = hoy.get(Calendar.HOUR);
		if (hoy.get(Calendar.AM_PM) == Calendar.PM)
			hor += 12;
		min = hoy.get(Calendar.MINUTE);
		seg = hoy.get(Calendar.SECOND);
		mil = hoy.get(Calendar.MILLISECOND);
		horS = String.valueOf(hor);
		minS = String.valueOf(min);
		segS = String.valueOf(seg);
		milS = "00"+String.valueOf(mil);
		hora = (horS.length()==1 ? "0"+horS : horS) + ":" +
				(minS.length()==1 ? "0"+minS : minS) + ":" +
				(segS.length()==1 ? "0"+segS : segS) + ":" +
				milS.substring(milS.length()-3);

		return hora;
	}

	// Obtener la fecha en formato DD/MM/AAAA
	public static String obtenerFecha() {
		Calendar hoy = new GregorianCalendar();
		String fecha = "", diaS,mesS;
		int dia, mes, anno;

		dia = hoy.get(Calendar.DAY_OF_MONTH);
		mes = hoy.get(Calendar.MONTH)+1;
		anno = hoy.get(Calendar.YEAR);
		diaS = String.valueOf(dia);
		mesS = String.valueOf(mes);
		fecha = String.valueOf(anno) + "/"+
				(mesS.length()==1 ? "0"+mesS : mesS) + "/" +
				(diaS.length()==1 ? "0"+diaS : diaS);

		return fecha;
	}

	public static float redondeo2D(float n) {
		double x = n*100+0.5;
		long y = (int)x;

		return (float)y/100;
	}

	public static float redondeo1D(float n) {
		double x = n*10+0.5;
		long y = (int)x;

		return (float)y/10;
	}

	public static int redondeo(float n) {
		return (int)(n+0.5);
	}

	public static String hmsToString(byte h, byte m, byte s) {
		String sh, sm, ss, hora;

		sh = "00"+Byte.toString(h);
		sh = sh.substring(sh.length()-2);
		sm = "00"+Byte.toString(m);
		sm = sm.substring(sm.length()-2);
		ss = "00"+Byte.toString(s);
		ss = ss.substring(ss.length()-2);
		hora = sh+":"+sm+":"+ss;

		return hora;
	}

	public static String obtenerIP() {
		String ip = "";

		try {
			String IP = InetAddress.getLocalHost().toString();
			int iBarra = IP.indexOf("/");
			if (iBarra > 0)
				ip = IP.substring(iBarra + 1);
		} catch (Exception e) {}

		return ip;
	}

	public static void DialogoMsjError(Component padre, String mensaje, String titulo) {
		JOptionPane.showMessageDialog(padre,mensaje,titulo,JOptionPane.WARNING_MESSAGE);
	}

	public static String sustituirParams(String cad, String par1) {
		int i = cad.indexOf("%1");
		if (i > 0)
			cad = cad.substring(0, i) + par1 + cad.substring(i + 2);
		return cad;	
	}

	public static String sustituirParams(String cad, String par1, String par2) {
		int i = cad.indexOf("%1");
		if (i > 0)
			cad = cad.substring(0, i) + par1 + cad.substring(i + 2);
		i = cad.indexOf("%2");
		if (i > 0)
			cad = cad.substring(0, i) + par2 + cad.substring(i + 2);
		return cad;	
	}

	// Resta dos horas en segundos
	public static long restarHoras(String horaInic, String horaFin) {
		int h, m, s, h2, m2, s2;
		long si, sf, dif;

		h = Integer.parseInt(horaInic.substring(0,2));
		m = Integer.parseInt(horaInic.substring(3,5));
		s = Integer.parseInt(horaInic.substring(6,8));
		h2 = Integer.parseInt(horaFin.substring(0,2));
		m2 = Integer.parseInt(horaFin.substring(3,5));
		s2 = Integer.parseInt(horaFin.substring(6,8));
		si = h*60*60+m*60+s;
		sf = h2*60*60+m2*60+s2;
		dif = sf-si;
		if (dif < 0)
			dif = 0;

		return dif;
	}

}

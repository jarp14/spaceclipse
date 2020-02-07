package spaceclipse.util;

public class Utiles {

	public static boolean entreDosPuntos(int x, int p1, int p2) {
		int min,max;
		if (p1 <= p2){
			min = p1;
			max = p2;
		} else {
			min = p2;
			max = p1;
		}
		return (min<=x && x<=max);
	}

	public static boolean enArea(int x, int y, int xArea, int yArea, int AnchoArea, int AltoArea) {
		return entreDosPuntos(x,xArea,xArea+AnchoArea-1) && entreDosPuntos(y,yArea,yArea+AltoArea-1);
	}

	public static boolean enLinea(int x, int y, int x1, int y1, int x2, int y2) {
		float pendLinea, pendPunto;

		if (!(y2-y1==0 || y2-y==0)) {
			if (x2>=x1 && y2>=y1) { // Cuadrante 4
				pendLinea = (float)(x2-x1)/(float)(y2-y1);
				pendPunto = (float)(x2-x)/(float)(y2-y);
			} else
				if (x2>=x1 && y2<=y1) { // Cuadrante 1
					pendLinea = (float)(x2-x1)/(float)(y1-y2);
					pendPunto = (float)(x2-x)/(float)(y-y2);
				} else
					if (x2<=x1 && y2>=y1) { // Cuadrante 3
						pendLinea = (float)(x1-x2)/(float)(y2-y1);
						pendPunto = (float)(x-x2)/(float)(y2-y);
					} else { // Cuadrante 2
						pendLinea = (float)(x1-x2)/(float)(y1-y2);
						pendPunto = (float)(x-x2)/(float)(y-y2);
					}
			if (entreDosPuntos(x,x1,x2) && entreDosPuntos(y,y1,y2))
				if (0.125<=pendLinea && pendLinea<=8) { // Ni muy vertical ni muy horizontal
					if (pendPunto*0.8<=pendLinea && pendLinea<=pendPunto*1.2) // 30% de error
						return true;
				} else
					return true;
		} else // Pendiente infinito (linea horizontal)
			if (entreDosPuntos(x,x1,x2) && entreDosPuntos(y,y1-1,y1+1))
				return true;

		return false;
	}

	public static double distanciaEntrePuntos (int x1, int y1, int x2, int y2){
		return Math.sqrt(Math.pow(y2-y1,2)+Math.pow(x2-x1,2));
	}

}

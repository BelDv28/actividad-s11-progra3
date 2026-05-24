package umg.actividad;

import java.util.Scanner;


public class Main {


    private static int pos;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            String linea = scanner.nextLine().trim();
            if (linea.isEmpty()) continue;

            pos = 0;                              // reiniciar posición
            StringBuilder resultado = new StringBuilder();
            extraerHojas(linea, resultado);
            System.out.println(resultado);
        }

        scanner.close();
    }

    private static boolean extraerHojas(String s, StringBuilder sb) {
        if (pos >= s.length()) return false;

        char c = s.charAt(pos++);

        // Nodo vacío no se  agrega nada, no es hoja
        if (c == '.') return false;

        // Es un nodo real: parsear hijo izquierdo y derecho
        boolean hijoIzqVacio = !extraerHojas(s, sb);   // true si hijo izq es '.'
        boolean hijoDerVacio = !extraerHojas(s, sb);   // true si hijo der es '.'

        // Es hoja si ambos hijos son vacíos
        if (hijoIzqVacio && hijoDerVacio) {
            sb.append(c);
            return true;  // sí es hoja
        }

        return true;  // nodo interno, pero sí existe
    }
}

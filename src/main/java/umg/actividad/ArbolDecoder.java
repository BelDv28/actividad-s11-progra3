package umg.actividad;

public class ArbolDecoder {

    private int pos;

    public String decodificar(String mensaje) {
        if (mensaje == null || mensaje.isBlank())
            throw new IllegalArgumentException("El mensaje no puede estar vacío.");
        pos = 0;
        StringBuilder sb = new StringBuilder();
        extraerHojas(mensaje.trim(), sb);
        if (sb.isEmpty())
            throw new IllegalArgumentException("No se encontraron hojas válidas.");
        return sb.toString();
    }

    private boolean extraerHojas(String s, StringBuilder sb) {
        if (pos >= s.length()) return false;
        char c = s.charAt(pos++);
        if (c == '.') return false;
        boolean izqVacio = !extraerHojas(s, sb);
        boolean derVacio = !extraerHojas(s, sb);
        if (izqVacio && derVacio) sb.append(c);
        return true;
    }
}

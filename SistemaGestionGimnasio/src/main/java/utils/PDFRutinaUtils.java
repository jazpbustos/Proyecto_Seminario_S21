package utils;

import entidad.Ejercicio;
import entidad.Rutinas;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.File;
import java.time.LocalDate;

public class PDFRutinaUtils {

    public static File exportarPDF(Rutinas rutina) {
        try {
            String carpeta = "rutinas_pdf/";
            String nombreArchivo = "Rutina_" + rutina.getNombre().replace(" ", "_") + ".pdf";
            File file = new File(carpeta + nombreArchivo);
            file.getParentFile().mkdirs();

            PDDocument doc = new PDDocument();
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            PDPageContentStream cs = new PDPageContentStream(doc, page);

            float x = 50;
            float y = 750;

            // ---------- TÍTULO ----------
            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA_BOLD, 22);
            cs.newLineAtOffset(x, y);
            cs.showText("Rutina: " + rutina.getNombre());
            cs.endText();
            y -= 40;

            // ---------- FECHAS ----------
            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA, 12);
            cs.newLineAtOffset(x, y);
            cs.showText("Inicio: " + rutina.getFechaInicio() +
                    "   |   Fin: " + rutina.getFechaFin());
            cs.endText();
            y -= 30;

            // ---------- DESCRIPCIÓN ----------
            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA, 14);
            cs.newLineAtOffset(x, y);
            cs.showText("Descripción:");
            cs.endText();
            y -= 20;

            String[] descLines = rutina.getDescripcion().split("\n");
            for (String l : descLines) {
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 12);
                cs.newLineAtOffset(x, y);
                cs.showText(l);
                cs.endText();
                y -= 18;
            }

            y -= 20;

            // ---------- EJERCICIOS ----------
            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA_BOLD, 14);
            cs.newLineAtOffset(x, y);
            cs.showText("Ejercicios:");
            cs.endText();
            y -= 25;

            for (Ejercicio ej : rutina.getEjercicios()) {
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 12);
                cs.newLineAtOffset(x, y);
                cs.showText("Día " + ej.getDia() + " - " +
                        ej.getNombre() + " (" + ej.getSeries() + "x" + ej.getReps() + ")");
                cs.endText();
                y -= 18;
            }

            y -= 30;

            // ---------- NOTAS SEMANALES ----------
            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA_BOLD, 14);
            cs.newLineAtOffset(x, y);
            cs.showText("Notas semanales:");
            cs.endText();
            y -= 25;

            int i = 1;
            for (String nota : rutina.getNotasSemanales()) {
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 12);
                cs.newLineAtOffset(x, y);
                cs.showText("Semana " + i + ": " + nota);
                cs.endText();
                y -= 18;
                i++;
            }

            cs.close();
            doc.save(file);
            doc.close();

            return file;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

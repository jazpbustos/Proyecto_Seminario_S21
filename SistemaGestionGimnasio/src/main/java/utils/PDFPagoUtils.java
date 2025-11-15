package utils;

import interfaz.PagosController;
import javafx.collections.ObservableList;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.time.format.DateTimeFormatter;

public class PDFPagoUtils {

    public static File exportarPDF(ObservableList<PagosController.Pago> lista) {

        try {
            // 🟧 1. Crear carpeta si no existe
            File carpeta = new File("pagos_pdf");
            if (!carpeta.exists()) {
                carpeta.mkdirs();
            }

            // 🟩 2. Archivo dentro de la carpeta
            File file = new File(carpeta, "listado_pagos_pdf.pdf");

            PDDocument doc = new PDDocument();
            PDPage page = new PDPage(PDRectangle.LETTER);
            doc.addPage(page);

            PDPageContentStream cs = new PDPageContentStream(doc, page);

            float y = 720;

            // 🔹 Título
            cs.setFont(PDType1Font.HELVETICA_BOLD, 12);
            cs.beginText();
            cs.newLineAtOffset(50, y);
            cs.showText("Listado de Pagos - Exportado");
            cs.endText();

            y -= 25;
            cs.setFont(PDType1Font.HELVETICA, 10);

            double totalRecaudado = 0;
            double totalDeuda = 0;

            // 🔹 Listado de pagos
            for (PagosController.Pago p : lista) {

                // Nueva página si nos quedamos sin espacio
                if (y < 60) {
                    cs.close();
                    page = new PDPage(PDRectangle.LETTER);
                    doc.addPage(page);
                    cs = new PDPageContentStream(doc, page);
                    y = 750;
                }

                // Acumular totales
                if (p.getEstado() != null) {
                    String est = p.getEstado().toLowerCase();
                    if (est.equals("pagó") || est.equals("pagado")) {
                        totalRecaudado += p.getMonto();
                    } else if (est.equals("adeuda") || est.equals("deuda") || est.equals("adeudado")) {
                        totalDeuda += p.getMonto();
                    }
                }

                // Escribir línea del pago
                cs.beginText();
                cs.newLineAtOffset(50, y);
                cs.showText(
                        p.getApellido() + ", " + p.getNombre() +
                                "  | DNI: " + p.getDni() +
                                "  | " + p.getActividad() +
                                "  | $" + p.getMonto() +
                                "  | " + p.getEstado() +
                                "  | " + p.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                );
                cs.endText();

                y -= 15;
            }

            // 🔹 Espacio antes de mostrar totales
            y -= 30;

            // Nueva página si no alcanza
            if (y < 100) {
                cs.close();
                page = new PDPage(PDRectangle.LETTER);
                doc.addPage(page);
                cs = new PDPageContentStream(doc, page);
                y = 750;
            }

            // 🔹 Totales finales
            cs.setFont(PDType1Font.HELVETICA_BOLD, 11);
            cs.beginText();
            cs.newLineAtOffset(50, y);
            cs.showText("Totales del período:");
            cs.endText();

            y -= 18;

            cs.setFont(PDType1Font.HELVETICA, 10);
            cs.beginText();
            cs.newLineAtOffset(50, y);
            cs.showText("Total recaudado: $" + String.format("%.2f", totalRecaudado));
            cs.endText();

            y -= 15;

            cs.beginText();
            cs.newLineAtOffset(50, y);
            cs.showText("Total adeudado : $" + String.format("%.2f", totalDeuda));
            cs.endText();

            cs.close();

            System.out.println("Guardando PDF en: " + file.getAbsolutePath());
            doc.save(file);
            doc.close();

            return file;

        } catch (Exception ex) {
            System.err.println("Error al generar PDF: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        }
    }

}

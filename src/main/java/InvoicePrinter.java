import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

public class InvoicePrinter
{
    // Output Path
    private final String outputPath = "./inv.pdf";

    // Fonts
    private final Font H1_FONT = FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD);
    private final Font H2_FONT = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
    private final Font H3_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);
    private final Font POS_FONT = FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL);
    private final Font ADDRESS_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);

    // Double formatter
    private final DecimalFormat df2 = new DecimalFormat(",###,###.00");

    // Euro Symbol
    final String EUROSYMBOL = "\u20ac" + "  ";


    public void print(Order order)
    {
        try
        {
            // Generate name
            //String invoiceName = "Invoice-" + UUID.randomUUID().toString() + ".pdf";
            String invoiceName = "Inv.pdf";

            // Create document
            Document document = new Document();
            document.setMargins(80,50,50,50);

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(invoiceName));
            document.open();

            // Add Header
            document.add(buildHeader());

            // Add invoice number
            Paragraph invoiceNumber = new Paragraph("Rechnung Nr. 3000492456", H1_FONT);
            invoiceNumber.setSpacingBefore(80);
            document.add(invoiceNumber);

            // Add invoice date
            Paragraph invoiceDate = new Paragraph("Rechnungsdatum 18.04.2022", H2_FONT);
            document.add(invoiceDate);

            // Add ticket table
            PdfPTable ticketTable = buildTicketTable();
            double total = 0.0;
            for(Ticket t : order.getTickets())
            {
                total += t.getPrice();
                addTicketPosition(ticketTable, t);
            }
            addTicketTableFooter(ticketTable, total, 0.1);

            document.add(ticketTable);

            document.close();
            writer.close();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    private PdfPTable buildHeader() throws DocumentException, IOException {

        //Configure table
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100); //Width 100%
        header.setWidths(new float[]{0.7f, 0.3f});

        // Logo cell
        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(0);
        Image logo = Image.getInstance("./assets/logo.png");
        logo.scaleAbsolute(110,200);
        logoCell.addElement(logo);

        // Address block
        PdfPTable addressTable = new PdfPTable(1);

        PdfPCell addr1 = new PdfPCell();
        addr1.setBorder(0);
        addr1.setPaddingTop(0);
        addr1.setPaddingBottom(0);
        addr1.addElement(new Paragraph("Ticketline 4.0 GmbH", ADDRESS_FONT));

        PdfPCell addr2 = new PdfPCell();
        addr2.setBorder(0);
        addr2.setPaddingTop(0);
        addr2.setPaddingBottom(0);
        addr2.addElement(new Paragraph("Hauptstrasse 1", ADDRESS_FONT));

        PdfPCell addr3 = new PdfPCell();
        addr3.setBorder(0);
        addr3.setPaddingTop(0);
        addr3.setPaddingBottom(0);
        addr3.addElement(new Paragraph("1010 - Wien", ADDRESS_FONT));

        PdfPCell addr4 = new PdfPCell();
        addr4.setBorder(0);
        addr4.setPaddingTop(0);
        addr4.setPaddingBottom(0);
        addr4.addElement(new Paragraph("\noffice@ticketline4.at", ADDRESS_FONT));

        PdfPCell addr5 = new PdfPCell();
        addr5.setBorder(0);
        addr5.setPaddingTop(0);
        addr5.setPaddingBottom(0);
        addr5.addElement(new Paragraph("www.ticketline4.at", ADDRESS_FONT));

        addressTable.addCell(addr1);
        addressTable.addCell(addr2);
        addressTable.addCell(addr3);
        addressTable.addCell(addr4);
        addressTable.addCell(addr5);


        PdfPCell address = new PdfPCell();
        address.setBorder(0);
        address.addElement(addressTable);

        header.addCell(logoCell);
        header.addCell(address);

        return header;
    }

    private PdfPTable buildTicketTable() throws DocumentException
    {
        // Create table
        PdfPTable table = new PdfPTable(2); // 5 columns.
        table.setWidthPercentage(100); //Width 100%
        table.setSpacingBefore(10f); //Space before table

        //Set Column widths
        float[] columnWidths = {0.9f, 0.1f};
        table.setWidths(columnWidths);

        String[] titles = {"Ticket","Preis"};

        for (int i = 0; i < titles.length; i++)
        {
            PdfPCell cell = new PdfPCell(new Paragraph(titles[i], H3_FONT));
            cell.setPaddingBottom(6);
            cell.setPaddingTop(3);
            cell.setBorderWidth(0f);
            cell.setBorderWidthTop(0f);
            cell.setBorderWidthBottom(1f);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_TOP);
            table.addCell(cell);
        }

        return table;
    }

    private void addTicketPosition(PdfPTable ticketTable, Ticket ticket)
    {
        PdfPCell titleCell = new PdfPCell(new Paragraph(ticket.getTitle(), POS_FONT));
        titleCell.setPaddingBottom(5);
        titleCell.setBorderWidth(0f);
        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);


        PdfPCell priceCell = new PdfPCell(new Paragraph(priceToString(ticket.getPrice()), POS_FONT));
        priceCell.setPaddingBottom(5);
        priceCell.setBorderWidth(0f);
        priceCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        ticketTable.addCell(titleCell);
        ticketTable.addCell(priceCell);
    }

    private void addTicketTableFooter(PdfPTable ticketTable, double total, double tax)
    {
        // Add bottom line
        PdfPCell lineCell = new PdfPCell();
        lineCell.setBorderWidth(0);
        lineCell.setBorderWidthTop(1f);

        for (int i = 0; i < ticketTable.getNumberOfColumns(); i++)
        {
            ticketTable.addCell(lineCell);
        }

        // Net total
        PdfPCell netTotalTextCell = new PdfPCell(new Paragraph("Gesamtsumme exkl. Ust. ", POS_FONT));
        netTotalTextCell.setBorderWidth(0);
        netTotalTextCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        ticketTable.addCell(netTotalTextCell);

        PdfPCell netTotalCell = new PdfPCell(new Paragraph(priceToString(total/(1+tax)), POS_FONT));
        netTotalCell.setBorderWidth(0);
        netTotalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        ticketTable.addCell(netTotalCell);

        // Tax
        PdfPCell taxTextCell = new PdfPCell(new Paragraph("" + (int)(tax*100) + "% Ust. ", POS_FONT));
        taxTextCell.setBorderWidth(0);
        taxTextCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        ticketTable.addCell(taxTextCell);

        PdfPCell taxCell = new PdfPCell(new Paragraph(priceToString(total-(total/(1+tax))), POS_FONT));
        taxCell.setBorderWidth(0);
        taxCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        ticketTable.addCell(taxCell);

        // Total
        PdfPCell totalTextCell = new PdfPCell(new Paragraph("Gesamtsumme inkl. USt.", H3_FONT));
        totalTextCell.setBorderWidth(0);
        totalTextCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        ticketTable.addCell(totalTextCell);

        PdfPCell totalCell = new PdfPCell(new Paragraph(priceToString(total), H3_FONT));
        totalCell.setBorderWidth(0);
        totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        ticketTable.addCell(totalCell);
    }

    private String priceToString(double price)
    {
        String string = df2.format(price);

        if(string.length() == 3)
        {
            string = "0" + string;
        }

        String euros = string.substring(0, string.length() - 4);
        String cents = string.substring(string.length() - 4);

        euros = euros.replace(',',' ');
        cents = cents.replace('.',',');

        return euros + cents + EUROSYMBOL;
    }
}

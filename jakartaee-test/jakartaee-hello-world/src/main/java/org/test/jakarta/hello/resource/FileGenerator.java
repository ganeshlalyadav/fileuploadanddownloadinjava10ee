package org.test.jakarta.hello.resource;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.test.jakarta.hello.dto.ProductDto;
import org.test.jakarta.hello.repository.ProductRepository;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Path("/download")
public class FileGenerator{

    @Inject
    private ProductRepository productRepository;

    @GET
    @Path("/all")
    @Produces({MediaType.APPLICATION_OCTET_STREAM})
    public Response downloadExpenses(@QueryParam("format") @DefaultValue("excel") String format) {
        if ("pdf".equalsIgnoreCase(format)) {
            return downloadExpensesAsPdf();
        } else {
            return downloadExpensesAsExcel();
        }
    }

    private Response downloadExpensesAsExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Products");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"id", "name", "Price"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            List<ProductDto> products = productRepository.getAllProduct();
            populateExpensesData(sheet, products, workbook);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            byte[] excelBytes = outputStream.toByteArray();

            return Response.ok(excelBytes)
                    .header("Content-Disposition", "attachment; filename=\"expenses.xlsx\"")
                    .type("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    private Response downloadExpensesAsPdf() {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Products");

            XSSFRow headerRow = sheet.createRow(0);
            String[] headers = {"id", "Name", "Price"};
            List<String> boldHeaders = Arrays.asList("id", "Name", "Price");
            createHeaderRowWithFormatting(workbook, sheet, headers, boldHeaders);

            List<ProductDto> products = productRepository.getAllProduct();
            populateExpensesData(sheet, products, workbook);

            ByteArrayOutputStream excelOutputStream = new ByteArrayOutputStream();
            workbook.write(excelOutputStream);
            workbook.close();

            try (PDDocument pdfDocument = new PDDocument()) {
                PDPage page = new PDPage();
                pdfDocument.addPage(page);

                try (PDPageContentStream contentStream = new PDPageContentStream(pdfDocument, page)) {
                    contentStream.setFont(PDType1Font.HELVETICA, 12);
                    writeExcelDataToPdf(contentStream, excelOutputStream.toByteArray());
                }

                ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
                pdfDocument.save(pdfOutputStream);
                byte[] pdfBytes = pdfOutputStream.toByteArray();

                return Response.ok(pdfBytes)
                        .header("Content-Disposition", "attachment; filename=\"expenses.pdf\"")
                        .type("application/pdf")
                        .build();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    private void writeExcelDataToPdf(PDPageContentStream contentStream, byte[] excelOutputStream) throws IOException {
        try (XSSFWorkbook excelWorkbook = new XSSFWorkbook(new ByteArrayInputStream(excelOutputStream))) {
            XSSFSheet excelSheet = excelWorkbook.getSheetAt(0);
            int numRows = excelSheet.getPhysicalNumberOfRows();

            PDFont boldFont = PDType1Font.HELVETICA_BOLD;
            PDFont regularFont = PDType1Font.HELVETICA;

            for (int i = 0; i < numRows; i++) {
                XSSFRow row = excelSheet.getRow(i);
                if (row != null) {
                    int numCells = row.getPhysicalNumberOfCells();
                    for (int cellNum = 0; cellNum < numCells; cellNum++) {
                        XSSFCell cell = row.getCell(cellNum);
                        if (cell != null) {
                            String cellValue = cell.toString();

                            if (i == 0) {
                                contentStream.setFont(boldFont, 12);
                            } else {
                                contentStream.setFont(regularFont, 12);
                            }

                            contentStream.beginText();
                            contentStream.newLineAtOffset(50 + cellNum * 100, 700 - i * 20);
                            contentStream.showText(cellValue);
                            contentStream.endText();
                        }
                    }
                }
            }
        }
    }

    private void createHeaderRowWithFormatting(XSSFWorkbook workbook, XSSFSheet sheet, String[] headers, List<String> boldHeaders) {
        XSSFRow headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue((headers[i]));
            if (boldHeaders.contains(headers[i])) {
                XSSFFont headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setFontHeightInPoints((short) 14);
                XSSFCellStyle headerCellStyle = workbook.createCellStyle();
                headerCellStyle.setFont(headerFont);
                cell.setCellStyle(headerCellStyle);
            }
        }
    }

    private void populateExpensesData(Sheet sheet, List<ProductDto> expenses, Workbook workbook) {
        int rowNum = 1;
        for (ProductDto product : expenses) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(product.getId());
            row.createCell(1).setCellValue(product.getName());
            row.createCell(2).setCellValue(product.getPrice());
        }
    }}

package org.test.jakarta.hello.controller;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.test.jakarta.hello.model.ExcelReader;
import org.test.jakarta.hello.resource.ExcelWriter;

import java.io.IOException;
import java.io.InputStream;

@Path("/excel")
public class ExcelResource {

    @POST
    @Path("/read")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response readExcel(InputStream inputStream) {
        try {
            ExcelReader reader = new ExcelReader();
            reader.readExcel(inputStream);
            return Response.ok("File processed successfully").build();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error processing file: " + e.getMessage()).build();
        }
    }

    @POST
    @Path("/write")
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public Response writeExcel() {
        try {
            ExcelWriter writer = new ExcelWriter();
            byte[] excelFile = writer.writeExcel();

            return Response.ok(excelFile)
                    .header("Content-Disposition", "attachment; filename=\"sample.xlsx\"")
                    .build();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error writing file: " + e.getMessage()).build();
        }
    }
}

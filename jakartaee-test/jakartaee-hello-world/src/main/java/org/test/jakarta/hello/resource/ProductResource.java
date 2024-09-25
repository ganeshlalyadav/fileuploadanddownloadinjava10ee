package org.test.jakarta.hello.resource;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.test.jakarta.hello.model.Product;
import org.test.jakarta.hello.repository.ProductRepository;
import java.util.List;

@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    private ProductRepository productRepository;

    @POST
    public Response create(Product product) {
        productRepository.create(product);
        return Response.ok(product).build();
    }

    @GET
    @Path("/{id}")
    public Product find(@PathParam("id") Long id) {
        return productRepository.find(id);
    }

    @PUT
    @Path("/{id}")
    public Product update(@PathParam("id") Long id, Product product) {
        product.setId(id);
        return productRepository.update(product);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        productRepository.delete(id);
        return Response.noContent().build();
    }

    @GET
    public List<Product> findAll() {
        return productRepository.findAll();
    }
}

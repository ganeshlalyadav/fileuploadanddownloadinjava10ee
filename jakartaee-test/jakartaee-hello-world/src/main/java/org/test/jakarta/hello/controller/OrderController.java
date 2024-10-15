package org.test.jakarta.hello.controller;

import jakarta.ejb.EJB;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.test.jakarta.hello.model.OrderEntity;
import org.test.jakarta.hello.resource.OrderService;

@Path("/order")
public class OrderController {

    @EJB
    private OrderService orderService;

    @POST
    @Path("/create")
    @Consumes("application/json")
    @Produces("application/json")
    public String createOrder(OrderEntity order) {
        try {
            orderService.processOrder(order);
            return "Order processed successfully with ID: " + order.getId();
        } catch (Exception e) {
            return "Error processing order: " + e.getMessage();
        }
    }
}

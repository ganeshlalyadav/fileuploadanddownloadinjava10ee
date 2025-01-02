package com.example.locking;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Path("/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItemController {

    @Inject
    private LockingService lockingService;

    @POST
    public Response saveItem(Item item) {
        Item savedItem = lockingService.saveItem(item);
        return Response.ok(savedItem).build();
    }

    @POST
    @Path("/simulate-concurrent-updates")
    @Produces(MediaType.APPLICATION_JSON)
    public Response simulateConcurrentOptimistic(@QueryParam("id") Long itemId, @QueryParam("newName1") String newName1, @QueryParam("newName2") String newName2) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<Void> task1 = () -> {
            lockingService.updateWithOptimisticLock(itemId, newName1);
            return null;
        };

        Callable<Void> task2 = () -> {
            lockingService.updateWithOptimisticLock(itemId, newName2);
            return null;
        };

        try {
            List<Future<Void>> futures = executorService.invokeAll(List.of(task1, task2));
            for (Future<Void> future : futures) {
                future.get();
            }
            return Response.ok("Both updates completed successfully").build();
        } catch (Exception e) {
            return Response.status(Response.Status.CONFLICT).entity("Conflict occurred: " + e.getMessage()).build();
        } finally {
            executorService.shutdown();
        }
    }

    @POST
    @Path("/simulate-concurrent-pessimistic")
    @Produces(MediaType.APPLICATION_JSON)
    public Response simulateConcurrentPessimistic(@QueryParam("id") Long itemId, @QueryParam("newName1") String newName1, @QueryParam("newName2") String newName2) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<Void> task1 = () -> {
            lockingService.updateWithPessimisticLock(itemId, newName1);
            return null;
        };

        Callable<Void> task2 = () -> {
            lockingService.updateWithPessimisticLock(itemId, newName2);
            return null;
        };

        try {
            List<Future<Void>> futures = executorService.invokeAll(List.of(task1, task2));
            for (Future<Void> future : futures) {
                future.get();
            }
            return Response.ok("Both updates completed successfully").build();
        } catch (Exception e) {
            return Response.status(Response.Status.CONFLICT).entity("Conflict occurred: " + e.getMessage()).build();
        } finally {
            executorService.shutdown();
        }
    }


}

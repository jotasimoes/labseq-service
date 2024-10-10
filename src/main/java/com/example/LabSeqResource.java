package com.example;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

@Path("/labseq")
@OpenAPIDefinition(
        info = @Info(
                title = "Labseq API",
                version = "1.0",
                description = "API for calculating Labseq sequence"
        )
)
public class LabSeqResource {

    private Map<Integer, Long> cache = new HashMap<>();

    @GET
    @Path("/{n}")
    @Produces(MediaType.TEXT_PLAIN)
    @APIResponses(value = {
            @APIResponse(
                    responseCode = "200",
                    description = "Successful operation",
                    content = @Content(mediaType = "text/plain",
                            schema = @Schema(implementation = String.class))
            ),
            @APIResponse(
                    responseCode = "400",
                    description = "Invalid input"
            )
    })
    public Response getLabSeq(
            @Parameter(description = "Index of the Labseq sequence", required = true)
            @PathParam("n") String nStr
    ) {
        try {
            int n = Integer.parseInt(nStr);
            if (n < 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Error: Index must be non-negative")
                        .build();
            }
            long result = calculateLabSeq(n);
            return Response.ok(String.valueOf(result)).build();
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error: Invalid input. Provide a non-negative integer")
                    .build();
        }
    }

    private long calculateLabSeq(int n) {
        if (cache.containsKey(n)) {
            return cache.get(n);
        }

        long result;
        if (n == 0 || n == 2) result = 0;
        else if (n == 1 || n == 3) result = 1;
        else result = calculateLabSeq(n - 4) + calculateLabSeq(n - 3);

        cache.put(n, result);
        return result;
    }
}
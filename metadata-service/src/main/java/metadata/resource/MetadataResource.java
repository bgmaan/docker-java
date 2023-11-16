package metadata.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import metadata.model.Metadata;
import metadata.service.MetadataService;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Path("/metadata")
public class MetadataResource {

    @Inject
    MetadataService metadataService;

    @POST
    @Path("/")
    public Response postMetadata(Metadata metadata) {
        Long id;
        try {
            id = metadataService.processMetadata(metadata);
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok(id).build();
    }

    @GET
    @Path("/{id}")
    public Response getMetadata(@PathParam("id")long id) {
        Metadata metadata;
        try {
            metadata = metadataService.getMetadataById(id);
            if (Objects.isNull(metadata)) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok(metadata).build();
    }

    @DELETE
    public Response deleteMetadata(@QueryParam("id") String id) {
        List<Long> convertedCountriesList;
        if (id.length() > 200) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        try {
            convertedCountriesList =
                    Stream.of(id.split(",", -1)).map(Long::parseLong).collect(Collectors.toList());
            metadataService.deleteAllByListOfId(convertedCountriesList);
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok(convertedCountriesList).build();
    }
}

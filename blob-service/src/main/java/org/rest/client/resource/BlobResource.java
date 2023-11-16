package org.rest.client.resource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.rest.client.exception.InternalServerErrorException;
import org.rest.client.resource.model.MultipartBody;
import org.rest.client.resource.model.NewSongResponse;
import org.rest.client.song.db.entities.SongBlob;
import org.rest.client.song.service.SongService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Path("/blob")
@ApplicationScoped
public class BlobResource {

    @Inject
    SongService songService;

    @POST
    @Path("/")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response postAudio(@MultipartForm MultipartBody data) {
        NewSongResponse newSongResponse;

        try {
            newSongResponse = songService.processSong(data.getFile());
        } catch (InternalServerErrorException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.ok(newSongResponse).build();
    }

    @GET
    @Path("/{id}")
    public Response getSongData(@PathParam("id") long id) {
        SongBlob songBlob;
        try {
            songBlob = songService.getSongById(id);
            if (Objects.isNull(songBlob)) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok(songBlob).build();
    }

    @DELETE
    public Response deleteSongs(@QueryParam("id") String id) {
        if (id.length() > 200) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        List<Long> listOfIds;
        try {
            listOfIds = Stream.of(id.split(",", -1)).map(Long::parseLong).collect(Collectors.toList());
            songService.deleteAllByListOfId(listOfIds);
        } catch (InternalServerErrorException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.ok(listOfIds).build();
    }
}

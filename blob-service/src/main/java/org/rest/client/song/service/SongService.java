package org.rest.client.song.service;

import io.quarkus.eureka.client.EurekaClient;
import io.quarkus.eureka.client.loadBalancer.LoadBalanced;
import io.quarkus.eureka.client.loadBalancer.LoadBalancerType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.client.Entity;
import org.apache.commons.io.IOUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.jboss.logging.Logger;
import org.rest.client.exception.InternalServerErrorException;
import org.rest.client.resource.model.NewSongResponse;
import org.rest.client.song.db.BlobRepository;
import org.rest.client.song.db.entities.SongBlob;
import org.rest.client.song.model.MetadataRequest;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@ApplicationScoped
public class SongService {
    private static final Logger LOG = Logger.getLogger(SongService.class);
    private static final String MIME_TYPE = "audio/mpeg";
    private static final String MIME_ERROR = "Incorrect mime type!";
    public static final String CANNOT_PARSE_METADATA = "Cannot parse metadata";
    public static final String DELETE_BY_ID_QUERY = "id in (?1)";
    public static final String METADATA_SERVICE_NAME = "METADATA";
    public static final String METADATA_PATH = "/metadata";

    @Inject
    BlobRepository blobRepository;

    @Inject
    @LoadBalanced(type = LoadBalancerType.ROUND_ROBIN)
    public EurekaClient eurekaClient;

    @Transactional
    public NewSongResponse processSong(InputStream songStream) throws InternalServerErrorException {
        byte[] byteArray = getByteArrayFromInputStream(songStream);
        checkIfFileIsCorrect(byteArray);
        Long id = persistSong(byteArray);
        sendRequestToMetadataService(byteArray, id);
        return new NewSongResponse(id.toString());
    }

    private void sendRequestToMetadataService(byte[] byteArray, Long id) throws InternalServerErrorException {
        eurekaClient.app(METADATA_SERVICE_NAME).path(METADATA_PATH).request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON_TYPE).post(Entity.json(parseMetadata(byteArray, id)));
    }

    private static byte[] getByteArrayFromInputStream(InputStream songStream) throws InternalServerErrorException {
        byte[] byteArray;
        try {
            byteArray = IOUtils.toByteArray(songStream);
            songStream.close();
        } catch (IOException e) {
            LOG.error(e.getMessage());
            throw new InternalServerErrorException(CANNOT_PARSE_METADATA);
        }
        return byteArray;
    }

    public SongBlob getSongById(long id) {
        return SongBlob.findById(id);
    }

    @Transactional
    public void deleteAllByListOfId(List<Long> ids) throws InternalServerErrorException {
        try {
            blobRepository.delete(DELETE_BY_ID_QUERY, ids);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new InternalServerErrorException(CANNOT_PARSE_METADATA);
        }
    }

    private Long persistSong(byte[] bytes) {
        SongBlob songBlob = new SongBlob();
        songBlob.setBlob(bytes);
        blobRepository.persist(songBlob);
        blobRepository.flush();
        return songBlob.getId();
    }

    private MetadataRequest parseMetadata(byte[] bytes, long id) throws InternalServerErrorException {
        try {
            InputStream targetStream = new ByteArrayInputStream(bytes);
            ContentHandler handler = new DefaultHandler();
            Metadata metadata = new Metadata();
            Parser parser = new Mp3Parser();
            ParseContext parseCtx = new ParseContext();
            parser.parse(targetStream, handler, metadata, parseCtx);
            targetStream.close();
            MetadataRequest metadataRequest = MetadataRequest.fromMetadata(metadata, id);
            return metadataRequest;
        } catch (IOException | SAXException | TikaException e) {
            LOG.error(e.getMessage());
            throw new InternalServerErrorException(CANNOT_PARSE_METADATA);
        }
    }

    private void checkIfFileIsCorrect(byte[] byteArray) throws InternalServerErrorException {
        InputStream targetStream = new ByteArrayInputStream(byteArray);
        MediaType mimetype;
        try {
            TikaConfig tika = new TikaConfig();
            mimetype = tika.getDetector().detect(TikaInputStream.get(targetStream), new Metadata());
            targetStream.close();
        } catch (IOException | TikaException e) {
            LOG.error(e.getMessage());
            throw new InternalServerErrorException(CANNOT_PARSE_METADATA);
        }
        if (!mimetype.toString().equals(MIME_TYPE)) {
            throw new InternalServerErrorException(MIME_ERROR);
        }
    }
}

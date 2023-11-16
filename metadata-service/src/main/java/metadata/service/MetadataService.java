package metadata.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import metadata.db.MetadataRepository;
import metadata.model.Metadata;

import java.util.List;

@ApplicationScoped
public class MetadataService {

    public static final String DELETE_RESOURCE_BY_ID_QUERY = "resourceId in (?1)";
    public static final String ID = "id";
    @Inject
    MetadataRepository metadataRepository;


    @Transactional
    public Long processMetadata(Metadata metadata) {
        metadataRepository.persist(metadata);
        metadataRepository.flush();
        return metadata.getId();
    }

    public Metadata getMetadataById(long id) {
        return metadataRepository.find(ID, id).firstResult();
    }

    @Transactional
    public void deleteAllByListOfId(List<Long> ids) {
        metadataRepository.delete(DELETE_RESOURCE_BY_ID_QUERY, ids);
    }
}

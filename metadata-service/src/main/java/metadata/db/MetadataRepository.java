package metadata.db;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import metadata.model.Metadata;
@ApplicationScoped
public class MetadataRepository  implements PanacheRepository<Metadata> {}

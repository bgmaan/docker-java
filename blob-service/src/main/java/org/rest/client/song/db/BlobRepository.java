package org.rest.client.song.db;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.rest.client.song.db.entities.SongBlob;
@ApplicationScoped
public class BlobRepository implements PanacheRepository<SongBlob> {}

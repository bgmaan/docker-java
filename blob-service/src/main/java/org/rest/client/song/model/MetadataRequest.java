package org.rest.client.song.model;

import jakarta.persistence.Id;
import org.apache.tika.metadata.Metadata;

public class MetadataRequest {
    @Id
    private Long resourceId;
    private String name;

    private String artist;
    private String album;

    private String length;

    private String year;

    public MetadataRequest(String name, String artist, String album, String length, Long resourceId, String year) {
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.length = length;
        this.resourceId = resourceId;
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public static MetadataRequest fromMetadata(Metadata metadata, Long resourceId) {
        return new MetadataRequest(metadata.get("xmpDM:name"), metadata.get("xmpDM:artist"), metadata.get("xmpDM:album"), metadata.get("xmpDM:duration"), resourceId, metadata.get("xmpDM:year"));
    }
}

package com.example.booking.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "resources")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private String location;

    @Column(nullable = false)
    private boolean available = true;

    public Resource() {
    }

    public Resource(Long id, String name, String description, String location, boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.available = available;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isAvailable() {
        return available;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public static ResourceBuilder builder() {
        return new ResourceBuilder();
    }

    public static class ResourceBuilder {
        private Long id;
        private String name;
        private String description;
        private String location;
        private boolean available = true;

        ResourceBuilder() {
        }

        public ResourceBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ResourceBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ResourceBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ResourceBuilder location(String location) {
            this.location = location;
            return this;
        }

        public ResourceBuilder available(boolean available) {
            this.available = available;
            return this;
        }

        public Resource build() {
            return new Resource(id, name, description, location, available);
        }
    }
}

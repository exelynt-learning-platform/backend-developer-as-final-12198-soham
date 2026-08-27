package com.example.booking.dto.resource;

public class ResourceResponse {

    private Long id;
    private String name;
    private String description;
    private String location;
    private boolean available;

    public ResourceResponse() {
    }

    public ResourceResponse(Long id, String name, String description, String location, boolean available) {
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

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public static ResourceResponseBuilder builder() {
        return new ResourceResponseBuilder();
    }

    public static class ResourceResponseBuilder {
        private Long id;
        private String name;
        private String description;
        private String location;
        private boolean available;

        ResourceResponseBuilder() {
        }

        public ResourceResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public ResourceResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ResourceResponseBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ResourceResponseBuilder location(String location) {
            this.location = location;
            return this;
        }

        public ResourceResponseBuilder available(boolean available) {
            this.available = available;
            return this;
        }

        public ResourceResponse build() {
            return new ResourceResponse(id, name, description, location, available);
        }
    }
}

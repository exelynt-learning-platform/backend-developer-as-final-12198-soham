package com.example.booking.dto.resource;

import jakarta.validation.constraints.NotBlank;

public class ResourceRequest {

    @NotBlank(message = "Resource name is required")
    private String name;

    private String description;

    private String location;

    private boolean available = true;

    public ResourceRequest() {
    }

    public ResourceRequest(String name, String description, String location, boolean available) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.available = available;
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

    public static ResourceRequestBuilder builder() {
        return new ResourceRequestBuilder();
    }

    public static class ResourceRequestBuilder {
        private String name;
        private String description;
        private String location;
        private boolean available = true;

        ResourceRequestBuilder() {
        }

        public ResourceRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ResourceRequestBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ResourceRequestBuilder location(String location) {
            this.location = location;
            return this;
        }

        public ResourceRequestBuilder available(boolean available) {
            this.available = available;
            return this;
        }

        public ResourceRequest build() {
            return new ResourceRequest(name, description, location, available);
        }
    }
}

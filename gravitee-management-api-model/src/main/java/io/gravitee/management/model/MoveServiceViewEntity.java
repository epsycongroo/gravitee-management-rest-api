package io.gravitee.management.model;

import javax.validation.constraints.NotNull;

/**
 * @author Jokki
 */
public class MoveServiceViewEntity {

    private int prev;

    private int post;

    @NotNull
    private ServiceViewEntity serviceViewEntity;

    public int getPost() {
        return post;
    }

    public int getPrev() {
        return prev;
    }

    public ServiceViewEntity getServiceViewEntity() {
        return serviceViewEntity;
    }

    public void setPost(int post) {
        this.post = post;
    }

    public void setPrev(int prev) {
        this.prev = prev;
    }

    public void setServiceViewEntity(ServiceViewEntity serviceViewEntity) {
        this.serviceViewEntity = serviceViewEntity;
    }
}

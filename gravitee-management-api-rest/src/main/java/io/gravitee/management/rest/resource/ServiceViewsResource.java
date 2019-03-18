/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.management.rest.resource;

import io.gravitee.common.http.MediaType;
import io.gravitee.management.model.ServiceViewEntity;
import io.gravitee.management.model.permissions.RolePermission;
import io.gravitee.management.model.permissions.RolePermissionAction;
import io.gravitee.management.rest.security.Permission;
import io.gravitee.management.rest.security.Permissions;
import io.gravitee.management.service.ServiceViewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Jokki
 */
@Api(tags = "ServiceViews")
public class ServiceViewsResource extends AbstractResource {

    @Autowired
    private ServiceViewService viewService;

    @Context
    private ResourceContext resourceContext;


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation("获取服务资源目录")
    public List<ServiceViewEntity> list(@QueryParam("all") boolean all)  {
        boolean viewAll = (all && hasPermission(RolePermission.PORTAL_VIEW, RolePermissionAction.UPDATE, RolePermissionAction.CREATE, RolePermissionAction.DELETE));

        return viewService.findAll()
                .stream()
                .filter(v -> viewAll || !v.isHidden())
                .sorted(Comparator.comparingInt(ServiceViewEntity::getOrder))
                .collect(Collectors.toList());
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Permissions({
            @Permission(value = RolePermission.PORTAL_VIEW, acls = RolePermissionAction.CREATE)
    })
    @ApiOperation("创建服务资源")
    public ServiceViewEntity create(@Valid @NotNull final ServiceViewEntity view) {
        return viewService.create(view);
    }

    @Path("{id}")
    public ServiceViewResource getViewResource() {
        return resourceContext.getResource(ServiceViewResource.class);
    }
}

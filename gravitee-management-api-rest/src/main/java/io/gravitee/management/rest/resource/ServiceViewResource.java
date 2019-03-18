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
import io.gravitee.management.model.MoveServiceViewEntity;
import io.gravitee.management.model.ServiceViewEntity;
import io.gravitee.management.model.permissions.RolePermission;
import io.gravitee.management.model.permissions.RolePermissionAction;
import io.gravitee.management.rest.security.Permission;
import io.gravitee.management.rest.security.Permissions;
import io.gravitee.management.service.ServiceViewService;
import io.gravitee.management.service.exceptions.UnauthorizedAccessException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static io.gravitee.common.http.MediaType.APPLICATION_JSON;

/**
 * @author Jokki
 */
@Api(tags = "ServiceView")
public class ServiceViewResource extends AbstractResource {

    @Autowired
    private ServiceViewService viewService;

    @GET
    @Produces(APPLICATION_JSON)
    @ApiOperation("获取服务资源详细信息")
    public ServiceViewEntity get(@PathParam("id") String viewId) {
        boolean canShowView = hasPermission(RolePermission.PORTAL_VIEW, RolePermissionAction.READ);
        ServiceViewEntity view = viewService.findById(viewId);

        if (canShowView || !view.isHidden()) {
            return view;
        }

        throw new UnauthorizedAccessException();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Permissions({
            @Permission(value = RolePermission.PORTAL_VIEW, acls = RolePermissionAction.UPDATE)
    })
    @ApiOperation("更新服务资源")
    public ServiceViewEntity update(@PathParam("id") String viewId, @Valid @NotNull final ServiceViewEntity view) {
        return viewService.update(viewId, view);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Permissions({
            @Permission(value = RolePermission.PORTAL_VIEW, acls = RolePermissionAction.UPDATE)
    })
    @ApiOperation("移动服务资源排列顺序")
    public Response move(@PathParam("id") String viewId, @Valid @NotNull final MoveServiceViewEntity view) {
        return Response.ok(viewService.move(view.getServiceViewEntity(), view.getPrev(), view.getPost())).build();
    }


    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Permissions({
            @Permission(value = RolePermission.PORTAL_VIEW, acls = RolePermissionAction.DELETE)
    })
    @ApiOperation("删除服务资源")
    public void delete(@PathParam("id") String id) {
        viewService.delete(id);
    }
}

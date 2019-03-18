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
package io.gravitee.management.service.impl;

import io.gravitee.common.utils.UUID;
import io.gravitee.management.model.ServiceViewEntity;
import io.gravitee.management.service.ApiService;
import io.gravitee.management.service.AuditService;
import io.gravitee.management.service.ServiceViewService;
import io.gravitee.management.service.exceptions.TechnicalManagementException;
import io.gravitee.management.service.exceptions.ViewNotFoundException;
import io.gravitee.repository.exceptions.TechnicalException;
import io.gravitee.repository.management.api.ServiceViewRepository;
import io.gravitee.repository.management.model.ServiceView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.gravitee.repository.management.model.Audit.AuditProperties.VIEW;
import static io.gravitee.repository.management.model.View.AuditEvent.VIEW_CREATED;
import static io.gravitee.repository.management.model.View.AuditEvent.VIEW_DELETED;
import static io.gravitee.repository.management.model.View.AuditEvent.VIEW_UPDATED;


/**
 * @author Jokki
 */
@Component
public class ServiceViewServiceImpl extends TransactionalService implements ServiceViewService {

    private final Logger logger = LoggerFactory.getLogger(ViewServiceImpl.class);

    private static final int ORDER_DEFAULT_VALUE = 1000;

    @Autowired
    private ServiceViewRepository viewRepository;

    @Autowired
    private ApiService apiService;

    @Autowired
    private AuditService auditService;

    @Override
    public List<ServiceViewEntity> findAll() {
        try {
            logger.debug("Find all service views");
            return viewRepository.findAll()
                    .stream()
                    .map(this::convert).collect(Collectors.toList());
        } catch (TechnicalException ex) {
            logger.error("An error occurs while trying to find all service views", ex);
            throw new TechnicalManagementException("An error occurs while trying to find all views", ex);
        }
    }

    @Override
    public ServiceViewEntity findById(String id) {
        try {
            logger.debug("Find service view by id : {}", id);
            Optional<ServiceView> view = viewRepository.findById(id);
            if (view.isPresent()) {
                return convert(view.get());
            }
            throw new ViewNotFoundException(id);
        } catch (TechnicalException ex) {
            logger.error("An error occurs while trying to find a service view using its ID: {}", id, ex);
            throw new TechnicalManagementException("An error occurs while trying to find a service view using its ID: " + id, ex);
        }
    }

    @Override
    public ServiceViewEntity create(ServiceViewEntity viewEntity) {
        try {
            ServiceView view = convert(viewEntity);
            Optional<ServiceView> max = viewRepository.findMaxOrderByPid(viewEntity.getPid());
            max.ifPresent(serviceView -> {
                if (serviceView.getOrder() + ORDER_DEFAULT_VALUE >= Integer.MAX_VALUE) {
                    view.setOrder(Integer.MAX_VALUE - ORDER_DEFAULT_VALUE);
                } else {
                    view.setOrder(serviceView.getOrder() + ORDER_DEFAULT_VALUE);
                }
            });
            view.setId(UUID.toString(UUID.random()));
            view.setCreatedAt(new Date());
            view.setUpdatedAt(view.getCreatedAt());
            ServiceViewEntity createdView = convert(viewRepository.create(view));
            auditService.createPortalAuditLog(
                    Collections.singletonMap(VIEW, view.getId()),
                    VIEW_CREATED,
                    new Date(),
                    null,
                    view);
            return createdView;
        } catch (TechnicalException ex) {
            logger.error("An error occurs while trying to create service view {}", viewEntity.getName(), ex);
            throw new TechnicalManagementException("An error occurs while trying to create service view " + viewEntity.getName(), ex);
        }
    }

    @Override
    public ServiceViewEntity update(String viewId, ServiceViewEntity viewEntity) {
        try {
            logger.debug("Update Service View {}", viewId);

            Optional<ServiceView> optViewToUpdate = viewRepository.findById(viewId);
            if (!optViewToUpdate.isPresent()) {
                throw new ViewNotFoundException(viewId);
            }

            ServiceView view = convert(viewEntity);
            view.setUpdatedAt(new Date());
            ServiceViewEntity updatedView = convert(viewRepository.update(view));
            auditService.createPortalAuditLog(
                    Collections.singletonMap(VIEW, view.getId()),
                    VIEW_UPDATED,
                    new Date(),
                    optViewToUpdate.get(),
                    view);

            return updatedView;
        } catch (TechnicalException ex) {
            logger.error("An error occurs while trying to update service view {}", viewEntity.getName(), ex);
            throw new TechnicalManagementException("An error occurs while trying to update service view " + viewEntity.getName(), ex);
        }
    }

    @Override
    public void delete(String viewId) {
        if (ServiceView.OTHER_ID.equals(viewId)) {
            logger.error("Delete the default Service view is forbidden");
            throw new TechnicalManagementException("Delete the default Service view is forbidden");
        }
        try {
            Optional<ServiceView> viewOptional = viewRepository.findById(viewId);
            if (viewOptional.isPresent()) {
                List<String> deleteIds = viewRepository.deleteByPid(viewId);
                auditService.createPortalAuditLog(
                        Collections.singletonMap(VIEW, viewId),
                        VIEW_DELETED,
                        new Date(),
                        null,
                        viewOptional.get());

                // delete all reference on APIs
                apiService.deleteViewFromAPIs(deleteIds);
            }
        } catch (TechnicalException ex) {
            logger.error("An error occurs while trying to delete Service view {}", viewId, ex);
            throw new TechnicalManagementException("An error occurs while trying to delete Service view " + viewId, ex);
        }
    }

    @Override
    public boolean move(ServiceViewEntity viewEntity, int prev, int post){
        try {
            if(post - prev <= 1) {
                viewRepository.updateOrderByPid(viewEntity.getPid(), post, ORDER_DEFAULT_VALUE);
            }
            viewEntity.setOrder(prev + 1);
            update(viewEntity.getId(), viewEntity);
            return true;
        } catch (TechnicalException e) {
            logger.error("An error occurs while trying to move Service view {}", viewEntity.getId(), e);
            throw new TechnicalManagementException("An error occurs while trying to move Service view "
                    + viewEntity.getId(), e);
        }
    }

    @Override
    public void createDefaultServiceView() {
        ServiceView view = new ServiceView();
        view.setId(ServiceView.OTHER_ID);
        view.setPid("root");
        view.setName("其他服务");
        view.setDescription("其他服务");
        view.setOrder(Integer.MIN_VALUE);
        view.setCreatedAt(new Date());
        view.setUpdatedAt(view.getCreatedAt());
        view.setAncestors(new String[]{});
        try{
            viewRepository.create(view);
        } catch (TechnicalException ex) {
            logger.error("An error occurs while trying to create view {}", view.getName(), ex);
            throw new TechnicalManagementException("An error occurs while trying to create view " + view.getName(), ex);
        }
    }


    private ServiceViewEntity convert(ServiceView serviceView) {
        ServiceViewEntity serviceViewEntity = new ServiceViewEntity();
        serviceViewEntity.setId(serviceView.getId());
        serviceViewEntity.setPid(serviceView.getPid());
        serviceViewEntity.setName(serviceView.getName());
        serviceViewEntity.setDescription(serviceView.getDescription());
        serviceViewEntity.setHidden(serviceView.isHidden());
        serviceViewEntity.setOrder(serviceView.getOrder());
        serviceViewEntity.setCreatedAt(serviceView.getCreatedAt());
        serviceViewEntity.setUpdatedAt(serviceView.getUpdatedAt());
        serviceViewEntity.setAncestors(serviceView.getAncestors());
        return serviceViewEntity;
    }

    private ServiceView convert(ServiceViewEntity serviceView) {
        ServiceView serviceViewEntity = new ServiceView();
        serviceViewEntity.setId(serviceView.getId());
        serviceViewEntity.setPid(serviceView.getPid());
        serviceViewEntity.setName(serviceView.getName());
        serviceViewEntity.setDescription(serviceView.getDescription());
        serviceViewEntity.setHidden(serviceView.isHidden());
        serviceViewEntity.setOrder(serviceView.getOrder());
        serviceViewEntity.setCreatedAt(serviceView.getCreatedAt());
        serviceViewEntity.setUpdatedAt(serviceView.getUpdatedAt());
        serviceViewEntity.setAncestors(serviceView.getAncestors());
        return serviceViewEntity;
    }
}

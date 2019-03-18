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
package io.gravitee.management.service;

import io.gravitee.management.model.ServiceViewEntity;

import java.util.List;

/**
 * @author Jokki
 */
public interface ServiceViewService {

    List<ServiceViewEntity> findAll();

    ServiceViewEntity findById(String id);

    ServiceViewEntity create(ServiceViewEntity view);

    ServiceViewEntity update(String viewId, ServiceViewEntity view);

    void delete(String viewId);

    void createDefaultServiceView();

    boolean move(ServiceViewEntity viewEntity, int prev, int post);
}

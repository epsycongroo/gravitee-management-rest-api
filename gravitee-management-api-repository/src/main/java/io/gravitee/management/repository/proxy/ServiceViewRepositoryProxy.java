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
package io.gravitee.management.repository.proxy;

import io.gravitee.repository.exceptions.TechnicalException;
import io.gravitee.repository.management.api.ServiceViewRepository;
import io.gravitee.repository.management.model.ServiceView;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Jokki
 */
@Component
public class ServiceViewRepositoryProxy extends AbstractProxy<ServiceViewRepository> implements ServiceViewRepository {
    @Override
    public Set<ServiceView> findAll() throws TechnicalException {
        return target.findAll();
    }

    @Override
    public Optional<ServiceView> findMaxOrderByPid(String pid) throws TechnicalException {
        return target.findMaxOrderByPid(pid);
    }

    @Override
    public List<String> deleteByPid(String pid) throws TechnicalException {
        return target.deleteByPid(pid);
    }

    @Override
    public void updateOrderByPid(String pid, int post, int number) throws TechnicalException {
        target.updateOrderByPid(pid, post, number);
    }

    @Override
    public Optional<ServiceView> findById(String s) throws TechnicalException {
        return target.findById(s);
    }

    @Override
    public ServiceView create(ServiceView serviceView) throws TechnicalException {
        return target.create(serviceView);
    }

    @Override
    public ServiceView update(ServiceView serviceView) throws TechnicalException {
        return target.update(serviceView);
    }

    @Override
    public void delete(String s) throws TechnicalException {
        target.delete(s);
    }
}

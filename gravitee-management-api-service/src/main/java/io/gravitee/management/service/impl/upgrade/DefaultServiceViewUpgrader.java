package io.gravitee.management.service.impl.upgrade;

import io.gravitee.management.model.ServiceViewEntity;
import io.gravitee.management.service.ServiceViewService;
import io.gravitee.management.service.Upgrader;
import io.gravitee.repository.management.model.ServiceView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author Jokki
 */
@Component
public class DefaultServiceViewUpgrader implements Upgrader, Ordered {

    /**
     * Logger.
     */
    private final Logger logger = LoggerFactory.getLogger(DefaultViewUpgrader.class);

    @Autowired
    private ServiceViewService viewService;

    @Override
    public boolean upgrade() {
        // Initialize default view
        Optional<ServiceViewEntity> optionalAllView = viewService.findAll().
                stream().
                filter(v -> v.getId().equals(ServiceView.OTHER_ID)).
                findFirst();
        if(!optionalAllView.isPresent()) {
            logger.info("Create default View");
            viewService.createDefaultServiceView();
        }

        return true;
    }

    @Override
    public int getOrder() {
        return 300;
    }
}

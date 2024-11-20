package com.redhat.widget.rest;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WidgetService {

    private static final Logger LOG = LoggerFactory.getLogger(WidgetService.class);

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private WidgetRepository widgetRepository;

    public Widget makeWidget(String name, String description) {

        Widget widget = new Widget();
        widget.setName(name);
        widget.setDescription(description);

        return saveOrUpdateWidget(widget);
    }

    @Transactional(readOnly = true)
    public Widget findById(Long id) throws WidgetNotFoundException {

        Optional<Widget> optional = widgetRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }

        throw new WidgetNotFoundException();
    }

    @Transactional(readOnly = true)
    public Widget findWidgetByName(String name) throws UnsupportedEncodingException {

        Objects.requireNonNull(name, "<name> cannot be null");
        LOG.debug("{}", name);

        return widgetRepository.findWidgetByName(name);
    }

    @Transactional(readOnly = true)
    public Collection<Widget> findWidgets() {

        return StreamSupport
                .stream(widgetRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public Widget saveOrUpdateWidget(final Widget widget) {

        Widget result = widgetRepository.save(widget);
        em.flush();

        return result;
    }

    public void deleteWidget(final Widget widget) throws WidgetNotFoundException {

        Objects.requireNonNull(widget, "<widget> cannot be null");

        widgetRepository.delete(widget);
        em.flush();
    }

    public void deleteAll() {

        widgetRepository.deleteAll();
        em.flush();
    }

}

package com.redhat.widget.rest;

import org.springframework.data.repository.CrudRepository;

public interface WidgetRepository extends CrudRepository<Widget, Long> {

    Widget findWidgetByName(String name);

}

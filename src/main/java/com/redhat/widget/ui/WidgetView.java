package com.redhat.widget.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.redhat.widget.rest.Widget;
import com.redhat.widget.rest.WidgetService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import org.primefaces.PrimeFaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ViewScoped
@Component
public class WidgetView {

    private static final Logger LOG = LoggerFactory.getLogger(WidgetView.class);

    @Autowired
    private transient WidgetService widgetService;

    private List<Widget> widgets;

    private Widget selectedWidget;

    private List<Widget> selectedWidgets;

    @PostConstruct
    public void init() {

        widgets = new ArrayList<>(widgetService.findWidgets());

        selectedWidgets = new ArrayList<>();
    }

    public Collection<Widget> getWidgets() {

        return widgets;
    }

    public Widget getSelectedWidget() {

        return selectedWidget;
    }

    public void setSelectedWidget(Widget selectedWidget) {

        this.selectedWidget = selectedWidget;
    }

    public List<Widget> getSelectedWidgets() {

        return selectedWidgets;
    }

    public void setSelectedWidgets(List<Widget> selectedWidgets) {

        this.selectedWidgets = selectedWidgets;
    }

    public void openNew() {

        this.selectedWidget = new Widget();
    }

    public void saveWidget() {

        if (this.selectedWidget.getId() == null) {
            widgetService.saveOrUpdateWidget(selectedWidget);
            widgets = new ArrayList<>(widgetService.findWidgets());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Widget Added"));
        } else {
            widgetService.saveOrUpdateWidget(selectedWidget);
            widgets = new ArrayList<>(widgetService.findWidgets());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Widget Updated"));
        }

        PrimeFaces.current().executeScript("PF('manageWidgetDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-widgets");
    }

    public void deleteWidget() {

        widgetService.deleteWidget(selectedWidget);
        this.selectedWidgets.clear();
        this.selectedWidget = null;
        widgets = new ArrayList<>(widgetService.findWidgets());

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Widget Removed"));
        PrimeFaces.current().ajax().update("form:messages", "form:dt-widgets");
    }

    public String getDeleteButtonMessage() {

        if (hasSelectedWidgets()) {
            int size = this.selectedWidgets.size();
            return size > 1 ? size + " widgets selected" : "1 widget selected";
        }

        return "Delete";
    }

    public boolean hasSelectedWidgets() {

        return this.selectedWidgets != null && !this.selectedWidgets.isEmpty();
    }

    public void deleteSelectedWidgets() {

        for (Widget widget : selectedWidgets) {
            widgetService.deleteWidget(widget);
        }
        widgets = new ArrayList<>(widgetService.findWidgets());
        this.selectedWidgets.clear();

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Widgets Removed"));
        PrimeFaces.current().ajax().update("form:messages", "form:dt-widgets");
        PrimeFaces.current().executeScript("PF('dtWidgets').clearFilters()");
    }

}

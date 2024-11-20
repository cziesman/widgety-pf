package com.redhat.widget.rest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class WidgetRestController {

    private static final Logger LOG = LoggerFactory.getLogger(WidgetRestController.class);

    @Autowired
    private WidgetService widgetService;

    @Operation(summary = "Get a widget by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns a widget",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Widget.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid id was provided",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Widget was not found",
                    content = @Content)
    })
    @GetMapping("/widget/{id}")
    public Widget findById(@Parameter(description = "id of widget to be searched") @PathVariable long id) {

        return widgetService.findById(id);
    }

    @Operation(summary = "Get a widget by its name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns a widget",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Widget.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid name was provided",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Widget was not found",
                    content = @Content)
    })
    @GetMapping("/widget/name/{name}")
    public Widget findByName(@Parameter(description = "name of widget to be searched") @PathVariable String name) throws UnsupportedEncodingException {

        String decoded = URLDecoder.decode(name, StandardCharsets.UTF_8);

        return widgetService.findWidgetByName(decoded);
    }

    @Operation(summary = "Gets all widgets")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns a list of widgets",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Widget.class))})
    })
    @GetMapping("/widgets")
    public Collection<Widget> findWidgets() {

        return widgetService.findWidgets();
    }

    @Operation(summary = "Save or update widget")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Widget was saved/updated",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Widget with invalid values was provided",
                    content = @Content)
    })
    @PutMapping(value = "/widget", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Widget saveOrUpdateWidget(@Valid @RequestBody(required = true) final Widget widget) throws UnsupportedEncodingException {

        String decodedName = URLDecoder.decode(widget.getName(), StandardCharsets.UTF_8);
        String decodedDescription = URLDecoder.decode(widget.getDescription(), StandardCharsets.UTF_8);
        widget.setName(decodedName);
        widget.setDescription(decodedDescription);

        LOG.debug("{}", widget);
        return widgetService.saveOrUpdateWidget(widget);
    }

    @Operation(summary = "Delete a widget by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Widget was deleted",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid id was provided",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Widget was not found",
                    content = @Content)
    })
    @GetMapping("/widget/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteWidget(@Parameter(description = "id of widget to be deleted") @PathVariable("id") final Long id) {

        widgetService.deleteWidget(widgetService.findById(id));
    }

    @Operation(summary = "Delete all widgets")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Widgets were deleted",
                    content = @Content)
    })
    @DeleteMapping("/widget/deleteAll")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAll() {

        widgetService.deleteAll();
    }

}

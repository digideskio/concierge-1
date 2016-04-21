package net.oneandone.concierge.manual;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonStructure;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class GetManual implements OperationManual {

    private List<QueryParameter> queryParameters;

    @Override
    public JsonStructure content() {
        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (final QueryParameter queryParameter : queryParameters) {
            final JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
            objectBuilder.add("id", queryParameter.id());
            objectBuilder.add("description", queryParameter.description());
            arrayBuilder.add(objectBuilder.build());
        }

        return Json.createObjectBuilder().add("query-parameters", arrayBuilder.build()).build();
    }
}
package com.zurrtum.create.client.flywheel.lib.config;

import com.google.gson.JsonObject;

public class StringValue extends ConfigValue<String> {
    StringValue(Builder builder, JsonObject parent, String name, String def) {
        super(builder, parent, name, def);
    }
}

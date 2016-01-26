package org.eventreducer.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;

public class ObjectMapper extends com.fasterxml.jackson.databind.ObjectMapper {

    public ObjectMapper() {
        super();
        registerModule(new EventReducerModule());
        // mapTyper code taken from Redisson
        TypeResolverBuilder<?> mapTyper = new com.fasterxml.jackson.databind.ObjectMapper.DefaultTypeResolverBuilder(com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL) {
            public boolean useForType(JavaType t)
            {
                switch (_appliesFor) {
                    case NON_CONCRETE_AND_ARRAYS:
                        while (t.isArrayType()) {
                            t = t.getContentType();
                        }
                        // fall through
                    case OBJECT_AND_NON_CONCRETE:
                        return (t.getRawClass() == Object.class) || !t.isConcrete();
                    case NON_FINAL:
                        while (t.isArrayType()) {
                            t = t.getContentType();
                        }
                        // to fix problem with wrong long to int conversion
                        if (t.getRawClass() == Long.class) {
                            return true;
                        }
                        return !t.isFinal(); // includes Object.class
                    default:
                        //case JAVA_LANG_OBJECT:
                        return (t.getRawClass() == Object.class);
                }
            }
        };
        mapTyper.init(JsonTypeInfo.Id.CLASS, null);
        mapTyper.inclusion(JsonTypeInfo.As.PROPERTY);
        setDefaultTyping(mapTyper);

        setSerializationInclusion(JsonInclude.Include.NON_NULL);
        setVisibility(getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
    }
}

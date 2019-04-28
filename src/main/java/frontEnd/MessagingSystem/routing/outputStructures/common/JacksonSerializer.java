package frontEnd.MessagingSystem.routing.outputStructures.common;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import frontEnd.Interface.outputRouting.ExceptionHandler;
import frontEnd.Interface.outputRouting.ExceptionId;

/**
 * <p>JacksonSerializer class.</p>
 *
 * @author RigorityJTeam
 * Created on 4/6/19.
 * @version $Id: $Id
 * @since {VersionHere}
 *
 * <p>{Description Here}</p>
 */
public class JacksonSerializer {

    /**
     * <p>serialize.</p>
     *
     * @param obj         a {@link java.lang.Object} object.
     * @param prettyPrint a {@link java.lang.Boolean} object.
     * @param outputType  a {@link frontEnd.MessagingSystem.routing.outputStructures.common.JacksonSerializer.JacksonType} object.
     * @return a {@link java.lang.String} object.
     * @throws frontEnd.Interface.outputRouting.ExceptionHandler if any.
     */
    public static String serialize(Object obj, Boolean prettyPrint, JacksonType outputType) throws ExceptionHandler {
        ObjectMapper serializer = outputType.getOutputMapper();
        serializer.setSerializationInclusion(Include.NON_EMPTY);
        if (prettyPrint)
            serializer.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            return serializer.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new ExceptionHandler("Error marshalling output", ExceptionId.MAR_VAR);
        }
    }

    /**
     * @author RigorityJTeam
     * Created on 4/6/19.
     * @since {VersionHere}
     *
     * <p>{Description Here}</p>
     */
    public enum JacksonType {
        //region Values
        XML(".xml", new XmlMapper()),
        JSON(".json", new ObjectMapper()),
        YAML(".yaml", new ObjectMapper(new YAMLFactory())),
        ;
        //endregion

        //region Attributes
        private String extension;
        private ObjectMapper outputMapper;
        //endregion

        //region constructor
        JacksonType(String ext, ObjectMapper mapper) {
            this.extension = ext;
            this.outputMapper = mapper;
        }

        //endregion

        //region Getter
        public String getExtension() {
            return extension;
        }

        public ObjectMapper getOutputMapper() {
            return outputMapper;
        }
        //endregion
    }

}

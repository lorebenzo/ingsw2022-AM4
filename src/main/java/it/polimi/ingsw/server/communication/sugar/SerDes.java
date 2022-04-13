package it.polimi.ingsw.server.communication.sugar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import it.polimi.ingsw.server.communication.sugar.exceptions.MessageSerializationException;
import it.polimi.ingsw.server.communication.sugar.exceptions.MessageDeserializationException;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Serializer/Deserializer for sugar messages
 */
public class SerDes {
    private static final Gson gson;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapterFactory(new GenericTypeAdapterFactory());
        gsonBuilder.registerTypeAdapter(Class.class, new GenericTypeAdapter());
        gson = gsonBuilder.create();
    }

    public static String serialize(Message message) throws MessageSerializationException {
        try {
            return gson.toJson(message)
                    .replaceAll("\\s", "")  // Remove all newlines, spaces, tabs
                    + "\n";  // Adds a newline to the end
        } catch (Exception e) {
            throw new MessageSerializationException(e.getMessage());
        }
    }

    public static Message deserialize(String message) throws MessageDeserializationException {
        try {
            var msg = gson.fromJson(message, Message.class);
            return gson.fromJson(message, (Type) msg.messageClass);
        } catch (Exception e) {
            throw new MessageDeserializationException(e.getMessage());
        }
    }
}

class GenericTypeAdapter extends TypeAdapter<Class<?>> {
    @Override
    public Class<?> read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }

        try {
            return Class.forName(jsonReader.nextString());
        } catch (ClassNotFoundException exception) {
            throw new IOException(exception);
        }
    }

    @Override
    public void write(JsonWriter jsonWriter, Class<?> genericClass) throws IOException {
        if(genericClass == null){
            jsonWriter.nullValue();
            return;
        }
        jsonWriter.value(genericClass.getName());
    }
}

class GenericTypeAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gsonInstance, TypeToken<T> typeToken) {
        if(!Class.class.isAssignableFrom(typeToken.getRawType())) {
            return null;
        }
        return (TypeAdapter<T>) new GenericTypeAdapter();
    }
}
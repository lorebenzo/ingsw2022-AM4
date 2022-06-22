package it.polimi.ingsw.communication.sugar_framework;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import it.polimi.ingsw.communication.sugar_framework.exceptions.MessageDeserializationException;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;
import it.polimi.ingsw.server.model.game_logic.number_of_player_strategy.FourPlayerStrategy;
import it.polimi.ingsw.server.model.game_logic.number_of_player_strategy.NumberOfPlayersStrategy;
import it.polimi.ingsw.server.model.game_logic.number_of_player_strategy.ThreePlayerStrategy;
import it.polimi.ingsw.server.model.game_logic.number_of_player_strategy.TwoPlayerStrategy;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Serializer/Deserializer for sugar messages
 */
public class SerDes {
    private static final Gson gson;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        final RuntimeTypeAdapterFactory<NumberOfPlayersStrategy> typeFactory = RuntimeTypeAdapterFactory
                .of(NumberOfPlayersStrategy.class, "type")
                .registerSubtype(TwoPlayerStrategy.class)
                .registerSubtype(ThreePlayerStrategy.class)
                .registerSubtype(FourPlayerStrategy.class);

        gsonBuilder.registerTypeAdapterFactory(new GenericTypeAdapterFactory());
        gsonBuilder.registerTypeAdapter(Class.class, new GenericTypeAdapter());
        gsonBuilder.registerTypeAdapterFactory(typeFactory);
        gson = gsonBuilder.create();
    }

    public static String serialize(SugarMessage message) {
        return gson.toJson(message);
                    //.replaceAll("\\s", "")  // Remove all newlines, spaces, tabs
                    //+ "\n";  // Adds a newline to the end
    }

    public static SugarMessage deserialize(String message) throws MessageDeserializationException {
        try {
            var msg = gson.fromJson(message, SugarMessage.class);
            return gson.fromJson(message, (Type) msg.messageClass);
        } catch (Exception e) {
            throw new MessageDeserializationException(e.getMessage());
        }
    }

    public static Object deserialize(String s, Type t) {
        return gson.fromJson(s, t);
    }

    public static String serialize(Object o) {
        return gson.toJson(o);
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
        if(genericClass == null) {
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
package it.polimi.ingsw.communication.sugar_framework.message_processing;

import it.polimi.ingsw.communication.sugar_framework.Peer;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class SugarMessageProcessor {
    /**
     * vfvExecutes all @SugarMessageHandler(s) annotated methods in this class, if none is found, executes the "base()" method (if it exists)
     * @param message any Message object
     */
    public final void process(SugarMessage message, Peer sender) {
        // Get the methods of this class marked with the @Process annotation
        List<Method> methods = Arrays.stream(this.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(SugarMessageHandler.class))  // Get annotated methods
                .filter(method -> method.getParameterCount() == 2)  // Get methods that take exactly two parameters
                .filter(method -> method.getParameterTypes()[0].equals(SugarMessage.class))  // Get methods that take a Message as first parameter
                .filter(method -> method.getParameterTypes()[1].equals(Peer.class)) // Get methods that take a Peer as second parameter
                .collect(Collectors.toList());


        // Invoke the methods whose annotation parameter matches message type
        boolean invoked = false;
        for(var method : methods) {
            if(
                    method.getName().equalsIgnoreCase(
                            message.getClass().getSimpleName().toLowerCase()
                    )
            ) {
                try {
                    method.invoke(this, message, sender);
                    invoked = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if(!invoked) {
            // Try to invoke base method
            var baseMethod = methods.stream()
                    .filter(method -> method.getName().equalsIgnoreCase("base"))
                    .findFirst();

            if (baseMethod.isPresent()) {
                try {
                    baseMethod.get().invoke(this, message, sender);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

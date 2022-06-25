package it.polimi.ingsw.communication.sugar_framework.message_processing;

import it.polimi.ingsw.communication.sugar_framework.Peer;
import it.polimi.ingsw.communication.sugar_framework.messages.SugarMessage;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class SugarMessageProcessor {
    /**
     * Executes all @SugarMessageHandler(s) annotated methods in this class that match this method's signature, if none is found, executes the "base()" method (if it exists)
     * @param message any Message object
     * @param sender the Peer that sent the message
     */
    public synchronized final SugarMessage process(SugarMessage message, Peer sender) {
        // Get the methods of this class marked with the @Process annotation
        List<Method> methods = Arrays.stream(this.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(SugarMessageHandler.class))  // Get annotated methods
                .filter(method -> method.getParameterCount() == 2)  // Get methods that take exactly two parameters
                //.filter(method -> method.getParameterTypes()[0].equals(SugarMessage.class))  // Get methods that take a Message as first parameter
                .filter(method -> method.getParameterTypes()[1].equals(Peer.class)) // Get methods that take a Peer as second parameter
                .collect(Collectors.toList());

        // Invoke the method whose annotation parameter matches message type
        for(var method : methods) {
            if(
                    method.getName().equalsIgnoreCase(
                            message.getClass().getSimpleName()
                    )
            ) {
                try {
                    method.setAccessible(true);
                    return (SugarMessage) method.invoke(this, message, sender);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Try to invoke base method
        var baseMethod = methods.stream()
                .filter(method -> method.getName().equalsIgnoreCase("base"))
                .findFirst();

        if (baseMethod.isPresent()) {
            try {
                baseMethod.get().setAccessible(true);
                return (SugarMessage) baseMethod.get().invoke(this, message, sender);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public synchronized final SugarMessage processFromLowerLayers(SugarMessage message, Peer receiver) {
        // Get the methods of this class marked with the @Process annotation
        List<Method> methods = Arrays.stream(this.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(SugarMessageFromLowerLayersHandler.class))  // Get annotated methods
                .filter(method -> method.getParameterCount() == 2)  // Get methods that take exactly two parameters
                //.filter(method -> method.getParameterTypes()[0].equals(SugarMessage.class))  // Get methods that take a Message as first parameter
                .filter(method -> method.getParameterTypes()[1].equals(Peer.class)) // Get methods that take a Peer as second parameter
                .collect(Collectors.toList());

        // Invoke the method whose annotation parameter matches message type
        for(var method : methods) {
            if(
                    method.getName().equalsIgnoreCase(
                            message.getClass().getSimpleName()
                    )
            ) {
                try {
                    method.setAccessible(true);
                    return (SugarMessage) method.invoke(this, message, receiver);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Try to invoke base method
        var baseMethod = methods.stream()
                .filter(method -> method.getName().equalsIgnoreCase("baseLowerLayers"))
                .findFirst();

        if (baseMethod.isPresent()) {
            try {
                baseMethod.get().setAccessible(true);
                return (SugarMessage) baseMethod.get().invoke(this, message, receiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Executes all @SugarMessageHandler(s) annotated methods in this class that match this method's signature, if none is found, executes the "base()" method (if it exists)
     * @param message any Message object
     */
    public synchronized final void process(SugarMessage message) {
        // Get the methods of this class marked with the @Process annotation
        List<Method> methods = Arrays.stream(this.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(SugarMessageHandler.class))  // Get annotated methods
                .filter(method -> method.getParameterCount() == 1)  // Get methods that take exactly one parameter
                //.filter(method -> method.getParameterTypes()[0].equals(SugarMessage.class))  // Get methods that take a SugarMessage as parameter
                .collect(Collectors.toList());



        // Invoke the methods whose annotation parameter matches message type
        boolean invoked = false;
        for(var method : methods) {
            if(
                    method.getName().equalsIgnoreCase(
                            message.getClass().getSimpleName()
                    )
            ) {
                try {
                    method.setAccessible(true);
                    method.invoke(this, message);
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
                    baseMethod.get().setAccessible(true);
                    baseMethod.get().invoke(this, message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void drop(SugarMessage msg, String reason) {
        System.out.println("Dropping message: " + msg.serialize() + "\nReason: " + reason);
    }
}

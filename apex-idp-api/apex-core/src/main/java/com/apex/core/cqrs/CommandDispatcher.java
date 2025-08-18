package com.apex.core.cqrs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Command dispatcher for CQRS architecture
 * Routes commands to appropriate handlers
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommandDispatcher {

    private final ApplicationContext applicationContext;
    private final Map<Class<? extends Command>, CommandHandler<? extends Command>> handlers = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends Command> void dispatch(T command) {
        CommandHandler<T> handler = (CommandHandler<T>) getHandler(command.getClass());
        if (handler != null) {
            log.debug("Dispatching command: {} to handler: {}", command.getClass().getSimpleName(), handler.getClass().getSimpleName());
            handler.handle(command);
        } else {
            throw new IllegalArgumentException("No handler found for command: " + command.getClass().getSimpleName());
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Command> CommandHandler<T> getHandler(Class<T> commandClass) {
        return (CommandHandler<T>) handlers.computeIfAbsent(commandClass, this::findHandler);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T extends Command> CommandHandler<T> findHandler(Class<T> commandClass) {
        Map<String, CommandHandler> allHandlers = applicationContext.getBeansOfType(CommandHandler.class);
        
        for (CommandHandler handler : allHandlers.values()) {
            Type[] genericInterfaces = handler.getClass().getGenericInterfaces();
            for (Type genericInterface : genericInterfaces) {
                if (genericInterface instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
                    if (parameterizedType.getRawType().equals(CommandHandler.class)) {
                        Type[] typeArguments = parameterizedType.getActualTypeArguments();
                        if (typeArguments.length > 0 && typeArguments[0].equals(commandClass)) {
                            return handler;
                        }
                    }
                }
            }
        }
        return null;
    }
}

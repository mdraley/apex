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
 * Query dispatcher for CQRS architecture
 * Routes queries to appropriate handlers
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QueryDispatcher {

    private final ApplicationContext applicationContext;
    private final Map<Class<? extends Query>, QueryHandler<? extends Query, ?>> handlers = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends Query, R> R dispatch(T query) {
        QueryHandler<T, R> handler = (QueryHandler<T, R>) getHandler(query.getClass());
        if (handler != null) {
            log.debug("Dispatching query: {} to handler: {}", query.getClass().getSimpleName(), handler.getClass().getSimpleName());
            return handler.handle(query);
        } else {
            throw new IllegalArgumentException("No handler found for query: " + query.getClass().getSimpleName());
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Query> QueryHandler<T, ?> getHandler(Class<T> queryClass) {
        return (QueryHandler<T, ?>) handlers.computeIfAbsent(queryClass, this::findHandler);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T extends Query> QueryHandler<T, ?> findHandler(Class<T> queryClass) {
        Map<String, QueryHandler> allHandlers = applicationContext.getBeansOfType(QueryHandler.class);
        
        for (QueryHandler handler : allHandlers.values()) {
            Type[] genericInterfaces = handler.getClass().getGenericInterfaces();
            for (Type genericInterface : genericInterfaces) {
                if (genericInterface instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
                    if (parameterizedType.getRawType().equals(QueryHandler.class)) {
                        Type[] typeArguments = parameterizedType.getActualTypeArguments();
                        if (typeArguments.length > 0 && typeArguments[0].equals(queryClass)) {
                            return handler;
                        }
                    }
                }
            }
        }
        return null;
    }
}

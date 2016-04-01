package net.oneandone.concierge.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.oneandone.concierge.api.resolver.ExtensionResolver;
import net.oneandone.concierge.api.resolver.GroupResolver;
import net.oneandone.concierge.api.resolver.Resolver;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class Resolvers {

    @JsonProperty("group")
    private List<String> groupResolverClassNames;

    @JsonProperty("extension")
    private List<String> extensionResolverClassNames;

    private List<GroupResolver> groupResolvers;
    private List<ExtensionResolver> extensionResolvers;

    public List<GroupResolver> getGroupResolvers() {
        if (groupResolvers == null) {
            groupResolvers = getResolvers(GroupResolver.class, groupResolverClassNames);
        }
        return groupResolvers;
    }

    public List<ExtensionResolver> getExtensionResolvers() {
        if (extensionResolvers == null) {
            extensionResolvers = getResolvers(ExtensionResolver.class, extensionResolverClassNames);
        }
        return extensionResolvers;
    }

    private static <T extends Resolver> List<T> getResolvers(final Class<T> resolverInterface, final List<String> classNames) {
        log.debug("about to initialize resolvers for interface '{}': {}", resolverInterface, classNames);
        final List<T> resolvers = new ArrayList<>(classNames.size());

        try {
            for (final String resolverClassName : classNames) {
                try {
                    final Class resolvedClass = Class.forName(resolverClassName);
                    if (resolverInterface.isAssignableFrom(resolvedClass)) {
                        @SuppressWarnings("unchecked") final Class<T> resolverClass = (Class<T>) resolvedClass;
                        final T resolver = resolverClass.newInstance();
                        resolvers.add(resolver);
                    } else {
                        throw new IllegalArgumentException("resolved class '" + resolverClassName + "' is not a " + resolverInterface.getSimpleName());
                    }
                } catch (final ClassNotFoundException e) {
                    throw new IllegalStateException("could not find the resolver class", e);
                } catch (final InstantiationException | IllegalAccessException e) {
                    throw new IllegalStateException("could not instantiate the resolver", e);
                } catch (final IllegalArgumentException e) {
                    throw new IllegalStateException("specified resolver class does not implement the required interface " + resolverInterface.getSimpleName(), e);
                }
            }
        } catch (final IllegalStateException e) {
            log.error("could not initialize resolvers for class '" + resolverInterface + "'", e);
            throw e;
        }

        log.trace("completed initialization of all resolvers");
        return Collections.unmodifiableList(resolvers);
    }


}

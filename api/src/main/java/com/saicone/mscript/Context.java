package com.saicone.mscript;

import com.saicone.mscript.context.DelegateComposedContext;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * A central operation object which script is executed in.<br>
 * This object is a state holder of the script execution and can be used to get information about the current execution,
 * change the thread of execution, delay the execution, and more depending on the implementation of the context.
 *
 * @author Rubenicos
 */
public interface Context {

    /**
     * Standardized identifier for the server context, which is used to identify the server or the console itself.
     */
    UUID SERVER_ID = new UUID(0, 0);

    /**
     * Is the source of the script execution, usually the sender of the command or the entity that triggered the script.
     *
     * @return the source of the script execution
     */
    @NotNull
    Object source();

    /**
     * Is the object that the source is acting on behalf of.<br>
     * In other words, this is an impersonation of the source, and can be null if the source is not acting on behalf of anyone.
     *
     * @return the agent of the script execution
     */
    @Nullable
    Object agent();

    /**
     * Is the audience that receive any message from the script.
     *
     * @return an audience
     */
    @NotNull
    default Audience audience() {
        return (Audience) source();
    }

    /**
     * Is the audience that the script is currently pointing to.<br>
     * For example, the pointer used for message parsing and can be different from source if the context contains an agent.
     *
     * @return an audience
     */
    @NotNull
    default Audience pointer() {
        return (Audience) get();
    }

    /**
     * Get the object that the script is currently pointing to.<br>
     * If the context contains an agent, it will return the agent, otherwise it will return the source.
     *
     * @return the object that the script is currently pointing to
     * @param <T> the type of the object
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <T> T get() {
        final Object agent = agent();
        if (agent != null) {
            return (T) agent;
        }
        return (T) source();
    }

    /**
     * Get the object that the script is currently pointing to and cast it to the specified type.<br>
     * If the object is not of the specified type, a ClassCastException will be thrown.
     *
     * @param type the class to cast the object to
     * @return     the object cast to the specified type
     * @param <T>  the type of the object
     */
    @NotNull
    default <T> T get(@NotNull Class<T> type) throws ClassCastException {
        final Object object = get();
        return type.cast(object);
    }

    /**
     * Get the object that the script is currently pointing to and cast it to the specified type if possible.<br>
     * If the object is not of the specified type, an empty Optional will be returned.
     *
     * @param type the class to cast the object to
     * @return     an Optional containing the object cast to the specified type, or an empty Optional if the object is not of the specified type
     * @param <T>  the type of the object
     */
    @NotNull
    default <T> Optional<T> getIf(@NotNull Class<T> type) {
        final Object object = get();
        if (type.isInstance(object)) {
            return Optional.of(type.cast(object));
        }
        return Optional.empty();
    }

    /**
     * Get the unique identifier of the object that the script context is currently pointing to.
     *
     * @return a unique identifier
     */
    @NotNull
    default UUID getUniqueId() {
        throw new UnsupportedOperationException("Unique ID is not supported in this context");
    }

    /**
     * Parse the given string if it contains any placeholders or variables that
     * need to be replaced with their corresponding values from the script context.
     *
     * @param str the string to parse
     * @return    the parsed string
     */
    @NotNull
    default String parse(@NotNull String str) {
        return str;
    }

    /**
     * Parse the given component if it contains any placeholders or variables that
     * need to be replaced with their corresponding values from the script context.
     *
     * @param component the component to parse
     * @return          the parsed component
     */
    @Nullable
    @Contract("!null -> !null")
    default Component parse(@Nullable Component component) {
        if (component == null) {
            return null;
        }

        if (component instanceof TextComponent) {
            final String content = ((TextComponent) component).content();
            final String parsed = parse(content);
            if (!content.equals(parsed)) {
                component = ((TextComponent) component).content(parsed);
            }
        }

        if (component.children().isEmpty()) {
            return component;
        }

        return component.children(component.children().stream().map(this::parse).toList());
    }

    /**
     * Move to a synchronous thread if the current execution is outside of it.
     *
     * @param command the command to execute
     */
    default void sync(@NotNull Runnable command) {
        async(command);
    }

    /**
     * Move to an asynchronous thread if the current execution is on a synchronous thread.
     *
     * @param command the command to execute
     */
    default void async(@NotNull Runnable command) {
        command.run();
    }

    /**
     * Delay the execution of the given command for the specified time and unit.<br>
     * This method is only available in contexts that support delayed execution.
     *
     * @param time    the time to delay
     * @param unit    the unit of time to delay
     * @param command the command to execute after the delay
     */
    default void delay(long time, @NotNull TimeUnit unit, @NotNull Runnable command) {
        delayAsync(time, unit, command);
    }

    /**
     * Delay the execution of the given command for the specified time and unit asynchronously.<br>
     * This method is only available in contexts that support delayed execution.
     *
     * @param time    the time to delay
     * @param unit    the unit of time to delay
     * @param command the command to execute after the delay
     */
    default void delayAsync(long time, @NotNull TimeUnit unit, @NotNull Runnable command) {
        throw new UnsupportedOperationException("Delayed execution is not supported in this context");
    }

    /**
     * Returns the current context as the specified type, if possible.<br>
     * If the current context is not of the specified type, a {@link ClassCastException} will be thrown.
     *
     * @param type the type to cast the context to
     * @return     the current context as the specified type
     * @param <A> the type of the context
     */
    @NotNull
    @SuppressWarnings("unchecked")
    default <A extends Context> A as(@NotNull Class<A> type) {
        return (A) this;
    }

    /**
     * Convert the current context to a {@link ComposedContext}.
     *
     * @return a newly generated composed context that wraps the current context
     */
    @NotNull
    default ComposedContext composed() {
        return new DelegateComposedContext(this);
    }
}

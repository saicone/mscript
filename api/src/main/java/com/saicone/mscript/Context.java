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

public interface Context {

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

    @NotNull
    default UUID getUniqueId() {
        throw new UnsupportedOperationException("Unique ID is not supported in this context");
    }

    @NotNull
    default String parse(@NotNull String str) {
        return str;
    }

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

    default void sync(@NotNull Runnable command) {
        command.run();
    }

    default void async(@NotNull Runnable command) {
        command.run();
    }

    default void delay(long time, @NotNull TimeUnit unit, @NotNull Runnable command) {
        throw new UnsupportedOperationException("Delayed execution is not supported in this context");
    }

    @NotNull
    @SuppressWarnings("unchecked")
    default <A extends Context> A as(@NotNull Class<A> type) {
        return (A) this;
    }

    @NotNull
    default ComposedContext composed() {
        return new DelegateComposedContext(this);
    }
}

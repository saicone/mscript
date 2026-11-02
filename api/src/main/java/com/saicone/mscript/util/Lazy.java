/*
 * MIT License.
 *
 * Copyright (c) 2026 Rubenicos
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.saicone.mscript.util;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.function.Supplier;

/**
 * A lazy initialized object that is only initialized when {@link #get()} is called for the first time.<br>
 * This class is thread-safe and can be used in concurrent environments.
 *
 * @author Rubenicos
 *
 * @param <T> the type of object that is being lazily initialized
 */
@ApiStatus.Internal
public class Lazy<T> implements Supplier<T> {

    /**
     * Create a lazy initialized object with the given initializer.<br>
     * The initialized object is immutable and cannot be set to a new value.<br>
     * For mutable lazy initialized objects, use {@link #mutable(Supplier)} instead.
     *
     * @param initializer the initializer that will be used to initialize the object
     * @return            a newly generated lazy initialized object
     * @param <T>         the type of object that is being lazily initialized
     */
    @NotNull
    public static <T> Lazy<T> init(@NotNull Supplier<T> initializer) {
        return new Lazy<T>(initializer) {
            @Override
            public void set(T value) throws UnsupportedOperationException {
                throw new UnsupportedOperationException("Cannot set value of an immutable reference");
            }
        };
    }

    /**
     * Create a lazy initialized object with the given initializer.<br>
     * The initialized object is mutable and can be set to a new value before or after it is initialized.
     *
     * @param initializer the initializer that will be used to initialize the object
     * @return            a newly generated lazy initialized object
     * @param <T>         the type of object that is being lazily initialized
     */
    @NotNull
    public static <T> Lazy<T> mutable(@NotNull Supplier<T> initializer) {
        return new Lazy<>(initializer);
    }

    private final Supplier<T> initializer;
    private transient volatile boolean initialized;
    private transient T value;

    /**
     * Constructs a lazy initialized object with the given initializer.
     *
     * @param initializer the initializer that will be used to initialize the object
     */
    protected Lazy(@NotNull Supplier<T> initializer) {
        this.initializer = initializer;
    }

    /**
     * Returns the lazily initialized object, initializing it if it has not been initialized yet.
     *
     * @return the lazily initialized object
     */
    @Override
    @UnknownNullability
    public T get() {
        if (!initialized) {
            synchronized (this) {
                if (!initialized) {
                    value = initializer.get();
                    initialized = true;
                }
            }
        }
        return value;
    }

    /**
     * Sets the value of the lazily initialized object, initializing it if it has not been initialized yet.<br>
     * This operation is only allowed if the lazy object is mutable, otherwise it will throw an {@link UnsupportedOperationException}.
     *
     * @param value the value to set
     * @throws UnsupportedOperationException if the lazy object is immutable
     */
    public void set(@UnknownNullability T value) throws UnsupportedOperationException {
        synchronized (this) {
            this.value = value;
            this.initialized = true;
        }
    }
}

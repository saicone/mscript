package com.saicone.mscript.impl.execution;

import com.saicone.mscript.Context;
import com.saicone.mscript.Execution;
import com.saicone.mscript.Result;
import com.saicone.mscript.io.SectionReader;
import com.saicone.mscript.util.Mini;
import com.saicone.mscript.util.Strings;
import com.saicone.types.Types;
import net.kyori.adventure.title.Title;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Title execution implementation.
 *
 * @author Rubenicos
 */
public class TitleExecution implements Execution {

    /**
     * Title execution reader.
     */
    public static final SectionReader<TitleExecution> READER = new SectionReader<>("(show-?)?title") {
        @Override
        protected TitleExecution read(@NotNull String id, @NotNull String context) {
            final String[] parts = Strings.splitQuoted(context, ' ');

            final String title = parts.length > 0 ? parts[0] : null;
            final String subtitle = parts.length > 1 ? parts[1] : null;
            final int fadeIn = parts.length > 2 ? Types.INTEGER.parseOrElse(parts[2], 10) : 10;
            final int stay = parts.length > 3 ? Types.INTEGER.parseOrElse(parts[3], 70) : 70;
            final int fadeOut = parts.length > 4 ? Types.INTEGER.parseOrElse(parts[4], 20) : 20;
            return new TitleExecution(title, subtitle, fadeIn, stay, fadeOut);
        }

        @Override
        protected TitleExecution read(@NotNull String id, @NotNull Map<?, ?> context) {
            final String title = Types.STRING.parseOrNull(context.get("title"));
            final String subtitle = Types.STRING.parseOrNull(context.get("subtitle"));
            final int fadeIn = Types.INTEGER.parseOrElse(context.get("fade-in"), 10);
            final int stay = Types.INTEGER.parseOrElse(context.get("stay"), 70);
            final int fadeOut = Types.INTEGER.parseOrElse(context.get("fade-out"), 20);
            return new TitleExecution(title, subtitle, fadeIn, stay, fadeOut);
        }
    };

    private final String title;
    private final String subtitle;
    private final int fadeIn;
    private final int stay;
    private final int fadeOut;

    /**
     * Constructs a new title execution.
     *
     * @param title    the title text to display
     * @param subtitle the subtitle text to display
     * @param fadeIn   the duration in ticks for the title to fade in
     * @param stay     the duration in ticks for the title to stay on screen
     * @param fadeOut  the duration in ticks for the title to fade out
     */
    public TitleExecution(@Nullable String title, @Nullable String subtitle, int fadeIn, int stay, int fadeOut) {
        this.title = title;
        this.subtitle = subtitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    @Override
    public @NotNull Result run(@NotNull Context context) {
        final String title = this.title == null ? null : context.parse(this.title);
        final String subtitle = this.subtitle == null ? null : context.parse(this.subtitle);
        context.audience().showTitle(Title.title(
                Mini.get().parseOrEmpty(context.pointer(), title),
                Mini.get().parseOrEmpty(context.pointer(), subtitle),
                fadeIn,
                stay,
                fadeOut
        ));
        return Result.DONE;
    }
}

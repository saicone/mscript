package com.saicone.mscript.platform.bungeecord;

import com.saicone.mscript.Execution;
import com.saicone.mscript.io.SectionReader;
import com.saicone.mscript.platform.bungeecord.execution.BroadcastExecution;
import com.saicone.mscript.platform.bungeecord.execution.ConsoleCommandExecution;
import com.saicone.mscript.platform.bungeecord.execution.PlayerChatExecution;
import com.saicone.mscript.platform.bungeecord.execution.PlayerCommandExecution;
import com.saicone.mscript.platform.bungeecord.execution.ServerExecution;

/**
 * A collection of built-in Bungeecord executions.
 *
 * @author Rubenicos
 */
@SuppressWarnings("unused")
public final class BungeeExecutions {

    public static final SectionReader<? extends Execution> BROADCAST = BroadcastExecution.READER;
    public static final SectionReader<? extends Execution> CONSOLE_COMMAND = ConsoleCommandExecution.READER;
    public static final SectionReader<? extends Execution> PLAYER_CHAT = PlayerChatExecution.READER;
    public static final SectionReader<? extends Execution> PLAYER_COMMAND = PlayerCommandExecution.READER;
    public static final SectionReader<? extends Execution> SERVER = ServerExecution.READER;

    BungeeExecutions() {
    }
}

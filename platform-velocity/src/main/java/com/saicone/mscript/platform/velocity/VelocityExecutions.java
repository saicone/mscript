package com.saicone.mscript.platform.velocity;

import com.saicone.mscript.Execution;
import com.saicone.mscript.io.SectionReader;
import com.saicone.mscript.platform.velocity.execution.BroadcastExecution;
import com.saicone.mscript.platform.velocity.execution.ConsoleCommandExecution;
import com.saicone.mscript.platform.velocity.execution.PlayerChatExecution;
import com.saicone.mscript.platform.velocity.execution.PlayerCommandExecution;
import com.saicone.mscript.platform.velocity.execution.ServerExecution;

/**
 * A collection of built-in Velocity executions.
 *
 * @author Rubenicos
 */
@SuppressWarnings("unused")
public final class VelocityExecutions {

    public static final SectionReader<? extends Execution> BROADCAST = BroadcastExecution.READER;
    public static final SectionReader<? extends Execution> CONSOLE_COMMAND = ConsoleCommandExecution.READER;
    public static final SectionReader<? extends Execution> PLAYER_CHAT = PlayerChatExecution.READER;
    public static final SectionReader<? extends Execution> PLAYER_COMMAND = PlayerCommandExecution.READER;
    public static final SectionReader<? extends Execution> SERVER = ServerExecution.READER;

    VelocityExecutions() {
    }
}

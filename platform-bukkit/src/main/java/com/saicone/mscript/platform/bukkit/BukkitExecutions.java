package com.saicone.mscript.platform.bukkit;

import com.saicone.mscript.Execution;
import com.saicone.mscript.io.SectionReader;
import com.saicone.mscript.platform.bukkit.execution.BroadcastExecution;
import com.saicone.mscript.platform.bukkit.execution.PlayerChatExecution;
import com.saicone.mscript.platform.bukkit.execution.ConsoleCommandExecution;
import com.saicone.mscript.platform.bukkit.execution.PlayerCommandExecution;

public final class BukkitExecutions {

    public static final SectionReader<? extends Execution> BROADCAST = BroadcastExecution.READER;
    public static final SectionReader<? extends Execution> CHAT = PlayerChatExecution.READER;
    public static final SectionReader<? extends Execution> CONSOLE = ConsoleCommandExecution.READER;
    public static final SectionReader<? extends Execution> PLAYER = PlayerCommandExecution.READER;

    BukkitExecutions() {
    }
}

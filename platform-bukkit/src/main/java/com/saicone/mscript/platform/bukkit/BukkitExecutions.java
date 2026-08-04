package com.saicone.mscript.platform.bukkit;

import com.saicone.mscript.Execution;
import com.saicone.mscript.io.SectionReader;
import com.saicone.mscript.platform.bukkit.execution.BroadcastExecution;
import com.saicone.mscript.platform.bukkit.execution.PlayerChatExecution;
import com.saicone.mscript.platform.bukkit.execution.ConsoleCommandExecution;
import com.saicone.mscript.platform.bukkit.execution.PlayerCommandExecution;
import com.saicone.mscript.platform.bukkit.execution.ServerExecution;
import com.saicone.mscript.platform.bukkit.execution.hook.VaultMoneyExecution;

import java.util.function.Supplier;

public final class BukkitExecutions {

    public static final SectionReader<? extends Execution> BROADCAST = BroadcastExecution.READER;
    public static final SectionReader<? extends Execution> CONSOLE_COMMAND = ConsoleCommandExecution.READER;
    public static final SectionReader<? extends Execution> PLAYER_CHAT = PlayerChatExecution.READER;
    public static final SectionReader<? extends Execution> PLAYER_COMMAND = PlayerCommandExecution.READER;
    public static final SectionReader<? extends Execution> SERVER = ServerExecution.READER;

    public static final Supplier<SectionReader<? extends Execution>> VAULT_MONEY = VaultMoneyExecution::reader;

    BukkitExecutions() {
    }
}

package com.saicone.mscript.platform.bukkit;

import com.saicone.mscript.Execution;
import com.saicone.mscript.Result;
import com.saicone.mscript.io.SectionReader;
import com.saicone.mscript.platform.bukkit.execution.BroadcastExecution;
import com.saicone.mscript.platform.bukkit.execution.ChatExecution;
import com.saicone.mscript.platform.bukkit.execution.ConsoleExecution;
import com.saicone.mscript.platform.bukkit.execution.PlayerExecution;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public final class BukkitExecutions {

    public static final SectionReader<? extends Execution> BROADCAST = BroadcastExecution.READER;
    public static final SectionReader<? extends Execution> CHAT = ChatExecution.READER;
    public static final SectionReader<? extends Execution> CONSOLE = ConsoleExecution.READER;
    public static final SectionReader<? extends Execution> PLAYER = PlayerExecution.READER;

    BukkitExecutions() {
    }
}

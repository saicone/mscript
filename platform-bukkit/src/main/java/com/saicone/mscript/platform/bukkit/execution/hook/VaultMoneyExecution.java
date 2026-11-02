package com.saicone.mscript.platform.bukkit.execution.hook;

import com.saicone.mscript.Context;
import com.saicone.mscript.Execution;
import com.saicone.mscript.Result;
import com.saicone.mscript.impl.SingleSection;
import com.saicone.mscript.io.SectionReader;
import com.saicone.mscript.util.Lazy;
import com.saicone.types.Types;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

/**
 * Vault money execution implementation.
 *
 * @author Rubenicos
 */
public class VaultMoneyExecution extends SingleSection<Double> implements Execution {

    private static final Lazy<Object> ECONOMY = Lazy.init(() -> {
        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            final RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                return rsp.getProvider();
            }
        }
        return null;
    });

    /**
     * Constructs a new vault money execution reader.
     *
     * @return a section reader for vault money execution, or null if Vault is not enabled
     */
    @Nullable
    public static SectionReader<VaultMoneyExecution> reader() {
        if (!Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            return null;
        }

        return reader("vault:(give|add|deposit|take|remove|withdraw|set)-?(money|eco)", (id, object) -> {
            if (id.startsWith("vault:give") || id.startsWith("vault:add") || id.startsWith("vault:deposit")) {
                return new VaultMoneyExecution(Mode.GIVE, object);
            } else if (id.startsWith("vault:take") || id.startsWith("vault:remove") || id.startsWith("vault:withdraw")) {
                return new VaultMoneyExecution(Mode.TAKE, object);
            } else if (id.startsWith("vault:set")) {
                return new VaultMoneyExecution(Mode.SET, object);
            }
            throw new IllegalArgumentException("Invalid vault money execution id: " + id);
        });
    }

    private final Mode mode;

    /**
     * Constructs a new vault money execution.
     *
     * @param mode   the mode of the execution (GIVE, TAKE, SET)
     * @param object a double representing the amount of money to give/take/set
     */
    public VaultMoneyExecution(@NotNull Mode mode, @Nullable Object object) {
        super(object);
        this.mode = mode;
    }

    @Override
    protected @NonNull Double parse(@NotNull Object object) {
        return Types.DOUBLE.parse(object);
    }

    @Override
    public @NotNull Result run(@NotNull Context context) {
        final Player player = context.get();
        final double amount = getValue(context);
        final Economy economy = (Economy) ECONOMY.get();

        switch (mode) {
            case GIVE -> economy.depositPlayer(player, amount);
            case TAKE -> economy.withdrawPlayer(player, amount);
            case SET -> {
                final double balance = economy.getBalance(player);
                if (balance < amount) {
                    economy.depositPlayer(player, amount - balance);
                } else if (balance > amount) {
                    economy.withdrawPlayer(player, balance - amount);
                }
            }
        }

        return Result.DONE;
    }

    /**
     * The mode of the vault money execution.
     */
    public enum Mode {
        GIVE,
        TAKE,
        SET
    }
}

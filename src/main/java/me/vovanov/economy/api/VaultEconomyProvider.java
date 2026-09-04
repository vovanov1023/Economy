package me.vovanov.economy.api;

import me.vovanov.economy.model.enums.TransactionResult;
import me.vovanov.economy.services.ConfigService;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Collections;
import java.util.List;

public class VaultEconomyProvider implements Economy {
    private final EconomyAPI api;

    public VaultEconomyProvider(EconomyAPI api) {
        this.api = api;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "Economy";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double v) {
        int amount = (int) Math.floor(v);
        String alias = ConfigService.getInstance().getCurrencyAlias();
        return amount + " " + alias;
    }

    @Override
    public String currencyNamePlural() {
        return ConfigService.getInstance().getCurrencyAlias();
    }

    @Override
    public String currencyNameSingular() {
        return ConfigService.getInstance().getCurrencyAlias();
    }

    // ─── hasAccount ───────────────────────────────────────────────

    /**
     * @param s player name
     * @deprecated
     */
    @Override
    public boolean hasAccount(String s) {
        OfflinePlayer player = resolvePlayer(s);
        if (player == null) return false;
        return hasAccount(player);
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer) {
        return api.hasBankAccount(offlinePlayer.getUniqueId());
    }

    /**
     * @param s  player name
     * @param s1 world name (ignored — economy is global)
     * @deprecated
     */
    @Override
    public boolean hasAccount(String s, String s1) {
        return hasAccount(s);
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return hasAccount(offlinePlayer);
    }

    // ─── getBalance ───────────────────────────────────────────────

    /**
     * @param s player name
     * @deprecated
     */
    @Override
    public double getBalance(String s) {
        OfflinePlayer player = resolvePlayer(s);
        if (player == null) return 0;
        return getBalance(player);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer) {
        int balance = api.getUserBalance(offlinePlayer.getUniqueId());
        return balance == -1 ? 0 : balance;
    }

    /**
     * @param s  player name
     * @param s1 world name (ignored)
     * @deprecated
     */
    @Override
    public double getBalance(String s, String s1) {
        return getBalance(s);
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        return getBalance(offlinePlayer);
    }

    // ─── has ──────────────────────────────────────────────────────

    /**
     * @param s player name
     * @param v amount
     * @deprecated
     */
    @Override
    public boolean has(String s, double v) {
        OfflinePlayer player = resolvePlayer(s);
        if (player == null) return false;
        return has(player, v);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, double v) {
        int balance = api.getUserBalance(offlinePlayer.getUniqueId());
        if (balance == -1) return false;
        return balance >= Math.floor(v);
    }

    /**
     * @param s  player name
     * @param s1 world name (ignored)
     * @param v  amount
     * @deprecated
     */
    @Override
    public boolean has(String s, String s1, double v) {
        return has(s, v);
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        return has(offlinePlayer, v);
    }

    // ─── withdrawPlayer ───────────────────────────────────────────

    /**
     * @param s player name
     * @param v amount
     * @deprecated
     */
    @Override
    public EconomyResponse withdrawPlayer(String s, double v) {
        OfflinePlayer player = resolvePlayer(s);
        if (player == null) {
            return new EconomyResponse(v, 0, EconomyResponse.ResponseType.FAILURE, "Player not found");
        }
        return withdrawPlayer(player, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, double v) {
        int flooredVal = (int) Math.floor(v);
        TransactionResult result = api.withdrawUser(offlinePlayer.getUniqueId(), flooredVal);
        int newBalance = api.getUserBalance(offlinePlayer.getUniqueId());

        if (result == TransactionResult.SUCCESS) {
            return new EconomyResponse(flooredVal, newBalance, EconomyResponse.ResponseType.SUCCESS, null);
        }

        return new EconomyResponse(flooredVal, Math.max(newBalance, 0), EconomyResponse.ResponseType.FAILURE, result.name());
    }

    /**
     * @param s  player name
     * @param s1 world name (ignored)
     * @param v  amount
     * @deprecated
     */
    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        return withdrawPlayer(s, v);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return withdrawPlayer(offlinePlayer, v);
    }

    // ─── depositPlayer ────────────────────────────────────────────

    /**
     * @param s player name
     * @param v amount
     * @deprecated
     */
    @Override
    public EconomyResponse depositPlayer(String s, double v) {
        OfflinePlayer player = resolvePlayer(s);
        if (player == null) {
            return new EconomyResponse(v, 0, EconomyResponse.ResponseType.FAILURE, "Player not found");
        }
        return depositPlayer(player, v);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, double v) {
        int flooredVal = (int) Math.floor(v);
        TransactionResult result = api.depositUser(offlinePlayer.getUniqueId(), flooredVal);
        int newBalance = api.getUserBalance(offlinePlayer.getUniqueId());

        if (result == TransactionResult.SUCCESS) {
            return new EconomyResponse(flooredVal, newBalance, EconomyResponse.ResponseType.SUCCESS, null);
        }

        return new EconomyResponse(flooredVal, Math.max(newBalance, 0), EconomyResponse.ResponseType.FAILURE, result.name());
    }

    /**
     * @param s  player name
     * @param s1 world name (ignored)
     * @param v  amount
     * @deprecated
     */
    @Override
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        return depositPlayer(s, v);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return depositPlayer(offlinePlayer, v);
    }

    // ─── Bank operations (not supported) ──────────────────────────

    private EconomyResponse bankNotSupported() {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Bank support is not implemented");
    }

    /**
     * @param s  bank name
     * @param s1 player name
     * @deprecated
     */
    @Override
    public EconomyResponse createBank(String s, String s1) {
        return bankNotSupported();
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return bankNotSupported();
    }

    @Override
    public EconomyResponse deleteBank(String s) {
        return bankNotSupported();
    }

    @Override
    public EconomyResponse bankBalance(String s) {
        return bankNotSupported();
    }

    @Override
    public EconomyResponse bankHas(String s, double v) {
        return bankNotSupported();
    }

    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return bankNotSupported();
    }

    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return bankNotSupported();
    }

    /**
     * @param s  bank name
     * @param s1 player name
     * @deprecated
     */
    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return bankNotSupported();
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return bankNotSupported();
    }

    /**
     * @param s  bank name
     * @param s1 player name
     * @deprecated
     */
    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return bankNotSupported();
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return bankNotSupported();
    }

    @Override
    public List<String> getBanks() {
        return Collections.emptyList();
    }

    // ─── createPlayerAccount ──────────────────────────────────────

    /**
     * @param s player name
     * @deprecated
     */
    @Override
    public boolean createPlayerAccount(String s) {
        OfflinePlayer player = resolvePlayer(s);
        if (player == null) return false;
        return createPlayerAccount(player);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer) {
        if (api.hasBankAccount(offlinePlayer.getUniqueId())) return false;
        api.createBankAccount(offlinePlayer.getUniqueId());
        return true;
    }

    /**
     * @param s  player name
     * @param s1 world name (ignored)
     * @deprecated
     */
    @Override
    public boolean createPlayerAccount(String s, String s1) {
        return createPlayerAccount(s);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return createPlayerAccount(offlinePlayer);
    }

    // ─── Helpers ──────────────────────────────────────────────────

    /**
     * Resolves a player name to an OfflinePlayer.
     * Returns null if the name cannot be resolved.
     */

    private OfflinePlayer resolvePlayer(String name) {
        if (name == null || name.isEmpty()) return null;
        return Bukkit.getOfflinePlayerIfCached(name);
    }
}

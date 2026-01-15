package br.com.finalcraft.evernifecore.economy;

import br.com.finalcraft.evernifecore.config.playerdata.IPlayerData;
import br.com.finalcraft.evernifecore.exeptions.HytaleDoesNotHaveTheMinecraftEquivalentYet;

public class EconomyManager {

    private static IEconomyProvider ECONOMY_PROVIDER = new IEconomyProvider() {
        @Override
        public double ecoGet(IPlayerData playerData) {
            throw new HytaleDoesNotHaveTheMinecraftEquivalentYet();
        }

        @Override
        public void ecoGive(IPlayerData playerData, double value) {
            throw new HytaleDoesNotHaveTheMinecraftEquivalentYet();
        }

        @Override
        public boolean ecoTake(IPlayerData playerData, double value) {
            throw new HytaleDoesNotHaveTheMinecraftEquivalentYet();
        }

        @Override
        public void ecoSet(IPlayerData playerData, double value) {
            throw new HytaleDoesNotHaveTheMinecraftEquivalentYet();
        }
    };

    public static void setEconomyProvider(IEconomyProvider economyProvider) {
        ECONOMY_PROVIDER = economyProvider;
    }

    public static IEconomyProvider getProvider() {
        return ECONOMY_PROVIDER;
    }
}

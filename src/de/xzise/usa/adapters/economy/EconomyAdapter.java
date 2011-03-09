package de.xzise.usa.adapters.economy;

import de.xzise.usa.adapters.Adapter;

public interface EconomyAdapter extends Adapter {
	public EconomyAccount getAccount(String id) throws IllegalArgumentException;
    public EconomyAccount newAccount(String id) throws IllegalArgumentException;
    public EconomyAccount[] getAccounts();
    
    String formatMoney(int amount);
}

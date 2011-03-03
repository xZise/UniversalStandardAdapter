package de.xzise.usa.adapters;

public interface EconomyAdapter extends Adapter {
	public EconomyAccount getAccount(String id) throws IllegalArgumentException;
    public EconomyAccount newAccount(String id) throws IllegalArgumentException;
    public EconomyAccount[] getAccounts();
}

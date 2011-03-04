package de.xzise.usa.adapters.economy;

public interface EconomyAccount {
	boolean canPay(int amount);
	
	void transferMoney(int amount) throws EconomyException;
	void transferMoney(int amount, EconomyAccount target) throws EconomyException;
	String formatMoney(int amount);
}

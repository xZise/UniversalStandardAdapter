package de.xzise.usa.adapters.economy;

public interface EconomyAccount {
	boolean canPay(int amount);
	
	void transferMoney(int amount);
	void transferMoney(int amount, EconomyAccount target);
}

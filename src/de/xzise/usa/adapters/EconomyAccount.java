package de.xzise.usa.adapters;

public interface EconomyAccount {
	boolean canPay(int amount);
	
	void transferMoney(int amount);
	void transferMoney(int amount, EconomyAccount target);
}

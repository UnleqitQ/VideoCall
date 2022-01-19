package com.unleqitq.videocall.client.managers;

import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import com.unleqitq.videocall.sharedclasses.account.AbstractAccountManager;
import com.unleqitq.videocall.sharedclasses.account.Account;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AccountManager extends AbstractAccountManager {
	
	@NotNull
	private final IManagerHandler managerHandler;
	
	
	public AccountManager(@NotNull IManagerHandler managerHandler) {
		this.managerHandler = managerHandler;
	}
	
	@NotNull
	@Override
	public IManagerHandler getManagerHandler() {
		return managerHandler;
	}
	
	@NotNull
	@Override
	public Map<String, Account> getAccountNameMap() {
		return new HashMap<>();
	}
	
	@NotNull
	@Override
	public Map<UUID, Account> getAccountMap() {
		return new HashMap<>();
	}
	
	@Nullable
	@Override
	public Account getAccount(@NotNull UUID uuid) {
		return null;
	}
	
	@Nullable
	@Override
	public Account getAccount(@NotNull String username) {
		return null;
	}
	
	@Override
	public void save(@NotNull File file) throws IOException {
	}
	
	@Override
	public void addAccount(@NotNull Account account) {
	}
	
	@Override
	public void load(@NotNull File file) throws IOException {
	}
	
}

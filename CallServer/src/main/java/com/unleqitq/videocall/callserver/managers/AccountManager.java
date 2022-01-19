package com.unleqitq.videocall.callserver.managers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.unleqitq.videocall.sharedclasses.IManagerHandler;
import com.unleqitq.videocall.sharedclasses.account.AbstractAccountManager;
import com.unleqitq.videocall.sharedclasses.account.Account;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.collections4.MapUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
	private final Map<UUID, Account> accountMap = MapUtils.synchronizedMap(new HashMap<>());
	@NotNull
	private final Map<String, Account> accountNameMap = MapUtils.synchronizedMap(new HashMap<>());
	
	@NotNull
	@Override
	public Map<String, Account> getAccountNameMap() {
		return accountNameMap;
	}
	
	@NotNull
	@Override
	public Map<UUID, Account> getAccountMap() {
		return accountMap;
	}
	
	@Nullable
	@Override
	public Account getAccount(@NotNull UUID uuid) {
		return accountMap.get(uuid);
	}
	
	@Nullable
	@Override
	public Account getAccount(@NotNull String username) {
		return accountNameMap.get(username);
	}
	
	@Override
	public void save(@NotNull File file) throws IOException {
		if (!file.exists()) {
			if (!file.getParentFile().exists())
				file.getParentFile().mkdirs();
			file.createNewFile();
		}
		JsonArray array = new JsonArray();
		for (Account account : accountMap.values()) {
			array.add(account.save());
		}
		JsonWriter writer = new JsonWriter(new FileWriter(file));
		new Gson().toJson(array, writer);
		writer.close();
	}
	
	@Override
	public void addAccount(@NotNull Account account) {
		accountMap.put(account.getUuid(), account);
		accountNameMap.put(account.getUsername(), account);
	}
	
	@Override
	public void load(@NotNull File file) throws IOException {
		if (!file.exists()) {
			return;
		}
		JsonReader reader = new JsonReader(new FileReader(file));
		JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
		for (JsonElement element : array) {
			try {
				addAccount(Account.load(managerHandler, element.getAsJsonObject()));
			} catch (DecoderException e) {
				e.printStackTrace();
			}
		}
		reader.close();
	}
	
	
	@Nullable
	public Account createAccount(@NotNull String username, @NotNull byte[] password) {
		if (accountNameMap.containsKey(username))
			return null;
		UUID uuid;
		do {
			uuid = UUID.randomUUID();
		} while (getAccountMap().containsKey(uuid));
		Account account = new Account(managerHandler, uuid, username, password);
		addAccount(account);
		return account;
	}
	
}

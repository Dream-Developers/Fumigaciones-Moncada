package com.example.fumigacionesmoncada.SyncAdapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

public class SyncAdapterUsuarios extends AbstractThreadedSyncAdapter {
    private ContentResolver mContentResolver;
    private String clientes;
    private String token;
    private AccountManager mAccountManager;


    public SyncAdapterUsuarios(Context context, boolean autoInitialize){
        super(context, autoInitialize);
        this.mContentResolver = context.getContentResolver();
        mAccountManager = AccountManager.get(context);

    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {

    }

}

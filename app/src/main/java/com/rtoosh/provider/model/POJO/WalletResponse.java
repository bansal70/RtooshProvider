package com.rtoosh.provider.model.POJO;

/*
 * Created by rishav on 11/16/2017.
 */

import com.google.gson.annotations.SerializedName;
import com.rtoosh.provider.model.network.AbstractApiResponse;

public class WalletResponse extends AbstractApiResponse{
    @SerializedName("data")
    public Data data;
    @SerializedName("account")
    public Account account;
    @SerializedName("wallet")
    public Wallet wallet;

    public class Data {
        @SerializedName("Today")
        public float today;
        @SerializedName("Weekly")
        public float weekly;
        @SerializedName("Monthly")
        public float monthly;
        @SerializedName("Total")
        public float total;
    }

    public class Account {
        @SerializedName("Account")
        public AccountDetails account;
    }

    public class AccountDetails {
        @SerializedName("id")
        public String id;
        @SerializedName("user_id")
        public String userId;
        @SerializedName("name")
        public String name;
        @SerializedName("iban_no")
        public String ibanNo;
        @SerializedName("created")
        public String created;
        @SerializedName("modified")
        public String modified;
    }

    public class Wallet {
        @SerializedName("earning")
        public String earning;
        @SerializedName("due")
        public String due;
        @SerializedName("balance")
        public String balance;
    }

}

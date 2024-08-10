package me.vlink102.melomod.util.wrappers.hypixel;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.vlink102.melomod.util.game.SkyblockUtil;

import java.util.ArrayList;
import java.util.List;

public class Banking {
    private final Double balance;
    private final List<Transaction> transactions;

    @Deprecated
    public Banking(double balance, List<Transaction> transactions) {
        this.balance = balance;
        this.transactions = transactions;
    }

    public Banking(JsonObject object) {
        this.balance = SkyblockUtil.getAsDouble("balance", object);
        JsonArray transactionArray = SkyblockUtil.getAsJsonArray("transactions", object);
        List<Transaction> transactions = new ArrayList<>();
        for (JsonElement jsonElement : transactionArray) {
            JsonObject transaction = jsonElement.getAsJsonObject();
            transactions.add(new Transaction(transaction));
        }
        this.transactions = transactions;
    }

    public double getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public static class Transaction {
        private final Long timestamp;
        private final Transaction.Action action;
        private final String initiatorName;
        private final Double amount;

        @Deprecated
        public Transaction(Long timestamp, Transaction.Action action, String initiatorName, Double amount) {
            this.timestamp = timestamp;
            this.action = action;
            this.initiatorName = initiatorName;
            this.amount = amount;
        }

        public Transaction(JsonObject object) {
            this.amount = SkyblockUtil.getAsDouble("amount", object);
            this.timestamp = SkyblockUtil.getAsLong("timestamp", object);
            this.action = Transaction.Action.parseFromJSON(SkyblockUtil.getAsString("action", object));
            this.initiatorName = SkyblockUtil.getAsString("initiator_name", object);
        }

        public long getTimestamp() {
            return timestamp;
        }

        public Transaction.Action getAction() {
            return action;
        }

        public double getAmount() {
            return amount;
        }

        public String getInitiatorName() {
            return initiatorName;
        }

        public enum Action {
            DEPOSIT,
            WITHDRAW;

            public static Transaction.Action parseFromJSON(String action) {
                if (action == null) return null;
                if (action.equalsIgnoreCase("DEPOSIT")) return DEPOSIT;
                if (action.equalsIgnoreCase("WITHDRAW")) return WITHDRAW;
                return null;
            }
        }
    }
}

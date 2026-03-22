package helpers;

import models.Transaction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class TransactionListHelper {

    private TransactionListHelper() {
    }

    public static List<Transaction> newestFirst(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return List.of();
        }
        List<Transaction> copy = new ArrayList<>(transactions);
        copy.sort(byIdNewestFirst());
        return copy;
    }

    private static Comparator<Transaction> byIdNewestFirst() {
        return Comparator.comparing(Transaction::getId, Comparator.nullsLast(Comparator.reverseOrder()));
    }
}

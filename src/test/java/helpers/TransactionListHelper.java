package helpers;

import models.Transaction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * API может отдавать {@code transactions} в любом порядке. Для проверок «последняя операция»
 * приводим список к единому порядку: сначала самые новые (по {@code id}, как у типичного автоинкремента в БД).
 */
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

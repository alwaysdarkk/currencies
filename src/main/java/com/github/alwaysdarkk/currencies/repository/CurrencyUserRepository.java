package com.github.alwaysdarkk.currencies.repository;

import com.github.alwaysdarkk.currencies.data.currency.Currency;
import com.github.alwaysdarkk.currencies.data.user.CurrencyLeaderboardUser;
import com.github.alwaysdarkk.currencies.data.user.CurrencyUser;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class CurrencyUserRepository {

    private final SessionFactory sessionFactory;
    private final ExecutorService executorService;

    public CurrencyUserRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.executorService = Executors.newVirtualThreadPerTaskExecutor();
    }

    public @Nonnull CompletableFuture<Void> bulkInsert(@Nonnull Collection<CurrencyUser> users) {
        final List<CurrencyUser> dirtyUsers =
                users.stream().filter(CurrencyUser::isDirty).toList();
        if (dirtyUsers.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.runAsync(
                () -> {
                    try (Session session = this.sessionFactory.openSession()) {
                        final Transaction transaction = session.beginTransaction();
                        try {
                            int savedCount = 0;
                            for (CurrencyUser user : dirtyUsers) {
                                session.merge(user);

                                if (++savedCount % 50 == 0) {
                                    session.flush();
                                    session.clear();
                                }
                            }

                            transaction.commit();

                            dirtyUsers.forEach(user -> user.setDirty(false));
                        } catch (Exception exception) {
                            if (transaction.isActive()) {
                                transaction.rollback();
                            }

                            throw new RuntimeException("It was not possible save users.", exception);
                        }
                    }
                },
                this.executorService);
    }

    public @Nonnull CompletableFuture<Optional<CurrencyUser>> find(@Nonnull UUID id) {
        return CompletableFuture.supplyAsync(
                () -> {
                    try (Session session = this.sessionFactory.openSession()) {
                        return Optional.ofNullable(session.find(CurrencyUser.class, id));
                    }
                },
                this.executorService);
    }

    public @Nonnull CompletableFuture<List<CurrencyLeaderboardUser>> findLeaderboard(@Nonnull Currency currency) {
        return CompletableFuture.supplyAsync(
                () -> {
                    try (Session session = this.sessionFactory.openSession()) {
                        final String query = "SELECT u FROM CurrencyUser u " + "JOIN u.currencyMap m "
                                + "WHERE KEY(m) = :currencyId "
                                + "ORDER BY m DESC";
                        final List<CurrencyUser> users = session.createQuery(query, CurrencyUser.class)
                                .setParameter("currencyId", currency.getId())
                                .setMaxResults(10)
                                .getResultList();

                        return IntStream.range(0, users.size())
                                .mapToObj(index -> {
                                    final CurrencyUser user = users.get(index);
                                    return new CurrencyLeaderboardUser(
                                            user.getId(), user.getUsername(), index + 1, user.getAmount(currency));
                                })
                                .toList();
                    }
                },
                this.executorService);
    }

    public void shutdown() {
        this.executorService.shutdown();
    }
}

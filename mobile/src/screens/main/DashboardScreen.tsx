import React, { useEffect, useState } from 'react';
import { View, StyleSheet, ScrollView, RefreshControl } from 'react-native';
import { Text, Card, useTheme, ActivityIndicator } from 'react-native-paper';
import { LineChart } from 'react-native-chart-kit';
import { Dimensions } from 'react-native';
import { accountService, transactionService, budgetService, savingsGoalService } from '../../services/api';
import type { Account, Transaction, Budget, SavingsGoal } from '../../types';

const DashboardScreen = () => {
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [recentTransactions, setRecentTransactions] = useState<Transaction[]>([]);
  const [budgets, setBudgets] = useState<Budget[]>([]);
  const [savingsGoals, setSavingsGoals] = useState<SavingsGoal[]>([]);
  const theme = useTheme();

  const fetchData = async () => {
    try {
      const [accountsData, transactionsData, budgetsData, goalsData] = await Promise.all([
        accountService.getAccounts(),
        transactionService.getTransactions(0, 5),
        budgetService.getBudgets(),
        savingsGoalService.getSavingsGoals(),
      ]);

      setAccounts(accountsData);
      setRecentTransactions(transactionsData.content);
      setBudgets(budgetsData);
      setSavingsGoals(goalsData);
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const onRefresh = () => {
    setRefreshing(true);
    fetchData();
  };

  if (loading) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color={theme.colors.primary} />
      </View>
    );
  }

  return (
    <ScrollView
      style={styles.container}
      refreshControl={
        <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
      }
    >
      {/* Total Balance Card */}
      <Card style={styles.card}>
        <Card.Content>
          <Text variant="titleMedium">Total Balance</Text>
          <Text variant="headlineMedium" style={styles.balance}>
            ${accounts.reduce((total, acc) => total + acc.currentBalance, 0).toFixed(2)}
          </Text>
        </Card.Content>
      </Card>

      {/* Recent Transactions */}
      <Card style={styles.card}>
        <Card.Content>
          <Text variant="titleMedium" style={styles.sectionTitle}>
            Recent Transactions
          </Text>
          {recentTransactions.map((transaction) => (
            <View key={transaction.id} style={styles.transactionItem}>
              <View>
                <Text variant="bodyLarge">{transaction.description}</Text>
                <Text variant="bodySmall" style={{ color: theme.colors.disabled }}>
                  {new Date(transaction.transactionDate).toLocaleDateString()}
                </Text>
              </View>
              <Text
                variant="bodyLarge"
                style={{
                  color:
                    transaction.type === 'INCOME'
                      ? theme.colors.success
                      : theme.colors.error,
                }}
              >
                {transaction.type === 'INCOME' ? '+' : '-'}${transaction.amount}
              </Text>
            </View>
          ))}
        </Card.Content>
      </Card>

      {/* Budget Overview */}
      <Card style={styles.card}>
        <Card.Content>
          <Text variant="titleMedium" style={styles.sectionTitle}>
            Budget Overview
          </Text>
          {budgets.map((budget) => (
            <View key={budget.id} style={styles.budgetItem}>
              <View>
                <Text variant="bodyLarge">{budget.category}</Text>
                <Text variant="bodySmall" style={{ color: theme.colors.disabled }}>
                  ${budget.spentAmount} / ${budget.amount}
                </Text>
              </View>
              <Text
                variant="bodyLarge"
                style={{
                  color:
                    budget.spentAmount > budget.amount
                      ? theme.colors.error
                      : theme.colors.success,
                }}
              >
                {((budget.spentAmount / budget.amount) * 100).toFixed(0)}%
              </Text>
            </View>
          ))}
        </Card.Content>
      </Card>

      {/* Savings Goals */}
      <Card style={styles.card}>
        <Card.Content>
          <Text variant="titleMedium" style={styles.sectionTitle}>
            Savings Goals
          </Text>
          {savingsGoals.map((goal) => (
            <View key={goal.id} style={styles.goalItem}>
              <View>
                <Text variant="bodyLarge">{goal.name}</Text>
                <Text variant="bodySmall" style={{ color: theme.colors.disabled }}>
                  ${goal.currentAmount} / ${goal.targetAmount}
                </Text>
              </View>
              <Text
                variant="bodyLarge"
                style={{
                  color:
                    goal.currentAmount >= goal.targetAmount
                      ? theme.colors.success
                      : theme.colors.primary,
                }}
              >
                {((goal.currentAmount / goal.targetAmount) * 100).toFixed(0)}%
              </Text>
            </View>
          ))}
        </Card.Content>
      </Card>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  loadingContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  card: {
    margin: 16,
    elevation: 4,
  },
  balance: {
    marginTop: 8,
    fontWeight: 'bold',
  },
  sectionTitle: {
    marginBottom: 16,
  },
  transactionItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 8,
    borderBottomWidth: 1,
    borderBottomColor: '#e0e0e0',
  },
  budgetItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 8,
    borderBottomWidth: 1,
    borderBottomColor: '#e0e0e0',
  },
  goalItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 8,
    borderBottomWidth: 1,
    borderBottomColor: '#e0e0e0',
  },
});

export default DashboardScreen; 
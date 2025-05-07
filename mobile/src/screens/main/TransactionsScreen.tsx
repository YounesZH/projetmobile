import React, { useEffect, useState } from 'react';
import { View, StyleSheet, ScrollView, RefreshControl } from 'react-native';
import { Text, Card, useTheme, ActivityIndicator, FAB, Portal, Dialog, TextInput, Button, SegmentedButtons } from 'react-native-paper';
import { Transaction, Account, TransactionType } from '../../types';
import { transactionService, accountService } from '../../services/api';

interface TransactionForm {
  amount: string;
  account: string;
  description: string;
  type: TransactionType;
  category: string;
  isRecurring: boolean;
}

const TransactionsScreen = () => {
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [visible, setVisible] = useState(false);
  const [newTransaction, setNewTransaction] = useState<TransactionForm>({
    amount: '',
    account: '',
    description: '',
    type: 'EXPENSE',
    category: '',
    isRecurring: false,
  });
  const theme = useTheme();

  const fetchData = async () => {
    try {
      const [transactionsData, accountsData] = await Promise.all([
        transactionService.getTransactions(0, 20),
        accountService.getAccounts(),
      ]);
      setTransactions(transactionsData.content);
      setAccounts(accountsData);
    } catch (error) {
      console.error('Error fetching data:', error);
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

  const handleCreateTransaction = async () => {
    try {
      await transactionService.createTransaction({
        ...newTransaction,
        amount: parseFloat(newTransaction.amount),
        account: { id: parseInt(newTransaction.account) } as Account,
      });
      setVisible(false);
      setNewTransaction({
        amount: '',
        account: '',
        description: '',
        type: 'EXPENSE',
        category: '',
        isRecurring: false,
      });
      fetchData();
    } catch (error) {
      console.error('Error creating transaction:', error);
    }
  };

  if (loading) {
    return (
      <View style={styles.loadingContainer}>
        <ActivityIndicator size="large" color={theme.colors.primary} />
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <ScrollView
        refreshControl={
          <RefreshControl refreshing={refreshing} onRefresh={onRefresh} />
        }
      >
        {transactions.map((transaction) => (
          <Card key={transaction.id} style={styles.card}>
            <Card.Content>
              <View style={styles.transactionHeader}>
                <View>
                  <Text variant="titleMedium">{transaction.description}</Text>
                  <Text variant="bodySmall" style={{ color: theme.colors.disabled }}>
                    {new Date(transaction.transactionDate).toLocaleDateString()}
                  </Text>
                  <Text variant="bodySmall" style={{ color: theme.colors.disabled }}>
                    {transaction.category}
                  </Text>
                </View>
                <Text
                  variant="titleLarge"
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
            </Card.Content>
          </Card>
        ))}
      </ScrollView>

      <Portal>
        <Dialog visible={visible} onDismiss={() => setVisible(false)}>
          <Dialog.Title>Add New Transaction</Dialog.Title>
          <Dialog.Content>
            <TextInput
              label="Description"
              value={newTransaction.description}
              onChangeText={(text) => setNewTransaction({ ...newTransaction, description: text })}
              style={styles.input}
            />
            <TextInput
              label="Amount"
              value={newTransaction.amount}
              onChangeText={(text) => setNewTransaction({ ...newTransaction, amount: text })}
              keyboardType="decimal-pad"
              style={styles.input}
            />
            <TextInput
              label="Category"
              value={newTransaction.category}
              onChangeText={(text) => setNewTransaction({ ...newTransaction, category: text })}
              style={styles.input}
            />
            <SegmentedButtons
              value={newTransaction.type}
              onValueChange={(value) => setNewTransaction({ ...newTransaction, type: value as TransactionType })}
              buttons={[
                { value: 'INCOME', label: 'Income' },
                { value: 'EXPENSE', label: 'Expense' },
                { value: 'TRANSFER', label: 'Transfer' },
              ]}
              style={styles.segmentedButtons}
            />
          </Dialog.Content>
          <Dialog.Actions>
            <Button onPress={() => setVisible(false)}>Cancel</Button>
            <Button onPress={handleCreateTransaction}>Create</Button>
          </Dialog.Actions>
        </Dialog>
      </Portal>

      <FAB
        icon="plus"
        style={styles.fab}
        onPress={() => setVisible(true)}
      />
    </View>
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
  transactionHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  input: {
    marginBottom: 16,
  },
  segmentedButtons: {
    marginBottom: 16,
  },
  fab: {
    position: 'absolute',
    margin: 16,
    right: 0,
    bottom: 0,
  },
});

export default TransactionsScreen; 
import React, { useEffect, useState } from 'react';
import { View, StyleSheet, ScrollView, RefreshControl } from 'react-native';
import { Text, Card, useTheme, ActivityIndicator, FAB, Portal, Dialog, TextInput, Button } from 'react-native-paper';
import { Account, AccountType } from '../../types';
import { accountService } from '../../services/api';

interface AccountForm {
  name: string;
  type: AccountType;
  currency: string;
}

const AccountsScreen = () => {
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [visible, setVisible] = useState(false);
  const [newAccount, setNewAccount] = useState<AccountForm>({
    name: '',
    type: 'CHECKING',
    currency: 'USD',
  });
  const theme = useTheme();

  const fetchAccounts = async () => {
    try {
      const data = await accountService.getAccounts();
      setAccounts(data);
    } catch (error) {
      console.error('Error fetching accounts:', error);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  useEffect(() => {
    fetchAccounts();
  }, []);

  const onRefresh = () => {
    setRefreshing(true);
    fetchAccounts();
  };

  const handleCreateAccount = async () => {
    try {
      await accountService.createAccount(newAccount);
      setVisible(false);
      setNewAccount({ name: '', type: 'CHECKING', currency: 'USD' });
      fetchAccounts();
    } catch (error) {
      console.error('Error creating account:', error);
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
        {accounts.map((account) => (
          <Card key={account.id} style={styles.card}>
            <Card.Content>
              <View style={styles.accountHeader}>
                <View>
                  <Text variant="titleMedium">{account.name}</Text>
                  <Text variant="bodySmall" style={{ color: theme.colors.disabled }}>
                    {account.type}
                  </Text>
                </View>
                <Text
                  variant="titleLarge"
                  style={{
                    color: account.currentBalance >= 0 ? theme.colors.success : theme.colors.error,
                  }}
                >
                  {account.currency} {account.currentBalance.toFixed(2)}
                </Text>
              </View>
            </Card.Content>
          </Card>
        ))}
      </ScrollView>

      <Portal>
        <Dialog visible={visible} onDismiss={() => setVisible(false)}>
          <Dialog.Title>Add New Account</Dialog.Title>
          <Dialog.Content>
            <TextInput
              label="Account Name"
              value={newAccount.name}
              onChangeText={(text) => setNewAccount({ ...newAccount, name: text })}
              style={styles.input}
            />
            <TextInput
              label="Currency"
              value={newAccount.currency}
              onChangeText={(text) => setNewAccount({ ...newAccount, currency: text })}
              style={styles.input}
            />
          </Dialog.Content>
          <Dialog.Actions>
            <Button onPress={() => setVisible(false)}>Cancel</Button>
            <Button onPress={handleCreateAccount}>Create</Button>
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
  accountHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  input: {
    marginBottom: 16,
  },
  fab: {
    position: 'absolute',
    margin: 16,
    right: 0,
    bottom: 0,
  },
});

export default AccountsScreen; 
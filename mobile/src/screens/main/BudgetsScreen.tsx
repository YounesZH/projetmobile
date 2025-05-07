import React, { useEffect, useState } from 'react';
import { View, StyleSheet, ScrollView, RefreshControl } from 'react-native';
import { Text, Card, useTheme, ActivityIndicator, FAB, Portal, Dialog, TextInput, Button, ProgressBar, SegmentedButtons } from 'react-native-paper';
import { Budget, BudgetPeriod } from '../../types';
import { budgetService } from '../../services/api';

interface BudgetForm {
  amount: string;
  category: string;
  period: BudgetPeriod;
}

const BudgetsScreen = () => {
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [budgets, setBudgets] = useState<Budget[]>([]);
  const [visible, setVisible] = useState(false);
  const [newBudget, setNewBudget] = useState<BudgetForm>({
    amount: '',
    category: '',
    period: 'MONTHLY',
  });
  const theme = useTheme();

  const fetchBudgets = async () => {
    try {
      const data = await budgetService.getBudgets();
      setBudgets(data);
    } catch (error) {
      console.error('Error fetching budgets:', error);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  useEffect(() => {
    fetchBudgets();
  }, []);

  const onRefresh = () => {
    setRefreshing(true);
    fetchBudgets();
  };

  const handleCreateBudget = async () => {
    try {
      await budgetService.createBudget({
        ...newBudget,
        amount: parseFloat(newBudget.amount),
      });
      setVisible(false);
      setNewBudget({
        amount: '',
        category: '',
        period: 'MONTHLY',
      });
      fetchBudgets();
    } catch (error) {
      console.error('Error creating budget:', error);
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
        {budgets.map((budget) => (
          <Card key={budget.id} style={styles.card}>
            <Card.Content>
              <View style={styles.budgetHeader}>
                <View>
                  <Text variant="titleMedium">{budget.category}</Text>
                  <Text variant="bodySmall" style={{ color: theme.colors.disabled }}>
                    {budget.period}
                  </Text>
                </View>
                <Text
                  variant="titleLarge"
                  style={{
                    color:
                      budget.spentAmount > budget.amount
                        ? theme.colors.error
                        : theme.colors.success,
                  }}
                >
                  ${budget.spentAmount} / ${budget.amount}
                </Text>
              </View>
              <ProgressBar
                progress={budget.spentAmount / budget.amount}
                color={
                  budget.spentAmount > budget.amount
                    ? theme.colors.error
                    : theme.colors.success
                }
                style={styles.progressBar}
              />
            </Card.Content>
          </Card>
        ))}
      </ScrollView>

      <Portal>
        <Dialog visible={visible} onDismiss={() => setVisible(false)}>
          <Dialog.Title>Add New Budget</Dialog.Title>
          <Dialog.Content>
            <TextInput
              label="Category"
              value={newBudget.category}
              onChangeText={(text) => setNewBudget({ ...newBudget, category: text })}
              style={styles.input}
            />
            <TextInput
              label="Amount"
              value={newBudget.amount}
              onChangeText={(text) => setNewBudget({ ...newBudget, amount: text })}
              keyboardType="decimal-pad"
              style={styles.input}
            />
            <View style={styles.input}>
              <Text>Period</Text>
              <View style={styles.periodButtons}>
                {(['DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY'] as const).map((period) => (
                  <Button
                    key={period}
                    mode={newBudget.period === period ? 'contained' : 'outlined'}
                    onPress={() => setNewBudget({ ...newBudget, period })}
                    style={styles.periodButton}
                  >
                    {period.charAt(0) + period.slice(1).toLowerCase()}
                  </Button>
                ))}
              </View>
            </View>
          </Dialog.Content>
          <Dialog.Actions>
            <Button onPress={() => setVisible(false)}>Cancel</Button>
            <Button onPress={handleCreateBudget}>Create</Button>
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
  budgetHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 8,
  },
  progressBar: {
    height: 8,
    borderRadius: 4,
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
  periodButtons: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  periodButton: {
    marginHorizontal: 4,
  },
});

export default BudgetsScreen; 
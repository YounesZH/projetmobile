import React, { useEffect, useState } from 'react';
import { View, StyleSheet, ScrollView, RefreshControl } from 'react-native';
import { Text, Card, useTheme, ActivityIndicator, FAB, Portal, Dialog, TextInput, Button, ProgressBar, SegmentedButtons } from 'react-native-paper';
import { SavingsGoal, Priority } from '../../types';
import { savingsGoalService } from '../../services/api';

interface SavingsGoalForm {
  name: string;
  targetAmount: string;
  targetDate: string;
  priority: Priority;
}

const SavingsScreen = () => {
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [savingsGoals, setSavingsGoals] = useState<SavingsGoal[]>([]);
  const [visible, setVisible] = useState(false);
  const [newGoal, setNewGoal] = useState<SavingsGoalForm>({
    name: '',
    targetAmount: '',
    targetDate: '',
    priority: 'MEDIUM',
  });
  const theme = useTheme();

  const fetchSavingsGoals = async () => {
    try {
      const data = await savingsGoalService.getSavingsGoals();
      setSavingsGoals(data);
    } catch (error) {
      console.error('Error fetching savings goals:', error);
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  useEffect(() => {
    fetchSavingsGoals();
  }, []);

  const onRefresh = () => {
    setRefreshing(true);
    fetchSavingsGoals();
  };

  const handleCreateGoal = async () => {
    try {
      await savingsGoalService.createSavingsGoal({
        ...newGoal,
        targetAmount: parseFloat(newGoal.targetAmount),
      });
      setVisible(false);
      setNewGoal({
        name: '',
        targetAmount: '',
        targetDate: '',
        priority: 'MEDIUM',
      });
      fetchSavingsGoals();
    } catch (error) {
      console.error('Error creating savings goal:', error);
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
        {savingsGoals.map((goal) => (
          <Card key={goal.id} style={styles.card}>
            <Card.Content>
              <View style={styles.goalHeader}>
                <View>
                  <Text variant="titleMedium">{goal.name}</Text>
                  <Text variant="bodySmall" style={{ color: theme.colors.disabled }}>
                    Target: {new Date(goal.targetDate).toLocaleDateString()}
                  </Text>
                  <Text variant="bodySmall" style={{ color: theme.colors.disabled }}>
                    Priority: {goal.priority}
                  </Text>
                </View>
                <Text
                  variant="titleLarge"
                  style={{
                    color:
                      goal.currentAmount >= goal.targetAmount
                        ? theme.colors.success
                        : theme.colors.primary,
                  }}
                >
                  ${goal.currentAmount} / ${goal.targetAmount}
                </Text>
              </View>
              <ProgressBar
                progress={goal.currentAmount / goal.targetAmount}
                color={
                  goal.currentAmount >= goal.targetAmount
                    ? theme.colors.success
                    : theme.colors.primary
                }
                style={styles.progressBar}
              />
            </Card.Content>
          </Card>
        ))}
      </ScrollView>

      <Portal>
        <Dialog visible={visible} onDismiss={() => setVisible(false)}>
          <Dialog.Title>Add New Savings Goal</Dialog.Title>
          <Dialog.Content>
            <TextInput
              label="Goal Name"
              value={newGoal.name}
              onChangeText={(text) => setNewGoal({ ...newGoal, name: text })}
              style={styles.input}
            />
            <TextInput
              label="Target Amount"
              value={newGoal.targetAmount}
              onChangeText={(text) => setNewGoal({ ...newGoal, targetAmount: text })}
              keyboardType="decimal-pad"
              style={styles.input}
            />
            <TextInput
              label="Target Date"
              value={newGoal.targetDate}
              onChangeText={(text) => setNewGoal({ ...newGoal, targetDate: text })}
              style={styles.input}
            />
            <SegmentedButtons
              value={newGoal.priority}
              onValueChange={(value) => setNewGoal({ ...newGoal, priority: value as Priority })}
              buttons={[
                { value: 'LOW', label: 'Low' },
                { value: 'MEDIUM', label: 'Medium' },
                { value: 'HIGH', label: 'High' },
              ]}
              style={styles.input}
            />
          </Dialog.Content>
          <Dialog.Actions>
            <Button onPress={() => setVisible(false)}>Cancel</Button>
            <Button onPress={handleCreateGoal}>Create</Button>
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
  goalHeader: {
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
});

export default SavingsScreen; 
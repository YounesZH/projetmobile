import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { AuthResponse, Account, Transaction, Budget, SavingsGoal } from '../types';

const API_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor for adding auth token
api.interceptors.request.use(
  async (config) => {
    const token = await AsyncStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for handling unauthorized access
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      await AsyncStorage.removeItem('token');
      // TODO: Navigate to login screen
    }
    return Promise.reject(error);
  }
);

export const authService = {
  login: async (email: string, password: string): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/login', { email, password });
    await AsyncStorage.setItem('token', response.data.token);
    return response.data;
  },

  register: async (userData: { email: string; password: string; firstName: string; lastName: string }): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/register', userData);
    await AsyncStorage.setItem('token', response.data.token);
    return response.data;
  },

  logout: async () => {
    await AsyncStorage.removeItem('token');
  },
};

export const accountService = {
  getAccounts: async (): Promise<Account[]> => {
    const response = await api.get<Account[]>('/accounts');
    return response.data;
  },

  createAccount: async (accountData: Partial<Account>): Promise<Account> => {
    const response = await api.post<Account>('/accounts', accountData);
    return response.data;
  },

  updateAccount: async (id: number, accountData: Partial<Account>): Promise<Account> => {
    const response = await api.put<Account>(`/accounts/${id}`, accountData);
    return response.data;
  },

  deleteAccount: async (id: number): Promise<void> => {
    await api.delete(`/accounts/${id}`);
  },
};

export const transactionService = {
  getTransactions: async (page: number, size: number): Promise<{ content: Transaction[]; totalElements: number }> => {
    const response = await api.get<{ content: Transaction[]; totalElements: number }>(`/transactions?page=${page}&size=${size}`);
    return response.data;
  },

  createTransaction: async (transactionData: Partial<Transaction>): Promise<Transaction> => {
    const response = await api.post<Transaction>('/transactions', transactionData);
    return response.data;
  },

  updateTransaction: async (id: number, transactionData: Partial<Transaction>): Promise<Transaction> => {
    const response = await api.put<Transaction>(`/transactions/${id}`, transactionData);
    return response.data;
  },

  deleteTransaction: async (id: number): Promise<void> => {
    await api.delete(`/transactions/${id}`);
  },
};

export const budgetService = {
  getBudgets: async (): Promise<Budget[]> => {
    const response = await api.get<Budget[]>('/budgets');
    return response.data;
  },

  createBudget: async (budgetData: Partial<Budget>): Promise<Budget> => {
    const response = await api.post<Budget>('/budgets', budgetData);
    return response.data;
  },

  updateBudget: async (id: number, budgetData: Partial<Budget>): Promise<Budget> => {
    const response = await api.put<Budget>(`/budgets/${id}`, budgetData);
    return response.data;
  },

  deleteBudget: async (id: number): Promise<void> => {
    await api.delete(`/budgets/${id}`);
  },

  getBudgetProgress: async (id: number): Promise<{ spent: number; remaining: number }> => {
    const response = await api.get<{ spent: number; remaining: number }>(`/budgets/${id}/progress`);
    return response.data;
  },
};

export const savingsGoalService = {
  getSavingsGoals: async (): Promise<SavingsGoal[]> => {
    const response = await api.get<SavingsGoal[]>('/savings-goals');
    return response.data;
  },

  createSavingsGoal: async (goalData: Partial<SavingsGoal>): Promise<SavingsGoal> => {
    const response = await api.post<SavingsGoal>('/savings-goals', goalData);
    return response.data;
  },

  updateSavingsGoal: async (id: number, goalData: Partial<SavingsGoal>): Promise<SavingsGoal> => {
    const response = await api.put<SavingsGoal>(`/savings-goals/${id}`, goalData);
    return response.data;
  },

  deleteSavingsGoal: async (id: number): Promise<void> => {
    await api.delete(`/savings-goals/${id}`);
  },

  addContribution: async (id: number, amount: number): Promise<SavingsGoal> => {
    const response = await api.post<SavingsGoal>(`/savings-goals/${id}/contribute`, { amount });
    return response.data;
  },
}; 
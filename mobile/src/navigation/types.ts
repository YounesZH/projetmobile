import { NativeStackScreenProps } from '@react-navigation/native-stack';

export type AuthStackParamList = {
  Login: undefined;
  Register: undefined;
  Main: undefined;
};

export type AuthScreenProps<T extends keyof AuthStackParamList> = NativeStackScreenProps<AuthStackParamList, T>;

export type MainTabParamList = {
  Dashboard: undefined;
  Accounts: undefined;
  Transactions: undefined;
  Budgets: undefined;
  Savings: undefined;
}; 
import { ComponentType } from 'react';
import { TextInputProps, ViewStyle } from 'react-native';

declare module 'react-native-paper' {
  export interface TextInputProps extends TextInputProps {
    mode?: 'flat' | 'outlined';
    label?: string;
    value?: string;
    onChangeText?: (text: string) => void;
    secureTextEntry?: boolean;
    autoCapitalize?: 'none' | 'sentences' | 'words' | 'characters';
    keyboardType?: 'default' | 'email-address' | 'numeric' | 'phone-pad' | 'number-pad' | 'decimal-pad';
    style?: ViewStyle;
  }

  export const TextInput: ComponentType<TextInputProps>;
  export const Text: ComponentType<any>;
  export const Button: ComponentType<any>;
  export const useTheme: () => any;
} 
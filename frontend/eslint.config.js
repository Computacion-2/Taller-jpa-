import js from '@eslint/js';
import globals from 'globals';
import react from 'eslint-plugin-react';
import reactHooks from 'eslint-plugin-react-hooks';
import reactRefresh from 'eslint-plugin-react-refresh';
import jsxA11y from 'eslint-plugin-jsx-a11y';

/**
 * Reglas de calidad del proyecto. El script "npm run lint" usa --max-warnings=0,
 * por lo que cualquier infraccion detiene la compilacion del WAR.
 */
export default [
  { ignores: ['dist', 'target', 'node_modules'] },
  {
    files: ['src/**/*.{js,jsx}'],
    languageOptions: {
      ecmaVersion: 'latest',
      sourceType: 'module',
      globals: globals.browser,
      parserOptions: { ecmaFeatures: { jsx: true } },
    },
    settings: { react: { version: 'detect' } },
    plugins: {
      react,
      'react-hooks': reactHooks,
      'react-refresh': reactRefresh,
      'jsx-a11y': jsxA11y,
    },
    rules: {
      ...js.configs.recommended.rules,
      ...react.configs.recommended.rules,
      ...react.configs['jsx-runtime'].rules,
      ...reactHooks.configs.recommended.rules,
      ...jsxA11y.flatConfigs.recommended.rules,

      // Hooks: reglas de los hooks y dependencias completas son obligatorias.
      'react-hooks/rules-of-hooks': 'error',
      'react-hooks/exhaustive-deps': 'error',
      'react-refresh/only-export-components': ['error', { allowConstantExport: true }],

      // Errores comunes y codigo muerto.
      'no-unused-vars': ['error', { argsIgnorePattern: '^_', varsIgnorePattern: '^_' }],
      'no-console': ['error', { allow: ['warn', 'error'] }],
      'no-debugger': 'error',
      'no-alert': 'error',
      'no-shadow': 'error',
      'no-duplicate-imports': 'error',
      'no-param-reassign': ['error', { props: false }],
      'no-implicit-coercion': 'error',
      'no-nested-ternary': 'error',
      'no-else-return': 'error',
      eqeqeq: ['error', 'always'],
      curly: ['error', 'all'],
      'no-var': 'error',
      'prefer-const': 'error',
      'prefer-template': 'error',
      'object-shorthand': 'error',

      // Mantenibilidad: funciones y archivos pequenos.
      complexity: ['error', 15],
      'max-depth': ['error', 3],
      'max-params': ['error', 4],
      'max-lines': ['error', { max: 300, skipBlankLines: true, skipComments: true }],

      // Buenas practicas de React/JSX.
      'react/prop-types': 'off',
      'react/jsx-key': 'error',
      'react/no-array-index-key': 'error',
      'react/self-closing-comp': 'error',
      'react/jsx-no-useless-fragment': 'error',
      'react/jsx-boolean-value': 'error',
      'react/jsx-curly-brace-presence': 'error',
      'react/jsx-pascal-case': 'error',
      'react/no-danger': 'error',
      'react/button-has-type': 'error',
      'react/hook-use-state': 'error',
      // Se permiten "render props" (rowActions, render) pero no componentes declarados dentro de otro.
      'react/no-unstable-nested-components': ['error', { allowAsProps: true }],
      'react/jsx-no-constructed-context-values': 'error',
      'react/function-component-definition': ['error', {
        namedComponents: 'function-declaration',
        unnamedComponents: 'arrow-function',
      }],
    },
  },
  {
    files: ['*.js', 'scripts/**/*.mjs'],
    languageOptions: { ecmaVersion: 'latest', sourceType: 'module', globals: globals.node },
    rules: { ...js.configs.recommended.rules },
  },
];

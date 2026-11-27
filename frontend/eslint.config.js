import globals from 'globals'

export default [
  {
    files: ['src/**/*.{js,jsx}'],
    languageOptions: {
      globals: globals.browser,
      parserOptions: {
        ecmaVersion: 'latest',
        sourceType: 'module',
        ecmaFeatures: { jsx: true },
      },
    },
    rules: {
      'no-constant-condition': 'error',
      'no-undef': 'error',
      'no-unreachable': 'error',
      'no-unused-vars': 'error',
    },
  },
]

import {
  forwardRef,
  type InputHTMLAttributes,
  type ReactNode,
} from 'react'

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label: string
  error?: string
  hint?: string
  icon?: ReactNode
}

export const Input = forwardRef<HTMLInputElement, InputProps>(
  ({ label, error, hint, icon, id, className = '', ...props }, ref) => {
    const inputId = id || props.name
    return (
      <label className="field" htmlFor={inputId}>
        <span className="field__label">{label}</span>
        <span className={`field__control ${error ? 'field__control--error' : ''}`}>
          {icon}
          <input
            ref={ref}
            id={inputId}
            className={className}
            aria-invalid={Boolean(error)}
            aria-describedby={error ? `${inputId}-error` : undefined}
            {...props}
          />
        </span>
        {error ? (
          <span className="field__error" id={`${inputId}-error`}>
            {error}
          </span>
        ) : hint ? (
          <span className="field__hint">{hint}</span>
        ) : null}
      </label>
    )
  },
)

Input.displayName = 'Input'

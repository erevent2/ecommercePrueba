import { render, screen } from '@testing-library/react'
import { afterEach, describe, expect, it, vi } from 'vitest'
import App from '../App'

describe('App', () => {
  afterEach(() => {
    window.history.pushState({}, '', '/')
    localStorage.clear()
  })

  it('renders the authentication route and shared navigation', async () => {
    window.history.pushState({}, '', '/auth')
    render(<App />)

    await vi.dynamicImportSettled()
    expect(await screen.findByText('Bienvenido de vuelta')).toBeInTheDocument()
    expect(screen.getByText('Blossom.')).toBeInTheDocument()
    expect(screen.getByText('Catálogo')).toBeInTheDocument()
  })
})

import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { afterEach, describe, expect, it, vi } from 'vitest'
import { api } from '../../api/axiosConfig'
import AuthPage from '../../pages/AuthPage'

describe('AuthPage', () => {
  afterEach(() => {
    localStorage.clear()
    vi.restoreAllMocks()
  })

  it('logs in, persists the token, and navigates to the catalogue', async () => {
    vi.spyOn(api, 'post').mockResolvedValue({ data: { token: 'jwt' } })
    render(<MemoryRouter><AuthPage /></MemoryRouter>)
    fireEvent.change(screen.getByPlaceholderText('tu@email.com'), { target: { value: 'user@example.com' } })
    fireEvent.change(screen.getByPlaceholderText('••••••••'), { target: { value: 'secret1' } })
    fireEvent.click(screen.getByRole('button', { name: 'Iniciar Sesión' }))

    await waitFor(() => expect(localStorage.getItem('blossom_token')).toBe('jwt'))
    expect(api.post).toHaveBeenCalledWith('/auth/login', {
      email: 'user@example.com', password: 'secret1',
    })
  })

  it('registers and switches back to login after success', async () => {
    vi.spyOn(api, 'post').mockResolvedValue({ data: {} })
    vi.spyOn(window, 'alert').mockImplementation(() => {})
    render(<MemoryRouter><AuthPage /></MemoryRouter>)
    fireEvent.click(screen.getByRole('button', { name: 'Regístrate aquí' }))
    fireEvent.change(screen.getByPlaceholderText('tu@email.com'), { target: { value: 'new@example.com' } })
    fireEvent.change(screen.getByPlaceholderText('••••••••'), { target: { value: 'secret1' } })
    fireEvent.click(screen.getByRole('button', { name: 'Registrarse' }))

    await waitFor(() => expect(api.post).toHaveBeenCalledWith('/auth/register', {
      email: 'new@example.com', password: 'secret1',
    }))
    expect(screen.getByText('Bienvenido de vuelta')).toBeInTheDocument()
    expect(window.alert).toHaveBeenCalled()
  })

  it('shows server errors and client validation messages', async () => {
    vi.spyOn(api, 'post').mockRejectedValue({ response: { data: { message: 'Invalid credentials' } } })
    render(<MemoryRouter><AuthPage /></MemoryRouter>)
    fireEvent.click(screen.getByRole('button', { name: 'Iniciar Sesión' }))
    expect(await screen.findByText('El correo es obligatorio')).toBeInTheDocument()
    expect(screen.getByText('La contraseña es obligatoria')).toBeInTheDocument()

    fireEvent.change(screen.getByPlaceholderText('tu@email.com'), { target: { value: 'user@example.com' } })
    fireEvent.change(screen.getByPlaceholderText('••••••••'), { target: { value: 'secret1' } })
    fireEvent.click(screen.getByRole('button', { name: 'Iniciar Sesión' }))
    expect(await screen.findByText('Invalid credentials')).toBeInTheDocument()
  })
})

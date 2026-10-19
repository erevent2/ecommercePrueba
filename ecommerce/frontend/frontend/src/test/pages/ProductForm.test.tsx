import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { afterEach, describe, expect, it, vi } from 'vitest'
import { api } from '../../api/axiosConfig'
import ProductForm from '../../pages/ProductForm'

describe('ProductForm', () => {
  afterEach(() => vi.restoreAllMocks())

  it('validates required fields and submits a physical product', async () => {
    const post = vi.spyOn(api, 'post').mockResolvedValue({ data: {} })
    vi.spyOn(window, 'alert').mockImplementation(() => {})
    render(<MemoryRouter><ProductForm /></MemoryRouter>)
    fireEvent.click(screen.getByRole('button', { name: 'Crear Producto' }))
    expect(await screen.findByText('El nombre es obligatorio')).toBeInTheDocument()

    fireEvent.change(screen.getByRole('textbox'), { target: { value: 'Desk' } })
    const selects = screen.getAllByRole('combobox')
    expect(Array.from(selects[0].querySelectorAll('option')).map((option) => option.value)).toEqual([
      '', 'Electrónica', 'Software', 'Libros', 'Otros',
    ])
    fireEvent.change(selects[0], { target: { value: 'Libros' } })
    fireEvent.change(screen.getByRole('spinbutton'), { target: { value: '39.99' } })
    fireEvent.change(selects[1], { target: { value: 'PHYSICAL' } })
    fireEvent.change(screen.getAllByRole('spinbutton')[1], { target: { value: '2' } })
    fireEvent.click(screen.getByRole('button', { name: 'Crear Producto' }))

    await waitFor(() => expect(post).toHaveBeenCalledWith('/products', expect.objectContaining({
      name: 'Desk', category: 'Libros', productType: 'PHYSICAL',
    })))
    expect(window.alert).toHaveBeenCalledWith('Producto creado exitosamente')
  })

  it('shows digital product fields and reports API errors', async () => {
    vi.spyOn(api, 'post').mockRejectedValue(new Error('forbidden'))
    vi.spyOn(window, 'alert').mockImplementation(() => {})
    render(<MemoryRouter><ProductForm /></MemoryRouter>)
    fireEvent.change(screen.getByRole('textbox'), { target: { value: 'E-book' } })
    const selects = screen.getAllByRole('combobox')
    fireEvent.change(selects[0], { target: { value: 'Software' } })
    fireEvent.change(screen.getByRole('spinbutton'), { target: { value: '12' } })
    fireEvent.change(selects[1], { target: { value: 'DIGITAL' } })

    expect(screen.getByText('Enlace de Descarga')).toBeInTheDocument()
    fireEvent.change(screen.getAllByRole('textbox')[1], { target: { value: 'https://example.test/file' } })

    fireEvent.click(screen.getByRole('button', { name: 'Crear Producto' }))
    await waitFor(() => expect(window.alert).toHaveBeenCalledWith(
      'Error al crear el producto. Revisa tu sesión o permisos.',
    ))
  })
})

import { fireEvent, render, screen } from '@testing-library/react'
import { beforeEach, describe, expect, it, vi } from 'vitest'
import { CartProvider } from '../../context/CartContext'
import ProductList from '../../pages/ProductList'
import { useProducts } from '../../hooks/useProducts'

vi.mock('../../hooks/useProducts', () => ({ useProducts: vi.fn() }))

const products = [{
  id: 1,
  name: 'Novel',
  category: 'Libros',
  price: 20,
  productType: 'DIGITAL' as const,
}]

describe('ProductList', () => {
  beforeEach(() => vi.mocked(useProducts).mockReset())

  it('shows loading, error and empty states', () => {
    vi.mocked(useProducts).mockReturnValue({ isLoading: true, isError: false } as ReturnType<typeof useProducts>)
    const { rerender } = render(<CartProvider><ProductList /></CartProvider>)
    expect(document.querySelector('.loader-spinner')).toBeInTheDocument()

    vi.mocked(useProducts).mockReturnValue({ isLoading: false, isError: true } as ReturnType<typeof useProducts>)
    rerender(<CartProvider><ProductList /></CartProvider>)
    expect(screen.getByText(/error al cargar el catálogo/i)).toBeInTheDocument()

    vi.mocked(useProducts).mockReturnValue({
      isLoading: false, isError: false, data: [],
    } as unknown as ReturnType<typeof useProducts>)
    rerender(<CartProvider><ProductList /></CartProvider>)
    expect(screen.getByText(/no encontramos productos/i)).toBeInTheDocument()
  })

  it('renders matching products and updates all filter fields', () => {
    vi.mocked(useProducts).mockReturnValue({
      isLoading: false, isError: false, data: products,
    } as ReturnType<typeof useProducts>)
    render(<CartProvider><ProductList /></CartProvider>)

    expect(screen.getByText('Novel')).toBeInTheDocument()
    fireEvent.change(screen.getByPlaceholderText('Ej. Teclado...'), { target: { value: 'Novel' } })
    fireEvent.change(screen.getByRole('combobox'), { target: { value: 'Libros' } })
    fireEvent.change(screen.getAllByPlaceholderText('$ 0')[0], { target: { value: '10' } })
    fireEvent.change(screen.getAllByPlaceholderText('$ 0')[1], { target: { value: '30' } })

    expect(useProducts).toHaveBeenLastCalledWith({
      name: 'Novel', category: 'Libros', minPrice: 10, maxPrice: 30,
    })
  })
})

import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { fireEvent, render, screen, waitFor } from '@testing-library/react'
import { afterEach, describe, expect, it, vi } from 'vitest'
import { api } from '../../api/axiosConfig'
import { CartProvider, useCart } from '../../context/CartContext'
import CartAndHistory from '../../pages/CartAndHistory'

function AddItem() {
  const { addToCart } = useCart()
  return <button onClick={() => addToCart({ productId: 7, name: 'Notebook', price: 15 })}>Add test item</button>
}

function renderPage(history: unknown[] = []) {
  localStorage.setItem('blossom_token', 'test-user-token')
  const client = new QueryClient({ defaultOptions: { queries: { retry: false } } })
  vi.spyOn(api, 'get').mockResolvedValue({ data: history })
  const rendered = render(
    <QueryClientProvider client={client}>
      <CartProvider>
        <AddItem />
        <CartAndHistory />
      </CartProvider>
    </QueryClientProvider>,
  )
  return { ...rendered, client }
}

describe('CartAndHistory', () => {
  afterEach(() => {
    vi.restoreAllMocks()
    localStorage.clear()
  })

  it('renders an empty cart and empty order history', async () => {
    renderPage()

    expect(screen.getByText('El carrito está vacío.')).toBeInTheDocument()
    expect(await screen.findByText('Aún no has realizado ninguna compra.')).toBeInTheDocument()
  })

  it('removes products and checks out the remaining cart', async () => {
    const post = vi.spyOn(api, 'post').mockResolvedValue({ data: {} })
    vi.spyOn(window, 'alert').mockImplementation(() => {})
    const { client } = renderPage()
    await screen.findByText('Aún no has realizado ninguna compra.')
    fireEvent.click(screen.getByText('Add test item'))
    expect(screen.getByText('Total:')).toBeInTheDocument()
    expect(screen.getAllByText('$15.00')).toHaveLength(2)
    fireEvent.click(screen.getByRole('button', { name: 'Eliminar' }))
    expect(screen.getByText('El carrito está vacío.')).toBeInTheDocument()

    fireEvent.click(screen.getByText('Add test item'))
    fireEvent.click(screen.getByRole('button', { name: 'Completar Compra' }))
    await waitFor(() => expect(post).toHaveBeenCalledWith('/orders', {
      items: [{ productId: 7, quantity: 1 }],
    }))
    expect(window.alert).toHaveBeenCalledWith('¡Orden creada exitosamente!')
    expect(screen.getByText('El carrito está vacío.')).toBeInTheDocument()
    client.clear()
  })

  it('renders order history and handles checkout failures', async () => {
    const post = vi.spyOn(api, 'post').mockRejectedValue(new Error('offline'))
    vi.spyOn(window, 'alert').mockImplementation(() => {})
    const history = [{
      id: 4,
      status: 'COMPLETED',
      items: [{ quantity: 2, productName: 'Book', unitPrice: 9.5 }],
    }]
    const { client } = renderPage(history)
    expect(await screen.findByText('Orden #4')).toBeInTheDocument()
    expect(screen.getByText('2x Book')).toBeInTheDocument()

    fireEvent.click(screen.getByText('Add test item'))
    fireEvent.click(screen.getByRole('button', { name: 'Completar Compra' }))
    await waitFor(() => expect(post).toHaveBeenCalled())
    expect(window.alert).toHaveBeenCalledWith('Error al procesar la orden.')
    client.clear()
  })

  it('refetches history when revisiting the account route', async () => {
    const get = vi.spyOn(api, 'get')
      .mockResolvedValueOnce({ data: [] })
      .mockResolvedValueOnce({ data: [{
        id: 9,
        status: 'COMPLETED',
        items: [],
      }] })
    const { client, unmount } = renderPage()
    await screen.findByText('Aún no has realizado ninguna compra.')
    expect(get).toHaveBeenCalledTimes(1)

    unmount()
    render(
      <QueryClientProvider client={client}>
        <CartProvider><CartAndHistory /></CartProvider>
      </QueryClientProvider>,
    )

    expect(await screen.findByText('Orden #9')).toBeInTheDocument()
    expect(get).toHaveBeenCalledTimes(2)
    client.clear()
  })

  it('uses a separate history cache for each authenticated token', async () => {
    const client = new QueryClient({ defaultOptions: { queries: { retry: false } } })
    client.setQueryData(['orderHistory', 'first-user-token'], [{
      id: 1, status: 'COMPLETED', items: [],
    }])
    localStorage.setItem('blossom_token', 'second-user-token')
    vi.spyOn(api, 'get').mockResolvedValue({ data: [{
      id: 2, status: 'COMPLETED', items: [],
    }] })

    render(
      <QueryClientProvider client={client}>
        <CartProvider><CartAndHistory /></CartProvider>
      </QueryClientProvider>,
    )

    expect(await screen.findByText('Orden #2')).toBeInTheDocument()
    expect(screen.queryByText('Orden #1')).not.toBeInTheDocument()
    expect(api.get).toHaveBeenCalledWith('/orders')
    client.clear()
  })

  it('does not request order history without an authentication token', () => {
    const get = vi.spyOn(api, 'get')
    render(
      <QueryClientProvider client={new QueryClient()}>
        <CartProvider><CartAndHistory /></CartProvider>
      </QueryClientProvider>,
    )

    expect(screen.getByText(/inicia sesión para consultar tu historial/i)).toBeInTheDocument()
    expect(get).not.toHaveBeenCalled()
  })
})

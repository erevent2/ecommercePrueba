import { fireEvent, render, screen } from '@testing-library/react'
import { describe, expect, it } from 'vitest'
import { CartProvider, useCart } from '../../context/CartContext'

function CartDetails() {
  const { cart, totalAmount, addToCart, removeFromCart, clearCart } = useCart()

  return (
    <div>
      <span data-testid="count">{cart.length}</span>
      <span data-testid="total">{totalAmount}</span>
      <button onClick={() => addToCart({ productId: 1, name: 'Book', price: 12 })}>add</button>
      <button onClick={() => removeFromCart(1)}>remove</button>
      <button onClick={clearCart}>clear</button>
    </div>
  )
}

describe('CartContext', () => {
  it('adds items, increments existing quantities and calculates totals', () => {
    render(<CartProvider><CartDetails /></CartProvider>)

    fireEvent.click(screen.getByText('add'))
    fireEvent.click(screen.getByText('add'))

    expect(screen.getByTestId('count')).toHaveTextContent('1')
    expect(screen.getByTestId('total')).toHaveTextContent('24')
  })

  it('removes items and clears the cart', () => {
    render(<CartProvider><CartDetails /></CartProvider>)
    fireEvent.click(screen.getByText('add'))
    fireEvent.click(screen.getByText('remove'))
    expect(screen.getByTestId('count')).toHaveTextContent('0')

    fireEvent.click(screen.getByText('add'))
    fireEvent.click(screen.getByText('clear'))
    expect(screen.getByTestId('total')).toHaveTextContent('0')
  })

  it('throws when used outside the provider', () => {
    function Consumer() {
      useCart()
      return null
    }

    expect(() => render(<Consumer />)).toThrow('useCart debe usarse dentro de un CartProvider')
  })
})
